package game.tools.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.plaf.synth.SynthSplitPaneUI;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * @author zhibing.zhou
 * log 统计
 * readme 
 * 
 * 游戏服务器中产生的消费日志目录（consume），
 * 把该文件夹放到readCataLog这个变量指定的地址中，
 * 运行程序即可。
 * 
 * 保存的xls文档位于readCataLog这个地址中。
 */
public class LogStatistics 
{
	private static final String MSG_ENCODE = "utf-8";
	private static final String TAB = "\t";

	private static int getConsumeDataIndex = 0 ;
	
	public static void main(String[] args) throws Exception
	{
		String readCataLog = "C:/Users/zhibing.zhou/Downloads/统计/付费/";
		String saveName = "钻石统计";
		
		consumeStatistics(saveName,readCataLog);
	}
	
	
	
	/**
	 *  钻石统计
	 * @param saveName
	 * @param dir
	 * @throws Exception
	 */
	private static void consumeStatistics(String saveName , String dir) throws Exception
	{
		ArrayList<File[]> fileLayer = new ArrayList<File[]>();
		
		File rootFile = new File(dir);
		
		File [] rootFileArr = rootFile.listFiles();
		
		fileLayer.add(rootFileArr);			//添加文档根目录层级
		
//		HashMap<String , ConsumeStatistics> sMap = new HashMap<String , ConsumeStatistics>();
		
		TreeMap<String , ConsumeStatistics> sMap = new TreeMap<String , ConsumeStatistics>();
		
		int rootIndex = 0 , dayIndex = 0;
		
		File curFile = fileLayer.get(0)[rootIndex];
		
		while(curFile.isDirectory())	//如果是目录的话
		{
			File [] listFiles = curFile.listFiles();
			curFile = listFiles[0];
			
			if(curFile.isDirectory())			//如果是目录的话，则添加到层级列表
			{
				fileLayer.add(listFiles);
				continue;
			}
			else								//如果不是，则获取上一级层级列表
			{
				dayIndex ++;
				File[] upLayerArr= fileLayer.get(fileLayer.size() - 1);
				if(dayIndex < upLayerArr.length )
					curFile = upLayerArr[dayIndex];
				else
					fileLayer.remove(upLayerArr);
			}
			
			if(listFiles != null)
			{
				for (File file : listFiles) 
				{
					getConsumeData(file , sMap);
//					System.out.println(file.getAbsolutePath());
				}
			}
			
		}
		
		
		System.out.println();
		
//		for (int i = 0; i < rootFileArr.length; i++) 
//		{
//			File log = rootFileArr[i];
//			if(log.isDirectory())			//如果是目录的话
//			{
//				File [] logs = log.listFiles();
//				
//				for (int j = 0; j < logs.length; j++) 
//				{
//					log = logs[j];
//					
//					if(log.isDirectory())			//如果是目录的话
//					{
//						logs = log.listFiles();
//						
//						for (int k = 0; k < logs.length; k++) 
//						{
//							log = logs[k];
//							getConsumeData(log , sMap);
//						}
//					}
//				}
//			}
//			else
//				getConsumeData(log , sMap);
//		}
		
		System.out.println("get data ok !");
		
		WritableWorkbook book = Workbook.createWorkbook(new File(dir+saveName+".xls"));
		// 创建 Excel 工作表
		WritableSheet wSheet = book.createSheet("name", 0);
		
		wSheet.addCell(new Label(0,0,"消耗类型"));
		wSheet.addCell(new Label(1,0,"总金额"));
		wSheet.addCell(new Label(2,0,"消费次数"));
		wSheet.addCell(new Label(3,0,"人数"));
		
		
		
		int index = 1;
		for (ConsumeStatistics consumeStat : sMap.values()) 
		{
			 wSheet.addCell(new Label(0,index,consumeStat.getCostName()));
			 wSheet.addCell(new Label(1,index,consumeStat.getTotalMoney() +""));
			 wSheet.addCell(new Label(2,index,consumeStat.getCount() +""));
			 wSheet.addCell(new Label(3,index,consumeStat.getRoleCount() + ""));
			 
			 index ++; 
		}
		
		book.write();
		book.close();
		
		System.out.println("export xls ok !");
	}
	
	
	/**
	 * 获取钻石消费的数据
	 * @param log
	 * @param sMap
	 * @throws Exception
	 */
	private static void getConsumeData(File log , Map<String , ConsumeStatistics> sMap) throws Exception
	{
		if(log.getName().indexOf("consume") < 0)
			return;
		
		BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(log),MSG_ENCODE));
		String line = null;
		
		while(read.ready()) 
		{
			line = read.readLine();
			if(line.indexOf("角色ID") >= 0 || "".equals(line))
				continue;
			
			String [] lineArr = line.split(TAB);
			String roleId = lineArr[0].split(" ")[2];
			String costName = lineArr[2];
			String money = lineArr[3];
			
			ConsumeStatistics consumeStat = sMap.get(costName);
			if(consumeStat == null)
			{
				consumeStat = new ConsumeStatistics(costName);
				sMap.put(costName, consumeStat);
			}
			consumeStat.add(roleId, money);
		}
		System.out.println(" in process ... " + log.getAbsolutePath() + " at " + (++getConsumeDataIndex) );
	}
}

class ConsumeStatistics
{
	private String costName;		//消耗名字
	private int  totalMoney ;		//消耗金额
	private int count;				//该类型数量
	private ArrayList<String> roleIdList ; //购买的人列表
	
	public ConsumeStatistics(String costName) 
	{
		this.costName = costName;
		this.roleIdList = new ArrayList<>();
	}
	
	int add(String roleId , String money)
	{
		if(!roleIdList.contains(roleId))
			roleIdList.add(roleId);
		
		this.totalMoney += Integer.parseInt(money);
		this.count++;
		
		return totalMoney;
	}

	public String getCostName() {		return costName;	}
	public int getTotalMoney() {		return totalMoney;	}
	public int getCount() {		return count;	}
	public int getRoleCount()	{		return roleIdList.size();	}
	
	
}