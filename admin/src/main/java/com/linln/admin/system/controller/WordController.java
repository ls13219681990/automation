package com.linln.admin.system.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/word")
public class WordController {







    /**
     * 列表页面
     */
    @RequestMapping("/index")

    public String index() {


        return "/system/word/index";
    }























}
