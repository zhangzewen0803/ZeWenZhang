package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.common.utils.VOUtils;
import com.tahoecn.bo.mapper.BoQuotaGroupMapMapper;
import com.tahoecn.bo.model.entity.BoQuotaGroupMap;
import com.tahoecn.bo.model.vo.QuotaHeadVO;
import com.tahoecn.bo.service.BoQuotaGroupMapService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 指标分组关系表/映射表，用于为分组批量分配指标数据 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class BoQuotaGroupMapServiceImpl extends ServiceImpl<BoQuotaGroupMapMapper, BoQuotaGroupMap> implements BoQuotaGroupMapService {


    @Override
    @Cacheable(value = "BoQuotaGroupMapServiceImpl",key = "'getQuotaGroupMapList:'+#groupCode")
    public List<BoQuotaGroupMap> getQuotaGroupMapList(String... groupCode) {
        QueryWrapper<BoQuotaGroupMap> quotaGroupMapQueryWrapper = new QueryWrapper<>();
        quotaGroupMapQueryWrapper.in("group_code", groupCode)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .orderByAsc("group_code,sort_no");
        return baseMapper.selectList(quotaGroupMapQueryWrapper);
    }

    @Override
    @Cacheable(value = "BoQuotaGroupMapServiceImpl",key = "'getQuotaGroupMapListReturnQuotaHeadVO:'+#groupCode")
    public List<QuotaHeadVO> getQuotaGroupMapListReturnQuotaHeadVO(String... groupCode) {
        List<BoQuotaGroupMap> quotaGroupMapList = getQuotaGroupMapList(groupCode);
        return VOUtils.makeQuotaHeadVOList(quotaGroupMapList);
    }

}
