package com.tahoecn.bo.model.vo;

/***
 * 
 * @ClassName：MonitorVo
 * @Description：调度任务监控接口Vo
 * @author matianzhi 
 * @date 2018年12月11日 下午1:53:35 
 * @version 1.0.0
 */
public class MonitorVo {
	
	private String jobResult;  //调度任务执行结果（0成功1失败）
	
	private String jobName;     //调度任务名称

	public String getJobResult() {
		return jobResult;
	}

	public void setJobResult(String jobResult) {
		this.jobResult = jobResult;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

}
