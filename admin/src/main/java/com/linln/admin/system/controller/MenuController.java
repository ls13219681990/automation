package com.linln.admin.system.controller;

import com.linln.admin.system.common.CommonMethod;
import com.linln.admin.system.validator.LineValid;
import com.linln.admin.system.validator.TreeValid;
import com.linln.admin.system.validator.MenuValid;
import com.linln.admin.system.validator.AddMeasuringSpotValid;
import com.linln.common.constant.AdminConst;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.EntityBeanUtil;
import com.linln.common.utils.HttpServletUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.actionLog.action.SaveAction;
import com.linln.component.actionLog.action.StatusAction;
import com.linln.component.actionLog.annotation.ActionLog;
import com.linln.component.actionLog.annotation.EntityParam;
import com.linln.component.shiro.ShiroUtil;
import com.linln.component.thymeleaf.utility.DictUtil;
import com.linln.modules.system.domain.*;
import com.linln.modules.system.service.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private LineInfoService lineInfoService;

    @Autowired
    private BidSectionService bidSectionService;

    @Autowired
    private WorkSpotService workSpotService;


    @Autowired
    private MeasuringSpotService measuringSpotService;


    @Autowired
    private AcquisitionInstrumentService acquisitionService;


    @Autowired
    private MeasuringSpotSensorService measuringSpotSensorService;


    /**
     * 跳转到列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:menu:index")
    public String index(Model model) {
        String search = HttpServletUtil.getRequest().getQueryString();
        model.addAttribute("search", search);
        return "/system/menu/index";
    }

    /**
     * 菜单数据列表
     */
    @GetMapping("/list")
    /*@RequiresPermissions("system:menu:index")*/
    @ResponseBody
    public ResultVo list(Menu menu) {
        menu.setEngineering(0);
        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("title", match -> match.contains()).withMatcher("engineering", ExampleMatcher.GenericPropertyMatchers.contains());

        // 获取菜单列表
        Example<Menu> example = Example.of(menu, matcher);
        Sort sort = new Sort(Sort.Direction.ASC, "sort");
        List<Menu> list = menuService.getListByExample(example, sort);

        Iterator<Menu> it = list.iterator();
        while (it.hasNext()) {
            Menu str = it.next();
            if (str.getType().equals((byte) 4) || str.getId() > 1000) {
                it.remove();
            }
        }
        // TODO: 2019/2/25 菜单类型处理方案
        list.forEach(editMenu -> {
            String type = String.valueOf(editMenu.getType());
            editMenu.setRemark(DictUtil.keyValue("MENU_TYPE", type));
        });

        return ResultVoUtil.success(list);
    }


    /**
     * 菜单数据列表
     */
    @GetMapping("/lineTree")
    @ResponseBody
    public ResultVo lineTree(Menu menu) {
        //获取当前用户
        User user = ShiroUtil.getSubject();


        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("title", match -> match.contains());

        // 获取菜单列表
        Example<Menu> example = Example.of(menu, matcher);
        Sort sort = new Sort(Sort.Direction.ASC, "sort");
        List<Menu> list = new ArrayList<>();
        if (user.getId().equals(AdminConst.ADMIN_ID)) {
            list = menuService.findEngineeringMenu();
        } else {
            list = menuService.findEngineeringMenu(user.getId());
        }
        //2020/3/3 工程树不显示测点
        Iterator<Menu> iter = list.iterator();
        while (iter.hasNext()) {
            if (iter.next().getUrl().contains("measuringSpot")) {
                iter.remove();
            }
        }

        // TODO: 2019/2/25 菜单类型处理方案
        list.forEach(editMenu -> {
            String type = String.valueOf(editMenu.getType());
            editMenu.setRemark(DictUtil.keyValue("MENU_TYPE", type));
        });
        ;
        return ResultVoUtil.success(CommonMethod.removeDuplicateWithOrder(list));
    }


    /**
     * 菜单数据列表
     */
    @GetMapping("/reportLineTree")
    @ResponseBody
    public ResultVo reportLineTree(Menu menu) {
        //获取当前用户
        User user = ShiroUtil.getSubject();


        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("title", match -> match.contains());

        // 获取菜单列表
        Example<Menu> example = Example.of(menu, matcher);
        Sort sort = new Sort(Sort.Direction.ASC, "sort");
        List<Menu> list = new ArrayList<>();
        if (user.getId().equals(AdminConst.ADMIN_ID)) {
            list = menuService.findEngineeringMenu();
        } else {
            list = menuService.findEngineeringMenu(user.getId());
        }


        // TODO: 2019/2/25 菜单类型处理方案
        list.forEach(editMenu -> {
            String type = String.valueOf(editMenu.getType());
            editMenu.setRemark(DictUtil.keyValue("MENU_TYPE", type));
        });
        Iterator<Menu> iterator = list.iterator();
        while (iterator.hasNext()){
            Menu removeMenu = iterator.next();
            if(removeMenu.getUrl().indexOf("measuringSpot") != -1){
                iterator.remove();
            }
        }


        List<Object> objects = MenuTreeUtil.menuList(list);

        return ResultVoUtil.success(objects);

    }


    /**
     * 获取排序菜单列表
     */
    @GetMapping("/sortList/{pid}/{notId}")
    @RequiresPermissions({"system:menu:add", "system:menu:edit"})
    @ResponseBody
    public Map<Integer, String> sortList(
            @PathVariable(value = "pid", required = false) Long pid,
            @PathVariable(value = "notId", required = false) Long notId) {
        // 本级排序菜单列表
        notId = notId != null ? notId : (long) 0;
        List<Menu> levelMenu = menuService.getListByPid(pid, notId);
        Map<Integer, String> sortMap = new TreeMap<>();
        for (int i = 1; i <= levelMenu.size(); i++) {
            sortMap.put(i, levelMenu.get(i - 1).getTitle());
        }
        return sortMap;
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping({"/add", "/add/{pid}"})
    @RequiresPermissions("system:menu:add")
    public String toAdd(@PathVariable(value = "pid", required = false) Menu pMenu, Model model) {
        model.addAttribute("pMenu", pMenu);
        return "/system/menu/add";
    }

    /**
     * 跳转到添加线路页面
     */
    @GetMapping({"/addLineInfo"})
    @RequiresPermissions("system:menu:addLineInfo")
    public String toAddLineInfo(Menu pMenu, Model model) {
        model.addAttribute("pMenu", pMenu);
        return "/system/menu/addLineInfo";
    }

    /**
     * 跳转到添加标段页面 （左边菜单树）
     */
    @GetMapping("/addBidSection/{pid}")
    @RequiresPermissions("system:menu:addBidSection")
    public String toaddBidSection(@PathVariable(value = "pid", required = false) Menu pMenu, Model model) {
        LineInfo lineInfo = lineInfoService.findByName(pMenu.getTitle());
        model.addAttribute("pMenu", pMenu);
        model.addAttribute("no", lineInfo.getNo());
        return "/system/menu/addBidSection";
    }

    /**
     * 跳转到添加标段页面 (右边ifrme)
     */
    @GetMapping("/addBidSection")
    @RequiresPermissions("system:menu:addBidSection")
    public String toaddBidSection(Long id, Model model, Boolean tree) {//tree等于 true 表示菜单树添加   反之就是ifrme 添加
        Menu pMenu = new Menu();
        LineInfo lineInfo = new LineInfo();
        if (tree) {
            pMenu = menuService.getById(id);
            lineInfo = lineInfoService.findByName(pMenu.getTitle());
        } else {
             lineInfo = lineInfoService.getById(id);
             pMenu = menuService.findByTitleAndEngineering(lineInfo.getName(), 1);
        }

        model.addAttribute("pMenu", pMenu);
        model.addAttribute("no", lineInfo.getNo());
        model.addAttribute("tree", tree);
        return "/system/menu/addBidSection";
    }


    /**
     * 跳转到添加工点页面 （左边菜单树）
     */
    @GetMapping("/addWorkSpot/{pid}")
    @RequiresPermissions("system:menu:addWorkSpot")
    public String toAddWorkSpot(@PathVariable(value = "pid", required = false) Menu pMenu, Model model) {
        BidSection bidSection = bidSectionService.findByName(pMenu.getTitle());
        LineInfo lineInfo = lineInfoService.getById(bidSection.getLineInfoId());
        bidSection.setNo(lineInfo.getNo() + bidSection.getNo());
        model.addAttribute("pMenu", pMenu);
        model.addAttribute("no", bidSection.getNo());
        return "/system/menu/addWorkSpot";
    }

    /**
     * 跳转到添加工点页面 (右边ifrme)
     */
    @GetMapping("/addWorkSpot")
    @RequiresPermissions("system:menu:addWorkSpot")
    public String toAddWorkSpot(Long id, Model model,Boolean tree) {//tree等于 true 表示菜单树添加   反之就是ifrme 添加
        BidSection bidSection = new BidSection();
        if (tree) {
            Menu pMenu = menuService.getById(id);
             bidSection = bidSectionService.findByName(pMenu.getTitle());
        } else {
             bidSection = bidSectionService.getById(id);
        }
        LineInfo lineInfo = lineInfoService.getById(bidSection.getLineInfoId());


        bidSection.setNo(lineInfo.getNo() + bidSection.getNo());
        model.addAttribute("pMenu", menuService.findByTitleAndEngineering(bidSection.getName(), 1));
        model.addAttribute("no", bidSection.getNo());
        model.addAttribute("tree", tree);
        return "/system/menu/addWorkSpot";
    }

    /**
     * 跳转到添加测点页面(左边菜单树)
     */
    @GetMapping("/addMeasuringSpot/{pid}")
    //@RequiresPermissions("system:menu:addMeasuringSpot")
    public String toAddMeasuringSpot(@PathVariable(value = "pid", required = false) Menu pMenu, Model model) {


        WorkSpot workSpot = workSpotService.findByName(pMenu.getTitle());

        BidSection bidSection = bidSectionService.getById(workSpot.getBidSectionId());

        LineInfo lineInfo = lineInfoService.getById(bidSection.getLineInfoId());
        workSpot.setNo(lineInfo.getNo() + bidSection.getNo() + workSpot.getNo());
        model.addAttribute("pMenu", pMenu);
        model.addAttribute("no", workSpot.getNo());
        return "/system/menu/addMeasuringSpot";
    }

    /**
     * 跳转到添加测点页面(右边ifrme)
     */
    @GetMapping("/addMeasuringSpot")
    //@RequiresPermissions("system:menu:addMeasuringSpot")
    public String toAddMeasuringSpot(Long id, Model model,Boolean tree) {//tree等于 true 表示菜单树添加   反之就是ifrme 添加

        WorkSpot workSpot = new WorkSpot();

        if (tree) {
            Menu pMenu = menuService.getById(id);
            workSpot = workSpotService.findByName(pMenu.getTitle());
        } else {
             workSpot = workSpotService.getById(id);
        }

        BidSection  bidSection = bidSectionService.getById(workSpot.getBidSectionId());

        LineInfo  lineInfo = lineInfoService.getById(bidSection.getLineInfoId());





        workSpot.setNo(lineInfo.getNo() + bidSection.getNo() + workSpot.getNo());
        model.addAttribute("pMenu", menuService.findByTitleAndEngineering(workSpot.getName(), 1));
        model.addAttribute("no", workSpot.getNo());
        model.addAttribute("tree", tree);
        return "/system/menu/addMeasuringSpot";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:menu:edit")
    public String toEdit(@PathVariable("id") Menu menu, Model model) {
        Menu pMenu = menuService.getById(menu.getPid());
        model.addAttribute("menu", menu);
        model.addAttribute("pMenu", pMenu);
        return "/system/menu/add";
    }


    /**
     * 跳转到线路编辑页面
     */
    @GetMapping("/lineInfoedit")
    //@RequiresPermissions("system:menu:lineInfoedit")
    public String tolineInfoEdit(Long id, Model model,Boolean tree) {
        LineInfo lineInfo = new LineInfo();
        if(tree){
            Menu menu = menuService.findById(id);
            lineInfo = lineInfoService.findByName(menu.getTitle());
        }else{
            lineInfo = lineInfoService.getById(id);
        }

        model.addAttribute("lineInfo", lineInfo);
        return "/system/menu/editLineInfo";
    }

    /**
     * 跳转到标段编辑页面
     */
    @GetMapping("/bidSectionEdit")
    //@RequiresPermissions("system:menu:edit")
    public String toBidSectionEdit( Long id, Model model,Boolean tree) {
        BidSection bidSection = new BidSection();

        if(tree){
            Menu menu = menuService.findById(id);
            bidSection = bidSectionService.findByName(menu.getTitle());
        }else{
            bidSection = bidSectionService.getById(id);
        }


        LineInfo lineInfo = lineInfoService.getById(bidSection.getLineInfoId());
        model.addAttribute("bidSection", bidSection);
        model.addAttribute("lineInfo", lineInfo);
        return "/system/menu/editBidSection";
    }


    /**
     * 跳转到工点编辑页面
     */
    @GetMapping("/workSpotEdit")
    //@RequiresPermissions("system:menu:workSpotEdit")
    public String workSpotEdit(Long id, Model model ,Boolean tree) {
        WorkSpot workSpot = new WorkSpot();

        if(tree){
            Menu menu = menuService.findById(id);
            workSpot = workSpotService.findByName(menu.getTitle());
        }else {
             workSpot = workSpotService.getById(id);
        }



        BidSection bidSection = bidSectionService.getById(workSpot.getBidSectionId());

        LineInfo lineInfo = lineInfoService.getById(bidSection.getLineInfoId());
        bidSection.setNo(lineInfo.getNo() + bidSection.getNo());

        model.addAttribute("workSpot", workSpot);
        model.addAttribute("bidSection", bidSection);
        return "/system/menu/editWorkSpot";
    }


    /**
     * 保存添加/修改的数据
     *
     * @param valid 验证对象
     * @param menu  实体对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:menu:add", "system:menu:edit"})
    @ResponseBody
    @ActionLog(name = "菜单管理", message = "菜单：${title}", action = SaveAction.class)
    public ResultVo save(@Validated MenuValid valid, @EntityParam Menu menu) {
        if (menu.getId() == null) {
            // 排序为空时，添加到最后
            if (menu.getSort() == null) {
                Integer sortMax = menuService.getSortMax(menu.getPid());
                menu.setSort(sortMax != null ? sortMax - 1 : 0);
            }

            // 添加全部上级序号
            Menu pMenu = menuService.getById(menu.getPid());
            menu.setPids(pMenu.getPids() + ",[" + menu.getPid() + "]");
        }

        // 复制保留无需修改的数据
        if (menu.getId() != null) {
            Menu beMenu = menuService.getById(menu.getId());
            EntityBeanUtil.copyProperties(beMenu, menu, "pids");
        }

        // 排序功能
        Integer sort = menu.getSort();
        Long notId = menu.getId() != null ? menu.getId() : 0;
        List<Menu> levelMenu = menuService.getListByPid(menu.getPid(), notId);
        levelMenu.add(sort, menu);
        for (int i = 1; i <= levelMenu.size(); i++) {
            levelMenu.get(i - 1).setSort(i);
        }

        // 保存数据
        menuService.save(levelMenu);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:menu:detail")
    public String toDetail(@PathVariable("id") Menu menu, Model model) {
        model.addAttribute("menu", menu);
        return "/system/menu/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:menu:status")
    @ResponseBody
    @ActionLog(name = "菜单状态", action = StatusAction.class)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (menuService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }


    /**
     * 保存添加/修改的数据（线路）
     *
     * @param pid      父级Id
     * @param lineInfo 实体对象
     */
    @PostMapping("/saveLineInfo")
    //@RequiresPermissions({"system:saveLineInfo:add", "system:saveLineInfo:edit"})
    @ResponseBody
    /*@ActionLog(name = "菜单管理", message = "菜单：${title}", action = SaveAction.class)*/
    public ResultVo saveLineInfo(@Validated LineValid valid, Long pid, LineInfo lineInfo) {

        int sort = menuService.findFirstByPidAndByEngineeringOrderBySortDesc(0L, 0).getSort() + 1;

        //解决线路名称重复
        List<Menu> menuList = menuService.findAllByTitleContaining(lineInfo.getName());
        if (!CollectionUtils.isEmpty(menuList)) {
            int i = menuList.size() / 2;
            lineInfo.setName(lineInfo.getName() + String.valueOf(i));
        }

        Menu menu = new Menu();
        menu.setTitle(lineInfo.getName());
        menu.setPid(0L);
        menu.setPids("[" + 0 + "]");
        menu.setUrl("/system/lineInfo/indexManage?nodeName=" + lineInfo.getName());
        menu.setSort(sort);
        menu.setType(Byte.valueOf("2"));  //2:子级菜单
        menu.setPerms("system:lineInfo:indexManage");
        menu.setIcon("&#xe629;");
        menu.setCreateDate(new Date());
        menu.setEngineering(1);
        menu.setStatus(Byte.valueOf("1"));//1：启用
        menuService.save(menu);


        //管理界面
        Menu engineeringMenu = new Menu();
        engineeringMenu.setTitle(lineInfo.getName());
        engineeringMenu.setPid(0L);
        engineeringMenu.setPids("[" + 0 + "]");
        engineeringMenu.setUrl("/system/lineInfo/index?nodeName=" + lineInfo.getName());
        engineeringMenu.setSort(sort);
        engineeringMenu.setType(Byte.valueOf("2"));  //2:子级菜单
        engineeringMenu.setPerms("system:lineInfo:index");
        engineeringMenu.setIcon("&#xe629;");
        engineeringMenu.setCreateDate(new Date());
        engineeringMenu.setEngineering(0);
        engineeringMenu.setStatus(Byte.valueOf("1"));//1：启用
        menuService.save(engineeringMenu);


        //1。保存线路信息
        lineInfoService.saveAndFlush(lineInfo);


        return ResultVoUtil.SAVE_SUCCESS;
    }


    /**
     * 保存添加/修改的数据（标段）
     *
     * @param pid        父级Id
     * @param bidSection 实体对象
     */
    @PostMapping("/saveBidSection")
    //@RequiresPermissions({"system:saveBidSection:add", "system:saveBidSection:edit"})
    @ResponseBody
    /*@ActionLog(name = "菜单管理", message = "菜单：${title}", action = SaveAction.class)*/
    public ResultVo saveBidsection(@Validated TreeValid valid, Long pid, @EntityParam BidSection bidSection, String editNo) {
        bidSection.setNo(editNo);

        //解决标段名称重复
        List<Menu> menuList = menuService.findAllByTitleContaining(bidSection.getName());
        if (!CollectionUtils.isEmpty(menuList)) {
            int i = menuList.size() / 2;
            bidSection.setName(bidSection.getName() + String.valueOf(i));
        }


        //2.保存标段菜单信息
        Menu usedMenu = menuService.getById(pid);
        Menu menu = new Menu();
        menu.setTitle(bidSection.getName());
        menu.setPid(pid);
        menu.setPids(usedMenu.getPids() + ",[" + pid + "]");
        menu.setUrl("/system/bidSection/indexManage?nodeName=" + bidSection.getName());
        menu.setSort(menuService.getListByPid(pid, 0L).size() + 1);
        menu.setType(Byte.valueOf("2"));  //2:子级菜单
        menu.setPerms("system:bidSection:indexManage");
        menu.setIcon("&#xe60a;");
        menu.setCreateDate(new Date());
        menu.setEngineering(1);
        menu.setStatus(Byte.valueOf("1"));//1：启用

        menuService.save(menu);


        //保存标段菜单
        Menu usedengineering = menuService.findByTitleAndEngineering(usedMenu.getTitle(), 0);

        //管理界面
        Menu engineeringMenu = new Menu();
        engineeringMenu.setTitle(bidSection.getName());
        engineeringMenu.setPid(usedengineering.getId());
        engineeringMenu.setPids(usedengineering.getPids() + ",[" + usedengineering.getId() + "]");
        engineeringMenu.setUrl("/system/bidSection/index?nodeName=" + bidSection.getName());
        engineeringMenu.setSort(menuService.getListByPid(usedengineering.getId(), 0L).size() + 1);
        engineeringMenu.setType(Byte.valueOf("2"));  //2:子级菜单
        engineeringMenu.setPerms("system:bidSection:index");
        engineeringMenu.setIcon("&#xe60a;");
        engineeringMenu.setCreateDate(new Date());
        engineeringMenu.setEngineering(0);
        engineeringMenu.setStatus(Byte.valueOf("1"));//1：启用
        menuService.save(engineeringMenu);


        //保存标段菜单
        Menu engineering = menuService.findByTitleAndEngineering(usedMenu.getTitle(), 0);


        bidSection.setLineInfoId(Long.valueOf(lineInfoService.findByName(engineering.getTitle()).getId()));


        //1。保存标段信息
        bidSectionService.saveAndFlush(bidSection);


        User user = ShiroUtil.getSubject();
        if (!user.getId().equals(AdminConst.ADMIN_ID)) {
            Set<Role> roles = user.getRoles();
            if (roles != null && !CollectionUtils.isEmpty(roles)) {
                roles.forEach(role -> {
                    menuService.addRoleMeun(role.getId(), menu.getId());
                    menuService.addRoleMeun(role.getId(), engineeringMenu.getId());
                });
            }
        }


        return ResultVoUtil.SAVE_SUCCESS;
    }


    /**
     * 保存添加/修改的数据(工点)
     *
     * @param pid      父级Id
     * @param workSpot 实体对象
     */
    @PostMapping("/saveWorkSpot")
    //@RequiresPermissions({"system:saveWorkSpot:add", "system:saveWorkSpot:edit"})
    @ResponseBody
    /*@ActionLog(name = "菜单管理", message = "菜单：${title}", action = SaveAction.class)*/
    public ResultVo saveWorkSpot(@Validated TreeValid valid, Long pid, @EntityParam WorkSpot workSpot, String editNo) {
        workSpot.setNo(editNo);

        //解决工点名称重复
        List<Menu> menuList = menuService.findAllByTitleContaining(workSpot.getName());
        if (!CollectionUtils.isEmpty(menuList)) {
            int i = menuList.size() / 2;
            workSpot.setName(workSpot.getName() + String.valueOf(i));
        }


        //2.保存菜单信息
        Menu usedMenu = menuService.getById(pid);
        Menu menu = new Menu();
        menu.setTitle(workSpot.getName());
        menu.setPid(pid);
        menu.setPids(usedMenu.getPids() + ",[" + pid + "]");
        menu.setUrl("/system/workSpot/indexManage?nodeName=" + workSpot.getName());
        menu.setSort(menuService.getListByPid(pid, 0L).size() + 1);
        menu.setType(Byte.valueOf("2"));  //2:子级菜单
        menu.setPerms("system:workSpot:indexManage");
        menu.setIcon("&#xe60a;");
        menu.setCreateDate(new Date());
        menu.setEngineering(1);
        menu.setStatus(Byte.valueOf("1"));//1：启用

        menuService.save(menu);


        Menu usedengineering = menuService.findByTitleAndEngineering(usedMenu.getTitle(), 0);
        //管理界面
        Menu engineeringMenu = new Menu();
        engineeringMenu.setTitle(workSpot.getName());
        engineeringMenu.setPid(usedengineering.getId());
        engineeringMenu.setPids(usedengineering.getPids() + ",[" + usedengineering.getId() + "]");
        engineeringMenu.setUrl("/system/workSpot/index?nodeName=" + workSpot.getName());
        engineeringMenu.setSort(menuService.getListByPid(usedengineering.getId(), 0L).size() + 1);
        engineeringMenu.setType(Byte.valueOf("2"));  //2:子级菜单
        engineeringMenu.setPerms("system:workSpot:index");
        engineeringMenu.setIcon("&#xe60a;");
        engineeringMenu.setCreateDate(new Date());
        engineeringMenu.setEngineering(0);
        engineeringMenu.setStatus(Byte.valueOf("1"));//1：启用
        menuService.save(engineeringMenu);

        //保存线路菜单
        Menu engineering = menuService.findByTitleAndEngineering(usedMenu.getTitle(), 0);
        workSpot.setBidSectionId(Long.valueOf(bidSectionService.findByName(engineering.getTitle()).getId()));
        //1。保存线路信息
        workSpotService.saveAndFlush(workSpot);

        User user = ShiroUtil.getSubject();
        if (!user.getId().equals(AdminConst.ADMIN_ID)) {
            Set<Role> roles = user.getRoles();
            if (roles != null && !CollectionUtils.isEmpty(roles)) {
                roles.forEach(role -> {
                    menuService.addRoleMeun(role.getId(), menu.getId());
                    menuService.addRoleMeun(role.getId(), engineeringMenu.getId());
                });
            }
        }
        return ResultVoUtil.SAVE_SUCCESS;
    }


    /**
     * 保存添加/修改的数据(测点)
     *
     * @param pid           父级Id
     * @param measuringSpot 实体对象
     */
    @PostMapping("/saveMeasuringSpot")
    //@RequiresPermissions({"system:saveMeasuringSpot:add", "system:saveMeasuringSpot:edit"})
    @ResponseBody
    /*@ActionLog(name = "菜单管理", message = "菜单：${title}", action = SaveAction.class)*/
    public ResultVo saveMeasuringSpot(@Validated AddMeasuringSpotValid valid, Long pid, @EntityParam MeasuringSpot measuringSpot, String editNo) {
        measuringSpot.setNo(editNo);


        //解决测点名称重复
        List<Menu> menuList = menuService.findAllByTitleContaining(measuringSpot.getName());
        if (!CollectionUtils.isEmpty(menuList)) {
            int i = menuList.size() / 2;
            measuringSpot.setName(measuringSpot.getName() + String.valueOf(i));
        }

        //2.保存菜单信息
        Menu usedMenu = menuService.getById(pid);
        Menu menu = new Menu();
        menu.setTitle(measuringSpot.getName());
        menu.setPid(pid);
        menu.setPids(usedMenu.getPids() + ",[" + pid + "]");
        menu.setUrl("/system/measuringSpot/indexManage?nodeName=" + measuringSpot.getName());
        menu.setSort(menuService.getListByPid(pid, 0L).size() + 1);
        menu.setType(Byte.valueOf("2"));  //2:子级菜单
        menu.setPerms("system:measuringSpot:indexManage");
        menu.setIcon("&#xe60a;");
        menu.setCreateDate(new Date());
        menu.setEngineering(1);
        menu.setStatus(Byte.valueOf("1"));//1：启用

        menuService.save(menu);

//保存线路菜单
        Menu usedengineering = menuService.findByTitleAndEngineering(usedMenu.getTitle(), 0);

        //管理界面
        Menu engineeringMenu = new Menu();
        engineeringMenu.setTitle(measuringSpot.getName());
        engineeringMenu.setPid(usedengineering.getId());
        engineeringMenu.setPids(usedengineering.getPids() + ",[" + usedengineering.getId() + "]");
        engineeringMenu.setUrl("/system/measuringSpot/index?nodeName=" + measuringSpot.getName());
        engineeringMenu.setSort(menuService.getListByPid(usedengineering.getId(), 0L).size() + 1);
        engineeringMenu.setType(Byte.valueOf("2"));  //2:子级菜单
        engineeringMenu.setPerms("system:measuringSpot:index");
        engineeringMenu.setIcon("&#xe60a;");
        engineeringMenu.setCreateDate(new Date());
        engineeringMenu.setEngineering(0);
        engineeringMenu.setStatus(Byte.valueOf("1"));//1：启用
        menuService.save(engineeringMenu);

        //保存线路菜单
        Menu engineering = menuService.findByTitleAndEngineering(usedMenu.getTitle(), 0);
        measuringSpot.setWorkSpotId(workSpotService.findByName(engineering.getTitle()).getId());
        measuringSpot.setStuts(1);//在测
        //1。保存线路信息
        measuringSpotService.saveAndFlush(measuringSpot);
        User user = ShiroUtil.getSubject();
        if (!user.getId().equals(AdminConst.ADMIN_ID)) {
            Set<Role> roles = user.getRoles();
            if (roles != null && !CollectionUtils.isEmpty(roles)) {
                roles.forEach(role -> {
                    menuService.addRoleMeun(role.getId(), menu.getId());
                    menuService.addRoleMeun(role.getId(), engineeringMenu.getId());
                });
            }
        }
        return ResultVoUtil.SAVE_SUCCESS;
    }


    /**
     * 删除节点
     */
    @RequestMapping("/delTree")
    //@RequiresPermissions("system:menu:delTree")
    @ResponseBody
    public ResultVo delTree(Long id) {
        Menu menu = menuService.getById(id);
        //查询节点下面的所以节点
        List<Menu> nodesList = menuService.findByPidsLike(menu.getId().toString());
        nodesList.add(menu);
        Menu clientEngineering = menuService.findByTitleAndEngineering(menu.getTitle(), 0);
        List<Menu> clientNodesList = menuService.findByPidsLike(clientEngineering.getId().toString());
        clientNodesList.add(clientEngineering);

        //删除节点对应的数据
        for (Menu m : nodesList) {
            Menu menuInfo = menuService.findByRoleMenuInfo(m.getId());
            if (menuInfo != null) {
                menuService.cancelRoleJoin(menuInfo.getId());
            }
            if (m.getUrl().contains("lineInfo")) { //删除线路节点
                lineInfoService.removeById(lineInfoService.findByName(m.getTitle()).getId());
            } else if (m.getUrl().contains("bidSection")) {//删除标段节点
                bidSectionService.removeById(bidSectionService.findByName(m.getTitle()).getId());
            } else if (m.getUrl().contains("workSpot")) {//删除工点节点

                WorkSpot workSpot = workSpotService.findByName(m.getTitle());
                //接触工点和采集仪的关联
                List<AcquisitionInstrument> acquisitionInstruments = acquisitionService.findByWorkSpotId(workSpot.getId());
                if (!CollectionUtils.isEmpty(acquisitionInstruments)) {
                    for (AcquisitionInstrument ai : acquisitionInstruments) {
                        ai.setNetWorkId(null);
                        acquisitionService.update(ai);
                    }
                }

                workSpotService.removeById(workSpot.getId());

            } else if (m.getUrl().contains("measuringSpot")) {//删除测点节点
                //删除测点传感器关联
                MeasuringSpot measuringSpot = measuringSpotService.findByName(m.getTitle());
                if (measuringSpot != null) {
                    List<MeasuringSpotSensor> measuringSpotSensorList = measuringSpotSensorService.getByMeasuringSpotId(measuringSpot.getId());
                    if (measuringSpotSensorList.size() > 0 && !measuringSpotSensorList.isEmpty()) {
                        measuringSpotSensorService.deleteBatch(measuringSpotSensorList);
                    }
                    measuringSpotService.removeById(measuringSpot.getId());
                }

            }
        }
        //先删除关联表
        for (Menu roleMenu : clientNodesList) {
            menuService.cancelRoleJoin(roleMenu.getId());
        }
        //删除菜单
        menuService.deleteBatch(nodesList);
        menuService.deleteBatch(clientNodesList);
        return ResultVoUtil.success("删除成功");
    }


    /**
     * 删除节点
     */
    @RequestMapping("/delLine")
    @RequiresPermissions("system:menu:delLine")
    @ResponseBody
    public ResultVo delLine(Long id) {

        LineInfo lineInfo = lineInfoService.getById(id);
        Menu menu = menuService.findByTitleAndEngineering(lineInfo.getName(), 1);


        //查询节点下面的所以节点
        List<Menu> nodesList = menuService.findByPidsLike(menu.getId().toString());
        nodesList.add(menu);
        Menu clientEngineering = menuService.findByTitleAndEngineering(menu.getTitle(), 0);
        List<Menu> clientNodesList = menuService.findByPidsLike(clientEngineering.getId().toString());
        clientNodesList.add(clientEngineering);

        //删除节点对应的数据
        for (Menu m : nodesList) {
            Menu menuInfo = menuService.findByRoleMenuInfo(m.getId());
            if (menuInfo != null) {
                menuService.cancelRoleJoin(menuInfo.getId());
            }
            if (m.getUrl().contains("lineInfo")) { //删除线路节点
                lineInfoService.removeById(lineInfoService.findByName(m.getTitle()).getId());
            } else if (m.getUrl().contains("bidSection")) {//删除标段节点
                bidSectionService.removeById(bidSectionService.findByName(m.getTitle()).getId());
            } else if (m.getUrl().contains("workSpot")) {//删除工点节点

                WorkSpot workSpot = workSpotService.findByName(m.getTitle());
                //接触工点和采集仪的关联
                List<AcquisitionInstrument> acquisitionInstruments = acquisitionService.findByWorkSpotId(workSpot.getId());
                if (!CollectionUtils.isEmpty(acquisitionInstruments)) {
                    for (AcquisitionInstrument ai : acquisitionInstruments) {
                        ai.setNetWorkId(null);
                        acquisitionService.update(ai);
                    }
                }

                workSpotService.removeById(workSpot.getId());

            } else if (m.getUrl().contains("measuringSpot")) {//删除测点节点
                //删除测点传感器关联
                MeasuringSpot measuringSpot = measuringSpotService.findByName(m.getTitle());
                if (measuringSpot != null) {
                    List<MeasuringSpotSensor> measuringSpotSensorList = measuringSpotSensorService.getByMeasuringSpotId(measuringSpot.getId());
                    if (measuringSpotSensorList.size() > 0 && !measuringSpotSensorList.isEmpty()) {
                        measuringSpotSensorService.deleteBatch(measuringSpotSensorList);
                    }
                    measuringSpotService.removeById(measuringSpot.getId());
                }

            }
        }
        //先删除关联表
        for (Menu roleMenu : clientNodesList) {
            menuService.cancelRoleJoin(roleMenu.getId());
        }
        //删除菜单
        menuService.deleteBatch(nodesList);
        menuService.deleteBatch(clientNodesList);
        return ResultVoUtil.success("删除成功");
    }


}

























