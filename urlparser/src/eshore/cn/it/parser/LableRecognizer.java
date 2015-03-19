package eshore.cn.it.parser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import eshore.cn.it.data.DataLoader;
import eshore.cn.it.data.KeyWord;
import eshore.cn.it.data.ProbCalculater;

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
		String[] words1 = new String[]{"weather", "sina", "com", "cn"};
		String[] words2 = new String[]{"weather", "new", "sina", "com", "cn"};
		System.out.println(ParseUtil.isContainOther(words2, words1));
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
		//System.out.println(keyWord);
		return keyWord;
	}

	/**
	 * 下一步改进空间：
	 * 距离计算必须为每一个词设定权重：比如
	 * weather.news.sina.cn
	 * 那么weather的权重应该是最高的
	 * news的权重次子
	 * sina、cn的权重应该是更低
	 * 解决方案：计算每一个单词的出现的概率，如果碰到一次都没有出现的值，概率就是很小的赋值一个很小的数；
	 * 实际环境中，系统应该是多线程的，一个线程负责建立样本数据库；
	 * 一个线程负责监控样本数据库
	 * 一个线程负责更新单词出现的概率值，概率计算是需要才计算的，但是计算的时候一定要注意缓存
	 * 一个线程负责分析新数据属于哪个类别
	 * */
	private KeyWord findMaxScore(Map<String, Integer> scores) {
		List<Map.Entry<String,Integer>> list=
				new ArrayList<Map.Entry<String,Integer>>(scores.entrySet());

		LableRecognizer.ValueComparator vc = new ValueComparator();  
		Collections.sort(list, vc);
		String maxKey = null;
		if(list.size() > 0) {
			maxKey = list.get(0).getKey();
		} else {
			Random random = new Random(1000);//指定种子数字
			maxKey = random.nextInt(DataLoader.loadKeyWords().size()) + "";
		}
		return DataLoader.loadKeyWords().get(maxKey);
	}

	private Map<String, Integer> calculateScores(KeyWord keyWord) {
		Map<String, Integer> scores = new LinkedHashMap<String, Integer>();
		
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
		int weight = 5;
		if (keyWord.getUrl().equals(value.getUrl()))
			distance = 100;
		else {
			String[] words1 = null;
			String[] words2 = null;
			if (value.getHost().equals(keyWord.getHost())) {
				distance += 4 * weight;
			} else {
				words1 = keyWord.getHost().split("\\.");
				words2 = value.getHost().split("\\.");
			
				double probd = 0;
				if(ParseUtil.isContainOther(words1, words2))
					probd = doubleDistance(words1, value.getHost());
				else if(ParseUtil.isContainOther(words2, words1))
					probd = doubleDistance(words2, keyWord.getHost());
				distance = (int) (Math.round(probd) * weight);
			}
			
			//然后通过关键字相似程度计算距离
			if (keyWord.getKeywords() != null && value.getKeywords() != null &&
					keyWord.getKeywords().equalsIgnoreCase("") == false &&
					value.getKeywords().equalsIgnoreCase("") == false) {
				words1 = keyWord.getKeywords().split("\\|");
				words2 = value.getKeywords().split("\\|");
				if (words2.length > 0 && words1.length > 0) {
					if (words2[0].equals(words1[0]))
						distance += 2*weight;
					else if (words2[0].contains(words1[0]))
						distance += weight;
					
					if (words2.length > 1 && words2[1].equals(words1[0]))
						distance += 2*weight;
					else if (words2.length > 1 && words2[1].contains(words1[0]))
						distance += weight;
					
					for (String word : words1) {
						if (value.getKeywords().contains(word))
							distance++;
					}
				}
			}
		}
		if (distance > 0)
			System.out.println(keyWord + "-------" + value + " = " + distance);
		return distance;
	}

	private double doubleDistance(String[] words1, String domain) {
		double tmp = 1;
		for(String str : words1)
			tmp *= ProbCalculater.calConPro(str, domain);
		return tmp*10;
	}

	

	private KeyWord parseUrl2KeyWord(String url) throws MalformedURLException {
		URL u = new URL(url);
		KeyWord keyWord = new KeyWord();
		keyWord.setHost(u.getHost());
		keyWord.setUrl(url);
		
		String keyWords = "";
		String[] strs = u.getPath().split("[./]");
		for (int i = 0; i < strs.length; i++) {
			String tmp = ParseUtil.filterAdd(strs[i].trim());
			if (tmp != null)
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
