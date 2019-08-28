package com.tahoecn.bo.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.common.enums.VersionStatusEnum;
import com.tahoecn.bo.mapper.BoProjectSupplyPlanVersionMapper;
import com.tahoecn.bo.model.entity.BoProjectSupplyPlanVersion;
import com.tahoecn.bo.service.BoProjectSupplyPlanVersionService;

/**
 * <p>
 * 项目分期供货计划版本表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-08-08
 */
@Service
public class BoProjectSupplyPlanVersionServiceImpl extends ServiceImpl<BoProjectSupplyPlanVersionMapper, BoProjectSupplyPlanVersion> implements BoProjectSupplyPlanVersionService {

	@Override
	public BoProjectSupplyPlanVersion createVersion(String projectId, String stageCode, String planType) {
		return null;
	}

	@Override
	public boolean hasCanEditData(String projectId, String stageCode) {
		 QueryWrapper<BoProjectSupplyPlanVersion> queryWrapper = new QueryWrapper<>();
	        queryWrapper.eq("project_id", projectId)
	                .eq("stage_code", stageCode)
	                .eq("is_delete", IsDeleteEnum.NO.getKey())
	                .eq("is_disable", IsDisableEnum.NO.getKey())
	                .in("version_status", VersionStatusEnum.CHECKING.getKey(),
	                        VersionStatusEnum.CREATING.getKey(),
	                        VersionStatusEnum.REJECTED.getKey());
        return baseMapper.selectCount(queryWrapper) > 0;
	}

}
