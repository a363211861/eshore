package eshore.cn.it.data;

public class SiteMap {
	//注释信息
	private String comment;
	//网站地址
	private String url;
	//需要提取信息的选择条件
	private String selectStr;
	
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSelectStr() {
		return selectStr;
	}
	public void setSelectStr(String selectStr) {
		this.selectStr = selectStr;
	}
	public String toString() {
		return this.comment + " : " + url + " -> " + this.selectStr;
	}
}
