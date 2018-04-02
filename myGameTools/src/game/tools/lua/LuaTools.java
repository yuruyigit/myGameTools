package game.tools.lua;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;
import org.keplerproject.luajava.LuaObject;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;
import game.tools.log.LogUtil;
import game.tools.utils.DateTools;
import game.tools.utils.Util;

/**
 * @author zzb
 * 2014-4-10 下午4:12:40
 * lua 工具类
 */
public class LuaTools 
{

	private static final String [] LUA_PATH = {
		"CalcFormula",					//0 计算公式
		"Formula",						//1 计算函数
		"PvpBattleControl",				//2 pvp战斗    <br/>
		"Elements",						//3 法术元素
		"statistics/Main"				//4 统计lua
	};
	
	private static final ConcurrentHashMap<String, LuaState> LUA_MAP = new ConcurrentHashMap<String, LuaState>();
	/** zzb 2014-5-6 下午7:43:50 计算公式的lua*/
	public static final String LUA_CALCFORMULA = "CalcFormula" , LUA_FORMULA = "Formula" , LUA_PVP_BATTLE_CONTROL = "PvpBattleControl",
			LUA_ELEMENTS = "Elements" , LUA_STATISTICS = "RunStatistics";
	
	static
	{
		init();
	}
	
	public static void init()
	{
		synchronized (LUA_MAP) 
		{
			String rootPath = "lua/";
			LuaState lua = LuaStateFactory.newLuaState();
			lua.openLibs();
			lua.LdoFile(rootPath + LUA_CALCFORMULA+".lua");
			LUA_MAP.put(LUA_CALCFORMULA, lua);
			
			lua = LuaStateFactory.newLuaState();
			lua.openLibs();
			lua.LdoFile(rootPath + LUA_FORMULA+".lua");
			LUA_MAP.put(LUA_FORMULA, lua);
			
			lua = LuaStateFactory.newLuaState();
			lua.openLibs();
			lua.LdoFile(rootPath + "pvp/" + LUA_PVP_BATTLE_CONTROL+".lua");
			LUA_MAP.put(LUA_PVP_BATTLE_CONTROL, lua);
			
			lua = LuaStateFactory.newLuaState();
			lua.openLibs();
			lua.LdoFile(rootPath + LUA_ELEMENTS+".lua");
			LUA_MAP.put(LUA_ELEMENTS, lua);
			
			lua = LuaStateFactory.newLuaState();
			lua.openLibs();
			lua.LdoFile(rootPath + "statistics/"+ LUA_STATISTICS+".lua");
			LUA_MAP.put(LUA_STATISTICS, lua);
		}
	}
	
	/**
	 * @param appointDate 指定的日期
	 * @param o  要减去的天数
	 * @return  返回减去的天数的字符串
	 */
	public static String getCostDate(Object appointDate , Object o )
	{
		String date = (String)appointDate;
		String [] arr = date.split("-");
		int costDay = (int)o;
		Calendar cale = Calendar.getInstance();
		cale.set(Integer.parseInt(arr[0]) , Integer.parseInt(arr[1]) - 1, Integer.parseInt(arr[2]));
//		cale.setTimeInMillis(Tools.getCurrentTimeLong());
//		System.out.println("date1 = " + Tools.getSDFDate().format(cale.getTime()));
		cale.add(Calendar.DATE, costDay);
//		System.out.println("date2 = " + Tools.getSDFDate().format(cale.getTime()));
		return  DateTools.getCurrentTimeString(cale.getTimeInMillis());
	}
	
	private static final String CLASS_NAME = "result";
	
	/**
	 * zzb 2014-4-10 下午4:16:25
	 * 路径，返回值数量，函数名，参数列表
	 * @param path 传入的要调用的lua文件
	 * @param rtnNum 执行结果后的返回数量
	 * @param funName 传入的要调用的函数名
	 * @param obj	传入调用lua的参数,
	 * @return 返回对应的调用lua的函数返回数据对象
	 */
	public static Object luaExecute(String path ,int rtnNum, String funName,  Object... obj )
	{
		LuaState lua = LUA_MAP.get(path);
		lua.getGlobal("errFunc");
		// 找到函数 funName
		lua.getField(LuaState.LUA_GLOBALSINDEX, funName);
		
		String type = "";
		try 
		{
			if(obj != null)
			{
				for (int i = 0; i < obj.length; i++) 
				{
					type = obj[i].getClass().getSimpleName();
					if(type.equals("Integer"))
						lua.pushNumber(((int)obj[i]));
					else if(type.equals("String"))
						lua.pushString(obj[i].toString());
					else if(type.equals("Float"))
						lua.pushNumber((float)obj[i]);
					else if(type.equals("Double"))
						lua.pushNumber((double)obj[i]);
					else
						lua.pushObjectValue(obj[i]);
				}
				/**
				 * 参数1    一共放obj.length个参数 , 传放参数的数量
				 * 参数2 lua函数返回值的数量
				 * 参数3  拦截lua报错函数的索引 (这个索引是依次调用lua.getGlobal("errFunc");的次序 )
				 * 例如：
				 * 	lua.getGlobal("errFunc"); 为第一句而该参数为1
				 * 	==============================
				 *  lua.getGlobal("init");
				 *  lua.getGlobal("errFunc"); 为第二句而该参数为2
				 * 	
				*/ 
//				Tools.println("lua.call("+obj.length+", "+rtnNum+"); 1 funName = " + funName +" path = " + path);
				lua.pcall(obj.length, rtnNum,1);
//				Tools.println("lua.call("+obj.length+", "+rtnNum+"); 2");
			}
			else
				lua.pcall(0, rtnNum,1);
			
			if(rtnNum > 0)
			{
				lua.setField(LuaState.LUA_GLOBALSINDEX, CLASS_NAME);
//				LuaObject luaObj = lua.getLuaObject(0);
				LuaObject luaObj = lua.getLuaObject(CLASS_NAME);
//				Tools.println("result = " + luaObj.toString());
				String result = luaObj.toString();
				if( result.indexOf("@") > 0)
					return luaObj.getObject();
				return luaObj;
			}
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();
		}
		finally
		{
//			lua.close();
		}
		return null;
	}
	
	
	public static Object luaExecute(String path  ,String funName,  Object... obj )
	{
		return LuaTools.luaExecute(path, 1, funName, obj);
	}
	
