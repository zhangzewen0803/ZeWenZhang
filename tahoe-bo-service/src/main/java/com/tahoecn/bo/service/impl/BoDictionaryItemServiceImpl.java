package com.tahoecn.bo.service.impl;

import com.tahoecn.bo.common.enums.BasicParamEnum;
import com.tahoecn.bo.common.enums.VersionStatusEnum;
import com.tahoecn.bo.mapper.BoDictionaryTypeMapper;
import com.tahoecn.bo.model.entity.BoDictionaryItem;
import com.tahoecn.bo.mapper.BoDictionaryItemMapper;
import com.tahoecn.bo.model.entity.BoDictionaryType;
import com.tahoecn.bo.model.vo.BasicProjectParamInfoVo;
import com.tahoecn.bo.model.vo.DictionaryInfoVo;
import com.tahoecn.bo.model.vo.VersionInfoVo;
import com.tahoecn.bo.service.BoDictionaryItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 字典表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class BoDictionaryItemServiceImpl extends ServiceImpl<BoDictionaryItemMapper, BoDictionaryItem> implements BoDictionaryItemService {

    @Autowired
    private BoDictionaryItemMapper dictionaryItemMapper;

    @Autowired
    private BoDictionaryTypeMapper dictionaryTypeMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<DictionaryInfoVo> selectDictionInfo(String dicCode) {

        List<DictionaryInfoVo> typeInfos = new ArrayList<DictionaryInfoVo>();

        Map<String, Object> reqTypeMap = new HashMap<String, Object>();
        reqTypeMap.put("is_disable", 0);
        reqTypeMap.put("is_delete", 0);
        if (StringUtils.isNotBlank(dicCode)) {
            reqTypeMap.put("code", dicCode);
        }
        List<BoDictionaryType> boDictionaryTypes = dictionaryTypeMapper.selectByMap(reqTypeMap);
        List<String> typeIds = Optional.ofNullable(boDictionaryTypes)
                .orElseGet(ArrayList::new)
                .stream().map(BoDictionaryType::getId)
                .collect(Collectors.toList());

        if(boDictionaryTypes != null && boDictionaryTypes.size() > 0){
            List<BoDictionaryItem> dictionaryItems = dictionaryItemMapper.selectByTypeIds(typeIds);
            for (int i = 0; i < boDictionaryTypes.size(); i++) {
                BoDictionaryType boDictionaryType = boDictionaryTypes.get(i);
                if (boDictionaryType != null) {
                    DictionaryInfoVo dictionaryInfoVo = new DictionaryInfoVo();
                    dictionaryInfoVo.setDicCode(boDictionaryType.getCode());
                    dictionaryInfoVo.setDicName(boDictionaryType.getName());

                    List<DictionaryInfoVo.DictionaryItemInfoVo> itemList = new ArrayList<DictionaryInfoVo.DictionaryItemInfoVo>();
                    if(dictionaryItems != null && dictionaryItems.size() > 0){
                        for (int j = 0; j < dictionaryItems.size(); j++) {
                            BoDictionaryItem boDictionaryItem = dictionaryItems.get(j);

                            if (boDictionaryItem != null) {
                                if (boDictionaryItem.getItemTypeId().equals(boDictionaryType.getId())) {
                                    DictionaryInfoVo.DictionaryItemInfoVo dictionaryItemInfoVo = new DictionaryInfoVo.DictionaryItemInfoVo();
                                    dictionaryItemInfoVo.setDicItemCode(boDictionaryItem.getCode());
                                    dictionaryItemInfoVo.setDicItemName(boDictionaryItem.getName());
                                    itemList.add(dictionaryItemInfoVo);
                                }
                            }
                        }
                    }
                    dictionaryInfoVo.setDicItem(itemList);
                    typeInfos.add(dictionaryInfoVo);
                }
            }
        }
        return typeInfos;
    }

    @Override
    public List<BasicProjectParamInfoVo> getBasicProjectInfoList() {

        List<BasicProjectParamInfoVo> basicProjectParamInfoVos = new ArrayList<BasicProjectParamInfoVo>();
        for (BasicParamEnum basicParamEnum : BasicParamEnum.values()) {
            BasicProjectParamInfoVo basicProjectParamInfoVo = new BasicProjectParamInfoVo();
            basicProjectParamInfoVo.setItemKey(basicParamEnum.getKey());
            basicProjectParamInfoVo.setItemValue(basicParamEnum.getValue());
            basicProjectParamInfoVos.add(basicProjectParamInfoVo);
        }

        return basicProjectParamInfoVos;
    }
}
