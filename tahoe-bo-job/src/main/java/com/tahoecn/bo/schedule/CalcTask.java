package com.tahoecn.bo.schedule;

import com.tahoecn.bo.common.constants.RedisConstants;
import com.tahoecn.bo.service.BoManageOverviewAreaService;
import com.tahoecn.bo.service.BoManageOverviewPriceService;
import com.tahoecn.bo.service.BoProjectQuotaMapService;
import com.tahoecn.log.Log;
import com.tahoecn.log.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


/**
 * 计算任务
 *
 * @author panglx
 */
@Component
public class CalcTask {

    public static final Log logger = LogFactory.get();

    @Autowired
    private BoProjectQuotaMapService boProjectQuotaMapService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private BoManageOverviewPriceService boManageOverviewPriceService;

    @Autowired
    private BoManageOverviewAreaService boManageOverviewAreaService;

    /**
     * 计算项目/分期规划指标 任务 5 分钟 + 5 s 多5s ，可使队列阻塞先结束，然后重新执行任务
     */
    @Scheduled(fixedRate = (5 * 60 * 1000 + 5000))
    public void calcProjectQuotaMap() {
        try {
            if (redisTemplate.opsForValue().setIfAbsent(RedisConstants.PROJECT_QUOTA_CALC_LIST_LOCK, System.currentTimeMillis())) {
                logger.info("定时重启计算[项目/分期规划指标计算]任务："+System.currentTimeMillis());
                try {
                    boProjectQuotaMapService.processCalcProjectQuota();
                } catch (Exception e) {
                    //一般为超时结束，不必记录
                }
            }
        } finally {
            redisTemplate.delete(RedisConstants.PROJECT_QUOTA_CALC_LIST_LOCK);
        }
    }

    /**
     * 经营概览货值 - 当日汇总任务 ，30分钟 刷新一次
     */
    @Scheduled(cron = "0 0/30 * * * ? ")
    public void calcPriceToday() {
        try {
            if (redisTemplate.opsForValue().setIfAbsent(RedisConstants.MANAGE_OVERVIEW_PRICE_TODAY_CALC_TASK_LOCK,System.currentTimeMillis())){
                logger.info("启动[经营概览货值 - 当日汇总任务]："+System.currentTimeMillis());
                boManageOverviewPriceService.makeData(LocalDate.now());
            }
        } finally {
            redisTemplate.delete(RedisConstants.MANAGE_OVERVIEW_PRICE_TODAY_CALC_TASK_LOCK);
        }
    }

    /**
     * 经营概览货值 - 昨日数据汇总任务 - 1天 一次
     */
    @Scheduled(cron = "0 0 0 1/1 * ? ")
    public void calcPriceHistory() {
        try {
            if (redisTemplate.opsForValue().setIfAbsent(RedisConstants.MANAGE_OVERVIEW_PRICE_HISTORY_CALC_TASK_LOCK,System.currentTimeMillis())){
                LocalDate yesterday = LocalDate.now().minusDays(1);
                logger.info("启动[经营概览货值 - 昨日数据汇总任务]："+yesterday);
                boManageOverviewPriceService.makeData(yesterday);
            }
        } finally {
            redisTemplate.delete(RedisConstants.MANAGE_OVERVIEW_PRICE_HISTORY_CALC_TASK_LOCK);
        }
    }

    /**
     * 经营概览面积 - 当日汇总任务 ，30分钟 刷新一次
     */
    @Scheduled(cron = "0 0/30 * * * ? ")
    public void calcAreaToday() {
        try {
            if (redisTemplate.opsForValue().setIfAbsent(RedisConstants.MANAGE_OVERVIEW_AREA_TODAY_CALC_TASK_LOCK,System.currentTimeMillis())){
                logger.info("启动[经营概览面积 - 当日汇总任务]："+System.currentTimeMillis());
                boManageOverviewAreaService.makeData(LocalDate.now());
            }
        } finally {
            redisTemplate.delete(RedisConstants.MANAGE_OVERVIEW_AREA_TODAY_CALC_TASK_LOCK);
        }
    }

    /**
     * 经营概览面积- 昨日数据汇总任务 - 1天 一次
     */
    @Scheduled(cron = "0 0 0 1/1 * ? ")
    public void calcAreaHistory() {
        try {
            if (redisTemplate.opsForValue().setIfAbsent(RedisConstants.MANAGE_OVERVIEW_AREA_HISTORY_CALC_TASK_LOCK,System.currentTimeMillis())){
                LocalDate yesterday = LocalDate.now().minusDays(1);
                logger.info("启动[经营概览面积 - 昨日数据汇总任务]："+yesterday);
                boManageOverviewAreaService.makeData(yesterday);
            }
        } finally {
            redisTemplate.delete(RedisConstants.MANAGE_OVERVIEW_AREA_HISTORY_CALC_TASK_LOCK);
        }
    }


}
