package com.tahoecn.bo.service.impl;

import com.tahoecn.bo.model.entity.BoSysLog;
import com.tahoecn.bo.mapper.BoSysLogMapper;
import com.tahoecn.bo.service.BoSysLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统日志表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class BoSysLogServiceImpl extends ServiceImpl<BoSysLogMapper, BoSysLog> implements BoSysLogService {

	/* (non-Javadoc)
	 * @see com.tahoecn.bo.service.BoSysLogService#getBoSysLog(java.lang.String, java.lang.String)
	 */
	@Override
	public BoSysLog getBoSysLog(String string, String string2) {
		// TODO Auto-generated method stub
		return null;
	}

}
