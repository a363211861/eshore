package eshore.cn.it.gate;

import java.util.*;
import java.io.*;
import java.net.*;

import gate.*;
import gate.creole.*;
import gate.util.*;
import gate.util.persistence.PersistenceManager;
import gate.corpora.RepositioningInfo;

/**
 * 这个类演示如何使用 ANNIE作为其它应用的组件。输入一个网址，本程序会给出运行的结果，为标记的网页。
 * <P><B>注意:</B><BR>
 * 为了简单，异常都没有处理。
 */
public class StandAloneAnnie  {

  /** The Corpus Pipeline application to contain ANNIE */
  private CorpusController annieController;

  /**
   * 初始化 ANNIE 系统. This creates a "corpus pipeline"
   * application that can be used to run sets of documents through
   * the extraction system.
   */
  public void initAnnie() throws GateException, IOException {
    Out.prln("Initialising ANNIE...");

    // 必须加载ANNIE插件
    File pluginsHome = Gate.getPluginsHome();
    File anniePlugin = new File(pluginsHome, "ANNIE");
    File annieGapp = new File(anniePlugin, "ANNIE_with_defaults.gapp");
    annieController =
      (CorpusController) PersistenceManager.loadObjectFromFile(annieGapp);

    Out.prln("...ANNIE loaded");
  }

  /** 为 ANNIE's controller 加入语料库 */
  public void setCorpus(Corpus corpus) {
    annieController.setCorpus(corpus);
  } 

  /** 执行 ANNIE */
  public void execute() throws GateException {
    Out.prln("Running ANNIE...");
    annieController.execute();
    Out.prln("...ANNIE complete");
  }

