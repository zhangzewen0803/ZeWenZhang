package com.tahoecn.bo.interceptor;

import com.tahoecn.core.util.StrUtil;
import com.tahoecn.log.Log;
import com.tahoecn.log.LogFactory;
import com.tahoecn.security.SecureUtil;
import com.tahoecn.uc.UcClient;
import com.tahoecn.uc.exception.UcException;
import com.tahoecn.uc.request.UcV1SysPrivListRequest;
import com.tahoecn.uc.response.UcV1SysPrivListResponse;
import com.tahoecn.uc.sso.SSOAuthorization;
import com.tahoecn.uc.sso.security.token.SSOToken;
import com.tahoecn.uc.vo.UcV1SysPrivListResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 系统权限授权实现类
 *
 * @author panglx
 */
public class SysAuthorization implements SSOAuthorization {

    private static final Log logger = LogFactory.get();

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 用户系统权限redis前缀
     */
    private static final String USER_SYS_PRIV = "user_sys_priv:";

    /**
     * 验证方法
     */
    @Override
    public boolean isPermitted(SSOToken token, String permission) {
        /**
         * 循环判断权限编码是否合法，token 获取登录用户ID信息、判断相应权限也可作为缓存主键使用。
         */
        List<UcV1SysPrivListResultVO> systemPermissions = getSystemPermissions(token.getId(), token.getIssuer(),token);
        for (UcV1SysPrivListResultVO sysPrivListResultVO : systemPermissions) {
            if (StrUtil.isNotBlank(sysPrivListResultVO.getFdCode()) && sysPrivListResultVO.getFdCode().equals(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取权限，缓存24h
     */
    private List<UcV1SysPrivListResultVO> getSystemPermissions(String sid, String username,SSOToken token) {
        try {
            //key增加TOKEN的MD5值，是为了实现每次退出后再登录都可以重新请求一次接口获取最新的权限。
            String privKey = new StringBuilder(USER_SYS_PRIV)
                    .append(sid)
                    .append(username)
                    .append(SecureUtil.md5(token.getToken())).toString();
            List<UcV1SysPrivListResultVO> sysPrivList = (List<UcV1SysPrivListResultVO>) redisTemplate.opsForValue().get(privKey);
            if (sysPrivList != null) {
                return sysPrivList;
            }
            UcV1SysPrivListRequest request = new UcV1SysPrivListRequest();
            request.setUserName(username);
            UcV1SysPrivListResponse ucV1SysPrivListResponse = UcClient.v1SysPrivList(request);
            List<UcV1SysPrivListResultVO> result = ucV1SysPrivListResponse.getResult();
            redisTemplate.opsForValue().set(privKey, result, 1, TimeUnit.DAYS);
            return result;
        } catch (UcException e) {
            logger.error(e);
            return new ArrayList<>();
        }
    }
}