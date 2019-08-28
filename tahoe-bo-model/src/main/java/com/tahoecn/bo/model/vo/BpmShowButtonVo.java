package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName：BpmShowButtonVo
 * @Description：TODO(这里用一句话描述这个类的作用) 
 * @author liyongxu 
 * @date 2019年6月19日 下午4:29:20 
 * @version 1.0.0 
 */
@Data
public class BpmShowButtonVo{

	public BpmShowButtonVo(String butCode, String butName, String viewURL) {
		super();
		this.butCode = butCode;
		this.butName = butName;
		this.viewURL = viewURL;
	}
	
	public BpmShowButtonVo(String butCode, String butName) {
		super();
		this.butCode = butCode;
		this.butName = butName;
		this.viewURL = "";
	}

	@ApiModelProperty(value="按钮Code", name="butCode")
    private String butCode;
	
	@ApiModelProperty(value="按钮名称", name="butName")
	private String butName;
	
	@ApiModelProperty(value="流程Url", name="viewURL")
	private String viewURL;
	
	

}
