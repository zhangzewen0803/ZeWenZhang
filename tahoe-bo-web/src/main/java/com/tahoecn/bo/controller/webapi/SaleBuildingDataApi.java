/**
 * 项目名：泰禾资产
 * 包名：com.tahoecn.controller.common
 * 文件名：BpmReceiveApi.java
 * 版本信息：1.0.0
 * 日期：2018年10月16日-上午10:49:09
 * Copyright (c) 2018 Pactera 版权所有
 */
package com.tahoecn.bo.controller.webapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tahoecn.bo.common.enums.CodeEnum;
import com.tahoecn.bo.model.dto.BoPushProjectSubDto;
import com.tahoecn.bo.service.BoBuildingProductTypeMapService;
import com.tahoecn.core.json.JSONResult;
import com.tahoecn.uc.sso.annotation.Action;
import com.tahoecn.uc.sso.annotation.Login;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @ClassName：SaleBuildingDataApi
 * @Description：Sale楼栋数据API
 * @author liyongxu 
 * @date 2019年6月11日 下午4:54:12 
 * @version 1.0.0 
 */
@Api(tags = "sale楼栋数据API", value = "sale楼栋数据API")
@RestController
@RequestMapping(value = "/api/sale")
public class SaleBuildingDataApi {
	
	@Autowired
	private BoBuildingProductTypeMapService boBuildingProductTypeMapService;
	
	/**
	 * @Title: buildingData 
	 * @Description: Bo审批通过的楼栋信息
	 * @param approveId
	 * @return JSONResult<BoPushProjectSubDto>
	 * @author liyongxu
	 * @date 2019年6月11日 下午5:18:04 
	*/
	@ApiOperation(value = "Bo审批通过的楼栋信息", notes = "Bo审批通过的楼栋信息")
	@Login(action = Action.Skip)
	@RequestMapping(value = "/buildingData", method = RequestMethod.POST)
	public JSONResult<BoPushProjectSubDto> buildingData(String approveId) {
		JSONResult<BoPushProjectSubDto> jsonResult = new JSONResult<BoPushProjectSubDto>();
		BoPushProjectSubDto boPushProjectSub = boBuildingProductTypeMapService.getBuildingProductInfoListByApproveId(approveId);
		jsonResult.setData(boPushProjectSub);
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		return jsonResult;
	}

}
