package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.mapper.BoGovPlanCardMapper;
import com.tahoecn.bo.model.entity.BoGovPlanCard;
import com.tahoecn.bo.model.entity.BoGovPlanCardBuildingMap;
import com.tahoecn.bo.model.vo.CurrentUserVO;
import com.tahoecn.bo.service.BoGovPlanCardBuildingMapService;
import com.tahoecn.bo.service.BoGovPlanCardService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 政府方案信息表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-06-04
 */
@Service
public class BoGovPlanCardServiceImpl extends ServiceImpl<BoGovPlanCardMapper, BoGovPlanCard> implements BoGovPlanCardService {

    @Autowired
    private BoGovPlanCardBuildingMapService boGovPlanCardBuildingMapService;

    @Override
    public List<BoGovPlanCard> getGovPlanCardList(String projectQuotaExtendId) {
        QueryWrapper<BoGovPlanCard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_quota_extend_id", projectQuotaExtendId)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .orderByDesc("reply_time");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addGovPlanCardAndBuilding(BoGovPlanCard boGovPlanCard, List<BoGovPlanCardBuildingMap> boGovPlanCardBuildingMapList) {
        save(boGovPlanCard);
        boGovPlanCardBuildingMapService.saveBatch(boGovPlanCardBuildingMapList,100);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateGovPlanCardAndBuilding(BoGovPlanCard boGovPlanCard, List<BoGovPlanCardBuildingMap> deleteList, List<BoGovPlanCardBuildingMap> insertList) {
        updateById(boGovPlanCard);
        if (CollectionUtils.isNotEmpty(deleteList)) {
            boGovPlanCardBuildingMapService.updateBatchById(deleteList,100);
        }
        if (CollectionUtils.isNotEmpty(insertList)) {
            boGovPlanCardBuildingMapService.saveBatch(insertList,100);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeGovPlanCard(String[] ids, CurrentUserVO currentUser) {
        BoGovPlanCard example = new BoGovPlanCard();
        example.setIsDelete(IsDeleteEnum.YES.getKey());
        example.setUpdaterId(currentUser.getId());
        example.setUpdaterName(currentUser.getName());
        example.setUpdateTime(LocalDateTime.now());
        QueryWrapper<BoGovPlanCard> exampleWrapper = new QueryWrapper();
        exampleWrapper.in("id", ids);
        update(example, exampleWrapper);

        BoGovPlanCardBuildingMap govPlanCardBuildingExample = new BoGovPlanCardBuildingMap();
        govPlanCardBuildingExample.setIsDelete(IsDeleteEnum.YES.getKey());
        govPlanCardBuildingExample.setUpdaterId(currentUser.getId());
        govPlanCardBuildingExample.setUpdaterName(currentUser.getName());
        govPlanCardBuildingExample.setUpdateTime(LocalDateTime.now());

        QueryWrapper<BoGovPlanCardBuildingMap> govPlanCardBuildingExampleWrapper = new QueryWrapper();
        govPlanCardBuildingExampleWrapper.in("gov_plan_card_id", ids);
        boGovPlanCardBuildingMapService.update(govPlanCardBuildingExample, govPlanCardBuildingExampleWrapper);
        return true;
    }

    @Override
    public boolean existsPlanCode(String projectQuotaExtendId, String planCode) {
        QueryWrapper<BoGovPlanCard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_quota_extend_id", projectQuotaExtendId)
                .eq("plan_code", planCode)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectCount(queryWrapper) > 0;
    }
}
