package com.linln.admin.system.controller;


import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.CommonUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.actionLog.annotation.EntityParam;
import com.linln.modules.system.Common.MapEntity;
import com.linln.modules.system.Common.MapUpgradeEntity;
import com.linln.modules.system.domain.*;
import com.linln.modules.system.page.TablePage;
import com.linln.modules.system.service.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/excel")
public class ExcelController {







    /**
     * 列表页面
     */
    @GetMapping("/index")

    public String index() {


        return "/system/excel/index";
    }























}
