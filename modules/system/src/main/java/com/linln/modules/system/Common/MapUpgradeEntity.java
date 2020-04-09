package com.linln.modules.system.Common;

import lombok.Data;

@Data
public class MapUpgradeEntity {

    private Integer id;
    private Long entityId;
    private String name;
    private String value; //测点值
    private Integer acquisitionNumber;//传感器分组序号
    private String MeasuringSpotName; //测点名称
    private Integer isBinding; //有关联就是1  没有就是0


}
