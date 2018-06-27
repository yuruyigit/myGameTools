package game.tools.log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import game.tools.utils.DateTools;

class LogFile 
{
	private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]*");
	
	private static final String WRAP = "\n";
	private static final String TAB = "\t" ;
	private static final String MSG_ENCODE = "utf-8";
	
	/** 2016年9月27日下午3:22:58 log的根目录  */
	private static final String LOG_ROOT_PATH = "logs/";
	/** 2016年9月27日下午3:23:06 log的后缀名 */
	private static final String LOG_SUFFIX = ".log";
	
	
	/*** zzb  分割文件的大小 单MB*/
	private static final int ALLOC_SIZE = 300;
//	private static final int ALLOC_SIZE = 3;  
	
	/** 2016年4月9日下午10:40:02 当前文件段索引 */ 
	private int index;
	/** 2016年4月14日上午12:40:04 文件名字*/
	private String fileName;
	/** 2016年9月27日下午3:29:47 文件路径 */
	private String filePath;
	/** 2016年9月27日下午3:41:56 记录上次的时间名称 */
	private String oldTimeName;
	/** 2016年9月27日下午2:26:12  删除log日期 的时间范围 (0 为不删除) , 初始为不删除 */
	private int deleteDateNum = 0;
	/** 是否使用缓存列表写入数据*/
	private boolean isCache;
	
	private File file ;
	
	private BufferedWriter bufferWriter;
	
	private StringBuilder stringBuffer = new StringBuilder();
	
	
	public LogFile(String fileName , int deleteDateNum , boolean isCache) 
	{
		this.fileName = fileName;
		this.deleteDateNum = deleteDateNum;
//		this.isCache = isCache;
//		if(isCache)
//			writerList = new ArrayList<StringBuilder>(50);
	}
	
	public LogFile(String fileName) 	{		this(fileName , 0 , false);	}	
	public LogFile(String fileName , boolean isCache) 	{		this(fileName , 0 , isCache);	}
	public LogFile(String fileName , int deleteDateNum)	{		this(fileName , deleteDateNum , false);	}
	
	
	/**
	 *  创建对应的文件
	 */
	private void createFile() 
	{
		clearStringBuffer();
		createDefaultLogPath();
		createFileSize();
	}

	private boolean createFileSize() 
	{
		File dir = new File(this.filePath);
		if(!dir.exists())				//log下的目录是否存,如果不存在则对创建对应的log
		{
			dir.mkdirs();
			createAllocFile();
		}
		
		if (isNextDay()) 		//创建隔天文件
		{
			createAllocFile();
			
			//定时删除固定日期前的log
			taskDeleteLog();
		}
		
		if(isMaxAllocSize())			//大于指定的容量大小
			createAllocFile();
			
		if(bufferWriter == null)
			createAllocFile();
		
		return true;
	}
	
	/**
	 * 定时删除log
	 */
	private void taskDeleteLog()
	{
		if(deleteDateNum == 0)
			return ;
		
		int firstDate=Integer.parseInt(DateTools.getBeforeDay(new Date(),-deleteDateNum).replaceAll("-", ""));
		String pathDir = this.filePath + "../";
		
		File file = new File(pathDir);
		
		if (file.isDirectory()) 
		{
			File[] logs = file.listFiles();	// 获取文件夹中的文件集合
			
			for (int i = 0; i < logs.length; i++)	// 遍历集合 
			{
				File log = logs[i];	// 获取第i个文件
				String dateNum=log.getName().replaceAll("-", "");
				if(!isNumeric(dateNum))
					continue;
				int logInt = Integer.parseInt(dateNum.substring(0, 8));	// 获取第i个文件的名称
				if (logInt <= firstDate ) // 判断时候在时间内
					deleteFile(log);// 执行删除方法
			}
		}
	}
	
	public boolean isNumeric(String str) 
	{
		Matcher isNum = NUMBER_PATTERN.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
	
	private void deleteFile(File file) 
	{
		if (file.exists())// 判断文件是否存在 
		{
			if (file.isFile())	// 判断是否是文件
			{
				file.delete();// 删除文件
			}
			else if (file.isDirectory())	// 否则如果它是一个目录 
			{
				File[] files = file.listFiles();// 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++)	// 遍历目录下所有的文件 
					this.deleteFile(files[i]);	// 把每个文件用这个方法进行迭代
				
				file.delete();// 删除文件夹
			}
		}
	}
	
	private boolean isMaxAllocSize()
	{
		if(file != null && file.length() >= getAllocSize())			//大于指定的容量大小
			return true;
		
		return false;
	}
	
	public BufferedReader createBufferedRead(File file) throws Exception
	{
		return new BufferedReader(new InputStreamReader(new FileInputStream(file),MSG_ENCODE));
	}
	
	
	/**
	 * zzb  创建分割文件
	 */
	private synchronized void createAllocFile()
	{
		String fileDateName= fileName + "_" + getDateString();
		File maxF = null;
		int maxNo = 0;
		
		File f = new File(filePath);
		File [] listArr =  f.listFiles();
		for (File ff :listArr) 
		{
			if (ff.getName().contains(fileDateName)) 
			{
				if(ff.getName().contains("#"))
				{
					String name = ff.getName().split("#")[1];
					int no = Integer.parseInt(name.substring(0, name.lastIndexOf(".")));
					if(maxNo < no)
					{
						maxNo = no;
						maxF = ff;
					}
				}
			}
		}
		
		if(maxNo == 0 )
			maxNo = 1;
		
		if(maxF != null && maxF.length() >= getAllocSize())
			maxNo ++;
		
		try 
		{
			index = maxNo;
			f = createAppointNumberFile(index);
			
			this.file = f;
			this.bufferWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f , true), MSG_ENCODE));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}		
	}
	
	
	/**
	 * @return  返回一个没有后缀的日期名	 
	 * */
	private String getFileNameNoSuffix(String name)
	{
		return fileName + "_" + getDateString();
	}
	
	/**
	 * 创建指定编号文件
	 * @param number
	 */
	private File createAppointNumberFile(int number)
	{
		String name = getFileNameNoSuffix(fileName) +"#" + number + LOG_SUFFIX;
		String path = filePath + name;
		
		return new File(path);
	}
	
	
	/**	 
	 *  @return 返回分割的大小，单位b	 
	 * */
	private static long getAllocSize()
	{	
		return ALLOC_SIZE * 1024 * 1024;	
	}
	
	
	/**
	 * @return 返回是否是隔天
	 */
	private boolean isNextDay()
	{
		String dateString = getDateString();
		
		if (oldTimeName == null || !oldTimeName.equals(dateString)) 		//创建隔天文件
		{
			oldTimeName = dateString;
			return true;
		}
		
		return false;
	}
	
	/**
	 * @return 返回根据日期的默认log目录地址
	 */
	private String getDefaultPath()		
	{
		this.filePath = LOG_ROOT_PATH + fileName + "/" + getDateString()+  "/";
		return this.filePath;		
	}
	
	/**
	 * @return 返回以日期显示的字符串
	 */
	private String getDateString()
	{
//		date.setTime(System.currentTimeMillis());		
//		return DATE_FORMAT.format(date);
		
		return DateTools.getCurrentDateString(System.currentTimeMillis());
	}
	
	/** 
	 * @return 返回当前时间的字符串
	 */
	private String getNowTimeString()
	{
//		date.setTime(System.currentTimeMillis());		
//		return SDF.format(date);
		
		return DateTools.getCurrentTimeString(System.currentTimeMillis());
	}
	
	
	/**
	 * 清空stringBuffer的内容
	 */
	private void clearStringBuffer() 
	{
		if(stringBuffer.length() > 0)
			stringBuffer.delete(0, stringBuffer.length());	
	}
	
	
	/**
	 * 默认的路径为:项目/logs/fileName/日期/log文件#0.log
	 * @return 创建默认的log路径
	 */
	private String createDefaultLogPath()
	{
		String defaultPath = getDefaultPath();
		if(this.filePath == null || !this.filePath.equals(defaultPath))
			this.filePath = defaultPath;
		
		return filePath;
	}
	

	private File[] getReadFileArray() 
	{
		createDefaultLogPath();
		
		File rootFile = new File(this.filePath);
		
		File [] fileArray = rootFile.listFiles();
		
		return fileArray;
	}

	public synchronized void writeFile(Object content) 
	{
		try 
		{
			createFile();
			
			stringBuffer.append(getWriterTitle());
			stringBuffer.append(content).append(WRAP);
			
			bufferWriter.append(stringBuffer);
			bufferWriter.flush();
		}
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		
	}
	

	private String readFile() 
	{
		String str = null;
		try 
		{
			File [] files = getReadFileArray();
			
			clearStringBuffer();
			
			if(files != null)
			{
				for (int i = 0; i < files.length; i++) 
				{
					File file = files[i];
					
					BufferedReader bufferRead = createBufferedRead(file);
					
					while(bufferRead.ready())
					{
						str = bufferRead.readLine();
						stringBuffer.append(str).append(WRAP);
					}
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return stringBuffer.toString();
	}
	
	public synchronized void writeException(Object o , Exception e) 
	{
		try 
		{
			createFile();
			
			stringBuffer.append(getWriterTitle()).append(WRAP);
			
			if(o != null )
				stringBuffer.append(o).append(WRAP);
			
			stringBuffer.append(TAB).append(e.toString()).append(WRAP);
			StackTraceElement[] ste = e.getStackTrace();
			for (int i = 0; i < ste.length; i++)
				stringBuffer.append(TAB).append(TAB).append(ste[i].toString()).append(WRAP);
			
			bufferWriter.append(stringBuffer);
			bufferWriter.flush();
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
	}
	
	private String getWriterTitle()
	{
		Thread t = Thread.currentThread();
		String idName = DateTools.getCurrentTimeMSString() + " [" + t.getName()+" <" + t.getId() + ">] ";
		return idName;
	}
	
	public String getFileName() 	{		return fileName;	}
	public boolean isCache() {		return isCache;	}
}
