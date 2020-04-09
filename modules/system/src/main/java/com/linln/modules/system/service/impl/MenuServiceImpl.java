package com.linln.modules.system.service.impl;

import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.domain.Menu;
import com.linln.modules.system.repository.MenuRepository;
import com.linln.modules.system.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuRepository menuRepository;
    @PersistenceContext
    protected EntityManager em;

    @Override
    public Menu findById(Long id) {
        return menuRepository.getById(id);
    }

    @Override
    public void deleteBatch(List<Menu> menus) {

        for (Menu m : menus) {
            for (int i = 0; i < menus.size(); i++) {

                em.remove(em.merge(m));

                //em.remove();
                if (i % 30 == 0) {
                    em.flush();
                    em.clear();
                }
            }
        }

        em.flush();
    }

    @Override
    public void updateBatch(List<Menu> menus) {
        for (Menu m : menus) {
            for (int i = 0; i < menus.size(); i++) {
                em.merge(m);
                //em.remove(menus.get(i));
                if (i % 30 == 0) {
                    em.flush();
                    em.clear();
                }
            }
        }
        em.flush();

    }

    @Override
    public List<Menu> findByPidsLike(String pids) {
        return menuRepository.findByPidsLike(pids);
    }

    @Override
    public List<Menu> findByPidsLikeAndStatus(String pids, Byte status) {
        return menuRepository.findByPidsLikeAndStatus(pids, status);
    }

    @Override
    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    @Override
    public Menu findByTitle(String title) {
        return menuRepository.findByTitleAndEngineering(title,1);
    }


    /**
     * 根据菜单ID查询菜单数据
     *
     * @param id 菜单ID
     */
    @Override
    @Transactional
    public Menu getById(Long id) {
        if (id == 0L) {
            return new Menu(id, "顶级菜单", "");
        }
        return menuRepository.findById(id).orElse(null);
    }

    /**
     * 根据菜单url查询菜单数据
     *
     * @param url 菜单url
     */
    @Override
    @Transactional
    public Menu getByUrl(String url) {
        return menuRepository.findByUrl(url);
    }


    /**
     * 获取菜单列表数据
     *
     * @param example 查询实例
     * @param sort    排序对象
     */
    @Override
    public List<Menu> getListByExample(Example<Menu> example, Sort sort) {

        return menuRepository.findAll(example, sort);
    }

    /**
     * 根据菜单对象的Example判断是否存在
     *
     * @param menu 菜单对象
     */
    @Override
    public Menu getByMenuToExample(Menu menu) {
        return menuRepository.findOne(Example.of(menu)).orElse(null);
    }

    /**
     * 获取菜单列表数据
     */
    @Override
    public List<Menu> getListBySortOk() {
        Sort sort = new Sort(Sort.Direction.ASC, "type", "sort");
        return menuRepository.findAllByStatus(sort, StatusEnum.OK.getCode());
    }

    /**
     * 获取排序最大值
     *
     * @param pid 父菜单ID
     */
    @Override
    public Integer getSortMax(Long pid) {
        return menuRepository.findSortMax(pid);
    }

    /**
     * 根据父级菜单ID获取本级全部菜单
     *
     * @param pid   父菜单ID
     * @param notId 需要排除的菜单ID
     */
    @Override
    public List<Menu> getListByPid(Long pid, Long notId) {
        Sort sort = new Sort(Sort.Direction.ASC, "sort");
        return menuRepository.findByPidAndIdNot(sort, pid, notId);
    }

    /**
     * 保存菜单
     *
     * @param menu 菜单实体类
     */
    @Override
    public Menu save(Menu menu) {
        return menuRepository.save(menu);
    }

    /**
     * 保存多个菜单
     *
     * @param menuList 菜单实体类列表
     */
    @Override
    public List<Menu> save(List<Menu> menuList) {
        return menuRepository.saveAll(menuList);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> ids) {
        // 获取与之关联的所有菜单
        Set<Menu> treeMenus = new HashSet<>();
        List<Menu> menus = menuRepository.findByIdIn(ids);
        menus.forEach(menu -> {
            treeMenus.add(menu);
            treeMenus.addAll(menuRepository.findByPidsLikeAndStatus("%[" + menu.getId() + "]%", menu.getStatus()));
        });

        treeMenus.forEach(menu -> {
            // 删除菜单状态时，同时更新角色的权限
            if (statusEnum == StatusEnum.DELETE) {
                /*menu.getRoles().forEach(role -> {
                    role.getMenus().remove(menu);
                });*/
                // 非规范的Jpa操作，直接采用SQL语句
                menuRepository.cancelRoleJoin(menu.getId());
            }
            // 更新关联的所有菜单状态
            menu.setStatus(statusEnum.getCode());
        });

        return treeMenus.size() > 0;
    }

    @Override
    public Menu findByTitleAndEngineering(String title, Integer enginnering) {
        return menuRepository.findByTitleAndEngineering(title, enginnering);
    }

    @Override
    public Menu findFirstByPidAndByEngineeringOrderBySortDesc(Long pid, Integer engineering) {
        return menuRepository.findFirstByPidAndByEngineeringOrderBySortDesc(pid,engineering);
    }

    @Override
    public Menu findByRoleMenuInfo(Long id) {
        return menuRepository.findByRoleMenuInfo(id);
    }

    @Override
    public Integer cancelRoleJoin(Long id) {
        return menuRepository.cancelRoleJoin(id);
    }

    @Override
    public void addRoleMeun(Long roleId, Long menuId) {
        menuRepository.addRoleMeun(roleId, menuId);
    }

    @Override
    public List<Menu> findAllByTitleContaining(String title) {
        return menuRepository.findAllByTitleContaining(title);
    }

    @Override
    public List<Menu> findEngineeringMenu(Long id) {
        return menuRepository.findEngineeringMenu(id);
    }

    @Override
    public List<Menu> findEngineeringMenu() {
        return menuRepository.findEngineeringMenu();
    }


}
