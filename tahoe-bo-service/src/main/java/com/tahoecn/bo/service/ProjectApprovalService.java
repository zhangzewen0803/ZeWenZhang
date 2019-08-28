package com.tahoecn.bo.service;

import com.tahoecn.bo.model.bo.BpmBusinessInfoBo;
import com.tahoecn.core.json.JSONResult;

public interface ProjectApprovalService {

    JSONResult<String> projectValidateApproval(String extendId) throws Exception;

    JSONResult<String> subProjectValidateApproval(String extendId) throws Exception;

    BpmBusinessInfoBo projectCreatTitle(String extendId);

    BpmBusinessInfoBo subProjectCreatTitle(String extendId);
}
