package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.BoProjectLandPartMap;
import com.tahoecn.bo.model.vo.CurrentUserVO;
import com.tahoecn.bo.model.vo.LandAddSubProInfoBody;
import com.tahoecn.bo.model.vo.reqvo.*;
import com.tahoecn.core.json.JSONResult;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 项目分期地块数据表/映射表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoProjectLandPartMapService extends IService<BoProjectLandPartMap> {

    JSONResult selectLandInfoByProjectId(String projectId, String version, String isAdopt, JSONResult jsonResult) throws Exception;

    JSONResult selectLandInfoBySubProjectId(String subProjectId, String version, String isAdopt, JSONResult jsonResult) throws Exception;

    JSONResult selectCanUseLandInfoByProjectId(String projectId, String landName, Integer pageNo, Integer pageSize, JSONResult jsonResult) throws Exception;

    JSONResult selectCanUseLandBySubProject(String subProjectId, JSONResult jsonResult) throws Exception;

    JSONResult addLandToProject(ProjectAddLandReqParam projectAddLandReqParam, CurrentUserVO userVO, JSONResult jsonResult) throws Exception;

    JSONResult addLandToSubProject(SubProjectAddLandReqParam subProjectAddLandReqParam, CurrentUserVO userVO, JSONResult jsonResult) throws Exception;

    JSONResult deleteLandToProject(ProjectAddLandReqParam projectAddLandReqParam, CurrentUserVO userVO, JSONResult jsonResult) throws Exception;

    JSONResult deleteLandToSubProject(SubProjectDelLandReqParam subProjectDelLandReqParam, CurrentUserVO userVO, JSONResult jsonResult) throws Exception;

    JSONResult updateLandToSubProject(SubProjectAddLandReqParam subProjectAddLandReqParam, CurrentUserVO userVO, JSONResult jsonResult) throws Exception;

    JSONResult selectLandDetailsToSubProject(String subProjectId,String landId, JSONResult jsonResult) throws Exception;

    JSONResult selectCanUseLandDetailsToSubProject(String subProjectId, String landId, JSONResult jsonResult) throws Exception;

    /**
     * 通过项目/分期指标扩展表的ID，查询项目/分期最新版本的地块关系列表
     * @param projectQuotaExtendId
     * @return
     */
    List<BoProjectLandPartMap> getProjectLandPartListByProjectQuotaExtendId(String projectQuotaExtendId);
    /**
     * 查分期关联地块列表，通过分期版本ID，支持同时查多个分期
     * @return
     */
    List<BoProjectLandPartMap> getProjectLandPartListByProjectExtendIds(Collection<String> projectExtendIds);

    JSONResult getLandInfoByProject(String versionId, JSONResult jsonResult) throws Exception;

    JSONResult addLandInfoToProject(ReqParamProjectAddLandInfo projectAddLandInfo, CurrentUserVO currentUser, JSONResult jsonResult) throws Exception;

    JSONResult deleteLandInfoToProject(ProjectDeleteLandReqParam projectDeleteLandReqParam, CurrentUserVO currentUser, JSONResult jsonResult) throws Exception;

    JSONResult getLandInfoBySubPro(String versionId, JSONResult jsonResult) throws Exception;

    JSONResult getLandInfoDetailsToSubProject(String versionId, String landId, JSONResult jsonResult) throws Exception;

    JSONResult getLandInfoCanUseBySubProject(String versionId, JSONResult jsonResult) throws Exception;

    JSONResult getCanUseLandDetailsToSubProject(String subProjectId, String landId, JSONResult jsonResult) throws Exception;

    JSONResult addLandInfoToSubProject(LandAddSubProInfoBody landAddSubProInfoBody, CurrentUserVO currentUser, JSONResult jsonResult) throws Exception;

    JSONResult updateLandInfoToSubProject(LandAddSubProInfoBody landAddSubProInfoBody, CurrentUserVO currentUser, JSONResult jsonResult) throws Exception;

    JSONResult deleteLandInfoToSubProject(ProjectDeleteLandReqParam projectDeleteLandReqParam, CurrentUserVO currentUser, JSONResult jsonResult) throws Exception;
}
