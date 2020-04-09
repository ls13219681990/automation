package com.linln.modules.system.page;


import lombok.Data;

/**
 * 测点
 */


@Data
public class ReportMeasuringSpotDataPage {
    /*
    测点信息：测点名称、测点类型（量测项目）、测点编码、测点位置、安装部位，埋设时间、传感器编号、测量模式、测点初值、
    累计值红色报警阈值、累计值橙色报警阈值、累计值黄色报警阈值、变化速率红色报警阈值、变化速率橙色报警阈值、变化速率黄色报警阈值、
    节点网络ID号、节点地址、节点通道、备注。
（测点位置：隧道区间为里程标；车站基坑为分区等等，用户自行录入）

     */

    private Long id;
    private Long  measuringSpotId;
    private String measuringSpotSensorNo;//测点编号
    private String measuringSpotOriginalValue;//测点原始值
    private Double measuringSpotValue; //测点值
    private Double measuringSpotDifference;//测点差额
    private Double measuringSpotAccumulationValue;//测点累加值
    private String receiveTime;//接收时间
    private Double dayRateValue;
    private String measurePattern;
    private Byte status;
    private Double measuringSpotSensorLastTimeValue; //上次测值
/*    //变形量最大值    这两个放到前台来处理
    private Double deformationAmountMaxValue;
    //累计值最大值
    private Double cumulativeMaxValue;*/


    public ReportMeasuringSpotDataPage() {
    }

    public ReportMeasuringSpotDataPage(Long id, Long measuringSpotId, String measuringSpotSensorNo, String measuringSpotOriginalValue, Double measuringSpotValue, Double measuringSpotDifference, Double measuringSpotAccumulationValue, String receiveTime, Double dayRateValue, String measurePattern, Byte status) {
        this.id = id;
        this.measuringSpotId = measuringSpotId;
        this.measuringSpotSensorNo = measuringSpotSensorNo;
        this.measuringSpotOriginalValue = measuringSpotOriginalValue;
        this.measuringSpotValue = measuringSpotValue;
        this.measuringSpotDifference = measuringSpotDifference;
        this.measuringSpotAccumulationValue = measuringSpotAccumulationValue;
        this.receiveTime = receiveTime;
        this.dayRateValue = dayRateValue;
        this.measurePattern = measurePattern;
        this.status = status;
    }
}

