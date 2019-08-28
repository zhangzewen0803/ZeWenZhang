package com.tahoecn.bo.model.vo;

import java.io.Serializable;

/**
 * 当前登录人信息
 *
 * @author panglx
 * @date 2019/5/27
 */
public class CurrentUserVO implements Serializable {

    /**
     * id
     */
    private String id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 登录名
     */
    private String username;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
