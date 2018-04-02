package game.tools.utils;

import java.util.UUID;

public class UUIDTools 
{
	public  static  String getUUid()
	{
		return UUID.randomUUID().toString().replace("-", "");
	}
	
}
