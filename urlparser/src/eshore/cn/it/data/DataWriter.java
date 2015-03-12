package eshore.cn.it.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class DataWriter {
	/**
	 * method  <code>writeCSVTextKeyWords</code>
	 * 提取链接关键字信息之后写入csv文件当中
	 * 这个方法可以提取作为持久层组件
	 * @param url 
	 */
	public static void writeCSVTextKeyWords(String dataDir, String url, Map<String, Set<String>> textKeyWords) {
//		URL u = null;
//		try {
//			u = new URL(url);
//		} catch (MalformedURLException e1) {
//			e1.printStackTrace();
//			return;
//		}
		File dataFile = new File(dataDir, url);
		FileWriter writer = null;
		try {
			writer = new FileWriter(dataFile, true);
			Set<Entry<String, Set<String>>> es = textKeyWords.entrySet();
			Iterator<Entry<String, Set<String>>> it = es.iterator();
			while(it.hasNext()) {
				Entry<String, Set<String>> textLink = it.next();
				Set<String> values = textLink.getValue();
				if (values != null && values.size() > 0) {
					String key = textLink.getKey() + ":";
					for (String str : values)
						key += str + ",";
					key = key.substring(0, key.length() - 1);
					key += System.lineSeparator();
					System.out.println(key);
					writer.write(key);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				writer = null;
				e.printStackTrace();
			}
		}	
	}
}
