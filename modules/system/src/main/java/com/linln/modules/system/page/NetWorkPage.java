package com.linln.modules.system.page;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data

public class NetWorkPage {
    private Long id;
    private String name;
    private String registerSSID;//注册标识
    private String installationSite;// 安装地点
    private int connectStatus; //1 为连接 0为 未连接



}
