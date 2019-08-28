package com.tahoecn.bo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tahoecn.bo.model.entity.UcOrg;

/**
 * <p>
 * 同步UC组织架构表                                          Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface UcOrgMapper extends BaseMapper<UcOrg> {

	/**
	 * @Title: deleteAll 
	 * @Description: TODO(这里用一句话描述这个方法的作用) void
	 * @author liyongxu
	 * @date 2019年5月27日 下午5:48:21 
	*/
	@Update("truncate table uc_org")
	void removeOrg();
	
	/**
	 * @Title: batchSaveOrgList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param ucOrgList
	 * @return Integer
	 * @author liyongxu
	 * @date 2019年5月27日 下午5:47:16 
	*/
	Integer batchSaveOrgList(List<UcOrg> ucOrgList);

	/**
	 * @Title: findByFdSid 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param fdSid
	 * @return Object
	 * @author liyongxu
	 * @date 2019年5月28日 下午2:17:39 
	*/
	UcOrg findByFdSid(@Param(value = "fdSid")String fdSid);

	/**
	 * @Title: selectOrgList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return List<UcOrg>
	 * @author liyongxu
	 * @date 2019年6月13日 上午11:43:12 
	*/
	List<UcOrg> selectOrgList(@Param(value = "fdSid")String fdSid);

	/**
	 * @Title: selectProjectSubInfo 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param projectId
	 * @return List<UcOrg>
	 * @author liyongxu
	 * @date 2019年6月13日 下午2:09:45 
	*/
	List<UcOrg> selectProjectSubInfo(@Param(value = "fdPsid")String projectId);
	
    List<UcOrg> getSubProjectDataInfo();
	
}
