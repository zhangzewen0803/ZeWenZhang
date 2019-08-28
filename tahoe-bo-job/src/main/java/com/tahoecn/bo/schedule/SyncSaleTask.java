package com.tahoecn.bo.schedule;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.tahoecn.bo.common.utils.Constants;
import com.tahoecn.bo.common.utils.DataUtils;
import com.tahoecn.bo.common.utils.DateUtils;
import com.tahoecn.bo.common.utils.StrUtils;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.model.entity.SaleRoom;
import com.tahoecn.bo.model.entity.SaleRoomStream;
import com.tahoecn.bo.model.entity.SaleRoomSyncLog;
import com.tahoecn.bo.service.SalePushBuildingDataService;
import com.tahoecn.bo.service.SaleRoomService;
import com.tahoecn.bo.service.SaleRoomStreamService;
import com.tahoecn.bo.service.SaleRoomSyncLogService;

/**
 * 营销系统数据同步任务
 */
@Component
public class SyncSaleTask {
	
	@Autowired
	private SalePushBuildingDataService salePushBuildingDataService;

	@Autowired
	private SaleRoomStreamService saleRoomStreamService;
	
	@Autowired
	private SaleRoomService saleRoomService;
	
	@Autowired
	private SaleRoomSyncLogService saleRoomSyncLogService;
	
	private static final Logger logger = LoggerFactory.getLogger(SyncSaleTask.class);
	
