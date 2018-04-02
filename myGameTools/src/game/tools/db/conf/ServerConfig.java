package game.tools.db.conf;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import game.tools.db.cache.CacheFromData;
import game.tools.log.LogUtil;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;

public class ServerConfig 
{
	protected int id;
	protected String key ; 
	protected String val;
	protected String desc;
	
	private static boolean isPrintInfo ;
	
	/**
	 * 开启打印信息
	 */
	public static void openPrintInfo()
	{
		isPrintInfo = true;
	}
	
	/**
	 *<pre>
	 * 注意，传入的calss规范约定须有  id , key , val , desc ,这四个字段。
	 * 传入的class必需要继承于ServerConfig该类，并把class子类中的属性删除,并包留子类中的get set方法,
	 * 从而得以使用字段重名中的父类属性字段。
	 *</pre>
	 * @param clzss 传入的保存属性的实例 
	 * @param serverConfList 数据列表
	 */
	public static void initServerConfig(Class clzss , Collection<ServerConfig> serverConfList)
	{
		Field [] fieldArray = clzss.getDeclaredFields();
		
		try 
		{
			for (ServerConfig planConfig : serverConfList)
				setServerConfigAttribute(planConfig , fieldArray);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		serverConfList.clear();
	}
	
	
	/**
     * 为对象动态增加属性，并同时为属性赋值
     * @param className 需要创建的java类的名称
     * @param fieldMap  字段-字段值的属性map，需要添加的属性
     * @return
     */
    public static void addField(String className,Map<String,Object> fieldMap) throws Exception
    {

        ClassPool pool = ClassPool.getDefault();//获取javassist类池
        CtClass ctClass = pool.makeClass(className,pool.get(ServerConfig.class.getName()));//创建javassist类

        // 为创建的类ctClass添加属性
        Iterator it = fieldMap.entrySet().iterator();

        while (it.hasNext())	 // 遍历所有的属性 
        { 
            Map.Entry entry = (Map.Entry) it.next();
            String fieldName = (String)entry.getKey();
            Object fieldValue = entry.getValue();

            // 增加属性，这里仅仅是增加属性字段
            String fieldType = fieldValue.getClass().getName();
            CtField ctField = new CtField(pool.get(fieldType),fieldName, ctClass);
            ctField.setModifiers(Modifier.PUBLIC );
            ctClass.addField(ctField);
        }

//        Class c=ctClass.toClass();// 为创建的javassist类转换为java类
//
//        Object newObject = c.newInstance();// 为创建java对象
//
// 
//
//// 为创建的类newObject属性赋值
//
//        it = fieldMap.entrySet().iterator();
//
//        while (it.hasNext()) {  // 遍历所有的属性
//
//            Map.Entry entry = (Map.Entry) it.next();
//
//            String fieldName = (String)entry.getKey();
//
//            Object fieldValue = entry.getValue();
//
//            // 为属性赋值
//
//            this.setFieldValue(newObject,fieldName,fieldValue);
//
//        }

//        return newObject;

    }

	/**
	 *<pre>
	 * 注意，传入的calss规范约定须有  id , key , val , desc ,这四个字段。
	 * 传入的class必需要继承于ServerConfig该类，并把class子类中的属性删除，从而得以使用字段重名中的父类属性字段。
	 *</pre>
	 * @param clzss 传入的保存属性的实例 
	 * @param serverConfList 数据列表
	 */
	public static void initServerConfig(Class clzss , List<ServerConfig> serverConfList)
	{
		
		if(serverConfList == null)
			return ;
		
		Field [] fieldArray = clzss.getDeclaredFields();
		
		try 
		{
			for (ServerConfig planConfig : serverConfList)
				setServerConfigAttribute(planConfig , fieldArray);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		serverConfList.clear();
	}
	
	private static void setServerConfigAttribute(ServerConfig serverConfig , Field [] fieldArray) throws Exception
	{
		Field field = getFieldByName(fieldArray , serverConfig.key);
		
		if(field == null)
		{
			throw new Exception("Server Config Error " + serverConfig.key);
		}
		
		String fieldName = serverConfig.key , fieldValue = serverConfig.val.trim();
		try 
		{
			field.setAccessible(true);
			
			if (field.getType().toString().equals("int"))
				field.set(fieldName, Integer.parseInt(fieldValue));
			else if (field.getType().toString().equals("double"))
				field.set(fieldName, Double.parseDouble(fieldValue));
			else if (field.getType().toString().equals("long"))
					field.set(fieldName, Long.parseLong(fieldValue));
			else if (field.getType().toString().equals("float"))
				field.set(fieldName, Float.parseFloat(fieldValue));
			else if (field.getType().toString().equals("class java.lang.String"))
				field.set(fieldName, new String(fieldValue.getBytes("utf-8") , "utf-8"));
			else if (field.getType().toString().equals("boolean"))
				field.set(fieldName, Boolean.parseBoolean(fieldValue));
			
			if(isPrintInfo)
				System.out.println(fieldName + " = " + fieldValue);
		}
		catch (Exception e) 
		{
			System.out.println(field.getName() + " at error !" );
			LogUtil.error(e);
			e.printStackTrace();
		}
	}
	
	private static Field getFieldByName(Field [] fieldArray , String name)
	{
		for (Field field : fieldArray) 
		{
			if(field.getName().equalsIgnoreCase(name))
				return field;
		}
		return null;
	}

	
	public static void main(String[] args) throws Exception
	{
//		CacheFromData cfd = new CacheFromData("game.tools.db.entity" , false ,"jdbc:mysql://192.168.1.131:3306/game_logic_conf" , "root" , "root");
//		cfd.initCacheFromData();
//		
//		ConcurrentHashMap<Long , ServerConfig> confMap = cfd.getMapByTable("prf_server_config");
//		
//		ServerConfig.initServerConfig(PlanServerConfig.class, confMap.values());
		
		HashMap<String, Object> fieldMap = new HashMap<>();
		fieldMap.put("name", "张三");
		fieldMap.put("age", 18);
		
		ServerConfig.addField(ServerConfig.class.getName(), fieldMap);
		System.out.println(PlanServerConfig.getINIT_GOLD() + "  " + PlanServerConfig.getINIT_DIAMOND());
		
	}

	public String getKey() {		return key;	}
	public String getVal() {		return val;	}
	public void setVal(String val) {		this.val = val;	}
	public String getDesc() {		return desc;	}
	
}
