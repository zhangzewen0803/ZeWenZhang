package com.tahoecn.bo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tahoecn.bo.model.entity.UcUser;

/**
 * <p>
 * 同步UC用户表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface UcUserMapper extends BaseMapper<UcUser> {

	/**
	 * @Title: batchSaveUserList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param ucUserList
	 * @return Integer
	 * @author liyongxu
	 * @date 2019年5月27日 下午5:53:35 
	*/
	Integer batchSaveUserList(List<UcUser> ucUserList);

	/**
	 * @Title: removeUser 
	 * @Description: TODO(这里用一句话描述这个方法的作用) void
	 * @author liyongxu
	 * @date 2019年5月27日 下午5:53:33 
	*/
	@Update("truncate table uc_user")
	void removeUser();

	/**
	 * @Title: findByFdSid 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param fdSid
	 * @return UcUser
	 * @author liyongxu
	 * @date 2019年5月28日 下午2:41:30 
	*/
	UcUser findByFdSid(@Param(value = "fdSid")String fdSid);

}
