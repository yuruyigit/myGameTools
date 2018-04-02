package game.tools.platform;

public class Platforms 
{
	protected String platformId;
	
	public Platforms() 
	{
		
	}
	
	void setPlatformId(String platformId)
	{
		this.platformId = platformId;
	}
	
	/**
	 * @return 返回该平台的类型编号
	 */
	protected String getPlatformId()
	{
		return this.platformId;
	}
}
