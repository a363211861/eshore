package gate.creole.tokeniser;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface CLibrary extends Library {
	// 定义并初始化接口的静态变量
	CLibrary Instance = (CLibrary) Native.loadLibrary(
			"F:\\java_study\\中文分词ICTCLAS\\ICTCLAS2015\\bin\\ICTCLAS2015\\NLPIR", CLibrary.class);
	
	public int NLPIR_Init(String sDataPath, int encoding,
			String sLicenceCode);
			
	public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);
	public Double NLPIRt_FileProcess(String sSrc, String resultFileName, int bPOSTagged); 
	public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit,
			boolean bWeightOut);
	public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit,
			boolean bWeightOut);
	
	public int NLPIR_AddUserWord(String sWord);//add by qp 2008.11.10
	public int NLPIR_DelUsrWord(String sWord);//add by qp 2008.11.10
	
	public int NLPIR_ImportUserDict(String fileName, boolean overWrite);
	
	public String NLPIR_GetLastErrorMsg();
	public int  NLPIR_SaveTheUsrDic();
	
	public int  NLPIRt_SetPOSmap(int  nPOSmap);
	public void NLPIR_Exit();
	
}
