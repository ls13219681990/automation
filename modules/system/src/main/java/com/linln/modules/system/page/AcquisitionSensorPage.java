package com.linln.modules.system.page;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


/**
 * 采集仪和传感器的中间表
 */
@Data

public class AcquisitionSensorPage implements Serializable {

    private BigInteger id;
    private BigInteger acquisitionId;//采集仪编号
    private String passagewayId;//通道号
    private BigInteger sensorId;//传感器编号

    public AcquisitionSensorPage() {
    }

    public AcquisitionSensorPage(BigInteger id, BigInteger acquisitionId, String passagewayId, BigInteger sensorId) {
        this.id = id;
        this.acquisitionId = acquisitionId;
        this.passagewayId = passagewayId;
        this.sensorId = sensorId;
    }
}
