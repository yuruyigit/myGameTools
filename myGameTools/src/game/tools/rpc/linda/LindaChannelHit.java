package game.tools.rpc.linda;

import game.tools.utils.DateTools;

class LindaChannelHit 
{
	private long hitCount ;
	private transient long lastTime;
	
	public LindaChannelHit(long hitCount , long nowTime) 
	{
		this.hitCount = hitCount;
		this.lastTime = nowTime;
	}

	public long getHitCount() {
		return hitCount;
	}

	public synchronized void addHitCount(long nowTime)
	{
		this.hitCount ++;
		this.lastTime = nowTime;
	}
	
	public String getLastTime() {
		return DateTools.getCurrentTimeString(this.lastTime);
	}
	
	@Override
	public String toString() 
	{
		return "hitCount:" + hitCount;
	}
}
