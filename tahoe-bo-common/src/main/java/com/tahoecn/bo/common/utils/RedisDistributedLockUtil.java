package com.tahoecn.bo.common.utils;

import com.tahoecn.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 */
@Component
public class RedisDistributedLockUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisDistributedLockUtil.class);

    /**
     * 单个业务持有锁的时间30s,防止死锁
     */
    private static final long LOCK_EXPIRE = 30 * 1000L;

    /**
     * 每10毫秒尝试一次
     */
    private static final long LOCK_TRY_INTERVAL = 10L;

    /**
     * 进程等待超时20秒默认
     */
    private static final long LOCK_TRY_TIMEOUT = 20 * 1000L;

    private static final String KEY_PRIFIX = "lock_";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 操作redis获取全局锁
     *
     * @param lockKey           锁的名称
     * @param val           锁的值
     * @param timeout        获取的超时时间
     * @param tryInterval    多少ms尝试一次
     * @param lockExpireTime 获取成功后锁的过期时间
     * @return true 获取成功，false获取失败
     */

    private boolean tryLock(String lockKey,String val,long timeout,long tryInterval,long lockExpireTime){

        try{
            if(StrUtil.isEmpty(lockKey) || StrUtil.isEmpty(val)){
                return false;
            }

            String key = KEY_PRIFIX + lockKey;
            long startTime = System.currentTimeMillis();

            while (true){
                if(redisTemplate.opsForValue().setIfAbsent(key,val)){
                    redisTemplate.opsForValue().set(key,val,lockExpireTime,TimeUnit.MILLISECONDS);
                    logger.info("{0} : get lock",Thread.currentThread().getName());
                    return true;
                }else{
                    logger.info("{0} : wait lock",Thread.currentThread().getName());
                }

                if(System.currentTimeMillis() - startTime > timeout){
                    logger.info("{0} : get lock timeout {1}" + Thread.currentThread().getName(), timeout);
                    return false;
                }

                Thread.sleep(tryInterval);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            return false;
        }
    }

    public boolean lock(String lockKey,String lockVal) {
        return tryLock(lockKey, lockVal, LOCK_TRY_TIMEOUT, LOCK_TRY_INTERVAL, LOCK_EXPIRE);
    }

    public boolean lock(String lockKey,String lockVal, long timeout) {
        return tryLock(lockKey, lockVal, timeout, LOCK_TRY_INTERVAL, LOCK_EXPIRE);
    }

    public boolean lock(String lockKey,String lockVal, long timeout, long tryInterval) {
        return tryLock(lockKey, lockVal, timeout, tryInterval, LOCK_EXPIRE);
    }

    public boolean lock(String lockKey,String lockVal, long timeout, long tryInterval, long lockExpireTime) {
        return tryLock(lockKey, lockVal, timeout, tryInterval, lockExpireTime);
    }

    /**
     * 释放锁
     */
    public void unLock(String lockKey) {
        if (!StrUtil.isEmpty(lockKey)) {
            redisTemplate.delete(KEY_PRIFIX + lockKey);
        }
    }
}