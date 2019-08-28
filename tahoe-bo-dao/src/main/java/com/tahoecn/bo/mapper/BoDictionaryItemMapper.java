package com.tahoecn.bo.mapper;

import com.tahoecn.bo.model.entity.BoDictionaryItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 字典表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoDictionaryItemMapper extends BaseMapper<BoDictionaryItem> {

    List<BoDictionaryItem> selectByTypeIds(@Param("list") List<String> typeIds);
}
