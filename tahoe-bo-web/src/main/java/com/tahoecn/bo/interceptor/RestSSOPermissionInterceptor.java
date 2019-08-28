package com.tahoecn.bo.interceptor;

import com.tahoecn.core.json.JSONResult;
import com.tahoecn.core.util.JsonUtil;
import com.tahoecn.core.util.StrUtil;
import com.tahoecn.uc.sso.annotation.Action;
import com.tahoecn.uc.sso.annotation.Permission;
import com.tahoecn.uc.sso.security.token.SSOToken;
import com.tahoecn.uc.sso.web.interceptor.SSOPermissionInterceptor;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.lang.reflect.Method;

/**
 * sso权限拦截器
 *
 * @author panglx
 * @date 2019/4/25
 */
public class RestSSOPermissionInterceptor extends SSOPermissionInterceptor {


    /**
     * 验证权限
     *
     * @param request
     * @param handler
     * @param token
     * @return
     */
    @Override
    protected boolean isVerification(HttpServletRequest request, Object handler, SSOToken token) {
        /**
         * 验证注解
         */
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Permission pm = (Permission) method.getAnnotation(Permission.class);
        if (pm == null) {
            return this.isNothingAnnotationPass();
        }
        if (pm.action() == Action.Skip) {
            return true;
        }

        if (StrUtil.isNotBlank(pm.value()) && this.getAuthorization().isPermitted(token, pm.value())) {
            return true;
        }
        return false;
    }

    /**
     * 无权限响应
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean unauthorizedAccess(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONResult<Object> jsonResult = new JSONResult<>();
        jsonResult.setCode(-201);
        jsonResult.setMsg("无权限");
        response.setContentType("application/json;charset=utf-8");
        try (Writer writer = response.getWriter()) {
            writer.write(JsonUtil.convertObjectToJson(jsonResult));
            writer.flush();
        }
        return false;
    }
}
