package game.tools.rpc.linda;

import game.tools.weight.Weight;

class Linda extends Weight
{
	private String name;
	private String ip;
	private int port;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	
}
