package game.tools.protocol.flatbuffer;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.google.flatbuffers.FlatBufferBuilder;

import game.tools.protocol.flatbuffer.entity.Create;
import game.tools.protocol.flatbuffer.entity.Login;
import game.tools.protocol.flatbuffer.entity.Protocol;
import game.tools.protocol.flatbuffer.entity.ProtocolNo;
import game.tools.protocol.flatbuffer.entity.ProtocolType;
import game.tools.protocol.flatbuffer.entity.SingleProtocol;

/**
 * @author zzb
 * <pre>
namespace game.tools.protocol.flatbuffer.entity;  

enum ProtocolNo:int
{
	Login = 0,
}

enum ProtocolType:int 
{
	Login = 0,
	Exit = 1,
}

table Login
{
	getUid:string;
	getPass:string;
	getAge:int;
}

table Protocol
{
	protocolNo:ProtocolNo;
	protocolArray:[SingleProtocol];
}

table SingleProtocol {
	type:ProtocolType;
	content:[byte];
}

root_type Protocol;



本协议的数据结构如下:
Protocol
{
	//协议号
	protocolNo 
	//type为该单个协议类的类型，如(1:Login , 2:User , 3:Role)
	//content 为该单个协议的（二进制数组)结构，用于构建以type为类型的对象，如（type==1:Login.getRootAsLogin(content), type==2:User.getRootAsUser(content)）
	[{type,content}, {type,content} ,SingleProtocol]
}

 * </pre>
 */
public class FlatBufferProtocol 
{
	
	/** 2016年8月29日上午3:02:48 是否打印协议消息内容 */
	private static boolean IS_PRINT_PROTOCOL_MSG = true;
	/**
	 * @param ProtocolType 数组协议类型(这里为定义的枚举类型)
	 * @param objOffset 对应数据结构的下标浮值（也就是创建的一个flatbuffer的返回下标浮值）
	 * @return 返回一个SingleProtocol的数据据结构包
	 */
	private static final int [] getSignleProtocol(int protocolType , int objOffset)
	{
		return new int [] { protocolType , objOffset };
	}
	
	public static void main(String[] args) 
	{
		byte [] data = FlatBufferProtocol.getFlatBufferPkg
		(
				ProtocolNo.Login,
				
				(builder) ->  { return getSignleProtocol(ProtocolType.Login, Login.createLogin(builder, builder.createString("testuuiD"), builder.createString("testPasss"), 11));} ,
				(builder) ->  { return getSignleProtocol(ProtocolType.Create , Create.createCreate(builder, 11, 2, builder.createString("tokentString"), 77, 55, 66));} 
		);
		
		
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		FlatBufferProtocolObject protocolObject = new FlatBufferProtocolObject()
		{
			
			@Override
			public <T> T getProtocolObject(int type, ByteBuffer data) 
			{
				switch (type) 
				{
					case ProtocolType.Login:
						return (T)Login.getRootAsLogin(data);
					case ProtocolType.Create:
						return (T)Create.getRootAsCreate(data);
					default:
						break;
				}
				
				return  null;
			}
		};
		
		FlatBufferProtocol.closePrintProtocolMsg();
		
		Protocol revProtocol = getProtocolByByteArray(data , protocolObject);			//获取接收到的protocol对象
		SingleProtocol msgP = revProtocol.protocolArray(1);
		System.out.println(msgP.type() + " " + Arrays.toString(msgP.contentAsByteBuffer().array()));
		System.out.println("protocolNo = " +  revProtocol.protocolNo() + " protocolArrayLength = " +  revProtocol.protocolArrayLength());
		

//		test2();
	}
	
