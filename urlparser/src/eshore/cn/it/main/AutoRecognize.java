package eshore.cn.it.main;

import eshore.cn.it.parser.LableRecognizer;

public class AutoRecognize {

	public static void main(String[] args) {
		String url = "";
		//url = "http://sports.sina.com.cn/g/laliga/2015-03-16/12217544685.shtml";
		url = "http://hupy.iteye.com/blog/871085";
		//url = "http://woodpecker.org.cn/abyteofpython_cn/chinese/index.html";
		LableRecognizer recognize = new LableRecognizer();
		recognize.recognize(url);
	}

}
