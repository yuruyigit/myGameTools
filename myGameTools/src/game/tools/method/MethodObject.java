package game.tools.method;

import java.lang.reflect.Method;

import com.google.protobuf.GeneratedMessageLite;
public class MethodObject
{

	/** 2018年4月19日 上午2:21:07 protocol解析数据函数名*/
	private static final String PROTOBUFFER_PARSE_FORM = "parseFrom";
	
	private Object object;
	
	private int protocolNo;
	
	private Method method;
	
	private Method protobufferParseFromMethod;

	public MethodObject(int protocolNo, Object object, Method method) 
	{
		this.object = object;
		this.protocolNo = protocolNo;
		this.method = method;
		
		initProtobuffParseMethod();
	}

	/**
	 *  初始化protobuff的解析函数
	 */
	private void initProtobuffParseMethod()
	{
		Class<?> clzssParams [] = this.method.getParameterTypes();
		Class<?> paramClass = clzssParams[clzssParams.length - 1];
		
		try 
		{
			if(paramClass.getSuperclass().getName().indexOf("com.google.protobuf") >= 0)	//如果是protocolbuf的类
				this.protobufferParseFromMethod = paramClass.getMethod(MethodObject.PROTOBUFFER_PARSE_FORM, byte[].class);
		}
		catch (Exception e)		{		}
	}
	
	public Object getObject() {		return object;	}
	public int getProtocolNo() {		return protocolNo;	}
	public Method getMethod() {		return method;	}
	public Method getProtobufferParseFromMethod() {		return protobufferParseFromMethod;	}
}
