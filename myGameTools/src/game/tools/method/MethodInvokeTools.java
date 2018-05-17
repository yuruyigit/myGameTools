package game.tools.method;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Set;
import game.tools.utils.ClassUtils;

public class MethodInvokeTools 
{
	public static HashMap<Integer , MethodObject> getInvokeMethod(String classPath , Class aniClzss , IMethodNo imethodNo)
	{
		return getInvokeMethod(classPath, aniClzss, true , imethodNo);
	}
	
	/**
	 * @param classPath 传入要扫描的类
	 * @param aniClzss 传入要获取筛选的注解class
	 * @param safe 是否安全检测，这里主要是检测，无法创建对象而使用的对象函数。
	 * @param imethodNo 传入该注解如何获取ID号的接口实现
	 * @return
	 */
	public static HashMap<Integer , MethodObject> getInvokeMethod(String classPath , Class aniClzss , boolean safe ,IMethodNo imethodNo)
	{
		/** 协议处理函数集合 */
		HashMap<Integer , MethodObject> methodObjectMap = new HashMap<Integer , MethodObject>();
		
		try 
		{
			if(!methodObjectMap.isEmpty())		//如果不是空
				methodObjectMap.clear();
				
			Set<String> setString = ClassUtils.getClassName(classPath, true);
			
			for (String string : setString) 
			{
				Class<?> clzss = ClassLoader.getSystemClassLoader().loadClass(string);
				
				Object clzssObject = null;
				
				if(isCreate(clzss))			
					clzssObject = clzss.newInstance();
				
				Method[] methodArray = clzss.getDeclaredMethods();
				
				for (Method method : methodArray) 
				{
					Annotation annot = method.getAnnotation(aniClzss);
					
					if(annot != null)
					{
						int methodNo = imethodNo.getMethodNo(annot);
						
						if(methodObjectMap.containsKey(methodNo))
						{
							throw new Exception("LindaRpcServer : duplicate rpcNo key !! at  methodNo " + methodNo);
						}
						
						if(ClassUtils.isCreate(clzss))			
							clzssObject = clzss.newInstance();
						
						if(safe)				//如果进行安全检查
						{
							if(clzssObject == null)
							{
								if(!Modifier.isStatic(method.getModifiers()))			//如果这个函数是对象函数，并且对象是null，抛出错误提示。
								{
									throw new Exception("LindaRpcServer : Method is Object Method , but Object not create !! check is exist Empty construct method at " + methodNo);	
								}
							}
						}
						methodObjectMap.put(methodNo, new MethodObject(methodNo , clzssObject , method));
					}
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return methodObjectMap;
	}
	
	
	
	/**
	 * @return 是否有空的构建函数，用于创建对象使用
	 */
	public static boolean isCreate(Class clzss)
	{
		boolean isAbs = Modifier.isAbstract(clzss.getModifiers()) ;
		if(isAbs)			//如果是抽象类
			return false;
		
		Constructor [] constrArray = clzss.getConstructors();
		for (Constructor constructor : constrArray) 
		{
			if(constructor.getParameterTypes().length == 0)
				return true;
		}
		return false;
	}
	
}
