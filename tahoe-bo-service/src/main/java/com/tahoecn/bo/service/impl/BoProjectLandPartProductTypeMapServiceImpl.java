package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.common.utils.SqlUtils;
import com.tahoecn.bo.model.entity.BoProjectLandPartProductTypeMap;
import com.tahoecn.bo.mapper.BoProjectLandPartProductTypeMapMapper;
import com.tahoecn.bo.service.BoProjectLandPartProductTypeMapService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 项目分期地块业态关系表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-06-11
 */
@Service
public class BoProjectLandPartProductTypeMapServiceImpl extends ServiceImpl<BoProjectLandPartProductTypeMapMapper, BoProjectLandPartProductTypeMap> implements BoProjectLandPartProductTypeMapService {

    @Override
    public List<BoProjectLandPartProductTypeMap> getProjectLandPartProductTypeListByProjectExtendIds(Collection<String> projectExtendIds) {
        String ids = projectExtendIds.stream().map(x -> "'" + SqlUtils.safeFilter(x) + "'").reduce((a, b) -> a + "," + b).get();
        QueryWrapper<BoProjectLandPartProductTypeMap> queryWrapper = new QueryWrapper<>();
        queryWrapper.inSql("project_land_part_id", "select id from bo_project_land_part_map where project_extend_id in (" + ids + ")")
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return list(queryWrapper);
    }
}
