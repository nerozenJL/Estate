package com.uestc.domain;

/**
 * Created by Nerozen on 2016/3/24.
 * 在进行WiFi开锁时存储可用WiFi的相关信息，包括SSID，强度等以便进行匹配和排序
 */
public class WiFiProtalInfo {
    private int SignalStrenthLevel;
    private String Ssid;
    private String WiFiPassword;
    private String secret;

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }

    public int getSignalStrenthLevel() {
        return SignalStrenthLevel;
    }

    public String getSsid() {
        return Ssid;
    }

    public String getWiFiPassword(){ return WiFiPassword; }

    public void setSignalStrenthLevel(int signalStrenthLevel) {
        SignalStrenthLevel = signalStrenthLevel;
    }

    public void setSsid(String ssid) {
        Ssid = ssid;
    }

    public void setWiFiPassword(String wiFiPassword) {
        WiFiPassword = wiFiPassword;
    }

    public WiFiProtalInfo(String ssid, int signalStrenthLevel){
        this.Ssid = ssid;
        this.SignalStrenthLevel = signalStrenthLevel;
    }

    public WiFiProtalInfo(String ssid, String password, String secret){
        Ssid = ssid;
        WiFiPassword = password;
        this.secret = secret;
    }
}
