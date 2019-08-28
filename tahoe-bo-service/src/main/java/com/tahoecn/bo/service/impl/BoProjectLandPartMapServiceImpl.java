package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.enums.*;
import com.tahoecn.bo.common.utils.Constants;
import com.tahoecn.bo.common.utils.DataUtils;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.common.utils.VOUtils;
import com.tahoecn.bo.mapper.*;
import com.tahoecn.bo.model.dto.*;
import com.tahoecn.bo.model.entity.*;
import com.tahoecn.bo.model.vo.*;
import com.tahoecn.bo.model.vo.reqvo.*;
import com.tahoecn.bo.service.*;
import com.tahoecn.core.json.JSONResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
/**
 * <p>
 * 项目分期地块数据表/映射表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class BoProjectLandPartMapServiceImpl extends ServiceImpl<BoProjectLandPartMapMapper, BoProjectLandPartMap> implements BoProjectLandPartMapService {

    @Autowired
    private BoProjectLandPartMapMapper projectLandPartMapMapper;

    @Autowired
    private BoProjectExtendMapper projectExtendMapper;

    @Autowired
    private MdmProjectInfoMapper projectInfoMapper;

    @Autowired
    private TutouThLandinformationTbMapper landinformationTbMapper;

    @Autowired
    private BoLandPartProductTypeMapMapper landPartProductTypeMapMapper;

    @Autowired
    private BoProjectQuotaExtendMapper projectQuotaExtendMapper;

    @Autowired
    private BoProjectLandPartQuotaMapMapper projectLandPartQuotaMapMapper;

    @Autowired
    private BoLandPartProductTypeQuotaMapMapper landPartProductTypeQuotaMapMapper;

    @Autowired
    private BoProjectQuotaMapMapper projectQuotaMapMapper;

    @Autowired
    private BoProjectLandPartProductTypeMapMapper projectLandPartProductTypeMapMapper;

    @Autowired
    private BoProjectLandPartProductTypeQuotaMapMapper projectLandPartProductTypeQuotaMapMapper;

    @Autowired
    private BoQuotaGroupMapMapper quotaGroupMapMapper;

    @Autowired
    private BoProjectLandPartProductTypeMapService projectLandPartProductTypeMapService;

    @Autowired
    private BoProjectLandPartProductTypeQuotaMapService projectLandPartProductTypeQuotaMapService;

    @Autowired
    private BoQuotaGroupMapService boQuotaGroupMapService;

    @Autowired
    private BoProductTypeService boProductTypeService;

    @Override
    public JSONResult selectLandInfoByProjectId(String projectId, String version, String isAdopt, JSONResult jsonResult) {

        BoProjectExtend boProjectExtend = projectExtendMapper.selectByProjectId(projectId, version, isAdopt);
        if (boProjectExtend != null) {
            List<LandQuotaVo> landDetailsList = projectLandPartMapMapper.selectLandInfoByProjectId(boProjectExtend.getId());
            if(DataUtils.isNotEmpty(landDetailsList)){
                jsonResult.setData(landDetailsList);
            }else{
                jsonResult.setData(new ArrayList<LandQuotaVo>());
            }

            jsonResult.setCode(CodeEnum.SUCCESS.getKey());
            jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
        } else {
            jsonResult.setCode(CodeEnum.PROJECT_EXTENDS_INFO.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_EXTENDS_INFO.getValue());
        }

        return jsonResult;
    }

    @Override
    public JSONResult selectLandInfoBySubProjectId(String subProjectId, String version, String isAdopt, JSONResult jsonResult) {

        BoProjectExtend boProjectExtend = projectExtendMapper.selectByProjectId(subProjectId, version, isAdopt);
        if (boProjectExtend != null) {
            List<SubLandQuotaVo> landDetailsList = projectLandPartMapMapper.selectLandInfoBySubProjectId(boProjectExtend.getId());

            jsonResult.setData(landDetailsList);
            jsonResult.setCode(CodeEnum.SUCCESS.getKey());
            jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
        } else {
            jsonResult.setCode(CodeEnum.PROJECT_EXTENDS_INFO.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_EXTENDS_INFO.getValue());
        }

        return jsonResult;
    }

    @Override
    public JSONResult selectCanUseLandBySubProject(String subProjectId, JSONResult jsonResult) {

        List<MdmProjectInfo> subProjectInfos = projectInfoMapper.selectSubProjectBySubId(subProjectId);
        List<String> subIds = Optional.ofNullable(subProjectInfos)
                .orElseGet(ArrayList::new)
                .stream().map(MdmProjectInfo::getSid)
                .collect(Collectors.toList());

        List<SubProTotalQuoteValueDto> proTotalQuoteValueDtos = landPartProductTypeMapMapper.selectTotalQuoteByProjectId(subProjectInfos.get(0).getParentSid());

        List<String> notDevLandPartIds = new ArrayList<String>();
        List<BoProjectLandPartMap> notDevProjectLandPartMaps = projectLandPartMapMapper.selectNotDevLandIdsBySubIds(subIds);
        if(notDevProjectLandPartMaps != null && notDevProjectLandPartMaps.size() > 0){
            for (int i = 0; i < notDevProjectLandPartMaps.size(); i++){
                BoProjectLandPartMap boProjectLandPartMap = notDevProjectLandPartMaps.get(i);
                if(!notDevLandPartIds.contains(boProjectLandPartMap.getLandPartId())){
                    notDevLandPartIds.add(boProjectLandPartMap.getLandPartId());
                }
            }
        }

        List<String> useLandIds = new ArrayList<String>();
        List<BoProjectLandPartMap> boProjectLandPartMaps = projectLandPartMapMapper.selectByUseLandBySubProId(subProjectId);
        if(boProjectLandPartMaps != null && boProjectLandPartMaps.size() > 0){
            for (int i = 0; i < boProjectLandPartMaps.size(); i++){
                notDevLandPartIds.remove(boProjectLandPartMaps.get(i).getLandPartId());
                useLandIds.add(boProjectLandPartMaps.get(i).getLandPartId());
            }
        }

        List<SubProTotalQuoteValueDto> subProTotalQuoteValueDtos = new ArrayList<SubProTotalQuoteValueDto>();
        if(CollectionUtils.isNotEmpty(notDevLandPartIds)){
            subProTotalQuoteValueDtos = projectLandPartProductTypeQuotaMapMapper.selectTotalQuoteByLandIds(notDevLandPartIds);
        }

        Set<LandBasicInfoVo> landBasicInfoVos = new HashSet<LandBasicInfoVo>();
        if(proTotalQuoteValueDtos != null && proTotalQuoteValueDtos.size() > 0){
            for (int i = 0; i < proTotalQuoteValueDtos.size(); i++) {
                SubProTotalQuoteValueDto proTotalQuoteValueDto = proTotalQuoteValueDtos.get(i);
                String landLandId = proTotalQuoteValueDto.getLandId();
                String landproductCode = proTotalQuoteValueDto.getProductCode();
                String landquoteCode = proTotalQuoteValueDto.getQuoteCode();
                String landsumQuoteValue = proTotalQuoteValueDto.getSumQuoteValue();
                String landAttrQuotaValue = proTotalQuoteValueDto.getAttrQuotaValue();
                String landCode = landLandId + "&" + landAttrQuotaValue + "&" + landproductCode + "&" + landquoteCode;

                if(subProTotalQuoteValueDtos != null && subProTotalQuoteValueDtos.size() > 0){
                    for (int j = 0; j < subProTotalQuoteValueDtos.size(); j++) {
                        boolean index = false;
                        SubProTotalQuoteValueDto subProTotalQuoteValueDto = subProTotalQuoteValueDtos.get(j);
                        String sublandId = subProTotalQuoteValueDto.getLandId();
                        String subproductCode = subProTotalQuoteValueDto.getProductCode();
                        String subquoteCode = subProTotalQuoteValueDto.getQuoteCode();
                        String subsumQuoteValue = subProTotalQuoteValueDto.getSumQuoteValue();
                        String subAttrQuotaValue = subProTotalQuoteValueDto.getAttrQuotaValue();
                        String subCode = sublandId + "&" + subAttrQuotaValue +  "&" + subproductCode + "&" + subquoteCode;

                        if (landCode.equals(subCode)) {
                            index = true;
                            String sub = sub(Double.parseDouble(!StringUtils.isNotBlank(landsumQuoteValue)?"0":landsumQuoteValue), Double.parseDouble(!StringUtils.isNotBlank(subsumQuoteValue)?"0":subsumQuoteValue));
                            if (Double.parseDouble(StringUtils.isBlank(sub)?"0":sub) > 0d) {
                                LandBasicInfoVo landBasicInfoVo = new LandBasicInfoVo();
                                landBasicInfoVo.setLandName(subProTotalQuoteValueDto.getLandName());
                                landBasicInfoVo.setLandId(subProTotalQuoteValueDto.getLandId());
                                landBasicInfoVos.add(landBasicInfoVo);
                                break;
                            }
                        }

                        if(j < subProTotalQuoteValueDtos.size() - 1 && !index){
                            LandBasicInfoVo landBasicInfoVo = new LandBasicInfoVo();
                            landBasicInfoVo.setLandName(proTotalQuoteValueDto.getLandName());
                            landBasicInfoVo.setLandId(proTotalQuoteValueDto.getLandId());
                            landBasicInfoVos.add(landBasicInfoVo);
                        }
                    }
                }else{
                    LandBasicInfoVo landBasicInfoVo = new LandBasicInfoVo();
                    landBasicInfoVo.setLandName(proTotalQuoteValueDto.getLandName());
                    landBasicInfoVo.setLandId(proTotalQuoteValueDto.getLandId());
                    landBasicInfoVos.add(landBasicInfoVo);
                }
            }
        }

        Set<LandBasicInfoVo> result = new HashSet<LandBasicInfoVo>();
        if(landBasicInfoVos != null && landBasicInfoVos.size() > 0){
            for (LandBasicInfoVo landBasicInfoVo : landBasicInfoVos){
                String landId = landBasicInfoVo.getLandId();
                if(!useLandIds.contains(landId)){
                    result.add(landBasicInfoVo);
                }
            }
        }

        jsonResult.setData(result);
        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
        return jsonResult;

        /*List<BoProjectLandPartMap> notDevProjectLandPartMaps = projectLandPartMapMapper.selectNotDevLandIdsBySubIds(subIds);
        if (notDevProjectLandPartMaps != null && notDevProjectLandPartMaps.size() > 0) {

            List<String> notDevLandPartIds = Optional.ofNullable(notDevProjectLandPartMaps)
                    .orElseGet(ArrayList::new)
                    .stream().map(BoProjectLandPartMap::getId)
                    .collect(Collectors.toList());

            List<SubProTotalQuoteValueDto> proTotalQuoteValueDtos = landPartProductTypeMapMapper.selectTotalQuoteByProjectId(subProjectInfos.get(0).getParentSid());
            List<SubProTotalQuoteValueDto> subProTotalQuoteValueDtos = projectLandPartProductTypeQuotaMapMapper.selectTotalQuoteByLandIds(notDevLandPartIds);

            Set<String> landBasicSet = new HashSet<String>();
            Set<String> subBasicSet = new HashSet<String>();
            Set<LandBasicInfoVo> landBasicInfoVos = new HashSet<LandBasicInfoVo>();
            for (int i = 0; i < proTotalQuoteValueDtos.size(); i++) {
                SubProTotalQuoteValueDto proTotalQuoteValueDto = proTotalQuoteValueDtos.get(i);
                String landLandId = proTotalQuoteValueDto.getLandId();
                String landproductCode = proTotalQuoteValueDto.getProductCode();
                String landquoteCode = proTotalQuoteValueDto.getQuoteCode();
                String landsumQuoteValue = proTotalQuoteValueDto.getSumQuoteValue();
                String landAttrQuotaValue = proTotalQuoteValueDto.getAttrQuotaValue();
                String landCode = landLandId + "&" + landAttrQuotaValue + "&" + landproductCode + "&" + landquoteCode;
                landBasicSet.add(landLandId + "&" + proTotalQuoteValueDto.getLandName());

                if(subProTotalQuoteValueDtos != null && subProTotalQuoteValueDtos.size() > 0){
                    for (int j = 0; j < subProTotalQuoteValueDtos.size(); j++) {
                        boolean index = false;
                        SubProTotalQuoteValueDto subProTotalQuoteValueDto = subProTotalQuoteValueDtos.get(j);
                        String sublandId = subProTotalQuoteValueDto.getLandId();
                        String subproductCode = subProTotalQuoteValueDto.getProductCode();
                        String subquoteCode = subProTotalQuoteValueDto.getQuoteCode();
                        String subsumQuoteValue = subProTotalQuoteValueDto.getSumQuoteValue();
                        String subAttrQuotaValue = subProTotalQuoteValueDto.getAttrQuotaValue();
                        String subCode = sublandId + "&" + subAttrQuotaValue +  "&" + subproductCode + "&" + subquoteCode;
                        subBasicSet.add(sublandId + "&" + subProTotalQuoteValueDto.getLandName());

                        if (landCode.equals(subCode)) {
                            index = true;
                            double sub = sub(Double.parseDouble(!StringUtils.isNotBlank(landsumQuoteValue)?"0":landsumQuoteValue), Double.parseDouble(!StringUtils.isNotBlank(subsumQuoteValue)?"0":subsumQuoteValue));
                            if (sub > 0d) {
                                LandBasicInfoVo landBasicInfoVo = new LandBasicInfoVo();
                                landBasicInfoVo.setLandName(subProTotalQuoteValueDto.getLandName());
                                landBasicInfoVo.setLandId(subProTotalQuoteValueDto.getLandId());
                                landBasicInfoVos.add(landBasicInfoVo);
                                break;
                            }
                        }

                        if(j < subProTotalQuoteValueDtos.size() - 1 && !index){
                            LandBasicInfoVo landBasicInfoVo = new LandBasicInfoVo();
                            landBasicInfoVo.setLandName(proTotalQuoteValueDto.getLandName());
                            landBasicInfoVo.setLandId(proTotalQuoteValueDto.getLandId());
                            landBasicInfoVos.add(landBasicInfoVo);
                        }
                    }
                }else{
                    LandBasicInfoVo landBasicInfoVo = new LandBasicInfoVo();
                    landBasicInfoVo.setLandName(proTotalQuoteValueDto.getLandName());
                    landBasicInfoVo.setLandId(proTotalQuoteValueDto.getLandId());
                    landBasicInfoVos.add(landBasicInfoVo);
                    break;
                }
            }

            *//*Set<String> result = new HashSet<String>();
            result.clear();
            result.addAll(landBasicSet);
            result.removeAll(subBasicSet);*//*

            List<BoProjectLandPartMap> boProjectLandPartMaps = projectLandPartMapMapper.selectByUseLandBySubProId(subProjectId);
            if(boProjectLandPartMaps != null && boProjectLandPartMaps.size() > 0){
                for (int i = 0;i < boProjectLandPartMaps.size(); i++){
                    BoProjectLandPartMap boProjectLandPartMap = boProjectLandPartMaps.get(i);
                    String useStr = boProjectLandPartMap.getLandPartId() + "&" + boProjectLandPartMap.getLandPartName();
                    landBasicInfoVos.remove(useStr);
                }
            }

            *//*List<LandBasicInfoVo> landBasicInfos = new ArrayList<LandBasicInfoVo>();
            for (String str : result) {
                String[] split = str.split("&");
                LandBasicInfoVo landBasicInfoVo = new LandBasicInfoVo();
                landBasicInfoVo.setLandName(split[1]);
                landBasicInfoVo.setLandId(split[0]);
                landBasicInfos.add(landBasicInfoVo);
            }*//*

            jsonResult.setData(landBasicInfoVos);
        } else {
           *//* List<BoProjectLandPartMap> boProjectLandPartMaps = projectLandPartMapMapper.selectByUseLandByProId(subProjectInfos.get(0).getParentSid());
            List<String> landIds = Optional.ofNullable(boProjectLandPartMaps)
                    .orElseGet(ArrayList::new)
                    .stream().map(BoProjectLandPartMap::getLandPartId)
                    .collect(Collectors.toList());*//*

            Set<LandBasicInfoVo> lanInfoVos = new HashSet<LandBasicInfoVo>();
            List<LandBasicInfoVo> landBasicInfoVos = landPartProductTypeMapMapper.selectLandInfoByProId(subProjectInfos.get(0).getParentSid());
            List<String> isDevLandIds = landPartProductTypeMapMapper.selectIsDevLandBySubIds(subIds);

            for (int j = 0; j < landBasicInfoVos.size(); j++) {
                LandBasicInfoVo landBasicInfoVo = landBasicInfoVos.get(j);
                String landId = landBasicInfoVo.getLandId();
                if (!isDevLandIds.contains(landId)) {
                    lanInfoVos.add(landBasicInfoVo);
                }
            }

            jsonResult.setData(lanInfoVos);
        }

        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
        return jsonResult;*/
    }

    public String sub(double money, double price) {
        BigDecimal b1 = new BigDecimal(money).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal b2 = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP);
        String doubleValue = b1.subtract(b2).toString();
        return doubleValue;
    }

    public String add(double money, double price) {
        BigDecimal b1 = new BigDecimal(money).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal b2 = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP);
        String doubleValue = b1.add(b2).toString();
        return doubleValue;
    }

    public String addToString(String money, String price) {
        BigDecimal b1 = new BigDecimal(money).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal b2 = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP);
        String doubleValue = b1.add(b2).toString();
        return doubleValue;
    }

    public String subToString(String money, String price) {
        BigDecimal b1 = new BigDecimal(money).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal b2 = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP);
        String doubleValue = b1.subtract(b2).toString();
        return doubleValue;
    }

    @Override
    @Transactional
    public JSONResult addLandToSubProject(SubProjectAddLandReqParam subProjectAddLandReqParam, CurrentUserVO userVO, JSONResult jsonResult) {

        SubProjectAndExendInfoDto subProjectAndExendInfoDto = projectInfoMapper.selectSubProjectExtendIdBySubProjectId(subProjectAddLandReqParam.getSubProjectId());
        if (subProjectAndExendInfoDto != null) {
            Integer versionStatus = subProjectAndExendInfoDto.getVersionStatus();
            if (versionStatus != null && (versionStatus.equals(VersionStatusEnum.CHECKING.getKey()) || versionStatus.equals(VersionStatusEnum.PASSED.getKey()))) {
                jsonResult.setCode(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getKey());
                jsonResult.setMsg(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getValue());
                return jsonResult;
            }
        }

        Map<String, BoQuotaGroupMap> formatMap = new HashMap<String, BoQuotaGroupMap>();
        QueryWrapper<BoQuotaGroupMap> queryTypeQuotaWrapper = new QueryWrapper<BoQuotaGroupMap>();
        queryTypeQuotaWrapper.eq("group_code", "LAND_PART_PRODUCT_TYPE")
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        List<BoQuotaGroupMap> boQuotaGroupMaps = quotaGroupMapMapper.selectList(queryTypeQuotaWrapper);
        if(CollectionUtils.isNotEmpty(boQuotaGroupMaps)){
            for (int i = 0; i < boQuotaGroupMaps.size(); i++){
                BoQuotaGroupMap boQuotaGroupMap = boQuotaGroupMaps.get(i);
                formatMap.put(boQuotaGroupMap.getCode(), boQuotaGroupMap);
            }
        }

        List<BoProjectLandPartMap> boProjectLandPartMaps = projectLandPartMapMapper.selectByUseLandBySubProId(subProjectAddLandReqParam.getSubProjectId());
        if(boProjectLandPartMaps != null && boProjectLandPartMaps.size() > 0){
            List<String> useLandIds = Optional.ofNullable(boProjectLandPartMaps)
                    .orElseGet(ArrayList::new)
                    .stream().map(BoProjectLandPartMap::getLandPartId)
                    .collect(Collectors.toList());
            if(useLandIds != null && useLandIds.size() > 0){
                if(useLandIds.contains(subProjectAddLandReqParam.getLandId())){
                    jsonResult.setCode(CodeEnum.SUB_PROJECT_CREATE_LAND_EXTENDS.getKey());
                    jsonResult.setMsg(CodeEnum.SUB_PROJECT_CREATE_LAND_EXTENDS.getValue());
                    return jsonResult;
                }
            }
        }

        List<MdmProjectInfo> subProjectInfos = projectInfoMapper.selectSubProjectBySubId(subProjectAddLandReqParam.getSubProjectId());
        List<String> subIds = new ArrayList<String>();
        MdmProjectInfo subProjectInfo = null;
        for (int i = 0; i < subProjectInfos.size(); i++) {
            MdmProjectInfo mdmProjectInfo = subProjectInfos.get(i);
            String sid = mdmProjectInfo.getSid();
            subIds.add(sid);
            if (subProjectAddLandReqParam.getSubProjectId().equals(sid)) {
                subProjectInfo = mdmProjectInfo;
            }
        }
        SubProjectInfoDto subProjectInfoDto = projectInfoMapper.selectProjectBySubProjectId(subProjectAddLandReqParam.getSubProjectId(), null);

        Integer isDev = subProjectAddLandReqParam.getIsDev();
        if (isDev != null && isDev.equals(IsDevEnum.YES.getKey())) {

            //查看分期指标是否被使用
            List<ProjectProductTypeQuotaDto> projectProductTypeQuotaDtos = projectLandPartProductTypeQuotaMapMapper.selectTotalQuotaBySubProjectAndLandIds(subIds, subProjectAddLandReqParam.getLandId());
            if (projectProductTypeQuotaDtos != null && projectProductTypeQuotaDtos.size() > 0) {
                jsonResult.setCode(CodeEnum.SUB_PROJECT_LAND_EXTENDS.getKey());
                jsonResult.setMsg(CodeEnum.SUB_PROJECT_LAND_EXTENDS.getValue());
                return jsonResult;
            }

            //bo_project_land_part_map
            BoProjectLandPartMap boProjectLandPartMap = projectLandPartMapMapper.selectProjectLandBySubProjectIdAndLandId(subProjectInfos.get(0).getParentSid(), subProjectAddLandReqParam.getLandId());
            String landPartId = UUIDUtils.create();
            if (boProjectLandPartMap != null) {
                BoProjectLandPartMap subBoProjectLandPartMap = new BoProjectLandPartMap();
                subBoProjectLandPartMap.setId(landPartId);
                subBoProjectLandPartMap.setLandPartId(subProjectAddLandReqParam.getLandId());
                subBoProjectLandPartMap.setProjectExtendId(subProjectInfoDto.getExtendId());
                subBoProjectLandPartMap.setLandPartName(boProjectLandPartMap.getLandPartName());
                subBoProjectLandPartMap.setIsAllDev(isDev);
                subBoProjectLandPartMap.setTotalUseLandArea(boProjectLandPartMap.getTotalUseLandArea());
                subBoProjectLandPartMap.setCapacityBuildingArea(boProjectLandPartMap.getCapacityBuildingArea());
                subBoProjectLandPartMap.setCanUseLandArea(boProjectLandPartMap.getCanUseLandArea());
                subBoProjectLandPartMap.setBuildingLandCoverArea(boProjectLandPartMap.getBuildingLandCoverArea());
                subBoProjectLandPartMap.setTakeByLandUseArea(boProjectLandPartMap.getTakeByLandUseArea());
                subBoProjectLandPartMap.setLandGetPrice(boProjectLandPartMap.getLandGetPrice());
                subBoProjectLandPartMap.setAboveGroundCalcVolumeArea(boProjectLandPartMap.getAboveGroundCalcVolumeArea());
                subBoProjectLandPartMap.setUnderGroundCalcVolumeArea(boProjectLandPartMap.getUnderGroundCalcVolumeArea());
                subBoProjectLandPartMap.setRefProjectId(subProjectInfo.getSid());
                subBoProjectLandPartMap.setRefProjectName(subProjectInfo.getName());
                subBoProjectLandPartMap.setCreaterId(userVO.getId());
                subBoProjectLandPartMap.setCreaterName(userVO.getName());
                subBoProjectLandPartMap.setCreateTime(LocalDateTime.now());
                subBoProjectLandPartMap.setIsDelete(IsDeleteEnum.NO.getKey());
                subBoProjectLandPartMap.setIsDisable(IsDisableEnum.NO.getKey());

                projectLandPartMapMapper.insert(subBoProjectLandPartMap);
            }

            //根据分期地块查询项目投决会使用的业态信息
            Map<String, String> productMap = new HashMap<String, String>();
            List<String> landProductIds = new ArrayList<String>();
            List<BoLandPartProductTypeMapExtendDto> landPartProductTypeMaps = projectLandPartProductTypeMapMapper.selectLandProductByProjectId(subProjectInfos.get(0).getParentSid(), subProjectAddLandReqParam.getLandId());
            if (landPartProductTypeMaps != null && landPartProductTypeMaps.size() > 0) {

                List<BoProjectLandPartProductTypeMap> insertProjectLandPartProductTypeMapList = new ArrayList<BoProjectLandPartProductTypeMap>();
                List<BoProjectLandPartProductTypeQuotaMap> insertProjectLandPartProductTypeQuotaMapList = new ArrayList<BoProjectLandPartProductTypeQuotaMap>();
                for (int i = 0; i < landPartProductTypeMaps.size(); i++) {
                    BoLandPartProductTypeMapExtendDto boLandPartProductTypeMapExtendDto = landPartProductTypeMaps.get(i);
                    LocalDateTime time = LocalDateTime.now();

                    BoProjectLandPartProductTypeMap projectLandPartProductTypeMap = new BoProjectLandPartProductTypeMap();
                    String id = UUIDUtils.create();
                    projectLandPartProductTypeMap.setId(id);
                    projectLandPartProductTypeMap.setProjectLandPartId(landPartId);
                    projectLandPartProductTypeMap.setProductTypeId(boLandPartProductTypeMapExtendDto.getProductTypeId());
                    projectLandPartProductTypeMap.setProductTypeCode(boLandPartProductTypeMapExtendDto.getProductTypeCode());
                    projectLandPartProductTypeMap.setProductTypeName(boLandPartProductTypeMapExtendDto.getProductTypeName());
                    projectLandPartProductTypeMap.setIsDelete(IsDeleteEnum.NO.getKey());
                    projectLandPartProductTypeMap.setIsDisable(IsDisableEnum.NO.getKey());
                    projectLandPartProductTypeMap.setCreateTime(time);
                    projectLandPartProductTypeMap.setCreaterId(userVO.getId());
                    projectLandPartProductTypeMap.setCreaterName(userVO.getName());
                    projectLandPartProductTypeMap.setPropertyRightAttr(boLandPartProductTypeMapExtendDto.getAttrQuotaValue());
                    //projectLandPartProductTypeMapMapper.insert(projectLandPartProductTypeMap);
                    insertProjectLandPartProductTypeMapList.add(projectLandPartProductTypeMap);

                    landProductIds.add(boLandPartProductTypeMapExtendDto.getId());
                    productMap.put(boLandPartProductTypeMapExtendDto.getId(), id);
                }

                if (landProductIds != null && landProductIds.size() > 0) {
                    List<BoLandPartProductTypeQuotaMap> landPartProductTypeQuotaMaps = projectLandPartProductTypeQuotaMapMapper.selectLandQuoteByProductIds(landProductIds);
                    if (landPartProductTypeQuotaMaps != null && landPartProductTypeQuotaMaps.size() > 0) {
                        for (int i = 0; i < landPartProductTypeQuotaMaps.size(); i++) {
                            BoLandPartProductTypeQuotaMap boLandPartProductTypeQuotaMap = landPartProductTypeQuotaMaps.get(i);

                            BoProjectLandPartProductTypeQuotaMap projectLandPartProductTypeQuotaMap = new BoProjectLandPartProductTypeQuotaMap();
                            projectLandPartProductTypeQuotaMap.setId(UUIDUtils.create());
                            projectLandPartProductTypeQuotaMap.setProjectLandPartProductTypeId(productMap.get(boLandPartProductTypeQuotaMap.getLandPartProductTypeId()));
                            projectLandPartProductTypeQuotaMap.setQuotaGroupMapId(boLandPartProductTypeQuotaMap.getQuotaGroupMapId());
                            projectLandPartProductTypeQuotaMap.setQuotaId(boLandPartProductTypeQuotaMap.getQuotaId());
                            projectLandPartProductTypeQuotaMap.setQuotaCode(boLandPartProductTypeQuotaMap.getQuotaCode());
                            projectLandPartProductTypeQuotaMap.setQuotaValue(boLandPartProductTypeQuotaMap.getQuotaValue());
                            projectLandPartProductTypeQuotaMap.setIsDelete(IsDeleteEnum.NO.getKey());
                            projectLandPartProductTypeQuotaMap.setIsDisable(IsDisableEnum.NO.getKey());
                            projectLandPartProductTypeQuotaMap.setCreateTime(LocalDateTime.now());
                            projectLandPartProductTypeQuotaMap.setCreaterId(userVO.getId());
                            projectLandPartProductTypeQuotaMap.setCreaterName(userVO.getName());
                            //projectLandPartProductTypeQuotaMapMapper.insert(projectLandPartProductTypeQuotaMap);
                            insertProjectLandPartProductTypeQuotaMapList.add(projectLandPartProductTypeQuotaMap);
                        }
                    }
                }

                if(CollectionUtils.isNotEmpty(insertProjectLandPartProductTypeMapList)){
                    projectLandPartProductTypeMapService.saveBatch(insertProjectLandPartProductTypeMapList,100);
                }

                if(CollectionUtils.isNotEmpty(insertProjectLandPartProductTypeQuotaMapList)){
                    projectLandPartProductTypeQuotaMapService.saveBatch(insertProjectLandPartProductTypeQuotaMapList,100);
                }
            }
        } else {
            StringBuffer areaRestMsg = new StringBuffer();
            BoProjectLandPartMap boProjectLandPartMap = projectLandPartMapMapper.selectProjectLandBySubProjectIdAndLandId(subProjectInfos.get(0).getParentSid(), subProjectAddLandReqParam.getLandId());
            SubTotalAreaDto subTotalAreaDto = projectLandPartMapMapper.selectTotalSubAreaBySubIdsAndLandId(subProjectInfos, subProjectAddLandReqParam.getLandId());
            if (subTotalAreaDto != null) {
                String subTotal = sub(Double.parseDouble(!StringUtils.isNotBlank(boProjectLandPartMap.getTotalUseLandArea())?"0":boProjectLandPartMap.getTotalUseLandArea()),
                        Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumTotalArea())?"0":subTotalAreaDto.getSumTotalArea()));
                subTotal = sub(Double.parseDouble(!StringUtils.isNotBlank(subTotal)?"0":subTotal), Double.parseDouble(!StringUtils.isNotBlank(subProjectAddLandReqParam.getTotalMeasure())?"0":subProjectAddLandReqParam.getTotalMeasure()));
                if (Double.parseDouble(StringUtils.isBlank(subTotal)?"0":subTotal) < 0) {
                    areaRestMsg.append(boProjectLandPartMap.getLandPartName() + boProjectLandPartMap.getTotalUseLandArea());
                }

                String subUse = sub(Double.parseDouble(!StringUtils.isNotBlank(boProjectLandPartMap.getCanUseLandArea())?"0":boProjectLandPartMap.getCanUseLandArea()),
                        Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumUseArea())?"0":subTotalAreaDto.getSumUseArea()));
                subUse = sub(Double.parseDouble(!StringUtils.isNotBlank(subUse)?"0":subUse), Double.parseDouble(!StringUtils.isNotBlank(subProjectAddLandReqParam.getCanUseMeasure())?"0":subProjectAddLandReqParam.getCanUseMeasure()));
                if (Double.parseDouble(StringUtils.isBlank(subUse)?"0":subUse) < 0) {
                    areaRestMsg.append(boProjectLandPartMap.getLandPartName() + boProjectLandPartMap.getCanUseLandArea());
                }

                String subCover = sub(Double.parseDouble(!StringUtils.isNotBlank(boProjectLandPartMap.getBuildingLandCoverArea())?"0":boProjectLandPartMap.getBuildingLandCoverArea()),
                        Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumCoverArea())?"0":subTotalAreaDto.getSumCoverArea()));
                subCover = sub(Double.parseDouble(!StringUtils.isNotBlank(subCover)?"0":subCover), Double.parseDouble(!StringUtils.isNotBlank(subProjectAddLandReqParam.getBuildCoverMeasure())?"0":subProjectAddLandReqParam.getBuildCoverMeasure()));
                if (Double.parseDouble(StringUtils.isBlank(subCover)?"0":subCover) < 0) {
                    areaRestMsg.append(boProjectLandPartMap.getLandPartName() + boProjectLandPartMap.getBuildingLandCoverArea());
                }

                String subTake = sub(Double.parseDouble(!StringUtils.isNotBlank(boProjectLandPartMap.getTakeByLandUseArea())?"0":boProjectLandPartMap.getTakeByLandUseArea()),
                        Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumTakeArea())?"0":subTotalAreaDto.getSumTakeArea()));
                subTake = sub(Double.parseDouble(!StringUtils.isNotBlank(subTake)?"0":subTake), Double.parseDouble(!StringUtils.isNotBlank(subProjectAddLandReqParam.getUseTakeMeasure())?"0":subProjectAddLandReqParam.getUseTakeMeasure()));
                if (Double.parseDouble(StringUtils.isBlank(subTake)?"0":subTake) < 0) {
                    areaRestMsg.append(boProjectLandPartMap.getLandPartName() + boProjectLandPartMap.getTakeByLandUseArea());
                }

                String subPrice = sub(Double.parseDouble(!StringUtils.isNotBlank(boProjectLandPartMap.getLandGetPrice())?"0":boProjectLandPartMap.getLandGetPrice()),
                        Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumGetPrice())?"0":subTotalAreaDto.getSumGetPrice()));
                subPrice = sub(Double.parseDouble(!StringUtils.isNotBlank(subPrice)?"0":subPrice), Double.parseDouble(!StringUtils.isNotBlank(subProjectAddLandReqParam.getGetLandPrice())?"0":subProjectAddLandReqParam.getGetLandPrice()));
                if (Double.parseDouble(StringUtils.isBlank(subPrice)?"0":subPrice) < 0) {
                    areaRestMsg.append(boProjectLandPartMap.getLandPartName() + boProjectLandPartMap.getLandGetPrice());
                }

                String subAbove = sub(Double.parseDouble(!StringUtils.isNotBlank(boProjectLandPartMap.getAboveGroundCalcVolumeArea())?"0":boProjectLandPartMap.getAboveGroundCalcVolumeArea()),
                        Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumAboveArea())?"0":subTotalAreaDto.getSumAboveArea()));
                subAbove = sub(Double.parseDouble(!StringUtils.isNotBlank(subAbove)?"0":subAbove), Double.parseDouble(!StringUtils.isNotBlank(subProjectAddLandReqParam.getUpLandMeasure())?"0":subProjectAddLandReqParam.getUpLandMeasure()));
                if (Double.parseDouble(StringUtils.isBlank(subAbove)?"0":subAbove) < 0) {
                    areaRestMsg.append(boProjectLandPartMap.getLandPartName() + boProjectLandPartMap.getAboveGroundCalcVolumeArea());
                }

                String subUnder = sub(Double.parseDouble(!StringUtils.isNotBlank(boProjectLandPartMap.getUnderGroundCalcVolumeArea())?"0":boProjectLandPartMap.getUnderGroundCalcVolumeArea()),
                        Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumUnderArea())?"0":subTotalAreaDto.getSumUnderArea()));
                subUnder = sub(Double.parseDouble(!StringUtils.isNotBlank(subUnder)?"0":subUnder), Double.parseDouble(!StringUtils.isNotBlank(subProjectAddLandReqParam.getDownLandMeasure())?"0":subProjectAddLandReqParam.getDownLandMeasure()));
                if (Double.parseDouble(StringUtils.isBlank(subUnder)?"0":subUnder) < 0) {
                    areaRestMsg.append(boProjectLandPartMap.getLandPartName() + boProjectLandPartMap.getUnderGroundCalcVolumeArea());
                }

            }
            if (areaRestMsg != null && areaRestMsg.length() > 0) {
                jsonResult.setCode(CodeEnum.LAND_AREAT_EXTENDS.getKey());
                jsonResult.setMsg(areaRestMsg.toString());
                return jsonResult;
            }

            Map<String, String> extQuoteMap = new HashMap<String, String>();
            List<BoLandPartProductTypeQuotaMapExtendsDto> boLandPartProductTypeQuotaMapExtendsDtos = landPartProductTypeQuotaMapMapper.selectTotalQuoteByProjectIdAndLandId(subProjectInfos.get(0).getParentSid(), subProjectAddLandReqParam.getLandId());
            if (boLandPartProductTypeQuotaMapExtendsDtos != null && boLandPartProductTypeQuotaMapExtendsDtos.size() > 0) {

                List<SubProjectProductTypeVo> quoteTypeList = subProjectAddLandReqParam.getQuoteTypeList();
                for (int i = 0; i < quoteTypeList.size(); i++) {
                    SubProjectProductTypeVo subProjectProductTypeVo = quoteTypeList.get(i);
                    if (subProjectProductTypeVo != null) {
                        List<SubProjectQuoteTypeVo> subProjectQuoteTypeDetails = subProjectProductTypeVo.getSubProjectQuoteTypeDetails();
                        for (int j = 0; j < subProjectQuoteTypeDetails.size(); j++) {
                            SubProjectQuoteTypeVo subProjectQuoteTypeVo = subProjectQuoteTypeDetails.get(j);
                            extQuoteMap.put(subProjectProductTypeVo.getAttrQuotaValue() + "&" + subProjectProductTypeVo.getProductCode() + "&" + subProjectQuoteTypeVo.getQuoteCode(), subProjectQuoteTypeVo.getQuoteValue());
                        }
                    }
                }

                List<ProjectProductTypeQuotaDto> projectTotalProductTypeQuotaDtos = projectLandPartProductTypeQuotaMapMapper.selectTotalQuoteByLandId(subProjectAddLandReqParam.getLandId());
                if (projectTotalProductTypeQuotaDtos != null && projectTotalProductTypeQuotaDtos.size() > 0) {
                    for (int i = 0; i < projectTotalProductTypeQuotaDtos.size(); i++) {
                        ProjectProductTypeQuotaDto projectProductTypeQuotaDto = projectTotalProductTypeQuotaDtos.get(i);
                        String productCode = projectProductTypeQuotaDto.getProductCode();
                        String quoteCode = projectProductTypeQuotaDto.getQuoteCode();
                        String extValue = extQuoteMap.get(productCode + "&" + quoteCode);
                        if (StringUtils.isNotBlank(extValue)) {
                            String quoteValue = projectProductTypeQuotaDto.getQuoteValue();
                            if(StringUtils.isNotBlank(quoteValue)){
                                String addValue = add(Double.parseDouble(extValue), Double.parseDouble(quoteValue));
                                extQuoteMap.put(projectProductTypeQuotaDto.getAttrQuotaValue() + "&" + productCode + "&" + quoteCode, String.valueOf(addValue));
                            }
                        }
                    }
                }

                StringBuffer restMsg = new StringBuffer();
                for (int i = 0; i < boLandPartProductTypeQuotaMapExtendsDtos.size(); i++) {
                    BoLandPartProductTypeQuotaMapExtendsDto boLandPartProductTypeQuotaMapExtendsDto = boLandPartProductTypeQuotaMapExtendsDtos.get(i);
                    String productCode = boLandPartProductTypeQuotaMapExtendsDto.getProductCode();
                    String quotaCode = boLandPartProductTypeQuotaMapExtendsDto.getQuotaCode();
                    String extValue = extQuoteMap.get(productCode + "&" + quotaCode);
                    if (StringUtils.isNotBlank(extValue)) {
                        String quotaValue = boLandPartProductTypeQuotaMapExtendsDto.getQuotaValue();
                        if(StringUtils.isNotBlank(quotaValue)){
                            String sub = sub(Double.parseDouble(quotaValue), Double.parseDouble(extValue));
                            if (Double.parseDouble(StringUtils.isBlank(sub)?"0":sub) < 0) {
                                BoQuotaGroupMap boQuotaGroupMap = formatMap.get(quotaCode);
                                if (i < boLandPartProductTypeQuotaMapExtendsDtos.size() - 1) {
                                    restMsg.append(boQuotaGroupMap.getName()).append(",");
                                } else {
                                    restMsg.append(boQuotaGroupMap.getName());
                                }
                            }
                        }
                    }
                }

                if (restMsg != null && restMsg.length() > 0) {
                    jsonResult.setCode(CodeEnum.LAND_QUOTE_USE_ALL_EXTENDS.getKey());
                    jsonResult.setMsg(restMsg.toString()+"已经超标！");
                    return jsonResult;
                }

                String landPartId = UUIDUtils.create();
                if (boProjectLandPartMap != null) {
                    BoProjectLandPartMap subBoProjectLandPartMap = new BoProjectLandPartMap();
                    subBoProjectLandPartMap.setId(landPartId);
                    subBoProjectLandPartMap.setLandPartId(subProjectAddLandReqParam.getLandId());
                    subBoProjectLandPartMap.setProjectExtendId(subProjectInfoDto.getExtendId());
                    subBoProjectLandPartMap.setLandPartName(boProjectLandPartMap.getLandPartName());
                    subBoProjectLandPartMap.setIsAllDev(isDev);
                    subBoProjectLandPartMap.setTotalUseLandArea(subProjectAddLandReqParam.getTotalMeasure());

                    String capacityValue = "0";
                    if(StringUtils.isNotBlank(subProjectAddLandReqParam.getUpLandMeasure()) && StringUtils.isNotBlank(subProjectAddLandReqParam.getDownLandMeasure())){
                        capacityValue = String.valueOf(add(Double.parseDouble(subProjectAddLandReqParam.getUpLandMeasure()), Double.parseDouble(subProjectAddLandReqParam.getDownLandMeasure())));
                    }
                    subBoProjectLandPartMap.setCapacityBuildingArea(capacityValue);
                    subBoProjectLandPartMap.setCanUseLandArea(subProjectAddLandReqParam.getCanUseMeasure());
                    subBoProjectLandPartMap.setBuildingLandCoverArea(subProjectAddLandReqParam.getBuildCoverMeasure());
                    subBoProjectLandPartMap.setTakeByLandUseArea(subProjectAddLandReqParam.getUseTakeMeasure());
                    subBoProjectLandPartMap.setLandGetPrice(subProjectAddLandReqParam.getGetLandPrice());
                    subBoProjectLandPartMap.setAboveGroundCalcVolumeArea(subProjectAddLandReqParam.getUpLandMeasure());
                    subBoProjectLandPartMap.setUnderGroundCalcVolumeArea(subProjectAddLandReqParam.getDownLandMeasure());
                    subBoProjectLandPartMap.setRefProjectId(subProjectInfo.getSid());
                    subBoProjectLandPartMap.setRefProjectName(subProjectInfo.getName());
                    subBoProjectLandPartMap.setCreateTime(LocalDateTime.now());
                    subBoProjectLandPartMap.setCreaterId(userVO.getId());
                    subBoProjectLandPartMap.setCreaterName(userVO.getName());
                    subBoProjectLandPartMap.setIsDelete(IsDeleteEnum.NO.getKey());
                    subBoProjectLandPartMap.setIsDisable(IsDisableEnum.NO.getKey());

                    projectLandPartMapMapper.insert(subBoProjectLandPartMap);
                }

                List<BoProjectLandPartProductTypeMap> insertProjectLandPartProductTypeMapList = new ArrayList<BoProjectLandPartProductTypeMap>();
                List<BoProjectLandPartProductTypeQuotaMap> insertProjectLandPartProductTypeQuotaMapList = new ArrayList<BoProjectLandPartProductTypeQuotaMap>();
                for (int i = 0; i < quoteTypeList.size(); i++) {
                    SubProjectProductTypeVo subProjectProductTypeVo = quoteTypeList.get(i);

                    BoProjectLandPartProductTypeMap projectLandPartProductTypeMap = new BoProjectLandPartProductTypeMap();
                    String id = UUIDUtils.create();
                    projectLandPartProductTypeMap.setId(id);
                    projectLandPartProductTypeMap.setProjectLandPartId(landPartId);
                    projectLandPartProductTypeMap.setProductTypeId(subProjectProductTypeVo.getProductTypeId());
                    projectLandPartProductTypeMap.setProductTypeCode(subProjectProductTypeVo.getProductCode());
                    projectLandPartProductTypeMap.setProductTypeName(subProjectProductTypeVo.getProductName());
                    projectLandPartProductTypeMap.setIsDelete(IsDeleteEnum.NO.getKey());
                    projectLandPartProductTypeMap.setIsDisable(IsDisableEnum.NO.getKey());
                    projectLandPartProductTypeMap.setCreateTime(LocalDateTime.now());
                    projectLandPartProductTypeMap.setCreaterId(userVO.getId());
                    projectLandPartProductTypeMap.setCreaterName(userVO.getName());
                    projectLandPartProductTypeMap.setPropertyRightAttr(subProjectProductTypeVo.getAttrQuotaValue());
                    //projectLandPartProductTypeMapMapper.insert(projectLandPartProductTypeMap);
                    insertProjectLandPartProductTypeMapList.add(projectLandPartProductTypeMap);

                    for (int j = 0; j < subProjectProductTypeVo.getSubProjectQuoteTypeDetails().size(); j++) {
                        SubProjectQuoteTypeVo subProjectQuoteTypeVo = subProjectProductTypeVo.getSubProjectQuoteTypeDetails().get(j);

                        BoProjectLandPartProductTypeQuotaMap projectLandPartProductTypeQuotaMap = new BoProjectLandPartProductTypeQuotaMap();
                        projectLandPartProductTypeQuotaMap.setId(UUIDUtils.create());
                        projectLandPartProductTypeQuotaMap.setProjectLandPartProductTypeId(id);
                        projectLandPartProductTypeQuotaMap.setQuotaGroupMapId(subProjectQuoteTypeVo.getGroupMapId());
                        projectLandPartProductTypeQuotaMap.setQuotaId(subProjectQuoteTypeVo.getQuoteId());
                        projectLandPartProductTypeQuotaMap.setQuotaCode(subProjectQuoteTypeVo.getQuoteCode());
                        projectLandPartProductTypeQuotaMap.setQuotaValue(subProjectQuoteTypeVo.getQuoteValue());
                        projectLandPartProductTypeQuotaMap.setIsDelete(IsDeleteEnum.NO.getKey());
                        projectLandPartProductTypeQuotaMap.setIsDisable(IsDisableEnum.NO.getKey());
                        projectLandPartProductTypeQuotaMap.setCreateTime(LocalDateTime.now());
                        projectLandPartProductTypeQuotaMap.setCreaterId(userVO.getId());
                        projectLandPartProductTypeQuotaMap.setCreaterName(userVO.getName());
                        //projectLandPartProductTypeQuotaMapMapper.insert(projectLandPartProductTypeQuotaMap);
                        insertProjectLandPartProductTypeQuotaMapList.add(projectLandPartProductTypeQuotaMap);
                    }
                }

                if(CollectionUtils.isNotEmpty(insertProjectLandPartProductTypeMapList)){
                    projectLandPartProductTypeMapService.saveBatch(insertProjectLandPartProductTypeMapList,100);
                }

                if(CollectionUtils.isNotEmpty(insertProjectLandPartProductTypeQuotaMapList)){
                    projectLandPartProductTypeQuotaMapService.saveBatch(insertProjectLandPartProductTypeQuotaMapList,100);
                }
            }
        }

        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
        return jsonResult;
    }


    @Override
    public JSONResult selectCanUseLandInfoByProjectId(String projectId, String landName, Integer pageNo, Integer pageSize, JSONResult jsonResult) {
        List<LandSelInfoVo> landSelInfoVos = new ArrayList<LandSelInfoVo>();
        Page<LandSelInfoVo> page = new Page<LandSelInfoVo>(pageNo,pageSize);
        ProjectInfoDto projectInfoDto = projectInfoMapper.selectProjectByProjectId(projectId, null);

        if (projectInfoDto != null) {
            String cityCompanyId = projectInfoDto.getCityId();
            if (StringUtils.isNotBlank(cityCompanyId)) {
                landSelInfoVos = projectLandPartMapMapper.selectCanUseLandByProject(page, cityCompanyId, landName);
                jsonResult.setData(page.setRecords(landSelInfoVos));
                jsonResult.setCode(CodeEnum.SUCCESS.getKey());
                jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
            } else {
                jsonResult.setCode(CodeEnum.PROJECT_EXTENDS_INFO.getKey());
                jsonResult.setMsg(CodeEnum.PROJECT_EXTENDS_INFO.getValue());
            }
        } else {
            jsonResult.setCode(CodeEnum.PROJECT_EXTENDS.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_EXTENDS.getValue());
        }

        return jsonResult;
    }

    @Override
    @Transactional
    public JSONResult addLandToProject(ProjectAddLandReqParam projectAddLandReqParam, CurrentUserVO userVO, JSONResult jsonResult) throws Exception {

        String projectId = projectAddLandReqParam.getProjectId();
        BoProjectExtend boProjectExtend = projectExtendMapper.selectByProjectId(projectId, null,null);
        if (versionStatusVali(jsonResult, boProjectExtend.getVersionStatus(), boProjectExtend.getVersion()))
            return jsonResult;

        List<BoProjectLandPartMap> boProjectLandPartMaps = projectLandPartMapMapper.selectUseLandByLandId(projectAddLandReqParam.getLandId());
        if (boProjectLandPartMaps != null && boProjectLandPartMaps.size() > 0) {
            jsonResult.setCode(CodeEnum.LAND_TO_PROJECT_EXTENDS.getKey());
            jsonResult.setMsg(CodeEnum.LAND_TO_PROJECT_EXTENDS.getValue());
        } else {
            LandInfoDto landInfoDto = landinformationTbMapper.selectByLandId(projectAddLandReqParam.getLandId());
            if (landInfoDto != null) {
                if (boProjectExtend != null) {
                    if (versionStatusVali(jsonResult, boProjectExtend.getVersionStatus(), boProjectExtend.getVersion()))
                        return jsonResult;

                    BoProjectLandPartMap projectLandPartMap = new BoProjectLandPartMap();
                    projectLandPartMap.setId(UUIDUtils.create());
                    projectLandPartMap.setLandPartId(projectAddLandReqParam.getLandId());
                    projectLandPartMap.setProjectExtendId(boProjectExtend.getId());
                    projectLandPartMap.setLandPartName(landInfoDto.getLandName());
                    projectLandPartMap.setIsAllDev(1);
                    projectLandPartMap.setTotalUseLandArea(landInfoDto.getTaotalBuildMeasure());
                    projectLandPartMap.setCapacityBuildingArea(landInfoDto.getMeterBuildMeasure());
                    projectLandPartMap.setIsDelete(IsDeleteEnum.NO.getKey());
                    projectLandPartMap.setIsDisable(IsDisableEnum.NO.getKey());
                    projectLandPartMap.setCreaterId(userVO.getId());
                    projectLandPartMap.setCreaterName(userVO.getName());
                    projectLandPartMap.setCreateTime(LocalDateTime.now());
                    projectLandPartMap.setCanUseLandArea(landInfoDto.getTaotalBuildMeasure());
                    projectLandPartMapMapper.insert(projectLandPartMap);

                    jsonResult.setCode(CodeEnum.SUCCESS.getKey());
                    jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
                } else {
                    jsonResult.setCode(CodeEnum.PROJECT_EXTENDS_INFO.getKey());
                    jsonResult.setMsg(CodeEnum.PROJECT_EXTENDS_INFO.getValue());
                }
            } else {
                jsonResult.setCode(CodeEnum.LAND_INFO_EXTENDS.getKey());
                jsonResult.setMsg(CodeEnum.LAND_INFO_EXTENDS.getValue());
            }
        }

        return jsonResult;
    }

    private boolean versionStatusVali(JSONResult jsonResult, Integer versionStatus, String version) {
        if (!StringUtils.isNotBlank(version)) {
            jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
            return true;
        }
        if (versionStatus != null && (versionStatus.equals(VersionStatusEnum.CHECKING.getKey()) || versionStatus.equals(VersionStatusEnum.PASSED.getKey()))) {
            jsonResult.setCode(CodeEnum.PROJECT_EXTENDS_VERSION.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_EXTENDS_VERSION.getValue());
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public JSONResult deleteLandToProject(ProjectAddLandReqParam projectAddLandReqParam, CurrentUserVO userVO, JSONResult jsonResult) {

        BoProjectExtend boProjectExtend = projectExtendMapper.selectByProjectId(projectAddLandReqParam.getProjectId(), null,null);
        if (versionStatusVali(jsonResult, boProjectExtend.getVersionStatus(), boProjectExtend.getVersion()))
            return jsonResult;

        if (boProjectExtend != null) {
            List<BoProjectLandPartMap> boProjectLandPartMaps = projectLandPartMapMapper.selectUseLandByLandId(projectAddLandReqParam.getLandId());
            if (boProjectLandPartMaps != null && boProjectLandPartMaps.size() > 0) {

                List<BoLandPartProductTypeMap> boLandPartProductTypeMaps = landPartProductTypeMapMapper.selectProductTypeByLandId(boProjectLandPartMaps.get(0).getId());
                if (boLandPartProductTypeMaps != null && boLandPartProductTypeMaps.size() > 0) {
                    jsonResult.setCode(CodeEnum.PROJECT_LAND_USE_EXCEPTION.getKey());
                    jsonResult.setMsg(CodeEnum.PROJECT_LAND_USE_EXCEPTION.getValue());
                    return jsonResult;
                }

                /*List<String> projectOneQuotaLandIdsList = projectQuotaExtendMapper.selectAdoptStageOneBySubProId(projectAddLandReqParam.getProjectId(), StageCodeEnum.STAGE_01.getKey());
                if(projectOneQuotaLandIdsList != null && projectOneQuotaLandIdsList.size() > 0){
                    if(projectOneQuotaLandIdsList.contains(projectAddLandReqParam.getLandId())){
                        jsonResult.setCode(CodeEnum.PROJECT_LAND_DELETE_EXCEPTION.getKey());
                        jsonResult.setMsg(CodeEnum.PROJECT_LAND_DELETE_EXCEPTION.getValue());
                        return jsonResult;
                    }
                }*/

                BoProjectLandPartMap boProjectLandPartMap = new BoProjectLandPartMap();
                boProjectLandPartMap.setId(boProjectLandPartMaps.get(0).getId());
                boProjectLandPartMap.setIsDelete(IsDeleteEnum.YES.getKey());
                boProjectLandPartMap.setUpdaterId(userVO.getId());
                boProjectLandPartMap.setUpdaterName(userVO.getName());
                boProjectLandPartMap.setUpdateTime(LocalDateTime.now());
                projectLandPartMapMapper.updateById(boProjectLandPartMap);

                jsonResult.setCode(CodeEnum.SUCCESS.getKey());
                jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
            } else {
                jsonResult.setCode(CodeEnum.PROJECT_LAND_EXCEPTION.getKey());
                jsonResult.setMsg(CodeEnum.PROJECT_LAND_EXCEPTION.getValue());
            }
        } else {
            jsonResult.setCode(CodeEnum.PROJECT_EXTENDS_INFO.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_EXTENDS_INFO.getValue());
        }

        return jsonResult;
    }

    @Override
    @Transactional
    public JSONResult deleteLandToSubProject(SubProjectDelLandReqParam subProjectDelLandReqParam, CurrentUserVO userVO, JSONResult
            jsonResult) throws Exception {

        BoProjectExtend boProjectExtend = projectExtendMapper.selectByProjectId(subProjectDelLandReqParam.getSubProjectId(), null,null);
        if (versionStatusVali(jsonResult, boProjectExtend.getVersionStatus(), boProjectExtend.getVersion()))
            return jsonResult;

        SubProjectAndExendInfoDto subProjectAndExendInfoDto = projectInfoMapper.selectSubProjectExtendIdBySubProjectId(subProjectDelLandReqParam.getSubProjectId());
        if (subProjectAndExendInfoDto != null) {
            Integer versionStatus = subProjectAndExendInfoDto.getVersionStatus();
            if (versionStatus != null && (versionStatus.equals(VersionStatusEnum.CHECKING.getKey()) || versionStatus.equals(VersionStatusEnum.PASSED.getKey()))) {
                jsonResult.setCode(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getKey());
                jsonResult.setMsg(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getValue());
                return jsonResult;
            }
        }

        /*List<String> projectOtherQuotaLandIdsList = projectQuotaExtendMapper.selectAdoptStageOtherBySubProId(subProjectDelLandReqParam.getSubProjectId(), StageCodeEnum.STAGE_01.getKey());
        if(projectOtherQuotaLandIdsList != null && projectOtherQuotaLandIdsList.size() > 0){
            if(projectOtherQuotaLandIdsList.contains(subProjectDelLandReqParam.getLandId())){
                jsonResult.setCode(CodeEnum.PROJECT_LAND_DELETE_EXCEPTION.getKey());
                jsonResult.setMsg(CodeEnum.PROJECT_LAND_DELETE_EXCEPTION.getValue());
                return jsonResult;
            }
        }*/

        BoProjectLandPartMap boProjectLandPartMap = projectLandPartMapMapper.selectProjectLandBySubProjectIdAndLandId(subProjectDelLandReqParam.getSubProjectId(), subProjectDelLandReqParam.getLandId());
        if (boProjectLandPartMap != null) {

            List<String> productIds = new ArrayList<String>();
            List<BoProjectLandPartProductTypeMap> projectLandPartProductTypeMapList = new ArrayList<BoProjectLandPartProductTypeMap>();
            List<BoProjectLandPartProductTypeQuotaMap> projectLandPartProductTypeQuotaMapList = new ArrayList<BoProjectLandPartProductTypeQuotaMap>();

            List<BoProjectLandPartProductTypeMap> boProjectLandPartProductTypeMaps = projectLandPartProductTypeMapMapper.selectProductBySubIdAndLandId(subProjectDelLandReqParam.getSubProjectId(), subProjectDelLandReqParam.getLandId());
            if (boProjectLandPartProductTypeMaps != null && boProjectLandPartProductTypeMaps.size() > 0) {
                LocalDateTime now = LocalDateTime.now();
                for (int i = 0; i < boProjectLandPartProductTypeMaps.size(); i++) {
                    BoProjectLandPartProductTypeMap boProjectLandPartProductTypeMap = boProjectLandPartProductTypeMaps.get(i);
                    boProjectLandPartProductTypeMap.setIsDelete(IsDeleteEnum.YES.getKey());
                    boProjectLandPartProductTypeMap.setUpdateTime(now);
                    boProjectLandPartProductTypeMap.setUpdaterId(userVO.getId());
                    boProjectLandPartProductTypeMap.setUpdaterName(userVO.getName());
                    projectLandPartProductTypeMapList.add(boProjectLandPartProductTypeMap);
                    productIds.add(boProjectLandPartProductTypeMap.getId());
                }
            }

            if (productIds != null && productIds.size() > 0) {
                List<BoProjectLandPartProductTypeQuotaMap> boProjectLandPartProductTypeQuotaMaps = projectLandPartProductTypeQuotaMapMapper.selectByTypeQuoteIds(productIds);
                if (boProjectLandPartProductTypeQuotaMaps != null && boProjectLandPartProductTypeQuotaMaps.size() > 0) {
                    LocalDateTime now = LocalDateTime.now();
                    for (int i = 0; i < boProjectLandPartProductTypeQuotaMaps.size(); i++) {
                        BoProjectLandPartProductTypeQuotaMap boProjectLandPartProductTypeQuotaMap = boProjectLandPartProductTypeQuotaMaps.get(i);
                        boProjectLandPartProductTypeQuotaMap.setIsDelete(IsDeleteEnum.YES.getKey());
                        boProjectLandPartProductTypeQuotaMap.setUpdateTime(now);
                        boProjectLandPartProductTypeQuotaMap.setUpdaterId(userVO.getId());
                        boProjectLandPartProductTypeQuotaMap.setUpdaterName(userVO.getName());
                        projectLandPartProductTypeQuotaMapList.add(boProjectLandPartProductTypeQuotaMap);
                    }
                }
            }

            boProjectLandPartMap.setIsDelete(IsDeleteEnum.YES.getKey());
            boProjectLandPartMap.setUpdateTime(LocalDateTime.now());
            boProjectLandPartMap.setUpdaterId(userVO.getId());
            boProjectLandPartMap.setUpdaterName(userVO.getName());
            projectLandPartMapMapper.updateById(boProjectLandPartMap);
            if (projectLandPartProductTypeMapList != null && projectLandPartProductTypeMapList.size() > 0) {
                projectLandPartProductTypeMapMapper.updateIsDeleteBatch(projectLandPartProductTypeMapList);
            }
            if (projectLandPartProductTypeQuotaMapList != null && projectLandPartProductTypeQuotaMapList.size() > 0) {
                projectLandPartProductTypeQuotaMapMapper.updateIsDeleteBatch(projectLandPartProductTypeQuotaMapList);
            }
        }

        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());

        return jsonResult;
    }

    @Override
    @Transactional
    public JSONResult updateLandToSubProject(SubProjectAddLandReqParam subProjectAddLandReqParam, CurrentUserVO userVO, JSONResult
            jsonResult) throws Exception {

        BoProjectExtend boProjectExtend = projectExtendMapper.selectByProjectId(subProjectAddLandReqParam.getSubProjectId(), null, null);
        if (versionStatusVali(jsonResult, boProjectExtend.getVersionStatus(), boProjectExtend.getVersion()))
            return jsonResult;

        SubProjectAndExendInfoDto subProjectAndExendInfoDto = projectInfoMapper.selectSubProjectExtendIdBySubProjectId(subProjectAddLandReqParam.getSubProjectId());
        if (subProjectAndExendInfoDto != null) {
            Integer versionStatus = subProjectAndExendInfoDto.getVersionStatus();
            if (versionStatus != null && (versionStatus.equals(VersionStatusEnum.CHECKING.getKey()) || versionStatus.equals(VersionStatusEnum.PASSED.getKey()))) {
                jsonResult.setCode(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getKey());
                jsonResult.setMsg(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getValue());
                return jsonResult;
            }
        }

        Map<String, BoQuotaGroupMap> formatMap = new HashMap<String, BoQuotaGroupMap>();
        QueryWrapper<BoQuotaGroupMap> queryTypeQuotaWrapper = new QueryWrapper<BoQuotaGroupMap>();
        queryTypeQuotaWrapper.eq("group_code","LAND_PART_PRODUCT_TYPE")
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        List<BoQuotaGroupMap> boQuotaGroupMaps = quotaGroupMapMapper.selectList(queryTypeQuotaWrapper);
        if(CollectionUtils.isNotEmpty(boQuotaGroupMaps)){
            for (int i = 0; i < boQuotaGroupMaps.size(); i++){
                BoQuotaGroupMap boQuotaGroupMap = boQuotaGroupMaps.get(i);
                formatMap.put(boQuotaGroupMap.getCode(), boQuotaGroupMap);
            }
        }

        List<MdmProjectInfo> mdmProjectInfos = projectInfoMapper.selectSubProjectBySubId(subProjectAddLandReqParam.getSubProjectId());
        List<String> subIds = Optional.ofNullable(mdmProjectInfos)
                .orElseGet(ArrayList::new)
                .stream().map(MdmProjectInfo::getSid)
                .collect(Collectors.toList());
        subIds.remove(subProjectAddLandReqParam.getSubProjectId());

        List<ProjectProductTypeQuotaDto> projectProductTypeQuotaDtos = new ArrayList<ProjectProductTypeQuotaDto>();
        if (subIds != null && subIds.size() > 0) {
            projectProductTypeQuotaDtos = projectLandPartProductTypeQuotaMapMapper.selectTotalQuotaBySubProjectIdsAndLandId(subIds, subProjectAddLandReqParam.getLandId());
        }

        Integer isDev = subProjectAddLandReqParam.getIsDev();
        if (isDev != null && isDev.equals(IsDevEnum.YES.getKey())) {
            if (projectProductTypeQuotaDtos != null && projectProductTypeQuotaDtos.size() > 0) {
                jsonResult.setCode(CodeEnum.SUB_PROJECT_LAND_EXTENDS.getKey());
                jsonResult.setMsg(CodeEnum.SUB_PROJECT_LAND_EXTENDS.getValue());
                return jsonResult;
            } else {
                BoProjectLandPartMap projectLandPartMap = projectLandPartMapMapper.selectProjectLandBySubProjectIdAndLandId(mdmProjectInfos.get(0).getParentSid(), subProjectAddLandReqParam.getLandId());
                BoProjectLandPartMap subProjectLandPartMap = projectLandPartMapMapper.selectProjectLandBySubProjectIdAndLandId(subProjectAddLandReqParam.getSubProjectId(), subProjectAddLandReqParam.getLandId());
                if (projectLandPartMap != null) {
                    subProjectLandPartMap.setTotalUseLandArea(projectLandPartMap.getTotalUseLandArea());
                    subProjectLandPartMap.setCapacityBuildingArea(projectLandPartMap.getCapacityBuildingArea());
                    subProjectLandPartMap.setCanUseLandArea(projectLandPartMap.getCanUseLandArea());
                    subProjectLandPartMap.setBuildingLandCoverArea(projectLandPartMap.getBuildingLandCoverArea());
                    subProjectLandPartMap.setTakeByLandUseArea(projectLandPartMap.getTakeByLandUseArea());
                    subProjectLandPartMap.setLandGetPrice(projectLandPartMap.getLandGetPrice());
                    subProjectLandPartMap.setAboveGroundCalcVolumeArea(projectLandPartMap.getAboveGroundCalcVolumeArea());
                    subProjectLandPartMap.setUnderGroundCalcVolumeArea(projectLandPartMap.getUnderGroundCalcVolumeArea());
                    subProjectLandPartMap.setIsAllDev(isDev);
                    subProjectLandPartMap.setUpdaterId(userVO.getId());
                    subProjectLandPartMap.setUpdaterName(userVO.getName());
                    subProjectLandPartMap.setUpdateTime(LocalDateTime.now());
                    projectLandPartMapMapper.updateById(subProjectLandPartMap);
                } else {
                    jsonResult.setCode(CodeEnum.LAND_INFO_EXTENDS.getKey());
                    jsonResult.setMsg(CodeEnum.LAND_INFO_EXTENDS.getValue());
                    return jsonResult;
                }

                List<SubProjectProductTypeVo> quoteTypeList = subProjectAddLandReqParam.getQuoteTypeList();
                if (quoteTypeList != null && quoteTypeList.size() > 0) {
                    List<BoProjectLandPartProductTypeMap> projectLandPartProductTypeMapList = new ArrayList<BoProjectLandPartProductTypeMap>();
                    List<BoProjectLandPartProductTypeQuotaMap> projectLandPartProductTypeQuotaMapList = new ArrayList<BoProjectLandPartProductTypeQuotaMap>();

                    LocalDateTime now = LocalDateTime.now();
                    for (int i = 0; i < quoteTypeList.size(); i++) {
                        SubProjectProductTypeVo subProjectProductTypeVo = quoteTypeList.get(i);
                        BoProjectLandPartProductTypeMap projectLandPartProductTypeMap = new BoProjectLandPartProductTypeMap();
                        projectLandPartProductTypeMap.setId(subProjectProductTypeVo.getProductId());
                        projectLandPartProductTypeMap.setIsDelete(IsDeleteEnum.YES.getKey());
                        projectLandPartProductTypeMap.setUpdaterId(userVO.getId());
                        projectLandPartProductTypeMap.setUpdaterName(userVO.getName());
                        projectLandPartProductTypeMap.setUpdateTime(now);
                        projectLandPartProductTypeMapList.add(projectLandPartProductTypeMap);

                        List<SubProjectQuoteTypeVo> subProjectQuoteTypeDetails = subProjectProductTypeVo.getSubProjectQuoteTypeDetails();
                        if (subProjectQuoteTypeDetails != null && subProjectQuoteTypeDetails.size() > 0) {
                            for (int j = 0; j < subProjectQuoteTypeDetails.size(); j++) {
                                SubProjectQuoteTypeVo subProjectQuoteTypeVo = subProjectQuoteTypeDetails.get(j);
                                BoProjectLandPartProductTypeQuotaMap projectLandPartProductTypeQuotaMap = new BoProjectLandPartProductTypeQuotaMap();
                                projectLandPartProductTypeQuotaMap.setId(subProjectQuoteTypeVo.getTypeQuoteId());
                                projectLandPartProductTypeQuotaMap.setIsDelete(IsDeleteEnum.YES.getKey());
                                projectLandPartProductTypeQuotaMap.setUpdaterId(userVO.getId());
                                projectLandPartProductTypeQuotaMap.setUpdaterName(userVO.getName());
                                projectLandPartProductTypeQuotaMap.setUpdateTime(now);
                                projectLandPartProductTypeQuotaMapList.add(projectLandPartProductTypeQuotaMap);
                            }
                        }
                    }

                    if (projectLandPartProductTypeMapList != null && projectLandPartProductTypeMapList.size() > 0) {
                        projectLandPartProductTypeMapMapper.updateIsDeleteBatch(projectLandPartProductTypeMapList);
                    }
                    if (projectLandPartProductTypeQuotaMapList != null && projectLandPartProductTypeQuotaMapList.size() > 0) {
                        projectLandPartProductTypeQuotaMapMapper.updateIsDeleteBatch(projectLandPartProductTypeQuotaMapList);
                    }

                    Map<String, String> productMap = new HashMap<String, String>();
                    List<String> landProductIds = new ArrayList<String>();
                    List<BoProjectLandPartProductTypeMap> insertProjectLandPartProductTypeMapList = new ArrayList<BoProjectLandPartProductTypeMap>();
                    List<BoProjectLandPartProductTypeQuotaMap> insertProjectLandPartProductTypeQuotaMapList = new ArrayList<BoProjectLandPartProductTypeQuotaMap>();
                    List<BoLandPartProductTypeMapExtendDto> landPartProductTypeMaps = projectLandPartProductTypeMapMapper.selectLandProductByProjectId(mdmProjectInfos.get(0).getParentSid(), subProjectAddLandReqParam.getLandId());
                    if (landPartProductTypeMaps != null && landPartProductTypeMaps.size() > 0) {
                        for (int i = 0; i < landPartProductTypeMaps.size(); i++) {
                            BoLandPartProductTypeMapExtendDto boLandPartProductTypeMapExtendDto = landPartProductTypeMaps.get(i);
                            LocalDateTime time = LocalDateTime.now();

                            BoProjectLandPartProductTypeMap projectLandPartProductTypeMap = new BoProjectLandPartProductTypeMap();
                            String id = UUIDUtils.create();
                            projectLandPartProductTypeMap.setId(id);
                            projectLandPartProductTypeMap.setProjectLandPartId(subProjectAddLandReqParam.getLandId());
                            projectLandPartProductTypeMap.setProductTypeId(boLandPartProductTypeMapExtendDto.getProductTypeId());
                            projectLandPartProductTypeMap.setProductTypeCode(boLandPartProductTypeMapExtendDto.getProductTypeCode());
                            projectLandPartProductTypeMap.setProductTypeName(boLandPartProductTypeMapExtendDto.getProductTypeName());
                            projectLandPartProductTypeMap.setIsDelete(IsDeleteEnum.NO.getKey());
                            projectLandPartProductTypeMap.setIsDisable(IsDisableEnum.NO.getKey());
                            projectLandPartProductTypeMap.setCreateTime(time);
                            projectLandPartProductTypeMap.setCreaterId(userVO.getId());
                            projectLandPartProductTypeMap.setCreaterName(userVO.getName());
                            projectLandPartProductTypeMap.setPropertyRightAttr(boLandPartProductTypeMapExtendDto.getAttrQuotaValue());
                            //projectLandPartProductTypeMapMapper.insert(projectLandPartProductTypeMap);
                            insertProjectLandPartProductTypeMapList.add(projectLandPartProductTypeMap);

                            landProductIds.add(boLandPartProductTypeMapExtendDto.getId());
                            productMap.put(boLandPartProductTypeMapExtendDto.getId(), id);
                        }

                        if (landProductIds != null && landProductIds.size() > 0) {
                            List<BoLandPartProductTypeQuotaMap> landPartProductTypeQuotaMaps = projectLandPartProductTypeQuotaMapMapper.selectLandQuoteByProductIds(landProductIds);
                            if (landPartProductTypeQuotaMaps != null && landPartProductTypeQuotaMaps.size() > 0) {
                                for (int i = 0; i < landPartProductTypeQuotaMaps.size(); i++) {
                                    BoLandPartProductTypeQuotaMap boLandPartProductTypeQuotaMap = landPartProductTypeQuotaMaps.get(i);

                                    BoProjectLandPartProductTypeQuotaMap projectLandPartProductTypeQuotaMap = new BoProjectLandPartProductTypeQuotaMap();
                                    projectLandPartProductTypeQuotaMap.setId(UUIDUtils.create());
                                    projectLandPartProductTypeQuotaMap.setProjectLandPartProductTypeId(productMap.get(boLandPartProductTypeQuotaMap.getLandPartProductTypeId()));
                                    projectLandPartProductTypeQuotaMap.setQuotaGroupMapId(boLandPartProductTypeQuotaMap.getQuotaGroupMapId());
                                    projectLandPartProductTypeQuotaMap.setQuotaId(boLandPartProductTypeQuotaMap.getQuotaId());
                                    projectLandPartProductTypeQuotaMap.setQuotaCode(boLandPartProductTypeQuotaMap.getQuotaCode());
                                    projectLandPartProductTypeQuotaMap.setQuotaValue(boLandPartProductTypeQuotaMap.getQuotaValue());
                                    projectLandPartProductTypeQuotaMap.setIsDelete(IsDeleteEnum.NO.getKey());
                                    projectLandPartProductTypeQuotaMap.setIsDisable(IsDisableEnum.NO.getKey());
                                    projectLandPartProductTypeQuotaMap.setCreateTime(LocalDateTime.now());
                                    projectLandPartProductTypeQuotaMap.setCreaterId(userVO.getId());
                                    projectLandPartProductTypeQuotaMap.setCreaterName(userVO.getName());
                                    //projectLandPartProductTypeQuotaMapMapper.insert(projectLandPartProductTypeQuotaMap);
                                    insertProjectLandPartProductTypeQuotaMapList.add(projectLandPartProductTypeQuotaMap);
                                }
                            }
                        }

                        if(CollectionUtils.isNotEmpty(insertProjectLandPartProductTypeMapList)){
                            projectLandPartProductTypeMapService.saveBatch(insertProjectLandPartProductTypeMapList,100);
                        }

                        if(CollectionUtils.isNotEmpty(insertProjectLandPartProductTypeQuotaMapList)){
                            projectLandPartProductTypeQuotaMapService.saveBatch(insertProjectLandPartProductTypeQuotaMapList,100);
                        }
                    }
                }
            }
        } else {
            String projectLandPartMapId = "";
            BoProjectLandPartMap projectLandPartMap = projectLandPartMapMapper.selectProjectLandBySubProjectIdAndLandId(mdmProjectInfos.get(0).getParentSid(), subProjectAddLandReqParam.getLandId());

            SubTotalAreaDto subTotalAreaDto = null;
            if(subIds != null && subIds.size() > 0){
                subTotalAreaDto = projectLandPartMapMapper.selectSubTotalAreaBySubIdsAndLandId(subIds, subProjectAddLandReqParam.getLandId());
            }

            if (projectLandPartMap != null) {
                if (subTotalAreaDto != null) {
                    StringBuffer resAreaMsg = new StringBuffer();
                    String subTotalArea = sub(Double.parseDouble(!StringUtils.isNotBlank(projectLandPartMap.getTotalUseLandArea())?"0":projectLandPartMap.getTotalUseLandArea()),
                            Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumTotalArea())?"0":subTotalAreaDto.getSumTotalArea()));
                    subTotalArea = sub(Double.parseDouble(!StringUtils.isNotBlank(subTotalArea)?"0":subTotalArea), Double.parseDouble(!StringUtils.isNotBlank(subProjectAddLandReqParam.getTotalMeasure())?"0":subProjectAddLandReqParam.getTotalMeasure()));
                    if (Double.parseDouble(StringUtils.isBlank(subTotalArea)?"0":subTotalArea) < 0) {
                        resAreaMsg.append(projectLandPartMap.getLandPartName() + subTotalArea);
                    }

                    String subUseArea = sub(Double.parseDouble(!StringUtils.isNotBlank(projectLandPartMap.getCanUseLandArea())?"0":projectLandPartMap.getCanUseLandArea()),
                            Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumUseArea())?"0":subTotalAreaDto.getSumUseArea()));
                    subUseArea = sub(Double.parseDouble(!StringUtils.isNotBlank(subUseArea)?"0":subUseArea), Double.parseDouble(!StringUtils.isNotBlank(subProjectAddLandReqParam.getCanUseMeasure())?"0":subProjectAddLandReqParam.getCanUseMeasure()));
                    if (Double.parseDouble(StringUtils.isBlank(subUseArea)?"0":subUseArea) < 0) {
                        resAreaMsg.append(",").append(projectLandPartMap.getCanUseLandArea() + subUseArea);
                    }

                    String subCover = sub(Double.parseDouble(!StringUtils.isNotBlank(projectLandPartMap.getBuildingLandCoverArea())?"0":projectLandPartMap.getBuildingLandCoverArea()),
                            Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumCoverArea())?"0":subTotalAreaDto.getSumCoverArea()));
                    subCover = sub(Double.parseDouble(!StringUtils.isNotBlank(subCover)?"0":subCover), Double.parseDouble(!StringUtils.isNotBlank(subProjectAddLandReqParam.getBuildCoverMeasure())?"0":subProjectAddLandReqParam.getBuildCoverMeasure()));
                    if (Double.parseDouble(StringUtils.isBlank(subCover)?"0":subCover) < 0) {
                        resAreaMsg.append(projectLandPartMap.getLandPartName() + subCover);
                    }

                    String subTake = sub(Double.parseDouble(!StringUtils.isNotBlank(projectLandPartMap.getTakeByLandUseArea())?"0":projectLandPartMap.getTakeByLandUseArea()),
                            Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumTakeArea())?"0":subTotalAreaDto.getSumTakeArea()));
                    subTake = sub(Double.parseDouble(!StringUtils.isNotBlank(subTake)?"0":subTake), Double.parseDouble(!StringUtils.isNotBlank(subProjectAddLandReqParam.getUseTakeMeasure())?"0":subProjectAddLandReqParam.getUseTakeMeasure()));
                    if (Double.parseDouble(StringUtils.isBlank(subTake)?"0":subTake) < 0) {
                        resAreaMsg.append(projectLandPartMap.getLandPartName() + subTake);
                    }

                    String subPrice = sub(Double.parseDouble(!StringUtils.isNotBlank(projectLandPartMap.getLandGetPrice())?"0":projectLandPartMap.getLandGetPrice()),
                            Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumGetPrice())?"0":subTotalAreaDto.getSumGetPrice()));
                    subPrice = sub(Double.parseDouble(!StringUtils.isNotBlank(subPrice)?"0":subPrice), Double.parseDouble(!StringUtils.isNotBlank(subProjectAddLandReqParam.getGetLandPrice())?"0":subProjectAddLandReqParam.getGetLandPrice()));
                    if (Double.parseDouble(StringUtils.isBlank(subPrice)?"0":subPrice) < 0) {
                        resAreaMsg.append(projectLandPartMap.getLandPartName() + subPrice);
                    }

                    String subAbove = sub(Double.parseDouble(!StringUtils.isNotBlank(projectLandPartMap.getAboveGroundCalcVolumeArea())?"0":projectLandPartMap.getAboveGroundCalcVolumeArea()),
                            Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumAboveArea())?"0":subTotalAreaDto.getSumAboveArea()));
                    subAbove = sub(Double.parseDouble(!StringUtils.isNotBlank(subAbove)?"0":subAbove), Double.parseDouble(!StringUtils.isNotBlank(subProjectAddLandReqParam.getUpLandMeasure())?"0":subProjectAddLandReqParam.getUpLandMeasure()));
                    if (Double.parseDouble(StringUtils.isBlank(subAbove)?"0":subAbove) < 0) {
                        resAreaMsg.append(projectLandPartMap.getLandPartName() + subAbove);
                    }

                    String subUnder = sub(Double.parseDouble(!StringUtils.isNotBlank(projectLandPartMap.getUnderGroundCalcVolumeArea())?"0":projectLandPartMap.getUnderGroundCalcVolumeArea()),
                            Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumUnderArea())?"0":subTotalAreaDto.getSumUnderArea()));
                    subUnder = sub(Double.parseDouble(!StringUtils.isNotBlank(subUnder)?"0":subUnder), Double.parseDouble(!StringUtils.isNotBlank(subProjectAddLandReqParam.getDownLandMeasure())?"0":subProjectAddLandReqParam.getDownLandMeasure()));
                    if (Double.parseDouble(StringUtils.isBlank(subUnder)?"0":subUnder) < 0) {
                        resAreaMsg.append(projectLandPartMap.getLandPartName() + subUnder);
                    }

                    if (resAreaMsg != null && resAreaMsg.length() > 0) {
                        jsonResult.setCode(CodeEnum.LAND_AREAT_EXTENDS.getKey());
                        jsonResult.setMsg(resAreaMsg.toString()+"已经超标");
                        return jsonResult;
                    }
                }

                BoProjectLandPartMap subProjectLandPartMap = projectLandPartMapMapper.selectProjectLandBySubProjectIdAndLandId(subProjectAddLandReqParam.getSubProjectId(), subProjectAddLandReqParam.getLandId());
                subProjectLandPartMap.setTotalUseLandArea(subProjectAddLandReqParam.getTotalMeasure());
                subProjectLandPartMap.setCapacityBuildingArea(subProjectAddLandReqParam.getMeterBuildMeasure());
                subProjectLandPartMap.setCanUseLandArea(subProjectAddLandReqParam.getCanUseMeasure());
                subProjectLandPartMap.setIsAllDev(isDev);
                subProjectLandPartMap.setUpdateTime(LocalDateTime.now());
                subProjectLandPartMap.setUpdaterId(userVO.getId());
                subProjectLandPartMap.setUpdaterName(userVO.getName());
                projectLandPartMapMapper.updateById(subProjectLandPartMap);
                projectLandPartMapId = subProjectLandPartMap.getId();
            } else {
                jsonResult.setCode(CodeEnum.LAND_INFO_EXTENDS.getKey());
                jsonResult.setMsg(CodeEnum.LAND_INFO_EXTENDS.getValue());
                return jsonResult;
            }

            Map<String, String> extQuoteMap = new HashMap<String, String>();
            StringBuffer restMsg = new StringBuffer();
            List<BoLandPartProductTypeQuotaMapExtendsDto> boLandPartProductTypeQuotaMapExtendsDtos = landPartProductTypeQuotaMapMapper.selectTotalQuoteByProjectIdAndLandId(mdmProjectInfos.get(0).getParentSid(), subProjectAddLandReqParam.getLandId());
            if (boLandPartProductTypeQuotaMapExtendsDtos != null && boLandPartProductTypeQuotaMapExtendsDtos.size() > 0) {
                for (int i = 0; i < boLandPartProductTypeQuotaMapExtendsDtos.size(); i++) {
                    BoLandPartProductTypeQuotaMapExtendsDto boLandPartProductTypeQuotaMapExtendsDto = boLandPartProductTypeQuotaMapExtendsDtos.get(i);
                    extQuoteMap.put(boLandPartProductTypeQuotaMapExtendsDto.getAttrQuotaValue()+"&"+boLandPartProductTypeQuotaMapExtendsDto.getProductCode() + "&" + boLandPartProductTypeQuotaMapExtendsDto.getQuotaCode()
                            , !StringUtils.isNotBlank(boLandPartProductTypeQuotaMapExtendsDto.getQuotaValue())?"0":boLandPartProductTypeQuotaMapExtendsDto.getQuotaValue());

                }
            }

            if (projectProductTypeQuotaDtos != null && projectProductTypeQuotaDtos.size() > 0) {
                for (int i = 0; i < projectProductTypeQuotaDtos.size(); i++) {
                    ProjectProductTypeQuotaDto projectProductTypeQuotaDto = projectProductTypeQuotaDtos.get(i);
                    String productCode = projectProductTypeQuotaDto.getProductCode();
                    String quoteCode = projectProductTypeQuotaDto.getQuoteCode();
                    String attrQuotaValue = projectProductTypeQuotaDto.getAttrQuotaValue();

                    String landQuoteValue = extQuoteMap.get(productCode + "&" + quoteCode);
                    if (StringUtils.isNotBlank(landQuoteValue)) {
                        String subValue = sub(Double.parseDouble(landQuoteValue),
                                Double.parseDouble(!StringUtils.isNotBlank(projectProductTypeQuotaDto.getQuoteValue())?"0":projectProductTypeQuotaDto.getQuoteValue()));
                        extQuoteMap.put(attrQuotaValue+"&"+productCode + "&" + quoteCode, String.valueOf(subValue));
                    }
                }
            }

            List<SubProjectProductTypeVo> quoteTypeList = subProjectAddLandReqParam.getQuoteTypeList();
            for (int i = 0; i < quoteTypeList.size(); i++) {
                SubProjectProductTypeVo subProjectProductTypeVo = quoteTypeList.get(i);
                if (subProjectProductTypeVo != null) {
                    List<SubProjectQuoteTypeVo> subProjectQuoteTypeDetails = subProjectProductTypeVo.getSubProjectQuoteTypeDetails();
                    for (int j = 0; j < subProjectQuoteTypeDetails.size(); j++) {
                        SubProjectQuoteTypeVo subProjectQuoteTypeVo = subProjectQuoteTypeDetails.get(j);
                        String productCode = subProjectProductTypeVo.getProductCode();
                        String attrQuotaValue = subProjectProductTypeVo.getAttrQuotaValue();
                        String quoteCode = subProjectQuoteTypeVo.getQuoteCode();
                        String landQuoteValue = extQuoteMap.get(attrQuotaValue + "&" +productCode + "&" + quoteCode);
                        if (StringUtils.isNotBlank(landQuoteValue)) {
                            String quotaValue = subProjectQuoteTypeDetails.get(j).getQuoteValue();
                            String subValue = sub(Double.parseDouble(!StringUtils.isNotBlank(landQuoteValue)?"0":landQuoteValue),
                                    Double.parseDouble(!StringUtils.isNotBlank(quotaValue)?"0":quotaValue));
                            if (Double.parseDouble(StringUtils.isBlank(subValue)?"0":subValue) < 0) {
                                BoQuotaGroupMap boQuotaGroupMap = formatMap.get(quoteCode);
                                if (i < boLandPartProductTypeQuotaMapExtendsDtos.size() - 1) {
                                    restMsg.append(boQuotaGroupMap.getName()).append(",");
                                } else {
                                    restMsg.append(boQuotaGroupMap.getName());
                                }
                            }
                        }
                    }
                }
            }

            if (restMsg != null && restMsg.length() > 0) {
                jsonResult.setCode(CodeEnum.LAND_QUOTE_USE_ALL_EXTENDS.getKey());
                jsonResult.setMsg(restMsg.toString()+"指标已超标");
                return jsonResult;
            }

            List<String> delProjectLandPartProductTypeMapIds = new ArrayList<String>();
            List<BoProjectLandPartProductTypeMap> projectLandPartProductTypeMapList = new ArrayList<BoProjectLandPartProductTypeMap>();
            List<BoProjectLandPartProductTypeQuotaMap> projectLandPartProductTypeQuotaMapList = new ArrayList<BoProjectLandPartProductTypeQuotaMap>();
            List<BoProjectLandPartProductTypeMap> boProjectLandPartProductTypeMaps = projectLandPartProductTypeMapMapper.selectProductBySubIdAndLandId(subProjectAddLandReqParam.getSubProjectId(), subProjectAddLandReqParam.getLandId());
            if (boProjectLandPartProductTypeMaps != null && boProjectLandPartProductTypeMaps.size() > 0) {
                for (int i = 0; i < boProjectLandPartProductTypeMaps.size(); i++) {
                    BoProjectLandPartProductTypeMap boProjectLandPartProductTypeMap = boProjectLandPartProductTypeMaps.get(i);
                    boProjectLandPartProductTypeMap.setIsDelete(IsDeleteEnum.YES.getKey());
                    boProjectLandPartProductTypeMap.setUpdateTime(LocalDateTime.now());
                    boProjectLandPartProductTypeMap.setUpdaterId(userVO.getId());
                    boProjectLandPartProductTypeMap.setUpdaterName(userVO.getName());
                    projectLandPartProductTypeMapList.add(boProjectLandPartProductTypeMap);
                    delProjectLandPartProductTypeMapIds.add(boProjectLandPartProductTypeMap.getId());
                }
            }
            List<BoProjectLandPartProductTypeQuotaMap> boProjectLandPartProductTypeQuotaMaps = projectLandPartProductTypeQuotaMapMapper.selectByTypeQuoteIds(delProjectLandPartProductTypeMapIds);
            if (boProjectLandPartProductTypeQuotaMaps != null && boProjectLandPartProductTypeQuotaMaps.size() > 0) {
                for (int i = 0; i < boProjectLandPartProductTypeQuotaMaps.size(); i++) {
                    BoProjectLandPartProductTypeQuotaMap boProjectLandPartProductTypeQuotaMap = boProjectLandPartProductTypeQuotaMaps.get(i);
                    boProjectLandPartProductTypeQuotaMap.setIsDelete(IsDeleteEnum.YES.getKey());
                    boProjectLandPartProductTypeQuotaMap.setUpdateTime(LocalDateTime.now());
                    boProjectLandPartProductTypeQuotaMap.setUpdaterId(userVO.getId());
                    boProjectLandPartProductTypeQuotaMap.setUpdaterName(userVO.getName());
                    projectLandPartProductTypeQuotaMapList.add(boProjectLandPartProductTypeQuotaMap);
                }
            }

            if (projectLandPartProductTypeMapList != null && projectLandPartProductTypeMapList.size() > 0) {
                projectLandPartProductTypeMapMapper.updateIsDeleteBatch(projectLandPartProductTypeMapList);
            }
            if (projectLandPartProductTypeQuotaMapList != null && projectLandPartProductTypeQuotaMapList.size() > 0) {
                projectLandPartProductTypeQuotaMapMapper.updateIsDeleteBatch(projectLandPartProductTypeQuotaMapList);
            }

            List<BoProjectLandPartProductTypeMap> insertProjectLandPartProductTypeMapList = new ArrayList<BoProjectLandPartProductTypeMap>();
            List<BoProjectLandPartProductTypeQuotaMap> insertProjectLandPartProductTypeQuotaMapList = new ArrayList<BoProjectLandPartProductTypeQuotaMap>();
            for (int i = 0; i < quoteTypeList.size(); i++) {
                SubProjectProductTypeVo subProjectProductTypeVo = quoteTypeList.get(i);
                BoProjectLandPartProductTypeMap projectLandPartProductTypeMap = new BoProjectLandPartProductTypeMap();
                String id = UUIDUtils.create();
                projectLandPartProductTypeMap.setId(id);
                projectLandPartProductTypeMap.setProjectLandPartId(projectLandPartMapId);
                projectLandPartProductTypeMap.setProductTypeId(subProjectProductTypeVo.getProductTypeId());
                projectLandPartProductTypeMap.setProductTypeCode(subProjectProductTypeVo.getProductCode());
                projectLandPartProductTypeMap.setProductTypeName(subProjectProductTypeVo.getProductName());
                projectLandPartProductTypeMap.setIsDelete(IsDeleteEnum.NO.getKey());
                projectLandPartProductTypeMap.setIsDisable(IsDisableEnum.NO.getKey());
                projectLandPartProductTypeMap.setCreateTime(LocalDateTime.now());
                projectLandPartProductTypeMap.setCreaterId(userVO.getId());
                projectLandPartProductTypeMap.setCreaterName(userVO.getName());
                projectLandPartProductTypeMap.setPropertyRightAttr(subProjectProductTypeVo.getAttrQuotaValue());
                //projectLandPartProductTypeMapMapper.insert(projectLandPartProductTypeMap);
                insertProjectLandPartProductTypeMapList.add(projectLandPartProductTypeMap);

                for (int j = 0; j < subProjectProductTypeVo.getSubProjectQuoteTypeDetails().size(); j++) {
                    SubProjectQuoteTypeVo subProjectQuoteTypeVo = subProjectProductTypeVo.getSubProjectQuoteTypeDetails().get(j);
                    BoProjectLandPartProductTypeQuotaMap projectLandPartProductTypeQuotaMap = new BoProjectLandPartProductTypeQuotaMap();
                    projectLandPartProductTypeQuotaMap.setId(UUIDUtils.create());
                    projectLandPartProductTypeQuotaMap.setProjectLandPartProductTypeId(id);
                    projectLandPartProductTypeQuotaMap.setQuotaGroupMapId(subProjectQuoteTypeVo.getGroupMapId());
                    projectLandPartProductTypeQuotaMap.setQuotaId(subProjectQuoteTypeVo.getQuoteId());
                    projectLandPartProductTypeQuotaMap.setQuotaCode(subProjectQuoteTypeVo.getQuoteCode());
                    projectLandPartProductTypeQuotaMap.setQuotaValue(subProjectQuoteTypeVo.getQuoteValue());
                    projectLandPartProductTypeQuotaMap.setIsDelete(IsDeleteEnum.NO.getKey());
                    projectLandPartProductTypeQuotaMap.setIsDisable(IsDisableEnum.NO.getKey());
                    projectLandPartProductTypeQuotaMap.setCreateTime(LocalDateTime.now());
                    projectLandPartProductTypeQuotaMap.setCreaterId(userVO.getId());
                    projectLandPartProductTypeQuotaMap.setCreaterName(userVO.getName());
                    //projectLandPartProductTypeQuotaMapMapper.insert(projectLandPartProductTypeQuotaMap);
                    insertProjectLandPartProductTypeQuotaMapList.add(projectLandPartProductTypeQuotaMap);
                }
            }

            if(CollectionUtils.isNotEmpty(insertProjectLandPartProductTypeMapList)){
                projectLandPartProductTypeMapService.saveBatch(insertProjectLandPartProductTypeMapList,100);
            }

            if(CollectionUtils.isNotEmpty(insertProjectLandPartProductTypeQuotaMapList)){
                projectLandPartProductTypeQuotaMapService.saveBatch(insertProjectLandPartProductTypeQuotaMapList,100);
            }
        }

        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());

        return jsonResult;
    }

    @Override
    public JSONResult selectLandDetailsToSubProject(String subProjectId, String landId, JSONResult jsonResult) throws
            Exception {
        List<String> landPartProductTypeMapIds = new ArrayList<String>();
        BoProjectLandPartMap boProjectLandPartMap = projectLandPartMapMapper.selectProjectLandBySubProjectIdAndLandId(subProjectId, landId);
        if (boProjectLandPartMap != null) {
            List<BoProjectLandPartProductTypeMap> projectLandPartProductTypeMaps = projectLandPartProductTypeMapMapper.selectProductBySubIdAndLandId(subProjectId, landId);
            if (projectLandPartProductTypeMaps != null && projectLandPartProductTypeMaps.size() > 0) {
                for (int i = 0; i < projectLandPartProductTypeMaps.size(); i++) {
                    BoProjectLandPartProductTypeMap boProjectLandPartProductTypeMap = projectLandPartProductTypeMaps.get(i);
                    landPartProductTypeMapIds.add(boProjectLandPartProductTypeMap.getId());
                }
            }

            Map<String, BoQuotaGroupMap> formatMap = new HashMap<String, BoQuotaGroupMap>();
            QueryWrapper<BoQuotaGroupMap> queryTypeQuotaWrapper = new QueryWrapper<BoQuotaGroupMap>();
            queryTypeQuotaWrapper.eq("group_code", "LAND_PART_PRODUCT_TYPE")
                    .eq("is_delete", IsDeleteEnum.NO.getKey())
                    .eq("is_disable", IsDisableEnum.NO.getKey());
            List<BoQuotaGroupMap> boQuotaGroupMaps = quotaGroupMapMapper.selectList(queryTypeQuotaWrapper);
            if(CollectionUtils.isNotEmpty(boQuotaGroupMaps)){
                for (int i = 0; i < boQuotaGroupMaps.size(); i++){
                    BoQuotaGroupMap boQuotaGroupMap = boQuotaGroupMaps.get(i);
                    formatMap.put(boQuotaGroupMap.getCode(), boQuotaGroupMap);
                }
            }

            List<SubProjectProductTypeVo> subProjectProductTypeDetails = new ArrayList<SubProjectProductTypeVo>();
            if (landPartProductTypeMapIds != null && landPartProductTypeMapIds.size() > 0) {
                List<BoProjectLandPartProductTypeQuotaMap> projectLandPartProductTypeQuotaMaps = projectLandPartProductTypeQuotaMapMapper.selectByTypeQuoteIds(landPartProductTypeMapIds);
                for (int i = 0; i < projectLandPartProductTypeMaps.size(); i++) {
                    BoProjectLandPartProductTypeMap boProjectLandPartProductTypeMap = projectLandPartProductTypeMaps.get(i);
                    String id = boProjectLandPartProductTypeMap.getId();

                    List<SubProjectQuoteTypeVo> subProjectQuoteTypeVoDetails = new ArrayList<SubProjectQuoteTypeVo>();
                    if (projectLandPartProductTypeQuotaMaps != null && projectLandPartProductTypeQuotaMaps.size() > 0) {
                        for (int j = 0; j < projectLandPartProductTypeQuotaMaps.size(); j++) {
                            BoProjectLandPartProductTypeQuotaMap boProjectLandPartProductTypeQuotaMap = projectLandPartProductTypeQuotaMaps.get(j);
                            if (boProjectLandPartProductTypeQuotaMap != null) {
                                String projectLandPartProductTypeId = boProjectLandPartProductTypeQuotaMap.getProjectLandPartProductTypeId();
                                if (projectLandPartProductTypeId.equals(id)) {
                                    SubProjectQuoteTypeVo subProjectQuoteTypeVo = new SubProjectQuoteTypeVo();
                                    subProjectQuoteTypeVo.setTypeQuoteId(boProjectLandPartProductTypeQuotaMap.getId());
                                    subProjectQuoteTypeVo.setQuoteId(boProjectLandPartProductTypeQuotaMap.getQuotaId());
                                    subProjectQuoteTypeVo.setQuoteCode(boProjectLandPartProductTypeQuotaMap.getQuotaCode());
                                    subProjectQuoteTypeVo.setQuoteValue(boProjectLandPartProductTypeQuotaMap.getQuotaValue());
                                    subProjectQuoteTypeVo.setGroupMapId(boProjectLandPartProductTypeQuotaMap.getQuotaGroupMapId());

                                    BoQuotaGroupMap boQuotaGroupMap = formatMap.get(boProjectLandPartProductTypeQuotaMap.getQuotaCode());
                                    if(boQuotaGroupMap != null){
                                        subProjectQuoteTypeVo.setQuoteFormatName(boQuotaGroupMap.getName());
                                        subProjectQuoteTypeVo.setQuoteFormatType(boQuotaGroupMap.getQuotaType());
                                        subProjectQuoteTypeVo.setQuoteFormatValueType(boQuotaGroupMap.getValueType());
                                        subProjectQuoteTypeVo.setQuoteFormatData(boQuotaGroupMap.getDataFormat());
                                        subProjectQuoteTypeVo.setQuoteFormatUnit(boQuotaGroupMap.getUnit());
                                        subProjectQuoteTypeVo.setQuoteFormatIsGather(boQuotaGroupMap.getIsGather());
                                        subProjectQuoteTypeVo.setQuoteFormatIsEdit(boQuotaGroupMap.getIsEdit());
                                        subProjectQuoteTypeVo.setQuoteFormatFormat(boQuotaGroupMap.getFormula());
                                        subProjectQuoteTypeVo.setQuoteFormatRegexps(boQuotaGroupMap.getRegexps());
                                        subProjectQuoteTypeVo.setQuoteFormatColspan(boQuotaGroupMap.getColspan());
                                    }

                                    subProjectQuoteTypeVoDetails.add(subProjectQuoteTypeVo);
                                }
                            }
                        }

                        SubProjectProductTypeVo subProjectProductTypeVo = new SubProjectProductTypeVo();
                        subProjectProductTypeVo.setProductTypeId(boProjectLandPartProductTypeMap.getProductTypeId());
                        subProjectProductTypeVo.setProductId(boProjectLandPartProductTypeMap.getId());
                        subProjectProductTypeVo.setProductCode(boProjectLandPartProductTypeMap.getProductTypeCode());
                        subProjectProductTypeVo.setProductName(boProjectLandPartProductTypeMap.getProductTypeName());
                        subProjectProductTypeVo.setSubProjectQuoteTypeDetails(subProjectQuoteTypeVoDetails);
                        subProjectProductTypeVo.setAttrQuotaValue(boProjectLandPartProductTypeMap.getPropertyRightAttr());
                        subProjectProductTypeDetails.add(subProjectProductTypeVo);
                    }
                }

                SubProjectLandDetailsVo subProjectLandDetailsVo = new SubProjectLandDetailsVo();
                subProjectLandDetailsVo.setMeterBuildMeasure(boProjectLandPartMap.getCapacityBuildingArea());
                subProjectLandDetailsVo.setMeterBuildMeasure(boProjectLandPartMap.getTotalUseLandArea());
                subProjectLandDetailsVo.setCanUseMeasure(boProjectLandPartMap.getCanUseLandArea());
                subProjectLandDetailsVo.setBuildCoverMeasure(boProjectLandPartMap.getBuildingLandCoverArea());
                subProjectLandDetailsVo.setUseTakeMeasure(boProjectLandPartMap.getTakeByLandUseArea());
                subProjectLandDetailsVo.setGetLandPrice(boProjectLandPartMap.getLandGetPrice());
                subProjectLandDetailsVo.setUpLandMeasure(boProjectLandPartMap.getAboveGroundCalcVolumeArea());
                subProjectLandDetailsVo.setDownLandMeasure(boProjectLandPartMap.getUnderGroundCalcVolumeArea());
                subProjectLandDetailsVo.setSubProjectId(subProjectId);
                subProjectLandDetailsVo.setSubProjectProductTypeDetails(subProjectProductTypeDetails);

                jsonResult.setData(subProjectLandDetailsVo);
            }
        }

        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());

        return jsonResult;
    }

    @Override
    public JSONResult selectCanUseLandDetailsToSubProject(String subProjectId, String landId, JSONResult
            jsonResult) throws Exception {

        List<MdmProjectInfo> subProjectInfos = projectInfoMapper.selectSubProjectBySubId(subProjectId);
        List<String> subIds = Optional.ofNullable(subProjectInfos)
                .orElseGet(ArrayList::new)
                .stream().map(MdmProjectInfo::getSid)
                .collect(Collectors.toList());

        Map<String, BoQuotaGroupMap> formatMap = new HashMap<String, BoQuotaGroupMap>();
        QueryWrapper<BoQuotaGroupMap> queryTypeQuotaWrapper = new QueryWrapper<BoQuotaGroupMap>();
        queryTypeQuotaWrapper.eq("group_code","LAND_PART_PRODUCT_TYPE")
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        List<BoQuotaGroupMap> boQuotaGroupMaps = quotaGroupMapMapper.selectList(queryTypeQuotaWrapper);
        if(CollectionUtils.isNotEmpty(boQuotaGroupMaps)){
            for (int i = 0; i < boQuotaGroupMaps.size(); i++){
                BoQuotaGroupMap boQuotaGroupMap = boQuotaGroupMaps.get(i);
                formatMap.put(boQuotaGroupMap.getCode(), boQuotaGroupMap);
            }
        }

        List<BoLandPartProductTypeQuotaMapExtendsDto> boLandPartProductTypeQuotaMapExtendsDtos = landPartProductTypeQuotaMapMapper.selectTotalQuoteByProjectIdAndLandId(subProjectInfos.get(0).getParentSid(), landId);
        if (boLandPartProductTypeQuotaMapExtendsDtos != null && boLandPartProductTypeQuotaMapExtendsDtos.size() > 0) {

            Map<String, String> extMap = new HashMap<String, String>();
            for (int i = 0; i < boLandPartProductTypeQuotaMapExtendsDtos.size(); i++) {
                BoLandPartProductTypeQuotaMapExtendsDto boLandPartProductTypeQuotaMapExtendsDto = boLandPartProductTypeQuotaMapExtendsDtos.get(i);
                String landproductCode = boLandPartProductTypeQuotaMapExtendsDto.getProductCode();
                String landquotaCode = boLandPartProductTypeQuotaMapExtendsDto.getQuotaCode();
                String attrQuotaValue = boLandPartProductTypeQuotaMapExtendsDto.getAttrQuotaValue();
                extMap.put(attrQuotaValue + "&" +landproductCode + "&" + landquotaCode, boLandPartProductTypeQuotaMapExtendsDto.getQuotaValue());
            }

            List<BoProjectLandPartProductTypeQuotaMapExtendsDto> projectLandPartProductTypeQuotaMapExtendsDtos = projectLandPartProductTypeQuotaMapMapper.selectTotalQuoteBySubProjectIdsAndLandId(subIds, landId);
            if (projectLandPartProductTypeQuotaMapExtendsDtos != null && projectLandPartProductTypeQuotaMapExtendsDtos.size() > 0) {
                for (int j = 0; j < projectLandPartProductTypeQuotaMapExtendsDtos.size(); j++) {
                    BoProjectLandPartProductTypeQuotaMapExtendsDto boProjectLandPartProductTypeQuotaMapExtendsDto = projectLandPartProductTypeQuotaMapExtendsDtos.get(j);
                    String subproductCode = boProjectLandPartProductTypeQuotaMapExtendsDto.getProductCode();
                    String subquotaCode = boProjectLandPartProductTypeQuotaMapExtendsDto.getQuotaCode();
                    String attrQuotaValue = boProjectLandPartProductTypeQuotaMapExtendsDto.getAttrQuotaValue();
                    String extValue = extMap.get(attrQuotaValue + "&" + subproductCode + "&" + subquotaCode);
                    if (StringUtils.isNotBlank(extValue)) {
                        String subValue = sub(Double.parseDouble(extValue),
                                Double.parseDouble(!StringUtils.isNotBlank(boProjectLandPartProductTypeQuotaMapExtendsDto.getSumQuoteValue())?"0":boProjectLandPartProductTypeQuotaMapExtendsDto.getSumQuoteValue()));
                        extMap.put(attrQuotaValue + "&" + subproductCode + "&" + subquotaCode, String.valueOf(subValue));
                    }
                }
            }

            List<BoLandPartProductTypeMapExtendDto> landPartProductTypeMapList = landPartProductTypeQuotaMapMapper.selectLandPartByProIdAndLand(subProjectInfos.get(0).getParentSid(), landId);
            if (landPartProductTypeMapList != null && landPartProductTypeMapList.size() > 0) {
                List<String> landPartIds = Optional.ofNullable(landPartProductTypeMapList)
                        .orElseGet(ArrayList::new)
                        .stream().map(BoLandPartProductTypeMap::getId)
                        .collect(Collectors.toList());

                List<SubProjectProductTypeVo> subProjectProductTypeDetails = new ArrayList<SubProjectProductTypeVo>();
                if (landPartIds != null && landPartIds.size() > 0) {
                    List<BoLandPartProductTypeQuotaMap> boLandPartProductTypeQuotaMaps = landPartProductTypeQuotaMapMapper.selectByTypeId(landPartIds);
                    if (boLandPartProductTypeQuotaMaps != null && boLandPartProductTypeQuotaMaps.size() > 0) {
                        for (int i = 0; i < landPartProductTypeMapList.size(); i++) {
                            BoLandPartProductTypeMapExtendDto boLandPartProductTypeMapExtendDto = landPartProductTypeMapList.get(i);

                            List<SubProjectQuoteTypeVo> subProjectQuoteTypeDetails = new ArrayList<SubProjectQuoteTypeVo>();
                            for (int j = 0; j < boLandPartProductTypeQuotaMaps.size(); j++) {
                                BoLandPartProductTypeQuotaMap boLandPartProductTypeQuotaMap = boLandPartProductTypeQuotaMaps.get(j);
                                String landPartProductTypeId = boLandPartProductTypeQuotaMap.getLandPartProductTypeId();

                                if (landPartProductTypeId.equals(boLandPartProductTypeMapExtendDto.getId())) {
                                    SubProjectQuoteTypeVo subProjectQuoteTypeVo = new SubProjectQuoteTypeVo();
                                    subProjectQuoteTypeVo.setQuoteId(boLandPartProductTypeQuotaMap.getQuotaId());
                                    subProjectQuoteTypeVo.setGroupMapId(boLandPartProductTypeQuotaMap.getQuotaGroupMapId());
                                    subProjectQuoteTypeVo.setQuoteCode(boLandPartProductTypeQuotaMap.getQuotaCode());
                                    subProjectQuoteTypeVo.setQuoteValue(extMap.get(boLandPartProductTypeMapExtendDto.getAttrQuotaValue()+"&"+boLandPartProductTypeMapExtendDto.getProductTypeCode() + "&" + boLandPartProductTypeQuotaMap.getQuotaCode()));

                                    BoQuotaGroupMap boQuotaGroupMap = formatMap.get(boLandPartProductTypeQuotaMap.getQuotaCode());
                                    if(boQuotaGroupMap != null){
                                        subProjectQuoteTypeVo.setQuoteFormatName(boQuotaGroupMap.getName());
                                        subProjectQuoteTypeVo.setQuoteFormatType(boQuotaGroupMap.getQuotaType());
                                        subProjectQuoteTypeVo.setQuoteFormatValueType(boQuotaGroupMap.getValueType());
                                        subProjectQuoteTypeVo.setQuoteFormatData(boQuotaGroupMap.getDataFormat());
                                        subProjectQuoteTypeVo.setQuoteFormatUnit(boQuotaGroupMap.getUnit());
                                        subProjectQuoteTypeVo.setQuoteFormatIsGather(boQuotaGroupMap.getIsGather());
                                        subProjectQuoteTypeVo.setQuoteFormatIsEdit(boQuotaGroupMap.getIsEdit());
                                        subProjectQuoteTypeVo.setQuoteFormatFormat(boQuotaGroupMap.getFormula());
                                        subProjectQuoteTypeVo.setQuoteFormatRegexps(boQuotaGroupMap.getRegexps());
                                        subProjectQuoteTypeVo.setQuoteFormatColspan(boQuotaGroupMap.getColspan());
                                    }

                                    subProjectQuoteTypeDetails.add(subProjectQuoteTypeVo);
                                }
                            }

                            SubProjectProductTypeVo subProjectProductTypeVo = new SubProjectProductTypeVo();
                            subProjectProductTypeVo.setProductTypeId(boLandPartProductTypeMapExtendDto.getProductTypeId());
                            subProjectProductTypeVo.setProductCode(boLandPartProductTypeMapExtendDto.getProductTypeCode());
                            subProjectProductTypeVo.setProductName(boLandPartProductTypeMapExtendDto.getProductTypeName());
                            subProjectProductTypeVo.setAttrQuotaValue(boLandPartProductTypeMapExtendDto.getAttrQuotaValue());
                            subProjectProductTypeVo.setSubProjectQuoteTypeDetails(subProjectQuoteTypeDetails);
                            subProjectProductTypeDetails.add(subProjectProductTypeVo);
                        }
                    }
                }

                BoProjectLandPartMap boProjectLandPartMap = projectLandPartMapMapper.selectProjectLandBySubProjectIdAndLandId(subProjectInfos.get(0).getParentSid(), landId);
                SubTotalAreaDto subTotalAreaDto = projectLandPartMapMapper.selectTotalSubAreaBySubIdsAndLandId(subProjectInfos, landId);

                if(boProjectLandPartMap != null){
                    String subTotal = boProjectLandPartMap.getTotalUseLandArea();
                    String subUse = boProjectLandPartMap.getCanUseLandArea();
                    String subCover = boProjectLandPartMap.getBuildingLandCoverArea();
                    String subTake = boProjectLandPartMap.getTakeByLandUseArea();
                    String subPrice = boProjectLandPartMap.getLandGetPrice();
                    String subAbove = boProjectLandPartMap.getAboveGroundCalcVolumeArea();
                    String subUnder = boProjectLandPartMap.getUnderGroundCalcVolumeArea();

                    SubProjectLandAreaDetailsVo subProjectLandAreaDetailsVo = new SubProjectLandAreaDetailsVo();
                    subProjectLandAreaDetailsVo.setTt_totalMeasure(subTotal);
                    String tt_buildArea = String.valueOf(add(Double.parseDouble(!StringUtils.isNotBlank(subAbove)?"0":subAbove), Double.parseDouble(!StringUtils.isNotBlank(subUnder)?"0":subUnder)));
                    subProjectLandAreaDetailsVo.setTt_meterBuildMeasure(tt_buildArea);
                    subProjectLandAreaDetailsVo.setTt_canUseMeasure(subUse);
                    subProjectLandAreaDetailsVo.setTt_buildCoverMeasure(subCover);
                    subProjectLandAreaDetailsVo.setTt_useTakeMeasure(subTake);
                    subProjectLandAreaDetailsVo.setTt_getLandPrice(subPrice);
                    subProjectLandAreaDetailsVo.setTt_upLandMeasure(subAbove);
                    subProjectLandAreaDetailsVo.setTt_downLandMeasure(subUnder);

                    if (subTotalAreaDto != null) {
                        subTotal = String.valueOf(sub(Double.parseDouble(!StringUtils.isNotBlank(subTotal)?"0":subTotal), Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumTotalArea())?"0":subTotalAreaDto.getSumTotalArea())));
                        subUse = String.valueOf(sub(Double.parseDouble(!StringUtils.isNotBlank(subUse)?"0":subUse), Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumUseArea())?"0":subTotalAreaDto.getSumUseArea())));
                        subCover = String.valueOf(sub(Double.parseDouble(!StringUtils.isNotBlank(subCover)?"0":subCover), Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumCoverArea())?"0":subTotalAreaDto.getSumCoverArea())));
                        subTake = String.valueOf(sub(Double.parseDouble(!StringUtils.isNotBlank(subTake)?"0":subTake), Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumTakeArea())?"0":subTotalAreaDto.getSumTakeArea())));
                        subPrice = String.valueOf(sub(Double.parseDouble(!StringUtils.isNotBlank(subPrice)?"0":subPrice), Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumGetPrice())?"0":subTotalAreaDto.getSumGetPrice())));
                        subAbove = String.valueOf(sub(Double.parseDouble(!StringUtils.isNotBlank(subAbove)?"0":subAbove), Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumAboveArea())?"0":subTotalAreaDto.getSumAboveArea())));
                        subUnder = String.valueOf(sub(Double.parseDouble(!StringUtils.isNotBlank(subUnder)?"0":subUnder), Double.parseDouble(!StringUtils.isNotBlank(subTotalAreaDto.getSumUnderArea())?"0":subTotalAreaDto.getSumUnderArea())));
                    }

                    SubProjectLandDetailsVo subProjectLandDetailsVo = new SubProjectLandDetailsVo();
                    subProjectLandDetailsVo.setTotalMeasure(subTotal);
                    String buildArea= String.valueOf(add(Double.parseDouble(!StringUtils.isNotBlank(subAbove)?"0":subAbove), Double.parseDouble(!StringUtils.isNotBlank(subUnder)?"0":subUnder)));
                    subProjectLandDetailsVo.setMeterBuildMeasure(buildArea);
                    subProjectLandDetailsVo.setCanUseMeasure(subUse);
                    subProjectLandDetailsVo.setBuildCoverMeasure(subCover);
                    subProjectLandDetailsVo.setUseTakeMeasure(subTake);
                    subProjectLandDetailsVo.setGetLandPrice(subPrice);
                    subProjectLandDetailsVo.setUpLandMeasure(subAbove);
                    subProjectLandDetailsVo.setDownLandMeasure(subUnder);
                    subProjectLandDetailsVo.setSubProjectId(subProjectId);
                    subProjectLandDetailsVo.setSubProjectProductTypeDetails(subProjectProductTypeDetails);

                    jsonResult.setData(subProjectLandDetailsVo);
                }
            }
        }

        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
        return jsonResult;
    }

    @Override
    public List<BoProjectLandPartMap> getProjectLandPartListByProjectQuotaExtendId(String projectQuotaExtendId) {
        QueryWrapper<BoProjectLandPartMap> queryWrapper = new QueryWrapper<>();
        queryWrapper.inSql("project_extend_id", "select substring_index(group_concat(id order by create_time desc),',',1) from bo_project_extend where project_id=(" +
                "select project_id from bo_project_quota_extend where id='" + projectQuotaExtendId +
                "') and version_status=" + VersionStatusEnum.PASSED.getKey() + " and is_delete=" + IsDeleteEnum.NO.getKey() + " and is_disable=" + IsDisableEnum.NO.getKey() + " group by project_id");
        return projectLandPartMapMapper.selectList(queryWrapper);
    }

    @Override
    public List<BoProjectLandPartMap> getProjectLandPartListByProjectExtendIds(Collection<String> projectExtendIds) {
        QueryWrapper<BoProjectLandPartMap> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("project_extend_id", projectExtendIds)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return list(queryWrapper);
    }


    @Override
    public JSONResult getLandInfoByProject(String versionId, JSONResult jsonResult) throws Exception {

        List<LandQuotaVo> landDetailsList = projectLandPartMapMapper.selectLandInfoByProjectId(versionId);
        if(DataUtils.isNotEmpty(landDetailsList)){
            jsonResult.setData(landDetailsList);
        }else{
            jsonResult.setData(new ArrayList<LandQuotaVo>());
        }

        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());

        return jsonResult;
    }

    @Override
    @Transactional
    public JSONResult addLandInfoToProject(ReqParamProjectAddLandInfo projectAddLandInfo, CurrentUserVO currentUser, JSONResult jsonResult) throws Exception {
        BoProjectExtend boProjectExtend = projectExtendMapper.selectById(projectAddLandInfo.getVersionId());
        if(boProjectExtend != null){
            if(StringUtils.isNotBlank(boProjectExtend.getVersion())){
                if(boProjectExtend.getVersionStatus() != null &&
                        (boProjectExtend.getVersionStatus().equals(VersionStatusEnum.CREATING.getKey()) || boProjectExtend.getVersionStatus().equals(VersionStatusEnum.REJECTED.getKey()))){
                    List<BoProjectLandPartMap> boProjectLandPartMaps = projectLandPartMapMapper.selectUseLandByLandId(projectAddLandInfo.getLandId());
                    if (CollectionUtils.isEmpty(boProjectLandPartMaps)) {

                        LandInfoDto landInfoDto = landinformationTbMapper.selectByLandId(projectAddLandInfo.getLandId());

                        BoProjectLandPartMap projectLandPartMap = new BoProjectLandPartMap();
                        projectLandPartMap.setId(UUIDUtils.create());
                        projectLandPartMap.setLandPartId(projectAddLandInfo.getLandId());
                        projectLandPartMap.setProjectExtendId(boProjectExtend.getId());
                        projectLandPartMap.setLandPartName(landInfoDto.getLandName());
                        projectLandPartMap.setIsAllDev(1);
                        projectLandPartMap.setTotalUseLandArea(landInfoDto.getTaotalBuildMeasure());
                        projectLandPartMap.setCapacityBuildingArea(landInfoDto.getMeterBuildMeasure());
                        projectLandPartMap.setCanUseLandArea(landInfoDto.getCanUseMeasure());
                        projectLandPartMap.setBuildingLandCoverArea(projectAddLandInfo.getCoverArea());
                        projectLandPartMap.setAboveGroundCalcVolumeArea(projectAddLandInfo.getUpLandArea());
                        projectLandPartMap.setUnderGroundCalcVolumeArea(projectAddLandInfo.getDownLandArea());
                        projectLandPartMap.setLandGetPrice(projectAddLandInfo.getLandGetPrice());
                        projectLandPartMap.setTakeByLandUseArea(projectAddLandInfo.getTakeByLandUseArea());
                        projectLandPartMap.setIsDelete(IsDeleteEnum.NO.getKey());
                        projectLandPartMap.setIsDisable(IsDisableEnum.NO.getKey());
                        projectLandPartMap.setCreaterId(currentUser.getId());
                        projectLandPartMap.setCreaterName(currentUser.getName());
                        projectLandPartMap.setCreateTime(LocalDateTime.now());
                        projectLandPartMap.setCanUseLandArea(landInfoDto.getTaotalBuildMeasure());
                        projectLandPartMapMapper.insert(projectLandPartMap);

                        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
                        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
                    }else{
                        jsonResult.setCode(CodeEnum.LAND_TO_PROJECT_EXTENDS.getKey());
                        jsonResult.setMsg(CodeEnum.LAND_TO_PROJECT_EXTENDS.getValue());
                    }
                }else{
                    jsonResult.setCode(CodeEnum.PROJECT_EXTENDS_VERSION.getKey());
                    jsonResult.setMsg(CodeEnum.PROJECT_EXTENDS_VERSION.getValue());
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

    @Override
    @Transactional
    public JSONResult deleteLandInfoToProject(ProjectDeleteLandReqParam projectDeleteLandReqParam, CurrentUserVO currentUser, JSONResult jsonResult) throws Exception {

        BoProjectExtend boProjectExtend = projectExtendMapper.selectById(projectDeleteLandReqParam.getVersionId());
        if (boProjectExtend != null) {
            List<BoProjectLandPartMap> boProjectLandPartMaps = projectLandPartMapMapper.selectUseLandByLandId(projectDeleteLandReqParam.getLandId());
            if (boProjectLandPartMaps != null && boProjectLandPartMaps.size() > 0) {

                List<BoLandPartProductTypeMap> boLandPartProductTypeMaps = landPartProductTypeMapMapper.selectProductTypeByLandId(boProjectLandPartMaps.get(0).getId());
                if (boLandPartProductTypeMaps != null && boLandPartProductTypeMaps.size() > 0) {
                    jsonResult.setCode(CodeEnum.PROJECT_LAND_USE_EXCEPTION.getKey());
                    jsonResult.setMsg(CodeEnum.PROJECT_LAND_USE_EXCEPTION.getValue());
                    return jsonResult;
                }

                String version = boProjectExtend.getVersion();
                if(StringUtils.isBlank(version)){
                    jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
                    jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
                    return jsonResult;
                }

                Integer versionStatus = boProjectExtend.getVersionStatus();
                if(!versionStatus.equals(VersionStatusEnum.CREATING.getKey()) && !versionStatus.equals(VersionStatusEnum.REJECTED.getKey())){
                    jsonResult.setCode(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getKey());
                    jsonResult.setMsg(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getValue());
                    return jsonResult;
                }

                List<String> projectOneQuotaLandIdsList = projectQuotaExtendMapper.selectAdoptStageOneBySubProId(boProjectExtend.getProjectId(), StageCodeEnum.STAGE_01.getKey());
                if(projectOneQuotaLandIdsList != null && projectOneQuotaLandIdsList.size() > 0){
                    if(projectOneQuotaLandIdsList.contains(projectDeleteLandReqParam.getLandId())){
                        jsonResult.setCode(CodeEnum.PROJECT_LAND_DELETE_EXCEPTION.getKey());
                        jsonResult.setMsg(CodeEnum.PROJECT_LAND_DELETE_EXCEPTION.getValue());
                        return jsonResult;
                    }
                }

                BoProjectLandPartMap boProjectLandPartMap = new BoProjectLandPartMap();
                boProjectLandPartMap.setId(boProjectLandPartMaps.get(0).getId());
                boProjectLandPartMap.setIsDelete(IsDeleteEnum.YES.getKey());
                boProjectLandPartMap.setUpdaterId(currentUser.getId());
                boProjectLandPartMap.setUpdaterName(currentUser.getName());
                boProjectLandPartMap.setUpdateTime(LocalDateTime.now());
                projectLandPartMapMapper.updateById(boProjectLandPartMap);

                jsonResult.setCode(CodeEnum.SUCCESS.getKey());
                jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
            } else {
                jsonResult.setCode(CodeEnum.PROJECT_LAND_EXCEPTION.getKey());
                jsonResult.setMsg(CodeEnum.PROJECT_LAND_EXCEPTION.getValue());
            }
        } else {
            jsonResult.setCode(CodeEnum.PROJECT_EXTENDS_INFO.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_EXTENDS_INFO.getValue());
        }

        return jsonResult;
    }

    @Override
    public JSONResult getLandInfoBySubPro(String versionId, JSONResult jsonResult) throws Exception {

        List<SubLandQuotaVo> landDetailsList = projectLandPartMapMapper.selectLandInfoBySubProjectId(versionId);

        jsonResult.setData(landDetailsList);
        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());

        return jsonResult;
    }

    @Override
    public JSONResult getLandInfoDetailsToSubProject(String versionId, String landId, JSONResult jsonResult) throws Exception {

        LandToSubProInfoVo landToSubProInfoVo = new LandToSubProInfoVo();

        BoProjectLandPartMap subProjectLandPartMap = projectLandPartMapMapper.selectLandInfoByVersionIdAndLandId(versionId, landId);
        if(subProjectLandPartMap != null){

            //是否全部开发
            landToSubProInfoVo.setIsDev(subProjectLandPartMap.getIsAllDev());

            //查询分期面积
            LandAreaBasicInfoVo landAreaBasicInfoVo = new LandAreaBasicInfoVo();

            BoProjectLandPartMap proLandPartMap = projectLandPartMapMapper.selectBySubExtendId(versionId, landId);

            List<String> subProIds = projectExtendMapper.selectSubProIdsByVersionId(versionId);
            SubTotalAreaDto subTotalAreaDto = projectLandPartMapMapper.selectSubTotalAreaBySubIdsAndLandId(subProIds, landId);

            if(proLandPartMap != null){
                String totalUseLandArea = proLandPartMap.getTotalUseLandArea();
                String buildingLandCoverArea = proLandPartMap.getBuildingLandCoverArea();
                String capacityBuildingArea = proLandPartMap.getCapacityBuildingArea();
                String canUseLandArea = proLandPartMap.getCanUseLandArea();
                String takeByLandUseArea = proLandPartMap.getTakeByLandUseArea();
                String landGetPrice = proLandPartMap.getLandGetPrice();

                landAreaBasicInfoVo.setTotalTotalUserArea(StringUtils.isBlank(totalUseLandArea)?"0":totalUseLandArea);
                landAreaBasicInfoVo.setTotalBuildCoverArea(StringUtils.isBlank(buildingLandCoverArea)?"0":buildingLandCoverArea);
                landAreaBasicInfoVo.setTotalCanUseArea(StringUtils.isBlank(canUseLandArea)?"0":canUseLandArea);
                landAreaBasicInfoVo.setTotalCapBuildArea(StringUtils.isBlank(capacityBuildingArea)?"0":capacityBuildingArea);
                landAreaBasicInfoVo.setTotalLandPrice(StringUtils.isBlank(landGetPrice)?"0":landGetPrice);
                landAreaBasicInfoVo.setTotalTakeUseArea(StringUtils.isBlank(takeByLandUseArea)?"0":takeByLandUseArea);

                if(subTotalAreaDto != null){
                    String sumTotalArea = subTotalAreaDto.getSumTotalArea();
                    String sumBuildArea = subTotalAreaDto.getSumBuildArea();
                    String sumCoverArea = subTotalAreaDto.getSumCoverArea();
                    String sumUseArea = subTotalAreaDto.getSumUseArea();
                    String sumTakeArea = subTotalAreaDto.getSumTakeArea();
                    String sumGetPrice = subTotalAreaDto.getSumGetPrice();

                    landAreaBasicInfoVo.setNotTotalUserArea(String.valueOf(subToString(StringUtils.isBlank(totalUseLandArea)?"0":totalUseLandArea, StringUtils.isBlank(sumTotalArea)?"0":sumTotalArea)));
                    landAreaBasicInfoVo.setNotBuildCoverArea(String.valueOf(subToString(StringUtils.isBlank(buildingLandCoverArea)?"0":buildingLandCoverArea, StringUtils.isBlank(sumBuildArea)?"0":sumBuildArea)));
                    landAreaBasicInfoVo.setNotCanUseArea(String.valueOf(subToString(StringUtils.isBlank(canUseLandArea)?"0":canUseLandArea, StringUtils.isBlank(sumUseArea)?"0":sumUseArea)));
                    landAreaBasicInfoVo.setNotCapBuildArea(String.valueOf(subToString(StringUtils.isBlank(capacityBuildingArea)?"0":capacityBuildingArea, StringUtils.isBlank(sumCoverArea)?"0":sumCoverArea)));
                    landAreaBasicInfoVo.setNotLandPrice(String.valueOf(subToString(StringUtils.isBlank(landGetPrice)?"0":landGetPrice, StringUtils.isBlank(sumGetPrice)?"0":sumGetPrice)));
                    landAreaBasicInfoVo.setNotTakeUseArea(String.valueOf(subToString(StringUtils.isBlank(takeByLandUseArea)?"0":takeByLandUseArea, StringUtils.isBlank(sumTakeArea)?"0":sumTakeArea)));
                }

                landAreaBasicInfoVo.setThisTotalUserArea(StringUtils.isBlank(subProjectLandPartMap.getTotalUseLandArea())?"0":subProjectLandPartMap.getTotalUseLandArea());
                landAreaBasicInfoVo.setThisBuildCoverArea(StringUtils.isBlank(subProjectLandPartMap.getBuildingLandCoverArea())?"0":subProjectLandPartMap.getBuildingLandCoverArea());
                landAreaBasicInfoVo.setThisCapBuildArea(StringUtils.isBlank(subProjectLandPartMap.getCapacityBuildingArea())?"0":subProjectLandPartMap.getCapacityBuildingArea());
                landAreaBasicInfoVo.setThisCanUseArea(StringUtils.isBlank(subProjectLandPartMap.getCanUseLandArea())?"0":subProjectLandPartMap.getCanUseLandArea());
                landAreaBasicInfoVo.setThisTakeUseArea(StringUtils.isBlank(subProjectLandPartMap.getTakeByLandUseArea())?"0":subProjectLandPartMap.getTakeByLandUseArea());
                landAreaBasicInfoVo.setThisLandPrice(StringUtils.isBlank(subProjectLandPartMap.getLandGetPrice())?"0":subProjectLandPartMap.getLandGetPrice());

                landToSubProInfoVo.setLandAreaBasicInfoVo(landAreaBasicInfoVo);
            }

            //查指标头
            List<BoQuotaGroupMap> quotaGroupMapList = boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.LAND_PART_PRODUCT_TYPE.getKey());
            Map<String, String> codeValueTypeMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(quotaGroupMapList)) {
                List<QuotaHeadVO> head = VOUtils.makeQuotaHeadVOList(quotaGroupMapList);
                landToSubProInfoVo.setHead(head);
                quotaGroupMapList.forEach(x -> codeValueTypeMap.put(x.getCode(), x.getValueType()));
            } else {
                landToSubProInfoVo.setHead(new ArrayList<>(0));
            }

            //查地块业态信息
            List<LandProductToProjectVo> landProductToProjectList = new ArrayList<LandProductToProjectVo>();
            List<String> landProductToProjectIds = new ArrayList<String>();
            QueryWrapper<BoProjectLandPartProductTypeMap> queryWrapper = new QueryWrapper<BoProjectLandPartProductTypeMap>();
            queryWrapper.eq("project_land_part_id", subProjectLandPartMap.getId())
                    .eq("is_delete", IsDeleteEnum.NO.getKey())
                    .eq("is_disable", IsDisableEnum.NO.getKey());
            List<BoProjectLandPartProductTypeMap> projectLandPartProductTypeMapList = projectLandPartProductTypeMapMapper.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(projectLandPartProductTypeMapList)) {
                for (BoProjectLandPartProductTypeMap productTypeMap :projectLandPartProductTypeMapList){
                    LandProductToProjectVo landProductToProjectVo = new LandProductToProjectVo();

                    landProductToProjectVo.setProductTypeId(productTypeMap.getId());
                    landProductToProjectVo.setProductAttrType(productTypeMap.getPropertyRightAttr());
                    landProductToProjectVo.setProductTypeName(productTypeMap.getProductTypeName());
                    landProductToProjectVo.setProductTypeCode(productTypeMap.getProductTypeCode());
                    landProductToProjectList.add(landProductToProjectVo);

                    landProductToProjectIds.add(productTypeMap.getId());
                }
            }

            //查地块业态指标
            if(CollectionUtils.isNotEmpty(landProductToProjectIds)){
                List<ProductAttrQuotaDto> quotaMapList = projectLandPartProductTypeQuotaMapMapper.selectQuoteByProductIds(landProductToProjectIds);
                if (CollectionUtils.isNotEmpty(quotaMapList)) {
                    for (LandProductToProjectVo productType : landProductToProjectList) {
                        List<QuotaBodyVO> quotaList = new ArrayList<QuotaBodyVO>();
                        for (ProductAttrQuotaDto quotaType : quotaMapList) {
                            if (productType.getProductTypeId().equals(quotaType.getProductId())
                                    && productType.getProductAttrType().equals(quotaType.getAttrQuotaValue())) {
                                QuotaBodyVO quotaBodyVO = new QuotaBodyVO();
                                quotaBodyVO.setQuotaCode(quotaType.getQuoteCode());
                                quotaBodyVO.setQuotaValue(quotaType.getQuoteValue());
                                quotaList.add(quotaBodyVO);
                            }
                        }
                        productType.setQuotaList(quotaList);
                    }
                }
            }

            //手动排序
            Map<String, Integer> codeSortMap = boProductTypeService.getCodeSortMap();
            landProductToProjectList.sort((x, y) -> {
                Integer xSort = codeSortMap.get(x.getProductTypeCode());
                Integer ySort = codeSortMap.get(y.getProductTypeCode());
                if (!xSort.equals(ySort)) {
                    return xSort - ySort;
                }
                for (QuotaBodyVO xBody : x.getQuotaList()) {
                    if (QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(xBody.getQuotaCode())) {
                        for (QuotaBodyVO yBody : y.getQuotaList()) {
                            if (QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(xBody.getQuotaCode())) {
                                return StringUtils.compare(xBody.getQuotaValue(), yBody.getQuotaValue());
                            }
                        }
                    }
                }
                return 0;
            });

            //排序
            landProductToProjectList.sort((a, b) -> {
                try {
                    String aVal = a.getQuotaList().stream().filter(x -> QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(x.getQuotaCode())).findFirst().get().getQuotaValue();
                    String bVal = b.getQuotaList().stream().filter(x -> QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(x.getQuotaCode())).findFirst().get().getQuotaValue();
                    return StageCodeEnum.getByKey(aVal).getOrder() - StageCodeEnum.getByKey(bVal).getOrder();
                } catch (Exception e) {
                    return 0;
                }
            });

            landToSubProInfoVo.setLandProductToProjectVo(landProductToProjectList);
        }else{
            jsonResult.setCode(CodeEnum.LAND_DELETE_EXTENDS.getKey());
            jsonResult.setMsg(CodeEnum.LAND_DELETE_EXTENDS.getValue());
            return jsonResult;
        }

        jsonResult.setData(landToSubProInfoVo);
        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());

        return jsonResult;
    }

    @Override
    public JSONResult getLandInfoCanUseBySubProject(String versionId, JSONResult jsonResult) throws Exception {

        ProjectInfoDto projectInfoDto = projectExtendMapper.selectSubProjectByVersionId(versionId);

        if(projectInfoDto == null){
            jsonResult.setCode(CodeEnum.PROJECT_EXTENDS.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_EXTENDS.getValue());
            return jsonResult;
        }

        String version = projectInfoDto.getVersion();
        if(StringUtils.isBlank(version)){
            jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
            return jsonResult;
        }

        Integer versionStatus = projectInfoDto.getVersionStatus();
        if(!versionStatus.equals(VersionStatusEnum.CREATING.getKey()) && !versionStatus.equals(VersionStatusEnum.REJECTED.getKey())){
            jsonResult.setCode(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getKey());
            jsonResult.setMsg(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getValue());
            return jsonResult;
        }

        List<String> subProIds = projectExtendMapper.selectSubProIdsByVersionId(versionId);

        //查询项目下投决会最新版本业态指标信息
        List<SubProTotalQuoteValueDto> proQuoteValueDtos = landPartProductTypeMapMapper.selectTotalQuoteByProjectId(projectInfoDto.getProjectId());

        //查询项目下所有分期部分开发地块信息
        List<String> notDevLandPartIds = new ArrayList<String>();
        List<BoProjectLandPartMap> notDevProjectLandPartMaps = projectLandPartMapMapper.selectNotDevLandIdsBySubIds(subProIds);
        if(notDevProjectLandPartMaps != null && notDevProjectLandPartMaps.size() > 0){
            for (int i = 0; i < notDevProjectLandPartMaps.size(); i++){
                BoProjectLandPartMap boProjectLandPartMap = notDevProjectLandPartMaps.get(i);
                if(!notDevLandPartIds.contains(boProjectLandPartMap.getLandPartId())){
                    notDevLandPartIds.add(boProjectLandPartMap.getLandPartId());
                }
            }
        }

        //查询当前分期已使用地块信息
        List<String> useLandIds = new ArrayList<String>();
        QueryWrapper<BoProjectLandPartMap> queryWrapper = new QueryWrapper<BoProjectLandPartMap>();
        queryWrapper.eq("project_extend_id", versionId)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        List<BoProjectLandPartMap> boProjectLandPartMaps = projectLandPartMapMapper.selectList(queryWrapper);
        if(boProjectLandPartMaps != null && boProjectLandPartMaps.size() > 0){
            for (int i = 0; i < boProjectLandPartMaps.size(); i++){
                notDevLandPartIds.remove(boProjectLandPartMaps.get(i).getLandPartId());
                useLandIds.add(boProjectLandPartMaps.get(i).getLandPartId());
            }
        }

        //查询当前分期未使用的业态指标信息
        List<SubProTotalQuoteValueDto> subProQuoteValueDtos = new ArrayList<SubProTotalQuoteValueDto>();
        if(CollectionUtils.isNotEmpty(notDevLandPartIds)){
            subProQuoteValueDtos = projectLandPartProductTypeQuotaMapMapper.selectTotalQuoteByLandIds(notDevLandPartIds);
        }

        Set<LandBasicInfoVo> landBasicInfoVos = new HashSet<LandBasicInfoVo>();
        if(proQuoteValueDtos != null && proQuoteValueDtos.size() > 0){
            for (int i = 0; i < proQuoteValueDtos.size(); i++) {
                SubProTotalQuoteValueDto proTotalQuoteValueDto = proQuoteValueDtos.get(i);
                String landLandId = proTotalQuoteValueDto.getLandId();
                String landproductCode = proTotalQuoteValueDto.getProductCode();
                String landquoteCode = proTotalQuoteValueDto.getQuoteCode();
                String landsumQuoteValue = proTotalQuoteValueDto.getSumQuoteValue();
                String landAttrQuotaValue = proTotalQuoteValueDto.getAttrQuotaValue();
                String landCode = landLandId + "&" + landAttrQuotaValue + "&" + landproductCode + "&" + landquoteCode;

                if(subProQuoteValueDtos != null && subProQuoteValueDtos.size() > 0){
                    for (int j = 0; j < subProQuoteValueDtos.size(); j++) {
                        boolean index = false;
                        SubProTotalQuoteValueDto subProTotalQuoteValueDto = subProQuoteValueDtos.get(j);
                        String sublandId = subProTotalQuoteValueDto.getLandId();
                        String subproductCode = subProTotalQuoteValueDto.getProductCode();
                        String subquoteCode = subProTotalQuoteValueDto.getQuoteCode();
                        String subsumQuoteValue = subProTotalQuoteValueDto.getSumQuoteValue();
                        String subAttrQuotaValue = subProTotalQuoteValueDto.getAttrQuotaValue();
                        String subCode = sublandId + "&" + subAttrQuotaValue +  "&" + subproductCode + "&" + subquoteCode;

                        if (landCode.equals(subCode)) {
                            index = true;

                            if(StringUtils.isNotBlank(subquoteCode) && Constants.inspectQuotaList.contains(subquoteCode)){
                                String sub = sub(Double.parseDouble(!StringUtils.isNotBlank(landsumQuoteValue)?"0":landsumQuoteValue), Double.parseDouble(!StringUtils.isNotBlank(subsumQuoteValue)?"0":subsumQuoteValue));
                                if (Double.parseDouble(StringUtils.isBlank(sub)?"0":sub) > 0d) {
                                    LandBasicInfoVo landBasicInfoVo = new LandBasicInfoVo();
                                    landBasicInfoVo.setLandName(subProTotalQuoteValueDto.getLandName());
                                    landBasicInfoVo.setLandId(subProTotalQuoteValueDto.getLandId());
                                    landBasicInfoVos.add(landBasicInfoVo);
                                    break;
                                }
                            }
                        }

                        if(j < subProQuoteValueDtos.size() - 1 && !index){
                            LandBasicInfoVo landBasicInfoVo = new LandBasicInfoVo();
                            landBasicInfoVo.setLandName(proTotalQuoteValueDto.getLandName());
                            landBasicInfoVo.setLandId(proTotalQuoteValueDto.getLandId());
                            landBasicInfoVos.add(landBasicInfoVo);
                        }
                    }
                }else{
                    LandBasicInfoVo landBasicInfoVo = new LandBasicInfoVo();
                    landBasicInfoVo.setLandName(proTotalQuoteValueDto.getLandName());
                    landBasicInfoVo.setLandId(proTotalQuoteValueDto.getLandId());
                    landBasicInfoVos.add(landBasicInfoVo);
                }
            }
        }

        Set<LandBasicInfoVo> result = new HashSet<LandBasicInfoVo>();
        if(landBasicInfoVos != null && landBasicInfoVos.size() > 0){
            for (LandBasicInfoVo landBasicInfoVo : landBasicInfoVos){
                String landId = landBasicInfoVo.getLandId();
                if(!useLandIds.contains(landId)){
                    result.add(landBasicInfoVo);
                }
            }
        }

        jsonResult.setData(result);
        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
        return jsonResult;
    }

    @Override
    public JSONResult getCanUseLandDetailsToSubProject(String subProjectId, String landId, JSONResult jsonResult) throws Exception {

        LandToSubProInfoVo landToSubProInfoVo = new LandToSubProInfoVo();

        SubProjectInfoDto subProjectInfoDto = projectInfoMapper.selectProjectBySubProjectId(subProjectId, null);

        String version = subProjectInfoDto.getVersion();
        if(StringUtils.isBlank(version)){
            jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
            return jsonResult;
        }

        String versionStatus = subProjectInfoDto.getVersionState();
        if(!versionStatus.equals(String.valueOf(VersionStatusEnum.CREATING.getKey())) && !versionStatus.equals(String.valueOf(VersionStatusEnum.REJECTED.getKey()))){
            jsonResult.setCode(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getKey());
            jsonResult.setMsg(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getValue());
            return jsonResult;
        }

        BoProjectLandPartMap proLandPartMap = projectLandPartMapMapper.selectBySubExtendId(subProjectInfoDto.getExtendId(),landId);
        if(proLandPartMap != null){

            //是否全部开发
            landToSubProInfoVo.setIsDev(-1);

            //查询分期面积
            LandAreaBasicInfoVo landAreaBasicInfoVo = new LandAreaBasicInfoVo();

            List<String> subProIds = projectExtendMapper.selectSubProIdsByVersionId(subProjectInfoDto.getExtendId());
            SubTotalAreaDto subTotalAreaDto = projectLandPartMapMapper.selectSubTotalAreaBySubIdsAndLandId(subProIds, landId);

            if(proLandPartMap != null){
                String totalUseLandArea = proLandPartMap.getTotalUseLandArea();
                String buildingLandCoverArea = proLandPartMap.getBuildingLandCoverArea();
                String capacityBuildingArea = proLandPartMap.getCapacityBuildingArea();
                String canUseLandArea = proLandPartMap.getCanUseLandArea();
                String takeByLandUseArea = proLandPartMap.getTakeByLandUseArea();
                String landGetPrice = proLandPartMap.getLandGetPrice();

                landAreaBasicInfoVo.setTotalTotalUserArea(StringUtils.isBlank(totalUseLandArea)?"0":totalUseLandArea);
                landAreaBasicInfoVo.setTotalBuildCoverArea(StringUtils.isBlank(buildingLandCoverArea)?"0":buildingLandCoverArea);
                landAreaBasicInfoVo.setTotalCanUseArea(StringUtils.isBlank(canUseLandArea)?"0":canUseLandArea);
                landAreaBasicInfoVo.setTotalCapBuildArea(StringUtils.isBlank(capacityBuildingArea)?"0":capacityBuildingArea);
                landAreaBasicInfoVo.setTotalLandPrice(StringUtils.isBlank(landGetPrice)?"0":landGetPrice);
                landAreaBasicInfoVo.setTotalTakeUseArea(StringUtils.isBlank(takeByLandUseArea)?"0":takeByLandUseArea);

                if(subTotalAreaDto != null){
                    String sumTotalArea = subTotalAreaDto.getSumTotalArea();
                    String sumBuildArea = subTotalAreaDto.getSumBuildArea();
                    String sumCoverArea = subTotalAreaDto.getSumCoverArea();
                    String sumUseArea = subTotalAreaDto.getSumUseArea();
                    String sumTakeArea = subTotalAreaDto.getSumTakeArea();
                    String sumGetPrice = subTotalAreaDto.getSumGetPrice();

                    landAreaBasicInfoVo.setNotTotalUserArea(String.valueOf(subToString(StringUtils.isBlank(totalUseLandArea)?"0":totalUseLandArea, StringUtils.isBlank(sumTotalArea)?"0":sumTotalArea)));
                    landAreaBasicInfoVo.setNotBuildCoverArea(String.valueOf(subToString(StringUtils.isBlank(buildingLandCoverArea)?"0":buildingLandCoverArea, StringUtils.isBlank(sumBuildArea)?"0":sumBuildArea)));
                    landAreaBasicInfoVo.setNotCanUseArea(String.valueOf(subToString(StringUtils.isBlank(canUseLandArea)?"0":canUseLandArea, StringUtils.isBlank(sumUseArea)?"0":sumUseArea)));
                    landAreaBasicInfoVo.setNotCapBuildArea(String.valueOf(subToString(StringUtils.isBlank(capacityBuildingArea)?"0":capacityBuildingArea, StringUtils.isBlank(sumCoverArea)?"0":sumCoverArea)));
                    landAreaBasicInfoVo.setNotLandPrice(String.valueOf(subToString(StringUtils.isBlank(landGetPrice)?"0":landGetPrice, StringUtils.isBlank(sumGetPrice)?"0":sumGetPrice)));
                    landAreaBasicInfoVo.setNotTakeUseArea(String.valueOf(subToString(StringUtils.isBlank(takeByLandUseArea)?"0":takeByLandUseArea, StringUtils.isBlank(sumTakeArea)?"0":sumTakeArea)));
                }else{
                    landAreaBasicInfoVo.setNotTotalUserArea("0");
                    landAreaBasicInfoVo.setNotBuildCoverArea("0");
                    landAreaBasicInfoVo.setNotCanUseArea("0");
                    landAreaBasicInfoVo.setNotCapBuildArea("0");
                    landAreaBasicInfoVo.setNotLandPrice("0");
                    landAreaBasicInfoVo.setNotTakeUseArea("0");
                }

                landAreaBasicInfoVo.setThisTotalUserArea("0");
                landAreaBasicInfoVo.setThisBuildCoverArea("0");
                landAreaBasicInfoVo.setThisCapBuildArea("0");
                landAreaBasicInfoVo.setThisCanUseArea("0");
                landAreaBasicInfoVo.setThisTakeUseArea("0");
                landAreaBasicInfoVo.setThisLandPrice("0");

                landToSubProInfoVo.setLandAreaBasicInfoVo(landAreaBasicInfoVo);
            }

            //查指标头
            List<BoQuotaGroupMap> quotaGroupMapList = boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.LAND_PART_PRODUCT_TYPE.getKey());
            Map<String, String> codeValueTypeMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(quotaGroupMapList)) {
                List<QuotaHeadVO> head = VOUtils.makeQuotaHeadVOList(quotaGroupMapList);
                landToSubProInfoVo.setHead(head);
                quotaGroupMapList.forEach(x -> codeValueTypeMap.put(x.getCode(), x.getValueType()));
            } else {
                landToSubProInfoVo.setHead(new ArrayList<>(0));
            }

            //查询分期使用地块业态指标信息
            Map<String, String> sumSubQuotaMap = new HashMap<String, String>();
            List<ProjectProductTypeQuotaDto> projectProductTypeQuotaDtos = projectLandPartProductTypeQuotaMapMapper.selectTotalQuotaBySubProjectIdsAndLandId(subProIds, landId);
            if(CollectionUtils.isNotEmpty(projectProductTypeQuotaDtos)){
                for (ProjectProductTypeQuotaDto productType : projectProductTypeQuotaDtos){
                    sumSubQuotaMap.put(productType.getAttrQuotaValue()+"&"+productType.getProductCode()+"&"+productType.getQuoteCode(), productType.getQuoteValue());
                }
            }

            //查询投决会业态指标信息
            List<LandProductToProjectVo> landProductToProjectList = new ArrayList<LandProductToProjectVo>();
            List<BoLandPartProductTypeMapExtendDto> landPartProductTypeMapExtendDtoList = landPartProductTypeMapMapper.selectProductByProIdAndLandId(subProjectInfoDto.getProjectId(), landId);
            List<BoLandPartProductTypeQuotaMap> landPartProductTypeQuotaMapList = landPartProductTypeQuotaMapMapper.selectQuotaByProjectIdAndLandId(subProjectInfoDto.getProjectId(), landId);
            if(CollectionUtils.isNotEmpty(landPartProductTypeMapExtendDtoList)){
                for (BoLandPartProductTypeMapExtendDto productType : landPartProductTypeMapExtendDtoList){
                    LandProductToProjectVo landProductToProjectVo = new LandProductToProjectVo();

                    List<QuotaBodyVO> quotaList = new ArrayList<QuotaBodyVO>();
                    if(CollectionUtils.isNotEmpty(landPartProductTypeQuotaMapList)){
                        for (BoLandPartProductTypeQuotaMap quotaType : landPartProductTypeQuotaMapList){
                            if(productType.getId().equals(quotaType.getLandPartProductTypeId())){
                                QuotaBodyVO quotaBodyVO = new QuotaBodyVO();
                                quotaBodyVO.setQuotaCode(quotaType.getQuotaCode());

                                String quotaCode = quotaType.getQuotaCode();
                                String quotaValue = quotaType.getQuotaValue();
                                String subQuotaValue = sumSubQuotaMap.get(productType.getAttrQuotaValue() + "&" + productType.getProductTypeCode() + "&" + quotaType.getQuotaCode());
                                if(StringUtils.isNotBlank(quotaCode) && Constants.inspectQuotaList.contains(quotaCode)){
                                    String surplusQuotaValue = subToString(StringUtils.isBlank(quotaValue) ? "0" : quotaValue, StringUtils.isBlank(subQuotaValue) ? "0" : subQuotaValue);
                                    quotaBodyVO.setQuotaValue(String.valueOf(surplusQuotaValue));

                                }else{
                                    quotaBodyVO.setQuotaValue(String.valueOf(StringUtils.isBlank(quotaValue) ? "0" : quotaValue));
                                }
                                quotaList.add(quotaBodyVO);
                            }
                        }
                    }

                    landProductToProjectVo.setProductTypeId(productType.getId());
                    landProductToProjectVo.setProductAttrType(productType.getAttrQuotaValue());
                    landProductToProjectVo.setProductTypeName(productType.getProductTypeName());
                    landProductToProjectVo.setProductTypeCode(productType.getProductTypeCode());
                    landProductToProjectVo.setQuotaList(quotaList);
                    landProductToProjectList.add(landProductToProjectVo);
                }
            }

            //手动排序
            Map<String, Integer> codeSortMap = boProductTypeService.getCodeSortMap();
            landProductToProjectList.sort((x, y) -> {
                Integer xSort = codeSortMap.get(x.getProductTypeCode());
                Integer ySort = codeSortMap.get(y.getProductTypeCode());
                if (!xSort.equals(ySort)) {
                    return xSort - ySort;
                }
                for (QuotaBodyVO xBody : x.getQuotaList()) {
                    if (QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(xBody.getQuotaCode())) {
                        for (QuotaBodyVO yBody : y.getQuotaList()) {
                            if (QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(xBody.getQuotaCode())) {
                                return StringUtils.compare(xBody.getQuotaValue(), yBody.getQuotaValue());
                            }
                        }
                    }
                }
                return 0;
            });

            //排序
            landProductToProjectList.sort((a, b) -> {
                try {
                    String aVal = a.getQuotaList().stream().filter(x -> QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(x.getQuotaCode())).findFirst().get().getQuotaValue();
                    String bVal = b.getQuotaList().stream().filter(x -> QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(x.getQuotaCode())).findFirst().get().getQuotaValue();
                    return StageCodeEnum.getByKey(aVal).getOrder() - StageCodeEnum.getByKey(bVal).getOrder();
                } catch (Exception e) {
                    return 0;
                }
            });

            landToSubProInfoVo.setLandProductToProjectVo(landProductToProjectList);

        }else{
            jsonResult.setCode(CodeEnum.LAND_DELETE_EXTENDS.getKey());
            jsonResult.setMsg(CodeEnum.LAND_DELETE_EXTENDS.getValue());
            return jsonResult;
        }

        jsonResult.setData(landToSubProInfoVo);
        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());

        return jsonResult;
    }

    @Override
    @Transactional
    public JSONResult addLandInfoToSubProject(LandAddSubProInfoBody landAddSubProInfoBody, CurrentUserVO currentUser, JSONResult jsonResult) throws Exception {

        jsonResult.setCode(CodeEnum.ERROR.getKey());
        jsonResult.setMsg(CodeEnum.ERROR.getValue());

        ProjectInfoDto subProjectInfoDto = projectExtendMapper.selectSubProjectByVersionId(landAddSubProInfoBody.getVersionId());

        String version = subProjectInfoDto.getVersion();
        if(StringUtils.isBlank(version)){
            jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
            return jsonResult;
        }

        Integer versionStatus = subProjectInfoDto.getVersionStatus();
        if(!versionStatus.equals(VersionStatusEnum.CREATING.getKey()) && !versionStatus.equals(VersionStatusEnum.REJECTED.getKey())){
            jsonResult.setCode(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getKey());
            jsonResult.setMsg(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getValue());
            return jsonResult;
        }

        ProjectInfoDto projectInfoDto = projectExtendMapper.selectSubProjectByVersionId(landAddSubProInfoBody.getVersionId());

        //是否全部开发（0-否；1-是）
        Integer isDev = landAddSubProInfoBody.getIsDev();
        if(isDev.equals(IsDevEnum.YES.getKey())){

            //查看分期指标是否被使用
            List<BoProjectLandPartProductTypeQuotaMap> projectProductTypeQuotaDtos = projectLandPartProductTypeQuotaMapMapper.selectQuotaByLandId(landAddSubProInfoBody.getLandId());
            if (CollectionUtils.isNotEmpty(projectProductTypeQuotaDtos)) {
                jsonResult.setCode(CodeEnum.SUB_PROJECT_LAND_EXTENDS.getKey());
                jsonResult.setMsg(CodeEnum.SUB_PROJECT_LAND_EXTENDS.getValue());
                return jsonResult;
            }

            //bo_project_land_part_map
            BoProjectLandPartMapExtendDto projectLandPartMapExtendDto = projectLandPartMapMapper.selectProLandPartBySubExtIdAndLandId(landAddSubProInfoBody.getVersionId(), landAddSubProInfoBody.getLandId());
            if(projectLandPartMapExtendDto != null){
                String landPartId = UUIDUtils.create();

                BoProjectLandPartMap subBoProjectLandPartMap = new BoProjectLandPartMap();
                subBoProjectLandPartMap.setId(landPartId);
                subBoProjectLandPartMap.setLandPartId(projectLandPartMapExtendDto.getLandPartId());
                subBoProjectLandPartMap.setProjectExtendId(landAddSubProInfoBody.getVersionId());
                subBoProjectLandPartMap.setLandPartName(projectLandPartMapExtendDto.getLandPartName());
                subBoProjectLandPartMap.setIsAllDev(isDev);
                subBoProjectLandPartMap.setTotalUseLandArea(projectLandPartMapExtendDto.getTotalUseLandArea());
                subBoProjectLandPartMap.setCapacityBuildingArea(projectLandPartMapExtendDto.getCapacityBuildingArea());
                subBoProjectLandPartMap.setCanUseLandArea(projectLandPartMapExtendDto.getCanUseLandArea());
                subBoProjectLandPartMap.setBuildingLandCoverArea(projectLandPartMapExtendDto.getBuildingLandCoverArea());
                subBoProjectLandPartMap.setTakeByLandUseArea(projectLandPartMapExtendDto.getTakeByLandUseArea());
                subBoProjectLandPartMap.setLandGetPrice(projectLandPartMapExtendDto.getLandGetPrice());
                subBoProjectLandPartMap.setAboveGroundCalcVolumeArea(projectLandPartMapExtendDto.getAboveGroundCalcVolumeArea());
                subBoProjectLandPartMap.setUnderGroundCalcVolumeArea(projectLandPartMapExtendDto.getUnderGroundCalcVolumeArea());
                subBoProjectLandPartMap.setRefProjectId(projectLandPartMapExtendDto.getSubRefProjectId());
                subBoProjectLandPartMap.setRefProjectName(projectLandPartMapExtendDto.getSubRefProjectName());
                subBoProjectLandPartMap.setCreaterId(currentUser.getId());
                subBoProjectLandPartMap.setCreaterName(currentUser.getName());
                subBoProjectLandPartMap.setCreateTime(LocalDateTime.now());
                subBoProjectLandPartMap.setIsDelete(IsDeleteEnum.NO.getKey());
                subBoProjectLandPartMap.setIsDisable(IsDisableEnum.NO.getKey());

                //根据分期地块查询项目投决会使用的业态信息
                Map<String, String> productMap = new HashMap<String, String>();
                List<String> landProductIds = new ArrayList<String>();
                List<BoLandPartProductTypeMapExtendDto> landPartProductTypeMaps = projectLandPartProductTypeMapMapper.selectLandProductByProjectId(subProjectInfoDto.getProjectId(), landAddSubProInfoBody.getLandId());
                if (landPartProductTypeMaps != null && landPartProductTypeMaps.size() > 0) {

                    List<BoProjectLandPartProductTypeMap> insertProjectLandPartProductTypeMapList = new ArrayList<BoProjectLandPartProductTypeMap>();
                    List<BoProjectLandPartProductTypeQuotaMap> insertProjectLandPartProductTypeQuotaMapList = new ArrayList<BoProjectLandPartProductTypeQuotaMap>();
                    for (int i = 0; i < landPartProductTypeMaps.size(); i++) {
                        BoLandPartProductTypeMapExtendDto boLandPartProductTypeMapExtendDto = landPartProductTypeMaps.get(i);
                        LocalDateTime time = LocalDateTime.now();

                        BoProjectLandPartProductTypeMap projectLandPartProductTypeMap = new BoProjectLandPartProductTypeMap();
                        String id = UUIDUtils.create();
                        projectLandPartProductTypeMap.setId(id);
                        projectLandPartProductTypeMap.setProjectLandPartId(landPartId);
                        projectLandPartProductTypeMap.setProductTypeId(boLandPartProductTypeMapExtendDto.getProductTypeId());
                        projectLandPartProductTypeMap.setProductTypeCode(boLandPartProductTypeMapExtendDto.getProductTypeCode());
                        projectLandPartProductTypeMap.setProductTypeName(boLandPartProductTypeMapExtendDto.getProductTypeName());
                        projectLandPartProductTypeMap.setIsDelete(IsDeleteEnum.NO.getKey());
                        projectLandPartProductTypeMap.setIsDisable(IsDisableEnum.NO.getKey());
                        projectLandPartProductTypeMap.setCreateTime(time);
                        projectLandPartProductTypeMap.setCreaterId(currentUser.getId());
                        projectLandPartProductTypeMap.setCreaterName(currentUser.getName());
                        projectLandPartProductTypeMap.setPropertyRightAttr(boLandPartProductTypeMapExtendDto.getAttrQuotaValue());
                        insertProjectLandPartProductTypeMapList.add(projectLandPartProductTypeMap);

                        landProductIds.add(boLandPartProductTypeMapExtendDto.getId());
                        productMap.put(boLandPartProductTypeMapExtendDto.getId(), id);
                    }

                    if (landProductIds != null && landProductIds.size() > 0) {
                        List<BoLandPartProductTypeQuotaMap> landPartProductTypeQuotaMaps = projectLandPartProductTypeQuotaMapMapper.selectLandQuoteByProductIds(landProductIds);
                        if (landPartProductTypeQuotaMaps != null && landPartProductTypeQuotaMaps.size() > 0) {
                            for (int i = 0; i < landPartProductTypeQuotaMaps.size(); i++) {
                                BoLandPartProductTypeQuotaMap boLandPartProductTypeQuotaMap = landPartProductTypeQuotaMaps.get(i);

                                BoProjectLandPartProductTypeQuotaMap projectLandPartProductTypeQuotaMap = new BoProjectLandPartProductTypeQuotaMap();
                                projectLandPartProductTypeQuotaMap.setId(UUIDUtils.create());
                                projectLandPartProductTypeQuotaMap.setProjectLandPartProductTypeId(productMap.get(boLandPartProductTypeQuotaMap.getLandPartProductTypeId()));
                                projectLandPartProductTypeQuotaMap.setQuotaGroupMapId(boLandPartProductTypeQuotaMap.getQuotaGroupMapId());
                                projectLandPartProductTypeQuotaMap.setQuotaId(boLandPartProductTypeQuotaMap.getQuotaId());
                                projectLandPartProductTypeQuotaMap.setQuotaCode(boLandPartProductTypeQuotaMap.getQuotaCode());
                                projectLandPartProductTypeQuotaMap.setQuotaValue(boLandPartProductTypeQuotaMap.getQuotaValue());
                                projectLandPartProductTypeQuotaMap.setIsDelete(IsDeleteEnum.NO.getKey());
                                projectLandPartProductTypeQuotaMap.setIsDisable(IsDisableEnum.NO.getKey());
                                projectLandPartProductTypeQuotaMap.setCreateTime(LocalDateTime.now());
                                projectLandPartProductTypeQuotaMap.setCreaterId(currentUser.getId());
                                projectLandPartProductTypeQuotaMap.setCreaterName(currentUser.getName());
                                insertProjectLandPartProductTypeQuotaMapList.add(projectLandPartProductTypeQuotaMap);
                            }
                        }
                    }

                    projectLandPartMapMapper.insert(subBoProjectLandPartMap);

                    if (CollectionUtils.isNotEmpty(insertProjectLandPartProductTypeMapList)) {
                        projectLandPartProductTypeMapService.saveBatch(insertProjectLandPartProductTypeMapList, 100);
                    }

                    if (CollectionUtils.isNotEmpty(insertProjectLandPartProductTypeQuotaMapList)) {
                        projectLandPartProductTypeQuotaMapService.saveBatch(insertProjectLandPartProductTypeQuotaMapList, 100);
                    }

                    jsonResult.setCode(CodeEnum.SUCCESS.getKey());
                    jsonResult.setMsg(CodeEnum.SUCCESS.getValue());

                    return jsonResult;
                }
            }
        }else if(isDev.equals(IsDevEnum.NO.getKey())){
            StringBuffer areaRestMsg = new StringBuffer();
            BoProjectLandPartMap boProjectLandPartMap = projectLandPartMapMapper.selectProjectLandBySubProjectIdAndLandId(projectInfoDto.getProjectId(), landAddSubProInfoBody.getLandId());
            List<String> subProjectIds = projectExtendMapper.selectSubProIdsByVersionId(landAddSubProInfoBody.getVersionId());

            List<String> subProjectIdsCopy = new ArrayList<>();
            subProjectIdsCopy.addAll(subProjectIds);
            subProjectIdsCopy.remove(subProjectInfoDto.getSubProjectId());

            SubTotalAreaDto subTotalAreaDto = new SubTotalAreaDto();
            if(CollectionUtils.isNotEmpty(subProjectIdsCopy)){
                subTotalAreaDto = projectLandPartMapMapper.selectSubTotalAreaBySubIdsAndLandId(subProjectIdsCopy, landAddSubProInfoBody.getLandId());
            }

            if(subTotalAreaDto != null){
                areaRestMsg = alculationcArea(landAddSubProInfoBody, areaRestMsg, boProjectLandPartMap, subTotalAreaDto);
            }

            if (areaRestMsg != null && areaRestMsg.length() > 0) {
                jsonResult.setCode(CodeEnum.LAND_AREAT_EXTENDS.getKey());
                jsonResult.setMsg(areaRestMsg.toString());
                return jsonResult;
            }

            //查询指标基础信息
            Map<String, BoQuotaGroupMap> formatMap = new HashMap<String, BoQuotaGroupMap>();
            QueryWrapper<BoQuotaGroupMap> queryTypeQuotaWrapper = new QueryWrapper<BoQuotaGroupMap>();
            queryTypeQuotaWrapper.eq("group_code", "LAND_PART_PRODUCT_TYPE")
                    .eq("is_delete", IsDeleteEnum.NO.getKey())
                    .eq("is_disable", IsDisableEnum.NO.getKey());
            List<BoQuotaGroupMap> boQuotaGroupMaps = quotaGroupMapMapper.selectList(queryTypeQuotaWrapper);
            if(CollectionUtils.isNotEmpty(boQuotaGroupMaps)){
                for (int i = 0; i < boQuotaGroupMaps.size(); i++){
                    BoQuotaGroupMap boQuotaGroupMap = boQuotaGroupMaps.get(i);
                    formatMap.put(boQuotaGroupMap.getCode(), boQuotaGroupMap);
                }
            }

            //页面输入的业态信息
            Map<String, String> sumSubQuotaMap = new HashMap<String, String>();
            Map<String, String> affQuotaMap = new HashMap<String, String>();
            List<LandProductToProjectVo> affLandProductToProjectVo = landAddSubProInfoBody.getLandProductToProjectVo();
            if(CollectionUtils.isNotEmpty(affLandProductToProjectVo)){
                for (LandProductToProjectVo productType : affLandProductToProjectVo){
                    List<QuotaBodyVO> quotaList = productType.getQuotaList();
                    if(CollectionUtils.isNotEmpty(affLandProductToProjectVo)){
                        for (QuotaBodyVO quotaType : quotaList){
                            affQuotaMap.put(productType.getProductAttrType() + "&" + productType.getProductTypeCode() + "&" + quotaType.getQuotaCode(), quotaType.getQuotaValue());

                            sumSubQuotaMap.put(productType.getProductAttrType() + "&" + productType.getProductTypeCode() + "&" + quotaType.getQuotaCode(), quotaType.getQuotaValue());
                        }
                    }
                }
            }

            //查询分期使用地块业态指标信息
            List<ProjectProductTypeQuotaDto> projectProductTypeQuotaDtos = projectLandPartProductTypeQuotaMapMapper.selectTotalQuotaBySubProjectIdsAndLandId(subProjectIds, landAddSubProInfoBody.getLandId());
            if(CollectionUtils.isNotEmpty(projectProductTypeQuotaDtos)){
                for (ProjectProductTypeQuotaDto productType : projectProductTypeQuotaDtos){
                    String sumSubQuoteValue = sumSubQuotaMap.get(productType.getAttrQuotaValue() + "&" + productType.getProductCode() + "&" + productType.getQuoteCode());
                    if(StringUtils.isNotBlank(sumSubQuoteValue)){

                        BoQuotaGroupMap boQuotaGroupMap = formatMap.get(productType.getQuoteCode());
                        if(boQuotaGroupMap != null){
                            String valueType = boQuotaGroupMap.getValueType();
                            if(StringUtils.isNotBlank(valueType) && valueType.trim().equals("NUMBER")){
                                String sumsubQuotaValue = addToString(sumSubQuoteValue, productType.getQuoteValue());
                                sumSubQuotaMap.put(productType.getAttrQuotaValue() + "&" + productType.getProductCode() + "&" + productType.getQuoteCode(), String.valueOf(sumsubQuotaValue));
                            }
                        }
                    }
                }
            }

            //查询投决会业态指标信息
            List<BoProjectLandPartProductTypeMap> insertProjectLandPartProductTypeMapList = new ArrayList<BoProjectLandPartProductTypeMap>();
            List<BoProjectLandPartProductTypeQuotaMap> insertProjectLandPartProductTypeQuotaMapList = new ArrayList<BoProjectLandPartProductTypeQuotaMap>();

            //bo_project_land_part_map

            BoProjectLandPartMapExtendDto projectLandPartMapExtendDto = projectLandPartMapMapper.selectProLandPartBySubExtIdAndLandId(landAddSubProInfoBody.getVersionId(), landAddSubProInfoBody.getLandId());
            if(projectLandPartMapExtendDto != null) {
                String landPartId = UUIDUtils.create();

                BoProjectLandPartMap subBoProjectLandPartMap = new BoProjectLandPartMap();
                subBoProjectLandPartMap.setId(landPartId);
                subBoProjectLandPartMap.setLandPartId(projectLandPartMapExtendDto.getLandPartId());
                subBoProjectLandPartMap.setProjectExtendId(landAddSubProInfoBody.getVersionId());
                subBoProjectLandPartMap.setLandPartName(projectLandPartMapExtendDto.getLandPartName());
                subBoProjectLandPartMap.setIsAllDev(isDev);
                subBoProjectLandPartMap.setTotalUseLandArea(landAddSubProInfoBody.getLandBasicAreaInfoVo().getTotalMeasure());
                subBoProjectLandPartMap.setCapacityBuildingArea(landAddSubProInfoBody.getLandBasicAreaInfoVo().getMeterBuildMeasure());
                subBoProjectLandPartMap.setCanUseLandArea(landAddSubProInfoBody.getLandBasicAreaInfoVo().getCanUseMeasure());
                subBoProjectLandPartMap.setBuildingLandCoverArea(landAddSubProInfoBody.getLandBasicAreaInfoVo().getBuildCoverMeasure());
                subBoProjectLandPartMap.setTakeByLandUseArea(landAddSubProInfoBody.getLandBasicAreaInfoVo().getUseTakeMeasure());
                subBoProjectLandPartMap.setLandGetPrice(landAddSubProInfoBody.getLandBasicAreaInfoVo().getGetLandPrice());
                subBoProjectLandPartMap.setAboveGroundCalcVolumeArea(projectLandPartMapExtendDto.getAboveGroundCalcVolumeArea());
                subBoProjectLandPartMap.setUnderGroundCalcVolumeArea(projectLandPartMapExtendDto.getUnderGroundCalcVolumeArea());
                subBoProjectLandPartMap.setRefProjectId(projectLandPartMapExtendDto.getSubRefProjectId());
                subBoProjectLandPartMap.setRefProjectName(projectLandPartMapExtendDto.getSubRefProjectName());
                subBoProjectLandPartMap.setCreaterId(currentUser.getId());
                subBoProjectLandPartMap.setCreaterName(currentUser.getName());
                subBoProjectLandPartMap.setCreateTime(LocalDateTime.now());
                subBoProjectLandPartMap.setIsDelete(IsDeleteEnum.NO.getKey());
                subBoProjectLandPartMap.setIsDisable(IsDisableEnum.NO.getKey());

                StringBuffer restMsg = new StringBuffer();
                List<BoLandPartProductTypeMapExtendDto> landPartProductTypeMapExtendDtoList = landPartProductTypeMapMapper.selectProductByProIdAndLandId(subProjectInfoDto.getProjectId(), landAddSubProInfoBody.getLandId());
                List<BoLandPartProductTypeQuotaMap> landPartProductTypeQuotaMapList = landPartProductTypeQuotaMapMapper.selectQuotaByProjectIdAndLandId(subProjectInfoDto.getProjectId(), landAddSubProInfoBody.getLandId());
                if(CollectionUtils.isNotEmpty(landPartProductTypeMapExtendDtoList)){
                    for (BoLandPartProductTypeMapExtendDto productType : landPartProductTypeMapExtendDtoList){
                        String id = UUIDUtils.create();

                        List<QuotaBodyVO> quotaList = new ArrayList<QuotaBodyVO>();
                        if(CollectionUtils.isNotEmpty(landPartProductTypeQuotaMapList)){
                            for (BoLandPartProductTypeQuotaMap quotaType : landPartProductTypeQuotaMapList){
                                if(productType.getId().equals(quotaType.getLandPartProductTypeId())){
                                    QuotaBodyVO quotaBodyVO = new QuotaBodyVO();
                                    quotaBodyVO.setQuotaCode(quotaType.getQuotaCode());

                                    BoQuotaGroupMap boQuotaGroupMap = formatMap.get(quotaType.getQuotaCode());

                                    double subValue = 0d;
                                    String quotaCode = quotaType.getQuotaCode();
                                    if(boQuotaGroupMap != null){
                                        String quotaValue = quotaType.getQuotaValue();
                                        if(StringUtils.isNotBlank(quotaCode) && Constants.inspectQuotaList.contains(quotaCode)){
                                            String subQuotaValue = sumSubQuotaMap.get(productType.getAttrQuotaValue() + "&" + productType.getProductTypeCode() + "&" + quotaType.getQuotaCode());
                                            if(StringUtils.isNotBlank(subQuotaValue)){
                                                subValue = Double.parseDouble(subToString(StringUtils.isBlank(quotaValue) ? "0" : quotaValue, StringUtils.isBlank(subQuotaValue) ? "0" : subQuotaValue));
                                                if(subValue < 0d){
                                                    restMsg.append(productType.getProductTypeName()+boQuotaGroupMap.getName()).append(",");
                                                }
                                            }
                                        }
                                    }

                                    BoProjectLandPartProductTypeQuotaMap projectLandPartProductTypeQuotaMap = new BoProjectLandPartProductTypeQuotaMap();
                                    projectLandPartProductTypeQuotaMap.setId(UUIDUtils.create());
                                    projectLandPartProductTypeQuotaMap.setProjectLandPartProductTypeId(id);
                                    projectLandPartProductTypeQuotaMap.setQuotaGroupMapId(quotaType.getQuotaGroupMapId());
                                    projectLandPartProductTypeQuotaMap.setQuotaId(quotaType.getQuotaId());
                                    projectLandPartProductTypeQuotaMap.setQuotaCode(quotaType.getQuotaCode());
                                    projectLandPartProductTypeQuotaMap.setQuotaValue(affQuotaMap.get(productType.getAttrQuotaValue() + "&" + productType.getProductTypeCode() + "&" + quotaType.getQuotaCode()));
                                    projectLandPartProductTypeQuotaMap.setIsDelete(IsDeleteEnum.NO.getKey());
                                    projectLandPartProductTypeQuotaMap.setIsDisable(IsDisableEnum.NO.getKey());
                                    projectLandPartProductTypeQuotaMap.setCreateTime(LocalDateTime.now());
                                    projectLandPartProductTypeQuotaMap.setCreaterId(currentUser.getId());
                                    projectLandPartProductTypeQuotaMap.setCreaterName(currentUser.getName());
                                    insertProjectLandPartProductTypeQuotaMapList.add(projectLandPartProductTypeQuotaMap);
                                }
                            }
                        }

                        BoProjectLandPartProductTypeMap projectLandPartProductTypeMap = new BoProjectLandPartProductTypeMap();
                        projectLandPartProductTypeMap.setId(id);
                        projectLandPartProductTypeMap.setProjectLandPartId(landPartId);
                        projectLandPartProductTypeMap.setProductTypeId(productType.getProductTypeId());
                        projectLandPartProductTypeMap.setProductTypeCode(productType.getProductTypeCode());
                        projectLandPartProductTypeMap.setProductTypeName(productType.getProductTypeName());
                        projectLandPartProductTypeMap.setIsDelete(IsDeleteEnum.NO.getKey());
                        projectLandPartProductTypeMap.setIsDisable(IsDisableEnum.NO.getKey());
                        projectLandPartProductTypeMap.setCreateTime(LocalDateTime.now());
                        projectLandPartProductTypeMap.setCreaterId(currentUser.getId());
                        projectLandPartProductTypeMap.setCreaterName(currentUser.getName());
                        projectLandPartProductTypeMap.setPropertyRightAttr(productType.getAttrQuotaValue());
                        insertProjectLandPartProductTypeMapList.add(projectLandPartProductTypeMap);
                    }
                }

                if (restMsg != null && restMsg.length() > 0) {
                    jsonResult.setCode(CodeEnum.LAND_QUOTE_USE_ALL_EXTENDS.getKey());
                    jsonResult.setMsg(restMsg.toString()+"已经超标！");
                    return jsonResult;
                }

                if(subBoProjectLandPartMap != null){
                    projectLandPartMapMapper.insert(subBoProjectLandPartMap);
                }

                if(CollectionUtils.isNotEmpty(insertProjectLandPartProductTypeMapList)){
                    projectLandPartProductTypeMapService.saveBatch(insertProjectLandPartProductTypeMapList,100);
                }

                if(CollectionUtils.isNotEmpty(insertProjectLandPartProductTypeQuotaMapList)){
                    projectLandPartProductTypeQuotaMapService.saveBatch(insertProjectLandPartProductTypeQuotaMapList,100);
                }

                jsonResult.setCode(CodeEnum.SUCCESS.getKey());
                jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
                return jsonResult;
            }
        }

        return jsonResult;
    }

    private StringBuffer alculationcArea(LandAddSubProInfoBody landAddSubProInfoBody, StringBuffer areaRestMsg, BoProjectLandPartMap boProjectLandPartMap, SubTotalAreaDto subTotalAreaDto) {
        String subTotal = subToString(StringUtils.isBlank(boProjectLandPartMap.getTotalUseLandArea())?"0":boProjectLandPartMap.getTotalUseLandArea(),
                StringUtils.isBlank(subTotalAreaDto.getSumTotalArea())?"0":subTotalAreaDto.getSumTotalArea());
        subTotal = sub(Double.parseDouble(!StringUtils.isNotBlank(subTotal)?"0":subTotal), Double.parseDouble(!StringUtils.isNotBlank(landAddSubProInfoBody.getLandBasicAreaInfoVo().getTotalMeasure())?"0": landAddSubProInfoBody.getLandBasicAreaInfoVo().getTotalMeasure()));
        if (Double.parseDouble(StringUtils.isBlank(subTotal)?"0":subTotal) < 0) {
            areaRestMsg.append(boProjectLandPartMap.getLandPartName() + "总用地面积 已超过:"+Math.abs(Double.parseDouble(StringUtils.isBlank(subTotal)?"0":subTotal)));
        }

        String subUse = subToString(StringUtils.isBlank(boProjectLandPartMap.getCanUseLandArea())?"0":boProjectLandPartMap.getCanUseLandArea(),
                StringUtils.isBlank(subTotalAreaDto.getSumUseArea())?"0":subTotalAreaDto.getSumUseArea());
        subUse = sub(Double.parseDouble(!StringUtils.isNotBlank(subUse)?"0":subUse), Double.parseDouble(!StringUtils.isNotBlank(landAddSubProInfoBody.getLandBasicAreaInfoVo().getCanUseMeasure())?"0": landAddSubProInfoBody.getLandBasicAreaInfoVo().getCanUseMeasure()));
        if (Double.parseDouble(StringUtils.isBlank(subUse)?"0":subUse) < 0) {
            areaRestMsg.append(boProjectLandPartMap.getLandPartName() + "可建设用地面积 已超过:"+Math.abs(Double.parseDouble(StringUtils.isBlank(subUse)?"0":subUse)));
        }

        String subCover = subToString(StringUtils.isBlank(boProjectLandPartMap.getBuildingLandCoverArea())?"0":boProjectLandPartMap.getBuildingLandCoverArea(),
                StringUtils.isBlank(subTotalAreaDto.getSumCoverArea())?"0":subTotalAreaDto.getSumCoverArea());
        subCover = sub(Double.parseDouble(!StringUtils.isNotBlank(subCover)?"0":subCover), Double.parseDouble(!StringUtils.isNotBlank(landAddSubProInfoBody.getLandBasicAreaInfoVo().getBuildCoverMeasure())?"0": landAddSubProInfoBody.getLandBasicAreaInfoVo().getBuildCoverMeasure()));
        if (Double.parseDouble(StringUtils.isBlank(subCover)?"0":subCover) < 0) {
            if(areaRestMsg != null && areaRestMsg.length() > 0){
                areaRestMsg.append(",");
            }
            areaRestMsg.append(boProjectLandPartMap.getLandPartName() + "建筑占地面积 已超过:"+Math.abs(Double.parseDouble(StringUtils.isBlank(subCover)?"0":subCover)));
        }

        String subTake = subToString(StringUtils.isBlank(boProjectLandPartMap.getTakeByLandUseArea())?"0":boProjectLandPartMap.getTakeByLandUseArea(),
                StringUtils.isBlank(subTotalAreaDto.getSumTakeArea())?"0":subTotalAreaDto.getSumTakeArea());
        subTake = sub(Double.parseDouble(!StringUtils.isNotBlank(subTake)?"0":subTake), Double.parseDouble(!StringUtils.isNotBlank(landAddSubProInfoBody.getLandBasicAreaInfoVo().getUseTakeMeasure())?"0": landAddSubProInfoBody.getLandBasicAreaInfoVo().getUseTakeMeasure()));
        if (Double.parseDouble(StringUtils.isBlank(subTake)?"0":subTake) < 0) {
            if(areaRestMsg != null && areaRestMsg.length() > 0){
                areaRestMsg.append(",");
            }
            areaRestMsg.append(boProjectLandPartMap.getLandPartName() + "代征地用地面积 已超过:"+Math.abs(Double.parseDouble(StringUtils.isBlank(subTake)?"0":subTake)));
        }

        String subPrice = subToString(StringUtils.isBlank(boProjectLandPartMap.getLandGetPrice())?"0":boProjectLandPartMap.getLandGetPrice(),
                StringUtils.isBlank(subTotalAreaDto.getSumGetPrice())?"0":subTotalAreaDto.getSumGetPrice());
        subPrice = sub(Double.parseDouble(!StringUtils.isNotBlank(subPrice)?"0":subPrice), Double.parseDouble(!StringUtils.isNotBlank(landAddSubProInfoBody.getLandBasicAreaInfoVo().getGetLandPrice())?"0": landAddSubProInfoBody.getLandBasicAreaInfoVo().getGetLandPrice()));
        if (Double.parseDouble(StringUtils.isBlank(subPrice)?"0":subPrice) < 0) {
            if(areaRestMsg != null && areaRestMsg.length() > 0){
                areaRestMsg.append(",");
            }
            areaRestMsg.append(boProjectLandPartMap.getLandPartName() + "土地获取价款 已超过:"+Math.abs(Double.parseDouble(StringUtils.isBlank(subPrice)?"0":subPrice)));
        }

        String subBuild = subToString(StringUtils.isBlank(boProjectLandPartMap.getCapacityBuildingArea())?"0":boProjectLandPartMap.getCapacityBuildingArea(),
                StringUtils.isBlank(subTotalAreaDto.getSumBuildArea())?"0":subTotalAreaDto.getSumBuildArea());
        subBuild = sub(Double.parseDouble(!StringUtils.isNotBlank(subBuild)?"0":subBuild), Double.parseDouble(!StringUtils.isNotBlank(landAddSubProInfoBody.getLandBasicAreaInfoVo().getMeterBuildMeasure())?"0": landAddSubProInfoBody.getLandBasicAreaInfoVo().getMeterBuildMeasure()));
        if (Double.parseDouble(StringUtils.isBlank(subBuild)?"0":subBuild) < 0) {
            if(areaRestMsg != null && areaRestMsg.length() > 0){
                areaRestMsg.append(",");
            }
            areaRestMsg.append(boProjectLandPartMap.getLandPartName() + "计容建筑面积 已超过:"+Math.abs(Double.parseDouble(StringUtils.isBlank(subBuild)?"0":subBuild)));
        }

        return areaRestMsg;
    }

    @Override
    @Transactional
    public JSONResult updateLandInfoToSubProject(LandAddSubProInfoBody landAddSubProInfoBody, CurrentUserVO currentUser, JSONResult jsonResult) throws Exception {

        jsonResult.setCode(CodeEnum.ERROR.getKey());
        jsonResult.setMsg(CodeEnum.ERROR.getValue());

        ProjectInfoDto subProjectInfoDto = projectExtendMapper.selectSubProjectByVersionId(landAddSubProInfoBody.getVersionId());

        String version = subProjectInfoDto.getVersion();
        if(StringUtils.isBlank(version)){
            jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
            return jsonResult;
        }

        Integer versionStatus = subProjectInfoDto.getVersionStatus();
        if(!versionStatus.equals(VersionStatusEnum.CREATING.getKey()) && !versionStatus.equals(VersionStatusEnum.REJECTED.getKey())){
            jsonResult.setCode(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getKey());
            jsonResult.setMsg(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getValue());
            return jsonResult;
        }

        List<String> subProjectIds = projectExtendMapper.selectSubProIdsByVersionId(landAddSubProInfoBody.getVersionId());

        //项目地块和投决会信息
        BoProjectLandPartMapExtendDto projectLandPartMapExtendDto = projectLandPartMapMapper.selectProLandPartBySubExtIdAndLandId(landAddSubProInfoBody.getVersionId(), landAddSubProInfoBody.getLandId());
        List<BoLandPartProductTypeMapExtendDto> landPartProductTypeMapExtendDtoList = landPartProductTypeMapMapper.selectProductByProIdAndLandId(subProjectInfoDto.getProjectId(), landAddSubProInfoBody.getLandId());
        List<BoLandPartProductTypeQuotaMap> landPartProductTypeQuotaMapList = landPartProductTypeQuotaMapMapper.selectQuotaByProjectIdAndLandId(subProjectInfoDto.getProjectId(), landAddSubProInfoBody.getLandId());

        Integer isDev = landAddSubProInfoBody.getIsDev();
        if(isDev.equals(IsDevEnum.YES.getKey())){

            //查看分期指标是否被使用
            subProjectIds.remove(subProjectInfoDto.getSubProjectId());
            if(CollectionUtils.isNotEmpty(subProjectIds)){
                List<BoProjectLandPartProductTypeQuotaMap> projectProductTypeQuotaDtos = projectLandPartProductTypeQuotaMapMapper.selectQuotaBySubIdsAndLandId(subProjectIds, landAddSubProInfoBody.getLandId());
                if (CollectionUtils.isNotEmpty(projectProductTypeQuotaDtos)) {
                    jsonResult.setCode(CodeEnum.SUB_PROJECT_LAND_EXTENDS.getKey());
                    jsonResult.setMsg(CodeEnum.SUB_PROJECT_LAND_EXTENDS.getValue());
                    return jsonResult;
                }
            }

            //bo_project_land_part_map
            BoProjectLandPartMap projectLandPartMap = projectLandPartMapMapper.selectLandPartBySubExtIdAndLandId(landAddSubProInfoBody.getVersionId(), landAddSubProInfoBody.getLandId());
            if(projectLandPartMap != null){
                String landPartId = projectLandPartMap.getId();

                projectLandPartMap.setLandPartId(projectLandPartMap.getLandPartId());
                projectLandPartMap.setProjectExtendId(landAddSubProInfoBody.getVersionId());
                projectLandPartMap.setLandPartName(projectLandPartMap.getLandPartName());
                projectLandPartMap.setIsAllDev(isDev);
                projectLandPartMap.setTotalUseLandArea(projectLandPartMapExtendDto.getTotalUseLandArea());
                projectLandPartMap.setCapacityBuildingArea(projectLandPartMapExtendDto.getCapacityBuildingArea());
                projectLandPartMap.setCanUseLandArea(projectLandPartMapExtendDto.getCanUseLandArea());
                projectLandPartMap.setBuildingLandCoverArea(projectLandPartMapExtendDto.getBuildingLandCoverArea());
                projectLandPartMap.setTakeByLandUseArea(projectLandPartMapExtendDto.getTakeByLandUseArea());
                projectLandPartMap.setLandGetPrice(projectLandPartMapExtendDto.getLandGetPrice());
                projectLandPartMap.setAboveGroundCalcVolumeArea(projectLandPartMapExtendDto.getAboveGroundCalcVolumeArea());
                projectLandPartMap.setUnderGroundCalcVolumeArea(projectLandPartMapExtendDto.getUnderGroundCalcVolumeArea());
                projectLandPartMap.setRefProjectId(projectLandPartMap.getRefProjectId());
                projectLandPartMap.setRefProjectName(projectLandPartMap.getRefProjectName());
                projectLandPartMap.setCreaterId(currentUser.getId());
                projectLandPartMap.setCreaterName(currentUser.getName());
                projectLandPartMap.setCreateTime(LocalDateTime.now());
                projectLandPartMap.setIsDelete(IsDeleteEnum.NO.getKey());
                projectLandPartMap.setIsDisable(IsDisableEnum.NO.getKey());

                //删除之前分期业态和指标
                QueryWrapper<BoProjectLandPartProductTypeMap> productQueryWrapper = new QueryWrapper<BoProjectLandPartProductTypeMap>();
                productQueryWrapper.eq("project_land_part_id", projectLandPartMap.getId())
                        .eq("is_delete", IsDeleteEnum.NO.getKey())
                        .eq("is_disable", IsDisableEnum.NO.getKey());
                List<BoProjectLandPartProductTypeMap> boProjectLandPartProductTypeMaps = projectLandPartProductTypeMapMapper.selectList(productQueryWrapper);

                List<BoProjectLandPartProductTypeMap> projectLandPartProductTypeMapList = new ArrayList<BoProjectLandPartProductTypeMap>();
                List<BoProjectLandPartProductTypeQuotaMap> projectLandPartProductTypeQuotaMapList = new ArrayList<BoProjectLandPartProductTypeQuotaMap>();

                LocalDateTime now = LocalDateTime.now();
                if(CollectionUtils.isNotEmpty(boProjectLandPartProductTypeMaps)){
                    List<String> productIds = new ArrayList<String>();
                    for (int i = 0; i < boProjectLandPartProductTypeMaps.size(); i++) {
                        BoProjectLandPartProductTypeMap productTypeMap = boProjectLandPartProductTypeMaps.get(i);
                        productTypeMap.setId(productTypeMap.getId());
                        productTypeMap.setIsDelete(IsDeleteEnum.YES.getKey());
                        productTypeMap.setUpdaterId(currentUser.getId());
                        productTypeMap.setUpdaterName(currentUser.getName());
                        productTypeMap.setUpdateTime(now);
                        projectLandPartProductTypeMapList.add(productTypeMap);

                        productIds.add(productTypeMap.getId());
                    }

                    QueryWrapper<BoProjectLandPartProductTypeQuotaMap> quotaQueryWrapper = new QueryWrapper<BoProjectLandPartProductTypeQuotaMap>();
                    quotaQueryWrapper.in("project_land_part_product_type_id", productIds)
                            .eq("is_delete", IsDeleteEnum.NO.getKey())
                            .eq("is_disable", IsDisableEnum.NO.getKey());
                    List<BoProjectLandPartProductTypeQuotaMap> boProjectLandPartProductTypeQuotaMaps = projectLandPartProductTypeQuotaMapMapper.selectList(quotaQueryWrapper);
                    if (CollectionUtils.isNotEmpty(boProjectLandPartProductTypeQuotaMaps)) {
                        for (int j = 0; j < boProjectLandPartProductTypeQuotaMaps.size(); j++) {
                            BoProjectLandPartProductTypeQuotaMap boProjectLandPartProductTypeQuotaMap = boProjectLandPartProductTypeQuotaMaps.get(j);
                            boProjectLandPartProductTypeQuotaMap.setId(currentUser.getId());
                            boProjectLandPartProductTypeQuotaMap.setIsDelete(IsDeleteEnum.YES.getKey());
                            boProjectLandPartProductTypeQuotaMap.setUpdaterId(currentUser.getId());
                            boProjectLandPartProductTypeQuotaMap.setUpdaterName(currentUser.getName());
                            boProjectLandPartProductTypeQuotaMap.setUpdateTime(now);
                            projectLandPartProductTypeQuotaMapList.add(boProjectLandPartProductTypeQuotaMap);
                        }
                    }
                }

                if (projectLandPartProductTypeMapList != null && projectLandPartProductTypeMapList.size() > 0) {
                    projectLandPartProductTypeMapMapper.updateIsDeleteBatch(projectLandPartProductTypeMapList);
                }
                if (projectLandPartProductTypeQuotaMapList != null && projectLandPartProductTypeQuotaMapList.size() > 0) {
                    projectLandPartProductTypeQuotaMapMapper.updateIsDeleteBatch(projectLandPartProductTypeQuotaMapList);
                }

                //修改分期业态和指标信息
                List<BoProjectLandPartProductTypeMap> insertProjectLandPartProductTypeMapList = new ArrayList<BoProjectLandPartProductTypeMap>();
                List<BoProjectLandPartProductTypeQuotaMap> insertProjectLandPartProductTypeQuotaMapList = new ArrayList<BoProjectLandPartProductTypeQuotaMap>();

                if(CollectionUtils.isNotEmpty(landPartProductTypeMapExtendDtoList)){
                    for (BoLandPartProductTypeMapExtendDto productDto : landPartProductTypeMapExtendDtoList){
                        LocalDateTime time = LocalDateTime.now();

                        BoProjectLandPartProductTypeMap projectLandPartProductTypeMap = new BoProjectLandPartProductTypeMap();
                        String id = UUIDUtils.create();
                        projectLandPartProductTypeMap.setId(id);
                        projectLandPartProductTypeMap.setProjectLandPartId(landAddSubProInfoBody.getLandId());
                        projectLandPartProductTypeMap.setProductTypeId(productDto.getProductTypeId());
                        projectLandPartProductTypeMap.setProductTypeCode(productDto.getProductTypeCode());
                        projectLandPartProductTypeMap.setProductTypeName(productDto.getProductTypeName());
                        projectLandPartProductTypeMap.setIsDelete(IsDeleteEnum.NO.getKey());
                        projectLandPartProductTypeMap.setIsDisable(IsDisableEnum.NO.getKey());
                        projectLandPartProductTypeMap.setCreateTime(time);
                        projectLandPartProductTypeMap.setCreaterId(currentUser.getId());
                        projectLandPartProductTypeMap.setCreaterName(currentUser.getName());
                        projectLandPartProductTypeMap.setPropertyRightAttr(productDto.getAttrQuotaValue());
                        projectLandPartProductTypeMap.setUpdaterId(currentUser.getId());
                        projectLandPartProductTypeMap.setUpdaterName(currentUser.getName());
                        projectLandPartProductTypeMap.setUpdateTime(time);
                        insertProjectLandPartProductTypeMapList.add(projectLandPartProductTypeMap);

                        if(CollectionUtils.isNotEmpty(landPartProductTypeQuotaMapList)){
                            for (BoLandPartProductTypeQuotaMap quotaMap : landPartProductTypeQuotaMapList){
                                if(quotaMap.getLandPartProductTypeId().equals(productDto.getId())){
                                    BoProjectLandPartProductTypeQuotaMap projectLandPartProductTypeQuotaMap = new BoProjectLandPartProductTypeQuotaMap();
                                    projectLandPartProductTypeQuotaMap.setId(UUIDUtils.create());
                                    projectLandPartProductTypeQuotaMap.setProjectLandPartProductTypeId(id);
                                    projectLandPartProductTypeQuotaMap.setQuotaGroupMapId(quotaMap.getQuotaGroupMapId());
                                    projectLandPartProductTypeQuotaMap.setQuotaId(quotaMap.getQuotaId());
                                    projectLandPartProductTypeQuotaMap.setQuotaCode(quotaMap.getQuotaCode());
                                    projectLandPartProductTypeQuotaMap.setQuotaValue(quotaMap.getQuotaValue());
                                    projectLandPartProductTypeQuotaMap.setIsDelete(IsDeleteEnum.NO.getKey());
                                    projectLandPartProductTypeQuotaMap.setIsDisable(IsDisableEnum.NO.getKey());
                                    projectLandPartProductTypeQuotaMap.setCreateTime(time);
                                    projectLandPartProductTypeQuotaMap.setCreaterId(currentUser.getId());
                                    projectLandPartProductTypeQuotaMap.setCreaterName(currentUser.getName());
                                    projectLandPartProductTypeQuotaMap.setUpdaterId(currentUser.getId());
                                    projectLandPartProductTypeQuotaMap.setUpdaterName(currentUser.getName());
                                    projectLandPartProductTypeQuotaMap.setUpdateTime(time);
                                    insertProjectLandPartProductTypeQuotaMapList.add(projectLandPartProductTypeQuotaMap);
                                }
                            }
                        }
                    }
                }

                projectLandPartMapMapper.updateById(projectLandPartMap);

                if (CollectionUtils.isNotEmpty(insertProjectLandPartProductTypeMapList)) {
                    projectLandPartProductTypeMapService.saveBatch(insertProjectLandPartProductTypeMapList, 100);
                }

                if (CollectionUtils.isNotEmpty(insertProjectLandPartProductTypeQuotaMapList)) {
                    projectLandPartProductTypeQuotaMapService.saveBatch(insertProjectLandPartProductTypeQuotaMapList, 100);
                }

                jsonResult.setCode(CodeEnum.SUCCESS.getKey());
                jsonResult.setMsg(CodeEnum.SUCCESS.getValue());

                return jsonResult;
            }
        }else if(isDev.equals(IsDevEnum.NO.getKey())){
            StringBuffer areaRestMsg = new StringBuffer();
            subProjectIds.remove(subProjectInfoDto.getSubProjectId());
            SubTotalAreaDto subTotalAreaDto = new SubTotalAreaDto();
            if(CollectionUtils.isNotEmpty(subProjectIds)){
                subTotalAreaDto = projectLandPartMapMapper.selectSubTotalAreaBySubIdsAndLandId(subProjectIds, landAddSubProInfoBody.getLandId());
            }

            if(subTotalAreaDto != null){
                areaRestMsg = areaCalculation(landAddSubProInfoBody, projectLandPartMapExtendDto, areaRestMsg, subTotalAreaDto);
            }

            if (areaRestMsg != null && areaRestMsg.length() > 0) {
                jsonResult.setCode(CodeEnum.LAND_AREAT_EXTENDS.getKey());
                jsonResult.setMsg(areaRestMsg.toString());
                return jsonResult;
            }

            //查询指标基础信息
            Map<String, BoQuotaGroupMap> formatMap = new HashMap<String, BoQuotaGroupMap>();
            QueryWrapper<BoQuotaGroupMap> queryTypeQuotaWrapper = new QueryWrapper<BoQuotaGroupMap>();
            queryTypeQuotaWrapper.eq("group_code", "LAND_PART_PRODUCT_TYPE")
                    .eq("is_delete", IsDeleteEnum.NO.getKey())
                    .eq("is_disable", IsDisableEnum.NO.getKey());
            List<BoQuotaGroupMap> boQuotaGroupMaps = quotaGroupMapMapper.selectList(queryTypeQuotaWrapper);
            if(CollectionUtils.isNotEmpty(boQuotaGroupMaps)){
                for (int i = 0; i < boQuotaGroupMaps.size(); i++){
                    BoQuotaGroupMap boQuotaGroupMap = boQuotaGroupMaps.get(i);
                    formatMap.put(boQuotaGroupMap.getCode(), boQuotaGroupMap);
                }
            }

            //页面输入的业态信息
            Map<String, String> sumSubQuotaMap = new HashMap<String, String>();
            Map<String, String> affQuotaMap = new HashMap<String, String>();
            List<LandProductToProjectVo> affLandProductToProjectVo = landAddSubProInfoBody.getLandProductToProjectVo();
            if(CollectionUtils.isNotEmpty(affLandProductToProjectVo)){
                for (LandProductToProjectVo productType : affLandProductToProjectVo){
                    List<QuotaBodyVO> quotaList = productType.getQuotaList();
                    if(CollectionUtils.isNotEmpty(affLandProductToProjectVo)){
                        for (QuotaBodyVO quotaType : quotaList){
                            affQuotaMap.put(productType.getProductAttrType() + "&" + productType.getProductTypeCode() + "&" + quotaType.getQuotaCode(), quotaType.getQuotaValue());

                            sumSubQuotaMap.put(productType.getProductAttrType()+"&"+productType.getProductTypeCode()+"&"+quotaType.getQuotaCode(), quotaType.getQuotaValue());
                        }
                    }
                }
            }

            //查询分期使用地块业态指标信息
            if(CollectionUtils.isNotEmpty(subProjectIds)){
                List<ProjectProductTypeQuotaDto> projectProductTypeQuotaDtos = projectLandPartProductTypeQuotaMapMapper.selectTotalQuotaBySubProjectIdsAndLandId(subProjectIds, landAddSubProInfoBody.getLandId());
                if(CollectionUtils.isNotEmpty(projectProductTypeQuotaDtos)){
                    for (ProjectProductTypeQuotaDto productType : projectProductTypeQuotaDtos){
                        String sumSubQuotaValue = sumSubQuotaMap.get(productType.getAttrQuotaValue() + "&" + productType.getProductCode() + "&" + productType.getQuoteCode());
                        if(StringUtils.isNotBlank(sumSubQuotaValue)){

                            BoQuotaGroupMap boQuotaGroupMap = formatMap.get(productType.getQuoteCode());
                            if(boQuotaGroupMap != null){
                                String valueType = boQuotaGroupMap.getValueType();
                                if(StringUtils.isNotBlank(valueType) && valueType.trim().equals("NUMBER")){
                                    String sumsubQuotaValue = addToString(sumSubQuotaValue, productType.getQuoteValue());
                                    sumSubQuotaMap.put(productType.getAttrQuotaValue() + "&" + productType.getProductCode() + "&" + productType.getQuoteCode(), String.valueOf(sumsubQuotaValue));
                                }
                            }
                        }
                    }
                }
            }

            //bo_project_land_part_map
            BoProjectLandPartMap projectLandPartMap = projectLandPartMapMapper.selectLandPartBySubExtIdAndLandId(landAddSubProInfoBody.getVersionId(), landAddSubProInfoBody.getLandId());
            if(projectLandPartMap != null) {
                String landPartId = projectLandPartMap.getId();

                projectLandPartMap.setLandPartId(projectLandPartMap.getLandPartId());
                projectLandPartMap.setProjectExtendId(landAddSubProInfoBody.getVersionId());
                projectLandPartMap.setLandPartName(projectLandPartMap.getLandPartName());
                projectLandPartMap.setIsAllDev(isDev);
                projectLandPartMap.setTotalUseLandArea(landAddSubProInfoBody.getLandBasicAreaInfoVo().getTotalMeasure());
                projectLandPartMap.setCapacityBuildingArea(landAddSubProInfoBody.getLandBasicAreaInfoVo().getMeterBuildMeasure());
                projectLandPartMap.setCanUseLandArea(landAddSubProInfoBody.getLandBasicAreaInfoVo().getCanUseMeasure());
                projectLandPartMap.setBuildingLandCoverArea(landAddSubProInfoBody.getLandBasicAreaInfoVo().getBuildCoverMeasure());
                projectLandPartMap.setTakeByLandUseArea(landAddSubProInfoBody.getLandBasicAreaInfoVo().getUseTakeMeasure());
                projectLandPartMap.setLandGetPrice(landAddSubProInfoBody.getLandBasicAreaInfoVo().getGetLandPrice());
                projectLandPartMap.setAboveGroundCalcVolumeArea(projectLandPartMapExtendDto.getAboveGroundCalcVolumeArea());
                projectLandPartMap.setUnderGroundCalcVolumeArea(projectLandPartMapExtendDto.getUnderGroundCalcVolumeArea());
                projectLandPartMap.setRefProjectId(projectLandPartMap.getRefProjectId());
                projectLandPartMap.setRefProjectName(projectLandPartMap.getRefProjectName());
                projectLandPartMap.setUpdaterId(currentUser.getId());
                projectLandPartMap.setUpdaterName(currentUser.getName());
                projectLandPartMap.setUpdateTime(LocalDateTime.now());
                projectLandPartMap.setIsDelete(IsDeleteEnum.NO.getKey());
                projectLandPartMap.setIsDisable(IsDisableEnum.NO.getKey());

                //查询投决会业态指标信息
                List<BoProjectLandPartProductTypeMap> insertProjectLandPartProductTypeMapList = new ArrayList<BoProjectLandPartProductTypeMap>();
                List<BoProjectLandPartProductTypeQuotaMap> insertProjectLandPartProductTypeQuotaMapList = new ArrayList<BoProjectLandPartProductTypeQuotaMap>();

                StringBuffer restMsg = new StringBuffer();
                if(CollectionUtils.isNotEmpty(landPartProductTypeMapExtendDtoList)){
                    for (BoLandPartProductTypeMapExtendDto productType : landPartProductTypeMapExtendDtoList){
                        String id = UUIDUtils.create();
                        if(CollectionUtils.isNotEmpty(landPartProductTypeQuotaMapList)){
                            for (BoLandPartProductTypeQuotaMap quotaType : landPartProductTypeQuotaMapList){
                                if(productType.getId().equals(quotaType.getLandPartProductTypeId())){
                                    QuotaBodyVO quotaBodyVO = new QuotaBodyVO();
                                    quotaBodyVO.setQuotaCode(quotaType.getQuotaCode());

                                    BoQuotaGroupMap boQuotaGroupMap = formatMap.get(quotaType.getQuotaCode());

                                    double subValue = 0d;
                                    String quotaCode = quotaType.getQuotaCode();
                                    if(boQuotaGroupMap != null){
                                        String quotaValue = quotaType.getQuotaValue();
                                        if(StringUtils.isNotBlank(quotaCode) && Constants.inspectQuotaList.contains(quotaCode)){
                                            String subQuotaValue = sumSubQuotaMap.get(productType.getAttrQuotaValue() + "&" + productType.getProductTypeCode() + "&" + quotaType.getQuotaCode());
                                            if(StringUtils.isNotBlank(subQuotaValue)){
                                                subValue = Double.parseDouble(subToString(StringUtils.isBlank(quotaValue) ? "0" : quotaValue, StringUtils.isBlank(subQuotaValue) ? "0" : subQuotaValue));
                                                if(subValue < 0d){
                                                    restMsg.append(boQuotaGroupMap.getName()).append(",");
                                                }
                                            }
                                        }
                                    }

                                    BoProjectLandPartProductTypeQuotaMap projectLandPartProductTypeQuotaMap = new BoProjectLandPartProductTypeQuotaMap();
                                    projectLandPartProductTypeQuotaMap.setId(UUIDUtils.create());
                                    projectLandPartProductTypeQuotaMap.setProjectLandPartProductTypeId(id);
                                    projectLandPartProductTypeQuotaMap.setQuotaGroupMapId(quotaType.getQuotaGroupMapId());
                                    projectLandPartProductTypeQuotaMap.setQuotaId(quotaType.getQuotaId());
                                    projectLandPartProductTypeQuotaMap.setQuotaCode(quotaType.getQuotaCode());
                                    projectLandPartProductTypeQuotaMap.setQuotaValue(affQuotaMap.get(productType.getAttrQuotaValue() + "&" + productType.getProductTypeCode() + "&" + quotaType.getQuotaCode()));
                                    projectLandPartProductTypeQuotaMap.setIsDelete(IsDeleteEnum.NO.getKey());
                                    projectLandPartProductTypeQuotaMap.setIsDisable(IsDisableEnum.NO.getKey());
                                    projectLandPartProductTypeQuotaMap.setCreateTime(LocalDateTime.now());
                                    projectLandPartProductTypeQuotaMap.setCreaterId(currentUser.getId());
                                    projectLandPartProductTypeQuotaMap.setCreaterName(currentUser.getName());
                                    insertProjectLandPartProductTypeQuotaMapList.add(projectLandPartProductTypeQuotaMap);
                                }
                            }
                        }

                        BoProjectLandPartProductTypeMap projectLandPartProductTypeMap = new BoProjectLandPartProductTypeMap();
                        projectLandPartProductTypeMap.setId(id);
                        projectLandPartProductTypeMap.setProjectLandPartId(landPartId);
                        projectLandPartProductTypeMap.setProductTypeId(productType.getProductTypeId());
                        projectLandPartProductTypeMap.setProductTypeCode(productType.getProductTypeCode());
                        projectLandPartProductTypeMap.setProductTypeName(productType.getProductTypeName());
                        projectLandPartProductTypeMap.setIsDelete(IsDeleteEnum.NO.getKey());
                        projectLandPartProductTypeMap.setIsDisable(IsDisableEnum.NO.getKey());
                        projectLandPartProductTypeMap.setCreateTime(LocalDateTime.now());
                        projectLandPartProductTypeMap.setCreaterId(currentUser.getId());
                        projectLandPartProductTypeMap.setCreaterName(currentUser.getName());
                        projectLandPartProductTypeMap.setPropertyRightAttr(productType.getAttrQuotaValue());
                        insertProjectLandPartProductTypeMapList.add(projectLandPartProductTypeMap);
                    }
                }

                if (restMsg != null && restMsg.length() > 0) {
                    jsonResult.setCode(CodeEnum.LAND_QUOTE_USE_ALL_EXTENDS.getKey());
                    jsonResult.setMsg(restMsg.toString()+"已经超标！");
                    return jsonResult;
                }

                //删除之前分期业态和指标
                QueryWrapper<BoProjectLandPartProductTypeMap> productQueryWrapper = new QueryWrapper<BoProjectLandPartProductTypeMap>();
                productQueryWrapper.eq("project_land_part_id", projectLandPartMap.getId())
                        .eq("is_delete", IsDeleteEnum.NO.getKey())
                        .eq("is_disable", IsDisableEnum.NO.getKey());
                List<BoProjectLandPartProductTypeMap> boProjectLandPartProductTypeMaps = projectLandPartProductTypeMapMapper.selectList(productQueryWrapper);

                List<BoProjectLandPartProductTypeMap> projectLandPartProductTypeMapList = new ArrayList<BoProjectLandPartProductTypeMap>();
                List<BoProjectLandPartProductTypeQuotaMap> projectLandPartProductTypeQuotaMapList = new ArrayList<BoProjectLandPartProductTypeQuotaMap>();

                LocalDateTime now = LocalDateTime.now();
                if(CollectionUtils.isNotEmpty(boProjectLandPartProductTypeMaps)){
                    List<String> productIds = new ArrayList<String>();
                    for (int i = 0; i < boProjectLandPartProductTypeMaps.size(); i++) {
                        BoProjectLandPartProductTypeMap productTypeMap = boProjectLandPartProductTypeMaps.get(i);
                        productTypeMap.setId(productTypeMap.getId());
                        productTypeMap.setIsDelete(IsDeleteEnum.YES.getKey());
                        productTypeMap.setUpdaterId(currentUser.getId());
                        productTypeMap.setUpdaterName(currentUser.getName());
                        productTypeMap.setUpdateTime(now);
                        projectLandPartProductTypeMapList.add(productTypeMap);

                        productIds.add(productTypeMap.getId());
                    }

                    QueryWrapper<BoProjectLandPartProductTypeQuotaMap> quotaQueryWrapper = new QueryWrapper<BoProjectLandPartProductTypeQuotaMap>();
                    quotaQueryWrapper.in("project_land_part_product_type_id", productIds)
                            .eq("is_delete", IsDeleteEnum.NO.getKey())
                            .eq("is_disable", IsDisableEnum.NO.getKey());
                    List<BoProjectLandPartProductTypeQuotaMap> boProjectLandPartProductTypeQuotaMaps = projectLandPartProductTypeQuotaMapMapper.selectList(quotaQueryWrapper);
                    if (CollectionUtils.isNotEmpty(boProjectLandPartProductTypeQuotaMaps)) {
                        for (int j = 0; j < boProjectLandPartProductTypeQuotaMaps.size(); j++) {
                            BoProjectLandPartProductTypeQuotaMap boProjectLandPartProductTypeQuotaMap = boProjectLandPartProductTypeQuotaMaps.get(j);
                            boProjectLandPartProductTypeQuotaMap.setId(currentUser.getId());
                            boProjectLandPartProductTypeQuotaMap.setIsDelete(IsDeleteEnum.YES.getKey());
                            boProjectLandPartProductTypeQuotaMap.setUpdaterId(currentUser.getId());
                            boProjectLandPartProductTypeQuotaMap.setUpdaterName(currentUser.getName());
                            boProjectLandPartProductTypeQuotaMap.setUpdateTime(now);
                            projectLandPartProductTypeQuotaMapList.add(boProjectLandPartProductTypeQuotaMap);
                        }
                    }
                }

                if (projectLandPartProductTypeMapList != null && projectLandPartProductTypeMapList.size() > 0) {
                    projectLandPartProductTypeMapMapper.updateIsDeleteBatch(projectLandPartProductTypeMapList);
                }

                if (projectLandPartProductTypeQuotaMapList != null && projectLandPartProductTypeQuotaMapList.size() > 0) {
                    projectLandPartProductTypeQuotaMapMapper.updateIsDeleteBatch(projectLandPartProductTypeQuotaMapList);
                }

                if(projectLandPartMap != null){
                    projectLandPartMapMapper.updateById(projectLandPartMap);
                }

                if(CollectionUtils.isNotEmpty(insertProjectLandPartProductTypeMapList)){
                    projectLandPartProductTypeMapService.saveBatch(insertProjectLandPartProductTypeMapList,100);
                }

                if(CollectionUtils.isNotEmpty(insertProjectLandPartProductTypeQuotaMapList)){
                    projectLandPartProductTypeQuotaMapService.saveBatch(insertProjectLandPartProductTypeQuotaMapList,100);
                }
            }

            jsonResult.setCode(CodeEnum.SUCCESS.getKey());
            jsonResult.setMsg(CodeEnum.SUCCESS.getValue());

            return jsonResult;
        }

        return jsonResult;
    }

    private StringBuffer areaCalculation(LandAddSubProInfoBody landAddSubProInfoBody, BoProjectLandPartMapExtendDto projectLandPartMapExtendDto, StringBuffer areaRestMsg, SubTotalAreaDto subTotalAreaDto) {
        String subTotal = subToString(StringUtils.isBlank(projectLandPartMapExtendDto.getTotalUseLandArea())?"0":projectLandPartMapExtendDto.getTotalUseLandArea(),
                StringUtils.isBlank(subTotalAreaDto.getSumTotalArea())?"0":subTotalAreaDto.getSumTotalArea());
        subTotal = sub(Double.parseDouble(StringUtils.isBlank(subTotal)?"0":subTotal), Double.parseDouble(StringUtils.isBlank(landAddSubProInfoBody.getLandBasicAreaInfoVo().getTotalMeasure())?"0": landAddSubProInfoBody.getLandBasicAreaInfoVo().getTotalMeasure()));
        if (Double.parseDouble(StringUtils.isBlank(subTotal)?"0":subTotal) < 0) {
            if(areaRestMsg != null && areaRestMsg.length() > 0){
                areaRestMsg.append(",");
            }
            areaRestMsg.append(projectLandPartMapExtendDto.getLandPartName() + "总用地面积 已超过:"+Math.abs(Double.parseDouble(StringUtils.isBlank(subTotal)?"0":subTotal)));
        }

        String subUse = subToString(StringUtils.isBlank(projectLandPartMapExtendDto.getCanUseLandArea())?"0":projectLandPartMapExtendDto.getCanUseLandArea(),
                StringUtils.isBlank(subTotalAreaDto.getSumUseArea())?"0":subTotalAreaDto.getSumUseArea());
        subUse = sub(Double.parseDouble(StringUtils.isBlank(subUse)?"0":subUse), Double.parseDouble(StringUtils.isBlank(landAddSubProInfoBody.getLandBasicAreaInfoVo().getCanUseMeasure())?"0": landAddSubProInfoBody.getLandBasicAreaInfoVo().getCanUseMeasure()));
        if (Double.parseDouble(StringUtils.isBlank(subUse)?"0":subUse) < 0) {
            if(areaRestMsg != null && areaRestMsg.length() > 0){
                areaRestMsg.append(",");
            }
            areaRestMsg.append(projectLandPartMapExtendDto.getLandPartName() + "可建设用地面积 已超过:"+Math.abs(Double.parseDouble(StringUtils.isBlank(subUse)?"0":subUse)));
        }

        String subCover = subToString(StringUtils.isBlank(projectLandPartMapExtendDto.getBuildingLandCoverArea())?"0":projectLandPartMapExtendDto.getBuildingLandCoverArea(),
                StringUtils.isBlank(subTotalAreaDto.getSumCoverArea())?"0":subTotalAreaDto.getSumCoverArea());
        subCover = sub(Double.parseDouble(StringUtils.isBlank(subCover)?"0":subCover), Double.parseDouble(StringUtils.isBlank(landAddSubProInfoBody.getLandBasicAreaInfoVo().getBuildCoverMeasure())?"0": landAddSubProInfoBody.getLandBasicAreaInfoVo().getBuildCoverMeasure()));
        if (Double.parseDouble(StringUtils.isBlank(subCover)?"0":subCover) < 0) {
            if(areaRestMsg != null && areaRestMsg.length() > 0){
                areaRestMsg.append(",");
            }
            areaRestMsg.append(projectLandPartMapExtendDto.getLandPartName() + "建筑占地面积 已超过:"+Math.abs(Double.parseDouble(StringUtils.isBlank(subCover)?"0":subCover)));
        }

        String subTake = subToString(StringUtils.isBlank(projectLandPartMapExtendDto.getTakeByLandUseArea())?"0":projectLandPartMapExtendDto.getTakeByLandUseArea(),
                StringUtils.isBlank(subTotalAreaDto.getSumTakeArea())?"0":subTotalAreaDto.getSumTakeArea());
        subTake = sub(Double.parseDouble(StringUtils.isBlank(subTake)?"0":subTake), Double.parseDouble(StringUtils.isBlank(landAddSubProInfoBody.getLandBasicAreaInfoVo().getUseTakeMeasure())?"0": landAddSubProInfoBody.getLandBasicAreaInfoVo().getUseTakeMeasure()));
        if (Double.parseDouble(StringUtils.isBlank(subTake)?"0":subTake) < 0) {
            if(areaRestMsg != null && areaRestMsg.length() > 0){
                areaRestMsg.append(",");
            }
            areaRestMsg.append(projectLandPartMapExtendDto.getLandPartName() + "代征地用地面积 已超过:"+Math.abs(Double.parseDouble(StringUtils.isBlank(subTake)?"0":subTake)));
        }

        String subPrice = subToString(StringUtils.isBlank(projectLandPartMapExtendDto.getLandGetPrice())?"0":projectLandPartMapExtendDto.getLandGetPrice(),
                StringUtils.isBlank(subTotalAreaDto.getSumGetPrice())?"0":subTotalAreaDto.getSumGetPrice());
        subPrice = sub(Double.parseDouble(StringUtils.isBlank(subPrice)?"0":subPrice), Double.parseDouble(StringUtils.isBlank(landAddSubProInfoBody.getLandBasicAreaInfoVo().getGetLandPrice())?"0": landAddSubProInfoBody.getLandBasicAreaInfoVo().getGetLandPrice()));
        if (Double.parseDouble(StringUtils.isBlank(subPrice)?"0":subPrice) < 0) {
            if(areaRestMsg != null && areaRestMsg.length() > 0){
                areaRestMsg.append(",");
            }
            areaRestMsg.append(projectLandPartMapExtendDto.getLandPartName() + "土地获取价款 已超过:"+Math.abs(Double.parseDouble(StringUtils.isBlank(subPrice)?"0":subPrice)));
        }

        String subBuild = subToString(StringUtils.isBlank(projectLandPartMapExtendDto.getCapacityBuildingArea())?"0":projectLandPartMapExtendDto.getCapacityBuildingArea(),
                StringUtils.isBlank(subTotalAreaDto.getSumBuildArea())?"0":subTotalAreaDto.getSumBuildArea());
        subBuild = sub(Double.parseDouble(StringUtils.isBlank(subBuild)?"0":subBuild), Double.parseDouble(StringUtils.isBlank(landAddSubProInfoBody.getLandBasicAreaInfoVo().getMeterBuildMeasure())?"0": landAddSubProInfoBody.getLandBasicAreaInfoVo().getMeterBuildMeasure()));
        if (Double.parseDouble(StringUtils.isBlank(subBuild)?"0":subBuild) < 0) {
            if(areaRestMsg != null && areaRestMsg.length() > 0){
                areaRestMsg.append(",");
            }
            areaRestMsg.append(projectLandPartMapExtendDto.getLandPartName() + "计容建筑面积 已超过:"+Math.abs(Double.parseDouble(StringUtils.isBlank(subBuild)?"0":subBuild)));
        }

        return areaRestMsg;
    }

    @Override
    public JSONResult deleteLandInfoToSubProject(ProjectDeleteLandReqParam projectDeleteLandReqParam, CurrentUserVO currentUser, JSONResult jsonResult) throws Exception {

        ProjectInfoDto subProjectInfoDto = projectExtendMapper.selectSubProjectByVersionId(projectDeleteLandReqParam.getVersionId());
        String version = subProjectInfoDto.getVersion();
        if(StringUtils.isBlank(version)){
            jsonResult.setCode(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_CREATING_VERSION_EXCEPTION.getValue());
            return jsonResult;
        }

        Integer versionStatus = subProjectInfoDto.getVersionStatus();
        if(!versionStatus.equals(VersionStatusEnum.CREATING.getKey()) && !versionStatus.equals(VersionStatusEnum.REJECTED.getKey())){
            jsonResult.setCode(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getKey());
            jsonResult.setMsg(CodeEnum.SUB_PROJECT_EXTENDS_VERSION.getValue());
            return jsonResult;
        }

        List<String> projectOtherQuotaLandIdsList = projectQuotaExtendMapper.selectAdoptStageOtherBySubProId(subProjectInfoDto.getSubProjectId(), StageCodeEnum.STAGE_01.getKey());
        if(projectOtherQuotaLandIdsList != null && projectOtherQuotaLandIdsList.size() > 0){
            if(projectOtherQuotaLandIdsList.contains(projectDeleteLandReqParam.getLandId())){
                jsonResult.setCode(CodeEnum.PROJECT_LAND_DELETE_EXCEPTION.getKey());
                jsonResult.setMsg(CodeEnum.PROJECT_LAND_DELETE_EXCEPTION.getValue());
                return jsonResult;
            }
        }

        BoProjectLandPartMap boProjectLandPartMap = projectLandPartMapMapper.selectLandPartBySubExtIdAndLandId(projectDeleteLandReqParam.getVersionId(), projectDeleteLandReqParam.getLandId());
        if (boProjectLandPartMap != null) {

            List<String> productIds = new ArrayList<String>();
            List<BoProjectLandPartProductTypeMap> projectLandPartProductTypeMapList = new ArrayList<BoProjectLandPartProductTypeMap>();
            List<BoProjectLandPartProductTypeQuotaMap> projectLandPartProductTypeQuotaMapList = new ArrayList<BoProjectLandPartProductTypeQuotaMap>();

            QueryWrapper<BoProjectLandPartProductTypeMap> queryWrapper = new QueryWrapper<BoProjectLandPartProductTypeMap>();
            queryWrapper.eq("project_land_part_id", boProjectLandPartMap.getId())
                    .eq("is_delete", IsDeleteEnum.NO.getKey())
                    .eq("is_disable", IsDisableEnum.NO.getKey());
            List<BoProjectLandPartProductTypeMap> boProjectLandPartProductTypeMaps = projectLandPartProductTypeMapMapper.selectList(queryWrapper);
            if (boProjectLandPartProductTypeMaps != null && boProjectLandPartProductTypeMaps.size() > 0) {
                LocalDateTime now = LocalDateTime.now();
                for (int i = 0; i < boProjectLandPartProductTypeMaps.size(); i++) {
                    BoProjectLandPartProductTypeMap boProjectLandPartProductTypeMap = boProjectLandPartProductTypeMaps.get(i);
                    boProjectLandPartProductTypeMap.setIsDelete(IsDeleteEnum.YES.getKey());
                    boProjectLandPartProductTypeMap.setUpdateTime(now);
                    boProjectLandPartProductTypeMap.setUpdaterId(currentUser.getId());
                    boProjectLandPartProductTypeMap.setUpdaterName(currentUser.getName());
                    projectLandPartProductTypeMapList.add(boProjectLandPartProductTypeMap);
                    productIds.add(boProjectLandPartProductTypeMap.getId());
                }
            }

            if (productIds != null && productIds.size() > 0) {
                List<BoProjectLandPartProductTypeQuotaMap> boProjectLandPartProductTypeQuotaMaps = projectLandPartProductTypeQuotaMapMapper.selectByTypeQuoteIds(productIds);
                if (boProjectLandPartProductTypeQuotaMaps != null && boProjectLandPartProductTypeQuotaMaps.size() > 0) {
                    LocalDateTime now = LocalDateTime.now();
                    for (int i = 0; i < boProjectLandPartProductTypeQuotaMaps.size(); i++) {
                        BoProjectLandPartProductTypeQuotaMap boProjectLandPartProductTypeQuotaMap = boProjectLandPartProductTypeQuotaMaps.get(i);
                        boProjectLandPartProductTypeQuotaMap.setIsDelete(IsDeleteEnum.YES.getKey());
                        boProjectLandPartProductTypeQuotaMap.setUpdateTime(now);
                        boProjectLandPartProductTypeQuotaMap.setUpdaterId(currentUser.getId());
                        boProjectLandPartProductTypeQuotaMap.setUpdaterName(currentUser.getName());
                        projectLandPartProductTypeQuotaMapList.add(boProjectLandPartProductTypeQuotaMap);
                    }
                }
            }

            boProjectLandPartMap.setIsDelete(IsDeleteEnum.YES.getKey());
            boProjectLandPartMap.setUpdateTime(LocalDateTime.now());
            boProjectLandPartMap.setUpdaterId(currentUser.getId());
            boProjectLandPartMap.setUpdaterName(currentUser.getName());
            projectLandPartMapMapper.updateById(boProjectLandPartMap);
            if (projectLandPartProductTypeMapList != null && projectLandPartProductTypeMapList.size() > 0) {
                projectLandPartProductTypeMapMapper.updateIsDeleteBatch(projectLandPartProductTypeMapList);
            }
            if (projectLandPartProductTypeQuotaMapList != null && projectLandPartProductTypeQuotaMapList.size() > 0) {
                projectLandPartProductTypeQuotaMapMapper.updateIsDeleteBatch(projectLandPartProductTypeQuotaMapList);
            }
        }

        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
        jsonResult.setMsg(CodeEnum.SUCCESS.getValue());

        return jsonResult;
    }

}
