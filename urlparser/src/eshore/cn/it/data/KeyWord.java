package eshore.cn.it.data;

import java.io.Serializable;

public class KeyWord implements Serializable, Comparable<KeyWord>{
	private static final long serialVersionUID = -5724917249672619817L;
	//id,pid,url,host,keywords,label
	private String id;
	private String pid;
	private String url;
	private String host;
	private String keywords;
	private String label;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	@Override
	public int compareTo(KeyWord o) {
		return this.url.compareTo(o.getUrl());
	}
	
	@Override
	public String toString() {
		return id + "," + pid + "," + url + "," + host + "," + keywords + "," + label;
	}
}
