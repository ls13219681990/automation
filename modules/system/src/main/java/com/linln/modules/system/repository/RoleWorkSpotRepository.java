package com.linln.modules.system.repository;

import com.linln.modules.system.domain.AcquisitionSensor;
import com.linln.modules.system.domain.Dept;
import com.linln.modules.system.domain.RoleWorkSpot;
import com.linln.modules.system.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface RoleWorkSpotRepository extends JpaRepository<RoleWorkSpot, Long> {


    RoleWorkSpot findByWorkSpotId(Long workSpotId);


    List<RoleWorkSpot> findByRoleId(Long userId);


    void removeByRoleId(Long roleId);
}
