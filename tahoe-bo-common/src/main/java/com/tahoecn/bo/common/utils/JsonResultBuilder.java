package com.tahoecn.bo.common.utils;

import com.tahoecn.core.json.JSONResult;
import com.tahoecn.bo.common.enums.CodeEnum;

/**
 * JsonResult Builder
 *
 * @author panglx
 * @date 2019/2/20
 */
public class JsonResultBuilder {

    private JSONResult jsonResult;

    private JsonResultBuilder() {
        this.jsonResult = new JSONResult();
    }

    public static JsonResultBuilder create() {
        return new JsonResultBuilder();
    }

    public JsonResultBuilder setCode(int code) {
        jsonResult.setCode(code);
        return this;
    }

    public JsonResultBuilder setMessage(String message) {
        jsonResult.setMsg(message);
        return this;
    }

    public JsonResultBuilder setData(Object data) {
        jsonResult.setData(data);
        return this;
    }

    public JsonResultBuilder setDefault(CodeEnum tipsCodeEnum) {
        jsonResult.setCode(tipsCodeEnum.getKey());
        jsonResult.setMsg(tipsCodeEnum.getValue());
        return this;
    }

    public JsonResultBuilder setSuccess(boolean success) {
        setDefault(success ? CodeEnum.SUCCESS : CodeEnum.ERROR);
        return this;
    }


    public JSONResult build() {
        return jsonResult;
    }

    public static JSONResult success() {
        return create().setDefault(CodeEnum.SUCCESS).build();
    }
    public static JSONResult successWithData(Object data) {
        return create().setDefault(CodeEnum.SUCCESS).setData(data).build();
    }

    public static JSONResult success(String message) {
        return create().setCode(CodeEnum.SUCCESS.getKey()).setMessage(message).build();
    }

    public static JSONResult failed() {
        return create().setDefault(CodeEnum.ERROR).build();
    }

    public static JSONResult failed(String message) {
        return create().setCode(CodeEnum.ERROR.getKey()).setMessage(message).build();
    }

    public static JSONResult internalError() {
        return create().setDefault(CodeEnum.INTERNAL_ERROR).build();
    }

    public static JSONResult create(int code, String message) {
        return create().setCode(code).setMessage(message).build();
    }
    public static JSONResult create(CodeEnum codeEnum) {
        return create().setCode(codeEnum.getKey()).setMessage(codeEnum.getValue()).build();
    }

}
