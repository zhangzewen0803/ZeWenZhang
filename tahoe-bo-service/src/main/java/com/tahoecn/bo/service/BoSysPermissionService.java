package com.tahoecn.bo.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.BoSysPermission;
import com.tahoecn.bo.model.vo.DataPrivVO;

/**
 * <p>
 * 系统权限表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoSysPermissionService extends IService<BoSysPermission> {

    /**
     * @Title: getSysPrivList 
     * @Description: 获取系统权限
     * @param userName
     * @return List<BoSysPermission>
     * @author liyongxu
     * @date 2019年5月28日 下午9:06:38 
    */
    List<BoSysPermission> getSysPrivList(String userName);

    /**
     * @Title: getUserDataPrivList 
     * @Description: 获取数据权限
     * @param userName
     * @return List<DataPrivVO>
     * @author liyongxu
     * @date 2019年5月29日 下午3:51:59 
    */
    List<DataPrivVO> getUserDataPrivList(String userName);
    
}
