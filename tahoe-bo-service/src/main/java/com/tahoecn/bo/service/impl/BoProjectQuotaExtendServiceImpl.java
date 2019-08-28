package com.tahoecn.bo.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.common.enums.QuotaGroupCodeEnum;
import com.tahoecn.bo.common.enums.StageCodeEnum;
import com.tahoecn.bo.common.enums.VersionStatusEnum;
import com.tahoecn.bo.common.utils.BusinessObjectUtils;
import com.tahoecn.bo.common.utils.JsonResultBuilder;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.common.utils.VersionUtils;
import com.tahoecn.bo.mapper.BoProjectQuotaExtendMapper;
import com.tahoecn.bo.model.bo.BpmBusinessInfoBo;
import com.tahoecn.bo.model.entity.BoBuilding;
import com.tahoecn.bo.model.entity.BoBuildingProductTypeMap;
import com.tahoecn.bo.model.entity.BoBuildingProductTypeQuotaMap;
import com.tahoecn.bo.model.entity.BoBuildingQuotaMap;
import com.tahoecn.bo.model.entity.BoGovPlanCard;
import com.tahoecn.bo.model.entity.BoGovPlanCardBuildingMap;
import com.tahoecn.bo.model.entity.BoLandPartProductTypeMap;
import com.tahoecn.bo.model.entity.BoLandPartProductTypeQuotaMap;
import com.tahoecn.bo.model.entity.BoProjectLandPartMap;
import com.tahoecn.bo.model.entity.BoProjectPlanCard;
import com.tahoecn.bo.model.entity.BoProjectPlanCardBuildingMap;
import com.tahoecn.bo.model.entity.BoProjectQuotaExtend;
import com.tahoecn.bo.model.entity.BoProjectQuotaMap;
import com.tahoecn.bo.model.entity.BoQuotaGroupMap;
import com.tahoecn.bo.model.vo.CurrentUserVO;
import com.tahoecn.bo.service.BoBuildingProductTypeMapService;
import com.tahoecn.bo.service.BoBuildingProductTypeQuotaMapService;
import com.tahoecn.bo.service.BoBuildingQuotaMapService;
import com.tahoecn.bo.service.BoBuildingService;
import com.tahoecn.bo.service.BoGovPlanCardBuildingMapService;
import com.tahoecn.bo.service.BoGovPlanCardService;
import com.tahoecn.bo.service.BoLandPartProductTypeMapService;
import com.tahoecn.bo.service.BoLandPartProductTypeQuotaMapService;
import com.tahoecn.bo.service.BoProjectExtendService;
import com.tahoecn.bo.service.BoProjectLandPartMapService;
import com.tahoecn.bo.service.BoProjectPlanCardBuildingMapService;
import com.tahoecn.bo.service.BoProjectPlanCardService;
import com.tahoecn.bo.service.BoProjectQuotaExtendService;
import com.tahoecn.bo.service.BoProjectQuotaMapService;
import com.tahoecn.bo.service.BoQuotaGroupMapService;
import com.tahoecn.core.json.JSONResult;

