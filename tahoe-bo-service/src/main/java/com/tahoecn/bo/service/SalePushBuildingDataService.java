package com.tahoecn.bo.service;

/**
 * <p>
 * 营销推送 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-06-03
 */
public interface SalePushBuildingDataService{

	/**
	 * @Title: pushBuildingData 
	 * @Description: 推动楼栋业态到Sale
	 * @param saleUrl
	 * @param JSONObject
	 * @return JsonResult
	 * @author liyongxu
	 * @date 2019年6月13日 上午11:14:13 
	*/
	String pushBuildingData(String jsonList);
	
	/**
	 * @Title: syncRoomInfo 
	 * @Description: 同步Sale房间信息
	 * @param saleUrl
	 * @param jsonList
	 * @return JSONObject
	 * @author liyongxu
	 * @date 2019年6月13日 上午11:14:24 
	*/
	String syncRoomInfo(String jsonList);
	
	/**
	 * @Title: syncRoomStreamData 
	 * @Description: 同步Sale房间流水
	 * @param saleUrl
	 * @param jsonList
	 * @return JSONObject
	 * @author liyongxu
	 * @date 2019年6月13日 上午11:15:54 
	*/
	String syncRoomStreamData(String jsonList);
	
	/**
	 * @Title: checkBuildingData 
	 * @Description: 校验楼栋是否可以删除
	 * @param saleUrl
	 * @param jsonList
	 * @return JSONObject
	 * @author liyongxu
	 * @date 2019年6月13日 上午11:19:27 
	*/
	String checkBuildingData(String jsonList);
}
