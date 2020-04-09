package com.linln.common.utils;

import java.util.*;

public class CommonUtil {
    /**
     * 传入通道数量生成相应的通道数据
     * @param number
     * @return
     */
    public  static  String  passagewayData(Integer number){



    String value = "";


        for (Integer i= 1;i<=number;i++){


            value += i.toString()+",";

        }

        return    value.substring(0,value.length()-1);

    }


    public static void main(String[] args) {

        String s = passagewayData(32);
        System.out.println(s);
    }
}
