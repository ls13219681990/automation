package com.linln.modules.system.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data
@Getter
@Setter
@Entity
@Table(name="temperature_resistance_data")
@EntityListeners(AuditingEntityListener.class)
public class TemperatureResistanceData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long temperatureResistanceId;
    private String temp;//温度
    private String tempResist;//电阻







}