	/**
     * 定时从营销系统-基础信息
     * @throws ParseException 
     */
    @Scheduled(cron = "0 0 7,13 * * ?")
	public void syncRoomInfo(){
    	Integer isAll = 0;
		SaleRoomSyncLog saleRoomSyncLog = saleRoomSyncLogService.getSyncTimeStamp(Constants.SYNC_ROOM_INFO);
		if(saleRoomSyncLog == null || StrUtils.isEmpty(saleRoomSyncLog.getSyncTimestamp())) {
			SaleRoomSyncLog saleRoomSyncLogNew = new SaleRoomSyncLog();
			Date date = null;
			try {
				isAll = 1;
				date = DateUtils.toUtilDateFromStrDateByFormat("1970-01-01 00:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String mixDate = String.valueOf(date.getTime());
			saleRoomSyncLogNew.setSyncTimestamp(mixDate);
			saleRoomSyncLog = saleRoomSyncLogNew;
		}
		logger.info("Timestamp:" + saleRoomSyncLog.getSyncTimestamp());
		String respJson = salePushBuildingDataService.syncRoomInfo(saleRoomSyncLog.getSyncTimestamp());
		this.batchSaveRoomInfo(respJson,isAll);
    }
	
	/**
	 * @Title: batchSaveRoomInfo 
	 * @Description: 批量保存房间信息
	 * @param jsonResult void
	 * @author liyongxu
	 * @date 2019年6月17日 下午1:38:50 
	*/
	public void batchSaveRoomInfo(String respJson,Integer isAll) {
		String dateStr = "";
		Integer listSize = null;
		JSONObject respJsonObj = JSONObject.parseObject(respJson);
		logger.info("SaleRespCode:" + respJsonObj.getString("code"));
		logger.info("SaleRespMsg:" + respJsonObj.getString("msg"));
		
		List<SaleRoom> saleRoomInfoList = new ArrayList<>();
		if(respJsonObj.getString("code").equals("0")) {
			JSONObject dataJsonObj = respJsonObj.getJSONObject("data");
			JSONArray roomMxList = dataJsonObj.getJSONArray("roomMxList");
			dateStr = dataJsonObj.getString("time");
			logger.info("SaleResp RoomSize:" + roomMxList.size());
			listSize = roomMxList.size();
			boolean printBld = true;
			for (int i = 0; i < roomMxList.size(); i++) {
				JSONObject roomJson = roomMxList.getJSONObject(i);
    			SaleRoom saleRoom = new SaleRoom();
    			DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    			saleRoom.setRoomGuid(roomJson.getString("roomGUID"));
    			saleRoom.setBuGuid(roomJson.getString("bUGUID"));
    			saleRoom.setProjGuid(roomJson.getString("projGUID"));
    			saleRoom.setBldGuid(roomJson.getString("bldGUID"));
    			saleRoom.setMainRoomGuid(roomJson.getString("mainRoomGUID"));
    			saleRoom.setUnit(roomJson.getString("unit"));
    			saleRoom.setFloor(roomJson.getString("floor"));
    			saleRoom.setNo(roomJson.getString("no"));
    			saleRoom.setRoom(roomJson.getString("room"));
    			saleRoom.setRoomCode(roomJson.getString("roomCode"));
    			saleRoom.setHuXing(roomJson.getString("huXing"));
    			saleRoom.setStatus(roomJson.getString("status"));
    			saleRoom.setIsVirtualRoom(roomJson.getInteger("isVirtualRoom"));
    			saleRoom.setBldArea(roomJson.getBigDecimal("bldArea"));
    			saleRoom.setTnArea(roomJson.getBigDecimal("tnArea"));
    			if(StrUtils.isNotEmpty(roomJson.getString("blRhDate"))) {
    				saleRoom.setBlrhDate(LocalDateTime.parse(roomJson.getString("blRhDate"),df));
    			}
    			saleRoom.setPrice(roomJson.getBigDecimal("price"));
    			saleRoom.setTnPrice(roomJson.getBigDecimal("tnPrice"));
    			saleRoom.setBaseTotal(roomJson.getBigDecimal("baseTotal"));
    			saleRoom.setTotal(roomJson.getBigDecimal("total"));
    			if(StrUtils.isNotEmpty(roomJson.getString("tfDate"))) {
    				saleRoom.setTfDate(LocalDateTime.parse(roomJson.getString("tfDate"), df));
    			}
    			saleRoom.setDjArea(roomJson.getString("djArea"));
    			if(StrUtils.isNotEmpty(roomJson.getString("rHDate"))) {
    				saleRoom.setRhDate(LocalDateTime.parse(roomJson.getString("rHDate"), df));
    			}
    			saleRoom.setAreaStatus(roomJson.getString("areaStatus"));
    			saleRoom.setRoomStru(roomJson.getString("roomStru"));
    			saleRoom.setbProductTypeCode(roomJson.getString("bProductTypeCode"));
    			saleRoom.setYsBldArea(roomJson.getBigDecimal("ysBldArea"));
    			saleRoom.setYsTnArea(roomJson.getBigDecimal("ysTnArea"));
    			saleRoom.setScBldArea(roomJson.getBigDecimal("scBldArea"));
    			saleRoom.setScTnArea(roomJson.getBigDecimal("scTnArea"));
    			saleRoom.setEarnest(roomJson.getBigDecimal("earnest"));
	    		saleRoom.setYszNum(roomJson.getString("ySZNum"));
	    		saleRoom.setNoTaxAmount(roomJson.getBigDecimal("noTaxAmount"));
	    		saleRoom.setTaxAmount(roomJson.getBigDecimal("taxAmount"));
	    		saleRoom.setTaxRate(roomJson.getBigDecimal("taxRate"));
	    		saleRoom.setNoTaxAmountDj(roomJson.getBigDecimal("noTaxAmountDj"));
	    		saleRoom.setTaxAmountDj(roomJson.getBigDecimal("taxAmountDj"));
    			saleRoom.setIsMainRoom(roomJson.getInteger("isMainRoom"));
    			if(StrUtils.isNotEmpty(roomJson.getString("gxTime"))) {
    				saleRoom.setSourceCreateTime(LocalDateTime.parse(roomJson.getString("gxTime"),df));
    			}
    			if(StrUtils.isNotEmpty(roomJson.getString("sjQdYsDate"))) {
    				saleRoom.setRealTakeCardTime(LocalDateTime.parse(roomJson.getString("sjQdYsDate"),df));
    			}
    			if(StrUtils.isNotEmpty(roomJson.getString("yjQdYsDate"))) {
    				saleRoom.setPreTakeCardTime(LocalDateTime.parse(roomJson.getString("yjQdYsDate"),df));
    			}
    			saleRoom.setMainDataBldId(roomJson.getString("mainDataBldID"));
    			saleRoom.setMainDataProductTypeCode(roomJson.getString("mainDataProductTypeCode"));
    			saleRoomInfoList.add(saleRoom);
    			
    			if(i < 20){
					logger.info("saleRoomJson-" + (i+1) + ":" + JSONObject.toJSONString(roomJson,SerializerFeature.WriteMapNullValue));
				}else if(printBld){
					if(StrUtils.isNotEmpty(roomJson.getString("mainDataBldID"))) {
	    				logger.info("saleRoomJson-" + (i+1) + ":" + JSONObject.toJSONString(roomJson,SerializerFeature.WriteMapNullValue));
						printBld = false;
					}
				}
			}
		}
		
		//房间表信息数据不为空时入库
		if(DataUtils.isNotEmpty(saleRoomInfoList)) {
			saleRoomService.batchSaveRoomInfoData(saleRoomInfoList,isAll);
			this.saveSyncLogInfo(dateStr,Constants.SYNC_ROOM_INFO,listSize);
		}
	}
	
	/**
	 * 定时从营销系统-同步房源流水数据
	 * @throws ParseException 
	 */
	@Scheduled(cron = "0 0 7,13 * * ?")
	public void syncRoomStreamData(){
		Integer isAll = 0;
		SaleRoomSyncLog saleRoomSyncLog = saleRoomSyncLogService.getSyncTimeStamp(Constants.SYNC_ROOM_STREAM_INFO);
		if(saleRoomSyncLog == null || StrUtils.isEmpty(saleRoomSyncLog.getSyncTimestamp())) {
			SaleRoomSyncLog saleRoomSyncLogNew = new SaleRoomSyncLog();
			Date date = null;
			try {
				isAll = 1;
				date = DateUtils.toUtilDateFromStrDateByFormat("1970-01-01 00:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String mixDate = String.valueOf(date.getTime());
			saleRoomSyncLogNew.setSyncTimestamp(mixDate);
			saleRoomSyncLog = saleRoomSyncLogNew;
		}
		logger.info("Timestamp:" + saleRoomSyncLog.getSyncTimestamp());
		String respJson = salePushBuildingDataService.syncRoomStreamData(saleRoomSyncLog.getSyncTimestamp());
		this.batchSaveRoomStreamData(respJson,isAll);
	}
	
	/**
	 * @Title: batchSaveRoomStreamData 
	 * @Description: 批量保存房间流水
	 * @param jsonObject void
	 * @author liyongxu
	 * @date 2019年6月17日 上午11:18:03 
	*/
	public void batchSaveRoomStreamData(String respJson,Integer isAll) {
		String dateStr = "";
		Integer listSize = null;
		JSONObject respJsonObj = JSONObject.parseObject(respJson);
		logger.info("saleRoomStreamList code:" + respJsonObj.getString("code"));
		logger.info("saleRoomStreamList msg:" + respJsonObj.getString("msg"));
		List<SaleRoomStream> saleRoomStreamList = new ArrayList<SaleRoomStream>();
		if(respJsonObj.getString("code").equals("0")) {
			JSONObject dataJsonObj = respJsonObj.getJSONObject("data");
			JSONArray roomHistoryList = dataJsonObj.getJSONArray("roomHistoryList");
			dateStr = dataJsonObj.getString("time");
			logger.info("roomHistoryList size:" + roomHistoryList.size());
			listSize = roomHistoryList.size();
			boolean printBld = true;
    		for (int i = 0; i < roomHistoryList.size(); i++) {
    			JSONObject streamJson = roomHistoryList.getJSONObject(i);
    			SaleRoomStream saleRoomStream = new SaleRoomStream();
    			DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    			saleRoomStream.setStreamGuid(streamJson.getString("logGUID"));
    			saleRoomStream.setRoomGuid(streamJson.getString("roomGUID"));
    			saleRoomStream.setRoomCode(streamJson.getString("roomCode"));
    			saleRoomStream.setBldArea(streamJson.getBigDecimal("bldArea"));
    			saleRoomStream.setPrice(streamJson.getBigDecimal("price"));
    			saleRoomStream.setRoomTotal(streamJson.getBigDecimal("roomTotal"));
    			if(StrUtils.isNotEmpty(streamJson.getString("gxTime"))) {
    				saleRoomStream.setGxTime(LocalDateTime.parse(streamJson.getString("gxTime"),df));
    			}
    			saleRoomStream.setGxReason(streamJson.getString("gxReason"));
    			saleRoomStream.setAreaStatus(streamJson.getString("areaStatus"));
    			saleRoomStream.setOtherItem(streamJson.getBigDecimal("otherItem"));
    			saleRoomStream.setIsVirtualRoom(streamJson.getInteger("isVirtualRoom"));
    			saleRoomStream.setMainDataBldId(streamJson.getString("mainDataBldID"));
    			saleRoomStream.setMainDataProductTypeCode(streamJson.getString("mainDataProductTypeCode"));
    			saleRoomStream.setStatus(streamJson.getString("status"));
    			if(StrUtils.isNotEmpty(streamJson.getString("sjQdYsDate"))) {
    				saleRoomStream.setRealTakeCardTime(LocalDateTime.parse(streamJson.getString("sjQdYsDate"),df));
    			}
    			saleRoomStreamList.add(saleRoomStream);
    			
    			if(i < 20){
					logger.info("saleRoomJson-" + (i+1) + ":" + JSONObject.toJSONString(streamJson,SerializerFeature.WriteMapNullValue));
				}else if(printBld){
					if(StrUtils.isNotEmpty(streamJson.getString("mainDataBldID"))) {
	    				logger.info("saleRoomJson-" + (i+1) + ":" + JSONObject.toJSONString(streamJson,SerializerFeature.WriteMapNullValue));
						printBld = false;
					}
				}
    		}
		}
		
		//房间流水数据不为空时入库
		if(DataUtils.isNotEmpty(saleRoomStreamList)) {
			saleRoomStreamService.batchSaveRoomStreamData(saleRoomStreamList,isAll);
			this.saveSyncLogInfo(dateStr,Constants.SYNC_ROOM_STREAM_INFO,listSize);
		}
	}
	
	/**
	 * @Title: syncLog 
	 * @Description: 保存同步时间戳日志
	 * @param syncTimestamp void
	 * @author liyongxu
	 * @date 2019年6月26日 下午5:10:28 
	*/
	private void saveSyncLogInfo(String syncTimestamp,String type,Integer listSize) {
		SaleRoomSyncLog saleRoomSyncLog = new SaleRoomSyncLog();
		saleRoomSyncLog.setId(UUIDUtils.create());
		saleRoomSyncLog.setSyncTimestamp(syncTimestamp);
		saleRoomSyncLog.setType(type);
		saleRoomSyncLog.setNum(listSize);
		saleRoomSyncLog.setCreateTime(LocalDateTime.now());
		saleRoomSyncLogService.saveSyncLogInfo(saleRoomSyncLog);
	}
}
