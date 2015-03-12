package eshore.cn.it.data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	
	public static List<SiteMap> loadSiteMaps() {
		if (siteMaps == null) {
			siteMaps = new ArrayList<SiteMap>();
			File file = new File("resource", siteMapsFileName);
			try {
				List<String> lines = IOUtils.readLines(new FileReader(file));
				for (int i = 0; i < lines.size(); i+=2) {
					SiteMap sm = new SiteMap();
					String line = lines.get(i).trim();
					if (line.startsWith(commentPattern))
						sm.setComment(line.substring(1));
					line = lines.get(i + 1).trim();
					String[] tmps = line.split("\\|");
					if(tmps.length != 2)
						System.err.println("文件格式书写错误，必须一行注释一行数据，数据格式为：url|selectStr");
					else {
						sm.setUrl(tmps[0]);
						sm.setSelectStr(tmps[1]);
					}
					siteMaps.add(sm);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return siteMaps;
	}
	
	public static List<String> loadFilterWords() {
		if(filterWords == null) {
			filterWords = new ArrayList<String>();
			File file = new File("resource", filterWordsFileName);
			try {
				List<String> lines = IOUtils.readLines(new FileReader(file));
				for (int i = 0; i < lines.size(); i++) {
					filterWords.add(lines.get(i).trim());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return filterWords;
	}
	
	
	public static void main(String[] args) {
		System.out.println(DataLoader.loadSiteMaps());
		System.out.println(DataLoader.loadFilterWords());
		System.out.println(DataFilter.stopWordFilter("hello", DataLoader.loadFilterWords()));
	}
}
