package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 同步UC组织架构表                                         
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public class UcOrg implements Serializable {

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
     * 机构code|刘金华|20171217
     */
    private String fdCode;

    /**
     * 机构名称|刘金华|20171217
     */
    private String fdName;

    /**
     * 对应机构类型的code|刘金华|20171217
     */
    private String fdType;

    /**
     * 机构父id|刘金华|20171217
     */
    private String fdPsid;

    /**
     * 机构父名称|刘金华|20171217
     */
    private String fdPname;

    /**
     * 父标准角色id用/拼接的全路径|刘金华|20180103
     */
    private String fdPsidTree;

    /**
     * 父机构名称用/拼接的全路径|刘金华|20171217
     */
    private String fdPnameTree;

    /**
     * 机构id用/拼接的全路径|刘金华|20171217
     */
    private String fdSidTree;

    /**
     * 机构名称用/拼接的全路径|刘金华|20171217
     */
    private String fdNameTree;

    /**
     * 显示序列|刘金华|20171217
     */
    private Integer fdOrder;

    /**
     * 数据是否可用：@1可用，@-1不可用|刘金华|20171217
     */
    private Integer fdAvailable;

    /**
     * 是否删除,@1删除,@-1不删除
     */
    private Integer fdIsdelete;

    /**
     * 上一级机构类型为机构的id
     */
    private String fdPorgSid;

    /**
     * 0非叶子，1叶子结点
     */
    private Integer fdLeafFlag;

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
    public String getFdCode() {
        return fdCode;
    }

    public void setFdCode(String fdCode) {
        this.fdCode = fdCode;
    }
    public String getFdName() {
        return fdName;
    }

    public void setFdName(String fdName) {
        this.fdName = fdName;
    }
    public String getFdType() {
        return fdType;
    }

    public void setFdType(String fdType) {
        this.fdType = fdType;
    }
    public String getFdPsid() {
        return fdPsid;
    }

    public void setFdPsid(String fdPsid) {
        this.fdPsid = fdPsid;
    }
    public String getFdPname() {
        return fdPname;
    }

    public void setFdPname(String fdPname) {
        this.fdPname = fdPname;
    }
    public String getFdPsidTree() {
        return fdPsidTree;
    }

    public void setFdPsidTree(String fdPsidTree) {
        this.fdPsidTree = fdPsidTree;
    }
    public String getFdPnameTree() {
        return fdPnameTree;
    }

    public void setFdPnameTree(String fdPnameTree) {
        this.fdPnameTree = fdPnameTree;
    }
    public String getFdSidTree() {
        return fdSidTree;
    }

    public void setFdSidTree(String fdSidTree) {
        this.fdSidTree = fdSidTree;
    }
    public String getFdNameTree() {
        return fdNameTree;
    }

    public void setFdNameTree(String fdNameTree) {
        this.fdNameTree = fdNameTree;
    }
    public Integer getFdOrder() {
        return fdOrder;
    }

    public void setFdOrder(Integer fdOrder) {
        this.fdOrder = fdOrder;
    }
    public Integer getFdAvailable() {
        return fdAvailable;
    }

    public void setFdAvailable(Integer fdAvailable) {
        this.fdAvailable = fdAvailable;
    }
    public Integer getFdIsdelete() {
        return fdIsdelete;
    }

    public void setFdIsdelete(Integer fdIsdelete) {
        this.fdIsdelete = fdIsdelete;
    }
    public String getFdPorgSid() {
        return fdPorgSid;
    }

    public void setFdPorgSid(String fdPorgSid) {
        this.fdPorgSid = fdPorgSid;
    }
    public Integer getFdLeafFlag() {
        return fdLeafFlag;
    }

    public void setFdLeafFlag(Integer fdLeafFlag) {
        this.fdLeafFlag = fdLeafFlag;
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
        return "UcOrg{" +
        "id=" + id +
        ", fdSid=" + fdSid +
        ", fdCode=" + fdCode +
        ", fdName=" + fdName +
        ", fdType=" + fdType +
        ", fdPsid=" + fdPsid +
        ", fdPname=" + fdPname +
        ", fdPsidTree=" + fdPsidTree +
        ", fdPnameTree=" + fdPnameTree +
        ", fdSidTree=" + fdSidTree +
        ", fdNameTree=" + fdNameTree +
        ", fdOrder=" + fdOrder +
        ", fdAvailable=" + fdAvailable +
        ", fdIsdelete=" + fdIsdelete +
        ", fdPorgSid=" + fdPorgSid +
        ", fdLeafFlag=" + fdLeafFlag +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
