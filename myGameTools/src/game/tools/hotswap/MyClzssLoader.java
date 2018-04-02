package game.tools.hotswap;

import java.io.InputStream;

public class MyClzssLoader extends ClassLoader 
{
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException 
	{
		String fileName = name.substring(name.lastIndexOf(".") + 1 )+".class";
		
		InputStream is = this.getClass().getResourceAsStream(fileName);
		
		try 
		{
			byte [] b = new byte[is.available()];
			is.read(b);
			
			return defineClass(name, b, 0, b.length);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return super.findClass(name);
	}
}