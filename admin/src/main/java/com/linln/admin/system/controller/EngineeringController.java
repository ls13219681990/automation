package com.linln.admin.system.controller;

import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.CommonUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.actionLog.annotation.EntityParam;
import com.linln.component.thymeleaf.utility.DictUtil;
import com.linln.modules.system.domain.AcquisitionInstrument;
import com.linln.modules.system.domain.Menu;
import com.linln.modules.system.service.AcquisitionInstrumentService;
import com.linln.modules.system.service.DictService;
import com.linln.modules.system.service.MenuService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/engineering")
public class EngineeringController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private AcquisitionInstrumentService acquisitionService;


    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:engineering:index")
    public String index(Model model, @EntityParam AcquisitionInstrument acquisitionInstrument) {

        // 创建匹配器，进行动态查询匹配
        /*ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("No", match -> match.contains());*/


        /*ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("no", ExampleMatcher.GenericPropertyMatchers.contains());

        // 获取采集仪列表
        Example<AcquisitionInstrument> example = Example.of(acquisitionInstrument, matcher);*/


        Page<AcquisitionInstrument> pageList = acquisitionService.getPageList();


        // 封装数据
        model.addAttribute("list", pageList.getContent());
        model.addAttribute("page", pageList);
        return "/system/engineeringTree/index";
    }


    /**
     * 菜单数据列表
     */
    @GetMapping("/list")
    @RequiresPermissions("system:engineering:index")
    @ResponseBody
    public ResultVo list(Menu menu) {
        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("title", match -> match.contains());


        List<Menu> list = menuService.findEngineeringMenu();

        // TODO: 2019/2/25 菜单类型处理方案
        list.forEach(editMenu -> {
            String type = String.valueOf(editMenu.getType());
            editMenu.setRemark(DictUtil.keyValue("MENU_TYPE", type));
        });

        return ResultVoUtil.success(list);
    }




    /**
     * 跳转到添加线路页面
     */
    @GetMapping({ "/addengineeringTreeLineInfo"})
    @RequiresPermissions("system:engineering:addengineeringTreeLineInfo")
    public String toAddLineInfo( Menu pMenu, Model model) {
        model.addAttribute("pMenu", pMenu);
        return "/system/engineeringTree/addLineInfo";
    }



    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:acquisitionInstrument:edit")
    public String toEdit(@PathVariable("id") AcquisitionInstrument dict, Model model) {
        model.addAttribute("acquisition", dict);
        return "/system/acquisitionInstrument/add";
    }

    /**
     * 保存添加/修改的数据
     */
    @PostMapping({"/add", "/edit"})
    @RequiresPermissions({"system:acquisitionInstrument:add", "system:acquisitionInstrument:edit"})
    @ResponseBody
    /*@ActionLog(name = "字典管理", message = "字典：${title}", action = SaveAction.class)*/
    public ResultVo save(/*@Validated DictValid valid,*/ @EntityParam AcquisitionInstrument acquisition) {


        acquisition.setPassagewayNumber(CommonUtil.passagewayData(Integer.valueOf(acquisition.getPassagewayNumber())));

        //没有就保存有就刷新
        acquisitionService.saveAndFlush(acquisition);
        return ResultVoUtil.SAVE_SUCCESS;

    }
    /*

     */
/**
 * 跳转到详细页面
 *//*

    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:acquisitionInstrument:detail")
    public String toDetail(@PathVariable("id") Dict dict, Model model) {
        model.addAttribute("dict", dict);
        return "/system/acquisitionInstrument/detail";
    }
*/

    /**
     * 设置一条或者多条数据的状态
     */


    @GetMapping("/delete/{id}")
    @RequiresPermissions("system:acquisitionInstrument:detail")
    public ResultVo delete(@PathVariable("id") AcquisitionInstrument acquisition) {
        if(acquisition.getId() != null && acquisition.getId() >0){
            acquisitionService.deleteById(acquisition.getId());
        }
        return ResultVoUtil.success("删除成功");
    }


    /**
     * 查询全部采集仪
     */
    @GetMapping("/findAll")
    @RequiresPermissions("system:acquisitionInstrument:findAll")
    @ResponseBody
    public List<AcquisitionInstrument> findAll() {
        List<AcquisitionInstrument> all = acquisitionService.findAll();
        return all;
    }




    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:acquisitionInstrument:status")
    @ResponseBody

    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (acquisitionService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }



    /**
     * 查询全部采集仪
     */
    @GetMapping("/addLineInfo")
    @RequiresPermissions("system:engineeringTree:addLineInfo")
    public String toAddLineInfo() {

        return "system/engineeringTree/addLineInfo";
    }



}
