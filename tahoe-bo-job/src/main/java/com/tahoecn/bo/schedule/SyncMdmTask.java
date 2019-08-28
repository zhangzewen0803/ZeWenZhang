package com.tahoecn.bo.schedule;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tahoecn.bo.common.utils.DateUtils;
import com.tahoecn.bo.common.utils.HttpClientUtil;
import com.tahoecn.bo.common.utils.SignUtil;
import com.tahoecn.bo.model.vo.MonitorVo;
import com.tahoecn.bo.service.MdmProjectInfoService;


/**
 * 主数据数据同步任务
 */
@Component
public class SyncMdmTask {

	@Autowired
	private MdmProjectInfoService mdmProjectInfoService;
	
	@Value("${tahoe.mdm.key}")
	private String mdm_key;

	@Value("${tahoe.mdm.url}")
	private String mdm_url;

	@Value("${tahoe.mdm.client}")
	private String mdm_sys_id;

	/**
	 * @Title: syncMdmData 
	 * @Description: TODO(这里用一句话描述这个方法的作用) void
	 * @author liyongxu
	 * @date 2019年5月29日 下午5:03:09 
	*/
//	@Scheduled(fixedRate = 24 * 60 * 60 * 1000)
	public void syncProjectData() {
    	MonitorVo monitorVo = new MonitorVo();
    	monitorVo.setJobName("定时接受主数据所属公司(从MDM接口获取数据到本地库)");//调度任务所属业务系统名称
    	try {
    		insertCompanyLegalInfo();
    		monitorVo.setJobResult("0");//调度任务执行结果（0成功1失败）
		} catch (Exception e) {
			monitorVo.setJobResult("1");//调度任务执行结果（0成功1失败）
		}
	}
	
    /**
	 * 同步法人公司接口
	 */
	public String insertCompanyLegalInfo() {
		Integer timestamp = DateUtils.getSecondTimestamp(new Date());
		String companyUrl = "/projectInfo/listCity";
		Map<String, String> paramMap = new HashMap<>();
		String fromTime="";
		try {
			fromTime = URLEncoder.encode(timestamp.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String md5Hex = DigestUtils.md5Hex(System.currentTimeMillis() + fromTime + mdm_key);
		
		paramMap.put("token", md5Hex);
		paramMap.put("sysId", mdm_sys_id);
		paramMap.put("timestamp", timestamp.toString());
		String paramStr = SignUtil.createLinkString(SignUtil.buildRequestPara4UC(paramMap, mdm_key));
		StringBuilder builder = new StringBuilder();
		builder.append(mdm_url).append(companyUrl).append("?").append(paramStr);
		String jsonStr = HttpClientUtil.getInstance().sendHttpGet(builder.toString());
		
		JSONObject jsonObject = JSONObject.parseObject(jsonStr);
		String code = jsonObject.getString("code");
		String status = "0";
		if(status.equals(code)) {
			//成功 解析
			JSONArray jsonArray = jsonObject.getJSONArray("result");
			
			//删除数据库的数据
	//		mdmProjectInfoService.deleteAll();
			mdmProjectInfoService.saveMdmProjectInfo(jsonArray);
		}
		
		return jsonStr;
	}
	
}