/**
 * <p>
 * 项目分期扩展信息表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class BoProjectQuotaExtendServiceImpl extends ServiceImpl<BoProjectQuotaExtendMapper, BoProjectQuotaExtend> implements BoProjectQuotaExtendService {

	@Autowired
	private BoProjectQuotaExtendMapper boProjectQuotaExtendMapper;
	
    @Autowired
    private BoQuotaGroupMapService boQuotaGroupMapService;

    @Autowired
    private BoProjectQuotaMapService boProjectQuotaMapService;

    @Autowired
    private BoLandPartProductTypeMapService boLandPartProductTypeMapService;

    @Autowired
    private BoLandPartProductTypeQuotaMapService boLandPartProductTypeQuotaMapService;

    @Autowired
    private BoBuildingProductTypeMapService boBuildingProductTypeMapService;

    @Autowired
    private BoBuildingProductTypeQuotaMapService boBuildingProductTypeQuotaMapService;

    @Autowired
    private BoGovPlanCardService boGovPlanCardService;

    @Autowired
    private BoProjectPlanCardService boProjectPlanCardService;

    @Autowired
    private BoGovPlanCardBuildingMapService boGovPlanCardBuildingMapService;

    @Autowired
    private BoProjectPlanCardBuildingMapService boProjectPlanCardBuildingMapService;

    @Autowired
    private BoBuildingService boBuildingService;

    @Autowired
    private BoBuildingQuotaMapService boBuildingQuotaMapService;

    @Autowired
    private BoProjectExtendService boProjectExtendService;

    @Autowired
    private BoProjectLandPartMapService boProjectLandPartMapService;

    @Override
    public BoProjectQuotaExtend getLastPassedVersion(String projectId, String... stageCode) {
        QueryWrapper<BoProjectQuotaExtend> wrapper = new QueryWrapper<>();
        if (stageCode.length == 1) {
            wrapper.eq("stage_code", stageCode[0]);
        } else {
            wrapper.in("stage_code", stageCode);
        }
        wrapper.eq("project_id", projectId)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .eq("version_status", VersionStatusEnum.PASSED.getKey()).orderByDesc("create_time").last("limit 1");
        List<BoProjectQuotaExtend> boProjectQuotaExtends = baseMapper.selectList(wrapper);
        return boProjectQuotaExtends.isEmpty() ? null : boProjectQuotaExtends.get(0);

    }

    @Override
    public BoProjectQuotaExtend getLastVersion(String projectId, String stageCode) {
        Page<BoProjectQuotaExtend> page = new Page<>();
        page.setDesc("create_time");
        page.setSize(1);
        QueryWrapper<BoProjectQuotaExtend> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
                .eq("stage_code", stageCode)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        IPage<BoProjectQuotaExtend> pageResult = baseMapper.selectPage(page, wrapper);
        return pageResult.getRecords().isEmpty() ? null : pageResult.getRecords().get(0);

    }

    @Override
    public boolean hasCanEditData(String projectSubId, String stageCode) {
        QueryWrapper<BoProjectQuotaExtend> wrapper = new QueryWrapper<>();
        //需满足当前分期的当前阶段下，不存在编制中、审批中与审批驳回状态的数据
        wrapper.eq("project_id", projectSubId)
                .eq("stage_code", stageCode)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .in("version_status", VersionStatusEnum.CHECKING.getKey(),
                        VersionStatusEnum.CREATING.getKey(),
                        VersionStatusEnum.REJECTED.getKey());
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BoProjectQuotaExtend createVersion(String projectId, String stageCode, CurrentUserVO currentUserVO) {
        if (StageCodeEnum.getByKey(stageCode) == StageCodeEnum.STAGE_01) {
            return createProjectVersion(projectId, stageCode, currentUserVO);
        }
        return createProjectSubVersion(projectId, stageCode, currentUserVO);

    }

    @Override
    public boolean updateForClearApproveData(BoProjectQuotaExtend boProjectQuotaExtend) {
        boProjectQuotaExtend.setVersionStatus(VersionStatusEnum.CREATING.getKey());
        return baseMapper.updateForClearApproveData(boProjectQuotaExtend) > 0;
    }

    @Override
    public BpmBusinessInfoBo getPreApproveInfo(String versionId) {
        QueryWrapper<BoProjectQuotaExtend> queryWrapper = new QueryWrapper<>();
        //将stageName 赋值为 项目/分期的组合名称
        queryWrapper.select(
                "project_id",
                "stage_code",
                "version",
                "(select case bo_project_quota_extend.stage_code when '" + StageCodeEnum.STAGE_01.getKey() + "' then fd_name else concat(fd_pname, fd_name) end from uc_org a where a.fd_sid = bo_project_quota_extend.project_id limit 1 ) as stage_name"
        ).eq("id", versionId);
        BoProjectQuotaExtend boProjectQuotaExtend = getOne(queryWrapper);
        if (boProjectQuotaExtend == null) {
            return null;
        }
        StageCodeEnum stageCodeEnum = StageCodeEnum.getByKey(boProjectQuotaExtend.getStageCode());
        String versionDesc = stageCodeEnum.getValue() + boProjectQuotaExtend.getVersion();
        BpmBusinessInfoBo bpmBusinessInfoBo = new BpmBusinessInfoBo();
        bpmBusinessInfoBo.setDocSubject("请审批[" + boProjectQuotaExtend.getStageName() + versionDesc + "]研发指标");
        bpmBusinessInfoBo.setProjectId(boProjectQuotaExtend.getProjectId());
        bpmBusinessInfoBo.setVersionDesc(versionDesc);
        return bpmBusinessInfoBo;
    }

    @Override
    public JSONResult areaValidateApprove(String versionId) {
        BoProjectQuotaExtend boProjectQuotaExtend = getById(versionId);
        if (boProjectQuotaExtend == null) {
            return JsonResultBuilder.failed("版本不存在");
        }
        VersionStatusEnum versionStatusEnum = VersionStatusEnum.getByKey(boProjectQuotaExtend.getVersionStatus());
        if (versionStatusEnum != VersionStatusEnum.CREATING && versionStatusEnum != VersionStatusEnum.REJECTED) {
            return JsonResultBuilder.failed("当前版本不可发起审批");
        }
        if (StageCodeEnum.STAGE_01.getKey().equals(boProjectQuotaExtend.getStageCode())) {
            //面积版本使用地块
            List<BoLandPartProductTypeMap> landPartProductTypeList = boLandPartProductTypeMapService.getLandPartProductTypeList(boProjectQuotaExtend.getId());
            Set<String> areaLandUseSet = landPartProductTypeList.parallelStream().map(x -> x.getLandPartId()).collect(Collectors.toSet());
            //查项目规划的地块
            List<BoProjectLandPartMap> landPartList = boProjectLandPartMapService.getProjectLandPartListByProjectQuotaExtendId(versionId);
            Set<String> projectLandUseSet = landPartList.parallelStream().map(x -> x.getLandPartId()).collect(Collectors.toSet());
            if (projectLandUseSet.size() > areaLandUseSet.size()) {
                return JsonResultBuilder.failed("当前版本未使用全部规划地块");
            }
            for (String landId : areaLandUseSet) {
                if (!projectLandUseSet.contains(landId)) {
                    return JsonResultBuilder.failed("当前版本中的地块与项目规划地块不同步");
                }
            }
        }else {
            //面积版本使用地块
            List<BoBuilding> buildingProductTypeList = boBuildingService.getBuildingList(boProjectQuotaExtend.getId());
            Set<String> areaLandUseSet = buildingProductTypeList.parallelStream().map(x -> x.getProjectLandPartId()).collect(Collectors.toSet());
            //查项目规划的地块
            List<BoProjectLandPartMap> landPartList = boProjectLandPartMapService.getProjectLandPartListByProjectQuotaExtendId(versionId);
            Set<String> projectLandUseSet = landPartList.parallelStream().map(x -> x.getLandPartId()).collect(Collectors.toSet());
            if (projectLandUseSet.size() > areaLandUseSet.size()) {
                return JsonResultBuilder.failed("当前版本未使用全部规划地块");
            }
            for (String landId : areaLandUseSet) {
                if (!projectLandUseSet.contains(landId)) {
                    return JsonResultBuilder.failed("当前版本中的地块与分期规划地块不同步");
                }
            }
        }
        return JsonResultBuilder.success();
    }


    /**
     * 创建项目版本
     *
     * @return
     */
    private BoProjectQuotaExtend createProjectVersion(String projectId, String stageCode, CurrentUserVO currentUserVO) {
        //用于复制的版本
        BoProjectQuotaExtend lastPassedVersion = getLastPassedVersion(projectId, stageCode);

        //分期指标扩展信息表 - 新增
        BoProjectQuotaExtend newVersion = new BoProjectQuotaExtend();
        newVersion.setId(UUIDUtils.create());
        newVersion.setQuotaGroupCode(QuotaGroupCodeEnum.PROJECT_SUB_AREA_PLAN.getKey());
        newVersion.setStageCode(stageCode);
        newVersion.setStageName(StageCodeEnum.getByKey(stageCode).getValue());
        newVersion.setProjectId(projectId);
        newVersion.setStageCode(stageCode);
        newVersion.setVersion(VersionUtils.bigVersionInc(lastPassedVersion == null ? null : lastPassedVersion.getVersion()));
        newVersion.setPreId(lastPassedVersion == null ? null : lastPassedVersion.getId());
        newVersion.setVersionStatus(VersionStatusEnum.CREATING.getKey());
        newVersion.setCreateTime(LocalDateTime.now());
        newVersion.setCreaterId(currentUserVO.getId());
        newVersion.setCreaterName(currentUserVO.getName());
        baseMapper.insert(newVersion);

        //项目指标关系 - 新增
        if (lastPassedVersion == null) {
            List<BoQuotaGroupMap> quotaGroupMapList = boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.PROJECT_SUB_AREA_PLAN.getKey());
            if (!quotaGroupMapList.isEmpty()) {
                List<BoProjectQuotaMap> boProjectQuotaMapList = quotaGroupMapList.stream().map(x -> {
                    BoProjectQuotaMap boProjectQuotaMap = new BoProjectQuotaMap();
                    boProjectQuotaMap.setId(UUIDUtils.create());
                    boProjectQuotaMap.setCreaterId(currentUserVO.getId());
                    boProjectQuotaMap.setCreaterName(currentUserVO.getName());
                    boProjectQuotaMap.setCreateTime(LocalDateTime.now());
                    boProjectQuotaMap.setProjectQuotaExtendId(newVersion.getId());

                    boProjectQuotaMap.setQuotaCode(x.getCode());
                    boProjectQuotaMap.setQuotaGroupMapId(x.getId());
                    boProjectQuotaMap.setQuotaId(x.getQuotaId());
                    BusinessObjectUtils.quotaValueSetDefaultForObject(x.getValueType(), boProjectQuotaMap);
                    return boProjectQuotaMap;
                }).collect(Collectors.toList());
                boProjectQuotaMapService.saveBatch(boProjectQuotaMapList, 100);
            }
        } else {
            //复制指标
            List<BoProjectQuotaMap> projectQuotaMapList = boProjectQuotaMapService.getProjectQuotaMapList(lastPassedVersion.getId());
            if (!projectQuotaMapList.isEmpty()) {
                projectQuotaMapList.parallelStream().forEach(x -> {
                    x.setId(UUIDUtils.create());
                    x.setProjectQuotaExtendId(newVersion.getId());
                    x.setCreaterId(currentUserVO.getId());
                    x.setCreateTime(LocalDateTime.now());
                    x.setCreaterName(currentUserVO.getName());
                    x.setUpdaterId(null);
                    x.setUpdateTime(null);
                    x.setUpdaterName(null);
                });
                boProjectQuotaMapService.saveBatch(projectQuotaMapList, 100);
            }
        }

        //地块业态映射关系 - 复制
        if (lastPassedVersion != null) {
            List<BoLandPartProductTypeMap> landPartProductTypeList = boLandPartProductTypeMapService.getLandPartProductTypeList(lastPassedVersion.getId());
            if (CollectionUtils.isNotEmpty(landPartProductTypeList)) {
                List<String> refIds = landPartProductTypeList.parallelStream().map(x -> x.getId()).collect(Collectors.toList());
                List<BoLandPartProductTypeQuotaMap> landPartProductTypeQuotaList = boLandPartProductTypeQuotaMapService.getLandPartProductTypeQuotaList(refIds);

                Map<String, String> idMap = new HashMap<>();
                landPartProductTypeList.stream().forEach(x -> {
                    String id = UUIDUtils.create();
                    idMap.put(x.getId(), id);
                    x.setId(id);
                    x.setProjectQuotaExtendId(newVersion.getId());
                    x.setCreaterId(currentUserVO.getId());
                    x.setCreateTime(LocalDateTime.now());
                    x.setCreaterName(currentUserVO.getName());
                    x.setUpdaterId(null);
                    x.setUpdateTime(null);
                    x.setUpdaterName(null);
                });
                boLandPartProductTypeMapService.saveBatch(landPartProductTypeList, 100);

                //地块业态关系指标
                if (CollectionUtils.isNotEmpty(landPartProductTypeQuotaList)) {
                    landPartProductTypeQuotaList.parallelStream().forEach(x -> {
                        x.setId(UUIDUtils.create());
                        x.setLandPartProductTypeId(idMap.get(x.getLandPartProductTypeId()));
                        x.setCreaterId(currentUserVO.getId());
                        x.setCreateTime(LocalDateTime.now());
                        x.setCreaterName(currentUserVO.getName());
                        x.setUpdaterId(null);
                        x.setUpdateTime(null);
                        x.setUpdaterName(null);
                    });
                    boLandPartProductTypeQuotaMapService.saveBatch(landPartProductTypeQuotaList, 100);
                }
            }
        }
        return newVersion;
    }

    /**
     * 创建分期版本
     *
     * @return
     */
    private BoProjectQuotaExtend createProjectSubVersion(String projectId, String stageCode, CurrentUserVO currentUserVO) {
        return createProjectSubVersionStage234(projectId, stageCode, currentUserVO);
    }

    /**
     * 创建分期-234阶段的版本
     *
     * @param projectId
     * @param stageCode
     * @param currentUserVO
     * @return
     */
    private BoProjectQuotaExtend createProjectSubVersionStage234(String projectId, String stageCode, CurrentUserVO currentUserVO) {
        //用于版本号递增的版本数据
        BoProjectQuotaExtend currentVersion = getLastPassedVersion(projectId, stageCode);

        //用户复制数据的最新审批通过版本数据
        BoProjectQuotaExtend lastPassedVersion = getLastPassedVersion(projectId, StageCodeEnum.STAGE_03.getKey(), StageCodeEnum.STAGE_04.getKey(), StageCodeEnum.STAGE_02.getKey());

        //分期指标扩展信息表 - 新增
        BoProjectQuotaExtend newVersion = new BoProjectQuotaExtend();
        newVersion.setId(UUIDUtils.create());
        newVersion.setQuotaGroupCode(QuotaGroupCodeEnum.PROJECT_SUB_AREA_PLAN.getKey());
        newVersion.setStageCode(stageCode);
        newVersion.setStageName(StageCodeEnum.getByKey(stageCode).getValue());
        newVersion.setProjectId(projectId);
        newVersion.setStageCode(stageCode);
        newVersion.setVersion(VersionUtils.bigVersionInc(currentVersion == null ? null : currentVersion.getVersion()));
        newVersion.setPreId(lastPassedVersion == null ? null : lastPassedVersion.getId());
        newVersion.setVersionStatus(VersionStatusEnum.CREATING.getKey());
        newVersion.setCreateTime(LocalDateTime.now());
        newVersion.setCreaterId(currentUserVO.getId());
        newVersion.setCreaterName(currentUserVO.getName());
        baseMapper.insert(newVersion);

        //项目指标关系 - 新增
        if (lastPassedVersion == null) {
            List<BoQuotaGroupMap> quotaGroupMapList = boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.PROJECT_SUB_AREA_PLAN.getKey());
            if (!quotaGroupMapList.isEmpty()) {
                List<BoProjectQuotaMap> boProjectQuotaMapList = quotaGroupMapList.stream().map(x -> {
                    BoProjectQuotaMap boProjectQuotaMap = new BoProjectQuotaMap();
                    boProjectQuotaMap.setId(UUIDUtils.create());
                    boProjectQuotaMap.setCreaterId(currentUserVO.getId());
                    boProjectQuotaMap.setCreaterName(currentUserVO.getName());
                    boProjectQuotaMap.setCreateTime(LocalDateTime.now());
                    boProjectQuotaMap.setProjectQuotaExtendId(newVersion.getId());

                    boProjectQuotaMap.setQuotaCode(x.getCode());
                    boProjectQuotaMap.setQuotaGroupMapId(x.getId());
                    boProjectQuotaMap.setQuotaId(x.getQuotaId());
                    return boProjectQuotaMap;
                }).collect(Collectors.toList());
                boProjectQuotaMapService.saveBatch(boProjectQuotaMapList, 100);
            }
        } else {
            //复制指标
            List<BoProjectQuotaMap> projectQuotaMapList = boProjectQuotaMapService.getProjectQuotaMapList(lastPassedVersion.getId());
            if (!projectQuotaMapList.isEmpty()) {
                projectQuotaMapList.parallelStream().forEach(x -> {
                    x.setId(UUIDUtils.create());
                    x.setProjectQuotaExtendId(newVersion.getId());
                    x.setCreaterId(currentUserVO.getId());
                    x.setCreateTime(LocalDateTime.now());
                    x.setCreaterName(currentUserVO.getName());
                    x.setUpdaterId(null);
                    x.setUpdateTime(null);
                    x.setUpdaterName(null);
                });
                boProjectQuotaMapService.saveBatch(projectQuotaMapList, 100);
            }
        }


        if (lastPassedVersion != null) {
            //楼栋信息
            List<BoBuilding> buildingList = boBuildingService.getBuildingList(lastPassedVersion.getId());
            List<String> refBuildingIds = buildingList.parallelStream().map(x -> x.getId()).collect(Collectors.toList());
            List<BoBuildingQuotaMap> buildingQuotaList = boBuildingQuotaMapService.getBuildingQuotaList(refBuildingIds);
            Map<String, String> buildingIdMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(buildingList)) {
                buildingList.parallelStream().forEach(x -> {
                    String id = UUIDUtils.create();
                    buildingIdMap.put(x.getId(), id);
                    x.setPreId(x.getId());
                    x.setId(id);
                    x.setProjectQuotaExtendId(newVersion.getId());
                    x.setCreaterId(currentUserVO.getId());
                    x.setCreateTime(LocalDateTime.now());
                    x.setCreaterName(currentUserVO.getName());
                    x.setUpdaterId(null);
                    x.setUpdateTime(null);
                    x.setUpdaterName(null);
                });
                boBuildingService.saveBatch(buildingList, 100);

                //楼栋指标关系
                buildingQuotaList.parallelStream().forEach(x -> {
                    x.setId(UUIDUtils.create());
                    x.setBuildingId(buildingIdMap.get(x.getBuildingId()));
                    x.setCreaterId(currentUserVO.getId());
                    x.setCreateTime(LocalDateTime.now());
                    x.setCreaterName(currentUserVO.getName());
                    x.setUpdaterId(null);
                    x.setUpdateTime(null);
                    x.setUpdaterName(null);
                });
                boBuildingQuotaMapService.saveBatch(buildingQuotaList, 100);


                //楼栋业态映射关系
                List<BoBuildingProductTypeMap> buildingProductTypeList = boBuildingProductTypeMapService.getBuildingProductTypeList(lastPassedVersion.getId());
                if (CollectionUtils.isNotEmpty(buildingProductTypeList)) {
                    List<String> refIds = buildingProductTypeList.parallelStream().map(x -> x.getId()).collect(Collectors.toList());
                    List<BoBuildingProductTypeQuotaMap> buildingProductTypeQuotaList = boBuildingProductTypeQuotaMapService.getBuildingProductTypeQuotaList(refIds);

                    Map<String, String> buildingProductTypeIdMap = new HashMap<>();
                    buildingProductTypeList.stream().forEach(x -> {
                        String id = UUIDUtils.create();
                        buildingProductTypeIdMap.put(x.getId(), id);
                        x.setId(id);
                        x.setBuildingId(buildingIdMap.get(x.getBuildingId()));
                        x.setProjectQuotaExtendId(newVersion.getId());
                        x.setCreaterId(currentUserVO.getId());
                        x.setCreateTime(LocalDateTime.now());
                        x.setCreaterName(currentUserVO.getName());
                        x.setUpdaterId(null);
                        x.setUpdateTime(null);
                        x.setUpdaterName(null);
                    });
                    boBuildingProductTypeMapService.saveBatch(buildingProductTypeList, 100);

                    //楼栋业态指标
                    if (CollectionUtils.isNotEmpty(buildingProductTypeQuotaList)) {
                        buildingProductTypeQuotaList.parallelStream().forEach(x -> {
                            x.setId(UUIDUtils.create());
                            x.setBuildingProductTypeId(buildingProductTypeIdMap.get(x.getBuildingProductTypeId()));
                            x.setCreaterId(currentUserVO.getId());
                            x.setCreateTime(LocalDateTime.now());
                            x.setCreaterName(currentUserVO.getName());
                            x.setUpdaterId(null);
                            x.setUpdateTime(null);
                            x.setUpdaterName(null);
                        });
                        boBuildingProductTypeQuotaMapService.saveBatch(buildingProductTypeQuotaList, 100);
                    }
                }
            }


            //政府方案
            List<BoGovPlanCard> govPlanCardList = boGovPlanCardService.getGovPlanCardList(lastPassedVersion.getId());
            if (CollectionUtils.isNotEmpty(govPlanCardList)) {
                List<String> refIds = govPlanCardList.parallelStream().map(x -> x.getId()).collect(Collectors.toList());
                List<BoGovPlanCardBuildingMap> govPlanCardBuildingList = boGovPlanCardBuildingMapService.getGovPlanCardBuildingList(refIds);
                Map<String, String> govPlanCardIdMap = new HashMap<>();
                govPlanCardList.stream().forEach(x -> {
                    String id = UUIDUtils.create();
                    govPlanCardIdMap.put(x.getId(), id);
                    x.setId(id);
                    x.setProjectQuotaExtendId(newVersion.getId());
                    x.setCreaterId(currentUserVO.getId());
                    x.setCreateTime(LocalDateTime.now());
                    x.setCreaterName(currentUserVO.getName());
                    x.setUpdaterId(null);
                    x.setUpdateTime(null);
                    x.setUpdaterName(null);
                });
                boGovPlanCardService.saveBatch(govPlanCardList, 100);

                //政府方案楼栋
                if (CollectionUtils.isNotEmpty(govPlanCardBuildingList)) {
                    govPlanCardBuildingList.parallelStream().forEach(x -> {
                        x.setId(UUIDUtils.create());
                        x.setBuildingId(buildingIdMap.get(x.getBuildingId()));
                        x.setGovPlanCardId(govPlanCardIdMap.get(x.getGovPlanCardId()));
                        x.setCreaterId(currentUserVO.getId());
                        x.setCreateTime(LocalDateTime.now());
                        x.setCreaterName(currentUserVO.getName());
                        x.setUpdaterId(null);
                        x.setUpdateTime(null);
                        x.setUpdaterName(null);
                    });

                    boGovPlanCardBuildingMapService.saveBatch(govPlanCardBuildingList, 100);
                }
            }

            //工规证
            List<BoProjectPlanCard> projectPlanCardList = boProjectPlanCardService.getProjectPlanCardList(lastPassedVersion.getId());
            if (CollectionUtils.isNotEmpty(projectPlanCardList)) {
                List<String> refIds = projectPlanCardList.parallelStream().map(x -> x.getId()).collect(Collectors.toList());
                List<BoProjectPlanCardBuildingMap> boProjectPlanCardBuildingList = boProjectPlanCardBuildingMapService.getBoProjectPlanCardBuildingList(refIds);
                Map<String, String> projectPlanCardIdMap = new HashMap<>();
                projectPlanCardList.parallelStream().forEach(x -> {
                    String id = UUIDUtils.create();
                    projectPlanCardIdMap.put(x.getId(), id);
                    x.setId(id);
                    x.setProjectQuotaExtendId(newVersion.getId());
                    x.setCreaterId(currentUserVO.getId());
                    x.setCreateTime(LocalDateTime.now());
                    x.setCreaterName(currentUserVO.getName());
                    x.setUpdaterId(null);
                    x.setUpdateTime(null);
                    x.setUpdaterName(null);
                });

                boProjectPlanCardService.saveBatch(projectPlanCardList, 100);

                //工规证楼栋
                if (CollectionUtils.isNotEmpty(boProjectPlanCardBuildingList)) {
                    boProjectPlanCardBuildingList.parallelStream().forEach(x -> {
                        x.setId(UUIDUtils.create());
                        x.setBuildingId(buildingIdMap.get(x.getBuildingId()));
                        x.setProjectPlanCardId(projectPlanCardIdMap.get(x.getProjectPlanCardId()));
                        x.setCreaterId(currentUserVO.getId());
                        x.setCreateTime(LocalDateTime.now());
                        x.setCreaterName(currentUserVO.getName());
                        x.setUpdaterId(null);
                        x.setUpdateTime(null);
                        x.setUpdaterName(null);
                    });

                    boProjectPlanCardBuildingMapService.saveBatch(boProjectPlanCardBuildingList, 100);
                }
            }
        }
        return newVersion;
    }

	@Override
	public BoProjectQuotaExtend getAreaVersionInfos(String projectPriceExtendId) {
		return boProjectQuotaExtendMapper.getAreaVersionInfos(projectPriceExtendId);
	}

}
