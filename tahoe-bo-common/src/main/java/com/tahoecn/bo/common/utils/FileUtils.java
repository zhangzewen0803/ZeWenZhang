package com.tahoecn.bo.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件处理工具类.
 * <pre>
 * <b>依赖jar包：</b>
 * javax.servlet-api-3.0.1.jar
 * spring-web-4.1.3.RELEASE.jar
 * spring-mock-2.0.8.jar
 * </pre>
 * @author wangchaojie　2018年1月10日
 */

@Component
public class FileUtils {

	public static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

	@Value("${file.uploadFolder}")
	private String uploadFolder;

	@Value("${file.staticAccessPath}")
	private String staticAccessPath;

	/***
	 *
	 * @Title: FileUtils.java
	 * @Description: 上传到项目指定路径下
	 * @param: MultipartFile file
	 * @return: Map<String, Object>
	 * @throws: Exception JSONArray
	 * @author: Yang
	 * @date: 2019年3月22日
	 */
	public Map<String, Object> fileUploadToLocal(MultipartFile file) {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		File upload = null;
		try {
			//获取文件名
			String fileName = file.getOriginalFilename();

			//获取文件后缀
			String fileSuffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());

			//文件重命名
			String fileEnd = System.currentTimeMillis() + "";
			String uploadFileName = fileEnd + fileSuffix;

			//创建文件
			String uploadFileStr = uploadFolder + "/" + uploadFileName;
			File uploadFile = new File(uploadFileStr);
			if(!uploadFile.exists()){
				uploadFile.getParentFile().mkdir();
				//创建文件
				uploadFile.createNewFile();
			}

			//上传
			file.transferTo(uploadFile);

			jsonMap.put("success", true);
			//上传前文件名称
			jsonMap.put("accessName", uploadFileName);
			//绝对路径
			jsonMap.put("realPath", uploadFolder);
			//虚拟路径
			jsonMap.put("accessUrl", staticAccessPath.replace("**", "")+uploadFileName);
		} catch (Exception e) {
			jsonMap.put("success", false);
			jsonMap.put("error",e.getMessage());
			e.printStackTrace();
			logger.error("上传文件异常",e);
		}
		return jsonMap;
	}
    
}
