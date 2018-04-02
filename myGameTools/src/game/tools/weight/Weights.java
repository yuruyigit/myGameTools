package game.tools.weight;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import game.tools.utils.Sorts;
import game.tools.utils.Util;

/**
 * @author zhibing.zhou
 * <pre> <b>
 * 使用说明：
 * 	在实体类中必须继承Weight父类，如：market extend Weight。
 * 	删除子类中，weight字段，让其使用父类中的weight参与计算。
 *	该父类，添加了三个关于权重计算的属性 : weight,minWeight,maxWeight。
 *</b>
 *
 * 	<b>继承Weight父类，删除子类(market)中的weight权重字段，使用父类(Weight)中的weight字段。</b>
	public class market extends Weight
	{
	 	 private int id; 
		 private int cost_resource; 
		 private int item_id; 
		 private int min_count; 
		 private int max_count; 
		 private int min_price; 
		 private int max_price;
		 <del>private int weight;</del>
		 //添加总权重字段，用于随机最大范围
		 private static int totalWeight;
	}
	
	<b>返回的weightObjects中通过getTotalWeight()获取总权重，设置到新添加的market中的totalWeight中，用于权重的随机最大范围值</b>
	WeightObjects weightObjects = Weights.calcWeight(marketMap);
	market.setTotalWeight(weightObjects.getTotalWeight());
	
	<b>在随机的时候，需要用到totalWeight字段传入，会根据权重返回出随机的对应类型的对象</b>
	return Weights.getRandomWeightMap(marketMap, market.getTotalWeight());
	
	
	
 * </pre>
 */
public class Weights
{
	private static final String DEFAULT_SORT_ID_FIELD = "id";
	
	/**
	 * <pre>
	 * @param weightList 需要计算权重的列表
	 * 	<b> 排序字段，默认字段名为：id </b>
	 * @return 返回一个计算好的有序权重对象列表
	 * <pre>
	 */
	@SuppressWarnings("all")
	public static WeightObjects calcWeight(Map weightMap)
	{
		return calcWeightObjects(map2List(weightMap), DEFAULT_SORT_ID_FIELD );
	}
	
	public static WeightObjects calcWeight(Map weightMap, String sortIdField)
	{
		return calcWeightObjects(map2List(weightMap), sortIdField );
	}
	
	/**
	 * <pre>
	 * @param weightList 需要计算权重的列表
	 * 	<b>排序字段，默认字段名为：id</b>
	 * @return 返回一个计算好的有序权重对象列表
	 * <pre>
	 */
	public static WeightObjects calcWeight(List weightList)
	{
		return calcWeightObjects(weightList, DEFAULT_SORT_ID_FIELD );
	}
	
	
	public static WeightObjects calcWeight(List weightList, String sortIdField)
	{
		return calcWeightObjects((List<Weight>)weightList , sortIdField);
	}
	/**
	 * <pre>
	 * @param weightList 需要计算权重的列表
	 * @param sortIdField 排序字段 
	 * @return 返回一个计算好的有序权重对象，该对象中有totalWeight字段，则为总权重值。
	 * </pre>
	 */
	@SuppressWarnings("all")
	private static WeightObjects calcWeightObjects(List<Weight> weightList, String sortIdField)
	{
		Sorts.sort(weightList, sortIdField);		//正序排序
		
		List<WeightObject> weightObjectList = new ArrayList<WeightObject>(weightList.size());
		
		int temp = 0 , totalWeight = 0 , minWeight = 0 , maxWeight = 0;
		
		for (Weight weight : weightList)
		{
			totalWeight += weight.getWeight();			//总权重
			
			minWeight = temp +1;
			maxWeight = temp + weight.getWeight();
			
			weight.setMinWeight(minWeight);
			weight.setMaxWeight(maxWeight);
			
			weightObjectList.add(new WeightObject(weight, minWeight, maxWeight));
			
			temp = weight.getMaxWeight();
		}
		
		return new WeightObjects(totalWeight, weightObjectList);
	}
	
	/**
	 * @param weightObjects 权重操作对象
	 * @param isDebar 是否过滤随机的对象，避免重复随机。
	 * @return 返回一个根据权重随机对应的对象
	 */
	public static <T> T getRandomWeight(WeightObjects weightObjects , boolean isDebar)
	{
		int randomNo = Util.getRandomInt(1, weightObjects.getTotalWeight());// 随机数字
		
		List<WeightObject> weightList = weightObjects.getWeightObjectList();
		
		T result = null;
		
//		System.out.println("weightObject = " + JSONObject.toJSONString(weightObjects));
		
		//要过滤移除的权重对象，移除权重对象的权重差值。
		int removeIndex = -1  , wegithGapValue = 0;
		
		for (int i = 0; i < weightList.size(); i++) 
		{
			WeightObject weightObject = weightList.get(i);
			if(weightObject.getMinWeight()<=randomNo && weightObject.getMaxWeight()>=randomNo)	//满足权重
			{
				result = (T)weightObject.getObject();
				
				if(isDebar)			//如果要过虑已确定的数据
				{
					removeIndex = i;
					wegithGapValue = weightObject.getMaxWeight() - (weightObject.getMinWeight() - 1);
				}
				else
					break;
			}
			
			if(removeIndex >-1 && isDebar)		//依次递减过滤排除的权重
			{
				weightObject.setMinWeight(weightObject.getMinWeight()-wegithGapValue) ;
				weightObject.setMaxWeight(weightObject.getMaxWeight()-wegithGapValue);
			}
		}
		
		if(isDebar)
		{
			weightObjects.setTotalWeight(weightObjects.getTotalWeight()-wegithGapValue);	//重新算权重最大值
			
			if(removeIndex >-1 && isDebar)			//确定了移除的下标，进行过滤排除
				weightList.remove(removeIndex);
		}
		
		return result;
	}
	
	/**
	 * @param weightList	传入需要权重随机获取的列表数据对象（注：该列表已经经 <b>Weights.calcWeight</b> 计算出当前最小最大权重值)
	 * @param totalWeight	该列表中对应的总权重
	 * @return
	 */
	public static <T> T getRandomWeight(List weightList , int totalWeight)
	{
		if(weightList.size() == 1)
			return (T)weightList.get(0);
		
		int randomNo = Util.getRandomInt(1, totalWeight);// 随机数字
		for (Object object : weightList) 
		{
			Weight weight = (Weight)object;
			if(weight.getMinWeight()<=randomNo && weight.getMaxWeight()>=randomNo)	//满足权重
				return (T)weight;
		}
		return null;
	}
	
	/**
	 * @param weightMap		传入需要权重随机获取的列表数据对象（注：该列表已经经 <b>Weights.calcWeight</b> 计算出当前最小最大权重值)
	 * @param totalWeight	该列表中对应的总权重
	 * @return
	 */
	public static <T> T getRandomWeight(Map weightMap , int totalWeight)
	{
		if(weightMap.size() == 1)
			return (T)weightMap.values().iterator().next();
		
		int randomNo = Util.getRandomInt(1, totalWeight);// 随机数字
		Iterator iterator = weightMap.keySet().iterator();
		while(iterator.hasNext())
		{
			Weight weight = (Weight)weightMap.get(iterator.next());
			if(weight.getMinWeight()<=randomNo && weight.getMaxWeight()>=randomNo)	//满足权重
				return (T)weight;
		}
		return null;
	}
	
	private static ArrayList<Weight> map2List(Map weightMap)
	{
		return new ArrayList<>(weightMap.values());
	}
}
