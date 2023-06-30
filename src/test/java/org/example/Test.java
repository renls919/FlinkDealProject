package org.example;

import util.PropUtil;

public class Test {

    public static void main(String[] args) {
        PropUtil propUtil=new PropUtil();
        double lower_warn= (propUtil.readInt("sensor_temp_lower")*0.9);
        double upper_warn= (propUtil.readInt("sensor_temp_upper")*1.1);

        if(30>upper_warn){
            System.out.println(upper_warn);
        }


    }
}
