package game.tools.weight;

public class WeightObject 
{
	private Object object; 
	private int minWeight;
	private int maxWeight;
	
	
	public WeightObject(Object object, int minWeight, int maxWeight) 
	{
		this.object = object;
		this.minWeight = minWeight;
		this.maxWeight = maxWeight;
	}
	
	public <T> T getObject() {		return (T)object;	}
	public int getMinWeight() {		return minWeight;	}
	public int getMaxWeight() {		return maxWeight;	}
	public void setMinWeight(int minWeight) {		this.minWeight = minWeight;	}
	public void setMaxWeight(int maxWeight) {		this.maxWeight = maxWeight;	}
	
}
