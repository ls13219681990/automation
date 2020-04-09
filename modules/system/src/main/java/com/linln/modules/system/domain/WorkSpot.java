package com.linln.modules.system.domain;

import javax.persistence.*;

/**
 * 工点
 */



@Entity
@Table(name = "workSpot")
public class WorkSpot {


    //工点信息：工点名称、编码、起始里程、结束里程、工点类型、工点概况、工点平面图、备注。
    private Long id ;
    private String name;
    private String no;//编码
    private String startMileage;//起始里程
    private String endMileage;//结束里程
    private String workSpotType;//工点类型
    private String workSpotSurvey;//工点概况
    private Long bidSectionId;//
    private String remarks;//备注

    
    public WorkSpot() {
    }

    public WorkSpot(Long id, String name, String no, String startMileage, String endMileage, String workSpotType, String workSpotSurvey, Long bidSectionId, String remarks) {
        this.id = id;
        this.name = name;
        this.no = no;
        this.startMileage = startMileage;
        this.endMileage = endMileage;
        this.workSpotType = workSpotType;
        this.workSpotSurvey = workSpotSurvey;
        this.bidSectionId = bidSectionId;
        this.remarks = remarks;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    @Column(name = "name",  length = 40)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Column(name = "no",  length = 40)
    public String getNo() {
        return no;
    }

    public void setNo(String No) {
        this.no = No;
    }
    @Column(name = "startMileage",  length = 40)
    public String getStartMileage() {
        return startMileage;
    }

    public void setStartMileage(String startMileage) {
        this.startMileage = startMileage;
    }
    @Column(name = "endMileage",  length = 40)
    public String getEndMileage() {
        return endMileage;
    }

    public void setEndMileage(String endMileage) {
        this.endMileage = endMileage;
    }
    @Column(name = "workSpotType",  length = 40)
    public String getWorkSpotType() {
        return workSpotType;
    }

    public void setWorkSpotType(String workSpotType) {
        this.workSpotType = workSpotType;
    }
    @Column(name = "workSpotSurvey",  length = 600)
    public String getWorkSpotSurvey() {
        return workSpotSurvey;
    }

    public void setWorkSpotSurvey(String workSpotSurvey) {
        this.workSpotSurvey = workSpotSurvey;
    }

    @Column(name = "bid_section_id",  length = 200)
    public Long getBidSectionId() {
        return bidSectionId;
    }

    public void setBidSectionId(Long bidSectionId) {
        this.bidSectionId = bidSectionId;
    }

    @Column(name = "remarks",  length = 200)
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

}
