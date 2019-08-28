package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName：SupplyPlanYearInfoVo
 * @Description：供货计划年份Vo
 * @author liyongxu 
 * @date 2019年8月9日 上午9:54:19 
 * @version 1.0.0 
 */
@Data
public class SupplyPlanYearInfoVo{

	@ApiModelProperty(value="年份", name="yearNum")
    private String yearNum;

}
