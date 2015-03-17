package eshore.cn.it.data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

public class DataWriter {
	/**
	 * method  <code>writeCSVTextKeyWords</code>
	 * 提取链接关键字信息之后写入csv文件当中
	 * 这个方法可以提取作为持久层组件
	 * @param url 
	 */
	public static void writeCSVTextKeyWords(String dataDir, String url, Map<String, Set<KeyWord>> textKeyWords) {
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
			Set<Entry<String, Set<KeyWord>>> es = textKeyWords.entrySet();
			Iterator<Entry<String, Set<KeyWord>>> it = es.iterator();
			while(it.hasNext()) {
				Entry<String, Set<KeyWord>> textLink = it.next();
				Set<KeyWord> values = textLink.getValue();
				if (values != null && values.size() > 0) {
					String key = System.lineSeparator();
					for (KeyWord str : values) {
						key = str.toString();
						key += System.lineSeparator();
						//System.out.println(key);
						writer.write(key);
					}
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

	public static String getMaxKeyWordsId(String dataDir, String fileName) {
		File dataFile = new File(dataDir, fileName);
		FileReader reader = null;
		try {
			reader = new FileReader(dataFile);
			List<String> lines = IOUtils.readLines(reader);
			return "" + (lines.size() - 1);
		} catch (IOException e) {
			System.exit(1);
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return null;
	}

	public static void writeMaxDoneId(int MAX_DONE_ID) {
		File file = new File("resource", "MAXDONEID");
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write("" + MAX_DONE_ID);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}
}
