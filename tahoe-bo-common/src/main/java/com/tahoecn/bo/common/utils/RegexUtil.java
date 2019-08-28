package com.tahoecn.bo.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 正则表达式工具包
 * @author panglx
 *
 */
public class RegexUtil {

	/**
	 * 在指定文本中根据正则表达式匹配字符串,将匹配结果以列表返回
	 * e.g :
	 * 	content: qwertyu123qwe
	 * 	regex: (qwe).+(\\d+)qwe
	 * 	group: 2
	 * 	return: 123
	 * @param content 指定文本
	 * @param regex 正则表达式
	 * @param group 组id
	 * @return
	 */
	public static List<String> match(String content,String regex,int group){
		return match(match(content, regex), group);
	}

	/**
	 * 返回匹配对象
	 * @param content
	 * @param regex
	 * @return
	 */
	public static Matcher match(String content,String regex){
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(content);
		return matcher;
	}
	/**
	 * 将匹配对象 以列表返回，根据分组
	 * @return
	 */
	public static List<String> match(Matcher matcher,Integer group){
		List<String> list = new ArrayList<String>();
		while(matcher.find()){
			String group2 = matcher.group(group);
			list.add(group2);
		}
		return list;
	}

}
