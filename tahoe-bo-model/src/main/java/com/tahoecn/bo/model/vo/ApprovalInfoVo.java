package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("项目审批信息")
public class ApprovalInfoVo {

    @ApiModelProperty("项目/分期Id")
    private String projectId;

    @ApiModelProperty("审批内容")
    private String approvalContent;

    @ApiModelProperty("审批状态")
    private Integer approvalStatus;

    @ApiModelProperty("审批标题")
    private String approvalTitle;

    @ApiModelProperty("审批发起人")
    private String approvalMan;

    @ApiModelProperty("审批时间")
    private String approvalTime;

}
