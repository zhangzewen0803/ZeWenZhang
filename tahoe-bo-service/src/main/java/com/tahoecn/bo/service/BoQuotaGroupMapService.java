package com.tahoecn.bo.service;

import com.tahoecn.bo.model.entity.BoQuotaGroupMap;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.vo.QuotaHeadVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 指标分组关系表/映射表，用于为分组批量分配指标数据 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoQuotaGroupMapService extends IService<BoQuotaGroupMap> {

    /**
     *
     *
     * @param groupCode 分组CODE，支持多个，返回并集
     * @return
     */
    List<BoQuotaGroupMap> getQuotaGroupMapList(String... groupCode);

    /**
     * 查询指标分组关系列表 返回 QuotaHeadVO
     * @param groupCode
     * @return
     */
    List<QuotaHeadVO> getQuotaGroupMapListReturnQuotaHeadVO(String... groupCode);

}
