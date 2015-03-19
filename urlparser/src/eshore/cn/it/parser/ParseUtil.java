package eshore.cn.it.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eshore.cn.it.data.DataFilter;
import eshore.cn.it.data.DataLoader;

public class ParseUtil {
	/**
	 * method<code>hasDigit</code>
	 * 判断是否为含有数字
	 * @param str 传入的字符串  
	 * @return 是整数返回true,否则返回false  
	 */    
	public static boolean hasDigit(String content) {
		boolean flag = false;
		Pattern p = Pattern.compile(".*\\d+.*");
		Matcher m = p.matcher(content);
		if (m.matches())
			flag = true;
		return flag;
	}

	public static String filterAdd(String t) {
		if(t == null || t.equalsIgnoreCase("") || hasDigit(t) ||
			DataFilter.stopWordFilter(t, DataLoader.loadFilterWords()) )
			return null;
		return t;
	}
	
	public static boolean isContainOther(String[] words1, String[] words2) {
		
		for(String tmp : words2) {
			int mark = 0;
			for(; mark < words1.length; mark++)
				if(words1[mark].equals(tmp))
					break;
			if(mark >= words1.length)
				return false;
		}
	
		return true;
	}

}