  /**
   * 在命令行中输入网址，这是主方法。
   * <P><B>注意:</B><BR>
   * 这个类将所有文档处理都在内存中进行，如果不想在一直占用内存，请用DataStore持久化。
   */
  public static void main(String args[]) throws GateException, IOException {
    // 初始化GATE
    Out.prln("Initialising GATE...");
    GateEnvironment.setGateHome();	
    Gate.init();
    Out.prln("...GATE initialised");

    // 初始化ANNIE
    StandAloneAnnie annie = new StandAloneAnnie();
    annie.initAnnie();

    // 将命令行输入的文件算不加入语料库中
    Corpus corpus = Factory.newCorpus("StandAloneAnnie corpus");
    for(int i = 0; i < args.length; i++) {
      URL u = new URL(args[i]);
      FeatureMap params = Factory.newFeatureMap();
      params.put("sourceUrl", u);
      params.put("preserveOriginalContent", new Boolean(true));
      params.put("collectRepositioningInfo", new Boolean(true));
      Out.prln("Creating doc for " + u);
      Document doc = (Document)
        Factory.createResource("gate.corpora.DocumentImpl", params);
      corpus.add(doc);
    } 

    // 执行ANNIE
    annie.setCorpus(corpus);
    annie.execute();

    // for each document, get an XML document with the
    // person and location names added
    Iterator iter = corpus.iterator();
    int count = 0;
    String startTagPart_1 = "<span GateID=\"";
    String startTagPart_2 = "\" title=\"";
    String startTagPart_3 = "\" style=\"background:Red;\">";
    String endTag = "</span>";

    while(iter.hasNext()) {
      Document doc = (Document) iter.next();
      AnnotationSet defaultAnnotSet = doc.getAnnotations();
      Set annotTypesRequired = new HashSet();
      annotTypesRequired.add("Person");
      annotTypesRequired.add("Location");
      
      Set<Annotation> peopleAndPlaces =
        new HashSet<Annotation>(defaultAnnotSet.get(annotTypesRequired));

      FeatureMap features = doc.getFeatures();
      String originalContent = (String)
        features.get(GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME);
      RepositioningInfo info = (RepositioningInfo)
        features.get(GateConstants.DOCUMENT_REPOSITIONING_INFO_FEATURE_NAME);

      ++count;
      File file = new File("StANNIE_" + count + ".HTML");
      Out.prln("File name: '"+file.getAbsolutePath()+"'");
      if(originalContent != null && info != null) {
        Out.prln("OrigContent and reposInfo existing. Generate file...");

        Iterator it = peopleAndPlaces.iterator();
        Annotation currAnnot;
        SortedAnnotationList sortedAnnotations = new SortedAnnotationList();

        while(it.hasNext()) {
          currAnnot = (Annotation) it.next();
          sortedAnnotations.addSortedExclusive(currAnnot);
        } // while

        StringBuffer editableContent = new StringBuffer(originalContent);
        long insertPositionEnd;
        long insertPositionStart;
        // insert anotation tags backward
        Out.prln("Unsorted annotations count: "+peopleAndPlaces.size());
        Out.prln("Sorted annotations count: "+sortedAnnotations.size());
        for(int i=sortedAnnotations.size()-1; i>=0; --i) {
          currAnnot = (Annotation) sortedAnnotations.get(i);
          insertPositionStart =
            currAnnot.getStartNode().getOffset().longValue();
          insertPositionStart = info.getOriginalPos(insertPositionStart);
          insertPositionEnd = currAnnot.getEndNode().getOffset().longValue();
          insertPositionEnd = info.getOriginalPos(insertPositionEnd, true);
          if(insertPositionEnd != -1 && insertPositionStart != -1) {
        	Out.prln(editableContent.substring((int)insertPositionStart, (int)insertPositionEnd));
            editableContent.insert((int)insertPositionEnd, endTag);
            editableContent.insert((int)insertPositionStart, startTagPart_3);
            editableContent.insert((int)insertPositionStart,
                                                          currAnnot.getType());
            editableContent.insert((int)insertPositionStart, startTagPart_2);
            editableContent.insert((int)insertPositionStart,
                                                  currAnnot.getId().toString());
            editableContent.insert((int)insertPositionStart, startTagPart_1);
          } // if
        } // for

        FileWriter writer = new FileWriter(file);
        writer.write(editableContent.toString());
        writer.close();
      } // if - should generate
      else if (originalContent != null) {
        Out.prln("OrigContent existing. Generate file...");

        Iterator it = peopleAndPlaces.iterator();
        Annotation currAnnot;
        SortedAnnotationList sortedAnnotations = new SortedAnnotationList();

        while(it.hasNext()) {
          currAnnot = (Annotation) it.next();
          sortedAnnotations.addSortedExclusive(currAnnot);
        } // while

        StringBuffer editableContent = new StringBuffer(originalContent);
        long insertPositionEnd;
        long insertPositionStart;
        // insert anotation tags backward
        Out.prln("Unsorted annotations count: "+peopleAndPlaces.size());
        Out.prln("Sorted annotations count: "+sortedAnnotations.size());
        for(int i=sortedAnnotations.size()-1; i>=0; --i) {
          currAnnot = (Annotation) sortedAnnotations.get(i);
          insertPositionStart =
            currAnnot.getStartNode().getOffset().longValue();
          insertPositionEnd = currAnnot.getEndNode().getOffset().longValue();
          if(insertPositionEnd != -1 && insertPositionStart != -1) {
        	Out.prln(editableContent.substring((int)insertPositionStart, (int)insertPositionEnd));
            editableContent.insert((int)insertPositionEnd, endTag);
            editableContent.insert((int)insertPositionStart, startTagPart_3);
            editableContent.insert((int)insertPositionStart,
                                                          currAnnot.getType());
            editableContent.insert((int)insertPositionStart, startTagPart_2);
            editableContent.insert((int)insertPositionStart,
                                                  currAnnot.getId().toString());
            editableContent.insert((int)insertPositionStart, startTagPart_1);
          } // if
        } // for

        FileWriter writer = new FileWriter(file);
        writer.write(editableContent.toString());
        writer.close();
      }
      else {
        Out.prln("Content : "+originalContent);
        Out.prln("Repositioning: "+info);
      }

      String xmlDocument = doc.toXml(peopleAndPlaces, false);
      String fileName = new String("StANNIE_toXML_" + count + ".HTML");
      FileWriter writer = new FileWriter(fileName);
      writer.write(xmlDocument);
      writer.close();

    } // for each doc
  } // main

  /**
   *
   */
  public static class SortedAnnotationList extends Vector {
    public SortedAnnotationList() {
      super();
    } // SortedAnnotationList

    public boolean addSortedExclusive(Annotation annot) {
      Annotation currAnot = null;

      // overlapping check
      for (int i=0; i<size(); ++i) {
        currAnot = (Annotation) get(i);
        if(annot.overlaps(currAnot)) {
          return false;
        } // if
      } // for

      long annotStart = annot.getStartNode().getOffset().longValue();
      long currStart;
      // insert
      for (int i=0; i < size(); ++i) {
        currAnot = (Annotation) get(i);
        currStart = currAnot.getStartNode().getOffset().longValue();
        if(annotStart < currStart) {
          insertElementAt(annot, i);
          /*
           Out.prln("Insert start: "+annotStart+" at position: "+i+" size="+size());
           Out.prln("Current start: "+currStart);
           */
          return true;
        } // if
      } // for

      int size = size();
      insertElementAt(annot, size);
//Out.prln("Insert start: "+annotStart+" at size position: "+size);
      return true;
    } // addSorted
  } // SortedAnnotationList
} // class StandAloneAnnie