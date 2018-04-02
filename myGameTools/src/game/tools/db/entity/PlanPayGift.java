package game.tools.db.entity;

public class PlanPayGift
{
 	 private int id; 
	 private String name; 
	 private String item_List; 
	 private int hero_Id; 
	 private int price; 
	 private float dis_Count; 
	 private String product_Id; 
	 private String channel_Id; 
	 private String start_Time; 
	 private String end_Time; 
	 private int limit; 


	 private void setId(int id)
	 { 
		 this.id = id;
	 } 

	 public int getId() 
	 { 
		 return this.id;
	 } 

	 private void setName(String name)
	 { 
		 this.name = name;
	 } 

	 public String getName() 
	 { 
		 return this.name;
	 } 

	 private void setItem_List(String item_List)
	 { 
		 this.item_List = item_List;
	 } 

	 public String getItem_List() 
	 { 
		 return this.item_List;
	 } 

	 private void setHero_Id(int hero_Id)
	 { 
		 this.hero_Id = hero_Id;
	 } 

	 public int getHero_Id() 
	 { 
		 return this.hero_Id;
	 } 

	 private void setPrice(int price)
	 { 
		 this.price = price;
	 } 

	 public int getPrice() 
	 { 
		 return this.price;
	 } 

	 private void setDis_Count(float dis_Count)
	 { 
		 this.dis_Count = dis_Count;
	 } 

	 public float getDis_Count() 
	 { 
		 return this.dis_Count;
	 } 

	 private void setProduct_Id(String product_Id)
	 { 
		 this.product_Id = product_Id;
	 } 

	 public String getProduct_Id() 
	 { 
		 return this.product_Id;
	 } 

	 private void setChannel_Id(String channel_Id)
	 { 
		 this.channel_Id = channel_Id;
	 } 

	 public String getChannel_Id() 
	 { 
		 return this.channel_Id;
	 } 

	 private void setStart_Time(String start_Time)
	 { 
		 this.start_Time = start_Time;
	 } 

	 public String getStart_Time() 
	 { 
		 return this.start_Time;
	 } 

	 private void setEnd_Time(String end_Time)
	 { 
		 this.end_Time = end_Time;
	 } 

	 public String getEnd_Time() 
	 { 
		 return this.end_Time;
	 } 

	 private void setLimit(int limit)
	 { 
		 this.limit = limit;
	 } 

	 public int getLimit() 
	 { 
		 return this.limit;
	 } 



}