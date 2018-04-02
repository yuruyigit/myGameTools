package game.tools.weight;
import java.util.List;

public class WeightObjects 
{
	private int totalWeight;
	private List<WeightObject> weightObjectList;
	
	public WeightObjects(int totalWeight, List<WeightObject> weightObjectList) 
	{
		this.totalWeight = totalWeight;
		this.weightObjectList = weightObjectList;
	}

	public int getTotalWeight() {		return totalWeight;	}
	public void setTotalWeight(int totalWeight) {		this.totalWeight = totalWeight;	}
	public List<WeightObject> getWeightObjectList() {		return weightObjectList;	}
	
}
