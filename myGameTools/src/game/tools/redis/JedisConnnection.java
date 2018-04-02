package game.tools.redis;

/**
 * @author zhibing.zhou
 * reids  连接信息
 */
public class JedisConnnection 
{
	 private String host  , pass;
	 private int port , timeout = 10000 , weight = 1 ;
	 
	 public JedisConnnection(String host , int port , String pass , int timeout , int weight) 
	 {
		 this.host = host;
		 this.port = port;
		 this.pass = pass;
		 this.timeout = timeout;
		 if(weight > 0)
			 this.weight = weight;
	 }
	 
	 public JedisConnnection(String host , int port , String pass , int timeout ) 
	 {
		 this.host = host;
		 this.port = port;
		 this.pass = pass;
		 this.timeout = timeout;
	 }

	 public JedisConnnection(String host , int port , String pass ) 
	 {
		 this.host = host;
		 this.port = port;
		 this.pass = pass;
	 }
	 
	 
	 public JedisConnnection(String host , int port ) 
	 {
		 this.host = host;
		 this.port = port;
	 }

	public String getHost() {
		return host;
	}

	public String getPass() {
		return pass;
	}

	public int getPort() {
		return port;
	}

	public int getTimeout() {
		return timeout;
	}

	public int getWeight() {
		return weight;
	}
	
	@Override
	public String toString() {
		return host+":"+port+"["+weight+"]";
	}
}
