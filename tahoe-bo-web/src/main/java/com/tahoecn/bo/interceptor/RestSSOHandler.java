package com.tahoecn.bo.interceptor;

import com.tahoecn.bo.config.TahoeSSOConfig;
import com.tahoecn.core.json.JSONResult;
import com.tahoecn.core.util.StrUtil;
import com.tahoecn.log.Log;
import com.tahoecn.log.LogFactory;
import com.tahoecn.uc.sso.SSOConfig;
import com.tahoecn.uc.sso.common.util.JsonUtil;
import com.tahoecn.uc.sso.web.handler.UcSSOHandler;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * UC单点登录映射器
 * @author panglx
 * @date 2019/5/7
 */
public class RestSSOHandler implements UcSSOHandler {

    private static final Log logger = LogFactory.get();

    @Autowired
    private TahoeSSOConfig tahoeSSOConfig;

    /**
     * Ajax 请求以JSON数据返回结果
     * @param request
     * @param response
     * @return
     */
    @Override
    public boolean preTokenIsNullAjax(HttpServletRequest request, HttpServletResponse response) {
        try {
            String loginUrl = SSOConfig.getInstance().getLoginUrl();
            JSONResult<String> jsonResult = new JSONResult<>();
            jsonResult.setCode(-1);
            jsonResult.setMsg("未登录");
            String referer = request.getHeader("Referer");
            String withReturnURL = loginUrl + "&ReturnURL=";
            jsonResult.setData(StrUtil.isBlank(referer) ? withReturnURL + URLEncoder.encode(tahoeSSOConfig.getWebUrl(), "UTF-8") : withReturnURL + URLEncoder.encode(referer, "UTF-8"));
            String result = JsonUtil.toJson(jsonResult);
            response.setContentType("application/json;charset=utf-8");
            try (PrintWriter writer = response.getWriter()) {
                writer.write(result);
                writer.flush();
            } catch (IOException e) {
                logger.error(e);
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
        return false;
    }

    /**
     * 非Ajax方式，也JSON返回
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     */
    @Override
    public boolean preTokenIsNull(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return preTokenIsNullAjax(httpServletRequest,httpServletResponse);
    }
}
