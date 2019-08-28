package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.bo.BpmBusinessInfoBo;
import com.tahoecn.bo.model.entity.BoProjectQuotaExtend;
import com.tahoecn.bo.model.vo.CurrentUserVO;
import com.tahoecn.core.json.JSONResult;

/**
 * <p>
 * 项目分期扩展信息表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoProjectQuotaExtendService extends IService<BoProjectQuotaExtend> {

    /**
     * 查最新审批通过版本
     *
     * @param projectId 项目/分期ID
     * @param stageCode 阶段CODE
     * @return 最新版本，没有则返回null
     */
    BoProjectQuotaExtend getLastPassedVersion(String projectId, String... stageCode);

    /**
     * 查最新版本
     *
     * @param projectId 项目/分期ID
     * @param stageCode 阶段CODE
     * @return 最新版本，没有则返回null
     */
    BoProjectQuotaExtend getLastVersion(String projectId, String stageCode);


    /**
     * 是否满足创建版本条件
     *
     * @param projectSubId 分期ID
     * @param stageCode    阶段CODE
     * @return 满足返回true，否则false
     */
    boolean hasCanEditData(String projectSubId, String stageCode);


    /**
     * 创建版本
     *
     * @param projectSubId
     * @param stageCode
     * @param currentUserVO
     * @return
     */
    BoProjectQuotaExtend createVersion(String projectSubId, String stageCode, CurrentUserVO currentUserVO);

    /**
     * 清空审批相关数据，将审批状态改为编制中
     *
     * @param boProjectQuotaExtend
     * @return
     */
    @Deprecated
    boolean updateForClearApproveData(BoProjectQuotaExtend boProjectQuotaExtend);

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
    JSONResult areaValidateApprove(String versionId);

	/**
	 * @Title: getAreaVersionInfos 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param projectPriceExtendId
	 * @return BoProjectQuotaExtend
	 * @author liyongxu
	 * @date 2019年8月12日 上午10:43:54 
	*/
	BoProjectQuotaExtend getAreaVersionInfos(String projectPriceExtendId);

}
