package game.tools.rpc.linda;

import java.util.Arrays;

public class LindaRpcPackage 
{
	private Object [] paramArray;
	
	private transient String stringValue;
	
	private int index;
	
	public LindaRpcPackage(Object [] paramArray) 
	{
		this.paramArray = paramArray;
	}
	
	public <T> T get()
	{
		if(index < paramArray.length - 1)
			index++;
		return (T)paramArray[index];
	}
	
	public <T> T get(int index)
	{
		if(index > paramArray.length - 1)
		{
			try {
				throw new Exception("Array Index Out ! ");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return (T)paramArray[index];
	}
	
	@Override
	public String toString() 
	{
		if(stringValue == null)
			stringValue = Arrays.toString(paramArray);
		return stringValue;
	}
}
