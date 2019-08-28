package com.tahoecn.bo.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 字符串工具类
 * @author solutionNC 2017年12月27日
 */
public class StrUtils {

	/** 逗号（,） */
	public static final String DELIMIT_COMMA = ",";
	/** 英文句号（.） */
	public static final String DELIMIT_STOP = ".";
	/** （“”）空字符串 */
	private static final String BLANK_STRING = "";

	/**
	 * 判断是否为null 或空字符串 或字符串中全是空格 或字符串("null").
	 * <pre>
	 * StrUtils.isEmpty(null)      = true
	 * StrUtils.isEmpty("")        = true
	 * StrUtils.isEmpty(" ")       = true
	 * StrUtils.isEmpty("null")    = true
	 * StrUtils.isEmpty("NULL")    = false
	 * StrUtils.isEmpty(" null ")  = false
	 * </pre>
	 * 
	 * @param s 字符串
	 * @return 是 true；否 false 。
	 * 
	 */
	public static boolean isEmpty(final String s) {
		// null
		if (s == null) {
			return true;
		}
		return (s.isEmpty() || s.trim().length() == 0) || "null".equals(s);
	}
	
	/**
	 * 判断是否为非null 或非空字符串 或字符串中包含非空格字符 或非字符串("null").
	 * <pre>
	 * StrUtils.isNotEmpty(null)      = false
	 * StrUtils.isNotEmpty("")        = false
	 * StrUtils.isNotEmpty(" ")       = false
	 * StrUtils.isNotEmpty("null")    = false
	 * StrUtils.isNotEmpty("NULL")    = true
	 * StrUtils.isNotEmpty(" null ")  = true
	 * </pre>
	 * 
	 * @param s 字符串
	 * @return 是 true； 否 false 。
	 * 
	 */
	public static boolean isNotEmpty(final String s) {
		return !isEmpty(s);
	}

	/**
	 * 按分隔符将字符串组装为字符串数组.</br>
	 * 如果传入参数为null或空串，直接返回空数组。
	 * <pre>
	 * StrUtils.split("",",")          = {}
	 * StrUtils.split("null,b",",")    = {"null","b"}
	 * StrUtils.split("1,2,3,a,b",",") = {"1","2","3","a","b"}
	 * </pre>
	 * @param s 待分割字符串
	 * @param delimiter 分割符
	 * @return 字符串数组
	 */
	public static String[] split(String s, String delimiter) {
		if (isNotEmpty(s)) {
			return s.split(delimiter);
		}
		return new String[] {};
	}

	/**
	 * 按逗号分隔符（“,”）将字符串组装为字符串List集合.</br>
	 * 如果传入参数为null或空串，直接返回空List。</br>
	 * PS:按10进制转换字符串
	 * <pre>
	 * StrUtils.extractIntegerList(null)               = {}
	 * StrUtils.extractIntegerList(" ")                = {}
	 * StrUtils.extractIntegerList(",bob,1,2,3,4,a,5") = {1,2,3,4,5}
	 * </pre>
	 * 
	 * @param str 待分割字符串
	 * @return List&lt;Integer&gt;
	 */
	public static List<Integer> extractIntegerList(String str) {
		List<Integer> list = new ArrayList<Integer>();
		if (str == null) {
			return list;
		}
		String[] strArr = str.split(DELIMIT_COMMA);
		for (String s : strArr) {
			for (char cs : s.toCharArray()) {
				// 包含非空格字符
				if (cs > '9' || cs < '0') {
					break;
				}
			}
			try {
				list.add(Integer.parseInt(s, 10));
			} catch (Exception e) {
				continue;
			}
		}
		return list;
	}

	/**
	 * 按逗号分隔符（“,”）将字符串组装为字符串List集合.</br>
	 * 如果传入参数为null或空串，直接返回空List。</br>
	 * PS:按10进制转换字符串
	 * <pre>
	 * StrUtils.extractLongList(null)               = {}
	 * StrUtils.extractLongList(" ")                = {}
	 * StrUtils.extractLongList(",bob,1,2,3,4,a,5") = {1,2,3,4,5}
	 * </pre>
	 * @param str 待分割字符串
	 * @return List&lt;Long&gt;
	 */
	public static List<Long> extractLongList(String str) {
		List<Long> list = new ArrayList<Long>();
		if (str == null) {
			return list;
		}
		String[] strArr = str.split(DELIMIT_COMMA);
		for (String s : strArr) {
			for (char cs : s.toCharArray()) {
				// 包含非空格字符
				if (cs > '9' || cs < '0') {
					break;
				}
			}
			try {
				list.add(Long.parseLong(s, 10));
			} catch (Exception e) {
				continue;
			}
		}
		return list;
	}

