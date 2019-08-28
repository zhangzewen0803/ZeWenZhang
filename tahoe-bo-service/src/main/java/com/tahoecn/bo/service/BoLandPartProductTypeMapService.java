package com.tahoecn.bo.service;

import com.tahoecn.bo.model.entity.BoLandPartProductTypeMap;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.BoLandPartProductTypeQuotaMap;

import java.util.List;

/**
 * <p>
 * 地块业态关系表/映射表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoLandPartProductTypeMapService extends IService<BoLandPartProductTypeMap> {

    /**
     * 查询地块业态关系列表
     *
     * @param projectQuotaExtendId
     * @return
     */
    List<BoLandPartProductTypeMap> getLandPartProductTypeList(String projectQuotaExtendId);

    /**
     * 查询地块业态关系列表
     *
     * @param projectPriceExtendId
     * @return
     */
    List<BoLandPartProductTypeMap> getLandPartProductTypeListByPriceVersionId(String projectPriceExtendId);

    /**
     * 对关系和指标数据进行增删改
     * @param insertList 需要插入的地块业态关系数据
     * @param insertQuotaList 需要插入的地块业态指标数据
     * @param deleteList 需要删除的地块业态关系数据
     * @param deleteQuotaList 需要删除的地块业态指标数据
     * @param updateList 需要更新的地块业态关系数据
     * @param updateQuotaList 需要删除的地块业态关系数据
     * @return
     */
    boolean saveOrUpdateOrRemoveLandPartProductType(List<BoLandPartProductTypeMap> insertList,
                                                    List<BoLandPartProductTypeQuotaMap> insertQuotaList,
                                                    List<BoLandPartProductTypeMap> deleteList,
                                                    List<BoLandPartProductTypeQuotaMap> deleteQuotaList,
                                                    List<BoLandPartProductTypeMap> updateList,
                                                    List<BoLandPartProductTypeQuotaMap> updateQuotaList);

}
