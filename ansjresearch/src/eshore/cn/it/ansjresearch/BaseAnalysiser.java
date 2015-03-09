package eshore.cn.it.ansjresearch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.io.IOUtils;

public class BaseAnalysiser {
	private static String root = "corpus";
	public static void main(String[] args) throws FileNotFoundException, IOException {
		File directory = new File(root);
		File[] files = directory.listFiles(); 
		for (File file : files) {
			System.out.println(" 开始从文件 " + file.getName() + " 中提取信息：");
			List<String> lines = IOUtils.readLines(new FileReader(file));
			List<Term> parse = ToAnalysis.parse(lines.toString());
			String names = "";
			String addresses = "";
			String times = "";
			String moods = "";
			for(Term term : parse) {
				if (term.getNatureStr().startsWith("nr")) {
					names += term.getName() + ",";
				}
				if (term.getNatureStr().startsWith("ns")) {
					addresses += term.getName() + ",";
				}
				if (term.getNatureStr().startsWith("t")) {
					times += term.getName() + ",";
				}
				if (term.getNatureStr().startsWith("a")) {
					moods += term.getName() + ",";
				}
				
			}
			if (names.equalsIgnoreCase("") == false)
				System.out.println("提取的人名 : " + names.substring(0, names.length() - 1));
			if (addresses.equalsIgnoreCase("") == false)
				System.out.println("提取的地名 : " + addresses.substring(0, addresses.length() - 1));
			if (times.equalsIgnoreCase("") == false)
				System.out.println("提取的时间 : " + times.substring(0, times.length() - 1));
			if (moods.equalsIgnoreCase("") == false)
				System.out.println("提取的感情 : " + moods.substring(0, moods.length() - 1));
			System.out.println("----------------------------");
		}
		
	}
}
