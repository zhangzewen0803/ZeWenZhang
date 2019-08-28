package com.tahoecn.bo;

import com.tahoecn.bo.model.bo.BpmBusinessInfoBo;
import com.tahoecn.bo.service.BoProjectPriceExtendService;
import com.tahoecn.bo.service.BoProjectQuotaExtendService;
import com.tahoecn.bo.service.MdmProjectInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author panglx
 * @date 2019/6/13
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CommonTest {

    @Autowired
    private MdmProjectInfoService mdmProjectInfoService;

    @Autowired
    private BoProjectQuotaExtendService boProjectQuotaExtendService;

    @Autowired
    private BoProjectPriceExtendService boProjectPriceExtendService;

    @Test
    public void test0() {
//        List<MdmProjectInfo> list = mdmProjectInfoService.list();
//        UcV1OrgListRequest request = new UcV1OrgListRequest();
//        request.setOrgType("ORG3,ORG4,ORG4-1,ORG5-1,ORG5-2");
//        request.setReturnType(2);
//        try {
//            UcV1OrgListResponse ucV1OrgListResponse = UcClient.v1OrgList(request);
//            Map<String, UcV1OrgListResultVO> ucOrgMap = new ConcurrentHashMap<>();
//            List<UcV1OrgListResultVO> result = ucV1OrgListResponse.getResult();
//            result.parallelStream().forEach(x -> ucOrgMap.put(x.getFdSid(), x));
//            List<MdmProjectInfo> projectList = Collections.synchronizedList(new ArrayList<>());
//            list.parallelStream().forEach(x -> {
//                if (LevelTypeEnum.PROJECT.getKey().equals(x.getLevelType())) {
//                    //虚拟目录
//                    UcV1OrgListResultVO ucOrg = ucOrgMap.get(x.getParentSid());
//                    if (ucOrg != null) {
//                        //城市公司
//                        UcV1OrgListResultVO city = ucOrgMap.get(ucOrg.getFdPsid());
//                        if (city != null) {
//                            MdmProjectInfo mdmProjectInfo = new MdmProjectInfo();
//                            mdmProjectInfo.setSid(x.getSid());
//                            mdmProjectInfo.setCityCompanyId(city.getFdSid());
//                            mdmProjectInfo.setCityCompanyName(city.getFdName());
//                            UcV1OrgListResultVO region = ucOrgMap.get(city.getFdPsid());
//                            if (region != null) {
//                                mdmProjectInfo.setRegionId(region.getFdSid());
//                                mdmProjectInfo.setRegionName(region.getFdName());
//                            }
//                            projectList.add(mdmProjectInfo);
//                        }
//                    }
//                }
//            });
//
//            mdmProjectInfoService.updateBatchById(projectList);
//        } catch (UcException e) {
//            e.printStackTrace();
//        }


    }

    @Test
    public void test1(){
        BpmBusinessInfoBo c416b927aa8e4d4f81282562decd0067 = boProjectQuotaExtendService.getPreApproveInfo("c416b927aa8e4d4f81282562decd0067");
        System.out.println(c416b927aa8e4d4f81282562decd0067);
        BpmBusinessInfoBo fbf30d3d7555446ab2e10b517ea47f51 = boProjectPriceExtendService.getPreApproveInfo("fbf30d3d7555446ab2e10b517ea47f51");
        System.out.println(c416b927aa8e4d4f81282562decd0067);
    }
}
