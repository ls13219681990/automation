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
@Table(name="acquisition_instrument")
@EntityListeners(AuditingEntityListener.class)
public class AcquisitionInstrument implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String no;//采集仪编号
    private String passagewayNumber;//通道数量
    private String netWorkId;//4GID
    private String installationSite;// 安装地点
    private String installationTime;//安装时间
    private String communicationProtocol;//通讯协议
    private Long workSpotId;
    private Integer intervalstate; //1 开  0关
    private Integer intervalTime;//操作间隔
    private Byte status = StatusEnum.OK.getCode();


}
