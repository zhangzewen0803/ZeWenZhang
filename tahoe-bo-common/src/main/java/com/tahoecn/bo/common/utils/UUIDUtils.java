package com.tahoecn.bo.common.utils;

import java.util.UUID;
/**
 * 
 * 类名称：UUIDUtils
 * 类描述：生成UUID工具类
 * 创建人：yyd
 * 创建时间：2018年7月3日 下午2:32:08
 * @version 1.0.0
 */
public class UUIDUtils {

    /**
     * 创建32位uuid字符串
     *
     * @return 返回32位字符串
     */
    public static final String create() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
/*    public static void main(String[] args) {
		System.out.println(create());
	}*/
}
