package game.tools.fork;
import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;
import game.tools.log.LogUtil;

class RootForkTask extends RecursiveTask<Object>
{
	private static final long serialVersionUID = 1L;
	
	private ArrayList<SubForkTask> subForkTaskList;
	
	private ArrayList<Object> resultList;
	
	
	synchronized void addTasks(SubForkTask ...subTasks)
	{
		if(this.subForkTaskList == null)
		{
			subForkTaskList = new ArrayList<SubForkTask>(subTasks.length);
			resultList = new ArrayList<Object>(subTasks.length);
		}
		
		for (SubForkTask subForkTask : subTasks) 
			this.subForkTaskList.add(subForkTask);
	}
	
	@Override
	protected Object compute()
	{
		try
		{
			for (SubForkTask subTask : subForkTaskList) 
				subTask.fork();
			
			for (SubForkTask subTask : subForkTaskList) 
				resultList.add(subTask.get()); 
			
			return resultList.toArray();
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
		this.subForkTaskList.clear();
		this.resultList.clear();
	}
}