	/**
	 * @param protocolNo 处理协议号
	 * @param pkgArray 传入这个SingleProtocol数组，用来构建完整的Protocol协议结构
	 * @return 返回一个flatBuffer的数据包
	 */
	public static byte [] getFlatBufferPkg(int protocolNo , SignleProtocolPkg ...pkgArray)
	{
		FlatBufferBuilder builder = new FlatBufferBuilder(1);
		
		int protocol = -1;
		
		if(pkgArray != null)
		{
			int [] singleProtocolArray = new int[pkgArray.length];
			
			for (int i = 0; i < pkgArray.length; i++)
			{
				SignleProtocolPkg pkg = pkgArray[i];
				
				int [] retArray =  pkg.create(builder);
				
				int protocolType = retArray[0];
				int pkgObj = retArray[1];
				
				builder.finish(pkgObj);
				
				byte [] byteArray = builder.sizedByteArray();
				
				singleProtocolArray[i] = SingleProtocol.createSingleProtocol(builder, protocolType, SingleProtocol.createContentVector(builder, byteArray));
			}
			
			int protocolArr = Protocol.createProtocolArrayVector(builder, singleProtocolArray);
			
			protocol = Protocol.createProtocol(builder, protocolNo, protocolArr);
		}
		else
			protocol = Protocol.createProtocol(builder, protocolNo, 0);
		
		builder.finish(protocol);
		
		byte [] protocolByteArray = builder.sizedByteArray();
		
		return protocolByteArray;
	}
	
	
	
	/**
	 * 通过byte[]数组获取一个protocol的对象
	 * @param protocolByteArray 传入的byte数组
	 * @param protocolObject  要获取对应协议的对象执行函数
	 * @return
	 */ 
	public static Protocol getProtocolByByteArray(byte [] protocolByteArray, FlatBufferProtocolObject protocolObject)
	{
		ByteBuffer buf = ByteBuffer.wrap(protocolByteArray);
		
		Protocol p = Protocol.getRootAsProtocol(buf);
		
		printProtocolMsg(p , protocolObject);
		
		return p;
	}
	
	
	/**
	 * 打印协议信息内容
	 * @param protocolObject 
	 * @param protocolByteArray
	 */
	private static void printProtocolMsg(Protocol p, FlatBufferProtocolObject protocolObject )
	{
		if(!FlatBufferProtocol.IS_PRINT_PROTOCOL_MSG)			//是否打印协议内容
			return ;
		
		for (int i = 0; i < p.protocolArrayLength(); i++) 
		{
			SingleProtocol sp = p.protocolArray(i);
			
			Object protocolObj = protocolObject.getProtocolObject(sp.type(), sp.contentAsByteBuffer());
			
			printProtocolAttribute(protocolObj);
		}
	}
	
