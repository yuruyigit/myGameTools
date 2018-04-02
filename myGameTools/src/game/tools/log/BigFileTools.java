package game.tools.log;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;

interface E
{
	public void execute(Object o);
}
public class BigFileTools
{
	
	
	private static final HashMap<String , E> map = new HashMap();
	
	private void t1(Object o)
	{
		System.out.println("t1" + o);
	}
	
	private void t2(Object o)
	{
		System.out.println("t2" + o);
	}
	
	private void t3(Object o)
	{
		System.out.println("t3" + o);
	}
	
	public static void main(String [] args)
	{
//		bufferedReader("charge_20171203_0631.log");
//		bufferedWriter("charge_20171203_0632.log");
//		scannerFile("charge_20171203_0631.log");
//		readBigFile("charge_20171203_0632.log");\
		
		
//		new Thread(Run)
		BigFileTools ft = new BigFileTools();
		
		map.put("t1",ft::t1);
		map.put("t2",ft::t2);
		map.put("t3",ft::t3);
		
		
		map.get("t2").execute("ssss");
//		getMemInfo();
	}
	
	
	private static long count = 0;
	private static long fileLength = 0;
	private static String secContent = "";
	private static boolean start = true;
	public static void readBigFile( String fileName) 
	{
		
		Thread t = new Thread(()->{
			long index = 0;
			while(start)
			{
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				index ++;
				
				System.out.println("Count = " + count + " / " + fileLength + " index = " + index + " secContent = " + secContent.hashCode());
				
//				secContent = null;
			}
		});
		
		t.start();
		
		try 
		{
//		  String fileName = "/Users/mc2/Desktop/youku.txt";
			RandomAccessFile randomFile = null;
			randomFile = new RandomAccessFile(fileName, "r");
			
	        fileLength = randomFile.length();
	        System.out.println("文件大小:" + fileLength);
	        
//	        int start = 0;
	        randomFile.seek(0);
	        
	        int byteread = 0;
	        
	        byte [] rn = "{\"aid\":\"44\",\"chl\":\"jingFenChl\",\"chr\":\"jingFenChr\",\"did\":\"null\",\"ga\":0,\"gc\":\"com.xygame.allstarsoccer.funding\",\"gn\":\"null\",\"ip\":\"null\",\"key\":\"628s36yk8sg8ak0\",\"mny\":499,\"oid\":\"\",\"rid\":\"39\",\"svr\":\"01-30\",\"ts\":\"1512282662\"}".getBytes();
//	        System.out.println( " rn  = " + Arrays.toString(rn));
	        byte[] bytes = new byte[212];
//	        byte[] bytes = new byte[1024];
	        
//	        int count = 0;
//	        System.out.println("String = s" + new String(new byte [] {10 }) + "ss");
	        // 一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
	        // 将一次读取的字节数赋给byteread
	        String line = null;
	        while((line = randomFile.readLine()) != null) {
//	        	System.out.println("line = " + line);
	        	secContent = line + "" + Math.random();
	        	if(line == null || line == "")
	        		break;
	        	count ++;
	        }
//	        while ((byteread = randomFile.read(bytes)) != -1) 
//	        {
////	        	randomFile.seek(start);
//	        	
//	        	//if(secContent == "")
//	        	secContent = new String(bytes);
//	        	System.out.println("secContent = " + secContent);
////	        	System.out.println(Arrays.toString(bytes));
////	            System.out.write(bytes);
//	        	
//	        	count ++;
//	        	
////	        	if( count % 1000000 == 0)
////	        		System.out.println(fileLength - count);
//	        }
	        
	        System.out.println("Read OK ！");
//	        System.out.println(bytes.length);
//	        System.out.println(new String(bytes,"UTF-8"));
	        if (randomFile != null) {
	         randomFile.close();
	        }
	        
	        start = false;
			        
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	 
	 }
	
	
	public static void getDiskInfo()
    {
        File[] disks = File.listRoots();
        for(File file : disks)
        {
            System.out.print(file.getPath() + "    ");
            System.out.print("空闲未使用 = " + file.getFreeSpace() / 1024 / 1024 + "M" + "    ");// 空闲空间
            System.out.print("已经使用 = " + file.getUsableSpace() / 1024 / 1024 + "M" + "    ");// 可用空间
            System.out.print("总容量 = " + file.getTotalSpace() / 1024 / 1024 + "M" + "    ");// 总空间
            System.out.println();
        }
    }
    
    public static void getMemInfo()
    {
//        OperatingSystemMXBean mem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
//        System.out.println("Total RAM：" + mem.getTotalPhysicalMemorySize() / 1024 / 1024 + "MB");
//        System.out.println("Available　RAM：" + mem.getFreePhysicalMemorySize() / 1024 / 1024 + "MB");
    	
    	Runtime runtime;
    	try
    	{
			runtime=Runtime.getRuntime();
			System.out.println("处理器的数目"+runtime.availableProcessors());
			System.out.println("空闲内存量："+runtime.freeMemory()/ 1024L/1024L + "M av");
			System.out.println("使用的最大内存量："+runtime.maxMemory()/ 1024L/1024L + "M av");
			System.out.println("内存总量："+runtime.totalMemory()/ 1024L/1024L + "M av");
			System.out.println("使用总量："+(runtime.totalMemory() - runtime.freeMemory())/ 1024L/1024L + "M av");
		}
    	catch(Exception e)
    	{
			e.printStackTrace();
		}
    }
	
}
