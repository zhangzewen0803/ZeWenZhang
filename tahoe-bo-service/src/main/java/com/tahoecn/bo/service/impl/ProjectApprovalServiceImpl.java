package com.tahoecn.bo.service.impl;

import com.tahoecn.bo.common.enums.CodeEnum;
import com.tahoecn.bo.common.enums.VersionStatusEnum;
import com.tahoecn.bo.common.utils.Constants;
import com.tahoecn.bo.mapper.BoApproveRecordMapper;
import com.tahoecn.bo.mapper.BoProjectExtendMapper;
import com.tahoecn.bo.mapper.BoProjectLandPartMapMapper;
import com.tahoecn.bo.mapper.MdmProjectInfoMapper;
import com.tahoecn.bo.model.bo.BpmBusinessInfoBo;
import com.tahoecn.bo.model.dto.ProjectAndExtendInfoDto;
import com.tahoecn.bo.model.dto.SubProjectAndExtendInfoDto;
import com.tahoecn.bo.model.entity.BoProjectLandPartMap;
import com.tahoecn.bo.service.BpmReviewService;
import com.tahoecn.bo.service.ProjectApprovalService;
import com.tahoecn.core.json.JSONResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
public class ProjectApprovalServiceImpl implements ProjectApprovalService {

    @Autowired
    private BpmReviewService reviewService;

    @Autowired
    private BoProjectExtendMapper projectExtendMapper;

    @Autowired
    private BoApproveRecordMapper approveRecordMapper;

    @Autowired
    private MdmProjectInfoMapper projectInfoMapper;

    @Autowired
    private BoProjectLandPartMapMapper projectLandPartMapMapper;

    @Override
    public JSONResult<String> projectValidateApproval(String extendId) throws Exception {
        JSONResult jsonResult = new JSONResult();

        ProjectAndExtendInfoDto projectAndExtendInfoDto = projectInfoMapper.selectProjectByExtendId(extendId);
        if(projectAndExtendInfoDto != null){
            String version = projectAndExtendInfoDto.getVersionName();
            if(StringUtils.isNotBlank(version)){
                Integer versionState = projectAndExtendInfoDto.getVersionStatus();
                if(versionState != null){
                    if(versionState.equals(VersionStatusEnum.CHECKING.getKey()) || versionState.equals(VersionStatusEnum.PASSED.getKey())){
                        jsonResult.setCode(CodeEnum.PROJECT_EXTENDS_VERSION.getKey());
                        jsonResult.setMsg(CodeEnum.PROJECT_EXTENDS_VERSION.getValue());
                    }else{
                        List<BoProjectLandPartMap> boProjectLandPartMaps = projectLandPartMapMapper.selectLandByExtendId(extendId);
                        if(boProjectLandPartMaps != null && boProjectLandPartMaps.size() > 0){
                            jsonResult.setCode(CodeEnum.SUCCESS.getKey());
                            jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
                        }else{
                            jsonResult.setCode(CodeEnum.PROJECT_APPROVAL_LAND_ERROR.getKey());
                            jsonResult.setMsg(CodeEnum.PROJECT_APPROVAL_LAND_ERROR.getValue());
                        }
                    }
                }else{
                    jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
                    jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
                }
            }else{
                jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
                jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
            }
        }else{
            jsonResult.setCode(CodeEnum.SUB_PROJECT_EXTENDS.getKey());
            jsonResult.setMsg(CodeEnum.SUB_PROJECT_EXTENDS.getValue());
        }

        return jsonResult;
    }

    @Override
    public JSONResult<String> subProjectValidateApproval(String extendId) throws Exception {
        JSONResult jsonResult = new JSONResult();

        SubProjectAndExtendInfoDto subProjectAndExtendInfoDto = projectInfoMapper.selectSubProjectByExtendId(extendId);
        if(subProjectAndExtendInfoDto != null){
            String version = subProjectAndExtendInfoDto.getVersionName();
            if(StringUtils.isNotBlank(version)){
                Integer versionState = subProjectAndExtendInfoDto.getVersionStatus();
                if(versionState != null){
                    if(versionState.equals(VersionStatusEnum.CHECKING.getKey()) || versionState.equals(VersionStatusEnum.PASSED.getKey())){
                        jsonResult.setCode(CodeEnum.PROJECT_EXTENDS_VERSION.getKey());
                        jsonResult.setMsg(CodeEnum.PROJECT_EXTENDS_VERSION.getValue());
                    }else{
                        List<BoProjectLandPartMap> boProjectLandPartMaps = projectLandPartMapMapper.selectLandByExtendId(extendId);
                        if(boProjectLandPartMaps != null && boProjectLandPartMaps.size() > 0){
                            jsonResult.setCode(CodeEnum.SUCCESS.getKey());
                            jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
                        }else{
                            jsonResult.setCode(CodeEnum.PROJECT_APPROVAL_LAND_ERROR.getKey());
                            jsonResult.setMsg(CodeEnum.PROJECT_APPROVAL_LAND_ERROR.getValue());
                        }
                    }
                }else{
                    jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
                    jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
                }
            }else{
                jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
                jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
            }
        }else{
            jsonResult.setCode(CodeEnum.PROJECT_EXTENDS.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_EXTENDS.getValue());
        }

        return jsonResult;
    }


    /**
     * 项目审批标题生成
     * @param extendId 唯一ID
     * @return BpmBusinessInfoBo
     * @throws Exception
     */
    @Override
    public BpmBusinessInfoBo projectCreatTitle(String extendId){
        BpmBusinessInfoBo businessInfoBo = new BpmBusinessInfoBo();

        ProjectAndExtendInfoDto projectAndExtendInfoDto = projectInfoMapper.selectProjectByExtendId(extendId);
        if(projectAndExtendInfoDto != null){
            String title = MessageFormat.format(Constants.APPROVAL_TITLE_ESTABLISH, projectAndExtendInfoDto.getProjectName()+"项目",
                        "", "项目指标库", StringUtils.isBlank(projectAndExtendInfoDto.getVersionName())?"":projectAndExtendInfoDto.getVersionName(),Constants.PROJECT_QUOTA);
            businessInfoBo.setVersionDesc(projectAndExtendInfoDto.getVersionName());
            businessInfoBo.setProjectId(projectAndExtendInfoDto.getProjectId());
            businessInfoBo.setDocSubject(title);
        }

        return businessInfoBo;
    }


    /**
     * 分期审批标题生成
     * @param extendId 唯一ID
     * @return BpmBusinessInfoBo
     * @throws Exception
     */
    @Override
    public BpmBusinessInfoBo subProjectCreatTitle(String extendId){
        BpmBusinessInfoBo businessInfoBo = new BpmBusinessInfoBo();

        SubProjectAndExtendInfoDto subProjectAndExtendInfoDto = projectInfoMapper.selectSubProjectByExtendId(extendId);
        if(subProjectAndExtendInfoDto != null){
            String title = MessageFormat.format(Constants.APPROVAL_TITLE_ESTABLISH, subProjectAndExtendInfoDto.getProjectName()+"项目",
                    subProjectAndExtendInfoDto.getSubProjectName(), "项目分期指标库", StringUtils.isBlank(subProjectAndExtendInfoDto.getVersionName())?"":subProjectAndExtendInfoDto.getVersionName(),Constants.PROJECT_QUOTA);
            businessInfoBo.setDocSubject(title);
            businessInfoBo.setVersionDesc(subProjectAndExtendInfoDto.getVersionName());
            businessInfoBo.setProjectId(subProjectAndExtendInfoDto.getSubProjectId());
        }

        return businessInfoBo;
    }

}
