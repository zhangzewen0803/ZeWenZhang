package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 新增政府方案参数
 *
 * @author panglx
 * @date 2019/6/4
 */
@ApiModel("新增政府方案参数")
public class AddGovPlanCardReqBody implements Serializable {

    @ApiModelProperty("面积版本ID")
    @NotBlank(message = "versionId不能为空")
    private String versionId;

    @ApiModelProperty("方案号")
    @NotBlank(message = "planCode不能为空")
    private String planCode;

    @ApiModelProperty("批复时间，格式：yyyy-MM-dd，如：2019-06-04")
    @NotBlank(message = "replyTime不能为空")
    private String replyTime;

    @ApiModelProperty("楼栋ID列表")
    @NotEmpty(message = "buildingIdList必须有至少一个值")
    private List<String> buildingIdList;


    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public List<String> getBuildingIdList() {
        return buildingIdList;
    }

    public void setBuildingIdList(List<String> buildingIdList) {
        this.buildingIdList = buildingIdList;
    }
}
