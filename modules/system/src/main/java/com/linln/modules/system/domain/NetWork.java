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

@Data
@Getter
@Setter
@Entity
@Table(name="net_work")
@EntityListeners(AuditingEntityListener.class)
public class NetWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;//2020/3/17 添加模块名称
    private String registerSSID;//注册标识
    private String installationSite;// 安装地点



}
