package com.tahoecn.bo.controller;

import com.tahoecn.bo.model.vo.CurrentUserVO;
import com.tahoecn.core.json.JSONResult;
import com.tahoecn.log.Log;
import com.tahoecn.log.LogFactory;
import com.tahoecn.security.SecureUtil;
import com.tahoecn.uc.UcClient;
import com.tahoecn.uc.exception.UcException;
import com.tahoecn.uc.request.UcV1UserInfoRequest;
import com.tahoecn.uc.response.UcV1UserInfoResponse;
import com.tahoecn.uc.sso.SSOHelper;
import com.tahoecn.uc.sso.security.token.SSOToken;
import com.tahoecn.uc.vo.UcV1UserInfoResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Calendar;
import java.util.List;

@Component
public class TahoeBaseController {

    @Autowired
    private RedisTemplate redisTemplate;

    private static final Log logger = LogFactory.get();

    @Value("${tahoe.application.physicalPath}")
    private String physicalPath;

    public JSONResult uploadFiles(List<MultipartFile> files) {
        File dir;
        JSONResult json = new JSONResult();

        for (MultipartFile file : files) {
            String extensionName = "";
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && !"".equals(originalFilename)) {
                int index = originalFilename.lastIndexOf(".");
                if (index > 0) {
                    extensionName = originalFilename.substring(index, originalFilename.length());
                }
            }

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            String ypath = physicalPath + "/" + year;
            String mpath = physicalPath + "/" + year + "/" + month;

            //检查目录是否存在，存在就直接使用，不存在就创建目录
            dir = new File(ypath);
            if (!dir.exists()) {
                dir = new File(mpath);
                dir.mkdirs();
            } else {
                dir = new File(mpath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }

            //获取一个UUID来作为存入服务器中的文件的名字
            String filename = SecureUtil.simpleUUID();
            filename = filename + extensionName;
            try {
                //将文件转存到指定位置
                file.transferTo(new File(dir, filename));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //将文件的服务器地址存到数据库
            String path = "/" + year + "/" + month + "/" + filename;

            json.setCode(0);
            json.setMsg("upload files success!");
            json.setData(path);
        }

        return json;
    }

    public SSOToken getSSOToken() {
        return SSOHelper.getSSOToken(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    public CurrentUserVO getCurrentUser() {
        SSOToken ssoToken = getSSOToken();
        if (ssoToken == null) {
            throw new RuntimeException("当前无用户信息，请检查单点登录是否开启，并确保请求的URI在单点登录拦截之内。");
        }
        String key = "bo_user_info_" + ssoToken.getId();
        UcV1UserInfoResultVO userInfoResultVO = (UcV1UserInfoResultVO) redisTemplate.opsForValue().get(key);
        if (userInfoResultVO == null) {
            UcV1UserInfoRequest request = new UcV1UserInfoRequest();
            request.setUserSid(ssoToken.getId());
            try {
                UcV1UserInfoResponse ucV1UserInfoResponse = UcClient.v1UserInfo(request);
                userInfoResultVO = ucV1UserInfoResponse.getResult();
                redisTemplate.opsForValue().set(key,userInfoResultVO);
            } catch (UcException e) {
                logger.error(e);
            }
        }
        CurrentUserVO currentUserVO = new CurrentUserVO();
        currentUserVO.setId(ssoToken.getId());
        if (userInfoResultVO != null) {
            currentUserVO.setName(userInfoResultVO.getFdName());
        }
        currentUserVO.setUsername(ssoToken.getIssuer());
        return currentUserVO;
    }

}
