/**
 *
 */
package com.tahoecn.bo.aop;

import com.tahoecn.bo.common.enums.CodeEnum;
import com.tahoecn.bo.common.utils.JsonResultBuilder;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.core.thread.ThreadUtil;
import com.tahoecn.core.util.JsonUtil;
import com.tahoecn.log.Log;
import com.tahoecn.log.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import java.util.stream.IntStream;

/**
 * api 入参出参日志
 * 拦截的异常日志一些与 com.tahoecn.bo.aop.BaseControllerExceptionHandler 类
 * 相同，原因是希望在产生异常的响应时，将参数与异常的响应也记录。
 * BaseControllerExceptionHandler不能去掉，因为除了此类切入的api的方法外，其他包的方法也需要全局的异常拦截
 *
 * @author panglx
 */
@Aspect
@Configuration
@Order(1)
public class ApiLogHandler {

    private Log log = LogFactory.get();

    /**
     * 切入点
     */
    @Pointcut("execution(* com.tahoecn.bo.controller.webapi..*.*(..))")
    public void api() {
    }

    /**
     * 环绕
     *
     * @param proceedingJoinPoint
     * @return
     */
    @Around("api()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) {
        //0：uri，1：参数，2：响应，3：耗时
        Object[] logs = new Object[4];
        long begin = System.currentTimeMillis();
        try {
            Object[] args = proceedingJoinPoint.getArgs();
            logs[1] = args;
            Object back = proceedingJoinPoint.proceed(args);
            logs[2] = back;
            logs[3] = System.currentTimeMillis() - begin;
            return back;
        } catch (ConstraintViolationException e) {
            //参数校验异常
            String msg = e.getConstraintViolations().stream().map(x -> x.getMessage()).reduce((x, y) -> x + "," + y).get();
            return logs[2] = JsonResultBuilder.create(CodeEnum.ILLEGAL_REQUEST_PARAM_ERROR.getKey(), msg);
        } catch (MethodArgumentNotValidException e) {
            //对象参数校验异常
            String msg = e.getBindingResult().getAllErrors().stream().map(x -> x.getDefaultMessage()).reduce((x, y) -> x + "," + y).get();
            return logs[2] = JsonResultBuilder.create(CodeEnum.ILLEGAL_REQUEST_PARAM_ERROR.getKey(), msg);
        } catch (HttpRequestMethodNotSupportedException e) {
            //请求方法异常
            return logs[2] = JsonResultBuilder.create(CodeEnum.ILLEGAL_REQUEST_METHOD_ERROR.getKey(), e.getMessage());
        } catch (Throwable throwable) {
            //其他异常/错误
            log.error(throwable);
            return logs[2] = JsonResultBuilder.internalError();
        } finally {
            try {
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = requestAttributes.getRequest();
                logs[0] = request.getRequestURI();
                logs[3] = logs[3] == null ? System.currentTimeMillis() - begin : logs[3];
                ThreadUtil.execute(() -> {
                    try {
                        String uuid = UUIDUtils.create();
                        //过滤参数
                        if (logs[1] != null) {
                            Object[] args = (Object[]) logs[1];
                            IntStream.range(0, args.length).forEach(x -> {
                                if (args[x] instanceof HttpServletRequest) {
                                    args[x] = "HttpServletRequest";
                                } else if (args[x] instanceof HttpServletResponse) {
                                    args[x] = "HttpServletResponse";
                                } else if (args[x] instanceof HttpSession) {
                                    args[x] = "HttpSession";
                                }
                            });
                        }
                        String respJson, resp = logs[2] != null ? (respJson = JsonUtil.convertObjectToJson(logs[2])).length() > 1024 ? respJson.substring(0, 1024) + "..." : respJson : "无响应";
                        log.info("[{}]接口：{}", uuid, logs[0]);
                        log.info("[{}]参数：{}", uuid, logs[1] != null ? JsonUtil.convertObjectToJson(logs[1]) : "无参数");
                        log.info("[{}]响应：{}", uuid, resp);
                        log.info("[{}]耗时：{}", uuid, logs[3] + " ms");
                    } catch (Exception e) {
                        log.error(e);
                    }
                });
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

}
