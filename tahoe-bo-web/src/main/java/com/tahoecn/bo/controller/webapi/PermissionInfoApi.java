package com.tahoecn.bo.controller.webapi;


import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tahoecn.bo.common.enums.CodeEnum;
import com.tahoecn.bo.common.utils.StrUtils;
import com.tahoecn.bo.controller.TahoeBaseController;
import com.tahoecn.bo.model.entity.BoSysPermission;
import com.tahoecn.bo.model.vo.DataPrivVO;
import com.tahoecn.bo.model.vo.SysPrivVo;
import com.tahoecn.bo.service.BoSysPermissionService;
import com.tahoecn.core.json.JSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = "权限API", value = "权限API")
@RestController
@RequestMapping(value = "/api/permission")
public class PermissionInfoApi extends TahoeBaseController{
	
	@Autowired 
	private BoSysPermissionService boSysPermissionService;
	
	/**
	 * @Title: getUsreSysPriv 
	 * @Description: 用户系统权限
	 * @param request
	 * @param userName
	 * @return JSONResult<SysPrivVo>
	 * @author liyongxu
	 * @date 2019年5月29日 上午10:50:40 
	*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ApiOperation(value = "用户系统权限", notes = "用户系统权限")
    @RequestMapping(value = "/getUserSysPriv", method = RequestMethod.POST)
    public JSONResult<SysPrivVo> getUserSysPriv(HttpServletRequest request,
    		@ApiParam(name = "userName", value = "用户名", required = false) @RequestParam(value = "userName", defaultValue = "") String userName) {
		JSONResult jsonResult = new JSONResult();
       
		if(StrUtils.isEmpty(userName)) {
    		userName = getCurrentUser().getUsername();
    	}
    	//用户系统权限
    	List<BoSysPermission> sysPrivList = boSysPermissionService.getSysPrivList(userName);
		List<SysPrivVo> sysPrivVoList = prepareSysPrivRespVo(sysPrivList);
		jsonResult.setData(sysPrivVoList);
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
        return jsonResult;
    }
	
	/**
	 * @Title: prepareSysPrivRespVo 
	 * @Description: 组装用户系统权限vo
	 * @param sysPrivList
	 * @return List<SysPrivVo>
	 * @author liyongxu
	 * @date 2019年5月29日 上午11:00:43 
	*/
	private List<SysPrivVo> prepareSysPrivRespVo(List<BoSysPermission> sysPrivList) {
		List<SysPrivVo> sysPrivVoList = new ArrayList<SysPrivVo>();
		for (BoSysPermission boSysPerm : sysPrivList) {
			SysPrivVo sysPrivVo = new SysPrivVo();
			sysPrivVo.setSysPrivId(boSysPerm.getId());
			sysPrivVo.setPermissionType(boSysPerm.getPermissionType());
			sysPrivVo.setName(boSysPerm.getName());
			sysPrivVo.setCode(boSysPerm.getCode());
			sysPrivVo.setParentId(boSysPerm.getParentId());
			sysPrivVo.setPermissionUrl(boSysPerm.getPermissionUrl());
			sysPrivVoList.add(sysPrivVo);
		}
		return sysPrivVoList;
	}
	
	/**
	 * @Title: getUserDataPriv 
	 * @Description: 用户数据权限
	 * @param request
	 * @param userName
	 * @return JSONResult
	 * @author liyongxu
	 * @date 2019年5月29日 上午10:50:32 
	*/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "用户数据权限", notes = "用户数据权限")
    @RequestMapping(value = "/getUserDataPriv", method = RequestMethod.POST)
    public JSONResult<DataPrivVO> getUserDataPriv(HttpServletRequest request,
    		@ApiParam(name = "userName", value = "用户名", required = false) @RequestParam(value = "userName", defaultValue = "") String userName) {
		JSONResult jsonResult = new JSONResult();
		if(StrUtils.isEmpty(userName)) {
    		userName = getCurrentUser().getUsername();
    	}
        //用户数据权限
    	List<DataPrivVO> userDataPrivList = boSysPermissionService.getUserDataPrivList(userName);
		jsonResult.setData(userDataPrivList);
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
        return jsonResult;
    }
	
}
