package com.tahoecn.bo.service;

import com.tahoecn.bo.model.entity.BoProjectLandPartProductTypeMap;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 项目分期地块业态关系表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-06-11
 */
public interface BoProjectLandPartProductTypeMapService extends IService<BoProjectLandPartProductTypeMap> {

    /**
     * 查地块业态关系列表，通过分期版本ID，支持同时查多个分期
     * @param projectExtendIds
     * @return
     */
    List<BoProjectLandPartProductTypeMap> getProjectLandPartProductTypeListByProjectExtendIds(Collection<String> projectExtendIds);

}
