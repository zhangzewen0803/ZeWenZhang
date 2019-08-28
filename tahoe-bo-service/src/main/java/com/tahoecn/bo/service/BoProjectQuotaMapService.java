package com.tahoecn.bo.service;

import com.tahoecn.bo.model.entity.BoProjectQuotaMap;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 项目分期指标数据表/映射表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoProjectQuotaMapService extends IService<BoProjectQuotaMap> {

    /**
     * 查规划指标
     *
     * @param projectQuotaExtendId
     * @return
     */
    List<BoProjectQuotaMap> getProjectQuotaMapList(String projectQuotaExtendId);

    /**
     * 查项目或者分期规划指标，计算项目、分期等汇总的指标
     *
     * @param projectQuotaExtendId 项目或分期版本ID
     * @return
     */
    List<BoProjectQuotaMap> getProjectQuotaMapListWithCalcSum(String projectQuotaExtendId);

    /**
     * 查项目规划指标并计算汇总值
     *
     * @param projectQuotaExtendId
     * @return
     */
    List<BoProjectQuotaMap> getProjectQuotaMapListWithCalcSumProject(String projectQuotaExtendId);

    /**
     * 查询分期规划指标并计算汇总值
     *
     * @param projectQuotaExtendId
     * @return
     */
    List<BoProjectQuotaMap> getProjectQuotaMapListWithCalcSumProjectSub(String projectQuotaExtendId);

    /**
     * 通知 计算项目/分期规划指标 任务
     * @param projectQuotaExtendId
     */
    void noticeCalcProjectQuota(String... projectQuotaExtendId);


    /**
     * 处理项目/分期规划指标汇总任务
     */
    void processCalcProjectQuota();


}
