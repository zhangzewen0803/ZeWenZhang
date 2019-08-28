/**
 * 项目名：泰禾资产
 * 包名：com.tahoecn.vo.process
 * 文件名：AttachmentFormVo.java
 * 版本信息：1.0.0
 * 日期：2018年12月13日-下午4:33:11
 * Copyright (c) 2018 Pactera 版权所有
 */
 package com.tahoecn.bo.model.dto;

import java.io.Serializable;

/**
 * @ClassName：AttachmentFormVo
 * @Description：BPM附件信息接受类
 * @author LiuGuiChao 
 * @date 2018年12月13日 下午4:33:11 
 * @version 1.0.0 
 */
public class AttachmentFormDto implements Serializable {
	private static final long serialVersionUID = 5574850132593192753L;
    private String fdKey;
    private String fdFileName;
    private byte[] fdAttachment;
    private String fdAttachmentFilePath;
	  
	public String getFdKey() {
		return fdKey;
	}
	public void setFdKey(String fdKey) {
		this.fdKey = fdKey;
	}
	public String getFdFileName() {
		return fdFileName;
	}
	public void setFdFileName(String fdFileName) {
		this.fdFileName = fdFileName;
	}
	public byte[] getFdAttachment() {
		return fdAttachment;
	}
	public void setFdAttachment(byte[] fdAttachment) {
		this.fdAttachment = fdAttachment;
	}
	public String getFdAttachmentFilePath() {
		return fdAttachmentFilePath;
	}
	public void setFdAttachmentFilePath(String fdAttachmentFilePath) {
		this.fdAttachmentFilePath = fdAttachmentFilePath;
	}
}
