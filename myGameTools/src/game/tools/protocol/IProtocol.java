package game.tools.protocol;

import com.alibaba.fastjson.JSONObject;

public interface IProtocol<P , J> 
{
	void doProtocol(P playControl , J o);
}