	/**
	 * 按分隔符将字符串组装为字符串List集合.</br>
	 * 如果传入参数为null或空串，直接放回空List。
	 * <pre>
	 * StrUtils.splitToStringList(null)            = {}
	 * StrUtils.splitToStringList(" ")             = {}
	 * StrUtils.splitToStringList(",bob,1,null,5") = {"","bob","1","null","5"}
	 * </pre>
	 * @param str 待分割字符串
	 * @param delimiter 分割符
	 * @return List&lt;String&gt;
	 */
	public static List<String> splitToStringList(String str, String delimiter) {
		List<String> list = new ArrayList<String>();
		if (str == null) {
			return list;
		}
		String[] sArr = split(str, delimiter);
		for (int i = 0; i < sArr.length; i++) {
			list.add(sArr[i]);
		}
		return list;
	}

	/**
	 * 连接返回字符串.
	 * <pre>
	 * StrUtils.join({"hello"," ","world","!"},"")  = "hello world!"
	 * StrUtils.join({"hello"," ","world","!"},",") = "hello, ,world,!"
	 * </pre>
	 * @param collection 待连接成字符串的集合
	 * @param separator 连接符
	 * @return String
	 */
	public static String join(Collection<?> collection, String separator) {
		return join(collection, separator, BLANK_STRING, BLANK_STRING);
	}

	/**
	 * 连接返回字符串并添加前缀和后缀.
	 * <pre>
	 * StrUtils.join({"hello"," ","world","!"},",","p","s") = "phellos,p s,pworlds,p!s"
	 * </pre>
	 * @param collection 待连接成字符串的集合
	 * @param separator 连接符
	 * @param prefix 前缀，添加到每个集合元素前
	 * @param suffix 后缀，添加到每个集合元素后
	 * @return String
	 */
	public static String join(Collection<?> collection, String separator, String prefix, String suffix) {
		if (collection == null || collection.isEmpty()) {
			return BLANK_STRING;
		}
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = collection.iterator();
		while (it.hasNext()) {
			sb.append(prefix).append(it.next()).append(suffix);
			if (it.hasNext()) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	/**
	 * 高亮处理当前匹配的关键词.</br>
	 * PS: 给keyword添加&lt;em&gt;标签。
	 * <pre>
	 * StrUtils.highLightHtml("hello world!","hello")       = "&lt;em&gt;hello&lt;/em&gt; world!"
	 * --> <em>hello</em> world!
	 * StrUtils.highLightHtml("hello world!","hello|world") = "&lt;em&gt;hello&lt;/em&gt; &lt;em&gt;world&lt;/em&gt;!"
	 * --> <em>hello</em> <em>world</em>!
	 * </pre>
	 * @param content 待高亮处理的文本
	 * @param keyword 关键字 (多关键字用"|"分隔 如：张三|李四)
	 * @return String
	 */
	public static String highlightHtml(String content, String keyword) {
		Pattern p = Pattern.compile(keyword);
		Matcher m = p.matcher(content);

		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "<em>$0</em>");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 判断字符串（str）中是否包含指定字符串（subStr）.
	 * <pre>
	 * StrUtils.contains(null, "b") = false;
	 * StrUtils.contains("bob!", null) = false;
	 * StrUtils.contains("bob!", "a") = false;
	 * StrUtils.contains("bob!", "b") = true;
	 * </pre>
	 * @param str 待判断的字符串
	 * @param subStr 被包含的字符串
	 * @return true 包含； false 不包含 。
	 */
	public static boolean contains(String str, String subStr) {
		if (str == null || subStr == null) {
			return false;
		}
		return str.indexOf(subStr) > -1;
	}

	/**
	 * 
	 * 判断用delimiter分隔的str中是否包含subStr.
	 * <pre>
	 * StrUtils.contains(null, "b", ",") = false;
	 * StrUtils.contains("a,b,c", null, "") = false;
	 * StrUtils.contains("a,b,c", "b", null) = false;
	 * StrUtils.contains("a,bob,c", "b", ",") = false;
	 * StrUtils.contains("a,b,c", "b", ",") = true;
	 * </pre>
	 * @param str 待分割的字符串
	 * @param delimiter 分隔符
	 * @param subStr 被包含的字符串
	 * @return true 包含；false 不包含
	 *
	 */
	public static boolean contains(String str, String delimiter, String subStr) {
		if (str == null || subStr == null || delimiter == null) {
			return false;
		}
		String[] splitStr = split(str, delimiter);
		if (splitStr.length > 0) {
			for (int i = 0; i < splitStr.length; i++) {
				if (subStr.equals(splitStr[i])) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	  * 参数非null判断，返回第一个非null的对象的toString()，全部为null时，返回空字符串.
	  * <pre>
	  * StrUtils.getFirstNonNullStr(null)         = "";
	  * StrUtils.getFirstNonNullStr(null,"a")     = "a";
	  * StrUtils.getFirstNonNullStr(null,"b","a") = "b";
	  * </pre>
	  * @param obj 待判断对象
	  * @return String
	  *
	 */
	public static String getFirstNonNullStr(Object ... obj) {
		if (obj == null) {
			return BLANK_STRING;
		}
		for (int i = 0; i < obj.length; i++) {
			if(obj[i] != null) {
				return String.valueOf(obj[i]);
			}
		}
		return BLANK_STRING;
	}
	
}
