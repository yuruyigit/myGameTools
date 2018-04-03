package game.tools.rpc.linda;

import java.util.Arrays;

public class LindaRpcPackage 
{
	private Object [] paramArray;
	
	private transient String stringValue;
	
	private int index = 0;
	
	public LindaRpcPackage(Object [] paramArray) 
	{
		this.paramArray = paramArray;
	}
	
	public <T> T get()
	{
		Object object = (T)paramArray[index];
		
		if(index < paramArray.length - 1)
			index++;
		
		return (T)object;
	}
	
	public <T> T get(int index)
	{
		if(index > paramArray.length - 1 || index  < 0)
		{
			try {
				throw new Exception("Array Index Out ! at : " +index);
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
		{
			synchronized (this) {
				if(stringValue == null)
					stringValue = Arrays.toString(paramArray);
			}
		}
		return stringValue;
	}
}
