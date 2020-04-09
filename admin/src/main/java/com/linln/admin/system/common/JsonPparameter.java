package com.linln.admin.system.common;

import lombok.Data;

@Data
public class JsonPparameter {

    private Integer axisAzimuth;//轴线方位角

    private double[][] parameterValue;//

    public JsonPparameter() {
    }

    public JsonPparameter(Integer axisAzimuth, double[][] parameterValue) {
        this.axisAzimuth = axisAzimuth;
        this.parameterValue = parameterValue;
    }

    public Integer getAxisAzimuth() {
        return axisAzimuth;
    }

    public void setAxisAzimuth(Integer axisAzimuth) {
        this.axisAzimuth = axisAzimuth;
    }

    public double[][] getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(double[][] parameterValue) {
        this.parameterValue = parameterValue;
    }
}