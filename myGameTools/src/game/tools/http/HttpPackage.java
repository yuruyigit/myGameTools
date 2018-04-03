package game.tools.http;

import java.util.Arrays;

public class HttpPackage 
{
	private transient String stringValue;
	
	private Object [] paramArray;
	
	private int index = 0;
	
	public HttpPackage( Object ...paramArray) 
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
