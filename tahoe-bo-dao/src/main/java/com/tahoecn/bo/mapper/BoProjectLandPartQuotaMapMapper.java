package com.tahoecn.bo.mapper;

import com.tahoecn.bo.model.dto.ProjectProductTypeQuotaDto;
import com.tahoecn.bo.model.entity.BoProjectLandPartQuotaMap;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 项目分期地块指标信息表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-06-03
 */
public interface BoProjectLandPartQuotaMapMapper extends BaseMapper<BoProjectLandPartQuotaMap> {

    List<ProjectProductTypeQuotaDto> selectSubProjectQuotaValuesByProjectId(@Param("proId") String proId);
}
