package com.tahoecn.bo.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tahoecn.bo.service.SalePushBuildingDataService;
import com.tahoecn.http.HttpUtil;

/**
 * <p>
 * 营销推送楼栋 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-06-03
 */
@Service
public class SalePushBuildingDataServiceImpl implements SalePushBuildingDataService {


    @Value("${tahoe.sale.push.url}")
	private String salePushUrl;
    
    @Value("${tahoe.sale.roomInfo.url}")
    private String saleSyncRoomUrl;
    
    @Value("${tahoe.sale.roomStream.url}")
    private String saleSyncRoomStreamUrl;
    
    @Value("${tahoe.sale.check.url}")
    private String saleCheckUrl;
	
	/***
	 * 调Sale推送接口
	 * @param now 当前时间
	 * @param userToken 用户Token
	 * @return
	 */
	@Override
	public String pushBuildingData(String jsonList){
		String jsonStr =  HttpUtil.post(salePushUrl, jsonList);
		return jsonStr;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String syncRoomInfo(String time){
		Map paramMap = new HashMap<>();
		paramMap.put("time", time);
		paramMap.put("v", System.currentTimeMillis());
		String jsonStr = HttpUtil.get(saleSyncRoomUrl, paramMap);
		return jsonStr;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String syncRoomStreamData(String time){
		Map paramMap = new HashMap<>();
		paramMap.put("time", time);
		paramMap.put("v", System.currentTimeMillis());
		String jsonStr = HttpUtil.get(saleSyncRoomStreamUrl, paramMap);
		return jsonStr;
	}
	
	@Override
	public String checkBuildingData(String jsonList){
		String jsonStr =  HttpUtil.post(saleCheckUrl, jsonList);
		return jsonStr;
	}
	
}
