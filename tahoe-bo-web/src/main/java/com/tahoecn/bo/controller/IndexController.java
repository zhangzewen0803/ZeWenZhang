package com.tahoecn.bo.controller;

import com.tahoecn.bo.common.enums.LevelTypeEnum;
import com.tahoecn.bo.common.utils.JsonResultBuilder;
import com.tahoecn.bo.model.entity.MdmProjectInfo;
import com.tahoecn.bo.service.MdmProjectInfoService;
import com.tahoecn.core.json.JSONResult;
import com.tahoecn.log.Log;
import com.tahoecn.log.LogFactory;
import com.tahoecn.uc.UcClient;
import com.tahoecn.uc.exception.UcException;
import com.tahoecn.uc.request.UcV1OrgListRequest;
import com.tahoecn.uc.response.UcV1OrgListResponse;
import com.tahoecn.uc.vo.UcV1OrgListResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Index
 *
 * @author panglx
 */
@Controller
public class IndexController extends TahoeBaseController {

    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public String home() {
        return "redirect:/swagger-ui.html";
    }


}
