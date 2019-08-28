package com.tahoecn.bo.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.utils.Constants;
import com.tahoecn.bo.common.utils.ListUtils;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.mapper.UcUserMapper;
import com.tahoecn.bo.model.entity.UcUser;
import com.tahoecn.bo.service.UcUserService;
import com.tahoecn.uc.UcClient;
import com.tahoecn.uc.exception.UcException;
import com.tahoecn.uc.request.UcV1UserInfoRequest;
import com.tahoecn.uc.response.UcV1UserInfoResponse;
import com.tahoecn.uc.vo.UcV1UserInfoResultVO;
import com.tahoecn.uc.vo.UcV3UserListResultVO;

/**
 * @ClassName：UcUserServiceImpl
 * @Description：同步UC用户表
 * @author liyongxu 
 * @date 2019年5月27日 下午3:24:41 
 * @version 1.0.0 
 */
@Service
public class UcUserServiceImpl extends ServiceImpl<UcUserMapper, UcUser> implements UcUserService {

	@Autowired
	private UcUserMapper ucUserMapper;
	
	//用户数据拆分入库数量
	private final Integer UC_USER_SIZE = 3000;
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional(rollbackFor = Exception.class)
	public void batchSaveUserList(List<UcV3UserListResultVO> allUserList) {
		List<UcUser> ucUserList = new ArrayList<UcUser>();
		for (UcV3UserListResultVO ucUserListResultVO : allUserList) {
			UcUser user = new UcUser();
			user.setId(UUIDUtils.create());
			user.setFdSid(ucUserListResultVO.getFdSid());
			user.setFdName(ucUserListResultVO.getFdName());
			user.setFdUsername(ucUserListResultVO.getFdUsername());
			user.setFdOrder(ucUserListResultVO.getFdOrder());
			user.setFdTel(ucUserListResultVO.getFdTel());
			user.setFdWorkPhone(ucUserListResultVO.getFdWorkPhone());
			user.setFdEmail(ucUserListResultVO.getFdEmail());
			user.setFdAvailable(ucUserListResultVO.getFdAvailable());
			user.setFdOrgIdTree(ucUserListResultVO.getFdOrgIdTree());
			user.setFdOrgNameTree(ucUserListResultVO.getFdOrgNameTree());
			user.setFdOrgId(ucUserListResultVO.getFdOrgId());
			user.setFdOrgName(ucUserListResultVO.getFdOrgName());
			user.setFdIsdelete(ucUserListResultVO.getFdIsdelete());
			user.setFdGender(ucUserListResultVO.getFdGender());
			user.setFdTahoeMessageSid(ucUserListResultVO.getFdTahoeMessageSid());
			user.setFdProvinceCode(ucUserListResultVO.getFd_province_code());
			user.setFdProvinceName(ucUserListResultVO.getFd_province_name());
			user.setFdCityCode(ucUserListResultVO.getFd_city_code());
			user.setFdCityName(ucUserListResultVO.getFd_city_name());
			user.setCreateTime(LocalDateTime.now());
			
			ucUserList.add(user);
		}
		//将用户数据集合拆分成3000长的的集合
        List<List> subList = ListUtils.getSubList(ucUserList, UC_USER_SIZE);
        for (List list : subList) {
        	 List<UcUser> lists =(List<UcUser>)list;
        	Integer num = ucUserMapper.batchSaveUserList(lists);
    		System.out.println(num);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeUser() {
		ucUserMapper.removeUser();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveOrUpdateUcUser(UcUser ucUser) {
		UcUser userTset = ucUserMapper.findByFdSid(ucUser.getFdSid());
		if(userTset == null) {
			ucUser.setId(UUIDUtils.create());
			ucUserMapper.insert(ucUser);
		}else {
			ucUser.setId(userTset.getId());
			ucUserMapper.updateById(ucUser);
		}
	}

	@Override
	@Transactional
	public void deleteByFdSid(String fdSid) {
		UcUser userTset = ucUserMapper.findByFdSid(fdSid);
		if(userTset != null) {
			userTset.setFdAvailable(Constants.DELETE);
			userTset.setFdIsdelete(Constants.NOAVAILABLE);
			ucUserMapper.updateById(userTset);
		}
	}

	@Override
	public UcV1UserInfoResultVO getUsreInfo(String userName) {
		UcV1UserInfoRequest ucV1UserInfoRequest = new UcV1UserInfoRequest();
		ucV1UserInfoRequest.setUserName(userName);
		 UcV1UserInfoResultVO result = new UcV1UserInfoResultVO();
		try {
		    UcV1UserInfoResponse ucV1UserInfoResponse = UcClient.v1UserInfo(ucV1UserInfoRequest);
		    result = ucV1UserInfoResponse.getResult();
		} catch (UcException e) {
		    //请求失败（网络异常或UC返回code不为0），捕获异常信息。
		    String message = e.getMessage();
		    System.out.println(message);
		}
		return result;
	}
	
}
