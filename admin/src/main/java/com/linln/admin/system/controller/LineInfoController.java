package com.linln.admin.system.controller;

import com.linln.admin.system.common.CommonMethod;
import com.linln.common.constant.AdminConst;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.actionLog.annotation.EntityParam;
import com.linln.component.shiro.ShiroUtil;
import com.linln.modules.system.Common.EntityUtils;
import com.linln.modules.system.domain.*;
import com.linln.modules.system.page.LineMeasuringSpotDataPage;

import com.linln.modules.system.page.TablePage;
import com.linln.modules.system.page.TitlePage;
import com.linln.modules.system.service.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.linln.admin.system.common.CommonMethod.addHour;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/lineInfo")
public class LineInfoController {


    @Autowired
    LineInfoService lineInfoService;


    @Autowired
    private BidSectionService bidSectionService;


    @Autowired
    MeasuringSpotDataService measuringSpotDataService;


    @Autowired
    private MenuService menuService;


    /**
     * 跳转到列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:lineInfo:index")
    public String index(Model model, BidSection bidSection, String nodeName) {
        if (nodeName != null && !"".equals(nodeName)) {
            LineInfo lineInfo = lineInfoService.findByName(nodeName);

            bidSection.setLineInfoId(lineInfo.getId());
            // 创建匹配器，进行动态查询匹配
            ExampleMatcher matcher = ExampleMatcher.matching().
                    withMatcher("lineInfoId", ExampleMatcher.GenericPropertyMatchers.contains());


            Example<BidSection> example = Example.of(bidSection, matcher);
            Page<BidSection> list = bidSectionService.getPageList(example);

            // 封装数据
            model.addAttribute("list", list.getContent());
            model.addAttribute("page", list);
            model.addAttribute("lineInfo", lineInfo);
        }

        return "/system/lineInfo/index";
    }


    @GetMapping("/data")
    @RequiresPermissions("system:lineInfo:data")
    @ResponseBody
    public TablePage data(String nodeName) {

        List<LineMeasuringSpotDataPage> sensorData = new ArrayList<>();
        if (nodeName != null && !"".equals(nodeName)) {
            List<Object[]> byNameGetMeasuringSpotData = lineInfoService.findByNameGetMeasuringSpotData(nodeName);
            sensorData = EntityUtils.castEntity(byNameGetMeasuringSpotData, LineMeasuringSpotDataPage.class, new LineMeasuringSpotDataPage());

        }
        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(sensorData.size());
        tablePage.setData(sensorData);
        return tablePage;


    }


    @GetMapping("/indexManage")
    @RequiresPermissions("system:lineInfo:indexManage")
    public String indexManage(Model model, BidSection bidSection, String nodeName) {
        if (nodeName != null && !"".equals(nodeName)) {
            LineInfo lineInfo = lineInfoService.findByName(nodeName);
            bidSection.setLineInfoId(lineInfo.getId());
            // 创建匹配器，进行动态查询匹配
            ExampleMatcher matcher = ExampleMatcher.matching().
                    withMatcher("lineInfoId", ExampleMatcher.GenericPropertyMatchers.contains());
            Example<BidSection> example = Example.of(bidSection, matcher);
            Page<BidSection> list = bidSectionService.getPageList(example);
            // 封装数据
            model.addAttribute("list", list.getContent());
            model.addAttribute("page", list);
            model.addAttribute("lineInfo", lineInfo);
        }
        return "/system/lineInfo/indexManage";
    }


    @GetMapping("/info")
    @RequiresPermissions("system:lineInfo:info")
    public String info(Model model, String nodeName) {

        if (nodeName != null && !"".equals(nodeName)) {
            LineInfo lineInfo = lineInfoService.findByName(nodeName);
            model.addAttribute("lineData", lineInfo);
        }
        return "/system/lineInfo/info";
    }


    @GetMapping("/lineInfoAll")
    public String lineInfoAll(Model model, @EntityParam LineInfo lineInfo) {

        // 创建匹配器，进行动态查询匹配
        /*ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("No", match -> match.contains());*/


        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("no", ExampleMatcher.GenericPropertyMatchers.contains());

