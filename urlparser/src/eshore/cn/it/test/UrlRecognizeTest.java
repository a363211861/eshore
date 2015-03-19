package eshore.cn.it.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;

import eshore.cn.it.data.DataLoader;
import eshore.cn.it.data.KeyWord;
import eshore.cn.it.parser.LableRecognizer;

public class UrlRecognizeTest {
	public static final LableRecognizer RECOGNIZE = new LableRecognizer();
	public static final String TEST_FILE_NAME = "data/test/URL_Title_150318.txt";
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		
		int testNumber = 0;
		int correctNumber = 0;
		int correctFN = 0;
		File testFile = new File(TEST_FILE_NAME);
		FileInputStream in = null;
		try {
			in = new FileInputStream(testFile);
			List<String> tests = IOUtils.readLines(in, "GBK");
			testNumber = tests.size();
			for (String url : tests) {
				String[] tmp = url.split(",");
				KeyWord kw = RECOGNIZE.recognize(tmp[0]);
				String result = kw.getLabel();
				if (tmp[1].contains(result) || result.contains(tmp[1]))
					correctNumber++;
				else 
					System.out.println(url);
				KeyWord fkw = DataLoader.loadKeyWords().get(kw.getPid());
				if (fkw != null) {
					if (tmp[2].contains(fkw.getLabel()) || fkw.getLabel().contains(tmp[2]))
						correctFN++;
				}
			}
			long end = System.currentTimeMillis();
			System.out.println("共耗时：" + (end - start) + " ms");
			System.out.println("This test task number is: " + testNumber);
			System.out.println("The self label is correct answer number is: " + correctNumber);
			System.out.println("The father label is correct answer number is: " + correctFN);
			System.out.println("The ratio of self correct is: " + ((float)correctNumber/(float)testNumber)*100 +"%");
			System.out.println("The ratio of father correct is: " + ((float)correctFN/(float)testNumber)*100 +"%");
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

}
