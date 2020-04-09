package com.linln.modules.system.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * 工点
 */


@Data
@Getter
@Setter
@Entity
@Table(name="role_work_spot")
@EntityListeners(AuditingEntityListener.class)
public class RoleWorkSpot {


    /**
     * 用户对应工点:用于用作资源管理
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id ;
    private Long roleId;
    private Long workSpotId;

}
