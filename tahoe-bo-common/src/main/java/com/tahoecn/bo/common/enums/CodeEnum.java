package com.tahoecn.bo.common.enums;

/**
 * 提示CODE枚举
 *  -100到-199定义输入异常
 *  -200到-299定义输出异常
 *  100-199用户自定义code与返回信息
 * @author panglx
 * @date 2019/2/20
 */
public enum CodeEnum {
    SUCCESS(0,"成功"),
    ERROR(1,"失败"),
    TOKEN_ERROR(-1,"Token认证失败"),
    REQUEST_PARAM_NOT_FULL_ERROR(-100,"请求参数不完整"),
    ILLEGAL_REQUEST_PARAM_ERROR(-101,"参数格式不正确"),
    ILLEGAL_REQUEST_METHOD_ERROR(-102,"请求方法不合法"),
    INTERNAL_ERROR(-200,"服务器内部错误"),
    NONE_PERMISSION_ERROR(-201,"无权限访问"),

    PROJECT_EXTENDS(300,"项目信息不存在，请确认信息！"),
    PROJECT_EXTENDS_INFO(301,"项目信息不完整，请先完整数据信息！"),
    PROJECT_EXTENDS_VERSION(302,"项目在审批中或以审批通过,不能进行操作！"),
    PROJECT_LAND_EXCEPTION(303,"地块不在该项目中！"),
    PROJECT_LAND_USE_EXCEPTION(304,"地块已被其他项目选择！"),
    PROJECT_CREATING_EXCEPTION(305,"该项目在编辑中，不能再次编辑！"),
    PROJECT_CREATING_VERSION_EXCEPTION(306, "请先生成新版本！"),
    PROJECT_LAND_DELETE_EXCEPTION(307, "项目地块已被使用，不能删除！"),


    SUB_PROJECT_EXTENDS(350,"项目分期信息不完整，请确认信息！"),
    SUB_PROJECT_EXTENDS_INFO(351,"项目分期信息不完整，请先完整数据信息！"),
    SUB_TO_PROJECT_EXTENDS_(352,"项目分期所对应的项目不存在或已被删除！"),
    SUB_PROJECT_EXTENDS_VERSION(353,"项目分期在审批中或以审批通过,不能进行操作！"),
    SUB_PROJECT_LAND_EXTENDS(354,"项目分期地块已被使用！"),
    SUB_PROJECT_CREATE_LAND_EXTENDS(355,"项目分期地块不能再次添加！"),


    PROJECT_PART_DELETE(395, "分区已被删除！"),
    PROJECT_PART_CREAT(396, "请添加分区！"),

    LAND_TO_PROJECT_EXTENDS(401,"该地块已被使用！"),
    LAND_INFO_EXTENDS(402,"该地块信息有误！"),
    LAND_USE_ALL_EXTENDS(403,"该地块"),
    LAND_QUOTE_USE_ALL_EXTENDS(404,"该地块业态信息"),
    LAND_AREAT_EXTENDS(405, "该地块面积"),
    LAND_DELETE_EXTENDS(406, "该地块已被删除或没添加"),

    PROJECT_APPROVAL_EXTENDS(500, "该项目已在审批中！"),
    PROJECT_NOT_APPROVAL_EXTENDS(501, "该项目不在审批中！"),
    PROJECT_APPROVAL_ERROR(502, "该项目请求审批失败！"),
    PROJECT_APPROVAL_LAND_ERROR(503, "该项目下没有添加地块信息，请先添加！"),

    PROJECT_SPEEDZ_UPDATE_EXTENDS(550, "工程进度填报修改已过时间！"),

    APPROVAL_EXTENDS(600, "当前版本无审批记录！"),
    APPROVAL_PROCESS_USE_EXTENDS(601,"审批发起人是**，您无权限查看审批！"),
    ;

    private int key;

    private String value;

    CodeEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
