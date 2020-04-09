package com.linln.admin.system.validator;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class TreeValid implements Serializable {
    @NotEmpty(message = "名称不能为空")
    private String name;
    /*@NotEmpty(message = "编码不能为空")
    private String no;*/
    @NotEmpty(message = "编码不能为空")
    private String editNo;



}
