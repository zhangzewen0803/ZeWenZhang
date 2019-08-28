package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 同步UC标准角色表
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public class UcStandardRole implements Serializable {

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
     * 角色code|刘金华|20171217
     */
    private String fdCode;

    /**
     * 角色名称|刘金华|20171217
     */
    private String fdName;

    /**
     * 是否能发起流程@1能，@-1不能|刘金华|20171218
     */
    private Integer fdInitiatingProcess;

    /**
     * 是否在org下显示@1显示， @-1不显示|刘金华|20171218
     */
    private Integer fdIsOrg;

    /**
     * 角色父id|刘金华|20171217
     */
    private String fdPsid;

    /**
     * 角色父名称|刘金华|20171217
     */
    private String fdPname;

    /**
     * 父通用角色名称用/拼接的全路径|刘金华|20171217
     */
    private String fdPnameTree;

    /**
     * 父标准角色id用/拼接的全路径|刘金华|20171224
     */
    private String fdPsidTree;

    /**
     * 通用角色id用/拼接的全路径|刘金华|20171217
     */
    private String fdSidTree;

    /**
     * 通用角色名称用/拼接的全路径|刘金华|20171217
     */
    private String fdNameTree;

    /**
     * 角色级别分1-4级|刘金华|20171217
     */
    private Integer fdLevel;

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
     * 0非叶子，1叶子结点
     */
    private Integer fdLeafFlag;

    /**
     * 入口权限控制，比特位数据 |刘金华|20171217
     */
    private String fdSysAccess;

    /**
     * 备注
     */
    private String fdRemark;

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
    public Integer getFdInitiatingProcess() {
        return fdInitiatingProcess;
    }

    public void setFdInitiatingProcess(Integer fdInitiatingProcess) {
        this.fdInitiatingProcess = fdInitiatingProcess;
    }
    public Integer getFdIsOrg() {
        return fdIsOrg;
    }

    public void setFdIsOrg(Integer fdIsOrg) {
        this.fdIsOrg = fdIsOrg;
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
    public String getFdPnameTree() {
        return fdPnameTree;
    }

    public void setFdPnameTree(String fdPnameTree) {
        this.fdPnameTree = fdPnameTree;
    }
    public String getFdPsidTree() {
        return fdPsidTree;
    }

    public void setFdPsidTree(String fdPsidTree) {
        this.fdPsidTree = fdPsidTree;
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
    public Integer getFdLevel() {
        return fdLevel;
    }

    public void setFdLevel(Integer fdLevel) {
        this.fdLevel = fdLevel;
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
    public Integer getFdLeafFlag() {
        return fdLeafFlag;
    }

    public void setFdLeafFlag(Integer fdLeafFlag) {
        this.fdLeafFlag = fdLeafFlag;
    }
    public String getFdSysAccess() {
        return fdSysAccess;
    }

    public void setFdSysAccess(String fdSysAccess) {
        this.fdSysAccess = fdSysAccess;
    }
    public String getFdRemark() {
        return fdRemark;
    }

    public void setFdRemark(String fdRemark) {
        this.fdRemark = fdRemark;
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
        return "UcStandardRole{" +
        "id=" + id +
        ", fdSid=" + fdSid +
        ", fdCode=" + fdCode +
        ", fdName=" + fdName +
        ", fdInitiatingProcess=" + fdInitiatingProcess +
        ", fdIsOrg=" + fdIsOrg +
        ", fdPsid=" + fdPsid +
        ", fdPname=" + fdPname +
        ", fdPnameTree=" + fdPnameTree +
        ", fdPsidTree=" + fdPsidTree +
        ", fdSidTree=" + fdSidTree +
        ", fdNameTree=" + fdNameTree +
        ", fdLevel=" + fdLevel +
        ", fdOrder=" + fdOrder +
        ", fdAvailable=" + fdAvailable +
        ", fdIsdelete=" + fdIsdelete +
        ", fdLeafFlag=" + fdLeafFlag +
        ", fdSysAccess=" + fdSysAccess +
        ", fdRemark=" + fdRemark +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
