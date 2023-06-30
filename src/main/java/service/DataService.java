package service;

import controller.StopController;
import dao.KafkaDao;
import util.PropUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Random;

public class DataService {
    private PropUtil propUtil=new PropUtil();

    /**
     * 此方法用于生成大棚传感器数据,并将数据发送到指定的kafka主题中
     */
    public void generate(int start_period,
                         String dev_id,
                         int time_duration,
                         int abnormal_weight,
                         int abnormal_humweight,
                         int abnormal_CO2weight,
                         int abnormal_O2weight,
                         int abnormal_PHweight,
                         int abnormal_WPweight,
                         int interval) {
        //如果传入的订单id为空,则自动生成订单编号
        if(dev_id==null||dev_id.equals("")){
            dev_id=generate_devid();
        }

        // 计算总的采集次数
        int totalcounts = time_duration * 60 / interval;

        // 生成起始时间
        String start_time = generate_starttime(start_period);
        // 初始化采集时间为起始时间
        String collect_time = start_time;

        // 初始化传感器温度为20°
        double sensor_conTemp = 20.0;
        //初始化传感器湿度为600ppm
        double sensor_conHumi=600;
        //初始化传感器CO2浓度为500ppm
        double sensor_conCO2=500;
        //初始化传感器O2浓度为21%
        double sensor_conO2=21;
        //初始化传感器PH值为5.5
        double sensor_conPH=5.5;
        //初始化传感器风力为2m/s
        double sensor_conWP=2;

        // 初始化KafkaDao对象
        KafkaDao kafkaDao = new KafkaDao();

        for(int i=1;i<=totalcounts+1;i++){
            if(StopController.stopFlag_1==1){
                break;
            }else{
                try {

                    String sensor_data="{\"dev_id\":\""+dev_id+"\"," +
                            "\"data\":{\"start_time\":\""+start_time+
                            "\",\"collect_time\":\""+collect_time+
                            "\",\"conTemp\":\""+sensor_conTemp+
                            "\",\"conHumi\":\"\""+sensor_conHumi+
                            "\",\"conCO2\":\""+sensor_conCO2+
                            "\",\"conO2\":\""+sensor_conO2+
                            "\",\"conPH\":\""+sensor_conPH+
                            "\",\"conWP\":\""+sensor_conWP+"\"}}";
                    //将这些数据发送到对应的Kafka主题中
                    kafkaDao.toKafkaTopic(sensor_data,propUtil.readString("kafka_topic"));

                    System.out.println(sensor_data);
                    Thread.sleep(interval*1000);

                    collect_time=generate_collecttime(collect_time,interval);
                    sensor_conTemp=formatD3(generate_guntem(sensor_conTemp,abnormal_weight));
                    sensor_conHumi=formatD3(generateHumidityData(abnormal_humweight));
                    sensor_conCO2=formatD3(generateDioxideConcentration(abnormal_CO2weight));
                    sensor_conO2=formatD3(generateOxygenConcentration(abnormal_O2weight));
                    sensor_conPH=formatD3(generatePHData(abnormal_PHweight));
                    sensor_conWP=formatD3(generateWindPower(abnormal_WPweight));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
        StopController.stopFlag_1=0;

    }


    /**
     * 此方法用于生成传感器设备id
     * @return 生成的传感器设备id
     */
    public String generate_devid(){
        //指定日期解析格式
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        String time=sdf.format(System.currentTimeMillis());
        String dev_id="card"+time;
        return dev_id;
    }

    /**
     * 此方法用于处理起始传感器的具体时间信息
     * @param start_period 起始传感器时段
     * @return 起始传感器的具体时间信息
     */
    public String generate_starttime(int start_period){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd "+start_period+":mm:ss");
        String start_time=sdf.format(System.currentTimeMillis());

        return start_time;
    }

    /**
     * 此方法用于处理下一间隔的时间信息
     * @param curtime 当前时间
     * @param interval 数据产生的周期
     * @return 下一间隔的时间信息
     */
    public String generate_collecttime(String curtime,int interval) throws Exception {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=sdf.parse(curtime);
        String nexttime=sdf.format(date.getTime()+interval*1000);
        return  nexttime;
    }

    /**
     * 此方法用于控制double类型数据的精度为3位
     */
    public Double formatD3(Double value){
        return Double.parseDouble(String.format("%.3f", value));
    }


    /**
     * 此方法用于模拟产生随机的传感器温度数据
     */
    public double generate_guntem(double curtemp, int abnormal_weight){
        if(abnormal_weight==0){

            if(curtemp<=propUtil.readInt("sensor_temp_lower")){
                curtemp=curtemp+(new Random().nextInt(2)+1);
                return curtemp;
            }
            else if(curtemp>=propUtil.readInt("sensor_temp_upper")){
                curtemp=curtemp+(new Random().nextInt(2)-3);
                return curtemp;
            }else{
                curtemp=curtemp+(new Random().nextInt(3)-1);
                return curtemp;
            }
        }
        //如果参数abnormal_weight==1,则表示可能会产生异常传感温度数据
        else{
            //获取低于下限温度10%的温度
            double lower_warn=  (propUtil.readInt("sensor_temp_lower")*0.9);
            //获取高于上限温度10%的温度
            double upper_warn= (propUtil.readInt("sensor_temp_upper")*1.1);
            //如果温度在正常的18°~25°之间,则有1/20的概率产出下限告警值。1/20的概率产出上限告警值
            if(curtemp>=propUtil.readInt("sensor_temp_lower")&&curtemp<=propUtil.readInt("sensor_temp_upper")){
                int random=new Random().nextInt(20);
                //1/20的概率产出下限告警值
                if(random==6){
                    curtemp=lower_warn+new Random().nextInt(2)-1;
                    return curtemp;
                //1/20的概率产出上限告警值
                }else if(random==7){
                    curtemp=upper_warn+new Random().nextInt(3)-1;
                    return curtemp;
                }else{
                    //18/20的概率正常温度范围内波动
                    return curtemp+new Random().nextInt(3)-1;
                }
            }else{
                //当小于下限温度,向上回弹
                if(curtemp<=propUtil.readInt("sensor_temp_lower")){
                    curtemp=curtemp+new Random().nextInt(5);
                    return curtemp;
                }
                //当大于上限温度,向下回弹
                else{
                    curtemp=curtemp+new Random().nextInt(2)-3;
                    return curtemp;
                }
            }
        }

    }
    public double generateHumidityData(int abnormal_humweight) {
        // 模拟产生随机的传感器湿度数据
        Random random = new Random();
        double value = random.nextInt(20);
        //湿度正常范围：500ppm-1000ppm
        // 生成异常湿度数据
        if (value == 0 && abnormal_humweight == 1) {
            // 1/20的概率生成低于正常范围下限的数值
            return random.nextDouble() * 500;
        } else if (value == 1 && abnormal_humweight == 1) {
            // 1/20的概率生成高于正常范围上限的数值
            return random.nextDouble() * (2000 - 1000) + 1000;
        } else {
            // 18/20的概率生成正常范围内的湿度值
            return random.nextDouble() * (1000 - 500) + 500;
        }
    }
    public double generateDioxideConcentration(int abnormal_dioweight){
        double co2Data;

        // Generate random number from 0 to 29
        Random random = new Random();
        int randomValue = random.nextInt(30);

        // Check if  abnormal_dioweight is 1, i.e., there is a possibility of abnormal CO2 data
        if (abnormal_dioweight == 1) {
            // 1/30 probability of generating lower than normal CO2 value
            if (randomValue == 0) {
                co2Data = random.nextDouble() * (350.0 - 0.0) + 0.0;
            }
            // 1/30 probability of generating upper alarm limit CO2 value
            else if (randomValue == 1) {
                co2Data = random.nextDouble() * (Double.MAX_VALUE - 1000.0) + 1000.0;
            }
            // 28/30 probability of generating CO2 value within the normal range (350-1000)
            else {
                co2Data = random.nextDouble() * (1000.0 - 350.0) + 350.0;
            }
        }
        // If abnormal_dioweight is not 1, generate CO2 value within the normal range (350-1000)
        else {
            co2Data = random.nextDouble() * (1000.0 - 350.0) + 350.0;
        }

        return co2Data;
    }
    public double generateOxygenConcentration(int abnormal_O2weight){
        Random random=new Random();
        int randomPercent = random.nextInt(20);
        double o2Data;

        if (abnormal_O2weight == 1) {
            // abnormal data
            if (randomPercent == 0) {
                // 1/20 chance to generate abnormal data between 5% and 15%
                o2Data = 5 + 10 * random.nextDouble();
            } else if (randomPercent == 1) {
                // 1/20 chance to generate abnormal data between 23% and 30%
                o2Data = 23 + 7 * random.nextDouble();
            } else {
                // 18/20 chance to generate normal data between 15% and 23%
                o2Data = 15 + 8 * random.nextDouble();
            }
        } else {
            // normal data, between 15% and 23%
            o2Data = 15 + 8 * random.nextDouble();
        }
        return o2Data;
    }
    public double generatePHData(int abnormal_PHweight){
        Random random = new Random();
        double randomNumber = random.nextDouble();
        if (abnormal_PHweight== 1) {
            if (randomNumber < 1.0 / 30) {
                //1/30的概率 产生的异常数据位于0-5.5之间
                return random.nextDouble() * 5.5;
            } else if (randomNumber < 2.0 / 30) {
                //1/30的概率 产生的异常数据位于8.5-14之间
                return random.nextDouble() * (14 - 8.5) + 8.5;
            } else {
                // 产生的正常数据位于5.5-8.5之间
                return random.nextDouble() * (8.5 - 5.5) + 5.5;
            }
        } else {
            // 产生的正常数据位于5.5-8.5之间
            return random.nextDouble() * (8.5 - 5.5) + 5.5;
        }
    }

    public  double generateWindPower(int abnormal_WPweight) {
        Random rand = new Random();
        double windPower;

        if (abnormal_WPweight == 1) {
            int randomNum = rand.nextInt(30); // 生成0-29之间的随机数

            if (randomNum == 0) {
                // 1/30的概率产生的数据位于3-8之间
                windPower = rand.nextDouble() * (8 - 3) + 3; // 生成3-8之间的随机数
            } else {
                // 29/30的概率产生的数据位于0-3之间
                windPower = rand.nextDouble() * 3; // 生成0-3之间的随机数
            }
        } else {
            windPower = rand.nextDouble() * 3; // 生成0-3之间的随机数
        }
        return windPower;
    }



}
