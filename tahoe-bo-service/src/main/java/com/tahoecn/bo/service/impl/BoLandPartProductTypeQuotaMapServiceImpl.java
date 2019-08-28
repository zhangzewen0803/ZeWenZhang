package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.mapper.BoLandPartProductTypeQuotaMapMapper;
import com.tahoecn.bo.model.entity.BoLandPartProductTypeQuotaMap;
import com.tahoecn.bo.service.BoLandPartProductTypeQuotaMapService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 地块业态指标数据表/映射表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class BoLandPartProductTypeQuotaMapServiceImpl extends ServiceImpl<BoLandPartProductTypeQuotaMapMapper, BoLandPartProductTypeQuotaMap> implements BoLandPartProductTypeQuotaMapService {

    @Override
    public List<BoLandPartProductTypeQuotaMap> getLandPartProductTypeQuotaList(Collection<String> landPartProductTypeIdList) {
        QueryWrapper<BoLandPartProductTypeQuotaMap> boLandPartProductTypeQuotaMapQueryWrapper = new QueryWrapper<>();
        boLandPartProductTypeQuotaMapQueryWrapper.in("land_part_product_type_id", landPartProductTypeIdList)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(boLandPartProductTypeQuotaMapQueryWrapper);

    }

    @Override
    public List<BoLandPartProductTypeQuotaMap> getLandPartProductTypeQuotaList(String projectQuotaExtendId) {
        QueryWrapper<BoLandPartProductTypeQuotaMap> boLandPartProductTypeQuotaMapQueryWrapper = new QueryWrapper<>();
        boLandPartProductTypeQuotaMapQueryWrapper.inSql("land_part_product_type_id", "(select id from bo_land_part_product_type_map where project_quota_extend_id='" + projectQuotaExtendId.replace("'", "") + "')")
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(boLandPartProductTypeQuotaMapQueryWrapper);
    }

    @Override
    public List<BoLandPartProductTypeQuotaMap> getLandPartProductTypeQuotaListByPriceVersionId(String priceVersionId) {
        QueryWrapper<BoLandPartProductTypeQuotaMap> boLandPartProductTypeQuotaMapQueryWrapper = new QueryWrapper<>();
        boLandPartProductTypeQuotaMapQueryWrapper.inSql("land_part_product_type_id", "select id from bo_land_part_product_type_map where project_quota_extend_id in (select project_quota_extend_id from bo_project_price_extend where id = '" + priceVersionId.replace("'", "") + "')")
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(boLandPartProductTypeQuotaMapQueryWrapper);
    }
}
