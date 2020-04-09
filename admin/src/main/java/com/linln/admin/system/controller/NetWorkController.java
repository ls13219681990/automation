package com.linln.admin.system.controller;

import com.linln.admin.system.common.socket.ChannelInfo;
import com.linln.common.constant.AdminConst;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.actionLog.annotation.EntityParam;
import com.linln.component.shiro.ShiroUtil;
import com.linln.modules.system.domain.*;
import com.linln.modules.system.page.NetWorkPage;
import com.linln.modules.system.page.TablePage;
import com.linln.modules.system.service.NetWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.linln.admin.system.common.socket.HandlerHandlerSelectionKeyImpl.channelList;
import static org.springframework.beans.BeanUtils.copyProperties;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/netWork")
public class NetWorkController {


    @Autowired
    private NetWorkService netWorkService;


    /**
     * 列表页面
     */
    @GetMapping("/indexManage")
    /*@RequiresPermissions("system:netWork:indexManage")*/
    public String index(Model model, @EntityParam NetWork netWork) {

        // 创建匹配器，进行动态查询匹配
        /*ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("No", match -> match.contains());*/


        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("no", ExampleMatcher.GenericPropertyMatchers.contains());

        // 获取采集仪列表
        Example<NetWork> example = Example.of(netWork, matcher);


        Page<NetWork> pageList = netWorkService.getPageList(example);


        // 封装数据
        model.addAttribute("list", pageList.getContent());
        model.addAttribute("page", pageList);
        return "/system/netWork/indexManage";
    }


    /**
     * 查询用户的传感器
     */
    @RequestMapping("/userNetWork")
    /* @RequiresPermissions("system:acquisitionInstrument:userAcquisition")*/
    @ResponseBody
    public TablePage userNetWork(String registerSSID,Integer page, Integer limit) {

        List<NetWork> dataList = new ArrayList<>();
        List<NetWorkPage> netWorkPageList = new ArrayList<>();
        int total = 0;
        //获取当前用户
        User user = ShiroUtil.getSubject();
        //判断是不是超级管理员 是超级管理员那么久可以看到全部,不是就只能看到自己的和没有关联的
        if (user.getId().equals(AdminConst.ADMIN_ID)) {
            if (registerSSID != null && !"".equals(registerSSID)) {

                Page<NetWork> netWorkPage = netWorkService.findAllByRegisterSSIDLike("%" + registerSSID + "%", page, limit);
                total =  Integer.valueOf(String.valueOf(netWorkPage.getTotalElements())) ;
                dataList =  netWorkPage.getContent();

            } else {
                Page<NetWork> netWorkPage = netWorkService.findAll( page,  limit);
                total =  Integer.valueOf(String.valueOf(netWorkPage.getTotalElements())) ;
                dataList =  netWorkPage.getContent();
            }

        } else {
            if (registerSSID != null && !"".equals(registerSSID)) {
                //dataList = netWorkService.findUserAcquisitionByNo(user.getId(),registerSSID);
                Page<NetWork> netWorkPage = netWorkService.finUserNetWork(user.getId(), registerSSID,page,  limit);
                total =  Integer.valueOf(String.valueOf(netWorkPage.getTotalElements())) ;
                dataList =  netWorkPage.getContent();
            } else {
                Page<NetWork> netWorkPage = netWorkService.finUserNetWork(user.getId(),page,  limit);
                total =  Integer.valueOf(String.valueOf(netWorkPage.getTotalElements())) ;
                dataList =  netWorkPage.getContent();
            }
        }

        //10/16 增加模块在线状态  1为已连接  0为 未连接


        for (NetWork netWork : dataList) {
            NetWorkPage netWorkPage = new NetWorkPage();
            copyProperties(netWork, netWorkPage);

            for (ChannelInfo channelInfo : channelList) {
                if (netWorkPage.getRegisterSSID().equals(channelInfo.getNetWork())) {
                    netWorkPage.setConnectStatus(1);
                    break;
                }
            }
            netWorkPageList.add(netWorkPage);

        }


        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(total);
        tablePage.setData(netWorkPageList);
        return tablePage;
    }


    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    /*@RequiresPermissions("system:netWork:add")*/
    public String toAdd() {
        return "/system/netWork/add";
    }


    /**
     * 添加参数
     */
    @PostMapping("/save")
    /*@RequiresPermissions("system:netWork:save")*/
    @ResponseBody
    public ResultVo saveParameter(NetWork netWork) {
        netWorkService.saveAndFlush(netWork);
        return ResultVoUtil.SAVE_SUCCESS;
    }


    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    /*@RequiresPermissions("system:netWork:edit")*/
    public String toDetail(@PathVariable("id") NetWork netWork, Model model) {
        model.addAttribute("netWork", netWork);
        return "/system/netWork/edit";
    }


    /**
     * 跳转到编辑页面
     */
    @GetMapping("findAll")
    /*@RequiresPermissions("system:netWork:findAll")*/
    @ResponseBody
    public List<NetWork> findAll() {
        return netWorkService.findAll();
    }







    /**
     * 删除4G模块
     */
    @GetMapping("/del")
    /*@RequiresPermissions("system:netWork:save")*/
    @ResponseBody
    public ResultVo del(Long  id) {

        netWorkService.deleteById(id);

        return ResultVoUtil.SAVE_SUCCESS;
    }





}

























