package com.linln.modules.system.domain;

import com.linln.common.enums.StatusEnum;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Getter
@Setter
@Entity
@Table(name="report_template")
@EntityListeners(AuditingEntityListener.class)
public class Template implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String name;
    private String uploadDate;
    private String remarks;
    private String address;//模板地址


}
