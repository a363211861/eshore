package eshore.cn.it.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import eshore.cn.it.data.DataFilter;
import eshore.cn.it.data.DataLoader;
import eshore.cn.it.data.DataWriter;
import eshore.cn.it.data.KeyWord;
import eshore.cn.it.data.SiteMap;

/**
 * class  <code>UrlParser</code>
 * 此类通过下载指定页面抽取锚链接以及锚文本信息
 * 通过特定算法识别锚文本信息对应的关键英文单词
 * 为以后实现通过url链接识别网页类别积累语料库
 * 结果目前保存为 data目录下 keywords.csv文件
 * 其中特色为关键词识别算法，详情况请看： parseTextKeyWords
 * 整个类的设计采用mvc三层架构，具有一定的通用性，对按照真是意义编码的url网站具有普遍适应性
 * 可以将爬取层单独成组件 、可以将解析层单独作为组件、可以将持久层单独成组件
 * @author   clebeg
 * @version	 0.0.1
 * @see      java.lang.Class
 * @since    JDK1.8
 * */
public class UrlParser {
	//指定记录锚文本以及链接，为关键字提取做准备
	private Map<String, Set<KeyWord>> textLinks = new LinkedHashMap<String, Set<KeyWord>>();;
	private static int MAXID = -1;
	//数据保存目录为
	private String dataDir = "data";
	
	public static void main(String[] args) {
		UrlParser parser = new UrlParser();
		List<SiteMap> sms = DataLoader.loadSiteMaps();
		for (SiteMap sm : sms)
			parser.parseExecute(sm);
		
		DataWriter.writeCSVTextKeyWords(parser.dataDir, "keywords.txt", parser.textLinks);
	}
	
	public void parseExecute(SiteMap sm) {
		try {
			parseTextLinks(sm);		//第一步：获取想要的锚文本链接信息
			parseTextKeyWords();	//第二步：获取想要的锚文本及关键
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
	}
	
	/**
	 * method<code>parseTextLinks</code>
	 * 该方法通过利用Jsoup下载网页提取指定cssPath包围的部分链接
	 * 按照锚文本作为关键字，保存到map中
	 * @param sm 需要提取信息的网站
	 * @throws IOException 
	 */
	private void parseTextLinks(SiteMap sm) throws IOException {
		//注意这里使用 HashMap 是线程不安全的，以后扩展为多线程是要特别注意
		KeyWord oneSite = new KeyWord();
		oneSite.setUrl(sm.getIndexUrl());
		if (MAXID == -1)
			MAXID = Integer.parseInt(DataWriter.getMaxKeyWordsId(this.dataDir, "keywords.txt")) + 1;
		oneSite.setId("" + MAXID);
		oneSite.setPid("-1");
		oneSite.setLabel(sm.getSiteName());
		MAXID++;
		Set<KeyWord> ls = this.textLinks.get(oneSite.getUrl());
		if (ls == null) {
			ls = new LinkedHashSet<KeyWord>();
			ls.add(oneSite);
			this.textLinks.put(oneSite.getUrl(), ls);
		} else 
			ls.add(oneSite);
		
		
		//解析成 dom 树
		Document doc = Jsoup.connect(sm.getNavigateUrl()).get();
		//通过jsoup的特殊选择器选择所有的链接
        Elements elements = doc.select(sm.getSelectStr());
        
		for (Element e : elements) {
			Elements links = e.select("a");
			if (links.size() >= 1) {
				String tmpId = "-1";
				for(int i = 0; i < links.size(); i++) {
					KeyWord secondLevel = new KeyWord();
					
					String tmpStr = links.get(i).text().trim();
					secondLevel.setLabel(tmpStr);
					
					secondLevel.setUrl(links.get(i).attr("abs:href"));
					secondLevel.setId("" + MAXID);
					
					if (i == 0) {
						tmpId = secondLevel.getId();
						secondLevel.setPid(oneSite.getId());
					} else 
						secondLevel.setPid(tmpId);
					
					ls.add(secondLevel);
					MAXID++;
				}
			}
	       
		}
	}
	
	
	/**
	 * method<code>parseTextKeyWords</code>
	 * 遍历所有的锚文本为key链接为values的map
	 * 按照特定的算法提取链接中的关键词
	 */
	private void parseTextKeyWords() {
											
		Set<Entry<String, Set<KeyWord>>> es = this.textLinks.entrySet();
		Iterator<Entry<String, Set<KeyWord>>> it = es.iterator();
		
		//循环所有的链接，提取关键字
		while(it.hasNext()) {											
			Entry<String, Set<KeyWord>> textLink = it.next();
			Set<KeyWord> values = textLink.getValue();
			getKeyWords(values);
		}
	
		
	}

	/**
	 * method<code>getKeyWords</code>
	 * 提取链接关键字，通过比较原始链接域名，自身链接特殊关键词
	 * 按照特定的算法提取链接中的关键词
	 * @param values	   此关键字对应所有的urls
	 */
	private void getKeyWords(Set<KeyWord> values) {
		//循环提取每个链接的关键词
		for (KeyWord keyWord : values) {
			try {
				URL u = new URL(keyWord.getUrl());
				keyWord.setHost(u.getHost());
					
				String keyWords = "";
				
				//将在对应路径信息中将所有的关键词分解
				String[] strs = u.getPath().split("[./]");
				for (int i = 0; i < strs.length; i++) {
					String tmp = filterAdd(strs[i].trim());
					if (tmp != null)
						keyWords += tmp + "|";
				}
				if (keyWords.equalsIgnoreCase("") == false)
					keyWord.setKeywords(keyWords.substring(0, keyWords.length() - 1));
				else 
					keyWord.setKeywords("");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	private String filterAdd(String t) {
		if(t == null || t.equalsIgnoreCase("") || hasDigit(t) ||
			DataFilter.stopWordFilter(t, DataLoader.loadFilterWords()) )
			return null;
		return t;
	}
	/**
	 * method<code>hasDigit</code>
	 * 判断是否为含有数字
	 * @param str 传入的字符串  
	 * @return 是整数返回true,否则返回false  
	 */    
	public boolean hasDigit(String content) {
		boolean flag = false;
		Pattern p = Pattern.compile(".*\\d+.*");
		Matcher m = p.matcher(content);
		if (m.matches())
			flag = true;
		return flag;
	}


	public Map<String, Set<KeyWord>> getTextLinks() {
		return textLinks;
	}
	
}
