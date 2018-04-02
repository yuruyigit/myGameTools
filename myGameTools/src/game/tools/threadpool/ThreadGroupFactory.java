package game.tools.threadpool;

import java.util.concurrent.ThreadFactory;

public class ThreadGroupFactory implements ThreadFactory
{
	private String threadName ;
	private long index ;
	
	public ThreadGroupFactory(String threadName) 
	{
		this.threadName = threadName + "-";
//		System.out.println("Create ThreadGroupFactory " + threadName);
	}
	
	@Override
	public Thread newThread(Runnable r) 
	{
		return new Thread(r , threadName + (index++));
	}

	
	public String getThreadName() {		return threadName;	}
	public long getIndex() {		return index;	}
}
