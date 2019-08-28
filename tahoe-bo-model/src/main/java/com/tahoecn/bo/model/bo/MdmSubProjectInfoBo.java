package com.tahoecn.bo.model.bo;

import lombok.Data;

/**
 * @ClassName：MdmSubProjectInfoBo
 * @Description：TODO(这里用一句话描述这个类的作用) 
 * @author liyongxu 
 * @date 2019年5月29日 下午7:38:21 
 * @version 1.0.0 
 */
@Data
public class MdmSubProjectInfoBo{

	private String parentSid;
	private String sortId;
	private String sid;
	private String levelType;
	private String code;
	private String principal;
	private String f_Name;
	
}
