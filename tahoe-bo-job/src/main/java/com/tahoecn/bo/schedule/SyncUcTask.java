package com.tahoecn.bo.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tahoecn.bo.common.utils.DateUtils;
import com.tahoecn.bo.service.UcOrgService;
import com.tahoecn.bo.service.UcUserService;
import com.tahoecn.uc.UcClient;
import com.tahoecn.uc.exception.UcException;
import com.tahoecn.uc.request.UcV3OrgListRequest;
import com.tahoecn.uc.request.UcV3UserListRequest;
import com.tahoecn.uc.response.UcV3OrgListResponse;
import com.tahoecn.uc.response.UcV3UserListResponse;
import com.tahoecn.uc.vo.UcV3OrgListResultVO;
import com.tahoecn.uc.vo.UcV3UserListResultVO;

/**
 * UC系统数据同步任务
 */
@Component
public class SyncUcTask {

	@Autowired
	private UcUserService ucUserService;
	
	@Autowired
	private UcOrgService ucOrgService;
	
    private final Integer UC_PAGE_NO = 1;

    private final Integer UC_PAGE_SIZE = 50;

    /**
     * 从UC系统-同步组织架构数据
     */
//    @Scheduled(cron = "0 0 2 * * ?")
    public void syncUcOrgData() {

        String fromTime ="1970-01-01 00:00:00";
        String toTime=DateUtils.dateToString(new Date(), DateUtils.DATE_FULL_STR);

        UcV3OrgListRequest ucV3OrgListRequest = new UcV3OrgListRequest();
        ucV3OrgListRequest.setFromTime(fromTime);
        ucV3OrgListRequest.setToTime(toTime);
        ucV3OrgListRequest.setPageNo(UC_PAGE_NO);
        ucV3OrgListRequest.setPageSize(UC_PAGE_SIZE);

        try {
            List<UcV3OrgListResultVO> allOrgList  = new ArrayList<>();

            UcV3OrgListResponse ucV3OrgListResponse = UcClient.v3OrgList(ucV3OrgListRequest);
            List<UcV3OrgListResultVO> orgPageList = ucV3OrgListResponse.getResult();

            Integer totalPages = ucV3OrgListResponse.getTotalPages();

            if(orgPageList!=null && orgPageList.size()>0){
                allOrgList.addAll(orgPageList);
            }
            if(totalPages > UC_PAGE_NO){
               for(int i = UC_PAGE_NO + 1; i <= totalPages; i++){
                   ucV3OrgListRequest.setPageNo(i);
                   UcV3OrgListResponse orgListResp = UcClient.v3OrgList(ucV3OrgListRequest);
                   List<UcV3OrgListResultVO> orgListResult = orgListResp.getResult();
                   allOrgList.addAll(orgListResult);
               }
            }
            System.out.println("allOrgSize:"+allOrgList.size());
            //删除组织机构数据
            ucOrgService.removeOrg();
            //插入组织机构数据
            ucOrgService.batchSaveOrgList(allOrgList);
        } catch (UcException e) {
            //请求失败（网络异常或UC返回code不为0），捕获异常信息。
            String message = e.getMessage();
            System.out.println(message);
        }
    }

    /**
     * 从UC系统-同步用户数据
     */
//	@Scheduled(cron = "0 0 2 * * ?")
    public void syncUcUserData() {

        String fromTime ="1970-01-01 00:00:00";
        String toTime= DateUtils.dateToString(new Date(), DateUtils.DATE_FULL_STR);

        UcV3UserListRequest ucV3UserListRequest = new UcV3UserListRequest();
        ucV3UserListRequest.setFromTime(fromTime);
        ucV3UserListRequest.setToTime(toTime);
        ucV3UserListRequest.setPageNo(UC_PAGE_NO);
        ucV3UserListRequest.setPageSize(UC_PAGE_SIZE);
        try {

            List<UcV3UserListResultVO> allUserList  = new ArrayList<>();

            UcV3UserListResponse ucV3UserListResponse = UcClient.v3UserList(ucV3UserListRequest);
            List<UcV3UserListResultVO> ucV3UserList = ucV3UserListResponse.getResult();
            System.out.println(ucV3UserList.size());
            System.out.println(ucV3UserListResponse.getTotalPages());

            if(ucV3UserList!=null && ucV3UserList.size()>0){
                allUserList.addAll(ucV3UserList);
            }
            Integer totalPages = ucV3UserListResponse.getTotalPages();
            if(totalPages > UC_PAGE_NO){
                for(int i = UC_PAGE_NO + 1; i <= totalPages; i++){
                    ucV3UserListRequest.setPageNo(i);
                    UcV3UserListResponse ucUserListResp = UcClient.v3UserList(ucV3UserListRequest);
                    List<UcV3UserListResultVO> ucUserList = ucUserListResp.getResult();
                    allUserList.addAll(ucUserList);
                }
            }
            System.out.println("allUserListSize:"+allUserList.size());
            //删除用户
            ucUserService.removeUser();
            //同步用户数据入库
            ucUserService.batchSaveUserList(allUserList);
        } catch (UcException e) {
            //请求失败（网络异常或UC返回code不为0），捕获异常信息。
            String message = e.getMessage();
            System.out.println(message);
        }
    }

}
