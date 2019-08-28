package com.tahoecn.bo.model.vo;

import java.util.List;

import com.tahoecn.bo.model.dto.BoCheckBuildingDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName：CheckBuildingDataParam
 * @Description：校验楼栋数据Vo
 * @author liyongxu 
 * @date 2019年6月14日 下午3:30:52 
 * @version 1.0.0 
 */
@Data
@ApiModel(value="校验楼栋数据Vo",description="校验楼栋数据Vo")
public class CheckBuildingDataParam{

	@ApiModelProperty(value="数据信息", name="data", required=true)
	private List<BoCheckBuildingDto> data;

}
