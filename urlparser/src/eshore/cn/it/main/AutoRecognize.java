package eshore.cn.it.main;

import eshore.cn.it.parser.LableRecognizer;

public class AutoRecognize {

	public static void main(String[] args) {
		String url = "";
		//url= "http://collection.sina.com.cn/cpsc/20150318/0957182477.shtml";
		url = "http://weather.news.sina.com.cn/news/2015/0317/1650106811.html";
		//url = "http://weather.sina.com.cn/photos/beijing";
		//url = "http://slide.news.sina.com.cn/d/slide_1_31171_81493.html#p=1";
		LableRecognizer recognize = new LableRecognizer();
		System.out.println(recognize.recognize(url));
	}

}
