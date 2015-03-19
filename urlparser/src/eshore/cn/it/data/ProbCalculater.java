package eshore.cn.it.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import eshore.cn.it.parser.ParseUtil;

/***
 * 计算单词出现的概率
 * 一旦计算出来就报存到map里面，以后就不用计算了
 * 计算开销非常大
 * 如何优化是一个最大的问题
 * @author clebeg
 * @version 0.0.1
 * @time 2015-03-19
 * */
public class ProbCalculater {
	public static final Map<String, Double> PROBABILITIES = 
			new HashMap<String, Double>();
	public static final Map<String, Double> CONPROB = 
			new HashMap<String, Double>();
	public static final double MIN_PROB = 0.0001;

	public static double calculateProbability(String word) {
		double prob = MIN_PROB;
		if(PROBABILITIES.containsKey(word))
			return PROBABILITIES.get(word);
		else {
			int equals = 0, sum = 0;
			Set<Entry<String,KeyWord>> es = DataLoader.loadKeyWords().entrySet();
			Iterator<Entry<String, KeyWord>> it = es.iterator();
			while(it.hasNext()) {
				Entry<String, KeyWord> textLink = it.next();
				KeyWord value = textLink.getValue();
				String[] tmps = (value.getHost() + "/" + value.getKeywords()).split("[./]");
				for (String str : tmps) {
					if(str.equals(word))
						equals++;
					sum++;
				}
			}
			double result = (double)equals/(double)sum;
			if(prob < result)
				prob = result;
		}
		return prob;
	}
	
	/**
	 * 计算条件概率，就是出现包含某个域名情况下，出现某个单词的条件概率
	 * */
	public static double calConPro(String word, String domain) {
		double prob = MIN_PROB;
		if(CONPROB.containsKey(word))
			return PROBABILITIES.get(word);
		else {
			int equals = 0, sum = 0;
			Set<Entry<String,KeyWord>> es = DataLoader.loadKeyWords().entrySet();
			Iterator<Entry<String, KeyWord>> it = es.iterator();
			while(it.hasNext()) {
				Entry<String, KeyWord> textLink = it.next();
				KeyWord value = textLink.getValue();
				String[] tmp1 = value.getHost().split("\\.");
				String[] tmp2 = domain.split("\\.");
				if(ParseUtil.isContainOther(tmp1, tmp2)) {
					for (String str : tmp1) {
						if(str.equals(word))
							equals++;
						sum++;
					}
				}
			}
			double result = (double)equals/(double)sum;
			if(prob < result)
				prob = result;
		}
		return prob;
	}
	public static void main(String[] args) {
		System.out.println(calculateProbability("news"));
		System.out.println(calculateProbability("weather"));
		System.out.println(calConPro("weather", "weather.sina.com.cn"));
		System.out.println(calConPro("weather", "news.sina.com.cn"));
	}
}
