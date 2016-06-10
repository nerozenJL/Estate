package com.uestc.domain;

/**
 * Created by Nerozen on 2016/6/6.
 */
public class LeaseHistoryBean {
    final static public int SECTIONTYPE_HEADER = 0;
    final static public int SECTIONTYPE_CONTENT = 1;

    private String date;
    public String getDate() {
        return date;
    }

    private int SECTIONTYPE;
    public int getSECTIONTYPE() {
        return SECTIONTYPE;
    }

    private int leasePlanStatus; //该计划当前的状态
    private String stopName; //车位的名称
    private String leasePlanPeriod; //预计划的租用执行时间

    public LeaseHistoryBean(int sectionType, int leasePlanStatus,
                            String stopName, String leasePlanPeriod) {
        SECTIONTYPE = sectionType;
        this.leasePlanStatus = leasePlanStatus;
        this.stopName = stopName;
        this.leasePlanPeriod = leasePlanPeriod;
    }

    public LeaseHistoryBean(int sectionType, String date) {
        this.date = date;
    }
}
