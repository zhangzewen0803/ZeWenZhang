package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.common.enums.VersionStatusEnum;
import com.tahoecn.bo.common.utils.SqlUtils;
import com.tahoecn.bo.mapper.BoProjectExtendMapper;
import com.tahoecn.bo.model.entity.BoProjectExtend;
import com.tahoecn.bo.service.BoProjectExtendService;
import org.springframework.stereotype.Service;
import sun.misc.Version;

import java.util.List;

/**
 * <p>
 * 项目基础信息扩展表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class BoProjectExtendServiceImpl extends ServiceImpl<BoProjectExtendMapper, BoProjectExtend> implements BoProjectExtendService {

    @Override
    public BoProjectExtend getLastVersion(String projectId) {
        QueryWrapper<BoProjectExtend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .orderByDesc("create_time").last("limit 1");
        List<BoProjectExtend> boProjectExtends = baseMapper.selectList(queryWrapper);
        return boProjectExtends.isEmpty() ? null : boProjectExtends.get(0);
    }

    @Override
    public BoProjectExtend getLastPassedVersion(String projectId) {
        QueryWrapper<BoProjectExtend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId)
                .eq("version_status", VersionStatusEnum.PASSED.getKey())
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .orderByDesc("create_time").last("limit 1");
        List<BoProjectExtend> boProjectExtends = baseMapper.selectList(queryWrapper);
        return boProjectExtends.isEmpty() ? null : boProjectExtends.get(0);
    }

    @Override
    public List<BoProjectExtend> getProjectSubLastPassedVersionListByProjectId(String projectId) {
        QueryWrapper<BoProjectExtend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .eq("version_status", VersionStatusEnum.PASSED.getKey())
                .inSql("project_id", "select sid from mdm_project_info where parent_sid='" + SqlUtils.safeFilter(projectId) + "'")
                .exists("select 1 from bo_project_extend a where a.id=bo_project_extend.id and a.create_time = (select max(create_time) from bo_project_extend where a.project_id=project_id and is_delete=" + IsDeleteEnum.NO.getKey() + " and is_disable=" + IsDisableEnum.NO.getKey() + ")");
        return list(queryWrapper);
    }
}
