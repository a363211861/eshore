package pri.clebeg.machine_learning;

import java.util.List;

public class DicisionTreeNode {
	/**
	 * 是否是叶子节点，如果是叶子节点那么
	 * labelName	记录的就是分类名
	 * labelIndex	记录的分类名对应的索引
	 * 否则：
	 * labelName	记录的分裂节点属性名
	 * labelIndex	记录的属性对应索引
	 * */
	private boolean isLeaf;
	private String labelName;
	private int labelIndex;
	
	/**
	 * 记录的是节点由何值产生，如果是root节点则此值为NULL
	 * */
	private String value;
	
	private List<DicisionTreeNode> childs;

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	

	public int getLabelIndex() {
		return labelIndex;
	}

	public void setLabelIndex(int labelIndex) {
		this.labelIndex = labelIndex;
	}

	public List<DicisionTreeNode> getChilds() {
		return childs;
	}

	public void setChilds(List<DicisionTreeNode> childs) {
		this.childs = childs;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
