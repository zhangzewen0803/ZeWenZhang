package com.tahoecn.bo.common.constants;

/**
 * redis 常量
 *
 * @author panglx
 * @date 2019/6/1
 */
public class RedisConstants {

    /**
     * 面积创建版本锁定
     */
    public static final String AREA_CREATE_VERSION_LOCK = "bo_area_create_version_lock_";

    /**
     * 新增政府方案锁定
     */
    public static final String AREA_ADD_GOV_PLAN_CARD_LOCK = "bo_area_add_gov_plan_card_lock_";

    /**
     * 新增工规证锁定
     */
    public static final String AREA_ADD_PROJECT_PLAN_CARD_LOCK = "bo_area_add_project_plan_card_lock_";

    /**
     * 价格创建版本锁定
     */
    public static final String PRICE_CREATE_VERSION_LOCK = "bo_price_create_version_lock_";

    /**
     * 项目创建版本锁定
     */
    public static final String PROJECT_CREATE_VERSION_LOCK = "bo_project_create_version_lock_";

    /**
     * 分期创建版本锁定
     */
    public static final String SUB_PROJECT_CREATE_VERSION_LOCK = "bo_sub_project_create_version_lock_";

    /**
     * 更新楼栋指标锁定
     */
    public static final String BUILDING_PRODUCT_TYPE_LOCK = "bo_building_product_type_lock_";

    /**
     * 待计算规划指标队列
     */
    public static final String PROJECT_QUOTA_CALC_LIST = "bo_project_quota_calc_list";

    /**
     * 待计算规划指标队列 锁定
     */
    public static final String PROJECT_QUOTA_CALC_LIST_LOCK = "bo_project_quota_calc_list_lock";

    /**
     * 修改项目/分期规划指标锁
     */
    public static final String PROJECT_QUOTA_MAP_UPDATE_LOCK = "bo_project_quota_map_update_lock_";

    /**
     * 经营概览 -货值 当天缓存数据
     */
    public static final String MANAGE_OVERVIEW_PRICE_TODAY = "bo_manage_overview_price_today";

    /**
     * 经营概览 -面积 当天缓存数据
     */
    public static final String MANAGE_OVERVIEW_AREA_TODAY = "bo_manage_overview_area_today";


    /**
     * 经营概览 -货值 当日汇总任务锁
     */
    public static final String MANAGE_OVERVIEW_PRICE_TODAY_CALC_TASK_LOCK = "bo_manage_overview_price_today_calc_task_lock";

    /**
     * 经营概览 -货值 历史汇总任务锁
     */
    public static final String MANAGE_OVERVIEW_PRICE_HISTORY_CALC_TASK_LOCK = "bo_manage_overview_price_history_calc_task_lock";

    /**
     * 经营概览 -面积 当日汇总任务锁
     */
    public static final String MANAGE_OVERVIEW_AREA_TODAY_CALC_TASK_LOCK = "bo_manage_overview_area_today_calc_task_lock";

    /**
     * 经营概览 -面积 历史汇总任务锁
     */
    public static final String MANAGE_OVERVIEW_AREA_HISTORY_CALC_TASK_LOCK = "bo_manage_overview_area_history_calc_task_lock";
    
    /**
     * 供货计划创建版本锁定
     */
    public static final String SUPPLY_CREATE_VERSION_LOCK = "bo_supply_create_version_lock_";
}
