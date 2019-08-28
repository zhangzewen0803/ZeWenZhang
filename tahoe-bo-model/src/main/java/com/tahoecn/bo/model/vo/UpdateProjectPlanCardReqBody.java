package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 修改工规证参数
 *
 * @author panglx
 * @date 2019/6/4
 */
@ApiModel("修改工规证参数")
public class UpdateProjectPlanCardReqBody implements Serializable {

    @ApiModelProperty("工规证ID")
    @NotBlank(message = "projectPlanCardId不能为空")
    private String projectPlanCardId;

    @ApiModelProperty("工规证号")
    @NotBlank(message = "planCode不能为空")
    private String planCode;

    @ApiModelProperty("拿证时间，格式：yyyy-MM-dd，如：2019-06-04")
    @NotBlank(message = "replyTime不能为空")
    private String replyTime;

    @ApiModelProperty("楼栋ID列表")
    @NotEmpty(message = "buildingIdList必须有至少一个值")
    private List<String> buildingIdList;

    public String getProjectPlanCardId() {
        return projectPlanCardId;
    }

    public void setProjectPlanCardId(String projectPlanCardId) {
        this.projectPlanCardId = projectPlanCardId;
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
