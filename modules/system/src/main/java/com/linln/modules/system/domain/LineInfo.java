package com.linln.modules.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

/**
 * 线路信息
 */


@Entity
@Table(name = "lineInfo")
@Data
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
public class LineInfo {

    //线路名称、编码、起始里程、结束里程、建设单位、设计单位、线路概况、、备注
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    private String name;
    private String no;//编码
    private String startMileage;//起始里程
    private String endMileage;//结束里程
    private String buildCompany;//建设单位
    private String designCompany;//设计单位
    private String lineSurvey;//线路概况
    private String remarks;//备注

    public LineInfo() {
    }

    public LineInfo(Long id, String name, String no, String startMileage, String endMileage, String buildCompany, String designCompany, String lineSurvey, String remarks) {
        this.id = id;
        this.name = name;
        this.no = no;
        this.startMileage = startMileage;
        this.endMileage = endMileage;
        this.buildCompany = buildCompany;
        this.designCompany = designCompany;
        this.lineSurvey = lineSurvey;
        this.remarks = remarks;
    }





}
