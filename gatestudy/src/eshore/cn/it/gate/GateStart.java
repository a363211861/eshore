package eshore.cn.it.gate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import eshore.cn.it.gate.StandAloneAnnie.SortedAnnotationList;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.GateConstants;
import gate.creole.ANNIEConstants;
import gate.creole.ConditionalSerialAnalyserController;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import gate.util.Out;
import gate.util.persistence.PersistenceManager;

/**
 * class<code>GateStart</code>此类演示了如何利用GATE实现批量信息抽取
 *
 * @author   clebeg
 * @version	 0.0.1
 * @see      java.lang.Class
 * @since    JDK1.8
 * */
public class GateStart {
	//指定注册的plugin名字
	private String pluginName = "Lang_Chinese";
	//指定使用的资源类名
	private String resourceClassName = "gate.corpora.CorpusImpl";
	//指定数据保存的根目录，未实现自动创建，必须手动创建
	private String rootDataHome = "data";
	//指定数据编码方式
	private String fileEncoding = "UTF-8";
	private String corpusDirectory = "corpus";
	
	//定义基于语料库的GATE控制者
	private CorpusController controller;
	private Corpus corpus;
	
	private String[] annotationStrings = new String[]{"Date", "Person", "Location"};
	
	public static void main(String[] args) throws Exception {
		GateStart gs = new GateStart();
		gs.gateExecute();
	}
	
	/**
	 * method<code>gateExecute</code>
	 * 此方法可以执行一个完整的GATE信息抽取流程，最终把词性标注的结果保存
	 * */
	public void gateExecute() throws Exception {
		GateEnvironment.setGateHome();		//第一步：设置GATE安装目录
		gateInit();							//第二步：初始化GATE，加载必要的插件
		initController();					//第三步：初始化Controller
		
		corpus = addFileNameCorpus();		//第四步：加入语料信息
		
		controller.setCorpus(corpus);		//第五步：提交语料信息
		controller.execute();				//第六步：执行GATE流程
		
		persistDocuments();					//第七步：持久化执行结果
		
		infoExtract(annotationStrings);		//第八步：信息抽取
	}
	

	/**
	 * method<code>gateInit</code>
	 * 初始化gate，并且加载必要的组件
	 * */
	private void gateInit() throws GateException, MalformedURLException {
		Out.prln("...GATE initialise now ");
		Gate.init();
		
		File gateHome = Gate.getGateHome();
		File pluginsHome = new File(gateHome, "plugins");
		
		//这里需要设定pluginName参数，指定plugin的名字
		Gate.getCreoleRegister().registerDirectories(new File(pluginsHome, this.pluginName).toURI().toURL());
		
		Out.prln("...GATE initialised ");
	}
	
	/**
	 * method<code>initController</code>
	 * 初始化控制器,可以初始化成不同的控制器
	 * */
	private void initController() throws GateException, IOException {
		Out.prln("Initialising controller...");
		//chnController = (ConditionalSerialAnalyserController) PersistenceManager.loadObjectFromFile(new File(new File(Gate.getPluginsHome(),"Lang_Chinese"), "resources/chinese.gapp"));
		controller = (ConditionalSerialAnalyserController)
				PersistenceManager.loadObjectFromFile(new File(new File(
				Gate.getPluginsHome(), ANNIEConstants.PLUGIN_DIR),
				ANNIEConstants.DEFAULT_FILE));
		Out.prln("...controller loaded");
	} 
	
	
	/**
	 * method<code>addFileNameCorpus</code>
	 * 加入文档到语料库，改变这个方法可以批量加入语料
	 * */
	private Corpus addFileNameCorpus() throws ResourceInstantiationException, MalformedURLException {
		File cd = new File(corpusDirectory);
		File[] childs = null;
		if (cd.isDirectory()) 
			childs = cd.listFiles();
		
		//需要指定资源加载语料库的资源类
		Corpus corpus = (Corpus) Factory.createResource(this.resourceClassName);
		
		for (File file : childs) {
			if (file.isFile()) {
				StringBuffer corpusDir = new StringBuffer("");
				corpusDir.append("file:/").append(file.getAbsolutePath());
				URL u = new URL(corpusDir.toString());
				FeatureMap params = Factory.newFeatureMap();
				params.put("sourceUrl", u);
				params.put("preserveOriginalContent", new Boolean(true));
				params.put("collectRepositioningInfo", new Boolean(true));
				params.put("encoding", fileEncoding);//以UTF-8编码读取信息 否则会出现乱码
				Out.prln("Creating doc for " + u);
				Document docs = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);
				corpus.add(docs);
			}
		}
		return corpus;
	}


	
	/**
	 * method<code>persistDocuments</code>
	 * 持久化经过GATE处理之后的文件为xml,并且保存到指定目录中
	 * */
	private void persistDocuments() throws IOException {
		Iterator<Document> it = corpus.iterator();
		while(it.hasNext()) {
			Document doc = it.next();
			FileWriter writer = new FileWriter(new File(this.rootDataHome, doc.getName() + ".xml"));
			writer.write(doc.toXml());
			writer.close();
		}
	}

	
	/**
	 * method<code>infoExtract</code>
	 * 信息抽取，方法可以改进为传入想要抽取的Annotation Type数组
	 * */
	private void infoExtract(String[] annotationStrings) {
		Iterator<Document> iter = corpus.iterator();
		int count = 0;
		while(iter.hasNext()) {
		     Document doc = (Document) iter.next();
		      
		     AnnotationSet defaultAnnotSet = doc.getAnnotations();
		      
		      //加入需要提取的ANNOTATION
		     Set<String> annotTypesRequired = new HashSet<String>();
		     for (String anno : annotationStrings)
		    	 annotTypesRequired.add(anno);
		      
		     Set<Annotation> peopleAndPlaces =
		       new HashSet<Annotation>(defaultAnnotSet.get(annotTypesRequired));
	
		     FeatureMap features = doc.getFeatures();
		     String originalContent = (String)
		       features.get(GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME);
		     ++count;
		     Out.prln("Begin Extract the "+ count + " file, it's name + " + doc.getName());
		      
		     if(originalContent != null) {
		       Out.prln("OrigContent. Extract file...");
	
		       Iterator<Annotation> it = peopleAndPlaces.iterator();
		       Annotation currAnnot;
		       SortedAnnotationList sortedAnnotations = new SortedAnnotationList();
	
		       while(it.hasNext()) {
		         currAnnot = (Annotation) it.next();
		         sortedAnnotations.addSortedExclusive(currAnnot);
		       }
	
		       StringBuffer editableContent = new StringBuffer(originalContent);
		       long insertPositionEnd;
		       long insertPositionStart;
		        
		       Out.prln("Unsorted annotations count: "+peopleAndPlaces.size());
		       Out.prln("Sorted annotations count: "+sortedAnnotations.size());
		        
		       for(int i=sortedAnnotations.size()-1; i>=0; --i) {
		         currAnnot = (Annotation) sortedAnnotations.get(i);
		         insertPositionStart =
		           currAnnot.getStartNode().getOffset().longValue();
		          
		         insertPositionEnd = currAnnot.getEndNode().getOffset().longValue();
		          
		         if(insertPositionEnd != -1 && insertPositionStart != -1) {
		        	 Out.prln(currAnnot.getType() + " : " + editableContent.substring((int)insertPositionStart, (int)insertPositionEnd));
		         } 
		       } 
		     } 
		   }
		
	}
}
