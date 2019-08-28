package com.tahoecn.bo.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.UcUser;
import com.tahoecn.uc.vo.UcV1UserInfoResultVO;
import com.tahoecn.uc.vo.UcV3UserListResultVO;

/**
 * <p>
 * 同步UC用户表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface UcUserService extends IService<UcUser> {

    /**
     * @param allUserList void
     * @Title: batchSaveUserList
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author liyongxu
     * @date 2019年5月27日 上午11:35:56
     */
    void batchSaveUserList(List<UcV3UserListResultVO> allUserList);

    /**
     * @Title: removeUser 
     * @Description: 删除人员数据
     * @author liyongxu
     * @date 2019年5月27日 下午5:52:35 
    */
    void removeUser();

	/**
	 * @Title: saveOrUpdate 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param ucUserData void
	 * @author liyongxu
	 * @date 2019年5月28日 下午2:33:25 
	*/
	void saveOrUpdateUcUser(UcUser ucUser);

	/**
	 * @Title: deleteByFdSid 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param dataSid void
	 * @author liyongxu
	 * @date 2019年5月28日 下午2:39:40 
	*/
	void deleteByFdSid(String fdSid);

	/**
	 * @Title: getUsreInfo 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param userName void
	 * @author liyongxu
	 * @return 
	 * @date 2019年6月3日 下午4:51:15 
	*/
	UcV1UserInfoResultVO getUsreInfo(String userName);
}
