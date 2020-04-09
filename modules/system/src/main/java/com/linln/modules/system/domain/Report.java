package com.linln.modules.system.domain;

import com.linln.common.enums.StatusEnum;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name="report")
@EntityListeners(AuditingEntityListener.class)
public class Report implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String inputId;//录入人
    private String inputTime;//录入时间
    private String address;//报告地址
    private Long workSpotId;//工点ID

    public Report() {

    }

    public Report(String name, String inputId, String inputTime, String address, Long workSpotId) {
        this.name = name;
        this.inputId = inputId;
        this.inputTime = inputTime;
        this.address = address;
        this.workSpotId = workSpotId;
    }
}
