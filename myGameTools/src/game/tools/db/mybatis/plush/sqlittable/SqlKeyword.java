package game.tools.db.mybatis.plush.sqlittable;

class SqlKeyword 
{
	private static final String EMPTY = " " , WRAP = "\n" ,  TAB = "\t";
    private static final String[] VALUES = 
    {
    	"SELECT", "FROM", "WHERE", "ORDER BY", "LIMIT", "UNION ALL",
    	"INSERT"
    };

	public static final String SELECT ,FROM, WHERE, ORDER_BY , LIMIT , UNION_ALL;
	
	public static final String INSERT; 
	
	static
	{
		SELECT = VALUES[0];
		FROM = VALUES[1];
		WHERE = VALUES[2];
		ORDER_BY = VALUES[3];
		LIMIT = VALUES[4];
		UNION_ALL = VALUES[5];
		
		
		INSERT  = VALUES[6];
	}
	
    public static boolean isKeyword(String keyword)
	{
    	String lowerKeyWord = keyword.toLowerCase();
		String upperKeyWord = keyword.toUpperCase();
		
    	for (String key: VALUES) 
    	{
    		if(key.equals(lowerKeyWord) || key.equals(upperKeyWord))
    			return true;
		}
		return false;
	}
    
    public static Object [] getKeyword(String sql)
   	{
       	String lowerKeyWord = sql.toLowerCase();
   		String upperKeyWord = sql.toUpperCase();
   		
       	for (String keyObj: VALUES) 
       	{
      		String key = keyObj;
       		if(!keyObj.equals(VALUES[0]))
       			key = EMPTY + keyObj;
       		int index = lowerKeyWord.lastIndexOf(key);
       		if(index < 0)
       			index = upperKeyWord.lastIndexOf(key);
       		
       		if(index < 0 )
       		{
       			if(!keyObj.equals(VALUES[0]))
       				key = WRAP + keyObj;
       			
       			index = lowerKeyWord.lastIndexOf(key);
       			if(index < 0)
       				index = upperKeyWord.lastIndexOf(key);
       		}
       		
       		if(index < 0 )
       		{
       			if(!keyObj.equals(VALUES[0]))
       				key = WRAP + keyObj;
       			
       			index = lowerKeyWord.lastIndexOf(key);
       			if(index < 0)
       				index = upperKeyWord.lastIndexOf(key);
       		}
       		
       		
       		
       		if(index < 0 )
       		{
       			if(!keyObj.equals(VALUES[0]))
       				key = TAB + keyObj;
       			
       			index = lowerKeyWord.lastIndexOf(key);
       			if(index < 0)
       				index = upperKeyWord.lastIndexOf(key);
       		}
       		
       		if(index >= 0 )
       			return new Object [] {key , index};
   		}
   		return null;
   	}
    
    
    static String [] getSplit(String sql  , String splitStr)
   	{
       	String lowerSqlit = splitStr.toLowerCase();
   		String upperSqlit = splitStr.toUpperCase();
   		
   		String [] split = sql.split(lowerSqlit);
   		if(split == null)
   			split = sql.split(upperSqlit);
   		
   		return split;
   	}
    
    public static int getIndexOf(String sql , String keyword)
    {
    	String lowerKeyWord = keyword.toLowerCase();
   		String upperKeyWord = keyword.toUpperCase();
   		
   		int index = sql.indexOf(lowerKeyWord); 
   		if(index < 0 )
   			index = sql.indexOf(upperKeyWord);
   			
   		return index;
    }
}