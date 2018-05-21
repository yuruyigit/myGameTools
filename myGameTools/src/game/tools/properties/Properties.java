package game.tools.properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;

import game.tools.utils.StringTools;

public class Properties 
{
	private HashMap<String, Object> configMap = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(String key)
	{
		return (T)configMap.get(key);
	}
	
	protected java.util.Properties initProperties(String path)
	{
		return initProperties(getInputStream(path) , Properties.class);
	}
	
	protected java.util.Properties initProperties(String path , Class clzss)
	{
		return initProperties(getInputStream(path) , clzss);
	}
	
	protected static java.util.Properties initProperties(Properties p , String path , Class clzss)
	{
		return p.initProperties(getInputStream(path) , clzss);
	}
	
	@SuppressWarnings("rawtypes")
	protected java.util.Properties initProperties(InputStream is, Class clzss)
	{
		java.util.Properties properties = null;
		try
		{
			properties = new java.util.Properties();
			properties.load(is);
			
			Object value = null;
			
			Iterator<Object> iterator = properties.keySet().iterator();
			
			String notFieldMessage = "Properties.initProperties: Not Find Field  ";
			
			while(iterator.hasNext())
			{
				String fieldName = iterator.next().toString();
				String fieldValue = properties.getProperty(fieldName);
				
				if(clzss != null)			//如果可以映射类文件属性
				{
					Field field = null;
					try {
						field = clzss.getDeclaredField(fieldName);
					} catch (NoSuchFieldException e) {
						notFieldMessage += e.getLocalizedMessage() + ", ";
					}
					
					if(field != null)
					{
						if (field.getAnnotations().length <= 0)
						{
							field.setAccessible(true);
//							Field modifiersField = Field.class.getDeclaredField("modifiers");  
//							modifiersField.setAccessible(true);  
//							modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
							
							if (field.getType().toString().equals("int"))
								field.set(fieldName, Integer.parseInt(fieldValue));
							else if (field.getType().toString().equals("double"))
								field.set(fieldName, Double.parseDouble(fieldValue));
							else if (field.getType().toString().equals("long"))
								field.set(fieldName, Long.parseLong(fieldValue));
							else if (field.getType().toString().equals("float"))
								field.set(fieldName, Float.parseFloat(fieldValue));
							else if (field.getType().toString().equals("class java.lang.String"))
								field.set(fieldName, new String(fieldValue.getBytes("iso-8859-1") , "utf-8"));
							else if (field.getType().toString().equals("boolean"))
								field.set(fieldName, Boolean.parseBoolean(fieldValue));
						}
					}
				}
					
				
				if(StringTools.isNumber(fieldValue))
				{
					if(fieldValue.indexOf(".") >= 0)			//是小数
						value = Float.parseFloat(fieldValue);
					else
						value = Integer.parseInt(fieldValue);
				}
				else
					value = fieldValue;
				
				configMap.put(fieldName, value);
			}
			
			System.err.println(notFieldMessage);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return properties;
	}
	
	
	protected static void saveProperties(java.util.Properties properties, Class clazz , String path)
	{
		String fieldName = "";
		Field[] fields = clazz.getDeclaredFields();
		try
		{
			for (Field field : fields)
			{
				fieldName = field.getName();
				
				if(!properties.containsKey(fieldName))
					continue;
				
				field.setAccessible(true);
				properties.setProperty(fieldName, field.get(null).toString());
				
			}
			
			properties.store(new FileOutputStream(path), "CopyRight@Code 2014");
		} 
		catch (Exception e)
		{
			System.out.println("fieldName = " + fieldName);
			e.printStackTrace();
		}
	}
	
	private static InputStream getInputStream(String path)
	{
		try 
		{
//			InputStream is = Properties.class.getResourceAsStream(path);
			InputStream is = new FileInputStream(path);
			return is;
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			
			return null;
		}
	}
	

	public static void main(String[] args) 
	{
//		Integer b = getValue("sdafd");
////		String n = getValue("sdafd");
//		
		double aa = -19162431.1254;
		System.out.println(StringTools.isNumber(String.valueOf(aa)));
		System.out.println(StringTools.isNumber("asdfasdf123123"));
		System.out.println(StringTools.isNumber("-15.554"));
		System.out.println(StringTools.isNumber("-15"));
		System.out.println(StringTools.isNumber("15"));
		System.out.println(StringTools.isNumber("0.55"));
		System.out.println(StringTools.isNumber("0.55000011111"));
		System.out.println(StringTools.isNumber("0.a55000011111"));
		
		
		Properties p = new Properties();
		
		p.initProperties("conf/jdbc.properties");
		
		String db_user = p.getValue("db_user");
		int n = p.getValue("db_retryAttempts");
		
		System.out.println("db_user = " + db_user + " n = " + n);
//		System.out.println("db_url = " + db_url);
	}
}
