package game.tools.db.mybatis;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface IMybatisFactoryCmd<T>

{
	public abstract Object doCmd(long dbNo , T mapper);

	/**
	 * @return 反回泛型类型
	 */
	public default Class<T> getTClass()
	{
		Type genType = getClass().getGenericSuperclass();   
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();   
		return (Class)params[0];   
	}
}
