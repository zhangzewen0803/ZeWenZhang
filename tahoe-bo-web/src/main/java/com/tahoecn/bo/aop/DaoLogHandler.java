/**
 *
 */
package com.tahoecn.bo.aop;

import com.tahoecn.log.Log;
import com.tahoecn.log.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * dao日志 统计耗时
 * @author panglx
 */
@Aspect
@Configuration
@Order(1)
public class DaoLogHandler {

    private Log log = LogFactory.get();

    /**
     * 切入点
     */
    @Pointcut("execution(* com.tahoecn.bo.mapper..*.*(..))")
    public void api() {
    }

    /**
     * 环绕
     *
     * @param proceedingJoinPoint
     * @return
     */
    @Around("api()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long s = System.currentTimeMillis();
        try {
           return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        } finally {
            log.info(proceedingJoinPoint.getSignature().toString() + ",耗时："+(System.currentTimeMillis() - s) +" ms");
        }

    }

}
