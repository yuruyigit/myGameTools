package game.tools.rpc.linda;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * @author zhouzhibin
 * <b>请注意：该注解请使用静态函数来绑协议。</b>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LindaRpcNo {
	
	public int rpcNo() default 0;
}
