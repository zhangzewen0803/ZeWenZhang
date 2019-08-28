package com.tahoecn.bo.model.vo;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName：BpmBusinessInfoBo
 * @Description：TODO(这里用一句话描述这个类的作用) 
 * @author liyongxu 
 * @date 2019年6月19日 下午4:29:20 
 * @version 1.0.0 
 */
@Data
public class BpmProcessInfoVo{

	@ApiModelProperty(value="标题", name="docSubject")
    private String docSubject;
	
	@ApiModelProperty(value="版本描述", name="versionDesc")
	private String versionDesc;

	@ApiModelProperty(value="创建人Name", name="docCreator")
    private String docCreatorName;
	
	@ApiModelProperty(value="创建时间", name="createTime")
    private String createTime;
	
	@ApiModelProperty(value="内容", name="docContent")
	private String docContent;
	
	@ApiModelProperty(value="流程状态", name="processStatus")
	private String processStatus;

	@ApiModelProperty(value="驳回单据类型 （ 0-非驳回单据、1-返回打回人、2-重走流程）", name="backType")
    private String backType;
    
	@ApiModelProperty(value="按钮展示", name="showButton")
	private List<BpmShowButtonVo> showButton;
	
}
