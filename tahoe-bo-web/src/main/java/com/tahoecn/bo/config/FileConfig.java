/*
package com.tahoecn.bo.config;

import com.tahoecn.bo.common.utils.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.FileNotFoundException;

@Configuration
public class FileConfig {

	@Value("${file.uploadFolder}")
	private String uploadFolder;

	@Bean
	MultipartConfigElement multipartConfigElement() throws FileNotFoundException {
		*/
/*//*
/*File filepath = new File(ResourceUtils.getURL("classpath:").getPath());
		if(!filepath.exists()) filepath = new File("");
		String absolutePath = filepath.getAbsolutePath();
		String url = absolutePath + uploadFolder;
		Constants.UPLOAD_FOLDER = url;
		File uploadFile = new File(url);
		if(!uploadFile.exists()){
			uploadFile.getParentFile().mkdir();
			uploadFile.mkdir();
		}*//*


		String url = uploadFolder;
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setLocation(url);
	return factory.createMultipartConfig();
    }

}
*/
