package game.tools.hotswap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.util.Date;

public class App {
	public static void main(String[] args) throws Exception {
		Hello hello = new Hello();
		hello.sayHello();

		System.gc();

		fileChannelCopy(new File("D:\\WorkSpace\\EclipseSpace\\GameSourceCode\\Doraemon\\gameTools\\Hello.class") ,
				new File("D:\\WorkSpace\\EclipseSpace\\GameSourceCode\\Doraemon\\"
				+ "gameTools\\bin\\game\\tools\\hotswap\\Hello.class"));

		MyClzssLoader mcl = new MyClzssLoader();
		Class clzss = mcl.loadClass("game.tools.hotswap.Hello");

		Object o = clzss.newInstance();
		Method m = clzss.getMethod("sayHello");
		m.invoke(o);
		// hello.sayHello();
	}

	public static long forJava(File f1, File f2) throws Exception {
		long time = new Date().getTime();
		int length = 2097152;
		FileInputStream in = new FileInputStream(f1);
		FileOutputStream out = new FileOutputStream(f2);
		byte[] buffer = new byte[length];
		while (true) {
			int ins = in.read(buffer);
			if (ins == -1) {
				in.close();
				out.flush();
				out.close();
				return new Date().getTime() - time;
			} else
				out.write(buffer, 0, ins);
		}
	}

	public static void fileChannelCopy(File s, File t) {

		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;

		try 
		{
			fi = new FileInputStream(s);
			fo = new FileOutputStream(t);
			in = fi.getChannel();// 得到对应的文件通道
			out = fo.getChannel();// 得到对应的文件通道
			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				fi.close();
				in.close();
				fo.close();
				out.close();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}

	}
}
