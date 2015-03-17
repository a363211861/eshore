package eshore.cn.it.sentiment;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.util.Files;

/**
 * ChinesePolarityBasic 此类是利用lingpipe作中文情感预测的示例类
 * lingpipe适合做增量分析
 * @clebeg 2015-03-13
 * @version 0.0.1
 * */
public class ChinesePolarityBasic {
	private String[] mCategories = new String[]{"+1", "-1"};
	//这就是分类模型
	private DynamicLMClassifier<NGramProcessLM> mClassifier;
	
	private int numTests = 0;
    private int numCorrect = 0;
	private static final String TRAINFILES_INFO = 
			"polarity_corpus/hotel_reviews/train2.rlabelclass";
	private static final String TRAINFILES_DIR = 
			"polarity_corpus/hotel_reviews/train2";
	private static final String TESTFILES_DIR = 
			"polarity_corpus/hotel_reviews/test2";
	private static final String TESTFILES_INFO = 
			"polarity_corpus/hotel_reviews/test2.rlabelclass"; 
	private static final String ENCODING = "GBK";
	public static void main(String[] args) {
		try {
			new ChinesePolarityBasic().run();
	    } catch (Throwable t) {
	        System.out.println("Thrown: " + t);
	        t.printStackTrace(System.out);
	    }

	}


	public ChinesePolarityBasic() {
		super();
		int nGram = 8;
		mClassifier
	        = DynamicLMClassifier
	          .createNGramProcess(mCategories,nGram);
	}

	private void run() throws ClassNotFoundException,
    	IOException {
		train();
		evaluate();
	}

	private void train() throws IOException {
		FileReader input = new FileReader(new File(TRAINFILES_INFO));
		List<String> trainInfos = IOUtils.readLines(input);
		for (String str : trainInfos){
			String[] train = str.split(" ");
			train(train[1], new File(TRAINFILES_DIR, train[0]), ENCODING);
		}
	}
	
	private void evaluate() throws IOException {
		FileReader input = new FileReader(new File(TESTFILES_INFO));
		List<String> trainInfos = IOUtils.readLines(input);
		for (String str : trainInfos){
			String[] train = str.split(" ");
			evaluate(train[1], new File(TESTFILES_DIR, train[0]), ENCODING);
		}
		System.out.println("  # Test Cases="
                + numTests);
	    System.out.println("  # Correct="
                + numCorrect);
	    System.out.println("  % Correct="
                + ((double)numCorrect)
                   /(double)numTests);
	}

	/**
	 * 给定分类标识，给定训练文本，给定文本的编码，即可作分类训练
	 * 分类完成之后就会加入到分类模型中
	 * @throws IOException 
	 * */
	private void train(String category, File trainFile, String fileEncoding) 
			throws IOException {
		Classification classification = new Classification(category);
		String review = Files.readFromFile(trainFile, fileEncoding);
		Classified<CharSequence> classified
			= new Classified<CharSequence>(review,classification);
		mClassifier.handle(classified);
	}
	
	/**
	 * 给定分类标识，给定测试文本，给定文本的编码，即可作测试模型
	 * @throws IOException 
	 * */
	private void evaluate(String category, File testFile, String fileEncoding) 
			throws IOException {
		
	    String review
        	= Files.readFromFile(testFile, fileEncoding);
	    ++numTests;
	    Classification classification
        	= mClassifier.classify(review);
	    //得到训练结果
	    String resultCategory
        	= classification.bestCategory();
	    if (resultCategory.equals(category))
	    	++numCorrect;
	    
	}

}
