package com.linln.modules.system.domain;

import com.linln.common.enums.StatusEnum;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 传感器数据表
 */
@Data
@Entity
@Table(name="sensor_data")
/*@EntityListeners(AuditingEntityListener.class)*/
public class SensorData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sensorId;//传感器Id
    private String receiveTime;// 接收时间
    private String data;//传感器类型

}
