package com.linln.modules.system.service;

import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.domain.RoleWorkSpot;
import com.linln.modules.system.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface RoleWorkSpotService {

    /**
     * 保存用户工点
     * @param roleWorkSpot 角色工点
     */
    void save(RoleWorkSpot roleWorkSpot);

    /**
     * 保存用户列表工点
     * @param roleWorkSpots 用户工点实体类
     */
    void save(List<RoleWorkSpot> roleWorkSpots);


    /**
     * 根据工点ID查询 信息
     */
    RoleWorkSpot findByWorkSpotId(Long workSpotId);

    /**
     * 根据userId   查询信息
     * @param userId
     * @return
     */
    List<RoleWorkSpot> findByRoleId(Long userId);


    @Transactional
    void deleteAll ();

    @Transactional
    void removeByRoleId(Long roleId);


}
