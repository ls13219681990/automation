package com.linln.modules.system.domain;

import com.linln.common.enums.StatusEnum;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * 采集仪和传感器的中间表
 */
@Data
@Getter
@Setter
@Entity
@Table(name="acquisition_sensor")
@EntityListeners(AuditingEntityListener.class)
public class AcquisitionSensor implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long acquisitionId;//采集仪编号
    private Long sensorId;//传感器编号
    private String passagewayId;//通道号
}
