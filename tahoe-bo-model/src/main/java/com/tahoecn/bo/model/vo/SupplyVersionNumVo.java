package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName：SupplyVersionNumVo
 * @Description：供货计划版本号
 * @author liyongxu 
 * @date 2019年8月9日 上午9:54:19 
 * @version 1.0.0 
 */
@Data
public class SupplyVersionNumVo{

	@ApiModelProperty(value="版本Id", name="versionId")
    private String versionId;

    @ApiModelProperty(value="版本名称", name="versionName")
    private String versionName;

    @ApiModelProperty(value="版本状态", name="versionStatus")
    private Integer versionStatus;

    @ApiModelProperty(value="版本生成时间", name="versionDate")
    private String versionDate;
    
    @ApiModelProperty(value="流程id", name="versionProcessId")
    private String versionProcessId;

    @ApiModelProperty(value="流程时间", name="versionApprovaledDate")
    private String versionApprovaledDate;

    @ApiModelProperty(value="版本状态描述", name="versionStatusDesc")
    private String versionStatusDesc;
    
    @ApiModelProperty(value="价格开始时间", name="priceStartTime")
    private String priceStartTime;
    
    @ApiModelProperty(value="价格阶段名称", name="priceStageName")
    private String priceStageName;
    
    @ApiModelProperty(value="价格版本号", name="priceVersion")
    private String priceVersion;
    
    @ApiModelProperty(value="价格结束时间", name="priceEndTime")
    private String priceEndTime;

    @ApiModelProperty(value="面积开始时间", name="areaStartTime")
    private String areaStartTime;
    
    @ApiModelProperty(value="面积阶段名称", name="areaStageName")
    private String areaStageName;
    
    @ApiModelProperty(value="面积版本号", name="areaVersion")
    private String areaVersion;
    
    @ApiModelProperty(value="面积结束时间", name="areaEndTime")
    private String areaEndTime;
    
}
