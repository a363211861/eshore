package eshore.cn.it.test;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import eshore.cn.it.parser.UrlParser;

public class UrlParserTest {

	@Test
	public void testParseExecute() {
		UrlParser up = new UrlParser();
		String url = "http://news.sina.com.cn/guide/"; //指定特定页面，可以作为命令行参数
		String cssPath = "div#tab01 a";						   //具体css path, 控制解析范围	
		//up.parseExecute(url, cssPath);
		//Map<String, Set<String>> textLinks = up.getTextLinks();
		
		//assert(textLinks.containsKey("长微博"));
	}

}
