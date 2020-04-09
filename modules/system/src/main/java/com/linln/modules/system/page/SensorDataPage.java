package com.linln.modules.system.page;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 传感器数据表
 */
@Data
public class SensorDataPage implements Serializable {

    private Long id;
    private String sensorNo;//传感器编号
    private String receiveTime;// 接收时间
    private String data;//计算数据
    private Double primitiveData;//原始数据
}
