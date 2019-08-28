package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.BoManageOverviewPrice;
import com.tahoecn.bo.model.entity.BoManageOverviewVersion;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 经营概览货值统计表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-07-02
 */
public interface BoManageOverviewPriceService extends IService<BoManageOverviewPrice> {

    /**
     * 制作统计数据
     *
     * @param localDate
     */
    List<BoManageOverviewPrice> makeData(LocalDate localDate);

    /**
     * 保存经营概览版本 以及 货值
     *
     * @param boManageOverviewVersion
     * @param boManageOverviewPrices
     */
    void saveManageOverviewVersionAndPrice(BoManageOverviewVersion boManageOverviewVersion, Collection<BoManageOverviewPrice> boManageOverviewPrices);


    /**
     * 根据截止时间  查货值数据
     *
     * @param endDate
     * @param parentOrgId
     * @return
     */
    List<BoManageOverviewPrice> getListByVersionIdAndUcOrgList(LocalDate endDate, String parentOrgId);

    /**
     * 根据版本ID 查货值
     * @param versionId
     * @param parentOrgId
     * @return
     */
    List<BoManageOverviewPrice> getListByVersionIdAndUcOrgList(String versionId, String parentOrgId);

}
