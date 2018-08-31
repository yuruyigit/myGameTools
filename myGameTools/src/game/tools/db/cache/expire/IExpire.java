package game.tools.db.cache.expire;

public interface IExpire<V>
{
	public void expire(V v);
}
