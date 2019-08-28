package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.model.entity.BoBuilding;
import com.tahoecn.bo.model.vo.CurrentUserVO;
import com.tahoecn.bo.mapper.BoBuildingMapper;
import com.tahoecn.bo.service.BoBuildingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 楼栋信息表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class BoBuildingServiceImpl extends ServiceImpl<BoBuildingMapper, BoBuilding> implements BoBuildingService {

	@Autowired
	private BoBuildingMapper boBuildingMapper;
	
    @Override
    public List<BoBuilding> getBuildingList(String projectQuotaExtendId) {
        QueryWrapper<BoBuilding> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_quota_extend_id", projectQuotaExtendId)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<BoBuilding> getBuildingList(Collection<String> ids) {
        QueryWrapper<BoBuilding> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<BoBuilding> getAllBuildingList(String projectQuotaExtendId, Collection<String> names) {
        QueryWrapper<BoBuilding> queryWrapper = new QueryWrapper<>();
        queryWrapper.exists("select id from bo_project_quota_extend where project_id=(select project_id from bo_project_quota_extend where id='" + projectQuotaExtendId.replace("'", "") + "') and id=bo_building.project_quota_extend_id")
                .in("name", names);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public Map<String, BoBuilding> getAllBuildingNameMap(String projectQuotaExtendId, Collection<String> names) {
        List<BoBuilding> deletedBuildingList = getAllBuildingList(projectQuotaExtendId, names);
        if (deletedBuildingList.isEmpty()){
            return new HashMap<>(0);
        }
        Map<String,BoBuilding> map = new HashMap<>();
        deletedBuildingList.stream().forEach(x->map.put(x.getName(),x));
        return map;
    }

	@Override
	public void deleteBuildingByBulId(String buildingId, CurrentUserVO currentUserVO) {
		BoBuilding boBuilding = boBuildingMapper.selectById(buildingId);
		boBuilding.setIsDelete(IsDeleteEnum.YES.getKey());
		boBuilding.setUpdaterId(currentUserVO.getId());
		boBuilding.setUpdaterName(currentUserVO.getName());
		boBuilding.setUpdateTime(LocalDateTime.now());
		boBuildingMapper.updateById(boBuilding);
	}
}
