package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.bo.BpmBusinessInfoBo;
import com.tahoecn.bo.model.entity.BoProjectPriceExtend;
import com.tahoecn.bo.model.vo.CurrentUserVO;
import com.tahoecn.core.json.JSONResult;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * <p>
 * 项目分期价格扩展信息表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoProjectPriceExtendService extends IService<BoProjectPriceExtend> {

    /**
     * 查询最后一个版本
     *
     * @param projectId
     * @param stageCode
     * @return
     */
    BoProjectPriceExtend getLastVersion(String projectId, String stageCode);

    /**
     * 查询最后一个版本
     *
     * @param projectId
     * @param stageCode
     * @return
     */
    BoProjectPriceExtend getLastPassedVersion(String projectId, String stageCode);

    /**
     * 查询最后一个版本
     *
     * @param projectId
     * @param stageCode
     * @return
     */
    BoProjectPriceExtend getLastPassedVersion(String projectId, Collection<String> stageCode, LocalDateTime endTime);

    /**
     * 是否存在可编辑数据
     *
     * @param projectId
     * @param stageCode
     * @return
     */
    boolean hasCanEditData(String projectId, String stageCode);

    /**
     * 创建新版本
     *
     * @param projectId
     * @param projectQuotaExtendId
     * @param stageCode
     * @param user
     * @return
     */
    BoProjectPriceExtend createVersion(String projectId, String projectQuotaExtendId, String stageCode, CurrentUserVO user);

    /**
     * 清理审批数组，并重置审批状态为编制中
     * @param boProjectPriceExtend
     * @return
     */
    @Deprecated
    boolean updateForClearApproveData(BoProjectPriceExtend boProjectPriceExtend);

    /**
     * 查询预审批信息，包括主题、版本说明、
     * @param versionId
     * @return
     */
    BpmBusinessInfoBo getPreApproveInfo(String versionId);

    /**
     * 判断版本是否允许发起流程
     * @param versionId
     * @return
     */
    JSONResult priceValidateApprove(String versionId);
}
