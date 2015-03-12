/****
 * 	 Ansj Chinese tokeniser for GATE
 * 
 *   @author clebeg
 *   @reference Chinese tokeniser of whuwy 
 *   
 *   @help http://gatechinese.com
 * 
 * */
package gate.creole.tokeniser;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;

import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ExecutionInterruptedException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.RunTime;
import gate.event.ProgressListener;
import gate.event.StatusListener;
import gate.util.Err;
import gate.util.InvalidOffsetException;
@CreoleResource(name = "Ansj Chinese Tokeniser", comment = "A customisable Chinese tokeniser.", helpURL = "http://gatechinese.com", icon = "tokeniser")
public class AnsjGateTokeniser extends AbstractLanguageAnalyser {
	
	private static final long serialVersionUID = 851776022034622887L;

	public static final String DEF_TOK_DOCUMENT_PARAMETER_NAME = "document";

	public static final String DEF_TOK_ANNOT_SET_PARAMETER_NAME =  "annotationSetName";

	public static final String DEF_TOK_TOKRULES_URL_PARAMETER_NAME =  "tokeniserRulesURL";

	public static final String DEF_TOK_ENCODING_PARAMETER_NAME =  "encoding";

	public AnsjGateTokeniser() {
		
	}

	/** Initialize this resource, and return it. */
	public Resource init() throws ResourceInstantiationException {
		try{
			super.init();
			fireProgressChanged(100);
			fireProcessFinished();
	
		}catch(ResourceInstantiationException rie){
			throw rie;
		}catch(Exception e){
			throw new ResourceInstantiationException(e);
		}
	    return this;
	}

	/**
	* Prepares this Processing resource for a new run.
	*/
	public void reset(){
		document = null;
	}

	public void execute() throws ExecutionException{
		interrupted = false;
	    fireProgressChanged(0);
		fireStatusChanged("Tokenising " + document.getName() +  "...");
		
		String content = document.getContent().toString();
		List<Term> tokenswithpos = NlpAnalysis.parse(content);//调用Ansj分词
		
		SetFeatures(tokenswithpos);
		fireProgressChanged(5);
		ProgressListener pListener = new IntervalProgressListener(5, 50);
		StatusListener sListener = new StatusListener() {
			public void statusChanged(String text) {
				fireStatusChanged(text);
			}
		};
		if(isInterrupted()) throw new ExecutionInterruptedException(
		 "The execution of the \"" + getName() +
		 "\'' tokeniser has been abruptly interrupted!");

	}//execute

	/**
	* Notifies all the PRs in this controller that they should stop their
	* execution as soon as possible.
	*/
	public synchronized void interrupt(){
		interrupted = true;
	}


	/** Parses token and pos, add features to TOKEN annotation.
	 *
	 * @param tokenstream the string with token and pos
	 */
	private Boolean SetFeatures(List<Term> tokenstream) {
		// get document's annotationset
		AnnotationSet annotationSet;
		if(annotationSetName == null || annotationSetName.equals("")) 
			annotationSet = document.getAnnotations();
		else 
			annotationSet = document.getAnnotations(annotationSetName);
	
		FeatureMap newTokenFm;
	
		for(Term termTemp1:tokenstream){
			//we have a match!
			newTokenFm = Factory.newFeatureMap();
			int charIdx = termTemp1.getOffe() + termTemp1.getName().length();
			if(termTemp1.getNatureStr()!=null) {
			    newTokenFm.put(TOKEN_KIND_FEATURE_NAME,"word");
				newTokenFm.put(TOKEN_STRING_FEATURE_NAME, termTemp1.getName());
				newTokenFm.put(TOKEN_LENGTH_FEATURE_NAME,Integer.toString(termTemp1.getName().length()));
			    newTokenFm.put(TOKEN_CATEGORY_FEATURE_NAME, termTemp1.getNatureStr());
				try {
					annotationSet.add(new Long(termTemp1.getOffe()), new Long(charIdx),
					termTemp1.getNatureStr(), newTokenFm);
				} catch (InvalidOffsetException ioe) {
					//This REALLY shouldn’t happen!
					ioe.printStackTrace(Err.getPrintWriter());
				}
			} else {
			    newTokenFm.put(TOKEN_KIND_FEATURE_NAME,"word");
				newTokenFm.put(TOKEN_STRING_FEATURE_NAME, termTemp1.getName());
				newTokenFm.put(TOKEN_LENGTH_FEATURE_NAME,Integer.toString(termTemp1.getName().length()));
			    newTokenFm.put(TOKEN_CATEGORY_FEATURE_NAME, "unKnownToken");
				try {
					annotationSet.add(new Long(termTemp1.getOffe()),
					new Long(charIdx),
					"unKnownToken", newTokenFm);
				} catch (InvalidOffsetException ioe) {
					//This REALLY shouldn’t happen!
					ioe.printStackTrace(Err.getPrintWriter());
				}
			}
		}
		return true;
	}

	@RunTime
	@CreoleParameter(defaultValue="", comment="The annotation set to be used for the generated annotations")
	public void setAnnotationSetName(String annotationSetName) {
		this.annotationSetName = annotationSetName;
	}
	public String getAnnotationSetName() {
		return annotationSetName;
	}
	private String annotationSetName;

}
