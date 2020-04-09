package com.linln.modules.system.repository;

import com.linln.common.constant.StatusConst;
import com.linln.modules.system.domain.Menu;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface MenuRepository extends BaseRepository<Menu, Long> {


    /**
     * 查询出该菜单下面的所有菜单(包含自身)
     * @param pids
     * @return
     */
    @Query(value = "select m from Menu m where m.pids like %?1%")
    List<Menu> findByPidsLike( String pids);


    /**
     * @function 自定义JPQL
     * @param ids
     */
    // 注意更新和删除是需要加事务的， 并且要加上 @Modify的注解
    @Modifying
    @Transactional
    @Query("delete from Menu m where m.id in (?1)")
    void deleteBatch(List<Long> ids);




    /**
     * 查询所有菜单
     */
    @Query("select m from Menu m where type<>3  and engineering<>1 order by sort asc ")
    public List<Menu> findAll();

    /**
     * 查询用户对应的工程树
     */
    @Query(value = "select sm.* from sys_user su  \n" +
            "left join sys_user_role sur on sur.user_id = su.id\n" +
            "left join sys_role sr on sur.role_id = sr.id\n" +
            "left join sys_role_menu srm on srm.role_id = sr.id\n" +
            "left join sys_menu sm on srm.menu_id = sm.id\n" +
            "where sur.user_id = ?1 and sm.type<>3 and sm.engineering =1 order by sm.sort asc",nativeQuery = true)
    public List<Menu> findEngineeringMenu(Long id);

    /**
     * 查询所有菜单
     */
    @Query("select m from Menu m where type<>3 and engineering =1 order by sort asc  ")
    public List<Menu> findEngineeringMenu();

    /**
     * 查找多个菜单
     * @param ids id列表
     */
    public List<Menu> findByIdIn(List<Long> ids);

    /**
     * 查找响应状态的菜单
     * @param sort 排序对象
     */
    @Query("select m from Menu m where type<>3 and engineering <>1   ")
    public List<Menu> findAllByStatus(Sort sort, Byte status);

    /**
     * 查询菜单URL
     * @param url id列表
     */
    public Menu findByUrl(String url);

    /**
     * 根据父ID查找子菜单
     * @param pids pid列表
     */
    public List<Menu> findByPidsLikeAndStatus(String pids, Byte status);

    /**
     * 获取排序最大值
     * @param pid 父菜单ID
     */
    @Query("select max(sort) from Menu m where m.pid = ?1 and m.status <> " + StatusConst.DELETE)
    public Integer findSortMax(long pid);

    /**
     * 根据父级菜单ID获取本级全部菜单
     * @param sort 排序对象
     * @param pid 父菜单ID
     * @param notId 需要排除的菜单ID
     */
    public List<Menu> findByPidAndIdNot(Sort sort, long pid, long notId);

    /**
     * 取消菜单与角色之间的关系
     * @param id 菜单ID
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM sys_role_menu WHERE menu_id = ?1", nativeQuery = true)
    public Integer cancelRoleJoin(Long id);


    /**
     * 通过名称和工程标识 查看工程(不是管理的工程)
     * @param title
     * @param enginnering
     * @return
     */
    Menu findByTitleAndEngineering(String title, Integer enginnering);


    /**
     * 查询父节点排序后的数据
     * @param pid
     * @return
     */
    @Query(value = "SELECT * FROM sys_menu where pid = ?1 and engineering = ?2  ORDER BY sort desc LIMIT 1",nativeQuery = true)
    Menu findFirstByPidAndByEngineeringOrderBySortDesc(Long pid,Integer engineering );

    /**
     * 查询菜单绑定没有
     * @param id
     * @return
     */

    @Query(value = "SELECT sm.* FROM sys_menu  sm \n" +
            "inner join sys_role_menu srm on srm.menu_id = sm.id\n" +
            "where  sm.id = ?1",nativeQuery = true)
    Menu findByRoleMenuInfo(Long id );

    @Modifying
    @Query(value = "insert into sys_role_menu (role_id,menu_id)VALUES (?1,?2)",nativeQuery = true)
    void addRoleMeun(Long roleId , Long menuId);



    //@Query(value = "select * from sys_menu where title like '%?1%'",nativeQuery = true)
    List<Menu> findAllByTitleContaining(String title);


    Menu getById(Long id);




}
