package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 版本信息
 * @author panglx
 * @date 2019/5/27
 */
@ApiModel("版本信息")
public class VersionVO implements Serializable {

    @ApiModelProperty("版本ID")
    private String versionId;

    @ApiModelProperty("版本名称")
    private String versionName;

    @ApiModelProperty("版本状态")
    private Integer versionStatus;

    @ApiModelProperty("流程id")
    private String versionProcessId;

    @ApiModelProperty("流程时间")
    private String versionApprovaledDate;

    @ApiModelProperty("版本状态描述")
    private String versionStatusDesc;

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Integer getVersionStatus() {
        return versionStatus;
    }

    public void setVersionStatus(Integer versionStatus) {
        this.versionStatus = versionStatus;
    }

    public String getVersionStatusDesc() {
        return versionStatusDesc;
    }

    public void setVersionStatusDesc(String versionStatusDesc) {
        this.versionStatusDesc = versionStatusDesc;
    }

    public String getVersionProcessId() {
        return versionProcessId;
    }

    public void setVersionProcessId(String versionProcessId) {
        this.versionProcessId = versionProcessId;
    }

    public String getVersionApprovaledDate() {
        return versionApprovaledDate;
    }

    public void setVersionApprovaledDate(String versionApprovaledDate) {
        this.versionApprovaledDate = versionApprovaledDate;
    }

    @Override
    public String toString() {
        return "AreaVersionVO{" +
                "versionId='" + versionId + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionStatus=" + versionStatus +
                ", versionProcessId='" + versionProcessId + '\'' +
                ", versionApprovaledDate=" + versionApprovaledDate +
                ", versionStatusDesc='" + versionStatusDesc + '\'' +
                '}';
    }
}
