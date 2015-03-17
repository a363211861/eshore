package eshore.cn.it.parser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import eshore.cn.it.data.DataLoader;
import eshore.cn.it.data.KeyWord;

/**
 * class  <code>LableRecognizer</code>
 * 此类通过提取URL的特征，与已经存在关键词信息匹配，尽可能寻找正确的分类
 * 具体算法：
 * 通过比较当前URL与关键词提取的距离，具体距离的算法可以进一步改进，得到距离
 * 需要分类的URL最近的分类，即为此链接的分类。
 * @author   clebeg
 * @version	 0.0.1
 * @see      java.lang.Class
 * @since    JDK1.8
 * */
public class LableRecognizer {
	private static final int MAXDISTINCE = 100;
	
	public static void main(String[] args) {
		String url = "";
		//url = "http://sports.sina.com.cn/g/laliga/2015-03-16/12217544685.shtml";
		//url = "http://hupy.iteye.com/blog/871085";
		url = "http://woodpecker.org.cn/abyteofpython_cn/chinese/index.html";
		LableRecognizer recognize = new LableRecognizer();
		recognize.recognize(url);
	}
	
	public KeyWord recognize(String url) {
		KeyWord keyWord = null;
		Map<String, Integer> scores = null;
		try {
			keyWord = parseUrl2KeyWord(url);
			scores = calculateScores(keyWord);
			keyWord = findMaxScore(scores);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		System.out.println(keyWord);
		return keyWord;
	}

	private KeyWord findMaxScore(Map<String, Integer> scores) {
		List<Map.Entry<String,Integer>> list=
				new ArrayList<Map.Entry<String,Integer>>(scores.entrySet());

		LableRecognizer.ValueComparator vc = new ValueComparator();  
		Collections.sort(list, vc);
		String maxKey = list.get(0).getKey();
		return DataLoader.loadKeyWords().get(maxKey);
	}

	private Map<String, Integer> calculateScores(KeyWord keyWord) {
		Map<String, Integer> scores = new HashMap<String, Integer>();
		
		Map<String, KeyWord> keyWords = DataLoader.loadKeyWords();
		Set<Entry<String, KeyWord>> entry  = keyWords.entrySet();
		Iterator<Entry<String, KeyWord>> it = entry.iterator();
		while(it.hasNext()) {
			Entry<String, KeyWord> kv = it.next();
			int distance = distance(keyWord, kv.getValue());
			if(distance == MAXDISTINCE) {
				scores.put(kv.getKey(), distance);
				return scores;
			}
			if(distance > 0)
				scores.put(kv.getKey(), distance);
		}
		return scores;
	}

	private int distance(KeyWord keyWord, KeyWord value) {
		int distance = 0;
		if (keyWord.getUrl().equals(value.getUrl()))
			distance = 100;
		else {
			String[] words = keyWord.getKeywords().split("\\|");
			for (String word : words) {
				if ((value.getHost() + "|" + value.getKeywords()).contains(word))
					distance++;
			}
		}
		return distance;
	}

	private KeyWord parseUrl2KeyWord(String url) throws MalformedURLException {
		URL u = new URL(url);
		KeyWord keyWord = new KeyWord();
		keyWord.setHost(u.getHost());
		keyWord.setUrl(url);
		
		String keyWords = "";
		String[] strs = (u.getHost() + "/" + u.getPath()).split("[./]");
		for (int i = 0; i < strs.length; i++) {
			String tmp = strs[i].trim();
			if (tmp.equalsIgnoreCase("") == false)
				keyWords += tmp + "|";
		}
		if (keyWords.equalsIgnoreCase("") == false)
			keyWord.setKeywords(keyWords.substring(0, keyWords.length() - 1));
		else 
			keyWord.setKeywords(null);
		
		return keyWord;
	}
	
	
	private static class ValueComparator implements Comparator<Map.Entry<String,Integer>> {  
        public int compare(Map.Entry<String,Integer> m,Map.Entry<String,Integer> n) {  
            return n.getValue() - m.getValue();  
        }  
    }  
}
