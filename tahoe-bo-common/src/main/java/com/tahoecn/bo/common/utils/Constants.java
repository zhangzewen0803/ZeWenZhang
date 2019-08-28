
package com.tahoecn.bo.common.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Constants {

    private static final Logger LOGGER = LogManager.getLogger(Constants.class);

    /** 默认页码 1 */
    public static final Integer PAGENUM = 1;

    /** 默认行数 10 */
    public static final Integer PAGESIZE = 10;

    /** 默认页码 1 */
    public static final String PAGE_NUM = "1";

    /** 默认行数 10 */
    public static final String PAGE_SIZE = "10";

    /** 默认行数 50 */
    public static final String PAGE_SIZE_FIFTY = "50";

    /** APP默认行数5*/
    public static final String APP_PAGE_SIZE = "5";

    /** isDel 是否删除 0 未删除  */
    public static final Integer NODEL = 0;

    /** isDel 是否删除 1 已删除 */
    public static final Integer DEL = 1;

    /** Isdelete 是否删除 0 不删除  */
    public static final Integer NODELETE = -1;

    /** Isdelete 是否删除 1 删除 */
    public static final Integer DELETE = 1;

    /** available 是否删除 0 不可用  */
    public static final Integer NOAVAILABLE = -1;

    /** available 是否删除 1 可用 */
    public static final Integer AVAILABLE = 1;

    /** DISABLED 是否禁用 0 禁用; */
    public static final Integer DISABLED = 0;

    /** ENABLED 是否禁用 1 启用; */
    public static final Integer ENABLED = 1;

    /** VALID 是否有效 1 有效 */
    public static final Integer VALID = 1;

    /** isBind 是否有效 0 无效 */
    public static final Integer DISVALID = 0;

    /**
     * @Fields CNT_ZERO : 0 数量标识
     */
    public static final Integer CNT_ZERO = 0;

    /** ISHandModiFee */
    public static final String ISHANDMODIFEE = "1";

    /**
     * @Fields RESULT_FLAG : 返回是否成功标识--success
     */
    public static final String RESULT_FLAG = "success";

    /**
     * 文件上传模式(server/nas)
     */
    public static String UPLOAD_MODE = "server";

    /**
     * EXCEL_TEMPLATE_COLUMN_COUNT : excel模板列数量
     */
    public static Integer EXCEL_TEMPLATE_COLUMN_COUNT = 11;

    /**
     * EXCEL_TEMPLATE_COLUMN_COUNT : excel模板列数量
     */
    public static Integer EXCEL_INIT_TEMPLATE_COLUMN_COUNT = 12;

    /**
     * EXCEL_TEMPLATE_DEFAULT_SHEET : excel模板默认工作表 0 未起始表
     */
    public static Integer EXCEL_TEMPLATE_DEFAULT_SHEET = 0;

    /**
     * @Fields EXCEL_TEMPLATE_TITLE_ROW : 模板默认行
     */
    public static Integer EXCEL_TEMPLATE_TITLE_ROW = 1;

    /**
     * @Fields EXCEL_IMPORT_FORMAT_EXCEPTION : excel导入格式异常（手动调整列格式）：报错编码 1001
     */
    public static String EXCEL_IMPORT_FORMAT_EXCEPTION = "1001";
    /**
     * 文件上传NAS盘目录
     */
    public static String UPLOAD_NAS_DIR = "/opt/upload";

    /**
     * 文件NAS访问根目录
     */
    public static String UPLOAD_NAS_ACCESS = "/nas";

    /**
     * 上传默认目录
     */
    public static String UPLOAD_ROOT = "/files";

    /** 定时任务状态  0 正常 */
    public static final Integer SCHEDULE_START_STATUS_NORMAL=0;

    /** 定时任务状态  1 异常 */
    public static final Integer SCHEDULE_START_STATUS_EXCEPTION=1;

    /** 流程类型--项目 */
    public static final String PROCESS_STATUS_PROJECT = "PROJECT";
    /** 流程类型--分期 */
    public static final String PROCESS_STATUS_SUB_PROJECT = "SUBPROJECT";

    /** 流程类型--面积 */
    public static final String PROCESS_STATUS_AREA = "AREA";
    /** 流程类型--价格 */
    public static final String PROCESS_STATUS_PRICE = "PRICE";


    /** 流程状态--撤回 */
    public static final String PROCESS_STATUS_DRAFT = "10";
    /** 流程状态--驳回 */
    public static final String PROCESS_STATUS_REJECT = "11";
    /** 流程状态--正常审批 */
    public static final String PROCESS_STATUS_EXAMINING = "20";
    /** 流程状态--驳回后审批 */
    public static final String PROCESS_STATUS_REJECT_EXAMINING = "21";
    /** 流程状态--正常结束 */
    public static final String PROCESS_STATUS_PASS = "30";
    /** 流程状态--流程重新发起审批后结束 */
    public static final String PROCESS_STATUS_REJECT_PASS = "31";
    /** 流程状态--废弃状态 */
    public static final String PROCESS_STATUS_WASTE = "00";
    /** 流程状态--流程不存在（被删除） */
    public static final String PROCESS_STATUS_NONEXISTENT = "99";

    /** 流程日志--撤回 */
    public static final String PROCESS_LOG_DRAFT = "申请单据被撤回";
    /** 流程日志--驳回 */
    public static final String PROCESS_LOG_REJECT = "申请单据被驳回";
    /** 流程日志--正常审批 */
    public static final String PROCESS_LOG_EXAMINING = "申请单据审批中";
    /** 流程日志--驳回后审批 */
    public static final String PROCESS_LOG_REJECT_EXAMINING = "驳回后的单据重新发起后审批中";
    /** 流程日志--正常结束 */
    public static final String PROCESS_LOG_PASS = "申请单据审批通过";
    /** 流程日志--流程重新发起审批后结束 */
    public static final String PROCESS_LOG_REJECT_PASS = "驳回后的单据重新发起后审批通过";
    /** 流程日志--废弃日志 */
    public static final String PROCESS_LOG_WASTE = "废弃单据流程";
    /** 流程日志--流程不存在（被删除） */
    public static final String PROCESS_LOG_NONEXISTENT = "流程不存在";

    /**驳回编辑--非驳回操作*/
    public static final String REJECT_EDIT_TYPE_NON = "0";
    /**驳回编辑--返回打回人 部分修改*/
    public static final String REJECT_EDIT_TYPE_PART = "1";
    /**驳回编辑--重走流程 全部修改*/
    public static final String REJECT_EDIT_TYPE_ALL = "2";

    /**Sale 同步房间信息 类型 **/
    public static String SYNC_ROOM_INFO = "roomInfo";

    /**Sale 同步房间流水数据 类型 **/
    public static String SYNC_ROOM_STREAM_INFO = "roomStreamInfo";


    /**
     *  APPROVAL_TITLE_ESTABLISH  审批标题创建
     * 审批主题生成规则：
     * 规则：请审批[{$1}项目{$2}分期{$3}{$4}]{项目规划指标|分期规划指标|研发指标|价格审批|供货计划|签约回款计划|活钱计划|投资测算}
     * 示例：请审批[泰禾一号公馆项目一期经营决策会V1.0]{项目规划指标|分期规划指标|研发指标|价格审批|供货计划|签约回款计划|活钱计划|投资测算}
     * {$1}：项目名称；
     * {$2}：分期名称；
     * {$3}：阶段版本名称；
     * {$4}：小版本号；
     */
    public static final String APPROVAL_TITLE_ESTABLISH = "请审批[{0}{1}{2}{3}]{4}";
    public static final String PROJECT_QUOTA = "项目规划指标";
    public static final String SUB_PROJECT_QUOTA = "分期规划指标";

    //检查指标code值 地上可售面积  地下可售面积  地上可租面积  地下可租面积  地上可租售面积  地下可租售面积
    public final static List<String> inspectQuotaList = Arrays.asList("ABOVE_GROUND_CAN_SALE_AREA","UNDER_GROUND_CAN_SALE_AREA");
    // "ABOVE_GROUND_CAN_RENT_AREA","UNDER_GROUND_CAN_RENT_AREA","ABOVE_GROUND_CAN_RENT_SALE_AREA","UNDER_GROUND_CAN_RENT_SALE_AREA");


    /**
     * 方法描述：读取常量配置
     * 创建人：admin
     */
    synchronized static public void loadConfig() {
        LOGGER.info("读取配置文件中的常量 Begin...");
        InputStream is = Constants.class.getResourceAsStream("/config.properties");
        Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(is, "UTF-8"));
            if (properties.getProperty("upload.mode") != null) {
                UPLOAD_MODE = properties.getProperty("upload.mode");
            }
            if (properties.getProperty("upload.nas.dir") != null) {
                UPLOAD_NAS_DIR = properties.getProperty("upload.nas.dir");
            }
            if (properties.getProperty("upload.nas.access") != null) {
                UPLOAD_NAS_ACCESS = properties.getProperty("upload.nas.access");
            }
        } catch (Exception e) {
            LOGGER.error("读取常量配置文件异常:",e);
        }
        LOGGER.info("读取配置文件中的常量 End...");
    }
}
