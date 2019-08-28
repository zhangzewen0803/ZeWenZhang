package com.tahoecn.bo.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.UcOrg;
import com.tahoecn.uc.vo.UcV3OrgListResultVO;

/**
 * <p>
 * 同步UC组织架构表                                          服务类
 * </p>
 *
 * @author panglx
 * @since 20190527
 */
public interface UcOrgService extends IService<UcOrg> {

    /**
     * @param allOrgList void
     * @Title: batchSaveOrgList
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author liyongxu
     * @date 2019年5月27日 上午11:35:18
     */
    void batchSaveOrgList(List<UcV3OrgListResultVO> allOrgList);

    /**
     * @Title: removeOrg
     * @Description: TODO(这里用一句话描述这个方法的作用) void
     * @author liyongxu
     * @date 2019年5月27日 下午5:37:48
     */
    void removeOrg();

    /**
     * @param dataSid void
     * @Title: deleteByFdSid
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author liyongxu
     * @date 2019年5月28日 上午11:31:13
     */
    void deleteByFdSid(String fdSid);

    /**
     * @param data void
     * @Title: saveOrUpdate
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author liyongxu
     * @date 2019年5月28日 下午2:13:24
     */
    void saveOrUpdateUcOrg(UcOrg ucOrgData);

    /**
     * @return List<UcOrg>
     * @Title: getOrgInfo
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author liyongxu
     * @date 2019年6月13日 上午11:42:10
     */
    List<UcOrg> getPowerProjectDataInfo(String fdSid);

    /**
     * @param projectId
     * @return List<UcOrg>
     * @Title: getProjectSubInfo
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author liyongxu
     * @date 2019年6月13日 下午2:08:54
     */
    List<UcOrg> getProjectSubInfo(String projectId);

    List<UcOrg> getSubProjectDataInfo();


    /**
     * 查指定 fdSid 之下的所有子数据 仅项目/分期
     *
     * @param fdSid
     * @return
     */
    List<UcOrg> getChildrenProjectAndSubByFdSid(String fdSid);


    /**
     * 查指定 fdSid 之下的所有子数据
     *
     * @param fdSid
     * @return
     */
    List<UcOrg> getChildrenByFdSid(String fdSid);
}
