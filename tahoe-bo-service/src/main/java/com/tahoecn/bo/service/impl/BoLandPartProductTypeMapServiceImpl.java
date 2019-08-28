package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.mapper.BoLandPartProductTypeMapMapper;
import com.tahoecn.bo.model.entity.BoLandPartProductTypeMap;
import com.tahoecn.bo.model.entity.BoLandPartProductTypeQuotaMap;
import com.tahoecn.bo.service.BoLandPartProductTypeMapService;
import com.tahoecn.bo.service.BoLandPartProductTypeQuotaMapService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 地块业态关系表/映射表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class BoLandPartProductTypeMapServiceImpl extends ServiceImpl<BoLandPartProductTypeMapMapper, BoLandPartProductTypeMap> implements BoLandPartProductTypeMapService {

    @Autowired
    private BoLandPartProductTypeQuotaMapService boLandPartProductTypeQuotaMapService;

    @Override
    public List<BoLandPartProductTypeMap> getLandPartProductTypeList(String projectQuotaExtendId) {
        QueryWrapper<BoLandPartProductTypeMap> boLandPartProductTypeMapQueryWrapper = new QueryWrapper<>();
        boLandPartProductTypeMapQueryWrapper.eq("project_quota_extend_id", projectQuotaExtendId)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(boLandPartProductTypeMapQueryWrapper);

    }

    @Override
    public List<BoLandPartProductTypeMap> getLandPartProductTypeListByPriceVersionId(String projectPriceExtendId) {
        QueryWrapper<BoLandPartProductTypeMap> boLandPartProductTypeMapQueryWrapper = new QueryWrapper<>();
        boLandPartProductTypeMapQueryWrapper.inSql("project_quota_extend_id", "select project_quota_extend_id from bo_project_price_extend where id='" + projectPriceExtendId.replace("'", "") + "'")
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(boLandPartProductTypeMapQueryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateOrRemoveLandPartProductType(List<BoLandPartProductTypeMap> insertList, List<BoLandPartProductTypeQuotaMap> insertQuotaList, List<BoLandPartProductTypeMap> deleteList, List<BoLandPartProductTypeQuotaMap> deleteQuotaList, List<BoLandPartProductTypeMap> updateList, List<BoLandPartProductTypeQuotaMap> updateQuotaList) {
        if (CollectionUtils.isNotEmpty(insertList)) {
            saveBatch(insertList,100);
        }
        if (CollectionUtils.isNotEmpty(insertQuotaList)) {
            boLandPartProductTypeQuotaMapService.saveBatch(insertQuotaList, 100);
        }
        if (CollectionUtils.isNotEmpty(deleteList)) {
            updateBatchById(deleteList,100);
        }
        if (CollectionUtils.isNotEmpty(deleteQuotaList)) {
            boLandPartProductTypeQuotaMapService.updateBatchById(deleteQuotaList, 100);
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            updateBatchById(updateList,100);
        }
        if (CollectionUtils.isNotEmpty(updateQuotaList)) {
            boLandPartProductTypeQuotaMapService.updateBatchById(updateQuotaList, 100);
        }
        return true;
    }
}
