package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.enums.*;
import com.tahoecn.bo.common.utils.BusinessObjectUtils;
import com.tahoecn.bo.common.utils.JsonResultBuilder;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.common.utils.VersionUtils;
import com.tahoecn.bo.mapper.BoProjectPriceExtendMapper;
import com.tahoecn.bo.model.bo.BpmBusinessInfoBo;
import com.tahoecn.bo.model.entity.*;
import com.tahoecn.bo.model.vo.CurrentUserVO;
import com.tahoecn.bo.service.*;
import com.tahoecn.core.json.JSONResult;
import com.tahoecn.security.SecureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 项目分期价格扩展信息表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class BoProjectPriceExtendServiceImpl extends ServiceImpl<BoProjectPriceExtendMapper, BoProjectPriceExtend> implements BoProjectPriceExtendService {

    @Autowired
    private BoQuotaGroupMapService boQuotaGroupMapService;

    @Autowired
    private BoProjectPriceQuotaMapService boProjectPriceQuotaMapService;

    @Autowired
    private BoLandPartProductTypeMapService boLandPartProductTypeMapService;

    @Autowired
    private BoBuildingProductTypeMapService boBuildingProductTypeMapService;

    @Override
    public BoProjectPriceExtend getLastVersion(String projectId, String stageCode) {
        Page<BoProjectPriceExtend> page = new Page<>(1, 1);
        QueryWrapper<BoProjectPriceExtend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId)
                .eq("stage_code", stageCode)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .orderByDesc("create_time");
        IPage<BoProjectPriceExtend> iPage = baseMapper.selectPage(page, queryWrapper);
        return iPage.getRecords().isEmpty() ? null : iPage.getRecords().get(0);
    }

    @Override
    public BoProjectPriceExtend getLastPassedVersion(String projectId, String stageCode) {
        Page<BoProjectPriceExtend> page = new Page<>(1, 1);
        QueryWrapper<BoProjectPriceExtend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId)
                .eq("stage_code", stageCode)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .eq("version_status", VersionStatusEnum.PASSED.getKey())
                .orderByDesc("create_time");
        IPage<BoProjectPriceExtend> iPage = baseMapper.selectPage(page, queryWrapper);
        return iPage.getRecords().isEmpty() ? null : iPage.getRecords().get(0);
    }

    @Override
    public BoProjectPriceExtend getLastPassedVersion(String projectId, Collection<String> stageCodes, LocalDateTime endTime) {
        QueryWrapper<BoProjectPriceExtend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId)
                .in("stage_code", stageCodes)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .eq("version_status", VersionStatusEnum.PASSED.getKey())
                .lt("create_time",endTime)
                .orderByDesc("create_time")
                .last("limit 1");
        List<BoProjectPriceExtend> boProjectPriceExtends = baseMapper.selectList(queryWrapper);
        return boProjectPriceExtends.isEmpty() ? null : boProjectPriceExtends.get(0);
    }

    @Override
    public boolean hasCanEditData(String projectId, String stageCode) {
        QueryWrapper<BoProjectPriceExtend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId)
                .eq("stage_code", stageCode)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .in("version_status", VersionStatusEnum.CHECKING.getKey(),
                        VersionStatusEnum.CREATING.getKey(),
                        VersionStatusEnum.REJECTED.getKey());
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public BoProjectPriceExtend createVersion(String projectId, String projectQuotaExtendId, String stageCode, CurrentUserVO user) {
        //查最新价格审批通过版本，用于版本号递增
        BoProjectPriceExtend lastVersion = getLastPassedVersion(projectId, stageCode);

        //项目分期价格扩展信息表 - 新增
        BoProjectPriceExtend firstVersion = new BoProjectPriceExtend();
        firstVersion.setId(UUIDUtils.create());
        firstVersion.setProjectQuotaExtendId(projectQuotaExtendId);
        firstVersion.setQuotaGroupCode(QuotaGroupCodeEnum.PROJECT_PRICE.getKey());
        firstVersion.setStageCode(stageCode);
        firstVersion.setStageName(StageCodeEnum.getByKey(stageCode).getValue());
        firstVersion.setProjectId(projectId);
        firstVersion.setStageCode(stageCode);
        firstVersion.setApproveEndTime(null);
        firstVersion.setApproveId(null);
        firstVersion.setApproveStartTime(null);
        firstVersion.setVersion(VersionUtils.bigVersionInc(lastVersion == null ? null : lastVersion.getVersion()));
        firstVersion.setVersionStatus(VersionStatusEnum.CREATING.getKey());
        firstVersion.setCreateTime(LocalDateTime.now());
        firstVersion.setCreaterId(user.getId());
        firstVersion.setCreaterName(user.getName());
        baseMapper.insert(firstVersion);

        //价格指标关系表 - 新增
        List<BoQuotaGroupMap> quotaGroupMapList = boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.PROJECT_PRICE.getKey());
        List<BoProjectPriceQuotaMap> boProjectPriceQuotaMapList = new ArrayList<>();
        if (StageCodeEnum.getByKey(stageCode) == StageCodeEnum.STAGE_01) {
            List<BoLandPartProductTypeMap> landPartProductTypeList = boLandPartProductTypeMapService.getLandPartProductTypeList(projectQuotaExtendId);
            if (!landPartProductTypeList.isEmpty() && !quotaGroupMapList.isEmpty()) {
                //地块业态唯一
                Set<String> queryLandPartProductTypeSet = new HashSet<>();
                landPartProductTypeList.stream().forEach(x -> {
                    String key = x.getLandPartId() + x.getProductTypeCode();
                    if (!queryLandPartProductTypeSet.contains(key)) {
                        queryLandPartProductTypeSet.add(key);
                        quotaGroupMapList.stream().forEach(y -> {
                            makeQuotaPrice(user, boProjectPriceQuotaMapList, firstVersion.getId(), SecureUtil.md5(key), y.getCode(), y.getId(), y.getQuotaId(), y.getValueType());
                        });
                    }
                });
            }
        } else {
            List<BoBuildingProductTypeMap> buildingProductTypeList = boBuildingProductTypeMapService.getBuildingProductTypeList(projectQuotaExtendId);
            if (!buildingProductTypeList.isEmpty() && !quotaGroupMapList.isEmpty()) {
                //业态唯一
                Set<String> queryProductTypeSet = new HashSet<>();
                buildingProductTypeList.stream().forEach(x -> {
                    if (!queryProductTypeSet.contains(x.getProductTypeCode())) {
                        queryProductTypeSet.add(x.getProductTypeCode());
                        quotaGroupMapList.stream().forEach(y -> {
                            makeQuotaPrice(user, boProjectPriceQuotaMapList, firstVersion.getId(), SecureUtil.md5(x.getProductTypeCode()), y.getCode(), y.getId(), y.getQuotaId(), y.getValueType());
                        });
                    }
                });
            }
        }
        if (!boProjectPriceQuotaMapList.isEmpty()) {
            boProjectPriceQuotaMapService.saveBatch(boProjectPriceQuotaMapList, 100);
        }
        return firstVersion;
    }

    @Override
    public boolean updateForClearApproveData(BoProjectPriceExtend boProjectPriceExtend) {
        boProjectPriceExtend.setVersionStatus(VersionStatusEnum.CREATING.getKey());
        return baseMapper.updateForClearApproveData(boProjectPriceExtend) > 0;
    }

    @Override
    public BpmBusinessInfoBo getPreApproveInfo(String versionId) {
        QueryWrapper<BoProjectPriceExtend> queryWrapper = new QueryWrapper<>();
        //将stageName 赋值为 项目/分期的组合名称
        queryWrapper.select(
                "project_id",
                "stage_code",
                "version",
                "(select case bo_project_price_extend.stage_code when '" + StageCodeEnum.STAGE_01.getKey() + "' then fd_name else concat(fd_pname, fd_name) end from uc_org a where a.fd_sid = bo_project_price_extend.project_id limit 1 ) as stage_name"
        ).eq("id", versionId);
        BoProjectPriceExtend boProjectPriceExtend = getOne(queryWrapper);
        if (boProjectPriceExtend == null) {
            return null;
        }
        StageCodeEnum stageCodeEnum = StageCodeEnum.getByKey(boProjectPriceExtend.getStageCode());
        String versionDesc = stageCodeEnum.getValue() + boProjectPriceExtend.getVersion();
        BpmBusinessInfoBo bpmBusinessInfoBo = new BpmBusinessInfoBo();
        bpmBusinessInfoBo.setDocSubject("请审批[" + boProjectPriceExtend.getStageName() + versionDesc + "]价格审批");
        bpmBusinessInfoBo.setProjectId(boProjectPriceExtend.getProjectId());
        bpmBusinessInfoBo.setVersionDesc(versionDesc);
        return bpmBusinessInfoBo;
    }

    @Override
    public JSONResult priceValidateApprove(String versionId) {
        BoProjectPriceExtend boProjectPriceExtend = getById(versionId);
        if (boProjectPriceExtend == null) {
            return JsonResultBuilder.failed("版本不存在");
        }
        VersionStatusEnum versionStatusEnum = VersionStatusEnum.getByKey(boProjectPriceExtend.getVersionStatus());
        if (versionStatusEnum != VersionStatusEnum.CREATING && versionStatusEnum != VersionStatusEnum.REJECTED) {
            return JsonResultBuilder.failed("当前版本不可发起审批");
        }
        return JsonResultBuilder.success();
    }

    private void makeQuotaPrice(CurrentUserVO user, List<BoProjectPriceQuotaMap> boProjectPriceQuotaMapList, String projectPriceExtendId, String refId, String quotaCode, String quotaGroupMapId, String quotaId, String valueType) {
        BoProjectPriceQuotaMap boProjectPriceQuotaMap = new BoProjectPriceQuotaMap();
        boProjectPriceQuotaMap.setId(UUIDUtils.create());
        boProjectPriceQuotaMap.setCreaterId(user.getId());
        boProjectPriceQuotaMap.setCreaterName(user.getName());
        boProjectPriceQuotaMap.setCreateTime(LocalDateTime.now());
        boProjectPriceQuotaMap.setProjectPriceExtendId(projectPriceExtendId);
        boProjectPriceQuotaMap.setRefId(refId);

        boProjectPriceQuotaMap.setQuotaCode(quotaCode);
        boProjectPriceQuotaMap.setQuotaGroupMapId(quotaGroupMapId);
        boProjectPriceQuotaMap.setQuotaId(quotaId);
        boProjectPriceQuotaMapList.add(boProjectPriceQuotaMap);
        BusinessObjectUtils.quotaValueSetDefaultForObject(valueType, boProjectPriceQuotaMap);
    }

}
