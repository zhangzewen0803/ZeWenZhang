package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.model.entity.BoProjectPriceQuotaMap;
import com.tahoecn.bo.mapper.BoProjectPriceQuotaMapMapper;
import com.tahoecn.bo.service.BoProjectPriceQuotaMapService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 项目分期价格指标数据表/映射表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class BoProjectPriceQuotaMapServiceImpl extends ServiceImpl<BoProjectPriceQuotaMapMapper, BoProjectPriceQuotaMap> implements BoProjectPriceQuotaMapService {

    @Override
    public List<BoProjectPriceQuotaMap> getProjectPriceQuotaList(String projectPriceExtendId) {
        QueryWrapper<BoProjectPriceQuotaMap> quotaMapQueryWrapper = new QueryWrapper<>();
        quotaMapQueryWrapper.eq("project_price_extend_id", projectPriceExtendId)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(quotaMapQueryWrapper);
    }
}