	public static int luaExecuteInt(String path , String funName)
	{
		Object result = LuaTools.luaExecute(path, funName , null);
		return (int)Double.parseDouble(result.toString());
	}
	
	
	public static int luaExecuteInt(String path ,int rtnNum, String funName,  Object... obj )
	{
		Object result = LuaTools.luaExecute(path, rtnNum, funName, obj);
		return (int)Double.parseDouble(result.toString());
	}
	
	public static int luaExecuteInt(String path , String funName,  Object... obj )
	{
		Object result = LuaTools.luaExecute(path, 1, funName, obj);
		return (int)Double.parseDouble(result.toString());
	}
	
	public static double luaExecuteDouble(String path ,int rtnNum, String funName,  Object... obj )
	{
		Object result = LuaTools.luaExecute(path, rtnNum, funName, obj);
		return Double.parseDouble(result.toString());
	}
	
	public static Object getField(LuaObject luaObj , String field )
	{
		return getField(luaObj , field , "Object");
	}
	
	public static Object getField(LuaObject luaObj , String field , String returnType )
	{
		try 
		{
			switch (returnType) 
			{
				case "int":
					return (int)Double.parseDouble(luaObj.getField(field).toString());
				case "String":
					return luaObj.getField(field).toString();
				case "double":
					return Double.parseDouble(luaObj.getField(field).toString());
				case "boolean":
					return Boolean.parseBoolean(luaObj.getField(field).toString());
				default:
					return luaObj.getField(field);
			}
			
		} catch (Exception e) 
		{
			e.printStackTrace();
			LogUtil.error(e);
		}
		return null;
	}
	
	public static void println(Object msg)
	{
		System.out.println(msg);
	}
	
	/**
	 * @param msg
	 */
	public static void luaError(Object msg) 
	{
		String info = msg.toString();
		System.err.println(info);
		try {
			throw new Exception("LUA ERROR ....");
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.error(info,e);
		}
	}
	
	public static void errPrintln(Object msg)
	{
		System.err.println(msg);
	}
	
	public static int randomNo(int min , int max)
	{
//		Tools.println("随机 min = " + min + " max = " + max);
		return Util.getRandomInt(min, max);
	}
	
	public static void main(String[] args) {
		
//		LuaTools.luaExecute(LuaTools.LUA_CALCFORMULA, 1, "runStatistics" , "2014-12-01");
//		LuaTools.luaExecute(LuaTools.LUA_CALCFORMULA, 1, "runStatistics" , Tools.getCurrentDateStr());
		int result = (int)LuaTools.luaExecute(LuaTools.LUA_CALCFORMULA, 1, "calcFighting" , 100 , 100 , 100 ,10);
		System.out.println("");
//		LuaTools.luaExecute(LuaTools.LUA_CALCFORMULA, 1, "runStatistics" , Tools.getCurrentDateStr());
		
//		System.out.println(Tools.isDateOk("2014-12-03"));
		
//		Values v = new Values();
//		v.setName("张三");
//		LuaObject luaObj = (LuaObject)LuaTools.luaExecute(LuaTools.LUA_FORMULA, "init" );
//			try {
//				System.out.println(luaObj);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		System.out.println(" v.getName() = " + v.getName());
//		
//		try {
//			Thread.sleep(5000L);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		long startTime = System.currentTimeMillis();
//		
//		for (int i = 0; i < 12; i++) 
//			LuaTools.luaExecuteInt(LuaTools.LUA_FORMULA, 1, LuaFormula.calcRankPvpRewardExpFormula);
//		
//		long endTime = System.currentTimeMillis();
//		
//		Tools.println((endTime - startTime));
	}
}



class Values
{
	private int i;
	private String name ="节操啊";
	
	public void init(){
		i = 15;
	}
	
	public void print(){
		System.out.println("Values = " + i  );
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}



