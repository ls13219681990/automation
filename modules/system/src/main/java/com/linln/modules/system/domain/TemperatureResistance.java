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
@Table(name="temperature_resistance")
@EntityListeners(AuditingEntityListener.class)
public class TemperatureResistance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String uploadTime;






}
