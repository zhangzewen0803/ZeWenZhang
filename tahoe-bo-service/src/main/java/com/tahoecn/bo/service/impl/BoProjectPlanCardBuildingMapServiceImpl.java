package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.mapper.BoProjectPlanCardBuildingMapMapper;
import com.tahoecn.bo.model.entity.BoProjectPlanCardBuildingMap;
import com.tahoecn.bo.service.BoProjectPlanCardBuildingMapService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 工规证楼栋关系表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-06-04
 */
@Service
public class BoProjectPlanCardBuildingMapServiceImpl extends ServiceImpl<BoProjectPlanCardBuildingMapMapper, BoProjectPlanCardBuildingMap> implements BoProjectPlanCardBuildingMapService {

    @Override
    public List<BoProjectPlanCardBuildingMap> getBoProjectPlanCardBuildingList(List<String> projectPlanCardIdList) {
        QueryWrapper<BoProjectPlanCardBuildingMap> queryWrapper = new QueryWrapper();
        queryWrapper.in("project_plan_card_id", projectPlanCardIdList)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<BoProjectPlanCardBuildingMap> getBoProjectPlanCardBuildingList(String projectPlanCardId) {
        QueryWrapper<BoProjectPlanCardBuildingMap> queryWrapper = new QueryWrapper();
        queryWrapper.in("project_plan_card_id", projectPlanCardId)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(queryWrapper);
    }
}
