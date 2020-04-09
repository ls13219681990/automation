package com.linln.modules.system.domain;


import lombok.Data;

import javax.persistence.*;

/**
 * 测点
 */

@Entity
@Table(name = "measuring_spot")
@Data
public class MeasuringSpot {
    /*
    测点信息：测点名称、测点类型（量测项目）、测点编码、测点位置、安装部位，埋设时间、传感器编号、测量模式、测点初值、
    累计值红色报警阈值、累计值橙色报警阈值、累计值黄色报警阈值、变化速率红色报警阈值、变化速率橙色报警阈值、变化速率黄色报警阈值、
    节点网络ID号、节点地址、节点通道、备注。
（测点位置：隧道区间为里程标；车站基坑为分区等等，用户自行录入）

     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String measuringSpotType;//测点类型
    private String no;
    private String measuringSpotPosition;//测点位置
    private String installationLocation;//安装部位
    private String embedmentTime;//埋设时间
    private String sensorNo;//传感器编号
    private String auxiliarySensor; //辅助传感器
    private String measurePattern;//测量模式
    private String initialValue;//测点初值
    private Integer cumulativeGulesThreshold;//累计值红色报警阈值
    private Integer cumulativeOrangeThreshold;//累计值橙色报警阈值
    private Integer cumulativeYellowThreshold; //累计值黄色报警阈值
    private Integer changeGulesThreshold;//变化速率红色报警阈值
    private Integer changeOrangeThreshold;//变化速率橙色报警阈值
    private Integer changeYellowThreshold; //变化速率黄色报警阈值
    private Long workSpotId;
    private Integer measuringSpotSymbol;//测值符号:1为正/如果是是-1 就是 测点值X -1
    private Double calculatedValue;
    private String direction;//左右线
    private String remarks;
    private Integer stuts;//   3/26 新增测点状态 用于报告的备注
    

    public MeasuringSpot() {
    }

    public MeasuringSpot(String name, String measuringSpotType, String no, String measuringSpotPosition, String installationLocation, String embedmentTime, String sensorNo, String auxiliarySensor, String measurePattern, String initialValue, Integer cumulativeGulesThreshold, Integer cumulativeOrangeThreshold, Integer cumulativeYellowThreshold, Integer changeGulesThreshold, Integer changeOrangeThreshold, Integer changeYellowThreshold, Long workSpotId, Integer measuringSpotSymbol, Double calculatedValue, String direction, String remarks, Integer stuts) {
        this.name = name;
        this.measuringSpotType = measuringSpotType;
        this.no = no;
        this.measuringSpotPosition = measuringSpotPosition;
        this.installationLocation = installationLocation;
        this.embedmentTime = embedmentTime;
        this.sensorNo = sensorNo;
        this.auxiliarySensor = auxiliarySensor;
        this.measurePattern = measurePattern;
        this.initialValue = initialValue;
        this.cumulativeGulesThreshold = cumulativeGulesThreshold;
        this.cumulativeOrangeThreshold = cumulativeOrangeThreshold;
        this.cumulativeYellowThreshold = cumulativeYellowThreshold;
        this.changeGulesThreshold = changeGulesThreshold;
        this.changeOrangeThreshold = changeOrangeThreshold;
        this.changeYellowThreshold = changeYellowThreshold;
        this.workSpotId = workSpotId;
        this.measuringSpotSymbol = measuringSpotSymbol;
        this.calculatedValue = calculatedValue;
        this.direction = direction;
        this.remarks = remarks;
        this.stuts = stuts;
    }
}

