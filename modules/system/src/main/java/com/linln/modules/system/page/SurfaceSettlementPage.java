package com.linln.modules.system.page;

import com.linln.modules.system.domain.MeasuringSpotData;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 地表沉降表
 */
@Data
public class SurfaceSettlementPage implements Serializable {

   //表名称
    private String tableName;
    private Long workSpotId;
    //工程名称（工点名称取）
    private String projectName;
    //施工单位（标段里面取）
    private String constructionCompany;
    //监测项目（测点类型）
    private String measuringSpotType;
    //监理单位(标段里面取)
    private String supervisorCompany;
    //上次观测日期（上次生成报告的的时间）
    private String lastTimeDate;
    //本次观测日期
    private String thisTimeDate;
    //仪器规格(现在先空着以后在采集仪里面添加字段)
    private String  instrumentSpecifications;
    //备注
    private String  Remarks;
    //测点数据
    List<Object> measuringSpotData = new ArrayList<>();

 public SurfaceSettlementPage() {
 }

 public SurfaceSettlementPage(String tableName, Long workSpotId, String projectName, String constructionCompany, String measuringSpotType, String supervisorCompany, String lastTimeDate, String thisTimeDate, String instrumentSpecifications, String remarks, List<Object> measuringSpotData) {
  this.tableName = tableName;
  this.workSpotId = workSpotId;
  this.projectName = projectName;
  this.constructionCompany = constructionCompany;
  this.measuringSpotType = measuringSpotType;
  this.supervisorCompany = supervisorCompany;
  this.lastTimeDate = lastTimeDate;
  this.thisTimeDate = thisTimeDate;
  this.instrumentSpecifications = instrumentSpecifications;
  Remarks = remarks;
  this.measuringSpotData = measuringSpotData;
 }
}
