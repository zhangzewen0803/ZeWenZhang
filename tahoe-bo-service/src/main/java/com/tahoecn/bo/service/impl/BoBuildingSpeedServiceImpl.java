package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tahoecn.bo.common.constants.RegexConstant;
import com.tahoecn.bo.common.enums.*;
import com.tahoecn.bo.common.utils.DateUtils;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.mapper.BoBuildingMapper;
import com.tahoecn.bo.mapper.BoProjectQuotaExtendMapper;
import com.tahoecn.bo.model.dto.BoBuildingSpeedExtendDto;
import com.tahoecn.bo.model.entity.*;
import com.tahoecn.bo.mapper.BoBuildingSpeedMapper;
import com.tahoecn.bo.model.vo.CurrentUserVO;
import com.tahoecn.bo.model.vo.ProjectSpeedUpdateReqParam;
import com.tahoecn.bo.model.vo.ProjectSpeedVo;
import com.tahoecn.bo.model.vo.StageStatusVO;
import com.tahoecn.bo.service.BoBuildingSpeedService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.core.json.JSONResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * <p>
 * 楼栋信息填报表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-06-28
 */
@Service
public class BoBuildingSpeedServiceImpl extends ServiceImpl<BoBuildingSpeedMapper, BoBuildingSpeed> implements BoBuildingSpeedService {

    @Autowired
    private BoProjectQuotaExtendMapper boProjectQuotaExtendMapper;

    @Autowired
    private BoBuildingSpeedMapper boBuildingSpeedMapper;

    @Autowired
    private BoBuildingMapper boBuildingMapper;

