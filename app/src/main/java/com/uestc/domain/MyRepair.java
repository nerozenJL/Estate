package com.uestc.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ryon on 2015/10/29.
 * email:shadycola@gmail.com
 * 我的报修的实体类
 */
public class MyRepair implements Serializable {

    /**
     *
     */

    private int id, type, status, repairManId;

    private double remark;

    private String phone , title, content, description, submitTime, processTime, finishTime, imageIdList, remarkText, cuId, result, repairPhone, repairName;

    private List<String> picturePath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone () {
        return phone;
    }

    public void setPhone (String phone) {
        this.phone = phone;
    }

    public int getRepairManId() {
        return repairManId;
    }

    public void setRepairManId(int repairManId) {
        this.repairManId = repairManId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getRemark() {
        return remark;
    }

    public void setRemark(double remark) {
        this.remark = remark;
    }

    public String getRemarkText() {
        return remarkText;
    }

    public void setRemarkText(String remarkText) {
        this.remarkText = remarkText;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public String getRrepairPhone() {
        return repairPhone;
    }

    public void setRepairPhone(String repairPhone) {
        this.repairPhone = repairPhone;
    }

    public String getRepairName() {
        return repairName;
    }

    public void setRepairName(String repairName) {
        this.repairName = repairName;
    }
}
