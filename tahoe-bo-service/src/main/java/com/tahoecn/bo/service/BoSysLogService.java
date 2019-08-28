package com.tahoecn.bo.service;

import com.tahoecn.bo.model.entity.BoSysLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统日志表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoSysLogService extends IService<BoSysLog> {

	/**
	 * @Title: getBoSysLog 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param string
	 * @param string2
	 * @return BoSysLog
	 * @author liyongxu
	 * @date 2019年5月28日 上午11:17:28 
	*/
	BoSysLog getBoSysLog(String string, String string2);

}
