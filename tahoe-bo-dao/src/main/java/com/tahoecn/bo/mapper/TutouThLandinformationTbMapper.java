package com.tahoecn.bo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tahoecn.bo.model.dto.LandInfoDto;
import com.tahoecn.bo.model.entity.TutouThLandinformationTb;

/**
 * <p>
 * TUTOU-土地信息表-主表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public interface TutouThLandinformationTbMapper extends BaseMapper<TutouThLandinformationTb> {

    LandInfoDto selectByLandId(@Param("landId") String landId);

	/**
	 * @Title: selectLandinfoList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return List<TutouThLandinformationTb>
	 * @author liyongxu
	 * @date 2019年6月5日 下午8:18:25 
	*/
	List<TutouThLandinformationTb> selectLandinfoList();
	
	List<TutouThLandinformationTb> selectLandinfoListDS();

	/**
	 * @Title: batchInsert 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param tTLandinfoList void
	 * @author liyongxu
	 * @date 2019年6月5日 下午8:49:49 
	*/
	void batchInsertLandinfoList(List<TutouThLandinformationTb> tTLandinfoList);
	
	/**
	 * @Title: selectInfoByLandId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param landId
	 * @return TutouThLandinformationTb
	 * @author liyongxu
	 * @date 2019年6月12日 上午11:31:17 
	*/
	TutouThLandinformationTb selectInfoByLandId(@Param("landId") Integer landId);

	/**
	 * @Title: batchUpateLandinfoList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param tTLandinfoUpdateList void
	 * @author liyongxu
	 * @date 2019年6月11日 下午9:09:10 
	*/
	void batchUpateLandinfoList(List<TutouThLandinformationTb> tTLandinfoUpdateList);

}
