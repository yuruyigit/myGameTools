package game.tools.weight;

/**
 * @author zhibing.zhou
 * <pre> <b>
 * 使用说明：
 * 	在实体类中必须继承Weight父类，如：market extend Weight。
 * 	在子类中，则要删除对应父类的weight字段，让其使用父类中的weight参与计算。
 *	该父类，添加了三个关于权重计算的属性 : weight,minWeight,maxWeight。
 * </b></pre>
 */
public class Weight 
{
	protected int weight;
	protected int minWeight;
	protected int maxWeight;

	public int getMinWeight() {
		return minWeight;
	}

	public void setMinWeight(int minWeight) {
		this.minWeight = minWeight;
	}

	public int getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(int maxWeight) {
		this.maxWeight = maxWeight;
	}

	public int getWeight() {
		return weight;
	}

	private void setWeight(int weight) {
		this.weight = weight;
	}
}
