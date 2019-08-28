package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.BoManageOverviewArea;
import com.tahoecn.bo.model.entity.BoManageOverviewVersion;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 经营概览面积统计表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-07-18
 */
public interface BoManageOverviewAreaService extends IService<BoManageOverviewArea> {

    /**
     * 制作统计数据
     *
     * @param localDate
     */
    List<BoManageOverviewArea> makeData(LocalDate localDate);


    /**
     * 根据版本ID查询列表
     * @param versionId
     * @param parentOrgId
     * @return
     */
    List<BoManageOverviewArea> getListByVersionIdAndUcOrgList(String versionId, String parentOrgId);

    /**
     * 保存经营概览版本 以及 面积
     *
     * @param boManageOverviewVersion
     * @param boManageOverviewAreas
     */
    void saveManageOverviewVersionAndArea(BoManageOverviewVersion boManageOverviewVersion, Collection<BoManageOverviewArea> boManageOverviewAreas);


}
