package com.tahoecn.bo.model.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
* @ClassName: ReviewParameterFormVo
* @Description: TODO
* @author  LIUGUICHAO
* @date 2018年7月31日 下午3:19:03
* @version 1.0 
*/ 
@Data
public class OrderParameterFormDto {
	private String docSubject;
	private String fdTemplate;
	private String docCreator; 
	private String orderId;
	private String orderType; //单据类型 添加数据字典
	private Map<String,Object> formValues;
	private String formValuesJson; //已经拼接完成的表单参数Json数据
	private List<AttachmentFormDto> attachmentForms; //附件列表
	
}
