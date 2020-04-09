package com.linln.modules.system.service.impl;

import com.linln.common.data.PageSort;
import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.domain.Dept;
import com.linln.modules.system.domain.Role;
import com.linln.modules.system.domain.RoleWorkSpot;
import com.linln.modules.system.repository.RoleWorkSpotRepository;
import com.linln.modules.system.repository.RoleRepository;
import com.linln.modules.system.repository.RoleWorkSpotRepository;
import com.linln.modules.system.service.DeptService;
import com.linln.modules.system.service.RoleService;
import com.linln.modules.system.service.RoleWorkSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Service
public class RoleWorkSpotServiceImpl implements RoleWorkSpotService {

    @Autowired
    private RoleWorkSpotRepository userWorkSpotRepository;


    @Override
    public void save(RoleWorkSpot userWorkSpot) {
        userWorkSpotRepository.save(userWorkSpot);
    }

    @Override
    public void save(List<RoleWorkSpot> userList) {
        userWorkSpotRepository.saveAll(userList);
    }

    @Override
    public RoleWorkSpot findByWorkSpotId(Long workSpotId) {
        return userWorkSpotRepository.findByWorkSpotId(workSpotId);
    }

    @Override
    public List<RoleWorkSpot> findByRoleId(Long userId) {
        return userWorkSpotRepository.findByRoleId(userId);
    }

    @Override
    public void deleteAll() {
        userWorkSpotRepository.deleteAll();
    }

    @Override
    public void removeByRoleId(Long roleId) {
        userWorkSpotRepository.removeByRoleId(roleId);
    }
}
