package game.tools.http;

public class HttpPackage 
{
	private Object [] paramArray;
	
	private int index = 0;
	
	
	public HttpPackage( Object ...paramArray) 
	{
		this.paramArray = paramArray;
	}
	
	public void setProtocolNo(Object value)
	{
		this.paramArray[0] = value;
	}
	
	public void set(int index , Object value)
	{
		this.paramArray[index] = value;
	}
	
	public <T> T getValue(int index)
	{
		return (T)paramArray[index];
	}
	
	public <T> T getValue()
	{
		return getValue(index);
	}
}
