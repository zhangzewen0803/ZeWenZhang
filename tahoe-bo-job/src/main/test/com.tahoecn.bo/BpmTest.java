package com.tahoecn.bo;

import com.tahoecn.bo.model.dto.BpmReviewDto;
import com.tahoecn.bo.service.BpmReviewService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BpmTest {

    @Autowired
    private BpmReviewService bpmReviewService;


    @Test
    public void newProcessTest(){
        BpmReviewDto bpmReviewDto = new BpmReviewDto();
        bpmReviewDto.setDocSubject("测试项目指标审批");
        bpmReviewDto.setDocCreator("xujianming");
        Map<String, Object> maps = new HashMap<String, Object>();


        // maps 中存放表单数据  例如：
        bpmReviewDto.setFormValues(maps);
        bpmReviewService.newProcess(bpmReviewDto, null);
    }
    
    /**
     * jsonResult {"code":0}
     * java.lang.ClassCastException: java.lang.String cannot be cast to java.util.Map
	at com.tahoecn.bpm.BpmClient.updateFormData(BpmClient.java:304)
     */
    @Test
    public void updateFormDataTest(){
    	BpmReviewDto bpmReviewDto = new BpmReviewDto();
		bpmReviewDto.setProcessId("16b2156f643ec71c1d1120343c3a2e65");
		bpmReviewDto.setDocSubject("编辑测试项目指标审批");
		bpmReviewDto.setDocCreator("xujianming");
		bpmReviewService.updateFormData(bpmReviewDto);
    }
    
    /**
     * jsonResult {"code":0}
     * java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Boolean
		at com.tahoecn.bpm.BpmClient.backProcess(BpmClient.java:343)
     */
    @Test
    public void backProcessTest(){
    	String processId = "16b215af9cb698df7f456f74b0d96118";
		bpmReviewService.backProcess(processId);
    }
    
    /**
     * jsonResult {"code":604,"msg":"只有草稿和驳回的单据才可以废弃！"}
     */
    @Test
    public void dropProcessTest(){
    	String processId = "16b215af9cb698df7f456f74b0d96118";
		bpmReviewService.dropProcess(processId);
    }
    
}
