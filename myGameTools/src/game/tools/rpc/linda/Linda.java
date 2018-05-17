package game.tools.rpc.linda;

import game.tools.weight.Weight;

class Linda extends Weight
{
	public static final int GAP_TIME = 1 * 60 * 1000;
	
	private String name;
	private String ip;
	private int port;
	private long hit;

	synchronized void hit()
	{
		this.hit ++;
	}
	
	synchronized void reset()	{		this.hit = 0;	}
	public long getHit() {		return hit;	}
	public void setHit(long hit) {		this.hit = hit;	}
	public String getName() {		return name;	}
	public void setName(String name) {		this.name = name;	}
	public String getIp() {		return ip;	}
	public void setIp(String ip) {		this.ip = ip;	}
	public int getPort() {		return port;	}
	public void setPort(int port) {		this.port = port;	}
	public void setWeight(int weight) {	this.weight = weight;	}
}
