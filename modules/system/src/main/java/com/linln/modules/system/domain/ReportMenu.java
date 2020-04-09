package com.linln.modules.system.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Getter
@Setter
@Table(name="report_menu")
@EntityListeners(AuditingEntityListener.class)
public class ReportMenu implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long pid;
    private String pids;
    private String type;
    private String sort;




}
