package game.tools.log;

import com.alibaba.fastjson.JSONObject;

public class LogUtil 
{

	/** zzb 2014年12月5日下午12:06:22 错误 ， 信息，日志文件对象 */
	private static LogFile ERR_LF = new LogFile("err" , 30),
	INFO_LF = new LogFile("info" , 30),
	DEBUG_LF = new LogFile("debug" , 30),
	PROTOCOL_LF = new LogFile("protocol" , 30);
	
	/**
	 * 读取文件内容
	 * @param file
	 */
	public static String readFile(String fileName) 
	{

//		LogFile readLf = new LogFile(fileName, LogFile.EXEC_READ);
//		return readLf.readFile();
		return "";

	}

	/**
	 * 打印异常错误信息 2013-1-15 上午10:09:04
	 * @param e 传入的异常对象 , 执行结果将会把异常信息写入到err.txt里面
	 */
	public static void error(Exception e) 
	{
		ERR_LF.writeException(null ,e);
	}

	/**
	 * 打印异常错误信息 2013-1-15 上午10:09:04
	 * @param playControl 传入角色控制对象,主要获取哪个角色ID
	 * @param e 传入的异常对象 , 执行结果将会把异常信息写入到err.txt里面
	 */
	public static void error(Object o , Exception e) {
		ERR_LF.writeException(o,e);
	}
	public static void error(Exception e , Object o ) {
		ERR_LF.writeException(o,e);
	}
	
	public static void info(Object content)
	{
		INFO_LF.writeFile(content);
	}
	
	public static void protocol(Object content)
	{
		PROTOCOL_LF.writeFile(content);
	}
	
	public static void debug(String content) 
	{
		DEBUG_LF.writeFile(content);
	}
	
	public static void main(String[] args)
	{
		String content = "搬血：需调动全身精血，滚滚如雷鸣，熔炼骨文，在血液中催发出神曦，从而淬炼天地造化，滋养肉身。最高可使肉身达至十万斤极境。洞天："
				+ "开辟洞天。等于是夺了天地造化，不断直接吸收外界神精，补充己身。最多可有十大洞天，石昊将十大洞天合一化为唯一无上洞天，是遮天轮海境的雏形。疑为演化或修补仙域的重要手段之一。"
				+ "化灵：重塑真我的过程，与以往不同的蜕变，从肉身到精神，再到沟通外界的洞天，产生灵性，都将演变。肉身最高境界肉身成灵。铭纹：不再彻底借鉴凶兽、猛禽的符文，可以在体内铭刻自己的一些符号。"
				+ "列阵（尊者）：若说铭纹境是模仿其他种族，在体内刻下符文，并可以初步推演法，那么列阵就是更高层次的进化。可在体内刻下各种杀阵，甚至是先天混沌大阵，如曹雨生体内的第三杀阵。"
				+ "神火（伪神）：点燃神火，超脱凡人，亦可在体内种下道种。晚年时神火可能熄灭。真一（真神）：很多人都是通过在神火境种下道种，才蜕变而成，拥有超脱世俗的力量。真正点燃神火，神火死后才会熄灭。"
				+ "圣祭：特殊的过渡境界，前期法力不稳定，在神火境和天神境之间变动，修炼至后期则较为稳定。此境界可直接跳过不修炼，但如果修炼，日后将顺风顺水。此境界可度神王劫。"
				+ "天神：可断肢再生，一滴血即可崩塌山脉，天神威压可盖一切低境界修士。进入帝关战场的最低资格便是成为天神。"
				+ "虚道（教主）：与道交融，以完美的种子为媒介，触摸大道，可感悟天地间的妙理，第一次跟道全面接触，甚至给人以错觉，宛若化身成了天地大道。（注：虚道境是被称为教主的下限，其上境界皆可称之为教主。）"
				+ "斩我：全称斩我明道境，斩掉己身，明悟自己的道与法。遁一：统领一方的大修士，帝关战场上的首领级人物。至尊（无敌者）：为九天十地现有最高战力，抬手间摘星捉月。实力远超遁一境，仅是至尊威压便令低境界"
				+ "修士抬不起头，依靠长生物质可活几百万年而不死。真仙（不朽者、长生者）:已是大道领域的存在，实力远超人道，依靠长生物质可长生不死。仙古纪元被称为真仙的纪元。在仙战中完整的大界被打残，化作九天十地，"
				+ "仙域与九天十地通道也被关闭。天地法则残缺不全，导真致乱古纪元九天十地再无人可以成仙。";
		
//		for (int i = 0; i < 1; i++)
//		{
//			info(content);
//			error(i , new Exception("i = " + i ) );
//			
//			System.out.println("i = " + i);
//		}
		
		JSONObject o = new JSONObject();
		o.put("asdf", content);
		System.out.println(o.toJSONString());
		
		error(1111 , new Exception("asdfasdfasdfsdfasdf") );
		LogUtil.info("asdfasdf");
	}

}
