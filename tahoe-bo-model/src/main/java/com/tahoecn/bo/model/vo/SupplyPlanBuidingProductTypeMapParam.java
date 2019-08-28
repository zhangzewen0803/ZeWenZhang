package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName：UpdateSupplyPlanBuidingProductTypeParam
 * @Description：TODO(这里用一句话描述这个类的作用) 
 * @author liyongxu 
 * @date 2019年8月9日 下午4:26:10 
 * @version 1.0.0 
 */
@Data
public class SupplyPlanBuidingProductTypeMapParam{
	
	@ApiModelProperty(value="楼栋业态关系ID", name="productTypeMapId")
    private String productTypeMapId;
	
	@ApiModelProperty(value="供货日期", name="supplyDate")
    private String supplyDate;
	
}
