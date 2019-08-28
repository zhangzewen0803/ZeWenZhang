package com.tahoecn.bo.controller.webapi;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tahoecn.bo.common.enums.CodeEnum;
import com.tahoecn.bo.common.utils.DataUtils;
import com.tahoecn.bo.common.utils.DateUtils;
import com.tahoecn.bo.common.utils.StrUtils;
import com.tahoecn.bo.controller.TahoeBaseController;
import com.tahoecn.bo.model.entity.BoSysPermission;
import com.tahoecn.bo.model.entity.UcOrg;
import com.tahoecn.bo.model.vo.DataPrivDetailVO;
import com.tahoecn.bo.model.vo.DataPrivVO;
import com.tahoecn.bo.model.vo.OrgInfoVO;
import com.tahoecn.bo.model.vo.PowerProjectInfoVo;
import com.tahoecn.bo.model.vo.SysPrivVo;
import com.tahoecn.bo.model.vo.UserInfoVo;
import com.tahoecn.bo.model.vo.UsreDetailsRespVo;
import com.tahoecn.bo.service.BoSysPermissionService;
import com.tahoecn.bo.service.UcOrgService;
import com.tahoecn.bo.service.UcUserService;
import com.tahoecn.core.json.JSONResult;
import com.tahoecn.uc.vo.UcV1OrgInfoResultVO;
import com.tahoecn.uc.vo.UcV1UserInfoResultVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = "用户API", value = "用户API")
@RestController
@RequestMapping(value = "/api/user")
public class UserInfoApi extends TahoeBaseController{

	@Autowired 
	private UcUserService iUcUserService;
	
	@Autowired 
	private BoSysPermissionService boSysPermissionService;
	
	@Autowired
	private UcOrgService ucOrgService;
	
	private static Logger logger = LogManager.getLogger(UserInfoApi.class);
	
	/**
	 * @Title: getUserDetail
	 * @Description: 查询用户详情数据
	 * @return JsonResult
	 * @author liyongxu
	 * @date 2019年6月22日 下午3:35:38 
	*/
	@ApiOperation(value = "查询用户详情数据", notes = "查询用户详情数据")
    @RequestMapping(value = "/getUserDetails", method = RequestMethod.POST)
    public JSONResult<UsreDetailsRespVo> getUserDetails(
    		@ApiParam(name = "userName", value = "用户名", required = false) @RequestParam(value = "userName", defaultValue = "") String userName,
    		@ApiParam(name = "fdSid", value = "地产板块sid", required = false) @RequestParam(value = "fdSid", defaultValue = "") String fdSid) {
		if(StrUtils.isEmpty(userName)) {
    		userName = getCurrentUser().getUsername();
    	}
		JSONResult<UsreDetailsRespVo> jsonResult = new  JSONResult<UsreDetailsRespVo>();
		UsreDetailsRespVo usreDetailsVo = new UsreDetailsRespVo();
		logger.info("######################## ----- getUserDetails START" + DateUtils.toStrDateFromUtilDateByFormat(new Date(), DateUtils.DATE_FULL_STR));
		
		//用户信息
		UserInfoVo userInfo = this.prepareUsreInfoRespVo(iUcUserService.getUsreInfo(userName));
		usreDetailsVo.setUserInfo(userInfo);
		
		//用户系统权限
		List<SysPrivVo> sysPrivVoList = this.prepareSysPrivRespVo(boSysPermissionService.getSysPrivList(userName));
		usreDetailsVo.setUserSysPrivList(sysPrivVoList);
		
		//项目数据权限
		List<UcOrg> orgList = ucOrgService.getPowerProjectDataInfo(fdSid);
		logger.info("UcOrg 本地数据查询:" + orgList);
        List<PowerProjectInfoVo> projectPrivList = this.recomPowerProjectData(userName,orgList);
        usreDetailsVo.setProjectPrivJson(projectPrivList);
        
        logger.info("######################## ----- getUserDetails END" + DateUtils.toStrDateFromUtilDateByFormat(new Date(), DateUtils.DATE_FULL_STR));
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
 		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		jsonResult.setData(usreDetailsVo);
        return jsonResult;
    }

