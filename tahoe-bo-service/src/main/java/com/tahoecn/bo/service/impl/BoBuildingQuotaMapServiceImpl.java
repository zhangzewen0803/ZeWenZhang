package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.model.entity.BoBuildingQuotaMap;
import com.tahoecn.bo.model.vo.CurrentUserVO;
import com.tahoecn.bo.mapper.BoBuildingQuotaMapMapper;
import com.tahoecn.bo.service.BoBuildingQuotaMapService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 楼栋指标数据表/映射表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class BoBuildingQuotaMapServiceImpl extends ServiceImpl<BoBuildingQuotaMapMapper, BoBuildingQuotaMap> implements BoBuildingQuotaMapService {
	
	@Autowired
	private BoBuildingQuotaMapMapper boBuildingQuotaMapMapper;
	
    @Override
    public List<BoBuildingQuotaMap> getBuildingQuotaList(List<String> buildingIdList) {
        QueryWrapper<BoBuildingQuotaMap> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("building_id", buildingIdList)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<BoBuildingQuotaMap> getBuildingQuotaList(String projectQuotaExtendId) {
        QueryWrapper<BoBuildingQuotaMap> queryWrapper = new QueryWrapper<>();
        queryWrapper.inSql("building_id", "select id from bo_building where project_quota_extend_id='" + projectQuotaExtendId + "'")
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<BoBuildingQuotaMap> getDeletedBuildingQuotaList(String projectQuotaExtendId) {
        QueryWrapper<BoBuildingQuotaMap> queryWrapper = new QueryWrapper<>();
        queryWrapper.inSql("building_id", "select id from bo_building where project_quota_extend_id='" + projectQuotaExtendId + "'")
                .eq("is_delete", IsDeleteEnum.YES.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(queryWrapper);
    }

	@Override
	public void deleteBuildingQuotaByBulId(String buildingId, CurrentUserVO currentUserVO) {
		boBuildingQuotaMapMapper.deleteBuildingQuotaByBulId(buildingId,currentUserVO.getId(),currentUserVO.getName());
	}
}
