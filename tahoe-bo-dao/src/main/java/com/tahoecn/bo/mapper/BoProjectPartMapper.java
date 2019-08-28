package com.tahoecn.bo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tahoecn.bo.model.entity.BoProjectPart;
import com.tahoecn.bo.model.vo.PartitionInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 分期分区表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-06-01
 */
public interface BoProjectPartMapper extends BaseMapper<BoProjectPart> {

    void insertBatch(@Param("list") List<BoProjectPart> projectPartList);

    List<PartitionInfoVo> selectByExtendId(@Param("extendId") String extendId, @Param("noDel") int noDel, @Param("noDis") int noDis);
}