	/**
	 * @Title: prepareUsreInfoRespVo 
	 * @Description: 重组用户信息vo
	 * @param usreInfoResult
	 * @return UsreInfoResultVo
	 * @author liyongxu
	 * @date 2019年6月3日 下午5:06:17 
	*/
	private UserInfoVo prepareUsreInfoRespVo(UcV1UserInfoResultVO usreInfoResult) {
		UserInfoVo userInfo = new UserInfoVo();
		userInfo.setFdSid(usreInfoResult.getFdSid());
		userInfo.setFdName(usreInfoResult.getFdName());
		userInfo.setFdUsername(usreInfoResult.getFdUsername());
		userInfo.setFdOrgName(usreInfoResult.getFdOrgName());
		userInfo.setFdOrgId(usreInfoResult.getFdOrgId());
		userInfo.setFdOrgNameTree(usreInfoResult.getFdOrgNameTree());
		userInfo.setFdOrgIdTree(usreInfoResult.getFdOrgIdTree());
		userInfo.setFdOrder(usreInfoResult.getFdOrder());
		userInfo.setFdLock(usreInfoResult.getFdLock());
		userInfo.setFdTahoeMessageSid(usreInfoResult.getFdTahoeMessageSid());
		userInfo.setFdTopPhotoUrl(usreInfoResult.getFdTopPhotoUrl());
		userInfo.setFdSmallPhotoUrl(usreInfoResult.getFdSmallPhotoUrl());
		userInfo.setFdEntryTime(usreInfoResult.getFdEntryTime());
		userInfo.setOrgList(prepareOrgInfoRespVo(usreInfoResult.getOrgList()));
		return userInfo;
	}
	
	/**
	 * @Title: prepareSysPrivRespVo 
	 * @Description: 组装用户系统权限vo
	 * @param sysPrivList
	 * @return List<SysPrivVo>
	 * @author liyongxu
	 * @date 2019年6月22日 下午4:12:02 
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
	 * @Title: recomPowerProjectData 
	 * @Description: 重组数据
	 * @param userName
	 * @param orgList
	 * @return List<PowerProjectInfoVo>
	 * @author liyongxu
	 * @date 2019年6月13日 下午7:54:28 
	*/
	public List<PowerProjectInfoVo> recomPowerProjectData(String userName,List<UcOrg> orgList) {
		List<DataPrivVO> userDataPrivList = boSysPermissionService.getUserDataPrivList(userName);
		List<PowerProjectInfoVo> orgInfoList = new ArrayList<PowerProjectInfoVo>();
		List<String> sidList = new ArrayList<String>();
		String dataRangeName = "";
        for (DataPrivVO dataPrivVO : userDataPrivList) {
        	if(dataPrivVO.getFdCode().equals("bo_mnu_1")) {
        		List<DataPrivDetailVO> dataPrivList = dataPrivVO.getFdDataPrivList();
        		if(DataUtils.isNotEmpty(dataPrivList)) {
        			for (DataPrivDetailVO dataPrivDetailVO : dataPrivList) {
        				if(dataPrivDetailVO.getFdDataRangeName().equals("全集团")) {
        					dataRangeName = dataPrivDetailVO.getFdDataRangeName();
        				}else {
        					sidList.add(dataPrivDetailVO.getFdOrgSid());
						}
        			}
        		}
        	}
		}
        
        if(dataRangeName.equals("全集团")) {
        	for (UcOrg ucOrg : orgList) {
            	PowerProjectInfoVo orgInfoVo = new PowerProjectInfoVo();
            	orgInfoVo.setId(ucOrg.getFdSid());
            	orgInfoVo.setName(ucOrg.getFdName());
            	orgInfoVo.setType(ucOrg.getFdType());
            	if(ucOrg.getFdType().equals("ORG5-1")) {
            		orgInfoVo.setIsProject("1");
            	}else {
            		orgInfoVo.setIsProject("0");
    			}
            	orgInfoVo.setPId(ucOrg.getFdPsid());
            	orgInfoList.add(orgInfoVo);
    		}
        }else if(StrUtils.isEmpty(dataRangeName) && DataUtils.isNotEmpty(sidList)){
        	for (String sidStr : sidList) {
        		String[] split = new String[1];
            	for (UcOrg ucOrg : orgList) {
                	if(ucOrg.getFdSidTree().indexOf(sidStr) >= 0) {
                		PowerProjectInfoVo orgInfoVo = new PowerProjectInfoVo();
                		orgInfoVo.setId(ucOrg.getFdSid());
                    	orgInfoVo.setName(ucOrg.getFdName());
                    	orgInfoVo.setType(ucOrg.getFdType());
                    	if(ucOrg.getFdType().equals("ORG5-1")) {
                    		orgInfoVo.setIsProject("1");
                    	}else {
                    		orgInfoVo.setIsProject("0");
            			}
                    	orgInfoVo.setPId(ucOrg.getFdPsid());
                    	orgInfoList.add(orgInfoVo);
                    	split = ucOrg.getFdSidTree().split("/");
                	}
        		}
            	if(split != null) {
            		for (UcOrg ucOrg : orgList) {
                		for (int i = 1; i < split.length - 1; i++) {
    						if(ucOrg.getFdSid().equals(split[i])) {
    	        				PowerProjectInfoVo orgPowerInfoVo = new PowerProjectInfoVo();
    	        				orgPowerInfoVo.setId(ucOrg.getFdSid());
    	        				orgPowerInfoVo.setName(ucOrg.getFdName());
    	        				orgPowerInfoVo.setType(ucOrg.getFdType());
    	                    	if(ucOrg.getFdType().equals("ORG5-1")) {
    	                    		orgPowerInfoVo.setIsProject("1");
    	                    	}else {
    	                    		orgPowerInfoVo.setIsProject("0");
    	            			}
    	                    	orgPowerInfoVo.setPId(ucOrg.getFdPsid());
    	                    	orgInfoList.add(orgPowerInfoVo);
    	                	}
    					}
                	}
            	}
    		}
		}
		return orgInfoList;
	}
	
