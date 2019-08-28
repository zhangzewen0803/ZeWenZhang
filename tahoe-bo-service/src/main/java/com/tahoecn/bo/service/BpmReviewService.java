package com.tahoecn.bo.service;

import java.util.List;

import com.tahoecn.bo.model.bo.BpmReviewBo;
import com.tahoecn.bo.model.dto.BpmReviewDto;
import com.tahoecn.bo.model.vo.CurrentUserVO;
import com.tahoecn.bpm.vo.ClientButton;
import com.tahoecn.core.json.JSONResult;

/**
 * @ClassName：BpmService
 * @Description：Bpm
 * @author liyongxu 
 * @date 2019年5月31日 上午10:00:47 
 * @version 1.0.0 
 */
public interface BpmReviewService{

	/**
	 * @Title: newProcess 
	 * @Description: 发起流程
	 * @return JSONResult<String>
	 * @author liyongxu
	 * @param currentUserVO 
	 * @date 2019年5月31日 上午10:43:32 
	*/
	JSONResult<BpmReviewBo> newProcess(BpmReviewDto bpmReviewDto, CurrentUserVO currentUserVO);

	/**
	 * @Title: updateFormData 
	 * @Description: 修改表单内容
	 * @return JSONResult<String>
	 * @author liyongxu
	 * @param userName 
	 * @param currentUserVO 
	 * @date 2019年5月31日 上午10:44:30 
	*/
	JSONResult<BpmReviewBo> updateFormData(String processId,BpmReviewDto bpmReviewDto, String userName, CurrentUserVO currentUserVO);

	/**
	 * @Title: backProcess 
	 * @Description: 流程撤回
	 * @param processId
	 * @return JSONResult<String>
	 * @author liyongxu
	 * @date 2019年5月31日 上午10:44:50 
	*/
	JSONResult<String> backProcess(String processId,CurrentUserVO currentUserVO);

	/**
	 * @Title: dropProcess 
	 * @Description: 废弃流程
	 * @param processId
	 * @return JSONResult<String>
	 * @author liyongxu
	 * @date 2019年5月31日 上午10:46:53 
	*/
	JSONResult<String> dropProcess(String processId,CurrentUserVO currentUserVO);

	/**
	 * @Title: showButtons 
	 * @Description: 业务系统获取表单按钮状态
	 * @param processId
	 * @param clientButtons
	 * @return JSONResult<List<ClientButton>>
	 * @author liyongxu
	 * @date 2019年6月13日 下午5:11:01 
	*/
	JSONResult<List<ClientButton>> showButtons(String processId,List<ClientButton> clientButtons);
	
	/**
	 * @Title: getGotoAuditProcessUrl 
	 * @Description: 获取跳转审批过程URL
	 * @param processId
	 * @return JSONResult<String>
	 * @author liyongxu
	 * @date 2019年6月13日 下午5:13:26 
	*/
	JSONResult<String> getGotoAuditProcessUrl(String processId);
	
}
