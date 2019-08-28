package com.tahoecn.bo.model.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName：SupplyPlanDataVo
 * @Description：供货计划数据Vo
 * @author liyongxu 
 * @date 2019年8月9日 上午9:54:19 
 * @version 1.0.0 
 */
@Data
public class SupplyPlanBuildingProductTypeDataVo{

	@ApiModelProperty(value="供货计划列表数据Id", name="supplyPlanDataId")
	private String supplyPlanDataId;
	
	@ApiModelProperty(value="供货计划面积", name="supplyPlanArea")
    private BigDecimal supplyPlanArea;

	@ApiModelProperty(value="供货计划货值", name="supplyPlanPrice")
    private BigDecimal supplyPlanPrice;

	@ApiModelProperty(value="供货计划日期", name="supplyPlanDate")
    private LocalDateTime supplyPlanDate;

	@ApiModelProperty(value="是否可编辑(0可编辑，1不可编辑)", name="isEdit")
	private Integer isEdit;
	
	@ApiModelProperty(value=" 供货实际面积", name="supplyActualArea")
    private BigDecimal supplyActualArea;

	@ApiModelProperty(value="供货实际货值", name="supplyActualPrice")
    private BigDecimal supplyActualPrice;

	@ApiModelProperty(value="供货实际套数", name="supplyActualNumber")
    private BigDecimal supplyActualNumber;

	@ApiModelProperty(value="供货实际均价", name="supplyActualAvgPrice")
    private BigDecimal supplyActualAvgPrice;

	@ApiModelProperty(value="供货实际日期", name="supplyActualDate")
    private LocalDateTime supplyActualDate;

}
