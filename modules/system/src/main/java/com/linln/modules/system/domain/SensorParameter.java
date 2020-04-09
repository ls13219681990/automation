package com.linln.modules.system.domain;

import com.linln.common.enums.StatusEnum;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name="sensor_parameter")
@EntityListeners(AuditingEntityListener.class)
public class SensorParameter implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sensourId;//
    private Double k;//
    private Double f0;//
    private Double kt;//
    private Double t0;//
    private Double b;//计算系数
    private Double c;//分组系数
    //后期要加一个分组的 系数

}
