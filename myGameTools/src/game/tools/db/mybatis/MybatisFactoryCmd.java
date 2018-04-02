package game.tools.db.mybatis;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class MybatisFactoryCmd<T>
{
	private Class tClass ;			//泛型类型
	
	public abstract Object doCmd(Object dbNo , T mapper);

	/**
	 * @return 反回泛型类型
	 */
	public Class<T> getTClass()
	{
		if(tClass == null)
		{
			Type genType = getClass().getGenericSuperclass();   
			Type[] params = ((ParameterizedType) genType).getActualTypeArguments();   
			tClass =  (Class)params[0];   
		}
		return tClass;
	}
}
