package game.tools.protocol.protobuffer;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.GeneratedMessageLite.Builder;

public class ProtocolBuffer 
{
	private int protocolNo;
	
	private GeneratedMessageLite build;
	
	private byte [] byteArray ;
	
	private String toString;
	
	public ProtocolBuffer(int protocolNo, Builder builder) 
	{
		this.protocolNo = protocolNo;
		this.build = builder.build();
		this.byteArray = this.build.toByteArray();
	}
	
	public byte[] getBytes()	{		return byteArray;}
	public int getProtocolNo() {		return protocolNo;	}
	public GeneratedMessageLite getBuild() {		return build;	}
	
	@Override
	public String toString() 
	{
		if(toString == null)
			this.toString = this.build.toString().replaceAll("\n", "|");
		return this.toString;
	}	
	
	public static byte[] subBytes(byte[] src, int begin) 
	{ 
		return subBytes(src, begin , src.length - begin);
	}
	
	public static byte[] subBytes(byte[] src, int begin, int count) 
	{  
	    byte[] bs = new byte[count];  
	    System.arraycopy(src, begin, bs, 0, count);  
	    return bs;  
	}  
}
