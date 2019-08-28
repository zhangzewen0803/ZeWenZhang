package com.tahoecn.bo.model.bo;

import com.tahoecn.bo.model.dto.SubProjectInfoDto;
import com.tahoecn.bo.model.vo.PartitionInfoVo;
import lombok.Data;

import java.util.List;

@Data
public class SubProjectInfoBo extends SubProjectInfoDto {

    List<PartitionInfoVo> partitionInfoBos;

}
