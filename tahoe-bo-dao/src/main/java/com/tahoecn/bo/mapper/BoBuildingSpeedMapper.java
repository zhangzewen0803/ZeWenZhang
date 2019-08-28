package com.tahoecn.bo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tahoecn.bo.model.dto.BoBuildingSpeedExtendDto;
import com.tahoecn.bo.model.entity.BoBuildingSpeed;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 楼栋信息填报表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-06-28
 */
public interface BoBuildingSpeedMapper extends BaseMapper<BoBuildingSpeed> {

    List<BoBuildingSpeedExtendDto> selectInfoBySpeedTimeAndProjectId(@Param("projectId") String projectId, @Param("speedTime") String speedTime);

    void deleteInfoByProjectIdsAndTime(@Param("projectId") String projectId,@Param("speedTime") String speedTime,@Param("list") List<String> projectSpeedIdList,@Param("isDel") int isDel);

    void updateSpeedId(@Param("updateBuildingSpeed") BoBuildingSpeed updateBuildingSpeed);
}
