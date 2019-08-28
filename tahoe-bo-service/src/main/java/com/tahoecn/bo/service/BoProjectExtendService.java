package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.BoProjectExtend;

import java.util.List;

/**
 * <p>
 * 项目基础信息扩展表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoProjectExtendService extends IService<BoProjectExtend> {

    /**
     * 查最新版本项目/分期扩展信息
     * @param projectId 项目/分期ID
     * @return
     */
    BoProjectExtend getLastVersion(String projectId);

    /**
     * 查询最新已审批通过版本项目/分期信息
     * @param projectId
     * @return
     */
    BoProjectExtend getLastPassedVersion(String projectId);

    /**
     * 通过项目ID查所有分期最新的审批通过版本
     * @param projectId
     * @return
     */
    List<BoProjectExtend> getProjectSubLastPassedVersionListByProjectId(String projectId);

}
