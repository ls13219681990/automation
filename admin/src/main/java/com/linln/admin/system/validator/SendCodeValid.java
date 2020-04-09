package com.linln.admin.system.validator;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SendCodeValid implements Serializable {
    @NotNull(message = "采集仪还未连接")
    private Long id;
}
