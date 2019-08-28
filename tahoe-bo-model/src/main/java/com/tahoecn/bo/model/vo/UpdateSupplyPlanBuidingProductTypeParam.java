package com.tahoecn.bo.model.vo;

import java.util.List;

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
public class UpdateSupplyPlanBuidingProductTypeParam{
	
	@ApiModelProperty(value="版本Id", name="versionId")
    private String versionId;
	
	@ApiModelProperty(value="楼栋业态数据", name="supplyPlanBuidingProductTypeMapParam")
    private List<SupplyPlanBuidingProductTypeMapParam> supplyPlanBuidingProductTypeMapParam;
	
}
