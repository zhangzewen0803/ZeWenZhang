/**
 * 项目名：泰禾资产
 * 包名：com.tahoecn.bo.config
 * 文件名：UcRabbitMQMessageListener.java
 * 版本信息：1.0.0
 * 日期：2019年5月28日-下午4:47:29
 * Copyright (c) 2019 Pactera 版权所有
 */
 
package com.tahoecn.bo.config;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.tahoecn.bo.common.utils.StrUtils;
import com.tahoecn.bo.model.entity.UcOrg;
import com.tahoecn.bo.model.entity.UcUser;
import com.tahoecn.bo.service.UcOrgService;
import com.tahoecn.bo.service.UcUserService;

/**
 * @ClassName：UcRabbitMQMessageListener
 * @Description：TODO(这里用一句话描述这个类的作用) 
 * @author liyongxu 
 * @date 2019年5月28日 下午4:47:29 
 * @version 1.0.0 
 */
@Component
public class UcRabbitMQMessageListener implements ChannelAwareMessageListener {

	@Autowired
	private UcUserService ucUserService;
	
	@Autowired
	private UcOrgService ucOrgService;
	
	private static Logger logger = LogManager.getLogger(UcRabbitMQMessageListener.class);
	
	 /** 0-不同步；1-同步；2-不处理 */
    public static final String FD_SYNC_SYS_NO = "0";
    public static final String FD_SYNC_SYS = "1";
    public static final String FD_SYNC_SYS_HANDLE_NO = "2";
    
    /** 1-有效 */
    public static final String FD_AVAILABLE = "1";
    /** -1--未删除 */
    public static final String FD_ISDELETE = "-1";
    
	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		logger.info("### Receive UC Message Begin");
    	try {
			 byte[] body = message.getBody();
			 String receiveMsg = new String(body,"UTF-8");
			 if(StrUtils.isNotEmpty(receiveMsg)) {
				 logger.info("接收rabbitMQ推送数据：" + receiveMsg);
				 JSONObject jsonObject = JSONObject.parseObject(receiveMsg);
				 String opType = jsonObject.getString("opType");
				 String dataType = jsonObject.getString("dataType");
				 logger.info("推送类型dataType：" + dataType);
				 if(dataType.equals("uc_org")) {
					 //组织机构数据处理
					 operateUcOrg(opType,jsonObject);
				 }else if(dataType.equals("uc_user")) {
					//用户信息数据处理
					 operateUcUser(opType,jsonObject);
				 }
			 }
		} catch (Exception e) {
			logger.error("### Receive UC Message ERROR",e);
		} finally {
			try {
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.info("### Receive UC Message End");
		}
	}
	
	/**
	 * @Title: operateUcOrg 
	 * @Description: 处理UcOrg数据
	 * @param opType
	 * @param jsonObject void
	 * @author liyongxu
	 * @date 2019年5月28日 下午7:59:51 
	*/
	public void operateUcOrg(String opType, JSONObject jsonObject) {
		if(opType.equals("D")) {
			//日志记录
			 logger.info("同步UC组织机构信息，更新组织机构表");
			 String dataSid = jsonObject.getString("dataSid");
			 //删除组织机构
			 ucOrgService.deleteByFdSid(dataSid);
			 logger.info("删除组织机构："+dataSid);
		 }else {
			 JSONObject data =  (JSONObject) jsonObject.get("result");
			 String fdSyncSys = data.getString("fd_sync_sys");
			 if(StrUtils.isNotEmpty(fdSyncSys)) {
				 String fdAvailable = data.getString("fd_available");
				 String fdIsdelete = data.getString("fd_isdelete");
				 String flag = String.valueOf(fdSyncSys.charAt(37));
				 if(flag.equals(FD_SYNC_SYS)) {
					 if(fdAvailable.equals(FD_AVAILABLE) && fdIsdelete.equals(FD_ISDELETE)) {
						 JSONObject ucOrgData =  (JSONObject) jsonObject.get("result");
						 ucOrgService.saveOrUpdateUcOrg(this.binationOrUpdateUcOrg(ucOrgData));
						 if(opType.equals("I")) {
							 logger.info("新增组织机构：" + data.getString("fd_name"));
						 }else {
							 logger.info("修改组织机构：" + data.getString("fd_name"));
						 }
					 }
				 }
				 //flag为0，则查库，判断对应id是否存在，存在则删除
				 if(flag.equals(FD_SYNC_SYS_NO)){
					 if(opType.equals("U")) {
						 String dataSid = jsonObject.getString("dataSid");
						 ucOrgService.deleteByFdSid(dataSid);
						 logger.info("同步UC组织机构信息，更新组织机构表");
						 logger.info("删除组织机构：" + dataSid);
					 }
				 }
			 }
		 }
	}
	
