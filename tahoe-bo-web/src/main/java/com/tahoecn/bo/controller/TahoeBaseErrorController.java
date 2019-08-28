package com.tahoecn.bo.controller;

import com.tahoecn.core.json.JSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局错误信息处理
 * @author panglx
 * @date 2019/4/25
 */
@Controller
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class TahoeBaseErrorController implements ErrorController {

    @Autowired
    private ErrorProperties errorProperties;

    /**
     * rest 返回错误提示
     * @param request
     * @return
     */
    @RequestMapping
    @ResponseBody
    public ResponseEntity<?> error(HttpServletRequest request) {
        HttpStatus status = this.getStatus(request);
        JSONResult jsonResult = new JSONResult<>();
        jsonResult.setCode(status.value());
        jsonResult.setMsg(status.getReasonPhrase());
        return new ResponseEntity(jsonResult, status);
    }

    @Override
    public String getErrorPath() {
        return errorProperties.getPath();
    }

    protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
        if(statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            try {
                return HttpStatus.valueOf(statusCode.intValue());
            } catch (Exception var4) {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
    }
}
