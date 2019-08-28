package com.tahoecn.bo.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.enums.*;
import com.tahoecn.bo.common.utils.BusinessObjectUtils;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.common.utils.VersionUtils;
import com.tahoecn.bo.mapper.*;
import com.tahoecn.bo.model.bo.MdmProjectInfoBo;
import com.tahoecn.bo.model.bo.ProjectInfoBo;
import com.tahoecn.bo.model.bo.SubProjectInfoBo;
import com.tahoecn.bo.model.dto.*;
import com.tahoecn.bo.model.entity.*;
import com.tahoecn.bo.model.vo.*;
import com.tahoecn.bo.model.vo.reqvo.*;
import com.tahoecn.bo.service.*;
import com.tahoecn.core.json.JSONResult;
import com.tahoecn.uc.UcClient;
import com.tahoecn.uc.exception.UcException;
import com.tahoecn.uc.request.UcV1OrgListRequest;
import com.tahoecn.uc.response.UcV1OrgListResponse;
import com.tahoecn.uc.vo.UcV1OrgListResultVO;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 同步MDM-项目分期表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class MdmProjectInfoServiceImpl extends ServiceImpl<MdmProjectInfoMapper, MdmProjectInfo> implements MdmProjectInfoService {

    @Autowired
    private MdmProjectInfoMapper mdmProjectInfoMapper;

    @Autowired
    private BoProjectExtendMapper projectExtendMapper;

    @Autowired
    private BoProjectPartMapper projectPartMapper;

    @Autowired
    private BoProjectQuotaExtendMapper projectQuotaExtendMapper;

    @Autowired
    private BoProjectLandPartMapMapper projectLandPartMapMapper;

    @Autowired
    private BoProjectLandPartProductTypeMapMapper projectLandPartProductTypeMapMapper;

    @Autowired
    private BoProjectLandPartProductTypeQuotaMapMapper projectLandPartProductTypeQuotaMapMapper;

    @Autowired
    private TutouThLandinformationTbService tutouThLandinformationTbService;

    @Autowired
    private TutouThLandareaNewTbService tutouThLandareaNewTbService;

    @Autowired
    private BoQuotaGroupMapService boQuotaGroupMapService;

    @Autowired
    private BoProjectExtendService boProjectExtendService;

    @Autowired
    private BoProjectQuotaExtendService boProjectQuotaExtendService;

    @Autowired
    private BoProjectLandPartMapService boProjectLandPartMapService;

    @Autowired
    private BoProjectLandPartProductTypeMapService boProjectLandPartProductTypeMapService;

    @Autowired
    private BoProjectLandPartProductTypeQuotaMapService boProjectLandPartProductTypeQuotaMapService;

    @Autowired
    private BoProjectQuotaMapService boProjectQuotaMapService;

    @Autowired
    private BoLandPartProductTypeMapService boLandPartProductTypeMapService;

    @Autowired
    private BoLandPartProductTypeQuotaMapService boLandPartProductTypeQuotaMapService;

    @Autowired
    private BoProjectPartService projectPartService;

    @Autowired
    private BoApproveRecordMapper approveRecordMapper;

    @Autowired
    private UcUserMapper userMapper;


    @Override
    public ProjectInfoBo getProjectByProjectId(String projectId, String version) throws Exception {
        ProjectInfoBo projectInfoBo = new ProjectInfoBo();
       /* ProjectInfoDto projectInfoDto = mdmProjectInfoMapper.selectProjectByProjectId(projectId, version);
        if (projectInfoDto != null) {
            BeanUtils.copyProperties(projectInfoBo, projectInfoDto);
            List<String> pics = new ArrayList<String>();
            pics.add(projectInfoDto.getOverviewPicUrl());
            projectInfoBo.setPics(pics);
        }*/

        return projectInfoBo;
    }

    @Override
    public SubProjectInfoBo getProjectBySubProjectId(String subProjectId, String version) throws Exception {
        SubProjectInfoBo subProjectInfoBo = new SubProjectInfoBo();
        SubProjectInfoDto subProjectInfoDto = mdmProjectInfoMapper.selectProjectBySubProjectId(subProjectId, version);
        if (subProjectInfoDto != null) {
            BeanUtils.copyProperties(subProjectInfoBo, subProjectInfoDto);

            List<PartitionInfoVo> partitionInfos = projectPartMapper.selectByExtendId(subProjectInfoDto.getExtendId(), IsDeleteEnum.NO.getKey(), IsDisableEnum.NO.getKey());
            if (partitionInfos != null && partitionInfos.size() > 0) {
                subProjectInfoBo.setPartitionInfoBos(partitionInfos);
            } else {
                partitionInfos = new ArrayList<PartitionInfoVo>();
                subProjectInfoBo.setPartitionInfoBos(partitionInfos);
            }
        }

        return subProjectInfoBo;
    }

    @Override
    @Transient
    public JSONResult updateProjectInfo(ProjectUpdateInfoReqParam projectUpdateInfoReqParam, CurrentUserVO userVO, JSONResult jsonResult) {

        ProjectInfoDto projectInfoDto = projectExtendMapper.selectProjectByVersionId(projectUpdateInfoReqParam.getVersionId());

        if (projectInfoDto != null) {
            if (StringUtils.isNotBlank(projectInfoDto.getVersion())) {
                if (projectInfoDto.getVersionStatus().equals(VersionStatusEnum.CREATING.getKey())
                        || projectInfoDto.getVersionStatus().equals(VersionStatusEnum.REJECTED.getKey())) {
                    mdmProjectInfoMapper.updateProjectInfoById(projectUpdateInfoReqParam, projectInfoDto.getVersionId(), userVO);

                    jsonResult.setCode(CodeEnum.SUCCESS.getKey());
                    jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
                } else {
                    jsonResult.setCode(CodeEnum.PROJECT_EXTENDS_VERSION.getKey());
                    jsonResult.setMsg(CodeEnum.PROJECT_EXTENDS_VERSION.getValue());
                }
            } else {
                jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
                jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
                return jsonResult;
            }
        } else {
            jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
            return jsonResult;
        }

        return jsonResult;
    }

    @Override
    @Transactional
    public JSONResult updateSubProjectInfo(SubProjectUpdateInfoReqParam subProjectUpdateInfoReqParam, CurrentUserVO userVO, JSONResult jsonResult) throws Exception {

        //查询项目分期审批状态
        ProjectInfoDto projectInfoDto = projectExtendMapper.selectSubProjectByVersionId(subProjectUpdateInfoReqParam.getVersionId());

        if (projectInfoDto != null) {
            if (StringUtils.isNotBlank(projectInfoDto.getVersion())) {
                if (projectInfoDto.getVersionStatus().equals(VersionStatusEnum.CREATING.getKey())
                        || projectInfoDto.getVersionStatus().equals(VersionStatusEnum.REJECTED.getKey())) {

                    mdmProjectInfoMapper.updateSubProjectInfoById(subProjectUpdateInfoReqParam, subProjectUpdateInfoReqParam.getVersionId(), userVO);

                    List<PartitionInfoVo> partitionInfoVos = projectPartMapper.selectByExtendId(subProjectUpdateInfoReqParam.getVersionId(), IsDeleteEnum.NO.getKey(), IsDisableEnum.NO.getKey());
                    List<String> partitionAllIds = Optional.of(partitionInfoVos)
                            .orElseGet(ArrayList::new)
                            .stream().map(PartitionInfoVo::getPartitionId)
                            .collect(Collectors.toList());

                    List<PartitionInfoVo> partitions = subProjectUpdateInfoReqParam.getPartitions();
                    if (CollectionUtils.isNotEmpty(partitions)) {
                        List<String> partitionIds = Optional.of(partitions)
                                .orElseGet(ArrayList::new)
                                .stream().map(PartitionInfoVo::getPartitionId)
                                .collect(Collectors.toList());

                        if(CollectionUtils.isNotEmpty(partitionInfoVos)){
                            partitionAllIds.removeAll(partitionIds);
                        }

                        List<BoProjectPart> deleteProjectPartList = new ArrayList<BoProjectPart>();
                        if(CollectionUtils.isNotEmpty(partitionAllIds)){
                            for(String partitionId : partitionAllIds){
                                BoProjectPart projectPart = new BoProjectPart();
                                projectPart.setId(partitionId);
                                projectPart.setIsDelete(IsDeleteEnum.YES.getKey());
                                projectPart.setUpdaterId(userVO.getId());
                                projectPart.setUpdateTime(LocalDateTime.now());
                                projectPart.setUpdaterName(userVO.getName());
                                deleteProjectPartList.add(projectPart);
                            }
                            projectPartService.updateBatchById(deleteProjectPartList);
                        }

                        if(CollectionUtils.isNotEmpty(partitions)){
                            for (int i = 0; i < partitions.size(); i++) {
                                PartitionInfoVo partitionInfoVo = partitions.get(i);
                                if (partitionInfoVo != null) {
                                    String partitionId = partitionInfoVo.getPartitionId();
                                    BoProjectPart projectPart = new BoProjectPart();
                                    if (StringUtils.isNotBlank(partitionId)) {
                                        projectPart.setId(partitionId);
                                        projectPart.setName(partitionInfoVo.getPartitionName());
                                        projectPart.setCode(partitionInfoVo.getPartitionCode());
                                        projectPart.setUpdaterId(userVO.getId());
                                        projectPart.setUpdaterName(userVO.getUsername());
                                        projectPart.setUpdateTime(LocalDateTime.now());
                                        projectPart.setProjectExtendId(subProjectUpdateInfoReqParam.getVersionId());
                                        projectPartMapper.updateById(projectPart);
                                    } else {
                                        projectPart.setId(UUIDUtils.create());
                                        projectPart.setCode(partitionInfoVo.getPartitionCode());
                                        projectPart.setName(partitionInfoVo.getPartitionName());
                                        projectPart.setCreaterId(userVO.getId());
                                        projectPart.setCreaterName(userVO.getUsername());
                                        projectPart.setCreateTime(LocalDateTime.now());
                                        projectPart.setIsDelete(IsDeleteEnum.NO.getKey());
                                        projectPart.setIsDisable(IsDisableEnum.NO.getKey());
                                        projectPart.setProjectExtendId(subProjectUpdateInfoReqParam.getVersionId());
                                        projectPartMapper.insert(projectPart);
                                    }
                                }
                            }
                        }
                    }

                    jsonResult.setCode(CodeEnum.SUCCESS.getKey());
                    jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
                } else {
                    jsonResult.setCode(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getKey());
                    jsonResult.setMsg(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getValue());
                    return jsonResult;
                }
            } else {
                jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
                jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
                return jsonResult;
            }
        } else {
            jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
            return jsonResult;

        }

        return jsonResult;
    }

    private void insertOrUpdatePartInfo(SubProjectUpdateInfoReqParam subProjectUpdateInfoReqParam, SubProjectAndExendInfoDto subProjectAndExendInfoDto) {
        List<BoProjectPart> createParts = new ArrayList<BoProjectPart>();
        List<PartitionInfoVo> partitions = subProjectUpdateInfoReqParam.getPartitions();
        if (partitions != null && partitions.size() > 0) {
            for (int i = 0; i < partitions.size(); i++) {
                PartitionInfoVo partitionInfo = partitions.get(i);
                String partitionId = partitionInfo.getPartitionId();
                if (StringUtils.isNotBlank(partitionId)) {
                    BoProjectPart projectPart = new BoProjectPart();
                    projectPart.setId(partitionId);
                    projectPart.setCode(partitionInfo.getPartitionCode());
                    projectPart.setName(partitionInfo.getPartitionName());
                    projectPart.setUpdateTime(LocalDateTime.now());
                    projectPartMapper.updateById(projectPart);
                } else {
                    BoProjectPart projectPart = new BoProjectPart();
                    projectPart.setId(UUIDUtils.create());
                    projectPart.setCode(partitions.get(i).getPartitionCode());
                    projectPart.setName(partitions.get(i).getPartitionName());
                    projectPart.setProjectExtendId(subProjectAndExendInfoDto.getProjectExtendId());
                    projectPart.setIsDelete(IsDeleteEnum.NO.getKey());
                    projectPart.setIsDisable(IsDisableEnum.NO.getKey());
                    projectPart.setCreateTime(LocalDateTime.now());
                    createParts.add(projectPart);
                }
            }

            if (createParts != null && createParts.size() > 0) {
                projectPartMapper.insertBatch(createParts);
            }
        }
    }

    @Override
    public ProjectAndExendInfoDto selectProjectExtendIdByProjectId(String projectId) {
        return mdmProjectInfoMapper.selectProjectExtendIdByProjectId(projectId);
    }

    @Override
    public void deleteAll() {
        mdmProjectInfoMapper.deleteAll();
    }

    @Override
    @Transactional
    public void saveMdmProjectInfo(JSONArray jsonArray) {
        MdmProjectInfoBo mdmProjectInfoBo = new MdmProjectInfoBo();
        List<MdmProjectInfoBo> mdmProjectInfoList = new ArrayList<MdmProjectInfoBo>();
        System.out.println("#########" + jsonArray);
        //区域公司数据
        for (Object object : jsonArray) {
            JSONObject json = (JSONObject) object;
            mdmProjectInfoBo.setRegionId(json.getString("fdSid"));
            mdmProjectInfoBo.setRegionName(json.getString("fdPname"));
            //城市公司数据
            JSONArray estate = json.getJSONArray("estate");
            for (Object object2 : estate) {
                JSONObject estateJson = (JSONObject) object2;
                mdmProjectInfoBo.setCityCompanyId(estateJson.getString("fdSid"));
                mdmProjectInfoBo.setCityCompanyName(estateJson.getString("fdPname"));
                mdmProjectInfoList.addAll(this.splitProjectInfo(estateJson, mdmProjectInfoBo));
            }
        }
        mdmProjectInfoMapper.insertMdmProjectInfoList(mdmProjectInfoList);
    }

    /**
     * @param json                void
     * @param mdmProjectInfoBoOld
     * @Title: splitProjectInfo
     * @Description: 拆分项目分期数据
     * @author liyongxu
     * @date 2019年5月29日 下午8:16:54
     */
    public List<MdmProjectInfoBo> splitProjectInfo(JSONObject json, MdmProjectInfoBo mdmProjectInfoBoOld) {
        //项目json数据
        JSONArray project = json.getJSONArray("project");
        List<MdmProjectInfoBo> mdmProjectInfoList = new ArrayList<MdmProjectInfoBo>();
        for (Object object2 : project) {
            JSONObject projectJson = (JSONObject) object2;
            MdmProjectInfoBo mdmProjectInfoBo = new MdmProjectInfoBo();
            mdmProjectInfoBo.setParentSid(projectJson.getString("parentSid"));
            mdmProjectInfoBo.setSortId(projectJson.getString("sortId"));
            mdmProjectInfoBo.setSid(projectJson.getString("sid"));
            mdmProjectInfoBo.setLevelType(projectJson.getString("levelType"));
            mdmProjectInfoBo.setCode(projectJson.getString("code"));
//			mdmProjectInfoBo.setPrincipal(projectJson.getString("principal"));
            mdmProjectInfoBo.setFullName(projectJson.getString("fullName"));
            mdmProjectInfoBo.setRegionId(mdmProjectInfoBoOld.getRegionId());
            mdmProjectInfoBo.setRegionName(mdmProjectInfoBoOld.getRegionName());
            mdmProjectInfoBo.setCityCompanyId(mdmProjectInfoBoOld.getCityCompanyId());
            mdmProjectInfoBo.setCityCompanyName(mdmProjectInfoBoOld.getCityCompanyName());
            mdmProjectInfoList.add(mdmProjectInfoBo);

            //分期json数据
            JSONArray staging = projectJson.getJSONArray("staging");
            for (Object object3 : staging) {
                JSONObject stagingJson = (JSONObject) object3;
                MdmProjectInfoBo mdmStagingInfoBo = new MdmProjectInfoBo();
                mdmStagingInfoBo.setParentSid(stagingJson.getString("parentSid"));
                mdmStagingInfoBo.setSortId(stagingJson.getString("sortId"));
                mdmStagingInfoBo.setSid(stagingJson.getString("sid"));
                mdmStagingInfoBo.setLevelType(stagingJson.getString("levelType"));
                mdmStagingInfoBo.setCode(stagingJson.getString("code"));
//				mdmStagingInfoBo.setPrincipal(stagingJson.getString("principal"));
                mdmStagingInfoBo.setFullName(stagingJson.getString("f_Name"));
                mdmStagingInfoBo.setRegionId(mdmProjectInfoBoOld.getRegionId());
                mdmStagingInfoBo.setRegionName(mdmProjectInfoBoOld.getRegionName());
                mdmStagingInfoBo.setCityCompanyId(mdmProjectInfoBoOld.getCityCompanyId());
                mdmStagingInfoBo.setCityCompanyName(mdmProjectInfoBoOld.getCityCompanyName());
                mdmProjectInfoList.add(mdmStagingInfoBo);
            }
        }
        return mdmProjectInfoList;
    }

    @Override
    public void deleteMdmProjectInfo(String dataSid) {
//		mdmProjectInfoMapper.deleteBySid(dataSid);
    }

    @Override
    public void saveMdmProjectInfo(JSONObject result) {
        MdmProjectInfo mdmProjectInfo = new MdmProjectInfo();
        mdmProjectInfo.setParentSid(result.getString("parentSid"));
        mdmProjectInfo.setSortId(result.getInteger("sortId"));
        mdmProjectInfo.setSid(result.getString("sid"));
        mdmProjectInfo.setCode(result.getString("code"));
        mdmProjectInfo.setLevelType(result.getString("levelType"));
        if (mdmProjectInfo.getLevelType().equals("PROJECT")) {
            mdmProjectInfo.setFullName(result.getString("fullName"));
        } else {
            mdmProjectInfo.setFullName(result.getString("f_Name"));
        }
        mdmProjectInfoMapper.insert(mdmProjectInfo);
    }

    @Override
    public void updateMdmProjectInfo(JSONObject result) {
        MdmProjectInfo mdmProjectInfo = mdmProjectInfoMapper.selectBySid(result.getString("sid"));
        mdmProjectInfo.setParentSid(result.getString("parentSid"));
        mdmProjectInfo.setSortId(result.getInteger("sortId"));
        mdmProjectInfo.setSid(result.getString("sid"));
        mdmProjectInfo.setCode(result.getString("code"));
        mdmProjectInfo.setLevelType(result.getString("levelType"));
        if (mdmProjectInfo.getLevelType().equals("PROJECT")) {
            mdmProjectInfo.setFullName(result.getString("fullName"));
        } else {
            mdmProjectInfo.setFullName(result.getString("f_Name"));
        }
        mdmProjectInfoMapper.updateById(mdmProjectInfo);
    }

    @Override
    @Transactional
    public JSONResult updateProjectToNewVersInfo(ProjectUpdateVersReqParam projectUpdateVersReqParam, CurrentUserVO userVO, JSONResult jsonResult) {

        BoProjectExtend boProjectExtend = projectExtendMapper.selectByProjectId(projectUpdateVersReqParam.getProjectId(), null, null);
        if (boProjectExtend != null) {
            String version = boProjectExtend.getVersion();
            if (StringUtils.isNotBlank(version)) {
                Integer versionStatus = boProjectExtend.getVersionStatus();
                if (versionStatus != null && (versionStatus.equals(VersionStatusEnum.CREATING.getKey())
                        || versionStatus.equals(VersionStatusEnum.REJECTED.getKey()) || versionStatus.equals(VersionStatusEnum.CHECKING.getKey()))) {
                    jsonResult.setCode(CodeEnum.PROJECT_CREATING_EXCEPTION.getKey());
                    jsonResult.setMsg(CodeEnum.PROJECT_CREATING_EXCEPTION.getValue());
                    return jsonResult;
                } else {
                    String newExtId = UUIDUtils.create();
                    LocalDateTime now = LocalDateTime.now();

                    List<BoProjectLandPartMap> boProjectLandPartMaps = projectLandPartMapMapper.selectLastVersionLandByProExtId(projectUpdateVersReqParam.getProjectId());
                    if (boProjectLandPartMaps != null && boProjectLandPartMaps.size() > 0) {
                        for (int i = 0; i < boProjectLandPartMaps.size(); i++) {
                            BoProjectLandPartMap boProjectLandPartMap = boProjectLandPartMaps.get(i);
                            if (boProjectLandPartMap != null) {
                                boProjectLandPartMap.setId(UUIDUtils.create());
                                boProjectLandPartMap.setProjectExtendId(newExtId);
                                boProjectLandPartMap.setCreateTime(now);
                                boProjectLandPartMap.setCreaterId(userVO.getId());
                                boProjectLandPartMap.setCreaterName(userVO.getName());
                                boProjectLandPartMap.setIsDisable(IsDisableEnum.NO.getKey());
                                boProjectLandPartMap.setIsDelete(IsDeleteEnum.NO.getKey());
                                projectLandPartMapMapper.insert(boProjectLandPartMap);
                            }
                        }
                    }

                    String oldVersion = boProjectExtend.getVersion();
                    String newVsersion = VersionUtils.bigVersionInc(oldVersion);
                    boProjectExtend.setId(newExtId);
                    boProjectExtend.setVersion(newVsersion);
                    boProjectExtend.setVersionStatus(VersionStatusEnum.CREATING.getKey());
                    boProjectExtend.setCaseName(boProjectExtend.getCaseName());
                    boProjectExtend.setCreateTime(now);
                    boProjectExtend.setCreaterId(userVO.getId());
                    boProjectExtend.setCreaterName(userVO.getName());
                    boProjectExtend.setIsDisable(IsDisableEnum.NO.getKey());
                    boProjectExtend.setIsDelete(IsDeleteEnum.NO.getKey());
                    boProjectExtend.setPreId(oldVersion);
                    boProjectExtend.setGainStatusCode("obtainStatusG");
                    boProjectExtend.setApproveId(null);
                    boProjectExtend.setApproveStartTime(null);
                    boProjectExtend.setApproveEndTime(null);
                    boProjectExtend.setUpdaterId(null);
                    boProjectExtend.setUpdaterName(null);
                    boProjectExtend.setUpdateTime(null);
                    projectExtendMapper.insert(boProjectExtend);

                    jsonResult.setCode(CodeEnum.SUCCESS.getKey());
                    jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
                }
            } else {
                String newExtId = UUIDUtils.create();
                LocalDateTime now = LocalDateTime.now();

                List<BoProjectLandPartMap> boProjectLandPartMaps = projectLandPartMapMapper.selectLastVersionLandByProExtId(projectUpdateVersReqParam.getProjectId());
                if (boProjectLandPartMaps != null && boProjectLandPartMaps.size() > 0) {
                    for (int i = 0; i < boProjectLandPartMaps.size(); i++) {
                        BoProjectLandPartMap boProjectLandPartMap = boProjectLandPartMaps.get(i);
                        if (boProjectLandPartMap != null) {
                            boProjectLandPartMap.setId(UUIDUtils.create());
                            boProjectLandPartMap.setProjectExtendId(newExtId);
                            boProjectLandPartMap.setCreateTime(now);
                            boProjectLandPartMap.setCreaterId(userVO.getId());
                            boProjectLandPartMap.setCreaterName(userVO.getName());
                            boProjectLandPartMap.setIsDisable(IsDisableEnum.NO.getKey());
                            boProjectLandPartMap.setIsDelete(IsDeleteEnum.NO.getKey());
                            projectLandPartMapMapper.insert(boProjectLandPartMap);
                        }
                    }
                }

                String oldVersion = boProjectExtend.getVersion();
                String newVsersion = VersionUtils.bigVersionInc(oldVersion);
                boProjectExtend.setId(newExtId);
                boProjectExtend.setVersion(newVsersion);
                boProjectExtend.setVersionStatus(VersionStatusEnum.CREATING.getKey());
                boProjectExtend.setCaseName(boProjectExtend.getCaseName());
                boProjectExtend.setGainStatusCode("obtainStatusG");
                boProjectExtend.setCreateTime(now);
                boProjectExtend.setCreaterId(userVO.getId());
                boProjectExtend.setCreaterName(userVO.getName());
                boProjectExtend.setIsDisable(IsDisableEnum.NO.getKey());
                boProjectExtend.setIsDelete(IsDeleteEnum.NO.getKey());
                boProjectExtend.setPreId(oldVersion);
                boProjectExtend.setUpdaterId(null);
                boProjectExtend.setUpdaterName(null);
                boProjectExtend.setUpdateTime(null);
                projectExtendMapper.insert(boProjectExtend);

                jsonResult.setCode(CodeEnum.SUCCESS.getKey());
                jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
            }
        } else {
            MdmProjectInfo mdmProjectInfo = mdmProjectInfoMapper.selectById(projectUpdateVersReqParam.getProjectId());
            if (mdmProjectInfo != null) {
                BoProjectExtend projectExtend = new BoProjectExtend();
                projectExtend.setId(UUIDUtils.create());
                projectExtend.setProjectId(mdmProjectInfo.getSid());
                projectExtend.setProjectName(mdmProjectInfo.getFullName());
                projectExtend.setGainStatusCode("obtainStatusG");
                projectExtend.setVersionStatus(VersionStatusEnum.CREATING.getKey());
                projectExtend.setIsDelete(IsDeleteEnum.NO.getKey());
                projectExtend.setIsDisable(IsDisableEnum.NO.getKey());
                projectExtend.setCreateTime(LocalDateTime.now());
                projectExtend.setCreaterId(userVO.getId());
                projectExtend.setCreaterName(userVO.getName());
                projectExtend.setVersion(VersionUtils.bigVersionInc(null));
                projectExtendMapper.insert(projectExtend);

                jsonResult.setCode(CodeEnum.SUCCESS.getKey());
                jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
            } else {
                jsonResult.setCode(CodeEnum.PROJECT_EXTENDS.getKey());
                jsonResult.setMsg(CodeEnum.PROJECT_EXTENDS.getValue());
            }
        }

        return jsonResult;
    }

    @Override
    @Transactional
    public JSONResult deleteSubProjectPart(PartitionInfoVo partitionInfoVo, CurrentUserVO userVO, JSONResult jsonResult) throws Exception {

        BoProjectPart subProjectPart = projectPartMapper.selectById(partitionInfoVo.getPartitionId());
        if (subProjectPart != null) {
            Integer isDelete = subProjectPart.getIsDelete();
            Integer isDisable = subProjectPart.getIsDisable();
            if (isDelete != null && isDisable != null && subProjectPart.getProjectExtendId() != null
                    && isDisable.equals(IsDeleteEnum.NO.getKey()) && isDisable.equals(IsDisableEnum.NO.getKey())) {

                String projectExtendId = subProjectPart.getProjectExtendId();
                BoProjectExtend boProjectExtend = projectExtendMapper.selectById(projectExtendId);
                if (boProjectExtend != null) {
                    Integer versionStatus = boProjectExtend.getVersionStatus();
                    if (versionStatus != null && (versionStatus.equals(VersionStatusEnum.CHECKING.getKey()) || versionStatus.equals(VersionStatusEnum.PASSED.getKey()))) {
                        jsonResult.setCode(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getKey());
                        jsonResult.setMsg(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getValue());
                        return jsonResult;
                    }

                    BoProjectPart projectPart = new BoProjectPart();
                    projectPart.setId(partitionInfoVo.getPartitionId());
                    projectPart.setIsDelete(IsDeleteEnum.YES.getKey());
                    projectPart.setUpdaterId(userVO.getId());
                    projectPart.setUpdateTime(LocalDateTime.now());
                    projectPart.setUpdaterName(userVO.getName());
                    projectPartMapper.updateById(projectPart);
                } else {
                    jsonResult.setCode(CodeEnum.SUB_PROJECT_EXTENDS.getKey());
                    jsonResult.setMsg(CodeEnum.SUB_PROJECT_EXTENDS.getValue());
                    return jsonResult;
                }
            } else {
                jsonResult.setCode(CodeEnum.PROJECT_PART_DELETE.getKey());
                jsonResult.setMsg(CodeEnum.PROJECT_PART_DELETE.getValue());
                return jsonResult;
            }
        } else {
            jsonResult.setCode(CodeEnum.PROJECT_PART_CREAT.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_PART_CREAT.getValue());

            return jsonResult;
        }

        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());

        return jsonResult;
    }

    @Override
    @Transactional
    public JSONResult updateSubProjectToNewVersInfo(SubProjectUpdateVersReqParam subProjectUpdateVersReqParam, CurrentUserVO userVO, JSONResult jsonResult) throws Exception {
        BoProjectExtend boProjectExtend = projectExtendMapper.selectByProjectId(subProjectUpdateVersReqParam.getSubProjectId(), null, null);
        if (boProjectExtend != null) {
            String version = boProjectExtend.getVersion();
            if (StringUtils.isNotBlank(version)) {
                Integer versionStatus = boProjectExtend.getVersionStatus();
                if (versionStatus != null && (versionStatus.equals(VersionStatusEnum.CREATING.getKey())
                        || versionStatus.equals(VersionStatusEnum.REJECTED.getKey()) || versionStatus.equals(VersionStatusEnum.CHECKING.getKey()))) {
                    jsonResult.setCode(CodeEnum.PROJECT_CREATING_EXCEPTION.getKey());
                    jsonResult.setMsg(CodeEnum.PROJECT_CREATING_EXCEPTION.getValue());
                    return jsonResult;
                } else {
                    createNewVersionInfo(userVO, jsonResult, boProjectExtend);
                }
            } else {
                createNewVersionInfo(userVO, jsonResult, boProjectExtend);
            }
        } else {
            MdmProjectInfo mdmProjectInfo = mdmProjectInfoMapper.selectById(subProjectUpdateVersReqParam.getSubProjectId());
            if (mdmProjectInfo != null) {
                BoProjectExtend projectExtend = new BoProjectExtend();
                String extId = UUIDUtils.create();
                projectExtend.setId(extId);
                projectExtend.setProjectId(mdmProjectInfo.getSid());
                projectExtend.setProjectName(mdmProjectInfo.getName());
                projectExtend.setVersionStatus(VersionStatusEnum.CREATING.getKey());
                projectExtend.setGainStatusCode("obtainStatusG");
                projectExtend.setTaxTypeCode("taxTypeCodeY");
                projectExtend.setMergeTableTypeCode("mergeTableTypeCodeT");
                projectExtend.setTradeModeCode("tradeModeCodeM");
                projectExtend.setIsDelete(IsDeleteEnum.NO.getKey());
                projectExtend.setIsDisable(IsDisableEnum.NO.getKey());
                projectExtend.setCreateTime(LocalDateTime.now());
                projectExtend.setCreaterId(userVO.getId());
                projectExtend.setCreaterName(userVO.getName());
                projectExtend.setVersion(VersionUtils.bigVersionInc(null));
                projectExtendMapper.insert(projectExtend);
            } else {
                jsonResult.setCode(CodeEnum.SUB_PROJECT_EXTENDS.getKey());
                jsonResult.setMsg(CodeEnum.SUB_PROJECT_EXTENDS.getValue());
            }
            jsonResult.setCode(CodeEnum.SUCCESS.getKey());
            jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
        }
        return jsonResult;
    }

    private void createNewVersionInfo(CurrentUserVO userVO, JSONResult jsonResult, BoProjectExtend boProjectExtend) throws IllegalAccessException, InvocationTargetException {
        QueryWrapper<BoProjectLandPartMap> boProjectLandPartMapQueryWrapper = new QueryWrapper<BoProjectLandPartMap>();
        boProjectLandPartMapQueryWrapper.in("project_extend_id", boProjectExtend.getId())
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        List<BoProjectLandPartMap> boProjectLandPartMaps = projectLandPartMapMapper.selectList(boProjectLandPartMapQueryWrapper);
        List<String> boProjectLandPartMapIds = Optional.ofNullable(boProjectLandPartMaps)
                .orElseGet(ArrayList::new)
                .stream().map(BoProjectLandPartMap::getId)
                .collect(Collectors.toList());

        QueryWrapper<BoProjectLandPartProductTypeMap> boProjectLandPartProductTypeMapQueryWrapper = new QueryWrapper<BoProjectLandPartProductTypeMap>();
        boProjectLandPartProductTypeMapQueryWrapper.in("project_land_part_id", boProjectLandPartMapIds)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        List<BoProjectLandPartProductTypeMap> boProjectLandPartProductTypeMaps = projectLandPartProductTypeMapMapper.selectList(boProjectLandPartProductTypeMapQueryWrapper);
        List<String> boProjectLandPartProductTypeMapIds = Optional.ofNullable(boProjectLandPartProductTypeMaps)
                .orElseGet(ArrayList::new)
                .stream().map(BoProjectLandPartProductTypeMap::getId)
                .collect(Collectors.toList());

        QueryWrapper<BoProjectLandPartProductTypeQuotaMap> boProjectLandPartProductTypeQuotaMapQueryWrapper = new QueryWrapper<BoProjectLandPartProductTypeQuotaMap>();
        boProjectLandPartProductTypeQuotaMapQueryWrapper.in("project_land_part_product_type_id", boProjectLandPartProductTypeMapIds)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        List<BoProjectLandPartProductTypeQuotaMap> boProjectLandPartProductTypeQuotaMaps = projectLandPartProductTypeQuotaMapMapper.selectList(boProjectLandPartProductTypeQuotaMapQueryWrapper);

        String oldVersion = boProjectExtend.getVersion();
        String newVsersion = VersionUtils.bigVersionInc(oldVersion);
        String newExtId = UUIDUtils.create();
        boProjectExtend.setId(newExtId);
        boProjectExtend.setVersion(newVsersion);
        boProjectExtend.setProjectName(boProjectExtend.getProjectName());
        boProjectExtend.setVersionStatus(VersionStatusEnum.CREATING.getKey());
        boProjectExtend.setCreateTime(LocalDateTime.now());
        boProjectExtend.setCreaterId(userVO.getId());
        boProjectExtend.setCreaterName(userVO.getName());
        boProjectExtend.setIsDisable(IsDisableEnum.NO.getKey());
        boProjectExtend.setIsDelete(IsDeleteEnum.NO.getKey());
        boProjectExtend.setPreId(oldVersion);
        boProjectExtend.setGainStatusCode("obtainStatusG");
        boProjectExtend.setTaxTypeCode("taxTypeCodeY");
        boProjectExtend.setMergeTableTypeCode("mergeTableTypeCodeT");
        boProjectExtend.setTradeModeCode("tradeModeCodeM");
        boProjectExtend.setApproveId(null);
        boProjectExtend.setApproveStartTime(null);
        boProjectExtend.setApproveEndTime(null);
        boProjectExtend.setUpdaterId(null);
        boProjectExtend.setUpdaterName(null);
        boProjectExtend.setUpdateTime(null);
        projectExtendMapper.insert(boProjectExtend);

        List<PartitionInfoVo> partitionInfoVos = projectPartMapper.selectByExtendId(boProjectExtend.getId(), IsDeleteEnum.NO.getKey(), IsDisableEnum.NO.getKey());
        if (partitionInfoVos != null && partitionInfoVos.size() > 0) {
            for (int i = 0; i < partitionInfoVos.size(); i++) {
                PartitionInfoVo partitionInfoVo = partitionInfoVos.get(i);
                BoProjectPart projectPart = new BoProjectPart();
                projectPart.setId(UUIDUtils.create());
                projectPart.setCode(partitionInfoVo.getPartitionCode());
                projectPart.setName(partitionInfoVo.getPartitionName());
                projectPart.setIsDelete(IsDeleteEnum.NO.getKey());
                projectPart.setIsDisable(IsDisableEnum.NO.getKey());
                projectPart.setCreateTime(LocalDateTime.now());
                projectPart.setCreaterId(userVO.getId());
                projectPart.setCreaterName(userVO.getName());

                projectPartMapper.insert(projectPart);
            }
        }

        if (boProjectLandPartMaps != null && boProjectLandPartMaps.size() > 0) {
            for (int i = 0; i < boProjectLandPartMaps.size(); i++) {
                BoProjectLandPartMap boProjectLandPartMap = boProjectLandPartMaps.get(i);
                String oldProjectLandPartMapId = boProjectLandPartMap.getId();
                String newProjectLandPartMapId = UUIDUtils.create();

                BoProjectLandPartMap newProjectLandPartMap = new BoProjectLandPartMap();
                BeanUtils.copyProperties(newProjectLandPartMap, boProjectLandPartMap);
                newProjectLandPartMap.setId(newProjectLandPartMapId);
                newProjectLandPartMap.setProjectExtendId(newExtId);
                newProjectLandPartMap.setCreaterId(userVO.getId());
                newProjectLandPartMap.setCreaterName(userVO.getName());
                newProjectLandPartMap.setCreateTime(LocalDateTime.now());
                newProjectLandPartMap.setUpdaterId(null);
                newProjectLandPartMap.setUpdaterName(null);
                newProjectLandPartMap.setUpdateTime(null);
                projectLandPartMapMapper.insert(newProjectLandPartMap);

                if (boProjectLandPartProductTypeMaps != null && boProjectLandPartProductTypeMaps.size() > 0) {
                    for (int j = 0; j < boProjectLandPartProductTypeMaps.size(); j++) {
                        BoProjectLandPartProductTypeMap boProjectLandPartProductTypeMap = boProjectLandPartProductTypeMaps.get(j);
                        String oldProjectLandPartProductTypeMapId = boProjectLandPartProductTypeMap.getId();
                        String newProjectLandPartProductTypeMapId = UUIDUtils.create();

                        if (oldProjectLandPartMapId.equals(boProjectLandPartProductTypeMap.getProjectLandPartId())) {
                            BoProjectLandPartProductTypeMap newProjectLandPartProductTypeMap = new BoProjectLandPartProductTypeMap();
                            BeanUtils.copyProperties(newProjectLandPartProductTypeMap, boProjectLandPartProductTypeMap);
                            newProjectLandPartProductTypeMap.setId(newProjectLandPartProductTypeMapId);
                            newProjectLandPartProductTypeMap.setProjectLandPartId(newProjectLandPartMapId);
                            newProjectLandPartProductTypeMap.setUpdaterId(null);
                            newProjectLandPartProductTypeMap.setUpdaterName(null);
                            newProjectLandPartProductTypeMap.setUpdateTime(null);
                            newProjectLandPartProductTypeMap.setCreaterId(userVO.getId());
                            newProjectLandPartProductTypeMap.setCreaterName(userVO.getName());
                            newProjectLandPartProductTypeMap.setCreateTime(LocalDateTime.now());
                            projectLandPartProductTypeMapMapper.insert(newProjectLandPartProductTypeMap);
                        }

                        if (boProjectLandPartProductTypeQuotaMaps != null && boProjectLandPartProductTypeQuotaMaps.size() > 0) {
                            for (int k = 0; k < boProjectLandPartProductTypeQuotaMaps.size(); k++) {
                                BoProjectLandPartProductTypeQuotaMap boProjectLandPartProductTypeQuotaMap = boProjectLandPartProductTypeQuotaMaps.get(k);

                                if (oldProjectLandPartProductTypeMapId.equals(boProjectLandPartProductTypeQuotaMap.getProjectLandPartProductTypeId())) {
                                    BoProjectLandPartProductTypeQuotaMap newProjectLandPartProductTypeQuotaMap = new BoProjectLandPartProductTypeQuotaMap();
                                    BeanUtils.copyProperties(newProjectLandPartProductTypeQuotaMap, boProjectLandPartProductTypeQuotaMap);
                                    newProjectLandPartProductTypeQuotaMap.setId(UUIDUtils.create());
                                    newProjectLandPartProductTypeQuotaMap.setProjectLandPartProductTypeId(newProjectLandPartProductTypeMapId);
                                    newProjectLandPartProductTypeQuotaMap.setUpdaterId(null);
                                    newProjectLandPartProductTypeQuotaMap.setUpdaterName(null);
                                    newProjectLandPartProductTypeQuotaMap.setUpdateTime(null);
                                    newProjectLandPartProductTypeQuotaMap.setCreaterId(userVO.getId());
                                    newProjectLandPartProductTypeQuotaMap.setCreaterName(userVO.getName());
                                    newProjectLandPartProductTypeQuotaMap.setCreateTime(LocalDateTime.now());
                                    projectLandPartProductTypeQuotaMapMapper.insert(newProjectLandPartProductTypeQuotaMap);
                                }
                            }
                        }
                    }
                }
            }
        }

        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
    }

    @Override
    public JSONResult getProjectVersionInfo(String subProjectId, JSONResult jsonResult) throws Exception {
        VersionInfoVo versionInfoVo = new VersionInfoVo();
        List<VersionInfoVo.VersionDetailsInfoVo> versionDetailsInfoVoList = new ArrayList<VersionInfoVo.VersionDetailsInfoVo>();
        for (VersionStatusEnum versionStatusEnum : VersionStatusEnum.values()) {
            VersionInfoVo.VersionDetailsInfoVo versionDetailsInfoVo = new VersionInfoVo.VersionDetailsInfoVo();
            versionDetailsInfoVo.setVersionState(versionStatusEnum.getKey());
            versionDetailsInfoVo.setVersionAttr(versionStatusEnum.getValue());
            versionDetailsInfoVoList.add(versionDetailsInfoVo);
        }
        versionInfoVo.setVersionDetailsInfo(versionDetailsInfoVoList);

        String isNew = "0";
        List<ProjectAndExtendInfoDto> versions = projectExtendMapper.selectVersionInfoByProjectId(subProjectId);
        if (versions != null && versions.size() > 0) {
            ProjectAndExtendInfoDto projectAndExtendInfoDto = versions.get(0);

            Integer versionStatus = Integer.valueOf(projectAndExtendInfoDto.getVersionStatus());
            if (versionStatus != null && (versionStatus.equals(VersionStatusEnum.CREATING.getKey()) ||
                    (versionStatus.equals(VersionStatusEnum.CHECKING.getKey()) || (versionStatus.equals(VersionStatusEnum.REJECTED.getKey()))))) {
                isNew = "0";
                versionInfoVo.setIsNew(isNew);
            } else {
                isNew = "1";
                versionInfoVo.setIsNew(isNew);
            }
            versionInfoVo.setVersions(versions);
        } else {
            isNew = "1";
            versionInfoVo.setIsNew(isNew);
            versionInfoVo.setVersions(new ArrayList<ProjectAndExtendInfoDto>());
        }

        jsonResult.setData(versionInfoVo);
        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
        return jsonResult;
    }

    @Override
    public List<MdmProjectInfo> getProjectSubInfo(String projectId) {
        return mdmProjectInfoMapper.selectProjectSubInfo(projectId);
    }

    @Override
    public MdmProjectInfo getProjectInfo(String projectId) {
        return mdmProjectInfoMapper.selectById(projectId);
    }

    @Override
    @Transactional
    public JSONResult updateVersionState(ProjectUpdateVersReqParam projectUpdateVersReqParam, JSONResult jsonResult) {
        ProjectAndExtendInfoDto projectAndExtendInfoDto = projectExtendMapper.selectVersionByProjectId(projectUpdateVersReqParam);
        jsonResult.setCode(CodeEnum.ERROR.getKey());
        jsonResult.setMsg(CodeEnum.ERROR.getValue());

        if (projectAndExtendInfoDto != null) {
            String version = projectAndExtendInfoDto.getVersionName();
            if (StringUtils.isNotBlank(version)) {
                String extendId = projectAndExtendInfoDto.getVersionId();
                if (StringUtils.isNotBlank(extendId)) {
                    BoProjectExtend projectExtend = new BoProjectExtend();
                    projectExtend.setId(extendId);
                    projectExtend.setVersionStatus(VersionStatusEnum.PASSED.getKey());
                    projectExtendMapper.updateById(projectExtend);

                    jsonResult.setCode(CodeEnum.SUCCESS.getKey());
                    jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
                } else {
                    jsonResult.setCode(CodeEnum.PROJECT_EXTENDS.getKey());
                    jsonResult.setMsg(CodeEnum.PROJECT_EXTENDS.getValue());
                }
            } else {
                jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
                jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
            }
        }

        return jsonResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void makeHistoryInit(CurrentUserVO currentUser) {
        List<MdmProjectInfo> list = list();
        UcV1OrgListRequest request = new UcV1OrgListRequest();
        request.setOrgType("ORG3,ORG4,ORG4-1,ORG5-1,ORG5-2");
        request.setReturnType(2);
        try {
            UcV1OrgListResponse ucV1OrgListResponse = UcClient.v1OrgList(request);
            Map<String, UcV1OrgListResultVO> ucOrgMap = new ConcurrentHashMap<>();
            List<UcV1OrgListResultVO> result = ucV1OrgListResponse.getResult();
            result.parallelStream().forEach(x -> ucOrgMap.put(x.getFdSid(), x));


            LocalDateTime now = LocalDateTime.now();
            List<BoProjectExtend> boProjectExtendList = new ArrayList<>();
            List<TutouThLandinformationTb> tutouThLandinformationTbList = new ArrayList<>();
            List<TutouThLandareaNewTb> tutouThLandareaNewTbList = new ArrayList<>();
            List<BoProjectLandPartMap> projectLandPartMapList = new ArrayList<>();
            List<BoProjectQuotaExtend> boProjectQuotaExtendList = new ArrayList<>();
            List<BoProjectQuotaMap> boProjectQuotaMapList = new ArrayList<>();
            List<BoLandPartProductTypeMap> boLandPartProductTypeMapList = new ArrayList<>();
            List<BoLandPartProductTypeQuotaMap> boLandPartProductTypeQuotaMapList = new ArrayList<>();
            List<BoProjectLandPartProductTypeMap> boProjectLandPartProductTypeMapList = new ArrayList<>();
            List<BoProjectLandPartProductTypeQuotaMap> boProjectLandPartProductTypeQuotaMapList = new ArrayList<>();
            Map<String, TutouThLandinformationTb> projectIdMapLand = new HashMap<>();
            AtomicInteger landId = new AtomicInteger(20190000);

            //项目面积规划指标组
            List<BoQuotaGroupMap> quotaGroupMapList = boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.PROJECT_AREA_PLAN.getKey());
            //地块业态指标组
            List<BoQuotaGroupMap> landQuotaGroupList = boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.LAND_PART_PRODUCT_TYPE.getKey());


            //先项目遍历
            list.stream().forEach(x -> {
                if (LevelTypeEnum.PROJECT.getKey().equals(x.getLevelType())) {
                    landId.incrementAndGet();
                    //项目
                    BoProjectExtend boProjectExtend = new BoProjectExtend();
                    boProjectExtendList.add(boProjectExtend);
                    boProjectExtend.setId(UUIDUtils.create());
                    boProjectExtend.setVersionStatus(VersionStatusEnum.PASSED.getKey());
                    boProjectExtend.setVersion("V1.0");
                    boProjectExtend.setApproveEndTime(now);
                    boProjectExtend.setApproveStartTime(now);
                    boProjectExtend.setCreaterId(currentUser.getId());
                    boProjectExtend.setCreateTime(now);
                    boProjectExtend.setCreaterName(currentUser.getUsername());
                    boProjectExtend.setProjectId(x.getSid());
                    boProjectExtend.setProjectName(x.getFullName());

                    //地块
                    TutouThLandinformationTb tutouThLandinformationTb = new TutouThLandinformationTb();
                    tutouThLandinformationTbList.add(tutouThLandinformationTb);
                    tutouThLandinformationTb.setId(UUIDUtils.create());
                    tutouThLandinformationTb.setLandId(landId.get());
                    tutouThLandinformationTb.setProjectName("VT" + landId.get());
                    tutouThLandinformationTb.setStateId(19);
                    tutouThLandinformationTb.setFollowTime(now);
                    tutouThLandinformationTb.setCreateTime(now);
                    UcV1OrgListResultVO city = ucOrgMap.get(x.getParentSid());
                    if (city != null) {
                        tutouThLandinformationTb.setCityCode(city.getFdCode());
                        tutouThLandinformationTb.setCityName(city.getFdName());
                        UcV1OrgListResultVO region = ucOrgMap.get(city.getFdPsid());
                        if (region != null) {
                            tutouThLandinformationTb.setRegionCode(region.getFdCode());
                            tutouThLandinformationTb.setRegionName(region.getFdName());
                        }
                    }

                    //地块 - new
                    TutouThLandareaNewTb tutouThLandareaNewTb = new TutouThLandareaNewTb();
                    tutouThLandareaNewTb.setCreateTime(now);
                    tutouThLandareaNewTb.setLandId(landId.get());
                    tutouThLandareaNewTb.setId(UUIDUtils.create());
                    tutouThLandareaNewTb.setSourceId(landId.get());
                    tutouThLandareaNewTb.setLandNum(tutouThLandinformationTb.getProjectName());
                    tutouThLandareaNewTb.setConfirmStatus(2);
                    tutouThLandareaNewTb.setConfirmDate(now);
                    tutouThLandareaNewTbList.add(tutouThLandareaNewTb);


                    //项目地块关系
                    BoProjectLandPartMap boProjectLandPartMap = new BoProjectLandPartMap();
                    boProjectLandPartMap.setId(UUIDUtils.create());
                    boProjectLandPartMap.setCreateTime(now);
                    boProjectLandPartMap.setCreaterId(currentUser.getId());
                    boProjectLandPartMap.setCreateTime(now);
                    boProjectLandPartMap.setCreaterName(currentUser.getUsername());
                    boProjectLandPartMap.setLandPartId(tutouThLandinformationTb.getId());
                    boProjectLandPartMap.setLandPartName(tutouThLandinformationTb.getProjectName());
                    boProjectLandPartMap.setProjectExtendId(boProjectExtend.getId());
                    projectLandPartMapList.add(boProjectLandPartMap);
                    projectIdMapLand.put(boProjectExtend.getProjectId(), tutouThLandinformationTb);

                    //投决会面积版本
                    BoProjectQuotaExtend boProjectQuotaExtend = new BoProjectQuotaExtend();
                    boProjectQuotaExtend.setId(UUIDUtils.create());
                    boProjectQuotaExtend.setVersion("V1.0");
                    boProjectQuotaExtend.setStageName(StageCodeEnum.STAGE_01.getValue());
                    boProjectQuotaExtend.setStageCode(StageCodeEnum.STAGE_01.getKey());
                    boProjectQuotaExtend.setVersionStatus(VersionStatusEnum.PASSED.getKey());
                    boProjectQuotaExtend.setProjectId(x.getSid());
                    boProjectQuotaExtend.setApproveEndTime(now);
                    boProjectQuotaExtend.setApproveStartTime(now);
                    boProjectQuotaExtend.setCreaterId(currentUser.getId());
                    boProjectQuotaExtend.setCreaterName(currentUser.getUsername());
                    boProjectQuotaExtend.setCreateTime(now);
                    boProjectQuotaExtend.setQuotaGroupCode(QuotaGroupCodeEnum.PROJECT_AREA_PLAN.getKey());
                    boProjectQuotaExtendList.add(boProjectQuotaExtend);

                    //投决会面积规划指标
                    List<BoProjectQuotaMap> projectQuotaMapList = quotaGroupMapList.stream().map(y -> {
                        BoProjectQuotaMap boProjectQuotaMap = new BoProjectQuotaMap();
                        BusinessObjectUtils.quotaValueSetDefaultForObject(y.getValueType(), boProjectQuotaMap);
                        boProjectQuotaMap.setCreaterName(currentUser.getUsername());
                        boProjectQuotaMap.setCreaterId(currentUser.getId());
                        boProjectQuotaMap.setCreateTime(now);
                        boProjectQuotaMap.setId(UUIDUtils.create());
                        boProjectQuotaMap.setProjectQuotaExtendId(boProjectQuotaExtend.getId());
                        boProjectQuotaMap.setQuotaCode(y.getCode());
                        boProjectQuotaMap.setQuotaGroupMapId(y.getId());
                        boProjectQuotaMap.setQuotaId(y.getQuotaId());
                        return boProjectQuotaMap;
                    }).collect(Collectors.toList());
                    boProjectQuotaMapList.addAll(projectQuotaMapList);

                    //投决会地块业态
                    BoLandPartProductTypeMap boLandPartProductTypeMap = new BoLandPartProductTypeMap();
                    boLandPartProductTypeMap.setId(UUIDUtils.create());
                    boLandPartProductTypeMap.setProjectQuotaExtendId(boProjectQuotaExtend.getId());
                    boLandPartProductTypeMap.setCreaterId(currentUser.getId());
                    boLandPartProductTypeMap.setCreaterName(currentUser.getUsername());
                    boLandPartProductTypeMap.setCreateTime(now);
                    boLandPartProductTypeMap.setLandPartId(tutouThLandinformationTb.getId());
                    boLandPartProductTypeMap.setLandPartName(tutouThLandinformationTb.getProjectName());
                    boLandPartProductTypeMap.setProductTypeCode(ProductTypeEnum.LITTLE_HIGH_FLOOR.getKey());
                    boLandPartProductTypeMap.setProductTypeName(ProductTypeEnum.LITTLE_HIGH_FLOOR.getValue());
                    boLandPartProductTypeMapList.add(boLandPartProductTypeMap);

                    //投决会地块业态指标
                    List<BoLandPartProductTypeQuotaMap> landQuotaList = landQuotaGroupList.stream().map(y -> {
                        BoLandPartProductTypeQuotaMap boLandPartProductTypeQuotaMap = new BoLandPartProductTypeQuotaMap();
                        BusinessObjectUtils.quotaValueSetDefaultForObject(y.getValueType(), boLandPartProductTypeQuotaMap);
                        if (QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(y.getCode())) {
                            //有产权
                            boLandPartProductTypeQuotaMap.setQuotaValue("1001");
                        }
                        boLandPartProductTypeQuotaMap.setCreaterName(currentUser.getUsername());
                        boLandPartProductTypeQuotaMap.setCreaterId(currentUser.getId());
                        boLandPartProductTypeQuotaMap.setCreateTime(now);
                        boLandPartProductTypeQuotaMap.setId(UUIDUtils.create());
                        boLandPartProductTypeQuotaMap.setLandPartProductTypeId(boLandPartProductTypeMap.getId());
                        boLandPartProductTypeQuotaMap.setQuotaCode(y.getCode());
                        boLandPartProductTypeQuotaMap.setQuotaGroupMapId(y.getId());
                        boLandPartProductTypeQuotaMap.setQuotaId(y.getQuotaId());
                        return boLandPartProductTypeQuotaMap;
                    }).collect(Collectors.toList());
                    boLandPartProductTypeQuotaMapList.addAll(landQuotaList);

                    //项目地块业态
                    BoProjectLandPartProductTypeMap boProjectLandPartProductTypeMap = new BoProjectLandPartProductTypeMap();
                    boProjectLandPartProductTypeMap.setId(UUIDUtils.create());
                    boProjectLandPartProductTypeMap.setProjectLandPartId(boProjectLandPartMap.getId());
                    boProjectLandPartProductTypeMap.setCreaterId(currentUser.getId());
                    boProjectLandPartProductTypeMap.setCreaterName(currentUser.getUsername());
                    boProjectLandPartProductTypeMap.setCreateTime(now);
                    boProjectLandPartProductTypeMap.setPropertyRightAttr("1001");
                    boProjectLandPartProductTypeMap.setProductTypeCode(ProductTypeEnum.LITTLE_HIGH_FLOOR.getKey());
                    boProjectLandPartProductTypeMap.setProductTypeName(ProductTypeEnum.LITTLE_HIGH_FLOOR.getValue());
                    boProjectLandPartProductTypeMapList.add(boProjectLandPartProductTypeMap);

                    //项目地块业态指标
                    List<BoProjectLandPartProductTypeQuotaMap> projectLandQuotaList = landQuotaList.stream().map(y -> {
                        BoProjectLandPartProductTypeQuotaMap boProjectLandPartProductTypeQuotaMap = new BoProjectLandPartProductTypeQuotaMap();
                        boProjectLandPartProductTypeQuotaMap.setQuotaValue(y.getQuotaValue());
                        boProjectLandPartProductTypeQuotaMap.setCreaterName(currentUser.getUsername());
                        boProjectLandPartProductTypeQuotaMap.setCreaterId(currentUser.getId());
                        boProjectLandPartProductTypeQuotaMap.setCreateTime(now);
                        boProjectLandPartProductTypeQuotaMap.setId(UUIDUtils.create());
                        boProjectLandPartProductTypeQuotaMap.setProjectLandPartProductTypeId(boProjectLandPartProductTypeMap.getId());
                        boProjectLandPartProductTypeQuotaMap.setQuotaCode(y.getQuotaCode());
                        boProjectLandPartProductTypeQuotaMap.setQuotaGroupMapId(y.getId());
                        boProjectLandPartProductTypeQuotaMap.setQuotaId(y.getQuotaId());
                        return boProjectLandPartProductTypeQuotaMap;
                    }).collect(Collectors.toList());
                    boProjectLandPartProductTypeQuotaMapList.addAll(projectLandQuotaList);
                }
            });

            //再遍历分期
            list.stream().forEach(x -> {
                if (LevelTypeEnum.PROJECT_SUB.getKey().equals(x.getLevelType())) {
                    //分期
                    BoProjectExtend boProjectExtend = new BoProjectExtend();
                    boProjectExtendList.add(boProjectExtend);
                    boProjectExtend.setId(UUIDUtils.create());
                    boProjectExtend.setVersionStatus(VersionStatusEnum.PASSED.getKey());
                    boProjectExtend.setVersion("V1.0");
                    boProjectExtend.setApproveEndTime(now);
                    boProjectExtend.setApproveStartTime(now);
                    boProjectExtend.setCreaterId(currentUser.getId());
                    boProjectExtend.setCreateTime(now);
                    boProjectExtend.setCreaterName(currentUser.getUsername());
                    boProjectExtend.setProjectId(x.getSid());
                    boProjectExtend.setProjectName(x.getName());

                    //分期地块关系
                    BoProjectLandPartMap boProjectLandPartMap = new BoProjectLandPartMap();
                    boProjectLandPartMap.setId(UUIDUtils.create());
                    boProjectLandPartMap.setCreateTime(now);
                    boProjectLandPartMap.setCreaterId(currentUser.getId());
                    boProjectLandPartMap.setCreateTime(now);
                    boProjectLandPartMap.setCreaterName(currentUser.getUsername());
                    TutouThLandinformationTb tutouThLandinformationTb = projectIdMapLand.get(x.getParentSid());
                    boProjectLandPartMap.setLandPartId(tutouThLandinformationTb.getId());
                    boProjectLandPartMap.setLandPartName(tutouThLandinformationTb.getProjectName());
                    boProjectLandPartMap.setProjectExtendId(boProjectExtend.getId());
                    projectLandPartMapList.add(boProjectLandPartMap);

                    //分期地块业态
                    BoProjectLandPartProductTypeMap boProjectLandPartProductTypeMap = new BoProjectLandPartProductTypeMap();
                    boProjectLandPartProductTypeMap.setId(UUIDUtils.create());
                    boProjectLandPartProductTypeMap.setProjectLandPartId(boProjectLandPartMap.getId());
                    boProjectLandPartProductTypeMap.setCreaterId(currentUser.getId());
                    boProjectLandPartProductTypeMap.setCreaterName(currentUser.getUsername());
                    boProjectLandPartProductTypeMap.setCreateTime(now);
                    boProjectLandPartProductTypeMap.setPropertyRightAttr("1001");
                    boProjectLandPartProductTypeMap.setProductTypeCode(ProductTypeEnum.LITTLE_HIGH_FLOOR.getKey());
                    boProjectLandPartProductTypeMap.setProductTypeName(ProductTypeEnum.LITTLE_HIGH_FLOOR.getValue());
                    boProjectLandPartProductTypeMapList.add(boProjectLandPartProductTypeMap);

                    //分期地块业态指标
                    List<BoProjectLandPartProductTypeQuotaMap> landQuotaList = landQuotaGroupList.stream().map(y -> {
                        BoProjectLandPartProductTypeQuotaMap boLandPartProductTypeQuotaMap = new BoProjectLandPartProductTypeQuotaMap();
                        BusinessObjectUtils.quotaValueSetDefaultForObject(y.getValueType(), boLandPartProductTypeQuotaMap);
                        if (QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(y.getCode())) {
                            //有产权
                            boLandPartProductTypeQuotaMap.setQuotaValue("1001");
                        }
                        boLandPartProductTypeQuotaMap.setCreaterName(currentUser.getUsername());
                        boLandPartProductTypeQuotaMap.setCreaterId(currentUser.getId());
                        boLandPartProductTypeQuotaMap.setCreateTime(now);
                        boLandPartProductTypeQuotaMap.setId(UUIDUtils.create());
                        boLandPartProductTypeQuotaMap.setProjectLandPartProductTypeId(boProjectLandPartProductTypeMap.getId());
                        boLandPartProductTypeQuotaMap.setQuotaCode(y.getCode());
                        boLandPartProductTypeQuotaMap.setQuotaGroupMapId(y.getId());
                        boLandPartProductTypeQuotaMap.setQuotaId(y.getQuotaId());
                        return boLandPartProductTypeQuotaMap;
                    }).collect(Collectors.toList());
                    boProjectLandPartProductTypeQuotaMapList.addAll(landQuotaList);
                }
            });

            boProjectExtendService.saveBatch(boProjectExtendList, 100);
            tutouThLandinformationTbService.saveBatch(tutouThLandinformationTbList, 100);
            tutouThLandareaNewTbService.saveBatch(tutouThLandareaNewTbList, 100);
            boProjectLandPartMapService.saveBatch(projectLandPartMapList, 100);
            boProjectQuotaExtendService.saveBatch(boProjectQuotaExtendList, 100);
            boProjectQuotaMapService.saveBatch(boProjectQuotaMapList, 100);
            boLandPartProductTypeMapService.saveBatch(boLandPartProductTypeMapList, 100);
            boLandPartProductTypeQuotaMapService.saveBatch(boLandPartProductTypeQuotaMapList, 100);
            boProjectLandPartProductTypeMapService.saveBatch(boProjectLandPartProductTypeMapList, 100);
            boProjectLandPartProductTypeQuotaMapService.saveBatch(boProjectLandPartProductTypeQuotaMapList, 100);
        } catch (UcException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void makeHistoryInit(CurrentUserVO currentUser, String projectId, Integer landPartStartNo) {
        String[] projectIds = projectId.split(",");
        QueryWrapper<MdmProjectInfo> p = new QueryWrapper<>();
        p.in("sid", projectIds).or().in("parent_sid",projectIds);
        List<MdmProjectInfo> projectInfoList = list(p);
        UcV1OrgListRequest request = new UcV1OrgListRequest();
        request.setOrgType("ORG3,ORG4,ORG4-1,ORG5-1,ORG5-2");
        request.setReturnType(2);
        try {
            UcV1OrgListResponse ucV1OrgListResponse = UcClient.v1OrgList(request);
            Map<String, UcV1OrgListResultVO> ucOrgMap = new ConcurrentHashMap<>();
            List<UcV1OrgListResultVO> result = ucV1OrgListResponse.getResult();
            result.parallelStream().forEach(x -> ucOrgMap.put(x.getFdSid(), x));


            LocalDateTime now = LocalDateTime.now();
            List<BoProjectExtend> boProjectExtendList = new ArrayList<>();
            List<TutouThLandinformationTb> tutouThLandinformationTbList = new ArrayList<>();
            List<TutouThLandareaNewTb> tutouThLandareaNewTbList = new ArrayList<>();
            List<BoProjectLandPartMap> projectLandPartMapList = new ArrayList<>();
            List<BoProjectQuotaExtend> boProjectQuotaExtendList = new ArrayList<>();
            List<BoProjectQuotaMap> boProjectQuotaMapList = new ArrayList<>();
            List<BoLandPartProductTypeMap> boLandPartProductTypeMapList = new ArrayList<>();
            List<BoLandPartProductTypeQuotaMap> boLandPartProductTypeQuotaMapList = new ArrayList<>();
            List<BoProjectLandPartProductTypeMap> boProjectLandPartProductTypeMapList = new ArrayList<>();
            List<BoProjectLandPartProductTypeQuotaMap> boProjectLandPartProductTypeQuotaMapList = new ArrayList<>();
            Map<String, TutouThLandinformationTb> projectIdMapLand = new HashMap<>();
            AtomicInteger landId = new AtomicInteger(landPartStartNo);

            //项目面积规划指标组
            List<BoQuotaGroupMap> quotaGroupMapList = boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.PROJECT_AREA_PLAN.getKey());
            //地块业态指标组
            List<BoQuotaGroupMap> landQuotaGroupList = boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.LAND_PART_PRODUCT_TYPE.getKey());


            //先项目遍历
            projectInfoList.stream().forEach(x -> {
                if (LevelTypeEnum.PROJECT.getKey().equals(x.getLevelType())) {
                    landId.incrementAndGet();
                    //项目
                    BoProjectExtend boProjectExtend = new BoProjectExtend();
                    boProjectExtendList.add(boProjectExtend);
                    boProjectExtend.setId(UUIDUtils.create());
                    boProjectExtend.setVersionStatus(VersionStatusEnum.PASSED.getKey());
                    boProjectExtend.setVersion("V1.0");
                    boProjectExtend.setApproveEndTime(now);
                    boProjectExtend.setApproveStartTime(now);
                    boProjectExtend.setCreaterId(currentUser.getId());
                    boProjectExtend.setCreateTime(now);
                    boProjectExtend.setCreaterName(currentUser.getUsername());
                    boProjectExtend.setProjectId(x.getSid());
                    boProjectExtend.setProjectName(x.getFullName());

                    //地块
                    TutouThLandinformationTb tutouThLandinformationTb = new TutouThLandinformationTb();
                    tutouThLandinformationTbList.add(tutouThLandinformationTb);
                    tutouThLandinformationTb.setId(UUIDUtils.create());
                    tutouThLandinformationTb.setLandId(landId.get());
                    tutouThLandinformationTb.setProjectName("VT" + landId.get());
                    tutouThLandinformationTb.setStateId(19);
                    tutouThLandinformationTb.setFollowTime(now);
                    tutouThLandinformationTb.setCreateTime(now);
                    UcV1OrgListResultVO city = ucOrgMap.get(x.getParentSid());
                    if (city != null) {
                        tutouThLandinformationTb.setCityCode(city.getFdCode());
                        tutouThLandinformationTb.setCityName(city.getFdName());
                        UcV1OrgListResultVO region = ucOrgMap.get(city.getFdPsid());
                        if (region != null) {
                            tutouThLandinformationTb.setRegionCode(region.getFdCode());
                            tutouThLandinformationTb.setRegionName(region.getFdName());
                        }
                    }

                    //地块 - new
                    TutouThLandareaNewTb tutouThLandareaNewTb = new TutouThLandareaNewTb();
                    tutouThLandareaNewTb.setCreateTime(now);
                    tutouThLandareaNewTb.setLandId(landId.get());
                    tutouThLandareaNewTb.setId(UUIDUtils.create());
                    tutouThLandareaNewTb.setSourceId(landId.get());
                    tutouThLandareaNewTb.setLandNum(tutouThLandinformationTb.getProjectName());
                    tutouThLandareaNewTb.setConfirmStatus(2);
                    tutouThLandareaNewTb.setConfirmDate(now);
                    tutouThLandareaNewTbList.add(tutouThLandareaNewTb);


                    //项目地块关系
                    BoProjectLandPartMap boProjectLandPartMap = new BoProjectLandPartMap();
                    boProjectLandPartMap.setId(UUIDUtils.create());
                    boProjectLandPartMap.setCreateTime(now);
                    boProjectLandPartMap.setCreaterId(currentUser.getId());
                    boProjectLandPartMap.setCreateTime(now);
                    boProjectLandPartMap.setCreaterName(currentUser.getUsername());
                    boProjectLandPartMap.setLandPartId(tutouThLandinformationTb.getId());
                    boProjectLandPartMap.setLandPartName(tutouThLandinformationTb.getProjectName());
                    boProjectLandPartMap.setProjectExtendId(boProjectExtend.getId());
                    projectLandPartMapList.add(boProjectLandPartMap);
                    projectIdMapLand.put(boProjectExtend.getProjectId(), tutouThLandinformationTb);

                    //投决会面积版本
                    BoProjectQuotaExtend boProjectQuotaExtend = new BoProjectQuotaExtend();
                    boProjectQuotaExtend.setId(UUIDUtils.create());
                    boProjectQuotaExtend.setVersion("V1.0");
                    boProjectQuotaExtend.setStageName(StageCodeEnum.STAGE_01.getValue());
                    boProjectQuotaExtend.setStageCode(StageCodeEnum.STAGE_01.getKey());
                    boProjectQuotaExtend.setVersionStatus(VersionStatusEnum.PASSED.getKey());
                    boProjectQuotaExtend.setProjectId(x.getSid());
                    boProjectQuotaExtend.setApproveEndTime(now);
                    boProjectQuotaExtend.setApproveStartTime(now);
                    boProjectQuotaExtend.setCreaterId(currentUser.getId());
                    boProjectQuotaExtend.setCreaterName(currentUser.getUsername());
                    boProjectQuotaExtend.setCreateTime(now);
                    boProjectQuotaExtend.setQuotaGroupCode(QuotaGroupCodeEnum.PROJECT_AREA_PLAN.getKey());
                    boProjectQuotaExtendList.add(boProjectQuotaExtend);

                    //投决会面积规划指标
                    List<BoProjectQuotaMap> projectQuotaMapList = quotaGroupMapList.stream().map(y -> {
                        BoProjectQuotaMap boProjectQuotaMap = new BoProjectQuotaMap();
                        BusinessObjectUtils.quotaValueSetDefaultForObject(y.getValueType(), boProjectQuotaMap);
                        boProjectQuotaMap.setCreaterName(currentUser.getUsername());
                        boProjectQuotaMap.setCreaterId(currentUser.getId());
                        boProjectQuotaMap.setCreateTime(now);
                        boProjectQuotaMap.setId(UUIDUtils.create());
                        boProjectQuotaMap.setProjectQuotaExtendId(boProjectQuotaExtend.getId());
                        boProjectQuotaMap.setQuotaCode(y.getCode());
                        boProjectQuotaMap.setQuotaGroupMapId(y.getId());
                        boProjectQuotaMap.setQuotaId(y.getQuotaId());
                        return boProjectQuotaMap;
                    }).collect(Collectors.toList());
                    boProjectQuotaMapList.addAll(projectQuotaMapList);

                    //投决会地块业态
                    BoLandPartProductTypeMap boLandPartProductTypeMap = new BoLandPartProductTypeMap();
                    boLandPartProductTypeMap.setId(UUIDUtils.create());
                    boLandPartProductTypeMap.setProjectQuotaExtendId(boProjectQuotaExtend.getId());
                    boLandPartProductTypeMap.setCreaterId(currentUser.getId());
                    boLandPartProductTypeMap.setCreaterName(currentUser.getUsername());
                    boLandPartProductTypeMap.setCreateTime(now);
                    boLandPartProductTypeMap.setLandPartId(tutouThLandinformationTb.getId());
                    boLandPartProductTypeMap.setLandPartName(tutouThLandinformationTb.getProjectName());
                    boLandPartProductTypeMap.setProductTypeCode(ProductTypeEnum.LITTLE_HIGH_FLOOR.getKey());
                    boLandPartProductTypeMap.setProductTypeName(ProductTypeEnum.LITTLE_HIGH_FLOOR.getValue());
                    boLandPartProductTypeMapList.add(boLandPartProductTypeMap);

                    //投决会地块业态指标
                    List<BoLandPartProductTypeQuotaMap> landQuotaList = landQuotaGroupList.stream().map(y -> {
                        BoLandPartProductTypeQuotaMap boLandPartProductTypeQuotaMap = new BoLandPartProductTypeQuotaMap();
                        BusinessObjectUtils.quotaValueSetDefaultForObject(y.getValueType(), boLandPartProductTypeQuotaMap);
                        if (QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(y.getCode())) {
                            //有产权
                            boLandPartProductTypeQuotaMap.setQuotaValue("1001");
                        }
                        boLandPartProductTypeQuotaMap.setCreaterName(currentUser.getUsername());
                        boLandPartProductTypeQuotaMap.setCreaterId(currentUser.getId());
                        boLandPartProductTypeQuotaMap.setCreateTime(now);
                        boLandPartProductTypeQuotaMap.setId(UUIDUtils.create());
                        boLandPartProductTypeQuotaMap.setLandPartProductTypeId(boLandPartProductTypeMap.getId());
                        boLandPartProductTypeQuotaMap.setQuotaCode(y.getCode());
                        boLandPartProductTypeQuotaMap.setQuotaGroupMapId(y.getId());
                        boLandPartProductTypeQuotaMap.setQuotaId(y.getQuotaId());
                        return boLandPartProductTypeQuotaMap;
                    }).collect(Collectors.toList());
                    boLandPartProductTypeQuotaMapList.addAll(landQuotaList);

                    //项目地块业态
                    BoProjectLandPartProductTypeMap boProjectLandPartProductTypeMap = new BoProjectLandPartProductTypeMap();
                    boProjectLandPartProductTypeMap.setId(UUIDUtils.create());
                    boProjectLandPartProductTypeMap.setProjectLandPartId(boProjectLandPartMap.getId());
                    boProjectLandPartProductTypeMap.setCreaterId(currentUser.getId());
                    boProjectLandPartProductTypeMap.setCreaterName(currentUser.getUsername());
                    boProjectLandPartProductTypeMap.setCreateTime(now);
                    boProjectLandPartProductTypeMap.setPropertyRightAttr("1001");
                    boProjectLandPartProductTypeMap.setProductTypeCode(ProductTypeEnum.LITTLE_HIGH_FLOOR.getKey());
                    boProjectLandPartProductTypeMap.setProductTypeName(ProductTypeEnum.LITTLE_HIGH_FLOOR.getValue());
                    boProjectLandPartProductTypeMapList.add(boProjectLandPartProductTypeMap);

                    //项目地块业态指标
                    List<BoProjectLandPartProductTypeQuotaMap> projectLandQuotaList = landQuotaList.stream().map(y -> {
                        BoProjectLandPartProductTypeQuotaMap boProjectLandPartProductTypeQuotaMap = new BoProjectLandPartProductTypeQuotaMap();
                        boProjectLandPartProductTypeQuotaMap.setQuotaValue(y.getQuotaValue());
                        boProjectLandPartProductTypeQuotaMap.setCreaterName(currentUser.getUsername());
                        boProjectLandPartProductTypeQuotaMap.setCreaterId(currentUser.getId());
                        boProjectLandPartProductTypeQuotaMap.setCreateTime(now);
                        boProjectLandPartProductTypeQuotaMap.setId(UUIDUtils.create());
                        boProjectLandPartProductTypeQuotaMap.setProjectLandPartProductTypeId(boProjectLandPartProductTypeMap.getId());
                        boProjectLandPartProductTypeQuotaMap.setQuotaCode(y.getQuotaCode());
                        boProjectLandPartProductTypeQuotaMap.setQuotaGroupMapId(y.getId());
                        boProjectLandPartProductTypeQuotaMap.setQuotaId(y.getQuotaId());
                        return boProjectLandPartProductTypeQuotaMap;
                    }).collect(Collectors.toList());
                    boProjectLandPartProductTypeQuotaMapList.addAll(projectLandQuotaList);
                }
            });

            //再遍历分期
            projectInfoList.stream().forEach(x -> {
                if (LevelTypeEnum.PROJECT_SUB.getKey().equals(x.getLevelType())) {
                    //分期
                    BoProjectExtend boProjectExtend = new BoProjectExtend();
                    boProjectExtendList.add(boProjectExtend);
                    boProjectExtend.setId(UUIDUtils.create());
                    boProjectExtend.setVersionStatus(VersionStatusEnum.PASSED.getKey());
                    boProjectExtend.setVersion("V1.0");
                    boProjectExtend.setApproveEndTime(now);
                    boProjectExtend.setApproveStartTime(now);
                    boProjectExtend.setCreaterId(currentUser.getId());
                    boProjectExtend.setCreateTime(now);
                    boProjectExtend.setCreaterName(currentUser.getUsername());
                    boProjectExtend.setProjectId(x.getSid());
                    boProjectExtend.setProjectName(x.getName());

                    //分期地块关系
                    BoProjectLandPartMap boProjectLandPartMap = new BoProjectLandPartMap();
                    boProjectLandPartMap.setId(UUIDUtils.create());
                    boProjectLandPartMap.setCreateTime(now);
                    boProjectLandPartMap.setCreaterId(currentUser.getId());
                    boProjectLandPartMap.setCreateTime(now);
                    boProjectLandPartMap.setCreaterName(currentUser.getUsername());
                    TutouThLandinformationTb tutouThLandinformationTb = projectIdMapLand.get(x.getParentSid());
                    boProjectLandPartMap.setLandPartId(tutouThLandinformationTb.getId());
                    boProjectLandPartMap.setLandPartName(tutouThLandinformationTb.getProjectName());
                    boProjectLandPartMap.setProjectExtendId(boProjectExtend.getId());
                    projectLandPartMapList.add(boProjectLandPartMap);

                    //分期地块业态
                    BoProjectLandPartProductTypeMap boProjectLandPartProductTypeMap = new BoProjectLandPartProductTypeMap();
                    boProjectLandPartProductTypeMap.setId(UUIDUtils.create());
                    boProjectLandPartProductTypeMap.setProjectLandPartId(boProjectLandPartMap.getId());
                    boProjectLandPartProductTypeMap.setCreaterId(currentUser.getId());
                    boProjectLandPartProductTypeMap.setCreaterName(currentUser.getUsername());
                    boProjectLandPartProductTypeMap.setCreateTime(now);
                    boProjectLandPartProductTypeMap.setPropertyRightAttr("1001");
                    boProjectLandPartProductTypeMap.setProductTypeCode(ProductTypeEnum.LITTLE_HIGH_FLOOR.getKey());
                    boProjectLandPartProductTypeMap.setProductTypeName(ProductTypeEnum.LITTLE_HIGH_FLOOR.getValue());
                    boProjectLandPartProductTypeMapList.add(boProjectLandPartProductTypeMap);

                    //分期地块业态指标
                    List<BoProjectLandPartProductTypeQuotaMap> landQuotaList = landQuotaGroupList.stream().map(y -> {
                        BoProjectLandPartProductTypeQuotaMap boLandPartProductTypeQuotaMap = new BoProjectLandPartProductTypeQuotaMap();
                        BusinessObjectUtils.quotaValueSetDefaultForObject(y.getValueType(), boLandPartProductTypeQuotaMap);
                        if (QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(y.getCode())) {
                            //有产权
                            boLandPartProductTypeQuotaMap.setQuotaValue("1001");
                        }
                        boLandPartProductTypeQuotaMap.setCreaterName(currentUser.getUsername());
                        boLandPartProductTypeQuotaMap.setCreaterId(currentUser.getId());
                        boLandPartProductTypeQuotaMap.setCreateTime(now);
                        boLandPartProductTypeQuotaMap.setId(UUIDUtils.create());
                        boLandPartProductTypeQuotaMap.setProjectLandPartProductTypeId(boProjectLandPartProductTypeMap.getId());
                        boLandPartProductTypeQuotaMap.setQuotaCode(y.getCode());
                        boLandPartProductTypeQuotaMap.setQuotaGroupMapId(y.getId());
                        boLandPartProductTypeQuotaMap.setQuotaId(y.getQuotaId());
                        return boLandPartProductTypeQuotaMap;
                    }).collect(Collectors.toList());
                    boProjectLandPartProductTypeQuotaMapList.addAll(landQuotaList);
                }
            });

            boProjectExtendService.saveBatch(boProjectExtendList, 100);
            tutouThLandinformationTbService.saveBatch(tutouThLandinformationTbList, 100);
            tutouThLandareaNewTbService.saveBatch(tutouThLandareaNewTbList, 100);
            boProjectLandPartMapService.saveBatch(projectLandPartMapList, 100);
            boProjectQuotaExtendService.saveBatch(boProjectQuotaExtendList, 100);
            boProjectQuotaMapService.saveBatch(boProjectQuotaMapList, 100);
            boLandPartProductTypeMapService.saveBatch(boLandPartProductTypeMapList, 100);
            boLandPartProductTypeQuotaMapService.saveBatch(boLandPartProductTypeQuotaMapList, 100);
            boProjectLandPartProductTypeMapService.saveBatch(boProjectLandPartProductTypeMapList, 100);
            boProjectLandPartProductTypeQuotaMapService.saveBatch(boProjectLandPartProductTypeQuotaMapList, 100);
        } catch (UcException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONResult getProjectInfo(String projectId, String versionId, JSONResult jsonResult) throws Exception {

        ProjectInfoVo projectInfoVo = new ProjectInfoVo();
        if(StringUtils.isNotBlank(versionId)){
            ProjectInfoDto projectInfoDto = projectExtendMapper.selectProjectByVersionId(versionId);

            if(projectInfoDto != null){
                projectInfoVo.setVersionId(versionId);
                projectInfoVo.setProjectId(projectInfoDto.getProjectId());
                projectInfoVo.setProjectName(projectInfoDto.getProjectName());
                projectInfoVo.setProjectCaseName(projectInfoDto.getCaseName());
                projectInfoVo.setAreaCompany(projectInfoDto.getCompanyAreaName());
                projectInfoVo.setCityCompany(projectInfoDto.getCompanyCityName());
                projectInfoVo.setCityId(projectInfoDto.getCityId());
                projectInfoVo.setCity(projectInfoDto.getCityName());
                projectInfoVo.setProjectAddress(projectInfoDto.getProjectAddress());
                projectInfoVo.setObtainStatusCode(projectInfoDto.getGainStatusCode());
                projectInfoVo.setProjectPic(projectInfoDto.getPicUrl());
                projectInfoVo.setAddressPoint(projectInfoDto.getPoint());

                List<String> urls = projectExtendMapper.selectSubPicurlsByProjectId(projectInfoDto.getProjectId());
                projectInfoVo.setTotalPic(urls);
            }
        }else{
            MdmProjectInfo mdmProjectInfo = mdmProjectInfoMapper.selectById(projectId);
            if(mdmProjectInfo != null){
                projectInfoVo.setProjectId(mdmProjectInfo.getSid());
                projectInfoVo.setProjectName(mdmProjectInfo.getFullName());
                projectInfoVo.setAreaCompany(mdmProjectInfo.getRegionName());
                projectInfoVo.setCityCompany(mdmProjectInfo.getCityCompanyName());
            }
        }

        jsonResult.setData(projectInfoVo);
        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
        return jsonResult;
    }

    @Override
    public JSONResult getSubProjectInfo(String subProjectId, String versionId, JSONResult jsonResult) throws Exception {

        SubProjectInfoVo subProjectInfoVo = new SubProjectInfoVo();
        if(StringUtils.isNotBlank(versionId)){
            ProjectInfoDto projectInfoDto = projectExtendMapper.selectSubProjectByVersionId(versionId);

            if(projectInfoDto != null){
                subProjectInfoVo.setVersionId(versionId);
                subProjectInfoVo.setProjectId(projectInfoDto.getProjectId());
                subProjectInfoVo.setSubProjectId(projectInfoDto.getSubProjectId());
                subProjectInfoVo.setProjectName(projectInfoDto.getProjectName());
                subProjectInfoVo.setSubProjectName(projectInfoDto.getSubProjectName());
                subProjectInfoVo.setObtainStatusCode(projectInfoDto.getGainStatusCode());
                subProjectInfoVo.setProjectType(projectInfoDto.getProjectType());
                subProjectInfoVo.setTradeModeCode(projectInfoDto.getTradeModeCode());
                subProjectInfoVo.setMergeTableTypeCode(projectInfoDto.getMergeTableTypeCode());
                subProjectInfoVo.setTaxTypeCode(projectInfoDto.getTaxTypeCode());
                subProjectInfoVo.setTotalSubPic(projectInfoDto.getPicUrl());
                subProjectInfoVo.setObtainStatusCode(projectInfoDto.getGainStatusCode());

                List<PartitionInfoVo> partitionInfos = projectPartMapper.selectByExtendId(versionId, IsDeleteEnum.NO.getKey(), IsDisableEnum.NO.getKey());
                if (partitionInfos != null && partitionInfos.size() > 0) {
                    subProjectInfoVo.setPartitionNum(String.valueOf(partitionInfos.size()));
                    subProjectInfoVo.setPartitionDetails(partitionInfos);
                } else {
                    partitionInfos = new ArrayList<PartitionInfoVo>();
                    subProjectInfoVo.setPartitionNum("0");
                    subProjectInfoVo.setPartitionDetails(partitionInfos);
                }
            }
        }else{
            MdmProjectInfo subMdmProjectInfo = mdmProjectInfoMapper.selectById(subProjectId);
            MdmProjectInfo mdmProjectInfo = mdmProjectInfoMapper.selectById(subMdmProjectInfo.getParentSid());
            if(subMdmProjectInfo != null){
                subProjectInfoVo.setProjectId(mdmProjectInfo.getSid());
                subProjectInfoVo.setProjectName(mdmProjectInfo.getFullName());
                subProjectInfoVo.setSubProjectId(subMdmProjectInfo.getSid());
                subProjectInfoVo.setSubProjectName(subMdmProjectInfo.getName());
            }
        }

        jsonResult.setData(subProjectInfoVo);
        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
        return jsonResult;
    }

    @Override
    @Transactional
    public JSONResult updateVersionStatus(ProjectLaunchAppReqParam projectLaunchAppReqParam, JSONResult jsonResult) throws Exception {

        ProjectInfoDto projectInfoDto = projectExtendMapper.selectProjectByVersionId(projectLaunchAppReqParam.getVersionId());
        jsonResult.setCode(CodeEnum.ERROR.getKey());
        jsonResult.setMsg(CodeEnum.ERROR.getValue());

        if (projectInfoDto != null) {
            String version = projectInfoDto.getVersion();
            if (StringUtils.isNotBlank(version)) {
                String extendId = projectInfoDto.getVersionId();
                if (StringUtils.isNotBlank(extendId)) {
                    BoProjectExtend projectExtend = new BoProjectExtend();
                    projectExtend.setId(extendId);

                    if(projectLaunchAppReqParam.getVersionState() != null){
                        projectExtend.setVersionStatus(projectLaunchAppReqParam.getVersionState());
                    }else{
                        projectExtend.setVersionStatus(VersionStatusEnum.PASSED.getKey());
                    }

                    projectExtendMapper.updateById(projectExtend);

                    jsonResult.setCode(CodeEnum.SUCCESS.getKey());
                    jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
                } else {
                    jsonResult.setCode(CodeEnum.PROJECT_EXTENDS.getKey());
                    jsonResult.setMsg(CodeEnum.PROJECT_EXTENDS.getValue());
                }
            } else {
                jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
                jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
            }
        }

        return jsonResult;
    }

    @Override
    public JSONResult validateBeforApproval(String versionId, CurrentUserVO userVO, JSONResult jsonResult) throws Exception {
        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());

        BoProjectExtend boProjectExtend = projectExtendMapper.selectById(versionId);
        if(boProjectExtend != null){
            String version = boProjectExtend.getVersion();
            if(StringUtils.isNotBlank(version)){
                Integer versionStatus = boProjectExtend.getVersionStatus();
                String approveId = boProjectExtend.getApproveId();

                if(StringUtils.isBlank(approveId) && versionStatus != null && versionStatus.equals(VersionStatusEnum.PASSED.getKey())){
                    jsonResult.setCode(CodeEnum.APPROVAL_EXTENDS.getKey());
                    jsonResult.setMsg(CodeEnum.APPROVAL_EXTENDS.getValue());
                    return jsonResult;
                }

                if(StringUtils.isNotBlank(approveId)){
                    QueryWrapper<BoApproveRecord> queryWrapper = new QueryWrapper<BoApproveRecord>();
                    queryWrapper.eq("approve_id", approveId)
                            .eq("is_delete", IsDeleteEnum.NO.getKey())
                            .eq("is_disable", IsDisableEnum.NO.getKey())
                            .orderByDesc("create_time").last("limit 1");
                    List<BoApproveRecord> boApproveRecords = approveRecordMapper.selectList(queryWrapper);
                    if(CollectionUtils.isNotEmpty(boApproveRecords)){
                        BoApproveRecord boApproveRecord = boApproveRecords.get(0);
                        String createrId = boApproveRecord.getCreaterId();

                        if(StringUtils.isNotBlank(createrId) && !userVO.getId().equals(createrId)){
                            QueryWrapper<UcUser> queryUserWrapper = new QueryWrapper<UcUser>();
                            queryUserWrapper.eq("fd_sid", createrId) .last("limit 1");
                            List<UcUser> user = userMapper.selectList(queryUserWrapper);

                            jsonResult.setCode(CodeEnum.APPROVAL_PROCESS_USE_EXTENDS.getKey());
                            jsonResult.setMsg(CodeEnum.APPROVAL_PROCESS_USE_EXTENDS.getValue().replace("**", CollectionUtils.isEmpty(user)?"":user.get(0).getFdName()));
                            return jsonResult;
                        }
                    }
                }
            }else{
                jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
                jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
                return jsonResult;
            }
        }else{
            jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
            return jsonResult;
        }

        return jsonResult;
    }

}
