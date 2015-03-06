package eshore.cn.it.gate;

import gate.Document;
import gate.Corpus;
import gate.CorpusController;
import gate.AnnotationSet;
import gate.Gate;
import gate.Factory;
import gate.util.*;
import gate.util.persistence.PersistenceManager;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;

/**
 * 这个类说明如何使用GATE实现简单到信息提取.  它通过从 .gapp 文件中加载一个 GATE 应用
 * （注意：这个文件是通过GATE的图形用户界面 "Save application state" 按钮生成的）, 
 * 然后在多个文件上面执行这个应用程序。
 * 最终结果会写成 XML 文件，在这个例子里面，保存的文件命名就是通过输入文件名称后面加".out.xml"。
 * 为了保持程序捷径，本程序未做任何异常处理。
 * @author  clebeg
 * @version 0.0.1
 */
public class BatchProcessApp {
    /** 在命令行中第一个没有选项的索引	*/
	private static int firstFile = 0;
	
	/** .gapp 的保存路径*/
	private static File gappFile = null;
	
	/** 
	 * 需要写出的Annotation type，如果没有指定，那么什么都写出到
	 * GateXML.
	 */
	private static List annotTypesToWrite = null;
	
	/**
	 * 文档加载的编码类型，如果没有使用平台默认编码。
	 */
	private static String encoding = null;
	
  /**
   * main方法是程序的入口。 首先我们解析命令行选项（详细信息请查看usage方法），
   * 然后把所有其他命令行参数作为文件名进行处理。每个文件通过指定的应用程序加载、处理，
   * 最终结果保存为 (inputFile.out.xml).
   */
  public static void main(String[] args) throws Exception {
    parseCommandLine(args);

    GateEnvironment.setGateHome();		
    
    // initialise GATE - this must be done before calling any GATE APIs
    Gate.init();

    // load the saved application
    CorpusController application =
      (CorpusController)PersistenceManager.loadObjectFromFile(gappFile);

    // Create a Corpus to use.  We recycle the same Corpus object for each
    // iteration.  The string parameter to newCorpus() is simply the
    // GATE-internal name to use for the corpus.  It has no particular
    // significance.
    Corpus corpus = Factory.newCorpus("BatchProcessApp Corpus");
    application.setCorpus(corpus);

    // process the files one by one
    for(int i = firstFile; i < args.length; i++) {
      // load the document (using the specified encoding if one was given)
      File docFile = new File(args[i]);
      System.out.print("Processing document " + docFile + "...");
      Document doc = Factory.newDocument(docFile.toURL(), encoding);

      // put the document in the corpus
      corpus.add(doc);
      
      // run the application
      application.execute();

      // remove the document from the corpus again
      corpus.clear();

      String docXMLString = null;
      // if we want to just write out specific annotation types, we must
      // extract the annotations into a Set
      if(annotTypesToWrite != null) {
        // Create a temporary Set to hold the annotations we wish to write out
        Set annotationsToWrite = new HashSet();
        
        // we only extract annotations from the default (unnamed) AnnotationSet
        // in this example
        AnnotationSet defaultAnnots = doc.getAnnotations();
        Iterator annotTypesIt = annotTypesToWrite.iterator();
        while(annotTypesIt.hasNext()) {
          // extract all the annotations of each requested type and add them to
          // the temporary set
          AnnotationSet annotsOfThisType =
              defaultAnnots.get((String)annotTypesIt.next());
          if(annotsOfThisType != null) {
            annotationsToWrite.addAll(annotsOfThisType);
          }
        }

        // create the XML string using these annotations
        docXMLString = doc.toXml(annotationsToWrite);
      }
      // otherwise, just write out the whole document as GateXML
      else {
        docXMLString = doc.toXml();
      }

      // Release the document, as it is no longer needed
      Factory.deleteResource(doc);

      // output the XML to <inputFile>.out.xml
      String outputFileName = docFile.getName() + ".out.xml";
      File outputFile = new File(docFile.getParentFile(), outputFileName);

      // Write output files using the same encoding as the original
      FileOutputStream fos = new FileOutputStream(outputFile);
      BufferedOutputStream bos = new BufferedOutputStream(fos);
      OutputStreamWriter out;
      if(encoding == null) {
        out = new OutputStreamWriter(bos);
      }
      else {
        out = new OutputStreamWriter(bos, encoding);
      }

      out.write(docXMLString);
      
      out.close();
      System.out.println("done");
    } // for each file

    System.out.println("All done");
  } // void main(String[] args)


  /**
   * Parse command line options.
   */
  private static void parseCommandLine(String[] args) throws Exception {
    int i;
    // iterate over all options (arguments starting with '-')
    for(i = 0; i < args.length && args[i].charAt(0) == '-'; i++) {
      switch(args[i].charAt(1)) {
        // -a type = write out annotations of type a.
        case 'a':
          if(annotTypesToWrite == null) annotTypesToWrite = new ArrayList();
          annotTypesToWrite.add(args[++i]);
          break;

        // -g gappFile = path to the saved application
        case 'g':
          gappFile = new File(args[++i]);
          break;

        // -e encoding = character encoding for documents
        case 'e':
          encoding = args[++i];
          break;

        default:
          System.err.println("Unrecognised option " + args[i]);
          usage();
      }
    }

    // set index of the first non-option argument, which we take as the first
    // file to process
    firstFile = i;

    // sanity check other arguments
    if(gappFile == null) {
      System.err.println("No .gapp file specified");
      usage();
    }
  }

  /**
   * Print a usage message and exit.
   */
  private static final void usage() {
    System.err.println(
   "Usage:\n" +
   "   java sheffield.examples.BatchProcessApp -g <gappFile> [-e encoding]\n" +
   "            [-a annotType] [-a annotType] file1 file2 ... fileN\n" +
   "\n" +
   "-g gappFile : (required) the path to the saved application state we are\n" +
   "              to run over the given documents.  This application must be\n" +
   "              a \"corpus pipeline\" or a \"conditional corpus pipeline\".\n" +
   "\n" + 
   "-e encoding : (optional) the character encoding of the source documents.\n" +
   "              If not specified, the platform default encoding (currently\n" +
   "              \"" + System.getProperty("file.encoding") + "\") is assumed.\n" +
   "\n" + 
   "-a type     : (optional) write out just the annotations of this type as\n" +
   "              inline XML tags.  Multiple -a options are allowed, and\n" +
   "              annotations of all the specified types will be output.\n" +
   "              This is the equivalent of \"save preserving format\" in the\n" +
   "              GATE GUI.  If no -a option is given the whole of each\n" +
   "              processed document will be output as GateXML (the equivalent\n" +
   "              of \"save as XML\")."
   );

    System.exit(1);
  }

 
}
 