    /**
     * @Title: getUsreInfo 
     * @Description: 用户信息
     * @return JsonResult
     * @author liyongxu
     * @date 2019年5月27日 上午9:41:32 
    */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ApiOperation(value = "查询用户信息", notes = "查询用户信息")
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
    public JSONResult<UserInfoVo> getUserInfo(
    		@ApiParam(name = "userName", value = "用户名", required = false) @RequestParam(value = "userName", defaultValue = "") String userName) {
    	if(StrUtils.isEmpty(userName)) {
    		userName = getCurrentUser().getUsername();
    	}
    	JSONResult jsonResult = new JSONResult();
    	UcV1UserInfoResultVO usreInfoResult = iUcUserService.getUsreInfo(userName);
		UserInfoVo usreInfo = prepareUsreInfoRespVo(usreInfoResult);
		jsonResult.setData(usreInfo);
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
        return jsonResult;
    }

    /**
	 * @Title: prepareOrgInfoRespVo 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param orgList
	 * @return List<OrgInfoResultVO>
	 * @author liyongxu
	 * @date 2019年6月3日 下午7:23:22 
	*/
	private List<OrgInfoVO> prepareOrgInfoRespVo(List<UcV1OrgInfoResultVO> orgList) {
		List<OrgInfoVO> orgInfoResultList = new ArrayList<OrgInfoVO>();
		for (UcV1OrgInfoResultVO ucV1OrgInfoResultVO : orgList) {
			OrgInfoVO orgInfo = new OrgInfoVO();
			orgInfo.setFdSid(ucV1OrgInfoResultVO.getFdSid());
			orgInfo.setFdName(ucV1OrgInfoResultVO.getFdName());
			orgInfo.setFdTypeName(ucV1OrgInfoResultVO.getFdTypeName());
			orgInfo.setFdTypeSid(ucV1OrgInfoResultVO.getFdTypeSid());
			orgInfo.setFdTypeCode(ucV1OrgInfoResultVO.getFdTypeCode());
			orgInfo.setFdTypeBelong(ucV1OrgInfoResultVO.getFdTypeBelong());
			orgInfoResultList.add(orgInfo);
		}
		return orgInfoResultList;
	}

}
