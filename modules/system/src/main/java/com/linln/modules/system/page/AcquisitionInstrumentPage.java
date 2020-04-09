package com.linln.modules.system.page;

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

public class AcquisitionInstrumentPage implements Serializable {

    private Long id;
    private String name;
    private String no;//采集仪编号
    private String passagewayNumber;//通道数量
    private String netWorkId;//4GID
    private String installationSite;// 安装地点
    private String installationTime;//安装时间
    private String communicationProtocol;//通讯协议
    private Long workSpotId;
    private String openingOrClos; //1开启 0关闭



}
