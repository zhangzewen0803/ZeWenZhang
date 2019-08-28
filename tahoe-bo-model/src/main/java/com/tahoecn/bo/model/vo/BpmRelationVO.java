package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Bpm关联业务响应对象
 *
 */
@ApiModel("Bpm关联业务响应对象")
@Data
public class BpmRelationVO{

    @ApiModelProperty(value="流程Id", name="processId")
    private String processId;

    @ApiModelProperty(value="版本Id", name="versionId")
    private String versionId;
    
    @ApiModelProperty(value="类型", name="type")
    private String type;

}