	/**
	 * 打印协议消息属性
	 * @param protocolObj
	 */
	private static void printProtocolAttribute(Object protocolObj)
	{
		if(protocolObj == null)
			return;
		
		try 
		{
			StringBuilder sb = new StringBuilder("{");
			
			Method [] methodArray = protocolObj.getClass().getDeclaredMethods();
			
			for (int i = 2; i < methodArray.length; i++) 
			{
				Method method = methodArray[i];
				String methodName = method.getName();
				
				if(isDebarMethod(methodName))
					continue;
				
				Object retValue = method.invoke(protocolObj);
				if(sb.length() > 1)
					sb.append(",");
				if(method.getReturnType().getSimpleName().equals("String"))
					sb.append("\"").append(methodName).append("\":\"").append(retValue).append("\"");
				else
					sb.append("\"").append(methodName).append("\":").append(retValue);
			}
			sb.append("}");
			
			System.out.println(sb.toString());
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	
	/** 2016年8月29日上午2:57:55  需要排除的方法前缀*/
	private static final String [] DEBAR_METHOD = {"getRootAs" , "AsByteBuffer" , "create" , "__init" , "add" , "start" , "end"};
	
	/**
	 * @param methodName
	 * @return 是否排除的方法
	 */
	private static boolean isDebarMethod(String methodName)
	{
		for (int i = 0; i < DEBAR_METHOD.length; i++) 
		{
			if(methodName.indexOf(DEBAR_METHOD[i]) >= 0)
				return true;
		}
		return false;
	}
	
	/**
	 *  打开打印协议内容信息
	 */
	public static void openPrintProtocolMsg()
	{
		FlatBufferProtocol.IS_PRINT_PROTOCOL_MSG = true;
	}
	
	/**
	 *  关闭打印协议内容信息
	 */
	public static void closePrintProtocolMsg()
	{
		FlatBufferProtocol.IS_PRINT_PROTOCOL_MSG = false;
	}
	
	private static byte [] getFlatBufferPkg1(SignleProtocolPkg ...pkgArray)
	{
		FlatBufferBuilder builder = new FlatBufferBuilder(1);
		
		int [] singleProtocolArray = new int[pkgArray.length];
		
		for (int i = 0; i < pkgArray.length; i++)
		{
			SignleProtocolPkg pkg = pkgArray[i];
			
			int [] retArray =  pkg.create(builder);
			
			int protocolType = retArray[0];
			int pkgObj = retArray[1];
			
			builder.finish(pkgObj);
			
			byte [] byteArray = builder.sizedByteArray();
			
			singleProtocolArray[i] = SingleProtocol.createSingleProtocol(builder, protocolType, SingleProtocol.createContentVector(builder, byteArray));
			
		}
		
		int protocolArr = Protocol.createProtocolArrayVector(builder, singleProtocolArray);
		
		int protocol = Protocol.createProtocol(builder, ProtocolNo.Login, protocolArr);
		
		builder.finish(protocol);
		
		System.out.println("/////////////////////////////////////////");
		
		
		byte [] protocolByteArray = builder.sizedByteArray();
		ByteBuffer buf = ByteBuffer.wrap(protocolByteArray);
//		ByteBuffer buf = builder.dataBuffer();

		Protocol p1 = Protocol.getRootAsProtocol(buf);
		
		System.out.println("p1.protocolArrayLength() = " + p1.protocolArrayLength());
		
		for (int i = 0; i < p1.protocolArrayLength(); i++) 
		{
			SingleProtocol sp = p1.protocolArray(i);
			
			System.out.println(sp.type() + "  " + Login.getRootAsLogin(sp.contentAsByteBuffer()).pass());
		}
		
		return  builder.sizedByteArray();
	}
	
	
	private static void test2()
	{
		FlatBufferBuilder builder = new FlatBufferBuilder(1);
		
		int loginObj = Login.createLogin(builder, builder.createString("uid1"), builder.createString("pass11111") ,111);
		builder.finish(loginObj);
		byte [] loginByteArr = builder.sizedByteArray();
//		byte [] loginByteArr = builder.sizedByteArray(builder.dataBuffer().position(), builder.offset());
		
		int loginObj1 = Login.createLogin(builder, builder.createString("uid"), builder.createString("pass") , 123);
		builder.finish(loginObj1);
		byte [] loginByteArr1 = builder.sizedByteArray();
//		byte [] loginByteArr1 = builder.sizedByteArray(builder.dataBuffer().position(), builder.offset());
		
		int protocolArr = Protocol.createProtocolArrayVector(builder, new int[]
		{
			SingleProtocol.createSingleProtocol(builder, ProtocolType.Login, SingleProtocol.createContentVector(builder, loginByteArr)),
			SingleProtocol.createSingleProtocol(builder, ProtocolType.Exit,  SingleProtocol.createContentVector(builder, loginByteArr1)),
		});
		
		int protocol = Protocol.createProtocol(builder,  ProtocolNo.Login,protocolArr);
		
		builder.finish(protocol);
		
		System.out.println("/////////////////////////////////////////");
		
		byte [] protocolByteArray = builder.sizedByteArray();
		ByteBuffer buf = ByteBuffer.wrap(protocolByteArray);
//		ByteBuffer buf = builder.dataBuffer();

		Protocol p1 = Protocol.getRootAsProtocol(buf);
		
		for (int i = 0; i < p1.protocolArrayLength(); i++) 
		{
			SingleProtocol sp = p1.protocolArray(i);
			Login l = Login.getRootAsLogin(sp.contentAsByteBuffer());
			l.pass();
			System.out.println(sp.type() + "  " + l.pass());
		}
	}
	
	private static void test1()
	{
		
		FlatBufferBuilder builder = new FlatBufferBuilder(1);
//		int pbyte = SingleProtocol.createContentVector(builder, ObjectToByte(p));
		
		int loginObj = Login.createLogin(builder, builder.createString("uid"), builder.createString("pass") , 123);
		builder.finish(loginObj);
		
		byte [] loginByteArr = builder.sizedByteArray(builder.dataBuffer().position(), builder.offset());
		byte [] loginByte = Arrays.copyOf(loginByteArr, loginByteArr.length);
		

		ByteBuffer bb = ByteBuffer.wrap(loginByteArr);
		
//		ByteBuffer bbbbb = ByteBuffer.wrap(loginByteArr);
		
//		FlatBufferBuilder fb = new FlatBufferBuilder(bb);
		
		Login l = Login.getRootAsLogin(bb);
		System.out.println(l.age() + " " + l.uid());
		
		System.out.println("loginByteArr" + Arrays.toString(builder.dataBuffer().array()) + " len = " + loginByte.length);
	}
}
