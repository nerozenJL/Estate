package com.uestc.domain;

/**
 * Created by Ryon on 2015/11/18.
 * email:shadycola@gmail.com
 * 开门记录的实体类
 */
public class OpendoorRecord {

    final static int FAIL_OPEN  = 0;
    final static int SUCCESS_OPEN  = 0;

    final static int SOFTWARE_ERROR = 0;
    final static int HARDWARE_ERROR = 1;
    final static int PHONE_ERROR = 2;
    final static int USER_OPERATION_ERROR = 3;
    final static int OTHER_ERROR = 4;


    private String symbol,description,level;
    private int status;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
