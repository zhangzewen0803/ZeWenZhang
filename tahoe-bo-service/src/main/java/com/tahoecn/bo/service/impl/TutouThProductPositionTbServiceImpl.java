package com.tahoecn.bo.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.utils.DataUtils;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.mapper.TutouThProductPositionTbMapper;
import com.tahoecn.bo.model.entity.TutouThProductPositionTb;
import com.tahoecn.bo.service.TutouThProductPositionTbService;

/**
 * <p>
 * TUTOU-产品定位信息表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
@Service
public class TutouThProductPositionTbServiceImpl extends ServiceImpl<TutouThProductPositionTbMapper, TutouThProductPositionTb> implements TutouThProductPositionTbService {

	@Autowired
	private TutouThProductPositionTbMapper tutouThProductPositionTbMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchSaveProductPositList(List<TutouThProductPositionTb> productPositList) {
		List<TutouThProductPositionTb> productPositInsertList = new ArrayList<TutouThProductPositionTb>();
		List<TutouThProductPositionTb> productPositUpdateList = new ArrayList<TutouThProductPositionTb>();
		for (TutouThProductPositionTb tutouThProductPositionTb : productPositList) {
			TutouThProductPositionTb tutouThProductPositionTbOld = tutouThProductPositionTbMapper.selectByProductId(tutouThProductPositionTb.getProductId());
			if(tutouThProductPositionTbOld != null) {
				tutouThProductPositionTb.setId(tutouThProductPositionTbOld.getId());
				tutouThProductPositionTb.setUpdateTime(LocalDateTime.now());
				productPositUpdateList.add(tutouThProductPositionTb);
			}else {
				tutouThProductPositionTb.setId(UUIDUtils.create());
				tutouThProductPositionTb.setCreateTime(LocalDateTime.now());
				productPositInsertList.add(tutouThProductPositionTb);
			}
		}
		if (DataUtils.isNotEmpty(productPositUpdateList)) {
			tutouThProductPositionTbMapper.batchUpdateProductPositList(productPositUpdateList);
		}
		if (DataUtils.isNotEmpty(productPositInsertList)) {
			tutouThProductPositionTbMapper.batchInsertProductPositList(productPositInsertList);
		}
	}
	
}
