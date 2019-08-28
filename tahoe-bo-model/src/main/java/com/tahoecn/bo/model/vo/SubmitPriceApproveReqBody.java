package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 面积审批
 *
 * @author panglx
 * @date 2019/6/10
 */
@ApiModel("价格审批")
@Data
public class SubmitPriceApproveReqBody implements Serializable {

    @ApiModelProperty("价格版本ID")
    @NotBlank(message = "versionId不能为空")
    private String versionId;

    @ApiModelProperty("主题")
    @NotBlank(message = "subject不能为空")
    private String subject;

    @ApiModelProperty("说明")
    @NotBlank(message = "content不能为空")
    private String content;

    @ApiModelProperty("审批内容地址")
    @NotBlank(message = "reviewUrl不能为空")
    private String reviewUrl;

}
