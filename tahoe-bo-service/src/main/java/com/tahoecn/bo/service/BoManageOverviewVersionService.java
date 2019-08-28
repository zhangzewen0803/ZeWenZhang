package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.common.enums.ManageOverviewVersionTypeEnum;
import com.tahoecn.bo.model.entity.BoManageOverviewVersion;

import java.time.LocalDate;

/**
 * <p>
 * 经营概览数据统计版本表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-07-03
 */
public interface BoManageOverviewVersionService extends IService<BoManageOverviewVersion> {

    /**
     * 根据截至时间查询对应的版本
     * @param versionType
     * @param localDate
     * @return
     */
    BoManageOverviewVersion getByEndDate(ManageOverviewVersionTypeEnum versionType,LocalDate localDate);



}
