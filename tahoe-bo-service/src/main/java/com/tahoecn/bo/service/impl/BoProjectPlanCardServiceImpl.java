package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.mapper.BoProjectPlanCardMapper;
import com.tahoecn.bo.model.entity.BoProjectPlanCard;
import com.tahoecn.bo.model.entity.BoProjectPlanCardBuildingMap;
import com.tahoecn.bo.model.vo.CurrentUserVO;
import com.tahoecn.bo.service.BoProjectPlanCardBuildingMapService;
import com.tahoecn.bo.service.BoProjectPlanCardService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 工规证表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-06-04
 */
@Service
public class BoProjectPlanCardServiceImpl extends ServiceImpl<BoProjectPlanCardMapper, BoProjectPlanCard> implements BoProjectPlanCardService {

    @Autowired
    private BoProjectPlanCardBuildingMapService boProjectPlanCardBuildingMapService;

    @Override
    public List<BoProjectPlanCard> getProjectPlanCardList(String projectQuotaExtendId) {
        QueryWrapper<BoProjectPlanCard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_quota_extend_id", projectQuotaExtendId)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .orderByDesc("reply_time");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addProjectPlanCard(BoProjectPlanCard boProjectPlanCard, List<BoProjectPlanCardBuildingMap> boProjectPlanCardBuildingMapList) {
        save(boProjectPlanCard);
        boProjectPlanCardBuildingMapService.saveBatch(boProjectPlanCardBuildingMapList,100);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProjectPlanCardAndBuilding(BoProjectPlanCard boProjectPlanCard, List<BoProjectPlanCardBuildingMap> deleteList, List<BoProjectPlanCardBuildingMap> insertList) {
        updateById(boProjectPlanCard);
        if (CollectionUtils.isNotEmpty(deleteList)) {
            boProjectPlanCardBuildingMapService.updateBatchById(deleteList,100);
        }
        if (CollectionUtils.isNotEmpty(insertList)) {
            boProjectPlanCardBuildingMapService.saveBatch(insertList,100);
        }
        return true;
    }

    @Override
    public boolean removeProjectPlanCard(String[] ids, CurrentUserVO currentUser) {
        BoProjectPlanCard example = new BoProjectPlanCard();
        example.setIsDelete(IsDeleteEnum.YES.getKey());
        example.setUpdaterId(currentUser.getId());
        example.setUpdaterName(currentUser.getName());
        example.setUpdateTime(LocalDateTime.now());
        QueryWrapper<BoProjectPlanCard> exampleWrapper = new QueryWrapper();
        exampleWrapper.in("id", ids);
        update(example, exampleWrapper);

        BoProjectPlanCardBuildingMap govProjectCardBuildingExample = new BoProjectPlanCardBuildingMap();
        govProjectCardBuildingExample.setIsDelete(IsDeleteEnum.YES.getKey());
        govProjectCardBuildingExample.setUpdaterId(currentUser.getId());
        govProjectCardBuildingExample.setUpdaterName(currentUser.getName());
        govProjectCardBuildingExample.setUpdateTime(LocalDateTime.now());

        QueryWrapper<BoProjectPlanCardBuildingMap> boProjectPlanCardBuildingMapWrapper = new QueryWrapper();
        boProjectPlanCardBuildingMapWrapper.in("project_plan_card_id", ids);
        boProjectPlanCardBuildingMapService.update(govProjectCardBuildingExample, boProjectPlanCardBuildingMapWrapper);
        return true;
    }

    @Override
    public boolean existsPlanCode(String projectQuotaExtendId, String planCode) {
        QueryWrapper<BoProjectPlanCard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_quota_extend_id", projectQuotaExtendId)
                .eq("plan_code", planCode)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectCount(queryWrapper) > 0;
    }
}
