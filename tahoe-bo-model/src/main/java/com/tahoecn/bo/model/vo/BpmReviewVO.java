package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发起流程响应对象
 *
 * @author panglx
 * @date 2019/6/10
 */
@ApiModel("发起流程响应对象")
@Data
public class BpmReviewVO{

    @ApiModelProperty(value="审批ID", name="approveId")
    private String approveId;

    @ApiModelProperty(value="跳转地址", name="redirectUrl")
    private String redirectUrl;

}
