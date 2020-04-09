package com.linln.admin.system.common.word;


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

    Date date;
    Double value;
    public ReportDataInfo() {
    }


}

