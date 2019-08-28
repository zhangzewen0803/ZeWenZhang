package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.BoDictionaryItem;
import com.tahoecn.bo.model.vo.BasicProjectParamInfoVo;
import com.tahoecn.bo.model.vo.DictionaryInfoVo;

import java.util.List;

/**
 * <p>
 * 字典表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoDictionaryItemService extends IService<BoDictionaryItem> {

    List<DictionaryInfoVo> selectDictionInfo(String dicCode);

    List<BasicProjectParamInfoVo> getBasicProjectInfoList();
}
