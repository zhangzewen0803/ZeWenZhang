package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 同步UC用户表
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public class UcUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 全局唯一id|刘金华|20171217
     */
    private String fdSid;

    /**
     * 姓名|刘金华|20171217
     */
    private String fdName;

    /**
     * 登录用户名|刘金华|20171217
     */
    private String fdUsername;

    /**
     * 排序号|刘金华|20171217
     */
    private Integer fdOrder;

    /**
     * 用户手机号|刘金华|20171217
     */
    private String fdTel;

    /**
     * 用户工作电话|刘金华|20171217
     */
    private String fdWorkPhone;

    /**
     * 邮箱|刘金华|20171217
     */
    private String fdEmail;

    /**
     * 是否有效：@1可用，@-1不可用|刘金华|20171217
     */
    private Integer fdAvailable;

    /**
     * 部门id全路径|刘金华|20171217
     */
    private String fdOrgIdTree;

    /**
     * 部门全路径|刘金华|20171217
     */
    private String fdOrgNameTree;

    /**
     * 部门编号|刘金华|20171217
     */
    private String fdOrgId;

    /**
     * 部门|刘金华|20171217
     */
    private String fdOrgName;

    /**
     * 是否删除,@1删除,@-1不删除
     */
    private Integer fdIsdelete;

    /**
     * 性别,@-1男,1女
     */
    private Integer fdGender;

    /**
     * 泰信id|刘金华|20180313
     */
    private String fdTahoeMessageSid;

    /**
     * 用户工作省份code|刘金华|20180820
     */
    private String fdProvinceCode;

    /**
     * 用户工作省份name|刘金华|20180820
     */
    private String fdProvinceName;

    /**
     * 用户工作城市code|刘金华|20180820
     */
    private String fdCityCode;

    /**
     * 用户工作城市name|刘金华|20180820
     */
    private String fdCityName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getFdSid() {
        return fdSid;
    }

    public void setFdSid(String fdSid) {
        this.fdSid = fdSid;
    }
    public String getFdName() {
        return fdName;
    }

    public void setFdName(String fdName) {
        this.fdName = fdName;
    }
    public String getFdUsername() {
        return fdUsername;
    }

    public void setFdUsername(String fdUsername) {
        this.fdUsername = fdUsername;
    }
    public Integer getFdOrder() {
        return fdOrder;
    }

    public void setFdOrder(Integer fdOrder) {
        this.fdOrder = fdOrder;
    }
    public String getFdTel() {
        return fdTel;
    }

    public void setFdTel(String fdTel) {
        this.fdTel = fdTel;
    }
    public String getFdWorkPhone() {
        return fdWorkPhone;
    }

    public void setFdWorkPhone(String fdWorkPhone) {
        this.fdWorkPhone = fdWorkPhone;
    }
    public String getFdEmail() {
        return fdEmail;
    }

    public void setFdEmail(String fdEmail) {
        this.fdEmail = fdEmail;
    }
    public Integer getFdAvailable() {
        return fdAvailable;
    }

    public void setFdAvailable(Integer fdAvailable) {
        this.fdAvailable = fdAvailable;
    }
    public String getFdOrgIdTree() {
        return fdOrgIdTree;
    }

    public void setFdOrgIdTree(String fdOrgIdTree) {
        this.fdOrgIdTree = fdOrgIdTree;
    }
    public String getFdOrgNameTree() {
        return fdOrgNameTree;
    }

    public void setFdOrgNameTree(String fdOrgNameTree) {
        this.fdOrgNameTree = fdOrgNameTree;
    }
    public String getFdOrgId() {
        return fdOrgId;
    }

    public void setFdOrgId(String fdOrgId) {
        this.fdOrgId = fdOrgId;
    }
    public String getFdOrgName() {
        return fdOrgName;
    }

    public void setFdOrgName(String fdOrgName) {
        this.fdOrgName = fdOrgName;
    }
    public Integer getFdIsdelete() {
        return fdIsdelete;
    }

    public void setFdIsdelete(Integer fdIsdelete) {
        this.fdIsdelete = fdIsdelete;
    }
    public Integer getFdGender() {
        return fdGender;
    }

    public void setFdGender(Integer fdGender) {
        this.fdGender = fdGender;
    }
    public String getFdTahoeMessageSid() {
        return fdTahoeMessageSid;
    }

    public void setFdTahoeMessageSid(String fdTahoeMessageSid) {
        this.fdTahoeMessageSid = fdTahoeMessageSid;
    }
    public String getFdProvinceCode() {
        return fdProvinceCode;
    }

    public void setFdProvinceCode(String fdProvinceCode) {
        this.fdProvinceCode = fdProvinceCode;
    }
    public String getFdProvinceName() {
        return fdProvinceName;
    }

    public void setFdProvinceName(String fdProvinceName) {
        this.fdProvinceName = fdProvinceName;
    }
    public String getFdCityCode() {
        return fdCityCode;
    }

    public void setFdCityCode(String fdCityCode) {
        this.fdCityCode = fdCityCode;
    }
    public String getFdCityName() {
        return fdCityName;
    }

    public void setFdCityName(String fdCityName) {
        this.fdCityName = fdCityName;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "UcUser{" +
        "id=" + id +
        ", fdSid=" + fdSid +
        ", fdName=" + fdName +
        ", fdUsername=" + fdUsername +
        ", fdOrder=" + fdOrder +
        ", fdTel=" + fdTel +
        ", fdWorkPhone=" + fdWorkPhone +
        ", fdEmail=" + fdEmail +
        ", fdAvailable=" + fdAvailable +
        ", fdOrgIdTree=" + fdOrgIdTree +
        ", fdOrgNameTree=" + fdOrgNameTree +
        ", fdOrgId=" + fdOrgId +
        ", fdOrgName=" + fdOrgName +
        ", fdIsdelete=" + fdIsdelete +
        ", fdGender=" + fdGender +
        ", fdTahoeMessageSid=" + fdTahoeMessageSid +
        ", fdProvinceCode=" + fdProvinceCode +
        ", fdProvinceName=" + fdProvinceName +
        ", fdCityCode=" + fdCityCode +
        ", fdCityName=" + fdCityName +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
