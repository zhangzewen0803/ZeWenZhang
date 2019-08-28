package com.tahoecn.bo.controller.sample;

import com.tahoecn.core.json.JSONResult;
import com.tahoecn.uc.sso.annotation.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * SSO权限demo
 * @author panglx
 * @date 2019/4/24
 */
@Controller
@RequestMapping("/sample/sso")
public class SSOPermissionSampleController {

    /**
     * 有权限正常访问
     * 使用拥有UC001权限的账号访问
     * @return
     */
    @ResponseBody
    @RequestMapping("/permission1")
    @Permission("UC001")
    public JSONResult permission1(){
        JSONResult jsonResult = new JSONResult();
        jsonResult.setCode(0);
        jsonResult.setMsg("你拥有UC001权限，则可以看到此信息。");
        return jsonResult;
    }

    /**
     * 无权限被拦截,理论上不会存在一个XXXXXXXXXXXX的权限
     * 访问此地址，则被拦截。
     * @return
     */
    @ResponseBody
    @RequestMapping("/permission2")
    @Permission("XXXXXXXXXXXXXXXXX")
    public JSONResult permission2(){
        return new JSONResult();
    }

}
