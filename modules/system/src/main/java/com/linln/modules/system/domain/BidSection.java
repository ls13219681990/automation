package com.linln.modules.system.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * 合同段信息（标段）
 */
@Entity
@Table(name = "bidSection")
@Data
public class BidSection {

//合同段信息：合同段名称、编码、起始里程、结束里程、设计单位、施工单位、监理单位、合同段概况、合同段平面图、备注。
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private String name;
    private String no;//编码
    private String startMileage;//起始里程
    private String endMileage;//结束里程
    private String designCompany;//设计单位
    private String constructionCompany;//施工单位
    private String supervisorCompany;//监理单位
    private String bidSectionSurvey;//合同段概况
    private Long lineInfoId;//线路ID
    private Long startTime;//起始时间 （用于报告）
    private String remarks;//备注



    public BidSection() {
    }

    public BidSection(Long id, String name, String no, String startMileage, String endMileage, String designCompany, String constructionCompany, String supervisorCompany, String bidSectionSurvey, Long lineInfoId, Long startTime, String remarks) {
        this.id = id;
        this.name = name;
        this.no = no;
        this.startMileage = startMileage;
        this.endMileage = endMileage;
        this.designCompany = designCompany;
        this.constructionCompany = constructionCompany;
        this.supervisorCompany = supervisorCompany;
        this.bidSectionSurvey = bidSectionSurvey;
        this.lineInfoId = lineInfoId;
        this.startTime = startTime;
        this.remarks = remarks;
    }
}
