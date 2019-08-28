package com.tahoecn.bo.controller.sample;

import com.tahoecn.core.json.JSONResult;
import com.tahoecn.uc.sso.SSOHelper;
import com.tahoecn.uc.sso.annotation.Action;
import com.tahoecn.uc.sso.annotation.Login;
import com.tahoecn.uc.sso.common.SSOConstants;
import com.tahoecn.uc.sso.security.token.SSOToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 单点登录DEMO
 * @author panglx
 * @date 2019/4/24
 */
@Controller
@RequestMapping("/sample/sso")
public class SSOSampleController {

    /**
     * 若开启，无登录，访问则被sso拦截
     * @return
     */
    @ResponseBody
    @RequestMapping("/rest")
    public JSONResult rest(){
        return new JSONResult();
    }

    /**
     * 跳过SSO，无登录也可访问。
     * @return
     */
    @ResponseBody
    @RequestMapping("/skipsso")
    @Login(action = Action.Skip)
    public JSONResult skipsso(){
        JSONResult jsonResult = new JSONResult();
        jsonResult.setCode(0);
        jsonResult.setMsg("跳过sso");
        return jsonResult;
    }

    /**
     * 用户信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/user")
    public JSONResult user(HttpServletRequest request){
        SSOToken st = SSOHelper.getSSOToken(request);
        String id = st.getId();   //用户id
        String issuer = st.getIssuer();   //用户名
        Map data = (Map) st.getClaims().get(SSOConstants.TOKEN_USER_INFO); //用户信息
        String sysId = st.getSysId ();   //系统id
        JSONResult jsonResult = new JSONResult();
        jsonResult.setCode(0);
        jsonResult.setMsg("用户信息");
        jsonResult.setData("id: "+id+", issuer: "+issuer+", data: "+data+", sysId: "+sysId);
        return jsonResult;
    }

}
