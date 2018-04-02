package game.tools.properties;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import game.tools.log.LogUtil;

public class OperProperties
{
	private String path ;
	private HashMap<String ,String> kvMap = new HashMap<>();
	
	public OperProperties(String path) 
	{
		this.path = path;
	}
	
	public void setProperties(String key ,String value)
	{
		kvMap.put(key, value);
	}
	
	public void flush()
	{
		try 
		{
			StringBuilder sb = new StringBuilder();
			File f = new File(this.path);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"));
			
			while(br.ready())
			{
				String line = br.readLine();
				if(line == null)
				{
					break;
				}
				else if(line.equals("") || line.indexOf("#") >=0 )
				{
					sb.append(line+"\r\n");
					continue;
				}
				
				String [] lineArryay = line.split("=");
				if(lineArryay.length >= 1)
				{
					String inKey = lineArryay[0].trim();
					String value = kvMap.remove(inKey);
					if(value != null)
					{
						line = inKey+"="+value;
						System.out.println("set " + inKey + " = " + value);
					}
				}
				
				sb.append(line+"\r\n");
				
//				if(kvEmpty())			//是否设置完了
//					break;
			}
			
			if(sb.length() > 0)
			{
				br.close();
				
				BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f , false), "utf-8"));
				fw.write(sb.toString());
				fw.flush();
				fw.close();
			}
			
		}
		catch (Exception e) 
		{
			LogUtil.error(e);
			e.printStackTrace();
		}
	}
	
	private boolean kvEmpty()
	{
		return kvMap.size() <= 0;
	}
	
	public static void main(String[] args) 
	{
		OperProperties op = new OperProperties("conf/login.properties");
		op.setProperties("k", "v");
		op.flush();
	}
}
