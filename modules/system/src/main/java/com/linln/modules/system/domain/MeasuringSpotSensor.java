package com.linln.modules.system.domain;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 测点传感器中间表
 */
@Data
@Entity
@Getter
@Setter
@Table(name = "measuring_spot_sensor")
public class MeasuringSpotSensor {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long measuringSpotSensorId;

    Long acquisitionSensorId;

    Long measuringSpotId;//测点ID


    Integer acquisitionNumber;//序号


    String dataGroup;

}

