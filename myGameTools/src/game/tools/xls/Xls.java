package game.tools.xls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author zhibing.zhou
 * 基于poi 进行封装
 */
public class Xls 
{
	private String excelPath ;
	
	private String sheetName;
	
	private File excelFile;
	
	private Workbook workBook;  
	
	private static final DecimalFormat df = new DecimalFormat("#");
	
	public Xls(String excelPath) 
	{
		this.excelPath = excelPath;
		this.sheetName = "Sheet1";
		
		init();
	}
	
	public Xls(String excelPath, String sheetName) 
	{
		this.excelPath = excelPath;
		this.sheetName = sheetName;
		
		init();
	}

	private void init() 
	{
		excelFile= new File(excelPath);
		try 
		{
			if(excelFile.exists())
				workBook = new XSSFWorkbook(excelPath);
			else
				workBook = new XSSFWorkbook();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
		}
	}
	
	public void useSheet(String sheetName)
	{
		this.sheetName = sheetName;
	}

	private Sheet getSheet() 
	{
		Sheet sheet = this.workBook.getSheet(sheetName);
		
		if(sheet == null)
			sheet = this.workBook.getSheetAt(0);
		
		if(sheet == null)
			sheet = this.workBook.createSheet(sheetName);
		
		return sheet;
	}

	/**
	 * @param sheet
	 * @param row 添加位置，行从0开始
	 * @param column 添加位置，列从0开始
	 * @param value
	 */
	public void addCell(int rowNo , int columnNo , Object value)
	{
		if(value == null)
			return;
		
		Row row = getRow(rowNo);
		
		Cell cell = row.getCell(columnNo);
		if(cell == null)
			cell = row.createCell(columnNo);
		
		cell.setCellValue(value.toString());
	}
	
	private String getCellValue(int row , int column )
	{
		Row rowObj = getRow(row);
		if(rowObj == null)
			return null;
		
		Cell cell = rowObj.getCell(column);			//如果取出单元格为空
		
		if(cell == null)
			return null;
		
		int cellType = cell.getCellType();
		
//		CellType 类型 值
//		CELL_TYPE_NUMERIC 数值型 0
//		CELL_TYPE_STRING 字符串型 1
//		CELL_TYPE_FORMULA 公式型 2
//		CELL_TYPE_BLANK 空值 3
//		CELL_TYPE_BOOLEAN 布尔型 4
//		CELL_TYPE_ERROR 错误 5
		
//		System.out.println("cellType = " + cellType);
		
		switch (cellType) 
		{
			case Cell.CELL_TYPE_NUMERIC:
				return df.format(cell.getNumericCellValue());
			case Cell.CELL_TYPE_STRING:
				return cell.getStringCellValue();
			default:
				break;
		}
		return null;
	}
	
	
	public String getCell(int row , int column )
	{
		return getCellValue(row , column);
	}
	
	private Row getRow(int rowNo)
	{
		Sheet sheet = getSheet();
		Row rowObj = sheet.getRow(rowNo);
		if(rowObj == null)
			rowObj = sheet.createRow(rowNo);
		
		return rowObj;
	}
	
	public int getRowCount()
	{
		Sheet sheet = getSheet();
		return sheet.getLastRowNum();
	}
	
	public int getColumnCount()
	{
		Sheet sheet = getSheet();
		Row row = sheet.getRow(0);
		if(row != null)
			return row.getLastCellNum();
	
//		int coloumNum=sheet.getRow(0).getPhysicalNumberOfCells();		//获得总列数
//		int rowNum=sheet.getLastRowNum();					//获得总行数
//		return sheet.getRow(0).getPhysicalNumberOfCells();
		
		return sheet.getActiveCell().getColumn();
	}
	
	public void close()
	{
		if(workBook == null)
			return ;
		
		try 
		{
			if(!excelFile.exists())				//不存这个文件,而创建
			{
				FileOutputStream fileOut = new FileOutputStream(excelPath);
				workBook.write(fileOut);
				fileOut.flush();
				fileOut.close();
				
				System.out.println("create excel success");
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			try {
				workBook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

