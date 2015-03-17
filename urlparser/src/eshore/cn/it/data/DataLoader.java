package eshore.cn.it.data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

/**
 * class  <code>DataLoader</code>
 * 此类通过指定存放网站地址文件的，读取文件并解析成SiteMap对象
 * 目前是全部导入内存一次处理，将来如果工作量太大，则需要多线程处理
 * @author   clebeg
 * @version	 0.0.1
 * @see      java.lang.Class
 * @since    JDK1.8
 * */
public class DataLoader {
	//sitemap文件地址，里面包含网址和过滤条件
	private static final String siteMapsFileName = "sitemaps.txt";
	
	//sitemap文件地址，里面包含网址和过滤条件
	private static final String filterWordsFileName = "filterWords.txt";
	//注释条件
	private static final String commentPattern = "#"; 
	
	private static List<SiteMap> siteMaps = null;
	private static List<String> filterWords = null;
	private static Map<String, KeyWord> keyWords = null;
	public static int MAX_DONE_ID = -1;
	public static List<SiteMap> loadSiteMaps() {
		if (siteMaps == null) {
			siteMaps = new ArrayList<SiteMap>();
			File file = new File("resource", siteMapsFileName);
			FileReader reader = null;
			try {
				reader = new FileReader(file);
				List<String> lines = IOUtils.readLines(reader);
				if (lines.size() <= DataLoader.getMaxDoneId()) {
					System.err.println("没有网站需要处理，请添加按照要求添加网站。");
					System.exit(1);
				}
				for (int i = DataLoader.getMaxDoneId(); i < lines.size(); i++) {
					SiteMap sm = new SiteMap();
					String line = lines.get(i).trim();
					if (line.startsWith(commentPattern))
						continue;
					line = lines.get(i).trim();
					String[] tmps = line.split(",");
					if(tmps.length != 5) {
						System.err.println("文件格式书写错误，请按照要求填写所有属性，所有属性不能为空。");
						System.exit(1);
					}
					else {
						sm.setId(tmps[0]);
						sm.setSiteName(tmps[1]);
						sm.setIndexUrl(tmps[2]);
						sm.setNavigateUrl(tmps[3]);
						sm.setSelectStr(tmps[4]);
					}
					siteMaps.add(sm);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}
		return siteMaps;
	}
	
	public static List<String> loadFilterWords() {
		if(filterWords == null) {
			filterWords = new ArrayList<String>();
			File file = new File("resource", filterWordsFileName);
			FileReader reader = null;
			try {
				reader = new FileReader(file);
				List<String> lines = IOUtils.readLines(reader);
				for (int i = 0; i < lines.size(); i++) {
					filterWords.add(lines.get(i).trim());
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}
		return filterWords;
	}
	
	public static Map<String, KeyWord> loadKeyWords() {
		if (keyWords == null) {
			keyWords = new LinkedHashMap<String, KeyWord>();
			File file = new File("data", "keywords.txt");
			FileReader reader = null;
			try {
				reader = new FileReader(file);
				List<String> lines = IOUtils.readLines(reader);
				if (lines.size() <= 1) {
					System.err.println("没有关键词信息！");
					System.exit(1);
				}
				for (int i = 1; i < lines.size(); i++) {
					KeyWord sm = new KeyWord();
					String line = lines.get(i).trim();
					if (line.startsWith(commentPattern))
						continue;
					line = lines.get(i).trim();
					String[] tmps = line.split(",");
					if(tmps.length != 6) {
						System.err.println("文件格式书写错误，请按照要求填写所有属性，所有属性不能为空。");
						System.exit(1);
					}
					else {
						//1,-1,http://www.sina.com.cn/,www.sina.com.cn,,新浪网
						sm.setId(tmps[0]);
						sm.setPid(tmps[1]);
						sm.setUrl(tmps[2]);
						sm.setHost(tmps[3]);
						sm.setKeywords(tmps[4]);
						sm.setLabel(tmps[5]);
					}
					keyWords.put(sm.getId(), sm);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}
		return keyWords;
	}
	
	public static void main(String[] args) {
		System.out.println(DataLoader.loadSiteMaps());
		System.out.println(DataLoader.loadFilterWords());
		System.out.println(DataFilter.stopWordFilter("hello", DataLoader.loadFilterWords()));
	}

	public static int getMaxDoneId() {
		if (MAX_DONE_ID == -1) {
			File file = new File("resource", "MAXDONEID");
			FileReader reader = null;
			try {
				reader = new FileReader(file);
				List<String> lines = IOUtils.readLines(reader);
				MAX_DONE_ID = Integer.parseInt(lines.get(0).trim());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}
		return MAX_DONE_ID;
	}
}
