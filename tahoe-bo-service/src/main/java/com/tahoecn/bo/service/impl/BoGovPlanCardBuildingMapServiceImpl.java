package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.model.entity.BoGovPlanCardBuildingMap;
import com.tahoecn.bo.mapper.BoGovPlanCardBuildingMapMapper;
import com.tahoecn.bo.service.BoGovPlanCardBuildingMapService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 政府方案楼栋关系表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-06-04
 */
@Service
public class BoGovPlanCardBuildingMapServiceImpl extends ServiceImpl<BoGovPlanCardBuildingMapMapper, BoGovPlanCardBuildingMap> implements BoGovPlanCardBuildingMapService {

    @Override
    public List<BoGovPlanCardBuildingMap> getGovPlanCardBuildingList(List<String> govPlanCardIdList) {
        QueryWrapper<BoGovPlanCardBuildingMap> queryWrapper = new QueryWrapper();
        queryWrapper.in("gov_plan_card_id", govPlanCardIdList)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<BoGovPlanCardBuildingMap> getGovPlanCardBuildingList(String govPlanCardId) {
        QueryWrapper<BoGovPlanCardBuildingMap> queryWrapper = new QueryWrapper();
        queryWrapper.eq("gov_plan_card_id", govPlanCardId)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(queryWrapper);
    }
}
