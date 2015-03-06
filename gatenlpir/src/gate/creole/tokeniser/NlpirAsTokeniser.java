package gate.creole.tokeniser;

import gate.Gate;

import java.net.URL;

public class NlpirAsTokeniser {
	public Boolean init(int charset_type, URL usrdict) {
		String argu = System.getProperty("user.dir");
		if (Gate.runningOnUnix() == true)
			argu += "/plugins/ChineseTokeniser/lib";
		else 
			argu += "\\plugins\\ChineseTokeniser\\lib";
		int init_flag = CLibrary.Instance.NLPIR_Init(argu, charset_type, "0");
		String nativeBytes = null;
		if (0 == init_flag) {
			nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
			System.err.println("初始化失败！fail reason is "+nativeBytes);
			return false;
		}
		if(usrdict != null){
			String dictpath = usrdict.getPath();				      
			String usrdirstring = dictpath.substring(1, dictpath.length());
			
			//import user dictionary
			CLibrary.Instance.NLPIR_ImportUserDict(usrdirstring, true);
			
		}
		return true;
	}
	
	public void cleanup() {
		CLibrary.Instance.NLPIR_Exit();
	}
	
	/**
     * output token and POS pair, seperated by " "
     * for example:
     * input : 中国是世界上人口最多的国家。
     * output: 中国/ns 是/v 世界/n 上/f 人口/n 最/d 多/a 的/u 国家/n 。/w 
     * @param input String input
     * @param Encoding input's encoding
     * @param PosType POS Type
     * @exception ExecutionException
     */
     public String Process(String input, String Encoding, int PosType) {
    	String nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(input, 1);
		return nativeBytes;
  
	}		
}
