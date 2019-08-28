package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.model.entity.BoProjectLandPartProductTypeQuotaMap;
import com.tahoecn.bo.mapper.BoProjectLandPartProductTypeQuotaMapMapper;
import com.tahoecn.bo.service.BoProjectLandPartProductTypeQuotaMapService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 项目分期地块业态指标数据表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-06-11
 */
@Service
public class BoProjectLandPartProductTypeQuotaMapServiceImpl extends ServiceImpl<BoProjectLandPartProductTypeQuotaMapMapper, BoProjectLandPartProductTypeQuotaMap> implements BoProjectLandPartProductTypeQuotaMapService {

    @Override
    public List<BoProjectLandPartProductTypeQuotaMap> getListByProjectLandPartProductTypeIds(Collection<String> projectLandPartProductTypeIds) {
        QueryWrapper<BoProjectLandPartProductTypeQuotaMap> quotaMapQueryWrapper = new QueryWrapper<>();
        quotaMapQueryWrapper.in("project_land_part_product_type_id",projectLandPartProductTypeIds)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return list(quotaMapQueryWrapper);
    }
}
