package com.tahoecn.bo.controller.webapi;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
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
import com.tahoecn.bo.model.entity.MdmProjectInfo;
import com.tahoecn.bo.model.entity.UcOrg;
import com.tahoecn.bo.model.vo.DataPrivDetailVO;
import com.tahoecn.bo.model.vo.DataPrivVO;
import com.tahoecn.bo.model.vo.PowerProjectInfoVo;
import com.tahoecn.bo.model.vo.ProjectSubInfoVo;
import com.tahoecn.bo.service.BoSysPermissionService;
import com.tahoecn.bo.service.MdmProjectInfoService;
import com.tahoecn.bo.service.UcOrgService;
import com.tahoecn.core.json.JSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = "组织机构API", value = "组织机构API")
@RestController
@RequestMapping(value = "/api/org")
public class OrgInfoApi extends TahoeBaseController{

	@Autowired
	private UcOrgService ucOrgService;
	
	@Autowired
	private MdmProjectInfoService mdmProjectInfoService;
	
	@Autowired 
	private BoSysPermissionService boSysPermissionService;
	
	private static Logger logger = LogManager.getLogger(OrgInfoApi.class);
	
    /**
     * @Title: getOrgInfo 
     * @Description: 查询权限内项目数据
     * @return JSONResult
     * @author liyongxu
     * @date 2019年6月13日 上午11:43:55 
    */
    @ApiOperation(value = "查询权限内项目数据", notes = "查询权限内项目数据")
    @RequestMapping(value = "/getPowerProjectDataInfo", method = RequestMethod.POST)
    public JSONResult<List<PowerProjectInfoVo>> getPowerProjectDataInfo(HttpServletRequest request,
    		@ApiParam(name = "userName", value = "用户名", required = false) @RequestParam(value = "userName", defaultValue = "") String userName,
    	@ApiParam(name = "fdSid", value = "地产板块sid", required = false) @RequestParam(value = "fdSid", defaultValue = "") String fdSid) {
    	if(StrUtils.isEmpty(userName)) {
    		userName = getCurrentUser().getUsername();
    	}
		JSONResult<List<PowerProjectInfoVo>> jsonResult = new JSONResult<List<PowerProjectInfoVo>>();
		logger.info("######################## ----- START" + DateUtils.toStrDateFromUtilDateByFormat(new Date(), DateUtils.DATE_FULL_STR));
        List<UcOrg> orgList = ucOrgService.getPowerProjectDataInfo(fdSid);
        List<PowerProjectInfoVo> orgInfoList = this.recomPowerProjectData(userName,orgList);
        logger.info("######################## ----- END" + DateUtils.toStrDateFromUtilDateByFormat(new Date(), DateUtils.DATE_FULL_STR));
    	jsonResult.setData(orgInfoList);
        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
 		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
        return jsonResult;
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
	 * @Title: getProjectSubInfo 
	 * @Description: 查询项目下分期数据
	 * @param projectId
	 * @return JSONResult<List<ProjectSubInfoVo>>
	 * @author liyongxu
	 * @date 2019年6月13日 下午2:12:07 
	*/
    @ApiOperation(value = "查询项目下分期数据", notes = "查询项目下分期数据")
    @RequestMapping(value = "/getProjectSubInfo", method = RequestMethod.POST)
    public JSONResult<List<ProjectSubInfoVo>> getProjectSubInfo(@ApiParam(name = "projectId", value = "项目Id", required = true) @RequestParam(value = "projectId", required = false) String projectId) {
        JSONResult<List<ProjectSubInfoVo>> jsonResult = new JSONResult<List<ProjectSubInfoVo>>();
        List<ProjectSubInfoVo> projectSubInfoList = new ArrayList<ProjectSubInfoVo>();
        List<MdmProjectInfo> subInfoList = mdmProjectInfoService.getProjectSubInfo(projectId);
        MdmProjectInfo projectInfo = mdmProjectInfoService.getProjectInfo(projectId);
        for (MdmProjectInfo mdmProjectInfo : subInfoList) {
        	ProjectSubInfoVo projectSubInfo = new ProjectSubInfoVo();
        	projectSubInfo.setId(mdmProjectInfo.getSid());
        	projectSubInfo.setName(mdmProjectInfo.getName());
        	projectSubInfo.setPId(mdmProjectInfo.getParentSid());
        	projectSubInfo.setPname(projectInfo.getFullName()+"-"+mdmProjectInfo.getName());
        	projectSubInfoList.add(projectSubInfo);
		}
        jsonResult.setData(projectSubInfoList);
        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
        return jsonResult;
    }
	
}
