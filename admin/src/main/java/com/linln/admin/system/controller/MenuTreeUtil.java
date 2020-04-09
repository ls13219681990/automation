package com.linln.admin.system.controller;

import com.linln.modules.system.domain.Menu;
import com.linln.modules.system.domain.ReportMenu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 类名称：MenuTreeUtil
 * 类描述：递归构造树型结构
 */
public class MenuTreeUtil {

    public static Map<String, Object> mapArray = new LinkedHashMap<String, Object>();


    public static List<Object> menuList(List<Menu> menu) {
        List<Object> list = new ArrayList<Object>();
        for (Menu x : menu) {
            Map<String, Object> mapArr = new LinkedHashMap<String, Object>();
            if (x.getPid() == 0) {

                mapArr.put("title", x.getTitle());
                mapArr.put("icon", x.getIcon());
                mapArr.put("url", x.getUrl());
                mapArr.put("id", x.getId());
                mapArr.put("children", menuChild(x.getId(), menu));
                list.add(mapArr);
            }
        }
        return list;
    }


    public static List<Object> reportMenuList(List<ReportMenu> menu) {
        List<Object> list = new ArrayList<Object>();
        for (ReportMenu x : menu) {
            Map<String, Object> mapArr = new LinkedHashMap<String, Object>();
            if (x.getPid() == 0) {
                mapArr.put("title", x.getName());
                mapArr.put("id", x.getId());
       /* mapArr.put("icon", x.getIcon());
        mapArr.put("url", x.getUrl());*/
                mapArr.put("children", reportMenuChild(x.getId(), menu));
                list.add(mapArr);
            }
        }
        return list;
    }


    public static List<?> menuChild(Long id, List<Menu> menu) {
        List<Object> lists = new ArrayList<Object>();
        for (Menu a : menu) {
            Map<String, Object> childArray = new LinkedHashMap<String, Object>();
            if (id.equals(a.getPid())) {
                childArray.put("title", a.getTitle());
                childArray.put("icon", a.getIcon());
                childArray.put("url", a.getUrl());
                childArray.put("id", a.getId());
                childArray.put("children", menuChild(a.getId(), menu));
                lists.add(childArray);
            }
        }
        return lists;
    }


    public static List<?> reportMenuChild(Long id, List<ReportMenu> menu) {
        List<Object> lists = new ArrayList<Object>();
        for (ReportMenu a : menu) {
            Map<String, Object> childArray = new LinkedHashMap<String, Object>();
            if (id.equals(a.getPid())) {
                childArray.put("title", a.getName());
                childArray.put("id", a.getId());
                childArray.put("children", reportMenuChild(a.getId(), menu));
                lists.add(childArray);
            }
        }
        return lists;
    }


}