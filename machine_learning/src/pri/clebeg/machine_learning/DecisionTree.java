package pri.clebeg.machine_learning;

import java.util.ArrayList;
import java.util.List;


/**
 * class <code>DecisionTree</code>决策树
 * 本类提供决策树的基本算法，决策树的算法核心在于如何进行属性选择，本类将提供多种选择方式
 * 第二个重要的地方在于算法终止条件，同样也提供各种选择方式，当然也有默认条件
 * 第一版本只支持离散属性，第二版本再在增加支持连续属性的
 * 纯属学习用途，效率有待提升
 * @author   clebeg
 * @version	 0.0.1
 * @see      java.lang.Class
 * @since    JDK1.8
 * */
public class DecisionTree {
	//这里记录属性名称和其对应的可能取值，必须保证存入的数据的顺序
	private List<String> attributes;
	private List<String[]> values;
	
	//这里记录的是样本数据，而且每一个样本的下标索引与上面的属性是对应的
	private List<String[]> samples;
	private int classIndex = 0;
	
	//剩余还没有决策的属性值下标
	private List<Integer> restAttrsIndex;
	private int sampleMinNum = 1;
	/**
	 * 读取样本数据，这是整个程序的第一步
	 * */
	public void readData() {
		
	}
	
	/**
	 * 根据初始化的样本信息建立决策树。
	 * */
	private DicisionTreeNode treeGrowth(List<String[]> data, String value) {
		if(stopCond(data)) {
			//如果满足结束条件就创建叶子节点
			DicisionTreeNode leaf = new DicisionTreeNode();
			leaf.setLabelIndex(this.Classify(data));
			leaf.setLabelName(this.values.get(this.classIndex)[leaf.getLabelIndex()]);
			leaf.setLeaf(true);
			leaf.setValue(value);
			return leaf;
			
		} else {
			//如果还不能结束，就继续分裂
			DicisionTreeNode root = new DicisionTreeNode();
			root.setLabelIndex(findBestSplit(data));//选择最佳分裂属性，得到下标索引位置
			root.setLabelName(this.attributes.get(root.getLabelIndex()));
			root.setValue(value);
			//生成所有的孩子节点，这里的方式每一个孩子节点都要遍历一次
			List<DicisionTreeNode> childs = new ArrayList<DicisionTreeNode>();
			for (String attrValue : this.values.get(root.getLabelIndex())) {
				List<String[]> tmpData = new ArrayList<String[]>();
				for (String[] strs : data) {
					if (strs[root.getLabelIndex()].equals(attrValue) == false)
						tmpData.add(strs);
				}
				childs.add(treeGrowth(tmpData, attrValue));
			}
			root.setChilds(childs);
		}
		
		return null;
	}
	
	
	/**
	 * 算法结束条件，当所有样本全部属于同一类，或者属性值相等，
	 * 或者样本个数达到最小指定的数目，算法停止。
	 * */
	private boolean stopCond(List<String[]> data) {
		if (data.size() <= this.sampleMinNum)
			return true;
		
		//判断所有类别是否属于同一类，当然这个结束条件还可以加强
		String classValue = data.get(0)[this.classIndex];
		for (int j = 1; j < data.size(); j++) {
			if (classValue.equals(data.get(j)[this.classIndex]) == false)
				return false;
		}
		
		return true;
	}
	
	/**
	 * 为叶子节点确定类标号。
	 * 通过记录每一类的个数，从中找到类最多的那一类即为最终分类。
	 * */
	private int Classify(List<String[]> data) {
		int[] numbers = new int[this.values.get(this.classIndex).length];
		for (String[] sample : data) {
			for (int i = 0; i < this.values.get(this.classIndex).length; i++) {
				if (sample[this.classIndex].equals(this.values.get(this.classIndex)[i]))
					numbers[i]++;
			}
		}
		int maxIndex = 0, max = 0;
		for (int i = 0; i < numbers.length; i++) {
			if (max < numbers[i]) {
				max = numbers[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}
	
	/**
	 * 找到最佳划分属性。
	 * */
	private int findBestSplit(List<String[]> data) {
		int sum = data.size();
		//准备记录每一个待决定的属性
		int[][] prepare = new int[this.restAttrsIndex.size()][];
		
		//初始化每一个属性可能的取值
		for (int i = 0; i < this.restAttrsIndex.size(); i++)
			prepare[i] = new int[this.values.get(this.restAttrsIndex.get(i)).length];
		
		//准备记录每一个属性值的个数
		int[] numbers = new int[this.restAttrsIndex.size()];
		
		//下面开始遍历每一个样本
		for (String[] sample : data) {
			//for (int i = 0; i < this.restAttrsIndex.size(); i++)
		}
		return 0;
	}
}
