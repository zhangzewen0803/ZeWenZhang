package com.tahoecn.bo.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.tahoecn.core.util.StrUtil;
import com.tahoecn.uc.config.UcConfig;

/**
 * 单点登录配置类
 */
@Configuration
@PropertySource("classpath:tahoe.properties")
@ConfigurationProperties("tahoe.sso")
public class TahoeSSOConfig {

    /**
     * SSO开关
     */
    private Boolean open;

    /**
     * sso权限验证开关
     */
    private Boolean permissionOpen;

    /**
     * 单点登录拦截的uri
     */
    private String interceptMappingPath;

    /**
     * 权限校验拦截的uri
     */
    private String permissionInterceptMappingPath;

    /**
     * 登录URL
     */
    private String loginUrl;

    /**
     * 退出URL
     */
    private String logoutUrl;

    /**
     * 登录成功跳转url
     */
    private String webUrl;

    /**
     * cookie domain属性
     */
    private String cookieDomain;

    /**
     * uc-sso-web接口地址
     */
    private String ucSsoWebUrl;

    public String getInterceptMappingPath() {
        return interceptMappingPath;
    }

    public void setInterceptMappingPath(String interceptMappingPath) {
        this.interceptMappingPath = interceptMappingPath;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return open == null ? false : open;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getCookieDomain() {
        return cookieDomain;
    }

    public void setCookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    public Boolean getPermissionOpen() {
        return permissionOpen;
    }

    public void setPermissionOpen(Boolean permissionOpen) {
        this.permissionOpen = permissionOpen;
    }

    public boolean isPermissionOpen() {
        return permissionOpen == null ? false : permissionOpen;
    }

    public String getPermissionInterceptMappingPath() {
        return permissionInterceptMappingPath;
    }

    public void setPermissionInterceptMappingPath(String permissionInterceptMappingPath) {
        this.permissionInterceptMappingPath = permissionInterceptMappingPath;
    }

    public String getUcSsoWebUrl() {
        return ucSsoWebUrl;
    }

    public void setUcSsoWebUrl(String ucSsoWebUrl) {
        this.ucSsoWebUrl = ucSsoWebUrl;
    }

    public boolean isAllSSOConfigEmpty(){
        return open == null && StrUtil.isAllBlank(interceptMappingPath,loginUrl,logoutUrl,webUrl,cookieDomain,ucSsoWebUrl);
    }

    public boolean isAllPermissionConfigEmpty(){
        return permissionOpen == null && StrUtil.isBlank(permissionInterceptMappingPath) && isAllSSOConfigEmpty();
    }

    public List<String> listSSOConfigEmptyElements(){
        List<String> list = new ArrayList<>(9);
        if (open == null){
            list.add("tahoe.sso.open");
        }
        if (StrUtil.isBlank(interceptMappingPath)){
            list.add("tahoe.sso.intercept-mapping-path");
        }
        if (StrUtil.isBlank(loginUrl)){
            list.add("tahoe.sso.login-url");
        }
        if (StrUtil.isBlank(logoutUrl)){
            list.add("tahoe.sso.logout-url");
        }
        if (StrUtil.isBlank(webUrl)){
            list.add("tahoe.sso.web-url");
        }
        if (StrUtil.isBlank(cookieDomain)){
            list.add("tahoe.sso.cookie-domain");
        }
        if (StrUtil.isBlank(ucSsoWebUrl)){
            list.add("tahoe.sso.uc-sso-web-url");
        }
        return list;
    }

    public List<String> listPermissionConfigEmptyElements(){
        //依赖单点登录
        List<String> list = listSSOConfigEmptyElements();
        if (permissionOpen == null){
            list.add("tahoe.sso.permission-open");
        }
        if (StrUtil.isBlank(permissionInterceptMappingPath)){
            list.add("tahoe.sso.permission-intercept-mapping-path");
        }
        //依赖UC-API
        if (StrUtil.isBlank(UcConfig.key)){
            list.add("tahoe.uc.key");
        }
        if (StrUtil.isBlank(UcConfig.client)){
            list.add("tahoe.uc.client");
        }
        if (StrUtil.isBlank(UcConfig.domain)){
            list.add("tahoe.uc.url");
        }
        return list;
    }
}
