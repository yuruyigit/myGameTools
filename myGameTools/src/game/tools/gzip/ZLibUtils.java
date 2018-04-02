package game.tools.gzip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * ZLib鍘嬬缉宸ュ叿
 */
public abstract class ZLibUtils 
{
	
	public static byte[] compress(String src)
	{
		try {
			return compress(src.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 鍘嬬缉
	 * @param data 寰呭帇缂╂暟鎹�
	 * @return byte[] 鍘嬬缉鍚庣殑鏁版嵁
	 */
	public static byte[] compress(byte[] data) {
	
		byte[] output = new byte[0];

		Deflater compresser = new Deflater();

		compresser.reset();
		compresser.setInput(data);
		compresser.finish();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
		try {
			byte[] buf = new byte[1024];
			while (!compresser.finished()) {
				int i = compresser.deflate(buf);
				bos.write(buf, 0, i);
			}
			output = bos.toByteArray();
		} catch (Exception e) {
			output = data;
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		compresser.end();
		return output;
	}

	/**
	 * 鍘嬬缉
	 * @param data 寰呭帇缂╂暟鎹�
	 * @param os  杈撳嚭娴�
	 */
	public static void compress(byte[] data, OutputStream os) {
		DeflaterOutputStream dos = new DeflaterOutputStream(os);

		try {
			dos.write(data, 0, data.length);

			dos.finish();

			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 瑙ｅ帇缂�
	 * @param data 寰呭帇缂╃殑鏁版嵁
	 * @return byte[] 瑙ｅ帇缂╁悗鐨勬暟鎹�
	 */
	public static byte[] decompress(byte[] data) {
		byte[] output = new byte[0];

		Inflater decompresser = new Inflater();
		decompresser.reset();
		decompresser.setInput(data);

		ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
		try {
			byte[] buf = new byte[1024];
			while (!decompresser.finished()) {
				int i = decompresser.inflate(buf);
				o.write(buf, 0, i);
			}
			output = o.toByteArray();
		} catch (Exception e) {
			output = data;
			e.printStackTrace();
		} finally {
			try {
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		decompresser.end();
		return output;
	}

	public static String decompres(byte[] data) {
		byte [] result = decompress(data);
		try {
			return new String(result,"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 瑙ｅ帇缂�
	 * @param is  杈撳叆娴�
	 * @return byte[] 瑙ｅ帇缂╁悗鐨勬暟鎹�
	 */
	public static byte[] decompress(InputStream is) {
		InflaterInputStream iis = new InflaterInputStream(is);
		ByteArrayOutputStream o = new ByteArrayOutputStream(1024);
		try {
			int i = 1024;
			byte[] buf = new byte[i];

			while ((i = iis.read(buf, 0, i)) > 0) {
				o.write(buf, 0, i);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return o.toByteArray();
	}
	
//	public static void printCompress(byte [] data)
//	{
//		System.err.println("杈撳叆瀛楄妭闀垮害:\t" + input.length);  
//		  
//        byte[] data = ZLibUtils.compress(data);  
//        System.err.println("鍘嬬缉鍚庡瓧鑺傞暱搴�:\t" + data.length);  
//  
//        byte[] output = ZLibUtils.decompress(data);  
//        System.err.println("瑙ｅ帇缂╁悗瀛楄妭闀垮害:\t" + output.length);  
//        String outputStr = new String(output);  
//        System.err.println("杈撳嚭瀛楃涓�:\t" + outputStr);  
//        System.out.println("鍘嬬缉姣旂巼:" + (((double)data.length / output.length) * 100) + "%");
//        System.out.println("涓庢簮鏁版嵁:" + inputStr.equals(outputStr));
//	}
	public static void main(String[] args) 
	{
		System.err.println("瀛楄妭鍘嬬缉锛忚В鍘嬬缉娴嬭瘯");  
        String inputStr = "";  
        System.err.println("杈撳叆瀛楃涓�:\t" + inputStr);  
        byte[] input = null;
		try {
			input = inputStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}  
        System.err.println("杈撳叆瀛楄妭闀垮害:\t" + input.length);  
  
        byte[] data = ZLibUtils.compress(input);  
        System.err.println("鍘嬬缉鍚庡瓧鑺傞暱搴�:\t" + data.length);  
  
        byte[] output = ZLibUtils.decompress(data);  
        System.err.println("瑙ｅ帇缂╁悗瀛楄妭闀垮害:\t" + output.length);  
        String outputStr = new String(output);  
        System.err.println("杈撳嚭瀛楃涓�:\t" + outputStr);  
        System.out.println("鍘嬬缉姣旂巼:" + (((double)data.length / output.length) * 100) + "%");
        System.out.println("涓庢簮鏁版嵁:" + inputStr.equals(outputStr));
        
	}
}
