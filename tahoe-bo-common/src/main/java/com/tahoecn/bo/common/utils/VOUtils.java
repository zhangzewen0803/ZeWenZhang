package com.tahoecn.bo.common.utils;

import com.tahoecn.bo.model.entity.BoManageOverviewArea;
import com.tahoecn.bo.model.entity.BoManageOverviewPrice;
import com.tahoecn.bo.model.entity.BoQuotaGroupMap;
import com.tahoecn.bo.model.vo.ManageOverviewAreaVO;
import com.tahoecn.bo.model.vo.ManageOverviewPriceVO;
import com.tahoecn.bo.model.vo.QuotaHeadVO;
import com.tahoecn.log.Log;
import com.tahoecn.log.LogFactory;
import org.apache.commons.beanutils.PropertyUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * VOUtils
 *
 * @author panglx
 * @date 2019/5/31
 */
public class VOUtils {

    private static final Log logger = LogFactory.get();

    /**
     * 将BoQuotaGroupMap 转换为 QuotaHeadVO对象
     *
     * @param list
     * @return
     */
    public static List<QuotaHeadVO> makeQuotaHeadVOList(List<BoQuotaGroupMap> list) {
        return list.parallelStream().map(x -> {
            QuotaHeadVO quotaHeadVO = new QuotaHeadVO();
            try {
                PropertyUtils.copyProperties(quotaHeadVO, x);
            } catch (Exception e) {
                logger.error(e);
            }
            return quotaHeadVO;
        }).collect(Collectors.toList());
    }

    public static ManageOverviewPriceVO convert2ManageOverviewPriceVO(BoManageOverviewPrice boManageOverviewPrice) {
        ManageOverviewPriceVO manageOverviewPriceVO = new ManageOverviewPriceVO();
        try {
            PropertyUtils.copyProperties(manageOverviewPriceVO, boManageOverviewPrice);
        } catch (Exception e) {
            logger.error(e);
        }
        return manageOverviewPriceVO;
    }

    public static ManageOverviewAreaVO convert2ManageOverviewAreaVO(BoManageOverviewArea boManageOverviewArea) {
        ManageOverviewAreaVO manageOverviewAreaVO = new ManageOverviewAreaVO();
        try {
            PropertyUtils.copyProperties(manageOverviewAreaVO, boManageOverviewArea);
        } catch (Exception e) {
            logger.error(e);
        }
        return manageOverviewAreaVO;
    }


}
