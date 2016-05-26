package com.uestc.domain;

/**
 * Created by Ryon on 2015/11/14.
 * email:shadycola@gmail.com
 * 这是开门所需数据的实体类
 */
public class Secret {
    private  String symbol ,secret ,ssidPassword;
    private  int controlType;

    public   String getSymbol() {
        return symbol;
    }

    public  void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public   String getSecret() {
        return secret;
    }

    public   void setSecret(String secret) {
        this.secret = secret;
    }

    public   String getSsidPassword() {
        return ssidPassword;
    }

    public   void setSsidPassword(String ssidPassword) {
        this.ssidPassword = ssidPassword;
    }

    public   int getControlType() {
        return controlType;
    }

    public   void setControlType(int controlType) {

        this.controlType = controlType;
    }
}
