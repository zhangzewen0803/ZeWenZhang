package com.tahoecn.bo.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 跨域过滤器
 *
 * @author panglx
 */
@Component
@ServletComponentScan
@WebFilter(urlPatterns = "/*", filterName = "crossDomainFilter")
public class CrossDomainFilter implements Filter {

    @Value("${tahoe.bo.cross.domain.origin.regex}")
    private String crossDomainOriginRegex;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            String origin = request.getHeader("Origin");
            if (origin != null && origin.matches(crossDomainOriginRegex)) {
                response.addHeader("Access-Control-Allow-Origin", origin);
                String header = request.getHeader("Access-Control-Request-Headers");
                if (header != null) {
                    response.addHeader("Access-Control-Allow-Headers", header);
                }else {
                    response.addHeader("Access-Control-Allow-Headers", "*");
                }
                response.addHeader("Access-Control-Allow-Methods", "*");
                response.addHeader("Access-Control-Allow-Credentials", "true");
//                response.addHeader("Access-Control-Max-Age", "3600");
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
