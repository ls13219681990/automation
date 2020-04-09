package com.linln.modules.system.service;

import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.domain.Menu;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface MenuService {


    /**
     * 根据ID查询菜单
     * @param id
     * @return
     */
    Menu findById(Long id);


    /**
     * 批量删除
     * @param menus 批量数据ID
     */
    @Transactional
    void deleteBatch(List<Menu> menus);

    /**
     * 批量删除
     * @param menus 批量数据ID
     */
    @Transactional
    void updateBatch(List<Menu> menus);

    /**
     * 查询出该菜单下面的所有菜单(包含自身)
     * @param pids
     * @return
     */
    List<Menu> findByPidsLike(String pids);



    /**
     * 根据父ID查找子菜单
     * @param pids pid列表
     */
    public List<Menu> findByPidsLikeAndStatus(String pids, Byte status);



    /**
     * 查询所有菜单
     */
    List<Menu> findAll();




    Menu findByTitle(String title);

    public List<Menu> findEngineeringMenu(Long id);



    /**
     * 查询工程树
     * @return
     */
    public List<Menu> findEngineeringMenu();

    /**
     * 获取菜单列表数据
     * @param example 查询实例
     * @param sort 排序对象
     */
    List<Menu> getListByExample(Example<Menu> example, Sort sort);

    /**
     * 根据菜单对象的Example判断是否存在
     * @param menu 菜单对象
     */
    Menu getByMenuToExample(Menu menu);

    /**
     * 根据菜单ID查询菜单数据
     * @param id 菜单ID
     */
    Menu getById(Long id);

    /**
     * 根据菜单url查询菜单数据
     * @param url 菜单url
     */
    Menu getByUrl(String url);

    /**
     * 获取菜单列表数据
     */
    List<Menu> getListBySortOk();

    /**
     * 获取排序最大值
     * @param pid 父菜单ID
     */
    Integer getSortMax(Long pid);

    /**
     * 根据父级菜单ID获取本级全部菜单
     * @param pid 父菜单ID
     * @param notId 需要排除的菜单ID
     */
    List<Menu> getListByPid(Long pid, Long notId);

    /**
     * 保存菜单
     * @param menu 菜单实体类
     */
    Menu save(Menu menu);

    /**
     * 保存多个菜单
     * @param menuList 菜单实体类列表
     */
    List<Menu> save(List<Menu> menuList);

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);



    /**
     * 通过名称和工程标识 查看工程(不是管理的工程)
     * @param title
     * @param enginnering
     * @return
     */
    Menu findByTitleAndEngineering(String title,Integer enginnering);



    /**
     * 查询父节点排序后的数据
     * @param pid
     * @return
     */
    Menu findFirstByPidAndByEngineeringOrderBySortDesc(Long pid,Integer engineering );




    /**
     * 查询菜单绑定没有
     * @param id
     * @return
     */

    Menu findByRoleMenuInfo(Long id );




    /**
     * 取消菜单与角色之间的关系
     * @param id 菜单ID
     */
    public Integer cancelRoleJoin(Long id);


    /**
     * 添加角色菜单
     * @param roleId
     * @param menuId
     */
    @Transactional
    void addRoleMeun(Long roleId , Long menuId);


    /**
     * 模糊匹配
     * @param title
     * @return
     */
    List<Menu> findAllByTitleContaining(String title);

}
