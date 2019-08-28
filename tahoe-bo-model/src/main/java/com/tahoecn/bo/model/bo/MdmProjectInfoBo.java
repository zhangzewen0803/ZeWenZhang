package com.tahoecn.bo.model.bo;

import lombok.Data;

/**
 * @ClassName：MdmProjectInfoBo
 * @Description：TODO(这里用一句话描述这个类的作用) 
 * @author liyongxu 
 * @date 2019年5月29日 下午7:38:17 
 * @version 1.0.0 
 */
@Data
public class MdmProjectInfoBo{

	private String parentSid;
	private String sortId;
	private String sid;
	private String levelType;
	private String code;
	private String principal;
	private String fullName;
	private String regionId;
	private String regionName;
	private String cityCompanyId;
	private String cityCompanyName;

}
