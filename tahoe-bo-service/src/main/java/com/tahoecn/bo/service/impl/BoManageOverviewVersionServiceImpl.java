package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.common.enums.ManageOverviewVersionTypeEnum;
import com.tahoecn.bo.model.entity.BoManageOverviewVersion;
import com.tahoecn.bo.mapper.BoManageOverviewVersionMapper;
import com.tahoecn.bo.service.BoManageOverviewVersionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * <p>
 * 经营概览数据统计版本表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-07-03
 */
@Service
public class BoManageOverviewVersionServiceImpl extends ServiceImpl<BoManageOverviewVersionMapper, BoManageOverviewVersion> implements BoManageOverviewVersionService {

    @Override
    public BoManageOverviewVersion getByEndDate(ManageOverviewVersionTypeEnum versionType,LocalDate localDate) {

        QueryWrapper<BoManageOverviewVersion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("version_date", localDate)
                .eq("version_type", versionType.getKey())
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .orderByDesc("create_time").last("limit 1");
        return getOne(queryWrapper);
    }
}
