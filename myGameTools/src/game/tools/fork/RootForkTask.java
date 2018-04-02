package game.tools.fork;
import java.util.concurrent.RecursiveTask;
import game.tools.log.LogUtil;

class RootForkTask extends RecursiveTask<Object>
{
	private static final long serialVersionUID = 1L;
	
	private SubForkTask [] subForkTaskArray;
	
	private Object [] resultArray;
	
	void addTasks(SubForkTask ...subTasks)
	{
		subForkTaskArray = subTasks;
		resultArray = new Object[subTasks.length];
	}
	
	@Override
	protected Object compute()
	{
		try
		{
			for (SubForkTask subTask : subForkTaskArray) 
				subTask.fork();
			
			int index = 0;
			for (SubForkTask subTask : subForkTaskArray) 
				resultArray[index++] = subTask.get(); 
			
			return resultArray;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LogUtil.error(e);
		}
		
		return null;
	}

	public void clearTask()
	{
		this.subForkTaskArray = null;
		this.resultArray = null;
	}
}