package eshore.cn.it.main;

import java.util.List;

import eshore.cn.it.data.DataLoader;
import eshore.cn.it.data.DataWriter;
import eshore.cn.it.data.SiteMap;
import eshore.cn.it.parser.UrlParser;

public class ParseStart {
	//数据保存目录为
	private static final String DATADIR = "data";
	private static final String KEYWORDSFILENAME = "keywords.txt";
	public static void main(String[] args) {
		UrlParser parser = new UrlParser();
		List<SiteMap> sms = DataLoader.loadSiteMaps();
		for (SiteMap sm : sms)
			parser.parseExecute(sm.getUrl(), sm.getSelectStr());
		DataWriter.writeCSVTextKeyWords(DATADIR, KEYWORDSFILENAME, parser.getTextKeyWords());
	}

}