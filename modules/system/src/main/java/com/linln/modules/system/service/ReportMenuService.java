package com.linln.modules.system.service;

import com.linln.modules.system.domain.Report;
import com.linln.modules.system.domain.ReportMenu;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface ReportMenuService {


    /**
     * 获取菜单列表数据
     * @param example 查询实例
     * @param sort 排序对象
     */
    List<ReportMenu> getListByExample(Example<ReportMenu> example, Sort sort);


    void save(ReportMenu reportMenu);

    void delete(ReportMenu reportMenu);

    void update(ReportMenu reportMenu);



}
