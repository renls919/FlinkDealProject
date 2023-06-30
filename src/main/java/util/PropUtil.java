package util;

import java.io.*;
import java.util.*;

public class PropUtil implements Serializable {

    //配置文件的路径
    private String configPath=null;
    //配置文件对象
    private Properties props=null;

    /**
     * 默认构造函数，自动找到classpath下的config.properties。
     */
    public PropUtil(){
        InputStream in = PropUtil.class.getClassLoader().getResourceAsStream("env.properties");
        props = new Properties();
        try {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //关闭资源
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据key值读取配置的值
     */
    public Double readDouble(String key)  {
        return  Double.parseDouble(props.getProperty(key));
    }

    public int readInt(String key) {
        return  Integer.parseInt(props.getProperty(key));
    }

    public String readString(String key)  {
        return  props.getProperty(key);
    }

    public List<Integer> readIntList(String key)  {
        String[] strs=this.readString(key).split(",");
        List<Integer> result=new ArrayList<Integer>();
        for(String str:strs){
            result.add(Integer.parseInt(str));
        }
        return result;
    }


    /**
     * 读取properties的全部信息
     */
    public Map<String,String> readAllProperties() throws FileNotFoundException,IOException  {
        //保存所有的键值
        Map<String,String> map=new HashMap<String,String>();
        Enumeration en = props.propertyNames();
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            String Property = props.getProperty(key);
            map.put(key, Property);
        }
        return map;
    }

    /**
     * 设置某个key的值,并保存至文件。
     */
    public  void setValue(String key,String value) throws IOException {
        Properties prop = new Properties();
        InputStream fis = new FileInputStream(this.configPath);
        // 从输入流中读取属性列表（键和元素对）
        prop.load(fis);
        // 调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
        // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
        OutputStream fos = new FileOutputStream(this.configPath);
        prop.setProperty(key, value);
        // 以适合使用 load 方法加载到 Properties 表中的格式，
        // 将此 Properties 表中的属性列表（键和元素对）写入输出流
        prop.store(fos,"last update");
        //关闭文件
        fis.close();
        fos.close();
    }

}
