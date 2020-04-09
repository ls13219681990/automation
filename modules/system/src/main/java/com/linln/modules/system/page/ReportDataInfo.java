package com.linln.modules.system.page;


import lombok.Data;

import java.util.Date;

/**
 * 地下水位
 */


@Data
public class ReportDataInfo {
    /*
    观测值、观测值、开挖深度、差值、初测日期
     */

    String date;
    Double value;
    public ReportDataInfo() {
    }


    public ReportDataInfo(String date, Double value) {
        this.date = date;
        this.value = value;
    }
}

