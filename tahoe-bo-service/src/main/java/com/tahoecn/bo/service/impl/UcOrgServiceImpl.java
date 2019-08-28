package com.tahoecn.bo.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.enums.OrgTypeCodeEnum;
import com.tahoecn.bo.common.utils.Constants;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.mapper.UcOrgMapper;
import com.tahoecn.bo.model.entity.UcOrg;
import com.tahoecn.bo.service.UcOrgService;
import com.tahoecn.uc.vo.UcV3OrgListResultVO;

/**
 * @author liyongxu
 * @version 1.0.0
 * @ClassName：UcOrgServiceImpl
 * @Description：同步UC组织架构表
 * @date 2019年5月27日 下午3:24:13
 */
@Service
public class UcOrgServiceImpl extends ServiceImpl<UcOrgMapper, UcOrg> implements UcOrgService {

    @Autowired
    private UcOrgMapper ucOrgMapper;

    @Override
    @Transactional
    public void removeOrg() {
        ucOrgMapper.removeOrg();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveOrgList(List<UcV3OrgListResultVO> allOrgList) {
        List<UcOrg> ucOrgList = new ArrayList<UcOrg>();
        for (UcV3OrgListResultVO ucOrgListResultVO : allOrgList) {
            UcOrg ucOrg = new UcOrg();
            ucOrg.setId(UUIDUtils.create());
            ucOrg.setFdSid(ucOrgListResultVO.getFdSid());
            ucOrg.setFdName(ucOrgListResultVO.getFdName());
            ucOrg.setFdCode(ucOrgListResultVO.getFdCode());
            ucOrg.setFdName(ucOrgListResultVO.getFdName());
            ucOrg.setFdNameTree(ucOrgListResultVO.getFdNameTree());
            ucOrg.setFdType(ucOrgListResultVO.getFdType());
            ucOrg.setFdPsid(ucOrgListResultVO.getFdPsid());
            ucOrg.setFdPname(ucOrgListResultVO.getFdPname());
            ucOrg.setFdSidTree(ucOrgListResultVO.getFdSidTree());
            ucOrg.setFdOrder(ucOrgListResultVO.getFdOrder());
            ucOrg.setFdAvailable(ucOrgListResultVO.getFdAvailable());
            ucOrg.setFdIsdelete(ucOrgListResultVO.getFdIsdelete());
            ucOrg.setCreateTime(LocalDateTime.now());

            ucOrgList.add(ucOrg);
        }
        ucOrgMapper.batchSaveOrgList(ucOrgList);
    }

    @Override
    @Transactional
    public void deleteByFdSid(String fdSid) {
        UcOrg ucOrgTest = ucOrgMapper.findByFdSid(fdSid);
        if (ucOrgTest != null) {
            ucOrgTest.setFdIsdelete(Constants.DELETE);
            ucOrgTest.setFdAvailable(Constants.NOAVAILABLE);
            ucOrgMapper.updateById(ucOrgTest);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateUcOrg(UcOrg ucOrg) {
        UcOrg ucOrgTest = ucOrgMapper.findByFdSid(ucOrg.getFdSid());
        if (ucOrgTest == null) {
            ucOrg.setId(UUIDUtils.create());
            ucOrgMapper.insert(ucOrg);
        } else {
            ucOrg.setId(ucOrgTest.getId());
            ucOrgMapper.updateById(ucOrg);
        }
    }

    @Override
    public List<UcOrg> getPowerProjectDataInfo(String fdSid) {
        List<UcOrg> list = ucOrgMapper.selectOrgList(fdSid);
        return list;
    }

    @Override
    public List<UcOrg> getProjectSubInfo(String projectId) {
        return ucOrgMapper.selectProjectSubInfo(projectId);
    }

    @Override
    public List<UcOrg> getSubProjectDataInfo() {
        return ucOrgMapper.getSubProjectDataInfo();
    }

    @Override
    public List<UcOrg> getChildrenProjectAndSubByFdSid(String fdSid) {
        QueryWrapper<UcOrg> ucOrgQueryWrapper = new QueryWrapper<>();
        ucOrgQueryWrapper.eq("fd_sid", fdSid);
        UcOrg one = getOne(ucOrgQueryWrapper);
        if (one != null) {
            ucOrgQueryWrapper = new QueryWrapper<>();
            ucOrgQueryWrapper.likeRight("fd_sid_tree", one.getFdSidTree())
                    .in("fd_type", Stream.of(OrgTypeCodeEnum.UC_LAND_PROJECT.getCode(),OrgTypeCodeEnum.UC_LAND_PROJECT_SUB.getCode()).collect(Collectors.toList()))
                    .eq("fd_isdelete",-1)
                    .eq("fd_available",1);
            return list(ucOrgQueryWrapper);
        }
        return new ArrayList<>();
    }

    @Override
    public List<UcOrg> getChildrenByFdSid(String fdSid) {
        QueryWrapper<UcOrg> ucOrgQueryWrapper = new QueryWrapper<>();
        ucOrgQueryWrapper.eq("fd_sid", fdSid);
        UcOrg one = getOne(ucOrgQueryWrapper);
        if (one != null) {
            ucOrgQueryWrapper = new QueryWrapper<>();
            ucOrgQueryWrapper.likeRight("fd_sid_tree", one.getFdSidTree())
                    .in("fd_type", Arrays.stream(OrgTypeCodeEnum.values()).map(x->x.getCode()).collect(Collectors.toList()))
                    .eq("fd_isdelete",-1)
                    .eq("fd_available",1);
            return list(ucOrgQueryWrapper);
        }
        return new ArrayList<>();
    }

}
