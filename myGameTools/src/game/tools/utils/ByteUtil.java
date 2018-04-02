package game.tools.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import game.tools.weight.Weight;

public class ByteUtil {

	/**
	 * 将对象转化为字节流
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static byte[] objectToByte(Object object) {
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		try {
			if (object == null)
				return null;
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(object);
			byte[] bytes = bos.toByteArray();
			bos.close();
			oos.close();
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
				if (oos != null) {
					oos.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 将字节流转化为对象
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	public static Object byteToObject(byte[] bytes) throws Exception {
		if (bytes == null)
			return null;
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		Object object = null;
		try {
			bis = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bis);
			object = ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (ois != null) {
					ois.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return object;
	}
	
	

	private static Object clone(Object src)
	{
		List<Weight> dest = null;
	    try
	    {
	        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
	        ObjectOutputStream out = new ObjectOutputStream(byteOut);
	        out.writeObject(src);
	        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
	        ObjectInputStream in = new ObjectInputStream(byteIn);
	        dest = (List<Weight>) in.readObject();
	    }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    }
	    return dest;
	}
}
