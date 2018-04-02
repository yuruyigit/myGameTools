package game.tools.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TestSync2 implements Runnable
{
	int b = 100;
	
	synchronized void m1() throws Exception
	{
		System.err.println("m1");
		b = 1000;
		Thread.sleep(500);
		System.out.println("b = " + b);
	}
	
	synchronized void m2() throws Exception
	{
		System.err.println("m2");
		Thread.sleep(250);
		b = 2000;
	}
	
	public static void main(String [] args) throws Exception
	{
		TestSync2 tt = new TestSync2();
		Thread t = new Thread(tt);
		
		t.start();
		tt.m2();
		
		System.out.println("main thread b = " + tt.b);
		
	}

	@Override
	public void run() {
		try 
		{
			m1();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
