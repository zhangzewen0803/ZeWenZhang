package com.tahoecn.bo.mapper;

import com.tahoecn.bo.model.entity.BoBpmTemplateConfig;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 审批模板配置表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-07-19
 */
public interface BoBpmTemplateConfigMapper extends BaseMapper<BoBpmTemplateConfig> {

	/**
	 * @Title: selectOneTemplateIdByType 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param templateType
	 * @return BoBpmTemplateConfig
	 * @author liyongxu
	 * @date 2019年7月20日 上午11:57:19 
	*/
	BoBpmTemplateConfig selectOneTemplateIdByType(@Param(value = "templateType")String templateType);

}
