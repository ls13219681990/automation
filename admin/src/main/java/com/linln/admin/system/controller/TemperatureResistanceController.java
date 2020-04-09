package com.linln.admin.system.controller;

import com.linln.common.utils.POIUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.actionLog.annotation.EntityParam;
import com.linln.modules.system.page.TablePage;
import com.linln.modules.system.domain.TemperatureResistance;
import com.linln.modules.system.domain.TemperatureResistanceData;
import com.linln.modules.system.service.TemperatureResistanceDataService;
import com.linln.modules.system.service.TemperatureResistanceService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.linln.admin.system.common.CommonMethod;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/temperatureResistance")
public class TemperatureResistanceController {


    @Autowired
    private TemperatureResistanceService temperatureResistanceService;

    @Autowired
    private TemperatureResistanceDataService temperatureResistanceDataService;

    /**
     * 列表页面
     */
    @GetMapping("/indexManage")
    @RequiresPermissions("system:temperatureResistance:indexManage")
    public String index(Model model, @EntityParam TemperatureResistance temperatureResistance) {

        // 创建匹配器，进行动态查询匹配
        /*ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("No", match -> match.contains());*/


        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("no", ExampleMatcher.GenericPropertyMatchers.contains());

        // 获取采集仪列表
        Example<TemperatureResistance> example = Example.of(temperatureResistance, matcher);


        Page<TemperatureResistance> pageList = temperatureResistanceService.getPageList(example);


        // 封装数据
        model.addAttribute("list", pageList.getContent());
        model.addAttribute("page", pageList);
        return "/system/temperatureResistance/indexManage";
    }







    /**
     * 跳转到查看参数页面
     */
    @GetMapping("/detail/{id}")
    public String toDetail(@PathVariable("id") TemperatureResistance temperatureResistance, Model model){

        model.addAttribute("id",temperatureResistance.getId());
        return "/system/temperatureResistance/detail";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/toEdit/{id}")
    public String toEdit(@PathVariable("id") TemperatureResistance temperatureResistance, Model model){

        model.addAttribute("tr",temperatureResistance);
        return "/system/temperatureResistance/edit";
    }



    /**
     * 跳转到编辑页面
     */
    @PostMapping("/edit")
    @ResponseBody
    public ResultVo edit(TemperatureResistance temperatureResistance){
        temperatureResistanceService.update(temperatureResistance);
        return ResultVoUtil.success("修改成功");
    }



    /**
     * 通过ID获取温阻数据
     */
    @GetMapping("/temperatureResistanceData")
    @ResponseBody
    public TablePage data(Long id, Model model){
        List<TemperatureResistanceData> data = temperatureResistanceDataService.findByTemperatureResistanceId(id);
        Collections.sort(data, new Comparator<TemperatureResistanceData>() {
            public int compare(TemperatureResistanceData arg0, TemperatureResistanceData arg1) {
                Double hits0 = Double.valueOf(arg0.getTempResist());
                Double hits1 = Double.valueOf(arg1.getTempResist());
                if (hits1 < hits0) {
                    return 1;
                } else if (hits1 == hits0) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });


        TablePage tablePage = new TablePage();
        tablePage.setCode(0L);
        tablePage.setCount(data.size());
        tablePage.setData(data);
        return tablePage;
    }





    @GetMapping("/findAll")
    @ResponseBody
    public List<TemperatureResistance> findAll(){

        List<TemperatureResistance> temperatureResistances = temperatureResistanceService.findAll();

        return temperatureResistances;
    }




    @GetMapping("/downTemplate")
    @ResponseBody
    public void downTemplate(HttpServletResponse response) {
        InputStream inputStream = null;
        try {

            response.reset();
            response.setContentType("bin");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String("温阻模板.xls".getBytes(), "ISO-8859-1"));

            ServletOutputStream outputStream = response.getOutputStream();

            inputStream = new FileInputStream(new File(ResourceUtils.getURL("classpath:").getPath() + "static/excel/温阻模板.xls"));
            byte[] buff = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buff)) != -1) {
                outputStream.write(buff, 0, length);
            }
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {

                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 上传文件
     */
    @PostMapping("/uploadFiles")
    @ResponseBody
    public ResultVo uploadImg(@RequestParam MultipartFile file,
    HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 判断文件名是否为空
        if (file != null){
            String fileName = file.getOriginalFilename();
            List<String[]> strings = POIUtil.readExcel(file);

            TemperatureResistance temperatureResistance = new TemperatureResistance();
            temperatureResistance.setName(fileName);
            temperatureResistance.setUploadTime(CommonMethod.getDate());
            temperatureResistanceService.save(temperatureResistance);

            List<TemperatureResistanceData> dataList = new ArrayList<>();
            for (String[] s:strings) {
                TemperatureResistanceData data = new TemperatureResistanceData();
                data.setTemperatureResistanceId(temperatureResistance.getId());
                data.setTemp(s[0]);
                data.setTempResist(s[1]);
                dataList.add(data);
            }
            temperatureResistanceDataService.saveAll(dataList);
        }

        return ResultVoUtil.success("excel导入成功");
    }


}















