package game.tools.db.cache;

import java.util.Map;

public interface CacheFilter<T>
{
	public boolean filter(T t);
}
