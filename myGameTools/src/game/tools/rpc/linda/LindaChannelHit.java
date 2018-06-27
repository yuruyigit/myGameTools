package game.tools.rpc.linda;

import game.tools.utils.DateTools;

class LindaChannelHit 
{
	private long hitCount ;
	private transient long lastTime;
	
	public LindaChannelHit(long hitCount) 
	{
		this.hitCount = hitCount;
	}

	public long getHitCount() {
		return hitCount;
	}

	public synchronized void addHitCount()
	{
		this.hitCount ++;
		this.lastTime = System.currentTimeMillis();;
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
