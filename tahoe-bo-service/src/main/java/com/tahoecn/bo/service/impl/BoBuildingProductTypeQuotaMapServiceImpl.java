package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.mapper.BoBuildingProductTypeQuotaMapMapper;
import com.tahoecn.bo.model.entity.BoBuildingProductTypeQuotaMap;
import com.tahoecn.bo.model.vo.CurrentUserVO;
import com.tahoecn.bo.service.BoBuildingProductTypeQuotaMapService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 楼栋业态指标数据表/映射表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class BoBuildingProductTypeQuotaMapServiceImpl extends ServiceImpl<BoBuildingProductTypeQuotaMapMapper, BoBuildingProductTypeQuotaMap> implements BoBuildingProductTypeQuotaMapService {

	@Autowired
	private BoBuildingProductTypeQuotaMapMapper boBuildingProductTypeQuotaMapMapper;
	
    @Override
    public List<BoBuildingProductTypeQuotaMap> getBuildingProductTypeQuotaList(List<String> buildingProductTypeIdList) {
        QueryWrapper<BoBuildingProductTypeQuotaMap> boBuildingProductTypeQuotaMapQueryWrapper = new QueryWrapper<>();
        boBuildingProductTypeQuotaMapQueryWrapper.in("building_product_type_id", buildingProductTypeIdList)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(boBuildingProductTypeQuotaMapQueryWrapper);
    }

    @Override
    public List<BoBuildingProductTypeQuotaMap> getBuildingProductTypeQuotaList(String projectQuotaExtendId) {
        QueryWrapper<BoBuildingProductTypeQuotaMap> boBuildingProductTypeQuotaMapQueryWrapper = new QueryWrapper<>();
        boBuildingProductTypeQuotaMapQueryWrapper.inSql("building_product_type_id", "select id from bo_building_product_type_map where project_quota_extend_id = '" + projectQuotaExtendId.replace("'", "") + "' and is_delete=" + IsDeleteEnum.NO.getKey() + " and is_disable=" + IsDisableEnum.NO.getKey() + "")
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(boBuildingProductTypeQuotaMapQueryWrapper);
    }

    @Override
    public List<BoBuildingProductTypeQuotaMap> getDeletedBuildingProductTypeQuotaList(List<String> buildingProductTypeIdList) {
        QueryWrapper<BoBuildingProductTypeQuotaMap> boBuildingProductTypeQuotaMapQueryWrapper = new QueryWrapper<>();
        boBuildingProductTypeQuotaMapQueryWrapper.in("building_product_type_id", buildingProductTypeIdList)
                .eq("is_delete", IsDeleteEnum.YES.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(boBuildingProductTypeQuotaMapQueryWrapper);
    }

	@Override
	public void deletedBuildingProductTypeQuotaByBulId(String buildingId, CurrentUserVO currentUserVO) {
		boBuildingProductTypeQuotaMapMapper.deletedBuildingProductTypeQuotaByBulId(buildingId,currentUserVO.getId(),currentUserVO.getName());
	}

	@Override
	public void deletedBuildingProductTypeQuotaByProductTypeId(String productTypeId, CurrentUserVO currentUserVO) {
		boBuildingProductTypeQuotaMapMapper.deletedBuildingProductTypeQuotaByProductTypeId(productTypeId,currentUserVO.getId(),currentUserVO.getName());
	}
}
