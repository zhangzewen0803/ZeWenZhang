/**
 *
 */
package com.tahoecn.bo.aop;

import com.tahoecn.bo.common.enums.CodeEnum;
import com.tahoecn.bo.common.utils.JsonResultBuilder;
import com.tahoecn.core.json.JSONResult;
import com.tahoecn.log.Log;
import com.tahoecn.log.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

/**
 * 全局的的异常拦截器（拦截所有的控制器）（带有@RequestMapping注解的方法上都会拦截）
 *
 * @author liuqs
 * @since 2018年8月30日 上午10:03:05
 */
@ControllerAdvice
public class BaseControllerExceptionHandler {

    private Log log = LogFactory.get();

    /**
     * 参数校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public JSONResult constraintViolationException(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream().map(x -> x.getMessage()).reduce((x, y) -> x + "," + y).get();
        return JsonResultBuilder.create(CodeEnum.ILLEGAL_REQUEST_PARAM_ERROR.getKey(), msg);
    }

    /**
     * 对象参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public JSONResult constraintViolationException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getAllErrors().stream().map(x -> x.getDefaultMessage()).reduce((x, y) -> x + "," + y).get();
        return JsonResultBuilder.create(CodeEnum.ILLEGAL_REQUEST_PARAM_ERROR.getKey(), msg);
    }

    /**
     * 请求方法异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public JSONResult httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return JsonResultBuilder.create(CodeEnum.ILLEGAL_REQUEST_METHOD_ERROR.getKey(), e.getMessage());
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public JSONResult exception(Exception e) {
        log.error(e);
        return JsonResultBuilder.internalError();
    }

}