    @Override
    public JSONResult getProjectSpeedInfo(String projectId, String speedTime, JSONResult jsonResult) {

        jsonResult.setCode(CodeEnum.ERROR.getKey());
        jsonResult.setMsg(CodeEnum.ERROR.getValue());

        ProjectSpeedUpdateReqParam projectSpeedUpdateReqParam = new ProjectSpeedUpdateReqParam();
        projectSpeedUpdateReqParam.setProjectId(projectId);

        List<ProjectSpeedVo> projectSpeedVoList = new ArrayList<ProjectSpeedVo>();
        DateFormat df = new SimpleDateFormat("yyyy-MM");
        String formatData = df.format(new Date());
        projectSpeedUpdateReqParam.setSpeedTime(formatData);
        if (formatData.equals(speedTime)) {
            List<BoBuilding> boBuildings = new ArrayList<BoBuilding>();
            BoProjectQuotaExtend boProjectQuotaExtend = boProjectQuotaExtendMapper.selectNewInfoByProjectId(projectId, StageCodeEnum.STAGE_01.getKey());
            if (boProjectQuotaExtend != null) {
                QueryWrapper<BoBuilding> queryBuildWrapper = new QueryWrapper<BoBuilding>();
                queryBuildWrapper.eq("project_quota_extend_id", boProjectQuotaExtend.getId())
                        .eq("is_delete", IsDeleteEnum.NO.getKey())
                        .eq("is_disable", IsDisableEnum.NO.getKey());
                boBuildings = boBuildingMapper.selectList(queryBuildWrapper);
            }

            Map<String, BoBuildingSpeedExtendDto> boBuildingSpeedExtendDtoMap = new HashMap<String, BoBuildingSpeedExtendDto>();
            List<BoBuildingSpeedExtendDto> BoBuildingSpeedDtoList = boBuildingSpeedMapper.selectInfoBySpeedTimeAndProjectId(projectId, speedTime);
            if (CollectionUtils.isNotEmpty(BoBuildingSpeedDtoList)) {
                for (int i = 0; i < BoBuildingSpeedDtoList.size(); i++) {
                    BoBuildingSpeedExtendDto boBuildingSpeedExtendDto = BoBuildingSpeedDtoList.get(i);
                    boBuildingSpeedExtendDtoMap.put(boBuildingSpeedExtendDto.getOriginId(), boBuildingSpeedExtendDto);
                }
            }

            if (CollectionUtils.isNotEmpty(boBuildings)) {
                for (int i = 0; i < boBuildings.size(); i++) {
                    BoBuilding boBuilding = boBuildings.get(i);
                    ProjectSpeedVo psv = null;
                    if (CollectionUtils.isNotEmpty(BoBuildingSpeedDtoList)) {
                        for (int j = 0; j < BoBuildingSpeedDtoList.size(); j++) {
                            BoBuildingSpeedExtendDto boBuildingSpeedExtendDto = BoBuildingSpeedDtoList.get(j);
                            if (boBuildingSpeedExtendDto.getBuildId().equals(boBuilding.getOriginId())) {
                                psv = new ProjectSpeedVo();
                                psv.setBuildId(boBuildingSpeedExtendDto.getBuildId());
                                psv.setId(boBuildingSpeedExtendDto.getId());
                                psv.setBuildName(boBuildingSpeedExtendDto.getBuildName());
                                psv.setCompleteNumber(boBuildingSpeedExtendDto.getCompleteNumber() == null ? "" : boBuildingSpeedExtendDto.getCompleteNumber());
                                psv.setCurrentStart(boBuildingSpeedExtendDto.getCurrentStart() == null ? 0 : boBuildingSpeedExtendDto.getCurrentStart());
                                psv.setIsLoan(boBuildingSpeedExtendDto.getIsLoan() == null ? 0 : boBuildingSpeedExtendDto.getIsLoan());
                                psv.setIsRequirements(boBuildingSpeedExtendDto.getIsRequirements() == null ? 0 : boBuildingSpeedExtendDto.getIsRequirements());
                                psv.setRequirements(boBuildingSpeedExtendDto.getRequirements() == null ? "" : boBuildingSpeedExtendDto.getRequirements());
                                psv.setIsSale(boBuildingSpeedExtendDto.getIsSale() == null ? 0 : boBuildingSpeedExtendDto.getIsSale());
                                psv.setIsLoan(boBuildingSpeedExtendDto.getIsLoan() == null ? 0 : boBuildingSpeedExtendDto.getIsLoan());
                                if (boBuildingSpeedExtendDto.getStartTime() != null) {
                                    psv.setStartTime(boBuildingSpeedExtendDto.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T", " "));
                                } else {
                                    psv.setStartTime("");
                                }

                                psv.setIsStart(boBuildingSpeedExtendDto.getIsStart() == null ? 0 : boBuildingSpeedExtendDto.getIsStart());
                            }

                            if (j == BoBuildingSpeedDtoList.size() - 1) {
                                if (psv == null) {
                                    BoBuildingSpeedExtendDto boBuildingSpeedExtendDto1 = boBuildingSpeedExtendDtoMap.get(boBuilding.getOriginId());
                                    if (boBuildingSpeedExtendDto1 != null) {
                                        psv = new ProjectSpeedVo();
                                        psv.setBuildId(boBuildingSpeedExtendDto1.getBuildId());
                                        psv.setBuildName(boBuildingSpeedExtendDto1.getBuildName());
                                        psv.setCompleteNumber(boBuildingSpeedExtendDto1.getCompleteNumber() == null ? "" : boBuildingSpeedExtendDto1.getCompleteNumber());
                                        psv.setCurrentStart(boBuildingSpeedExtendDto1.getCurrentStart() == null ? 0 : boBuildingSpeedExtendDto1.getCurrentStart());
                                        psv.setIsLoan(boBuildingSpeedExtendDto1.getIsLoan() == null ? 0 : boBuildingSpeedExtendDto1.getIsLoan());
                                        psv.setRequirements(boBuildingSpeedExtendDto1.getRequirements() == null ? "" : boBuildingSpeedExtendDto1.getRequirements());
                                        psv.setIsRequirements(boBuildingSpeedExtendDto1.getIsRequirements() == null ? 0 : boBuildingSpeedExtendDto1.getIsRequirements());
                                        psv.setIsSale(boBuildingSpeedExtendDto1.getIsSale() == null ? 0 : boBuildingSpeedExtendDto1.getIsSale());
                                        psv.setIsLoan(boBuildingSpeedExtendDto1.getIsLoan() == null ? 0 : boBuildingSpeedExtendDto1.getIsLoan());
                                        if (boBuildingSpeedExtendDto1.getStartTime() != null) {
                                            psv.setStartTime(boBuildingSpeedExtendDto1.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T", " "));
                                        } else {
                                            psv.setStartTime("");
                                        }
                                        psv.setIsStart(boBuildingSpeedExtendDto1.getIsStart() == null ? 0 : boBuildingSpeedExtendDto1.getIsStart());
                                        psv.setRequirements(boBuildingSpeedExtendDto1.getRequirements());
                                    } else {
                                        psv = new ProjectSpeedVo();
                                        psv.setBuildId(boBuilding.getOriginId());
                                        psv.setBuildName(boBuilding.getName());
                                        psv.setRequirements("");
                                        psv.setCompleteNumber("");
                                        psv.setCurrentStart(0);
                                        psv.setIsLoan(0);
                                        psv.setIsRequirements(0);
                                        psv.setIsSale(0);
                                        psv.setIsLoan(0);
                                        psv.setIsStart(0);
                                        psv.setStartTime("");
                                    }
                                }
                            }
                        }
                        projectSpeedVoList.add(psv);
                    } else {
                        psv = new ProjectSpeedVo();
                        psv.setBuildId(boBuilding.getOriginId());
                        psv.setBuildName(boBuilding.getName());
                        psv.setCompleteNumber("");
                        psv.setRequirements("");
                        psv.setCurrentStart(0);
                        psv.setIsLoan(0);
                        psv.setIsRequirements(0);
                        psv.setIsSale(0);
                        psv.setIsLoan(0);
                        psv.setIsStart(0);
                        psv.setStartTime("");
                        projectSpeedVoList.add(psv);
                    }
                }
            }

            //楼栋排序
            projectSpeedVoList.sort((a, b) -> {
                String aName = a.getBuildName();
                String bName = b.getBuildName();
                Matcher aMatcher = RegexConstant.BUILDING_NAME_PATTERN.matcher(aName);
                Integer aNo = null;
                if (aMatcher.find()) {
                    aNo = Integer.valueOf(aMatcher.group(1));
                }
                Matcher bMatcher = RegexConstant.BUILDING_NAME_PATTERN.matcher(bName);
                Integer bNo = null;
                if (bMatcher.find()) {
                    bNo = Integer.valueOf(bMatcher.group(1));
                }
                if (aNo == null && bNo == null) {
                    return aName.compareTo(bName);
                }

                return aNo == null ? 1 : bNo == null ? -1 : aNo - bNo;
            });

            projectSpeedUpdateReqParam.setProjectSpeedVoList(projectSpeedVoList);
            jsonResult.setData(projectSpeedUpdateReqParam);
            jsonResult.setCode(CodeEnum.SUCCESS.getKey());
            jsonResult.setMsg(CodeEnum.SUCCESS.getValue());

            return jsonResult;
        } else {
            List<BoBuildingSpeedExtendDto> BoBuildingSpeedList = boBuildingSpeedMapper.selectInfoBySpeedTimeAndProjectId(projectId, speedTime);
            if (CollectionUtils.isNotEmpty(BoBuildingSpeedList)) {
                for (BoBuildingSpeedExtendDto buildingSpeedExtendDto : BoBuildingSpeedList) {
                    ProjectSpeedVo psv = new ProjectSpeedVo();
                    psv.setId(buildingSpeedExtendDto.getId());
                    psv.setBuildName(buildingSpeedExtendDto.getBuildName());
                    psv.setCompleteNumber(buildingSpeedExtendDto.getCompleteNumber() == null ? "" : buildingSpeedExtendDto.getCompleteNumber());
                    psv.setCurrentStart(buildingSpeedExtendDto.getCurrentStart() == null ? 0 : buildingSpeedExtendDto.getCurrentStart());
                    psv.setIsLoan(buildingSpeedExtendDto.getIsLoan() == null ? 0 : buildingSpeedExtendDto.getIsLoan());
                    psv.setIsRequirements(buildingSpeedExtendDto.getIsRequirements() == null ? 0 : buildingSpeedExtendDto.getIsRequirements());
                    psv.setIsSale(buildingSpeedExtendDto.getIsSale() == null ? 0 : buildingSpeedExtendDto.getIsSale());
                    psv.setIsLoan(buildingSpeedExtendDto.getIsLoan() == null ? 0 : buildingSpeedExtendDto.getIsLoan());
                    if (buildingSpeedExtendDto.getStartTime() != null) {
                        psv.setStartTime(buildingSpeedExtendDto.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T", " "));
                    } else {
                        psv.setStartTime("");
                    }
                    psv.setIsStart(buildingSpeedExtendDto.getIsStart() == null ? 0 : buildingSpeedExtendDto.getIsStart());
                    psv.setRequirements(buildingSpeedExtendDto.getRequirements());
                    projectSpeedVoList.add(psv);
                }
            }

            //楼栋排序
            projectSpeedVoList.sort((a, b) -> {
                String aName = a.getBuildName();
                String bName = b.getBuildName();
                Matcher aMatcher = RegexConstant.BUILDING_NAME_PATTERN.matcher(aName);
                Integer aNo = null;
                if (aMatcher.find()) {
                    aNo = Integer.valueOf(aMatcher.group(1));
                }
                Matcher bMatcher = RegexConstant.BUILDING_NAME_PATTERN.matcher(bName);
                Integer bNo = null;
                if (bMatcher.find()) {
                    bNo = Integer.valueOf(bMatcher.group(1));
                }
                if (aNo == null && bNo == null) {
                    return aName.compareTo(bName);
                }

                return aNo == null ? 1 : bNo == null ? -1 : aNo - bNo;
            });

            projectSpeedUpdateReqParam.setProjectSpeedVoList(projectSpeedVoList);
            jsonResult.setData(projectSpeedUpdateReqParam);
            jsonResult.setCode(CodeEnum.SUCCESS.getKey());
            jsonResult.setMsg(CodeEnum.SUCCESS.getValue());

            return jsonResult;
        }
    }

    @Override
    @Transactional
    public JSONResult updateProjectSpeedInfo(ProjectSpeedUpdateReqParam projectSpeedUpdateReqParam, CurrentUserVO userVO, JSONResult jsonResult) throws Exception {

        String speedTime = projectSpeedUpdateReqParam.getSpeedTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM");
        String formatData = df.format(new Date());
        if (formatData.equals(speedTime)) {

            List<ProjectSpeedVo> projectSpeedVoList = projectSpeedUpdateReqParam.getProjectSpeedVoList();
            List<String> projectSpeedIdList = new ArrayList<String>();
            for (ProjectSpeedVo projectSpeedVo : projectSpeedVoList) {
                String id = projectSpeedVo.getId();
                if (StringUtils.isNotBlank(id)) {
                    projectSpeedIdList.add(id);
                }
            }

            if (CollectionUtils.isNotEmpty(projectSpeedIdList)) {
                boBuildingSpeedMapper.deleteInfoByProjectIdsAndTime(projectSpeedUpdateReqParam.getProjectId(),
                        projectSpeedUpdateReqParam.getSpeedTime(), projectSpeedIdList, IsDeleteEnum.YES.getKey());
            }

            for (int i = 0; i < projectSpeedVoList.size(); i++) {
                ProjectSpeedVo projectSpeedVo = projectSpeedVoList.get(i);
                String id = projectSpeedVo.getId();
                if (StringUtils.isNotBlank(id)) {
                    BoBuildingSpeed updateBuildingSpeed = new BoBuildingSpeed();
                    updateBuildingSpeed.setId(projectSpeedVo.getId());
                    updateBuildingSpeed.setBuildId(projectSpeedVo.getBuildId());
                    if (StringUtils.isNotBlank(projectSpeedVo.getStartTime())) {
                        updateBuildingSpeed.setStartTime(LocalDateTime.parse(projectSpeedVo.getStartTime(), DateTimeFormatter.ofPattern(DateUtils.DATE_FULL_STR)));
                        updateBuildingSpeed.setIsStart(1);
                    } else {
                        updateBuildingSpeed.setStartTime(null);
                        updateBuildingSpeed.setIsStart(0);
                    }
                    updateBuildingSpeed.setRequirements(projectSpeedVo.getRequirements());
                    updateBuildingSpeed.setCurrentStart(projectSpeedVo.getCurrentStart());
                    updateBuildingSpeed.setCompleteNumber(projectSpeedVo.getCompleteNumber());
                    try {
                        if (StringUtils.isNotBlank(projectSpeedVo.getRequirements()) && StringUtils.isNotBlank(projectSpeedVo.getCompleteNumber())) {
                            double sub = sub(projectSpeedVo.getCompleteNumber(), projectSpeedVo.getRequirements());
                            if (sub >= 0d) {
                                updateBuildingSpeed.setIsRequirements(1);
                            } else {
                                updateBuildingSpeed.setIsRequirements(0);
                            }
                        } else {
                            updateBuildingSpeed.setIsRequirements(0);
                        }
                    } catch (Exception e) {
                        updateBuildingSpeed.setIsRequirements(0);
                    }
                    updateBuildingSpeed.setIsSale(projectSpeedVo.getIsSale());
                    updateBuildingSpeed.setIsLoan(projectSpeedVo.getIsLoan());
                    updateBuildingSpeed.setUpdaterId(userVO.getId());
                    updateBuildingSpeed.setUpdaterName(userVO.getName());
                    updateBuildingSpeed.setUpdateTime(LocalDateTime.now());
                    boBuildingSpeedMapper.updateSpeedId(updateBuildingSpeed);
                } else {
                    BoBuildingSpeed insertBuildingSpeed = new BoBuildingSpeed();
                    insertBuildingSpeed.setId(UUIDUtils.create());
                    insertBuildingSpeed.setBuildId(projectSpeedVo.getBuildId());
                    insertBuildingSpeed.setCompleteNumber(projectSpeedVo.getCompleteNumber());
                    insertBuildingSpeed.setIsStart(projectSpeedVo.getIsStart());
                    if (StringUtils.isNotBlank(projectSpeedVo.getStartTime())) {
                        insertBuildingSpeed.setStartTime(LocalDateTime.parse(projectSpeedVo.getStartTime(), DateTimeFormatter.ofPattern(DateUtils.DATE_FULL_STR)));
                        insertBuildingSpeed.setIsStart(1);
                    } else {
                        insertBuildingSpeed.setStartTime(null);
                        insertBuildingSpeed.setIsStart(0);
                    }
                    insertBuildingSpeed.setRequirements(projectSpeedVo.getRequirements());
                    insertBuildingSpeed.setCurrentStart(projectSpeedVo.getCurrentStart());
                    insertBuildingSpeed.setCompleteNumber(projectSpeedVo.getCompleteNumber());
                    try {
                        if (StringUtils.isNotBlank(projectSpeedVo.getRequirements()) && StringUtils.isNotBlank(projectSpeedVo.getCompleteNumber())) {
                            double sub = sub(projectSpeedVo.getCompleteNumber(), projectSpeedVo.getRequirements());
                            if (sub >= 0d) {
                                insertBuildingSpeed.setIsRequirements(1);
                            } else {
                                insertBuildingSpeed.setIsRequirements(0);
                            }
                        } else {
                            insertBuildingSpeed.setIsRequirements(0);
                        }
                    } catch (Exception e) {
                        insertBuildingSpeed.setIsRequirements(0);
                    }
                    insertBuildingSpeed.setIsSale(projectSpeedVo.getIsSale());
                    insertBuildingSpeed.setIsLoan(projectSpeedVo.getIsLoan());
                    insertBuildingSpeed.setProjectId(projectSpeedUpdateReqParam.getProjectId());
                    insertBuildingSpeed.setIsDelete(IsDeleteEnum.NO.getKey());
                    insertBuildingSpeed.setIsDisable(IsDisableEnum.NO.getKey());
                    insertBuildingSpeed.setCreaterId(userVO.getId());
                    insertBuildingSpeed.setCreaterName(userVO.getName());
                    insertBuildingSpeed.setCreateTime(LocalDateTime.now());
                    insertBuildingSpeed.setSpeedTime(LocalDateTime.now());
                    boBuildingSpeedMapper.insert(insertBuildingSpeed);
                }
            }

            jsonResult.setCode(CodeEnum.SUCCESS.getKey());
            jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
        } else {
            jsonResult.setCode(CodeEnum.PROJECT_SPEEDZ_UPDATE_EXTENDS.getKey());
            jsonResult.setMsg(CodeEnum.PROJECT_SPEEDZ_UPDATE_EXTENDS.getValue());
        }

        return jsonResult;
    }

    @Override
    public List<BoBuildingSpeed> getLastStartedBuildingSpeed(Collection<String> buildingOriginIds, LocalDateTime endTime) {
        QueryWrapper<BoBuildingSpeed> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .lt("start_time", endTime)
                .in("build_id", buildingOriginIds)
                .eq("is_start", IsStartEnum.YES.getKey())
                .apply("ifnull(update_time,create_time) = (select max(ifnull(update_time,create_time)) from bo_building_speed a where a.build_id=bo_building_speed.build_id and a.is_delete={0} and a.is_disable={1})", IsDeleteEnum.NO.getKey(), IsDisableEnum.NO.getKey());
        return list(queryWrapper);
    }

    @Override
    public List<BoBuildingSpeed> getLastBuildingSpeed(Collection<String> buildingOriginIds, LocalDateTime endTime) {
        QueryWrapper<BoBuildingSpeed> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .in("build_id", buildingOriginIds)
                .apply("ifnull(update_time,create_time) = (select max(ifnull(update_time,create_time)) from bo_building_speed a where a.build_id=bo_building_speed.build_id and a.is_delete={0} and a.is_disable={1} and ifnull(a.update_time,a.create_time) <= {2})", IsDeleteEnum.NO.getKey(), IsDisableEnum.NO.getKey(),endTime);
        return list(queryWrapper);
    }

    public double sub(String money, String price) {
        BigDecimal b1 = new BigDecimal(money).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal b2 = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP);
        double doubleValue = b1.subtract(b2).doubleValue();
        return doubleValue;
    }
}
