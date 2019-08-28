/**
 * 项目名：tahoe
 * 包名：com.tahoecn.common.utils
 * 文件名：JsonResult.java
 * 版本信息：1.0.0
 * 日期：2016年11月21日-下午12:27:21
 * Copyright (c) 2016Pactera-版权所有
 */
 
package com.tahoecn.bo.common.utils;

import java.util.HashMap;

import org.springframework.data.domain.Page;

/**
 * 类名称：JsonResult
 * 类描述：Json响应结果封装
 * 创建人：lee
 * 创建时间：2016年11月21日 下午12:27:21
 * @version 1.0.0
 */
public class JsonResult extends HashMap<String, Object> {

    private static final long serialVersionUID = -7484781490745076286L;

    /**
     * 成功{success:true}
     */
    public static JsonResult successResult() {
        JsonResult jsonReuslt = new JsonResult();
        jsonReuslt.put("success", true);
        return jsonReuslt;
    }

    /**
     * 成功并加入返回信息{success:true,msg=successMsg}
     * @param successMsg
     */
    public static JsonResult successResult(String successMsg) {
        JsonResult jsonReuslt = new JsonResult();
        jsonReuslt.put("success", true);
        jsonReuslt.put("msg", successMsg);
        return jsonReuslt;
    }

    /**
     * 异常并返回异常信息{success:false,error=errorMsg}
     * @param errorMsg
     */
    public static JsonResult errorResult(String errorMsg) {
        JsonResult jsonReuslt = new JsonResult();
        jsonReuslt.put("success", false);
        jsonReuslt.put("error", errorMsg);
        return jsonReuslt;
    }

    /**
     * 返回分页信息
     * @param page
     */
    public static JsonResult pageResult(Page<?> page) {
        JsonResult jsonReuslt = new JsonResult();
        jsonReuslt.put("success", true);
        jsonReuslt.put("total", page.getTotalElements());
        jsonReuslt.put("rows", page.getContent());
        return jsonReuslt;
    }

    /**
     * 成功并返回对象信息{success:true,data:{objJson}}
     * @param obj
     */
    public static JsonResult objectResult(Object obj) {
        JsonResult jsonReuslt = new JsonResult();
        jsonReuslt.put("success", true);
        jsonReuslt.put("data", obj);
        return jsonReuslt;
    }
}
