package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.BoBuildingSpeed;
import com.tahoecn.bo.model.vo.CurrentUserVO;
import com.tahoecn.bo.model.vo.ProjectSpeedUpdateReqParam;
import com.tahoecn.core.json.JSONResult;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 楼栋信息填报表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-06-28
 */
public interface BoBuildingSpeedService extends IService<BoBuildingSpeed> {

    JSONResult getProjectSpeedInfo(String projectId, String speedTime, JSONResult jsonResult) throws Exception;

    JSONResult updateProjectSpeedInfo(ProjectSpeedUpdateReqParam projectSpeedUpdateReqParam, CurrentUserVO userVO, JSONResult jsonResult)  throws Exception;

    /**
     * 根据楼栋origin_id查询对应截止时间最新的已开工楼栋记录
     * @param buildingOriginIds
     * @param endTime
     * @return
     */
    List<BoBuildingSpeed> getLastStartedBuildingSpeed(Collection<String> buildingOriginIds, LocalDateTime endTime);

    /**
     * 根据楼栋origin_id查询对应截止时间最新的楼栋记录
     * @param buildingOriginIds
     * @param endTime
     * @return
     */
    List<BoBuildingSpeed> getLastBuildingSpeed(Collection<String> buildingOriginIds, LocalDateTime endTime);

}
