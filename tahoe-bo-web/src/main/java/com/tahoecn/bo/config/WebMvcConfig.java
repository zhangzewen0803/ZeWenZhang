package com.tahoecn.bo.config;

import com.tahoecn.bo.interceptor.*;
import com.tahoecn.log.Log;
import com.tahoecn.log.LogFactory;
import com.tahoecn.uc.sso.SSOConfig;
import com.tahoecn.uc.sso.web.interceptor.SSOPermissionInterceptor;
import com.tahoecn.uc.sso.web.interceptor.SSOSpringInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer, Converter<String, Date> {

    private static final Log logger = LogFactory.get();

    @Autowired
    private TahoeSSOConfig tahoeSSOConfig;

    @Value("${tahoe.application.mappingPath}")
    private String relativePath;

    @Value("${tahoe.application.physicalPath}")
    private String physicalPath;

    @Value("${file.uploadFolder}")
    private String uploadFolder;

    @Value("${file.staticAccessPath}")
    private String staticAccessPath;

    @Bean
    GlobalInterceptor globalInterceptor() {
        return new GlobalInterceptor();
    }

    @Bean
    ErrorProperties errorProperties() {
        return new ErrorProperties();
    }

    /**
     * 单点登录方式映射器
     *
     * @return
     */
    @Bean
    RestSSOHandler restSSOHandler() {
        return new RestSSOHandler();
    }

    /**
     * 单点登录拦截器
     *
     * @return
     */
    @Bean
    SSOSpringInterceptor ssoSpringInterceptor() {
        SSOSpringInterceptor ssoSpringInterceptor = new SSOSpringInterceptor();
        try {
            Field handlerInterceptor = ssoSpringInterceptor.getClass().getDeclaredField("handlerInterceptor");
            handlerInterceptor.setAccessible(true);
            handlerInterceptor.set(ssoSpringInterceptor, restSSOHandler());
        } catch (NoSuchFieldException e) {
            logger.error(e);
        } catch (IllegalAccessException e) {
            logger.error(e);
        }
        return ssoSpringInterceptor;
    }

    /**
     * 单点登录配置
     */
    @Bean
    SSOConfig ssoConfig() {
        SSOConfig ssoC = new SSOConfig();
        ssoC.setLoginUrl(tahoeSSOConfig.getLoginUrl());
        ssoC.setLogoutUrl(tahoeSSOConfig.getLogoutUrl());
        ssoC.setUcwebUrl(tahoeSSOConfig.getWebUrl());
        ssoC.setCookieDomain(tahoeSSOConfig.getCookieDomain());
        ssoC.setUcssowebUrl(tahoeSSOConfig.getUcSsoWebUrl());
        SSOConfig ssoConfig = new SSOConfig();
        ssoConfig.setSsoConfig(ssoC);
        return ssoConfig;
    }

    /**
     * sso自定义权限验证类
     *
     * @return
     */
    @Bean
    SysAuthorization sysAuthorization() {
        return new SysAuthorization();
    }

    /**
     * sso权限拦截器
     * @return
     */
    @Bean
    SSOPermissionInterceptor ssoPermissionInterceptor() {
        SSOPermissionInterceptor ssoPermissionInterceptor = new RestSSOPermissionInterceptor();
        ssoPermissionInterceptor.setAuthorization(sysAuthorization());
        ssoPermissionInterceptor.setNothingAnnotationPass(true);
        return ssoPermissionInterceptor;
    }

    /**
     * 注册拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //全局拦截器
//        registry.addInterceptor(globalInterceptor()).addPathPatterns("/**").excludePathPatterns("/unauthorized");
        String excludePath = errorProperties().getPath();
        //单点登录拦截器
        if (!tahoeSSOConfig.isAllSSOConfigEmpty()) {
            List<String> list = tahoeSSOConfig.listSSOConfigEmptyElements();
            if (!list.isEmpty()) {
                String errorProperties = StringUtils.join(list, "\r\n");
                String errMsg = new StringBuilder()
                        .append("检测到您可能需要使用单点登录功能，")
                        .append("若需要,请保证tahoe.properties中以下配置项均正确填写，否则单点登录功能不生效：\r\n")
                        .append(errorProperties)
                        .toString();
                logger.error(errMsg);
            } else if (tahoeSSOConfig.isOpen()) {
                logger.info("单点登录功能已开启。");
                registry.addInterceptor(ssoSpringInterceptor())
                        .addPathPatterns(tahoeSSOConfig.getInterceptMappingPath())
                        .excludePathPatterns(excludePath);
            }else {
                logger.info("单点登录功能已关闭。");
            }
        }

        //权限校验拦截器
        if (!tahoeSSOConfig.isAllPermissionConfigEmpty()) {
            List<String> list = tahoeSSOConfig.listPermissionConfigEmptyElements();
            if (!list.isEmpty()) {
                String errorProperties = StringUtils.join(list, "\r\n");
                String errMsg = new StringBuilder()
                        .append("检测到您可能需要使用权限拦截功能，")
                        .append("若需要,请保证tahoe.properties中以下配置项均正确填写，否则权限拦截功能不生效：\r\n")
                        .append(errorProperties)
                        .toString();
                logger.error(errMsg);
            } else if (tahoeSSOConfig.isPermissionOpen()) {
                if (!tahoeSSOConfig.isOpen()) {
                    logger.error("权限拦截功能依赖单点登录功能，若要使用权限拦截功能，请开启单点登录功能（在tahoe.properties中，设置tahoe.sso.open=true），否则权限拦截功能不生效");
                } else {
                    logger.info("权限拦截功能已开启。");
                    registry.addInterceptor(ssoPermissionInterceptor()).addPathPatterns(tahoeSSOConfig.getPermissionInterceptMappingPath()).excludePathPatterns(excludePath);
                }
            }else {
                logger.info("权限拦截功能已关闭。");
            }
        }

    }

    /**
     * 建立URL相对路径与绝对路径关系
     *
     * @param registry
     */


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(relativePath, staticAccessPath).addResourceLocations("file:" + physicalPath, "file:" + uploadFolder);
    }

    private static final List<String> formarts = new ArrayList();

    static {
        formarts.add("yyyy-MM");
        formarts.add("yyyy-MM-dd");
        formarts.add("yyyy-MM-dd HH");
        formarts.add("yyyy-MM-dd HH:mm");
        formarts.add("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public Date convert(String source) {
        String value = source.trim();
        if ("".equals(value)) {
            return null;
        }
        if (source.matches("^\\d{4}-\\d{1,2}$")) {
            return parseDate(source, formarts.get(0));
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            return parseDate(source, formarts.get(1));
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")) {
            return parseDate(source, formarts.get(2));
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return parseDate(source, formarts.get(3));
        } else {
            throw new IllegalArgumentException("Invalid boolean value '" + source + "'");
        }
    }

    /**
     * 格式化日期
     *
     * @param dateStr String 字符型日期
     * @param format  String 格式
     * @return Date 日期
     */
    public Date parseDate(String dateStr, String format) {
        Date date = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            date = dateFormat.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }
}