        // 获取采集仪列表
        Example<LineInfo> example = Example.of(lineInfo, matcher);


        Page<LineInfo> pageList = lineInfoService.getPageList(example);


        // 封装数据
        model.addAttribute("list", pageList.getContent());
        model.addAttribute("page", pageList);
        return "/system/lineInfo/lineInfoAll";
    }


    /**
     * 查询所有路线
     * @return
     */
    @GetMapping("/lineInfoAllData")
    @ResponseBody
    public TablePage lineInfoAllData() {

        List<LineInfo> lineData = new ArrayList<>();


        User user = ShiroUtil.getSubject();
        List<Menu> list = new ArrayList<>();
        if (user.getId().equals(AdminConst.ADMIN_ID)) {
            list = menuService.findEngineeringMenu();
        } else {
            list = menuService.findEngineeringMenu(user.getId());
        }

        if(list.size() > 0 && !CollectionUtils.isEmpty(list) ){
            for (Menu menu: list) {
                LineInfo lineInfo = lineInfoService.findByName(menu.getTitle());
                if(lineInfo != null ){
                    lineData.add(lineInfo);
                }

            }
        }

        List data = CommonMethod.removeDuplicateWithOrder(lineData);

        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(data.size());
        tablePage.setData(data);
        return tablePage;

    }



    /**
     * 查询所有路线  报告列表下拉框使用
     * @return
     */
    @GetMapping("/lineInfoAllDataSelect")
    @ResponseBody
    public TablePage lineInfoAllDataSelect() {

        List<LineInfo> lineData = new ArrayList<>();


        User user = ShiroUtil.getSubject();
        List<Menu> list = new ArrayList<>();
        if (user.getId().equals(AdminConst.ADMIN_ID)) {
            list = menuService.findEngineeringMenu();
        } else {
            list = menuService.findEngineeringMenu(user.getId());
        }

        if(list.size() > 0 && !CollectionUtils.isEmpty(list) ){
            for (Menu menu: list) {
                LineInfo lineInfo = lineInfoService.findByName(menu.getTitle());
                if(lineInfo != null ){
                    lineData.add(lineInfo);
                }

            }
        }

        LineInfo lineInfo = new LineInfo();
        lineInfo.setName("全部");
        lineInfo.setId(0L);
        lineData.add(lineInfo);
        List data = CommonMethod.removeDuplicateWithOrder(lineData);
        Collections.sort(data, new Comparator<LineInfo>() {

            @Override
            public int compare(LineInfo o1, LineInfo o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(data.size());
        tablePage.setData(data);
        return tablePage;

    }






    @PostMapping("/updateLineInfo")
    @RequiresPermissions("system:lineInfo:updateLineInfo")
    @ResponseBody
    public ResultVo updateLineInfo(LineInfo lineInfo) {


        LineInfo used = lineInfoService.getById(lineInfo.getId());


        if (!used.getName().equals(lineInfo.getName())) {
            List<Menu> menuList = menuService.findAllByTitleContaining(used.getName());
            for (Menu menu : menuList) {
                if (menu.getUrl().indexOf("index") != -1) {
                    menu.setUrl("/system/lineInfo/index?nodeName=" + lineInfo.getName());
                } else {
                    menu.setUrl("/system/lineInfo/indexManage?nodeName=" + lineInfo.getName());
                }

                menuService.save(menu);
            }
        }



        if (!used.getName().equals(lineInfo.getName())) {
            List<Menu> menuList = menuService.findAllByTitleContaining(used.getName());
            for (Menu menu : menuList) {
                menu.setTitle(lineInfo.getName());
            }
            if (menuList.size() > 0 && !CollectionUtils.isEmpty(menuList)) {
                menuService.updateBatch(menuList);
            }
        }

        lineInfoService.update(lineInfo);

        return ResultVoUtil.success("修改成功");
    }

    @GetMapping("/title")
    @ResponseBody
    public ResultVo getTitle(Long id) {

        TitlePage titlePage = new TitlePage();

        LineInfo used = lineInfoService.getById(id);

        return ResultVoUtil.success(used);
    }



}

























