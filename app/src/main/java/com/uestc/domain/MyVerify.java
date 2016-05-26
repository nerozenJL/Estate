package com.uestc.domain;

import java.io.Serializable;

/**
 * Created by Ryon on 2016/1/4.
 * email:para.ryon@foxmail.com
 */
public class MyVerify implements Serializable {

    private String name,id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