	/**
	 * @Title: operateUcUser 
	 * @Description: 处理UcUser数据
	 * @param opType
	 * @param jsonObject void
	 * @author liyongxu
	 * @date 2019年5月28日 下午8:00:54 
	*/
	public void operateUcUser(String opType, JSONObject jsonObject) {
		//用户信息
		 if(opType.equals("D")) {
			//日志记录
			 logger.info("同步UC人员信息，更新人员表");
			 String dataSid = jsonObject.getString("dataSid");
			 //删除人员
			 ucUserService.deleteByFdSid(dataSid);
			 logger.info("删除人员："+dataSid);
		 }else {
			 JSONObject data =  (JSONObject) jsonObject.get("result");
			 String fdSyncSys = data.getString("fd_sync_sys");
			 if(StrUtils.isNotEmpty(fdSyncSys)) {
				 String fdAvailable = data.getString("fd_available");
				 String fdIsdelete = data.getString("fd_isdelete");
				 String flag = String.valueOf(fdSyncSys.charAt(37));
				 if(flag.equals(FD_SYNC_SYS)) {
					 if(fdAvailable.equals(FD_AVAILABLE) && fdIsdelete.equals(FD_ISDELETE)) {
						 JSONObject ucUserData =  (JSONObject) jsonObject.get("result");
						 ucUserService.saveOrUpdateUcUser(this.binationOrUpdateUcUser(ucUserData));
						 if(opType.equals("I")) {
							 logger.info("新增人员：" + data.getString("fd_username"));
						 }else {
							 logger.info("修改人员：" + data.getString("fd_username"));
						 }
					 }
				 }
			 }
		 }
	}
	
	/**
	 * @Title: binationOrUpdateUcUser 
	 * @Description: 重组UcUser数据
	 * @param jsonResult
	 * @return UcUser
	 * @author liyongxu
	 * @date 2019年5月28日 下午7:09:49 
	*/
	public UcUser binationOrUpdateUcUser(JSONObject jsonResult) {
		UcUser userTset = new UcUser();
		userTset.setFdSid(jsonResult.getString("fd_sid"));
		userTset.setFdName(jsonResult.getString("fd_name"));
		userTset.setFdUsername(jsonResult.getString("fd_username"));
		userTset.setFdOrder(jsonResult.getIntValue("fd_order"));
		userTset.setFdTel(jsonResult.getString("fd_tel"));
		userTset.setFdWorkPhone(jsonResult.getString("fd_work_phone"));
		userTset.setFdEmail(jsonResult.getString("fd_email"));
		userTset.setFdAvailable(jsonResult.getIntValue("fd_available"));
		userTset.setFdOrgIdTree(jsonResult.getString("fd_org_id_tree"));
		userTset.setFdOrgNameTree(jsonResult.getString("fd_org_name_tree"));
		userTset.setFdOrgId(jsonResult.getString("fd_org_id"));
		userTset.setFdOrgName(jsonResult.getString("fd_org_name"));
		userTset.setFdIsdelete(jsonResult.getIntValue("fd_isdelete"));
		userTset.setFdGender(jsonResult.getIntValue("fd_gender"));
		userTset.setFdTahoeMessageSid(jsonResult.getString("fd_tahoe_message_sid"));
		userTset.setFdProvinceCode(jsonResult.getString("fd_province_code"));
		userTset.setFdProvinceName(jsonResult.getString("fd_province_name"));
		userTset.setFdCityCode(jsonResult.getString("fd_city_code"));
		userTset.setFdCityName(jsonResult.getString("fd_city_name"));
		userTset.setCreateTime(LocalDateTime.now());
		return userTset;
	}
	
	/**
	 * @Title: binationOrUpdateUcOrg 
	 * @Description: 重组UcOrg数据
	 * @param jsonResult void
	 * @author liyongxu
	 * @date 2019年5月28日 下午7:52:52 
	*/
	public UcOrg binationOrUpdateUcOrg(JSONObject jsonResult) {
		UcOrg ucOrgTest = new UcOrg();
		ucOrgTest.setFdSid(jsonResult.getString("fd_sid"));
		ucOrgTest.setFdName(jsonResult.getString("fd_name"));
		ucOrgTest.setFdCode(jsonResult.getString("fd_code"));
		ucOrgTest.setFdType(jsonResult.getString("fd_type"));
		ucOrgTest.setFdPsid(jsonResult.getString("fd_psid"));
		ucOrgTest.setFdPname(jsonResult.getString("fd_pname"));
		ucOrgTest.setFdSidTree(jsonResult.getString("fd_sid_tree"));
		ucOrgTest.setFdNameTree(jsonResult.getString("fd_name_tree"));
		ucOrgTest.setFdOrder(jsonResult.getIntValue("fd_order"));
		ucOrgTest.setFdAvailable(jsonResult.getIntValue("fd_available"));
		ucOrgTest.setFdIsdelete(jsonResult.getIntValue("fd_isdelete"));
		ucOrgTest.setCreateTime(LocalDateTime.now());
		return ucOrgTest;
	}

}
