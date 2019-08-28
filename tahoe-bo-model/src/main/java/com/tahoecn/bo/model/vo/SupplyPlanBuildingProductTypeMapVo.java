package com.tahoecn.bo.model.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName：SupplyPlanBuildingProductTypeMapVo
 * @Description：TODO(这里用一句话描述这个类的作用) 
 * @author liyongxu 
 * @date 2019年8月9日 下午4:42:10 
 * @version 1.0.0 
 */
@Data
public class SupplyPlanBuildingProductTypeMapVo{

	@ApiModelProperty(value="楼栋业态关系ID", name="productTypeMapId")
    private String productTypeMapId;
	
	@ApiModelProperty(value="楼栋ID", name="buildingfId")
    private String buildingfId;

	@ApiModelProperty(value="楼栋名称", name="buildingfName")
    private String buildingfName;

	@ApiModelProperty(value="业态CODE", name="productTypeCode")
    private String productTypeCode;

	@ApiModelProperty(value="业态名称", name="productTypeName")
    private String productTypeName;

	@ApiModelProperty(value="全盘可售面积", name="totalCanSaleArea")
    private BigDecimal totalCanSaleArea;

	@ApiModelProperty(value="全盘可售货值", name="totalCanSalePrice")
    private BigDecimal totalCanSalePrice;

	@ApiModelProperty(value="总可售均价", name="totalCanSaleAvgPrice")
    private BigDecimal totalCanSaleAvgPrice;

	@ApiModelProperty(value="计划供货日期", name="supplyPlanDate")
    private LocalDateTime supplyPlanDate;

	@ApiModelProperty(value="供货计划面积", name="supplyPlanArea")
    private BigDecimal supplyPlanArea;

	@ApiModelProperty(value="供货计划货值", name="supplyPlanPrice")
    private BigDecimal supplyPlanPrice;

	@ApiModelProperty(value="供货实际面积", name="supplyActualArea")
    private BigDecimal supplyActualArea;

	@ApiModelProperty(value="供货实际货值", name="supplyActualPrice")
    private BigDecimal supplyActualPrice;

	@ApiModelProperty(value="供货实际套数", name="supplyActualNumber")
    private BigDecimal supplyActualNumber;

	@ApiModelProperty(value="供货实际均价", name="supplyActualAvgPrice")
    private BigDecimal supplyActualAvgPrice;

	@ApiModelProperty(value="供货实际日期", name="supplyActualDate")
    private LocalDateTime supplyActualDate;
	
	@ApiModelProperty(value="供货计划数据List", name="supplyPlanDataList")
    private List<SupplyPlanBuildingProductTypeDataVo> supplyPlanDataList;

}
