package com.tahoecn.bo.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.enums.PowerSpotEnum;
import com.tahoecn.bo.mapper.BoSysPermissionMapper;
import com.tahoecn.bo.model.entity.BoSysPermission;
import com.tahoecn.bo.model.vo.DataPrivDetailVO;
import com.tahoecn.bo.model.vo.DataPrivVO;
import com.tahoecn.bo.service.BoSysPermissionService;
import com.tahoecn.uc.UcClient;
import com.tahoecn.uc.exception.UcException;
import com.tahoecn.uc.request.UcV1SysPrivListRequest;
import com.tahoecn.uc.request.UcV1UserDataPrivListRequest;
import com.tahoecn.uc.response.UcV1SysPrivListResponse;
import com.tahoecn.uc.response.UcV1UserDataPrivListResponse;
import com.tahoecn.uc.vo.UcV1SysPrivListResultVO;
import com.tahoecn.uc.vo.UcV1UserDataPrivListResultVO;
import com.tahoecn.uc.vo.UcV1UserDataPrivResultVO;

/**
 * <p>
 * 系统权限表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class BoSysPermissionServiceImpl extends ServiceImpl<BoSysPermissionMapper, BoSysPermission> implements BoSysPermissionService {

	@Autowired
	private BoSysPermissionMapper boSysPermissionMapper;
	
	private static Logger logger = LogManager.getLogger(BoSysPermissionServiceImpl.class);
	
    @Override
	public List<BoSysPermission> getSysPrivList(String userName){
    	List<UcV1SysPrivListResultVO> result = new ArrayList<>();
    	List<BoSysPermission> boSysPermissionList = new ArrayList<BoSysPermission>();
        UcV1SysPrivListRequest ucV1SysPrivListRequest = new UcV1SysPrivListRequest();
        ucV1SysPrivListRequest.setUserName(userName);
        try {
            UcV1SysPrivListResponse ucV1SysPrivListResponse = UcClient.v1SysPrivList(ucV1SysPrivListRequest);
            result = ucV1SysPrivListResponse.getResult();
            for (UcV1SysPrivListResultVO ucV1SysPrivListResultVO : result) {
            	BoSysPermission boSysPermission = boSysPermissionMapper.selectByFdCode(ucV1SysPrivListResultVO.getFdCode());
            	logger.info("boSysPermissionList:" + boSysPermissionList);
            	if(boSysPermission != null) {
            		boSysPermissionList.add(boSysPermission);
            	}
			}
        } catch (UcException e) {
            //请求失败（网络异常或UC返回code不为0），捕获异常信息。
            String message = e.getMessage();
            System.out.println(message);
        }
		return boSysPermissionList;
    }
    
    /**
     * @Title: getUserDataPriv 
     * @Description: 从uc获取用户数据权限
     * @param userName
     * @return List<UcV1UserDataPrivListResultVO>
     * @author liyongxu
     * @date 2019年5月29日 下午2:44:30 
    */
    public List<UcV1UserDataPrivListResultVO> getUserDataPriv(String userName){
    	List<UcV1UserDataPrivListResultVO> result = new ArrayList<>();
        UcV1UserDataPrivListRequest ucV1UserDataPrivListRequest =
                new UcV1UserDataPrivListRequest();
        ucV1UserDataPrivListRequest.setUserName(userName);
        try {
            UcV1UserDataPrivListResponse ucV1UserDataPrivListResponse =
                    UcClient.v1UserDataPrivList(ucV1UserDataPrivListRequest);
            result = ucV1UserDataPrivListResponse.getResult();
        } catch (UcException e) {
            //请求失败（网络异常或UC返回code不为0），捕获异常信息。
            String message = e.getMessage();
            System.out.println(message);
        }
		return result;
    }
    
    @Override
	public List<DataPrivVO> getUserDataPrivList(String userName){
    	List<DataPrivVO> dataPrivVOList = new ArrayList<>();
    	List<UcV1UserDataPrivListResultVO> result = this.getUserDataPriv(userName);
		for(UcV1UserDataPrivListResultVO userDataPrivVO : result) {
			DataPrivVO dataPrivVO = new DataPrivVO();
			List<DataPrivDetailVO> dataPrivDetailVOList = new ArrayList<DataPrivDetailVO>();
			String fdCode = userDataPrivVO.getFdCode();
			dataPrivVO.setFdCode(fdCode);
			if(fdCode.equals(PowerSpotEnum.Management_Overview.getKey())) {
				dataPrivDetailVOList = getDataPrivDetail(dataPrivDetailVOList,userDataPrivVO,fdCode);
			}
			if(fdCode.equals(PowerSpotEnum.Project_Index.getKey())) {
				dataPrivDetailVOList = getDataPrivDetail(dataPrivDetailVOList,userDataPrivVO,fdCode);
			}
			if(fdCode.equals(PowerSpotEnum.Project_Sub_Index.getKey())) {
				dataPrivDetailVOList = getDataPrivDetail(dataPrivDetailVOList,userDataPrivVO,fdCode);
			}
			if(fdCode.equals(PowerSpotEnum.Area_Manage.getKey())) {
				dataPrivDetailVOList = getDataPrivDetail(dataPrivDetailVOList,userDataPrivVO,fdCode);
			}
			if(fdCode.equals(PowerSpotEnum.Price_Manage.getKey())) {
				dataPrivDetailVOList = getDataPrivDetail(dataPrivDetailVOList,userDataPrivVO,fdCode);
			}
			dataPrivVO.setFdDataPrivList(dataPrivDetailVOList);	
			dataPrivVOList.add(dataPrivVO);
		}
		return dataPrivVOList;
    }
	
    /**
     * @Title: getDataPrivDetail 
     * @Description: 获取数据权限点详情数据
     * @param dataPrivDetailVOList
     * @param userDataPrivVO
     * @param fdName
     * @return List<DataPrivDetailVO>
     * @author liyongxu
     * @date 2019年6月4日 下午6:07:00 
    */
    public List<DataPrivDetailVO> getDataPrivDetail(List<DataPrivDetailVO> dataPrivDetailVOList, UcV1UserDataPrivListResultVO userDataPrivVO, String fdName) {
		String fdDataRangeName = userDataPrivVO.getFdDataRangeName();
		if(fdDataRangeName.equals("全集团")) {
			DataPrivDetailVO dataPrivDetailVO = new DataPrivDetailVO();
			dataPrivDetailVO.setFdDataRangeName(fdDataRangeName);
			dataPrivDetailVOList.add(dataPrivDetailVO);
		}else if(fdDataRangeName.equals("本机构") || fdDataRangeName.equals("指定机构") || fdDataRangeName.equals("仅本机构")) {
			Set<String> tempContainer = new HashSet<String>();
			List<UcV1UserDataPrivResultVO> dataRrivlist = userDataPrivVO.getFdDataPrivList();
			for(UcV1UserDataPrivResultVO userDataPrivDetailVO : dataRrivlist) {
				DataPrivDetailVO dataPrivDetailVO = new DataPrivDetailVO();
				String fdSid = userDataPrivDetailVO.getFdSid();
				if(fdDataRangeName.equals("本机构")) {
					tempContainer.add(fdSid);
					dataPrivDetailVO.setFdSid(tempContainer.toString());
					dataPrivDetailVO.setFdCode(userDataPrivDetailVO.getFdCode());
					dataPrivDetailVO.setFdName(userDataPrivDetailVO.getFdName());
					dataPrivDetailVO.setFdDataRangeName(fdDataRangeName);
					dataPrivDetailVO.setFdOrgSid(userDataPrivDetailVO.getFdOrgSid());
					dataPrivDetailVOList.add(dataPrivDetailVO);
				}
			}
		}
		return dataPrivDetailVOList;
	}
}
