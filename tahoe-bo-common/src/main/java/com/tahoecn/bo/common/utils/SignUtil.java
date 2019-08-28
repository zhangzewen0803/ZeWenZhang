package com.tahoecn.bo.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 功能：签名处理核心文件 说明：用于MD5和SHA1的签名和校验
 * 
 * @author liuqs
 * @version 1.0
 * @since 2017年12月26日 上午9:35:30
 */
public class SignUtil {

	private static Logger logger = LoggerFactory.getLogger(SignUtil.class);

	// 默认的加密编码
	public static String DEFAULT_INPUT_CHARSET = "UTF-8";

	// 默认的加密方式SHA1
	public static String SHA1_SIGN_TYPE = "SHA1";

	// MD5加密方式
	public static String MD5_SIGN_TYPE = "MD5";

	// 加密方式
	public static String SIGN_TYPE = "sign_type";

	/**
	 * 签名字符串
	 * 
	 * @param text
	 *            需要签名的字符串
	 * @param key
	 *            密钥
	 * @param input_charset
	 *            编码格式
	 * @return 签名结果
	 */
	public static String sign(String text, String key, String input_charset, String sign_type) {
		text = text + key;
		String signResult = "";
		if (MD5_SIGN_TYPE.equalsIgnoreCase(sign_type)) {
			signResult = DigestUtils.md5Hex(getContentBytes(text, input_charset));
		} else if (SHA1_SIGN_TYPE.equalsIgnoreCase(sign_type)) {
			signResult = DigestUtils.sha1Hex(getContentBytes(text, input_charset));
		} else {
			signResult = DigestUtils.sha1Hex(getContentBytes(text, input_charset));
		}
		return signResult;
	}

	/**
	 * 默认MD5签名、编码UTF-8
	 * 
	 * @param text
	 * @param key
	 * @return
	 */
	public static String sign(String text, String key) {
		return sign(text, key, DEFAULT_INPUT_CHARSET, MD5_SIGN_TYPE);
	}

	/**
	 * 签名字符串
	 * 
	 * @param text
	 *            需要签名的字符串
	 * @param sign
	 *            签名结果
	 * @param key
	 *            密钥
	 * @param input_charset
	 *            编码格式
	 * @return 签名结果
	 */
	public static boolean verify(String text, String sign, String key, String input_charset, String sign_type) {
		text = text + key;
		String mysign = "";
		if (MD5_SIGN_TYPE.equalsIgnoreCase(sign_type)) {
			mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
		} else if (SHA1_SIGN_TYPE.equalsIgnoreCase(sign_type)) {
			mysign = DigestUtils.sha1Hex(getContentBytes(text, input_charset));
		} else {
			mysign = DigestUtils.sha1Hex(getContentBytes(text, input_charset));
		}
		logger.info("签名之后的sign：" + mysign);
		if (mysign.equals(sign)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 默认MD5校验方法、UTF-8编码
	 * 
	 * @param text
	 * @param sign
	 * @param key
	 * @return
	 */
	public static boolean verify(String text, String sign, String key) {
		return verify(text, sign, key, DEFAULT_INPUT_CHARSET, MD5_SIGN_TYPE);
	}

	/**
	 * @param content
	 * @param charset
	 * @return
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException
	 */
	private static byte[] getContentBytes(String content, String charset) {
		if (StringUtils.isBlank(charset)) {
			return content.getBytes();
		}
		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
		}
	}

	/**
	 * 除去数组中的空值和签名参数
	 * 
	 * @param sArray
	 *            签名参数组
	 * @return 去掉空值与签名参数后的新签名参数组
	 */
	public static Map<String, String> paraFilter(Map<String, String> sArray) {

		Map<String, String> result = new HashMap<String, String>();

		if (sArray == null || sArray.size() <= 0) {
			return result;
		}

		for (String key : sArray.keySet()) {
			String value = sArray.get(key);
			if (StringUtils.isBlank(value) || "sign".equalsIgnoreCase(key) || "sign_type".equalsIgnoreCase(key)
					|| "token".equalsIgnoreCase(key)) {
				continue;
			}
			result.put(key, value);
		}

		return result;
	}

	/**
	 * 构建&连接符拼接的字符串，用于构建get请求参数和签名字符串。
	 * 
	 * @param params
	 * @return
	 */
	public static String createLinkString(Map<String, String> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);

			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}

	/**
	 * 除sign与SIGN_TYPE之外全部参数参与签名
	 * 
	 * @param sParaTemp
	 * @param key
	 * @return
	 */
	public static Map<String, String> buildRequestPara(Map<String, String> sParaTemp, String key) {

		String sign_type = sParaTemp.get(SIGN_TYPE);
		if (StringUtils.isBlank(sign_type)) {
			sign_type = SHA1_SIGN_TYPE;
		}
		// 除去数组中的空值和签名参数
		Map<String, String> sPara = paraFilter(sParaTemp);
		// 生成签名字符串
		String signString = SignUtil.createLinkString(sPara);
		String input_charset = sPara.get("_input_charset");
		if (StringUtils.isBlank(input_charset)) {
			input_charset = "utf-8";
		}
		String mysign = SignUtil.sign(signString, key, input_charset, sign_type);
		// 签名结果与签名方式加入请求提交参数组中
		sPara.put("sign", mysign);
		sPara.put(SIGN_TYPE, sign_type);

		return sPara;
	}

	/**
	 * 只有timestamp的值与priv_key参与签名，签名结果保存到token参数上
	 * 
	 * @param sParaTemp
	 * @param priv_key
	 * @return
	 */
	public static Map<String, String> buildRequestPara4UC(Map<String, String> sParaTemp, String priv_key) {
		// 除去数组中的空值和签名参数
		Map<String, String> sPara = paraFilter(sParaTemp);
		// 生成签名字符串，只有参数timestamp参与签名
		String signString = sParaTemp.get("timestamp");
		String mysign = SignUtil.sign(signString, priv_key);
		// 签名结果与签名方式加入请求提交参数组中
		sPara.put("token", mysign);
		return sPara;
	}

	/**
	 * 根据传入的信息，生成签名结果
	 * 
	 * @param params
	 *            传入的参数数组
	 * @param sign
	 *            比对的签名结果
	 * @return 生成的签名结果
	 * @throws Exception
	 */
	public static boolean getSignVeryfy(Map<String, String> params, String sign, String channelKey) {
		boolean flag = true;
		// 过滤空值、sign与sign_type参数
		String sign_type = params.get(SIGN_TYPE);
		Map<String, String> sParaNew = paraFilter(params);
		// 获取待签名字符串
		String preSignStr = createLinkString(sParaNew);
		// 获取签名字符集
		String input_charset = params.get("_input_charset");
		if (StringUtils.isEmpty(input_charset)) {
			input_charset = DEFAULT_INPUT_CHARSET;
		}
		// 获得签名验证结果
		boolean isSign = false;
		logger.info("签名之前字符串:" + preSignStr);
		isSign = verify(preSignStr, sign, channelKey, input_charset, sign_type);
		if (!isSign) {
			flag = false;
		}
		return flag;
	}
}