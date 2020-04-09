package com.linln.modules.system.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.linln.common.enums.StatusEnum;
import lombok.Data;

import javax.persistence.*;

/**
 * 测点
 */

@Entity
@Table(name = "measuring_spot_data")
@Data

public class MeasuringSpotData {
    /*
    测点信息：测点名称、测点类型（量测项目）、测点编码、测点位置、安装部位，埋设时间、传感器编号、测量模式、测点初值、
    累计值红色报警阈值、累计值橙色报警阈值、累计值黄色报警阈值、变化速率红色报警阈值、变化速率橙色报警阈值、变化速率黄色报警阈值、
    节点网络ID号、节点地址、节点通道、备注。
（测点位置：隧道区间为里程标；车站基坑为分区等等，用户自行录入）

     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long  measuringSpotId;
    private String measuringSpotSensorNo;//测点编号
    private String measuringSpotOriginalValue;//测点原始值
    private Double measuringSpotValue; //测点值
    private Double measuringSpotDifference;//测点差额
    private Double measuringSpotAccumulationValue;//测点累加值
    private String receiveTime;//接收时间
    private Double dayRateValue;//日速率
    private Byte status = StatusEnum.OK.getCode();
    private String whetherAlignment; //1 是没有做对齐的 0是做了的

    public MeasuringSpotData() {
    }

    public MeasuringSpotData(Long measuringSpotId, String measuringSpotSensorNo, String measuringSpotOriginalValue, Double measuringSpotValue, Double measuringSpotDifference, Double measuringSpotAccumulationValue, String receiveTime, Double dayRateValue, Byte status, String whetherAlignment) {
        this.measuringSpotId = measuringSpotId;
        this.measuringSpotSensorNo = measuringSpotSensorNo;
        this.measuringSpotOriginalValue = measuringSpotOriginalValue;
        this.measuringSpotValue = measuringSpotValue;
        this.measuringSpotDifference = measuringSpotDifference;
        this.measuringSpotAccumulationValue = measuringSpotAccumulationValue;
        this.receiveTime = receiveTime;
        this.dayRateValue = dayRateValue;
        this.status = status;
        this.whetherAlignment = whetherAlignment;
    }
}

