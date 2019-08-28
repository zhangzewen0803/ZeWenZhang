/**
 * 
 */
package com.tahoecn.bo.model.vo;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName：DataPrivVO
 * @Description：TODO(这里用一句话描述这个类的作用) 
 * @author liyongxu 
 * @date 2019年6月17日 上午10:41:59 
 * @version 1.0.0 
 */
@Data
@ApiModel(value="用户数据权限信息",description="用户数据权限信息")
public class DataPrivVO{
	
	@ApiModelProperty(value="数据权限控制点名称", name="fdCode", required=true)	
	private String fdCode;
	
	@ApiModelProperty(value="数据权限json列表", name="fdDataPrivList", required=true)
	private List<DataPrivDetailVO> fdDataPrivList;
	
}
