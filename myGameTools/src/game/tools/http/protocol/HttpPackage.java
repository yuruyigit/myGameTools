package game.tools.http.protocol;

import java.io.Serializable;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpPackage implements Serializable
{
	private static final long serialVersionUID = 1L;

	private transient String stringValue;
	
	private transient HttpServletRequest req; 
	
	private transient HttpServletResponse resp; 
	
	private Object [] paramArray;
	
	private transient int index = 0;
	
	HttpPackage( HttpServletRequest req, HttpServletResponse resp , Object []paramArray) 
	{
		this.req = req;
		this.resp = resp;
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
	
	public HttpServletRequest getReq() {return req;}

	public HttpServletResponse getResp() {return resp;}

}
