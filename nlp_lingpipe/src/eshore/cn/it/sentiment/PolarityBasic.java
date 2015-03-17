package eshore.cn.it.sentiment;

import java.io.File;
import java.io.IOException;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.util.Files;

public class PolarityBasic {
	File mPolarityDir;
	String[] mCategories;
	DynamicLMClassifier<NGramProcessLM> mClassifier;
	
	public PolarityBasic(String[] args) {
		mPolarityDir = new File("polarity_corpus","txt_sentoken");
	    mCategories = mPolarityDir.list();
	    int nGram = 8;
	    mClassifier
	        = DynamicLMClassifier
	          .createNGramProcess(mCategories,nGram);
	}


	public static void main(String[] args) {
	    try {
	        new PolarityBasic(args).run();
	    } catch (Throwable t) {
	        System.out.println("Thrown: " + t);
	        t.printStackTrace(System.out);
	    }
	}
	
	private void run() throws ClassNotFoundException,
	    IOException {
		train();
		evaluate();
	}
	private void train() throws IOException {
	    for (int i = 0; i < mCategories.length; ++i) {
	        String category = mCategories[i];
	        Classification classification
	            = new Classification(category);
	        File dir = new File(mPolarityDir, mCategories[i]);
	        File[] trainFiles = dir.listFiles();
	        for (int j = 0; j < trainFiles.length; ++j) {
	            File trainFile = trainFiles[j];
	            if (isTrainingFile(trainFile)) {
	                String review
	                    = Files.readFromFile(trainFile,"ISO-8859-1");
	                Classified<CharSequence> classified
	                    = new Classified<CharSequence>(review,classification);
	                mClassifier.handle(classified);
	             }
	        }
	    }
	}
	boolean isTrainingFile(File file) {
	    return file.getName().charAt(2) != '9';  // test on fold 9
	}
	
	void evaluate() throws IOException {
	    int numTests = 0;
	    int numCorrect = 0;
	    for (int i = 0; i < mCategories.length; ++i) {
	        String category = mCategories[i];
	        File file = new File(mPolarityDir,mCategories[i]);
	        File[] testFiles = file.listFiles();
	        for (int j = 0; j < testFiles.length; ++j) {
	            File testFile = testFiles[j];
	            if (!isTrainingFile(testFile)) {
	                String review
	                    = Files.readFromFile(testFile,"ISO-8859-1");
	                ++numTests;
	                Classification classification
	                    = mClassifier.classify(review);
	                String resultCategory
	                    = classification.bestCategory();
	                if (resultCategory.equals(category))
	                    ++numCorrect;
	            }
	        }
	    }
	    System.out.println("  # Test Cases="
                + numTests);
	    System.out.println("  # Correct="
                + numCorrect);
	    System.out.println("  % Correct="
                + ((double)numCorrect)
                   /(double)numTests);
	}
}
