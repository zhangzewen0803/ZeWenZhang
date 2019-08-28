package com.tahoecn.bo.interceptor;

import com.google.common.collect.Maps;
import com.tahoecn.core.util.JsonUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Map;

/**
 *  全局的拦截器
 */
public class GlobalInterceptor implements HandlerInterceptor {

    /**
     * 在处理请求之前要做的动作
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("【1】在处理请求之前要做的动作");

        Map<String,String> map = Maps.newHashMap();
        System.out.println("url:"+request.getRequestURL().toString());
        System.out.println("todo:"+handler);

        //headers
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()){
            String parameter = headers.nextElement();
            map.put(parameter,request.getHeader(parameter));
            System.out.println(parameter+":"+request.getHeader(parameter));
        }

        //parameters
        Enumeration<String> enumeration =request.getParameterNames();
        while (enumeration.hasMoreElements()){
            String parameter = enumeration.nextElement();
            map.put(parameter,request.getParameter(parameter));
            System.out.println("parameters-"+parameter+":"+request.getParameter(parameter));
        }

        //cookies
        Cookie[] cookies = request.getCookies();
        if(cookies!=null) {
            for (Cookie cookie : cookies) {
                System.out.println(JsonUtil.convertObjectToJson(cookie));
            }
        }

        System.out.println("=====================================================================");

        return true;
    }

    /**
     * 完成请求处理后要做的动作
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        System.out.println("【2】完成请求处理后要做的动作");
    }

    /**
     * 请求结束后要做的动作
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        System.out.println("【3】请求结束后要做的动作");
    }

}
