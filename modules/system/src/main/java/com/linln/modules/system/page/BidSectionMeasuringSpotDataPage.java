package com.linln.modules.system.page;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * 传感器数据表
 */
@Data
public class BidSectionMeasuringSpotDataPage implements Serializable {

    private BigInteger id;
    //标段1	工点1	测点1	XXXXXX传感器	16409	16409	1	4F440000	2019-07-18 11:13:02
    private String workSpotName;//工点名称
    private String measuringSpotName;//测点名称
    private String measuringSpotType;//测点类型
    private String measuringSpotSensorNo;//测点编号
    private String measuringSpotOriginalValue;//测点原始值
    private Double measuringSpotValue; //测点值
    private Double measuringSpotDifference;//测点差额
    private Double measuringSpotAccumulationValue;//测点累加值
    private String receiveTime;//接收时间
    private Double dayRateValue;
    private String initial_value;

    public BidSectionMeasuringSpotDataPage() {
    }

    public BidSectionMeasuringSpotDataPage(BigInteger id, String workSpotName, String measuringSpotName, String measuringSpotType, String measuringSpotSensorNo, String measuringSpotOriginalValue, Double measuringSpotValue, Double measuringSpotDifference, Double measuringSpotAccumulationValue, String receiveTime, Double dayRateValue, String initial_value) {
        this.id = id;
        this.workSpotName = workSpotName;
        this.measuringSpotName = measuringSpotName;
        this.measuringSpotType = measuringSpotType;
        this.measuringSpotSensorNo = measuringSpotSensorNo;
        this.measuringSpotOriginalValue = measuringSpotOriginalValue;
        this.measuringSpotValue = measuringSpotValue;
        this.measuringSpotDifference = measuringSpotDifference;
        this.measuringSpotAccumulationValue = measuringSpotAccumulationValue;
        this.receiveTime = receiveTime;
        this.dayRateValue = dayRateValue;
        this.initial_value = initial_value;
    }
}
