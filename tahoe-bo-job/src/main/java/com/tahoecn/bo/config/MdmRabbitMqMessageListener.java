package com.tahoecn.bo.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.tahoecn.bo.service.MdmProjectInfoService;

@Component
@EnableTransactionManagement(proxyTargetClass = true)
public class MdmRabbitMqMessageListener implements ChannelAwareMessageListener{

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private MdmProjectInfoService mdmProjectInfoService;
	
	/**
	 * 接收消息
	 */
	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		try { 
			 byte[] body = message.getBody();
			 String receiveMsg = new String(body,"UTF-8");
			 LOGGER.info("接收到主数据mq结果为:==="+receiveMsg);
			 JSONObject jsonObject=JSONObject.parseObject(receiveMsg);
			 //opType 操作（增：I、删：D、改：U） 
			 String opType=jsonObject.getString("opType");
			 //主键
			 String dataSid=jsonObject.getString("dataSid");
			 String inst="I";
			 String del="D";
			 String upd="U";
			 if(opType.equals(inst)) {
				//结果
				 JSONObject result=jsonObject.getJSONObject("result");
				 LOGGER.info("新增主数据mq:==="+dataSid);
				 mdmProjectInfoService.saveMdmProjectInfo(result);
			 }else if(opType.equals(del)) {
				 LOGGER.info("删除主数据mq:==="+dataSid);
				 mdmProjectInfoService.deleteMdmProjectInfo(dataSid);
			 }else if(opType.equals(upd)) {
				//结果
				 JSONObject result=jsonObject.getJSONObject("result");
				 LOGGER.info("修改主数据mq:==="+dataSid);
				 mdmProjectInfoService.updateMdmProjectInfo(result);
			 }
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
//				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
     }
	
}
