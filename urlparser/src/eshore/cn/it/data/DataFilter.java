package eshore.cn.it.data;

import java.util.Collection;
import java.util.List;

/**
 * class  <code>DataFilter</code>
 * 此类通过对一定的方式过滤掉一些不想要的关键词
 * @author   clebeg
 * @version	 0.0.1
 * @see      java.lang.Class
 * @since    JDK1.8
 * */
public class DataFilter {
	
	/**
	 * method  <code>stopWordFilter</code>
	 * 静态方法，判断某个词语是否是需要过滤的词语，如果是就返回true，否则返回false
	 * 当停用词很多的时候，这个方法效率不高，可以重写提高此方法的效率
	 * @param word 需要判断是否被过滤的词
	 * @param stopWords 停用词库
	 * @return 是否需要过滤 如果是就返回true，否则返回false
	 * */
	public static boolean stopWordFilter(String word, List<String> stopWords) {
		for (String str : stopWords) {
			//如果是以^开头的停用词，表示只要以这个词开头就过滤掉
			if (str.startsWith("^")) {
				if (word.startsWith(str.substring(1)))
					return true;
			} else {
				if (word.equals(str))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * method  <code>findIncludeString</code>
	 * 静态方法，判断某个词语是否在某个集合中有包含的词， 如果是就返回包含的字符串，否则返回null
	 * 使用场景：比如判断是否是某个域名的二级域名
	 * 当集合中包含的词很多的时候，这个方法效率不高，可以重写提高此方法的效率
	 * @param word 需要判断某集合中的词被它包含
	 * @param stopWords 集合中包含的词
	 * @return 是否被它包含 如果是就返回包含的字符串，否则返回null
	 * */
	public static String findIncludeString(String word, Collection<String> findCollections) {
		for (String str : findCollections) {
			if(word.contains(str));
				return str;
		}
		return null;
	}
}
