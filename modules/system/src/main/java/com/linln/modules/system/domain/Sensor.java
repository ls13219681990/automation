package com.linln.modules.system.domain;

import com.linln.common.enums.StatusEnum;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name="sensor")
@EntityListeners(AuditingEntityListener.class)
public class Sensor implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;//传感器名称
    private String no;// 传感器编号
    private String type;//传感器类型
    private String model;  //  传感器型号
    private String manufactor; //传感器厂家
    private String physicsUnit; //  物理量单位
    private String decimalPoint;//小数点位
    private String temperatureResistanceName;//温阻表名称
    private String zeroPointValue;  // 传感器零点值
    private String isAuxiliarySensor;//是否是辅助传感器 1：是  0：不是



    private Byte status = StatusEnum.OK.getCode();
}
