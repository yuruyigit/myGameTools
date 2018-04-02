package game.tools.threadpool;

public interface IWork<T> 
{
	public void work(T t);
}
