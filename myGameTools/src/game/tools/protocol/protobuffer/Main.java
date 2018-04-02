package game.tools.protocol.protobuffer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import game.tools.protocol.protobuffer.Protocol.BaseDataType;
import game.tools.protocol.protobuffer.Protocol.Login;

public class Main 
{
	public static void main(String[] args) 
	{
		t();
	}
	
	private static void t2()
	{
		Protocol.BaseDataType.Builder builder = Protocol.BaseDataType.newBuilder();
		builder.setA(1);
		builder.setB(1.1f);
		builder.setC("c");
		builder.setD(1.258545);
		builder.setE(false);
		builder.setF(45864894435L);
		builder.setG(ByteString.copyFrom(new byte[] {1,2}));
		
		BaseDataType bdt = builder.build();
		
		Protocol.ComplexDataType.Builder builder1 = Protocol.ComplexDataType.newBuilder();
		
	}
	
	private static void t()
	{
		Protocol.Login.Builder builder = Protocol.Login.newBuilder();
		builder.setChannl("channel");
		builder.setPlaform("plaform");
		builder.setUserId("asdfasdfsadf");
		
		Login login = builder.build();
		
		System.out.println("===========Login Byte==========");
        for(byte b : login.toByteArray()){
            System.out.print(b);
        }
        System.out.println();
        System.out.println(login.toByteString());
        System.out.println("================================");

        //模拟接收Byte[]，反序列化成Person类
        byte[] byteArray =login.toByteArray();
        Login p2 = null;
		try {
			p2 = Login.parseFrom(byteArray);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}

        System.out.println("start");
        System.out.println("after :" +p2.toString());
        System.out.println("end");
        
	}	
}
