package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.common.enums.QuotaCodeEnum;
import com.tahoecn.bo.mapper.BoBuildingProductTypeMapMapper;
import com.tahoecn.bo.model.dto.BoPushBuildingDto;
import com.tahoecn.bo.model.dto.BoPushProductDto;
import com.tahoecn.bo.model.dto.BoPushProjectSubDto;
import com.tahoecn.bo.model.entity.*;
import com.tahoecn.bo.model.vo.CurrentUserVO;
import com.tahoecn.bo.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 楼栋业态关系表/映射表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class BoBuildingProductTypeMapServiceImpl extends ServiceImpl<BoBuildingProductTypeMapMapper, BoBuildingProductTypeMap> implements BoBuildingProductTypeMapService {

    @Autowired
    private BoProjectQuotaExtendService boProjectQuotaExtendService;

    @Autowired
    private BoBuildingService boBuildingService;

    @Autowired
    private BoBuildingQuotaMapService boBuildingQuotaMapService;

    @Autowired
    private BoBuildingProductTypeQuotaMapService boBuildingProductTypeQuotaMapService;

    @Autowired
    private MdmProjectInfoService mdmProjectInfoService;

    @Autowired
    private BoProjectPlanCardBuildingMapService boProjectPlanCardBuildingMapService;

    @Autowired
    private BoGovPlanCardBuildingMapService boGovPlanCardBuildingMapService;

	@Autowired
	private BoBuildingProductTypeMapMapper boBuildingProductTypeMapMapper;
	
    @Override
    public List<BoBuildingProductTypeMap> getBuildingProductTypeList(String projectQuotaExtendId) {
        QueryWrapper<BoBuildingProductTypeMap> boBuildingProductTypeMapQueryWrapper = new QueryWrapper<>();
        boBuildingProductTypeMapQueryWrapper.eq("project_quota_extend_id", projectQuotaExtendId)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(boBuildingProductTypeMapQueryWrapper);
    }

    @Override
    public List<BoBuildingProductTypeMap> getBuildingProductTypeListByBuildingIdList(Collection<String> buildingIdList) {
        QueryWrapper<BoBuildingProductTypeMap> boBuildingProductTypeMapQueryWrapper = new QueryWrapper<>();
        boBuildingProductTypeMapQueryWrapper.in("building_id", buildingIdList)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return list(boBuildingProductTypeMapQueryWrapper);
    }

    @Override
    public List<BoBuildingProductTypeMap> getBuildingProductTypeListByPriceVersionId(String projectPriceExtendId) {
        QueryWrapper<BoBuildingProductTypeMap> boBuildingProductTypeMapQueryWrapper = new QueryWrapper<>();
        boBuildingProductTypeMapQueryWrapper.exists("select project_quota_extend_id from bo_project_price_extend where bo_building_product_type_map.project_quota_extend_id=project_quota_extend_id and id='" + projectPriceExtendId.replace("'", "") + "'")
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(boBuildingProductTypeMapQueryWrapper);
    }

    @Override
    public List<BoBuildingProductTypeMap> getDeletedBuildingProductTypeList(String projectQuotaExtendId) {
        QueryWrapper<BoBuildingProductTypeMap> boBuildingProductTypeMapQueryWrapper = new QueryWrapper<>();
        boBuildingProductTypeMapQueryWrapper.exists("select id from bo_project_quota_extend where project_id=(select project_id from bo_project_quota_extend where id='" + projectQuotaExtendId.replace("'", "") + "') and id=bo_building_product_type_map.project_quota_extend_id")
                .eq("is_delete", IsDeleteEnum.YES.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(boBuildingProductTypeMapQueryWrapper);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrDeleteOrUpdateBuildingProductType(Map<String, Object> servMap) {
        List<BoBuilding> insertBuildingList = (List<BoBuilding>) servMap.get("insertBuildingList");
//        List<BoBuilding> updateBuildingList = (List<BoBuilding>) servMap.get("updateBuildingList");
        List<BoBuilding> deleteBuildingList = (List<BoBuilding>) servMap.get("deleteBuildingList");
        if (CollectionUtils.isNotEmpty(insertBuildingList)) {
            boBuildingService.saveBatch(insertBuildingList, 100);
        }
//        if (CollectionUtils.isNotEmpty(updateBuildingList)) {
//            boBuildingService.updateBatchById(updateBuildingList,100);
//        }
        if (CollectionUtils.isNotEmpty(deleteBuildingList)) {
            //同步删除有关的工规证、政府方案
            //工规证
            QueryWrapper<BoProjectPlanCardBuildingMap> projectPlanCardBuildingMapWrapper = new QueryWrapper<>();
            BoBuilding boBuilding = deleteBuildingList.get(0);
            BoProjectPlanCardBuildingMap boProjectPlanCardBuildingMap = new BoProjectPlanCardBuildingMap();
            boProjectPlanCardBuildingMap.setIsDelete(IsDeleteEnum.YES.getKey());
            boProjectPlanCardBuildingMap.setUpdaterName(boBuilding.getUpdaterName());
            boProjectPlanCardBuildingMap.setUpdateTime(boBuilding.getUpdateTime());
            boProjectPlanCardBuildingMap.setUpdaterId(boBuilding.getUpdaterId());
            projectPlanCardBuildingMapWrapper
                    .in("building_id", deleteBuildingList.stream().map(x -> x.getId()).collect(Collectors.toList()));
            boProjectPlanCardBuildingMapService.update(boProjectPlanCardBuildingMap, projectPlanCardBuildingMapWrapper);
            //政府方案
            QueryWrapper<BoGovPlanCardBuildingMap> govPlanCardBuildingMapQueryWrapper = new QueryWrapper<>();
            BoGovPlanCardBuildingMap boGovPlanCardBuildingMap = new BoGovPlanCardBuildingMap();
            boGovPlanCardBuildingMap.setIsDelete(IsDeleteEnum.YES.getKey());
            boGovPlanCardBuildingMap.setUpdaterName(boBuilding.getUpdaterName());
            boGovPlanCardBuildingMap.setUpdateTime(boBuilding.getUpdateTime());
            boGovPlanCardBuildingMap.setUpdaterId(boBuilding.getUpdaterId());
            govPlanCardBuildingMapQueryWrapper
                    .in("building_id", deleteBuildingList.stream().map(x -> x.getId()).collect(Collectors.toList()));
            boGovPlanCardBuildingMapService.update(boGovPlanCardBuildingMap, govPlanCardBuildingMapQueryWrapper);
            boBuildingService.updateBatchById(deleteBuildingList, 100);

            //删除楼栋业态
            QueryWrapper<BoBuildingQuotaMap> boBuildingQuotaMapQueryWrapper = new QueryWrapper<>();
            boBuildingQuotaMapQueryWrapper.in("building_id", deleteBuildingList.stream().map(x -> x.getId()).collect(Collectors.toSet()));
            BoBuildingQuotaMap boBuildingQuotaMap = new BoBuildingQuotaMap();
            boBuildingQuotaMap.setIsDelete(IsDeleteEnum.YES.getKey());
            boBuildingQuotaMap.setUpdaterName(boBuilding.getUpdaterName());
            boBuildingQuotaMap.setUpdateTime(boBuilding.getUpdateTime());
            boBuildingQuotaMap.setUpdaterId(boBuilding.getUpdaterId());
            boBuildingQuotaMapService.update(boBuildingQuotaMap, boBuildingQuotaMapQueryWrapper);

        }

        List<BoBuildingQuotaMap> insertBuildingQuotaList = (List<BoBuildingQuotaMap>) servMap.get("insertBuildingQuotaList");
        List<BoBuildingQuotaMap> updateBuildingQuotaList = (List<BoBuildingQuotaMap>) servMap.get("updateBuildingQuotaList");
//        List<BoBuildingQuotaMap> deleteBuildingQuotaList = (List<BoBuildingQuotaMap>) servMap.get("deleteBuildingQuotaList");
        if (CollectionUtils.isNotEmpty(insertBuildingQuotaList)) {
            boBuildingQuotaMapService.saveBatch(insertBuildingQuotaList, 100);
        }
        if (CollectionUtils.isNotEmpty(updateBuildingQuotaList)) {
            boBuildingQuotaMapService.updateBatchById(updateBuildingQuotaList, 100);
        }

        List<BoBuildingProductTypeMap> insertBuildingProductTypeList = (List<BoBuildingProductTypeMap>) servMap.get("insertBuildingProductTypeList");
//        List<BoBuildingProductTypeMap> updateBuildingProductTypeList = (List<BoBuildingProductTypeMap>) servMap.get("updateBuildingProductTypeList");
        List<BoBuildingProductTypeMap> deleteBuildingProductTypeList = (List<BoBuildingProductTypeMap>) servMap.get("deleteBuildingProductTypeList");
        if (CollectionUtils.isNotEmpty(insertBuildingProductTypeList)) {
            saveBatch(insertBuildingProductTypeList,100);
        }
//        if (CollectionUtils.isNotEmpty(updateBuildingProductTypeList)) {
//            updateBatchById(updateBuildingProductTypeList);
//        }
        if (CollectionUtils.isNotEmpty(deleteBuildingProductTypeList)) {
            updateBatchById(deleteBuildingProductTypeList,100);

            BoBuildingProductTypeMap boBuildingProductTypeMap = deleteBuildingProductTypeList.get(0);

            //删除楼栋业态指标
            QueryWrapper<BoBuildingProductTypeQuotaMap> boBuildingProductTypeMapQueryWrapper = new QueryWrapper<>();
            boBuildingProductTypeMapQueryWrapper.in("building_product_type_id", deleteBuildingProductTypeList.stream().map(x -> x.getId()).collect(Collectors.toSet()));
            BoBuildingProductTypeQuotaMap boBuildingProductTypeQuotaMap = new BoBuildingProductTypeQuotaMap();
            boBuildingProductTypeQuotaMap.setIsDelete(IsDeleteEnum.YES.getKey());
            boBuildingProductTypeQuotaMap.setUpdaterName(boBuildingProductTypeMap.getUpdaterName());
            boBuildingProductTypeQuotaMap.setUpdateTime(boBuildingProductTypeMap.getUpdateTime());
            boBuildingProductTypeQuotaMap.setUpdaterId(boBuildingProductTypeMap.getUpdaterId());
            boBuildingProductTypeQuotaMapService.update(boBuildingProductTypeQuotaMap, boBuildingProductTypeMapQueryWrapper);
        }

        List<BoBuildingProductTypeQuotaMap> insertBuildingProductTypeQuotaList = (List<BoBuildingProductTypeQuotaMap>) servMap.get("insertBuildingProductTypeQuotaList");
        List<BoBuildingProductTypeQuotaMap> updateBuildingProductTypeQuotaList = (List<BoBuildingProductTypeQuotaMap>) servMap.get("updateBuildingProductTypeQuotaList");
//        List<BoBuildingProductTypeQuotaMap> deleteBuildingProductTypeQuotaList = (List<BoBuildingProductTypeQuotaMap>) servMap.get("deleteBuildingProductTypeQuotaList");
        if (CollectionUtils.isNotEmpty(insertBuildingProductTypeQuotaList)) {
            boBuildingProductTypeQuotaMapService.saveBatch(insertBuildingProductTypeQuotaList, 100);
        }
        if (CollectionUtils.isNotEmpty(updateBuildingProductTypeQuotaList)) {
            boBuildingProductTypeQuotaMapService.updateBatchById(updateBuildingProductTypeQuotaList, 100);
        }
//        if (CollectionUtils.isNotEmpty(deleteBuildingProductTypeQuotaList)) {
//            boBuildingProductTypeQuotaMapService.updateBatchById(deleteBuildingProductTypeQuotaList, 100);
//        }

        return true;
    }

    @Override
    public BoPushProjectSubDto getBuildingProductInfoListByApproveId(String approveId) {
        if (StringUtils.isBlank(approveId)) {
            return null;
        }
        //查对应版本
        QueryWrapper<BoProjectQuotaExtend> queryWrapper = new QueryWrapper<BoProjectQuotaExtend>();
        queryWrapper.eq("approve_id", approveId).last("limit 1");
        BoProjectQuotaExtend boProjectQuotaExtend = boProjectQuotaExtendService.getOne(queryWrapper);
        if (boProjectQuotaExtend == null) {
            return null;
        }
        return getBuildingProductInfoListByBoProjectQuotaExtendObj(boProjectQuotaExtend);
    }

    @Override
    public BoPushProjectSubDto getBuildingProductInfoListByVersionId(String versionId) {
        if (StringUtils.isBlank(versionId)) {
            return null;
        }
        //查对应版本
        QueryWrapper<BoProjectQuotaExtend> queryWrapper = new QueryWrapper<BoProjectQuotaExtend>();
        queryWrapper.eq("id", versionId).last("limit 1");
        BoProjectQuotaExtend boProjectQuotaExtend = boProjectQuotaExtendService.getOne(queryWrapper);
        if (boProjectQuotaExtend == null) {
            return null;
        }
        return getBuildingProductInfoListByBoProjectQuotaExtendObj(boProjectQuotaExtend);


    }

    private BoPushProjectSubDto getBuildingProductInfoListByBoProjectQuotaExtendObj(BoProjectQuotaExtend boProjectQuotaExtend) {
        //查分期信息
        MdmProjectInfo mdmProjectInfo = mdmProjectInfoService.getById(boProjectQuotaExtend.getProjectId());
        if (mdmProjectInfo == null) {
            return null;
        }

        BoPushProjectSubDto boProjectSubDto = new BoPushProjectSubDto();
        boProjectSubDto.setProjectSubId(mdmProjectInfo.getSid());
        boProjectSubDto.setBuildingList(new ArrayList<>());

        //查楼栋、楼栋业态、楼栋业态指标信息
        List<BoBuildingProductTypeMap> buildingProductTypeList = getBuildingProductTypeList(boProjectQuotaExtend.getId());
        if (CollectionUtils.isEmpty(buildingProductTypeList)) {
            return boProjectSubDto;
        }

        //查楼栋业态指标，判断可售面积是否大于0，大于0的推送
        List<BoBuildingProductTypeQuotaMap> buildingProductTypeQuotaList = boBuildingProductTypeQuotaMapService.getBuildingProductTypeQuotaList(buildingProductTypeList.stream().map(x -> x.getId()).collect(Collectors.toList()));
        List<BoBuildingProductTypeMap> filteredBuildingProductTypeList = buildingProductTypeList.parallelStream().filter(x -> {
            Optional<BigDecimal> reduce = buildingProductTypeQuotaList
                    .parallelStream()
                    .filter(y->x.getId().equals(y.getBuildingProductTypeId()))
                    .filter(y -> QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA.getKey().equals(y.getQuotaCode()) || QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA.getKey().equals(y.getQuotaCode()))
                    .map(y -> new BigDecimal(StringUtils.isBlank(y.getQuotaValue()) ? "0" : y.getQuotaValue()))
                    .reduce(BigDecimal::add);
            if (reduce.isPresent() && reduce.get().doubleValue() > 0) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filteredBuildingProductTypeList)) {
            return boProjectSubDto;
        }

        //查楼栋信息,楼栋业态关联信息，building_origin_id字段冗余有时为空，暂未找到原因，先不取
        List<BoBuilding> buildingList = boBuildingService.getBuildingList(boProjectQuotaExtend.getId());
        Map<String, BoBuilding> buildingIdMap = buildingList.parallelStream().collect(Collectors.toMap(BoBuilding::getId, x -> x, (o, n) -> n));

        //转换对象
        Map<String, BoPushBuildingDto> buildGroupMap = new HashMap<>();
        Set<String> buildProductTypeGroupSet = new HashSet<>();
        filteredBuildingProductTypeList.stream().forEach(x -> {
            BoBuilding boBuilding = buildingIdMap.get(x.getBuildingId());
            if (boBuilding == null){
                return;
            }
            if (!buildGroupMap.containsKey(boBuilding.getOriginId())) {
                BoPushBuildingDto boBuildingDto = new BoPushBuildingDto();
                boBuildingDto.setBuildingId(boBuilding.getOriginId());
                boBuildingDto.setBuildingName(x.getBuildingName());
                boBuildingDto.setProductInfo(new ArrayList<>());
                buildGroupMap.put(boBuilding.getOriginId(), boBuildingDto);
            }
            String buildingProductType = boBuilding.getOriginId() + x.getProductTypeCode();
            if (!buildProductTypeGroupSet.contains(buildingProductType)) {
                buildProductTypeGroupSet.add(buildingProductType);
                BoPushBuildingDto boBuildingDto = buildGroupMap.get(boBuilding.getOriginId());
                BoPushProductDto boProductDto = new BoPushProductDto();
                boProductDto.setProductTypeCode(x.getProductTypeCode());
                boProductDto.setProductTypeName(x.getProductTypeName());
                boBuildingDto.getProductInfo().add(boProductDto);
            }

        });
        buildGroupMap.forEach((k, v) -> {
            boProjectSubDto.getBuildingList().add(v);
        });
        return boProjectSubDto;
    }

    @Override
	public List<BoBuildingProductTypeMap> getBuildingProductTypeListByBulId(String buildingId) {
		QueryWrapper<BoBuildingProductTypeMap> boBuildingProductTypeMapQueryWrapper = new QueryWrapper<>();
		boBuildingProductTypeMapQueryWrapper.in("building_id", buildingId)
            .eq("is_delete", IsDeleteEnum.NO.getKey())
            .eq("is_disable", IsDisableEnum.NO.getKey());
		return list(boBuildingProductTypeMapQueryWrapper);
	}

	@Override
	public List<BoBuildingProductTypeMap> getBuildingProductTypeListByProductId(String productId) {
		QueryWrapper<BoBuildingProductTypeMap> boBuildingProductTypeMapQueryWrapper = new QueryWrapper<>();
		boBuildingProductTypeMapQueryWrapper.in("id", productId)
            .eq("is_delete", IsDeleteEnum.NO.getKey())
            .eq("is_disable", IsDisableEnum.NO.getKey());
		return list(boBuildingProductTypeMapQueryWrapper);
	}

	@Override
	public void deleteBuildingProductTypeByBulId(String buildingId, CurrentUserVO currentUserVO) {
		boBuildingProductTypeMapMapper.deleteBuildingProductTypeByBulId(buildingId,currentUserVO.getId(),currentUserVO.getName());
	}

	@Override
	public void deleteBuildingProductTypeByProductId(String productId, CurrentUserVO currentUserVO) {
		boBuildingProductTypeMapMapper.deleteBuildingProductTypeByProductId(productId,currentUserVO.getId(),currentUserVO.getName());
	}

}
