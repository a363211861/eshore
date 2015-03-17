package eshore.cn.it.data;

import java.io.Serializable;

public class SiteMap implements Serializable{
	private static final long serialVersionUID = 71314950422298380L;
	private String id;
	//网站名称
	private String siteName;
	//网站主页地址
	private String indexUrl;
	//网站导航地址
	private String navigateUrl;
	//分类信息选择器
	private String selectStr;

	

	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getSiteName() {
		return siteName;
	}



	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}



	public String getIndexUrl() {
		return indexUrl;
	}



	public void setIndexUrl(String indexUrl) {
		this.indexUrl = indexUrl;
	}



	public String getNavigateUrl() {
		return navigateUrl;
	}



	public void setNavigateUrl(String navigateUrl) {
		this.navigateUrl = navigateUrl;
	}



	public String getSelectStr() {
		return selectStr;
	}



	public void setSelectStr(String selectStr) {
		this.selectStr = selectStr;
	}



	public String toString() {
		return this.siteName + " : " + navigateUrl + " -> " + this.selectStr;
	}
}
