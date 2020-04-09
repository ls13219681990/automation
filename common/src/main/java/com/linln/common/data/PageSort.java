package com.linln.common.data;

import com.linln.common.utils.HttpServletUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * 分页排序数据
 * @author SWICS
 * @date 2018/12/8
 */
public class PageSort {

    private static final Integer pageSizeDef = 10;
    private static final String orderByColumnDef = "createDate";
    private static final String AcquisitionOrderByColumnDef = "InstallationTime";
    private static final String SensorOrderByColumnDef = "id";
    private static final String SensorOrdeyByReceiveTime = "receiveTime";//接收时间
    private static final String orderByNo = "no";
    private static final Sort.Direction sortDirection = Sort.Direction.DESC;



    /**
     * 创建分页排序对象
     */
    public static PageRequest pageRequest(){
        return pageRequest(pageSizeDef, orderByColumnDef, sortDirection);
    }



    public static PageRequest pageAcquisitionRequest(){
        return pageRequest(pageSizeDef, AcquisitionOrderByColumnDef, sortDirection);
    }

    public static PageRequest pageSensorDataDESC(){
        return pageRequest(pageSizeDef, SensorOrdeyByReceiveTime, sortDirection);
    }



    public static PageRequest pageNoRequest(){
        return pageRequest(pageSizeDef, orderByNo, sortDirection);
    }

    public static PageRequest pageSensorRequest(){
        return pageRequest(pageSizeDef, SensorOrderByColumnDef, sortDirection);
    }


    /**
     * 创建分页排序对象
     * @param sortDirection 排序方式默认值
     */
    public static PageRequest pageRequest(Sort.Direction sortDirection){
        return pageRequest(pageSizeDef, orderByColumnDef, sortDirection);
    }

    /**
     * 创建分页排序对象
     * @param orderByColumnDef 排序字段名称默认值
     * @param sortDirection 排序方式默认值
     */
    public static PageRequest pageRequest(String orderByColumnDef, Sort.Direction sortDirection){
        return pageRequest(pageSizeDef, orderByColumnDef, sortDirection);
    }

    /**
     * 创建分页排序对象
     * @param pageSizeDef 分页数据数量默认值
     * @param orderByColumnDef 排序字段名称默认值
     * @param sortDirection 排序方式默认值
     */
    public static PageRequest pageRequest(Integer pageSizeDef, String orderByColumnDef, Sort.Direction sortDirection){
        Integer pageIndex = HttpServletUtil.getParameterInt("page", 1);
        Integer pageSize = HttpServletUtil.getParameterInt("size", pageSizeDef);
        String orderByColumn = HttpServletUtil.getParameter("orderByColumn", orderByColumnDef);
        String direction = HttpServletUtil.getParameter("isAsc", sortDirection.toString());
        Sort sort = new Sort(Sort.Direction.fromString(direction), orderByColumn);
        return PageRequest.of(pageIndex-1, pageSize, sort);
    }
}