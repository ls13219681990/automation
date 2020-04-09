package com.linln.modules.system.page;


import lombok.Data;

/**
 * 地下水位
 */


@Data
public class ReportDataPage {
    /*
    观测值、观测值、开挖深度、差值、初测日期
     */

    private String measuringSpotSensorNo;//测点编号
    private String initialValue;//测点初值
    private Double measuringSpotValue;//测点值

    private String excavationDepth;//开挖深度
    private String layerNumber;//层数

    private Double lastVlaue;//上次测值
    private Double thisVlaue;//本次测值

    private Double incrementValue;//增量
    private Double deformationAmount;//变化量（变化量）（变形量）
    private Double differenceVlaue;//差值
    private Double cumulative; //累计值
    private String firstTestDate;//初测日期（）

    private Double cumulativeSettlement;  //累计沉降值（建筑物倾斜）
    private Float  distance;//距离（建筑物倾斜）
    private Float  tiltCumulative;//倾斜累计值（建筑物倾斜）
    private String tiltDirection;//倾斜方向（建筑物倾斜）
    public ReportDataPage() {
    }


}

