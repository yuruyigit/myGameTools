package game.tools.utils;

public class ArrayString 
{
	/**
	 * 消耗字符串数组中的元素<br/>
	 * 使用数组场景类型为<br/>		
	 * [<br/>
	 * 		&emsp;[10001,10],<br/>
	 * 		&emsp;[10002,25]<br/>
	 * ];<br/>
	 * zzb 2015年4月30日下午4:39:43 <br/>
	 * @param arr<br/>
	 * @param condition<br/>
	 * @return 
	 */
	public static String costArrayByCondition(int [][] arr, int condition) 
	{
		return costArrayByCondition(arr, condition , 1);
	}
	
	
	/**
	 * 
	 * 消耗字符串数组中的元素<br/>
	 * 使用数组场景类型为<br/>		
	 * [<br/>
	 * 		&emsp;[10001,10],<br/>
	 * 		&emsp;[10002,25]<br/>
	 * ];<br/>
	 * zzb 2015年5月4日上午11:38:17 
	 * @param arr
	 * @param condition
	 * @param val 传入要扣除的值
	 * @return 
	 */
	public static String costArrayByCondition(int [][] arr, int condition , int val) 
	{
		if(arr == null)
			return null;
		
		StringBuffer updateStr = new StringBuffer();
		
		for (int i = 0; i < arr.length; i++) 
		{
			if(arr[i][0] == condition)
			{
				arr[i][1] -= val;
				if(arr[i][1] == 0)
					continue;
			}
			updateStr.append(arr[i][0]).append(Symbol.COLON).append(arr[i][1]).append(Symbol.COMMA);
		}
		return updateStr.toString();
	}
	
	/**
	 * 取消字符串数组
	 * 使用数组场景类型为<br/>		
	 * &emsp;[10001,10002],<br/>
	 * zzb 2015年5月5日下午2:46:35 
	 * @param arr
	 * @param condition
	 * @param val
	 * @return 
	 */
	public static String costArrayByCondition(int [] arr, int condition ) 
	{
		StringBuffer updateStr = new StringBuffer();
		for (int i = 0; i < arr.length; i++) 
		{
			if(arr[i] == condition)
					continue;
			
			updateStr.append(arr[i]).append(Symbol.COMMA);
		}
		return updateStr.toString();
	}
	
	/**
	 * 根据条件设置数组元素为累加值(如果没有该元素，则会自动添加数组中)<br/>
	 * 使用数组场景类型为		<br/>
	 *  [<br/>
	 * 		&emsp;[10001,10],<br/>
	 * 		&emsp;[10002,25]<br/>
	 * ];<br/>
	 * zzb 2015年4月30日下午4:33:37 	<br/> 
	 * @param arr<br/>
	 * @param condition<br/>
	 * @return 
	 */
	public static String addArrayByCondition(int [][] arr, int condition) 
	{
		StringBuffer updateStr = new StringBuffer();
		boolean isExist = false;
		if(arr != null)
		{
			for (int i = 0; i < arr.length; i++) 
			{
				if(arr[i][0] == condition)
				{
					arr[i][1]++;
					isExist = true;
					updateStr.append(arr[i][0]).append(Symbol.COLON).append(arr[i][1]).append(Symbol.COMMA);
				}
				else
					updateStr.append(arr[i][0]).append(Symbol.COLON).append(arr[i][1]).append(Symbol.COMMA);
			}
		}
		if(!isExist)
			updateStr.append(condition).append(Symbol.COLON).append(1).append(Symbol.COMMA);
		return updateStr.toString();
	}
	
	/**
	 * 根据条件设置数组元素为传入的值 <br/>
	 * 使用数组场景类型为		<br/>
	 * [<br/>
	 * 		&emsp;[10001,10],<br/>
	 * 		&emsp;[10002,25]<br/>
	 * ];<br/>
	 * zzb 2015年4月30日下午4:32:39<br/> 
	 * @param arr 传入要操作的数组<br/>
	 * @param condition 传入要操作数组的条件<br/>
	 * @param val 传入要设置条件元素的值<br/>
	 * @return 
	 */
	public static String setArrayByCondition(int [][] arr, int condition, int val) 
	{
		StringBuffer updateStr = new StringBuffer();
		
		for (int i = 0; i < arr.length; i++) 
		{
			if(arr[i][0] == condition)
				arr[i][1] = val;
			updateStr.append(arr[i][0]).append(Symbol.COLON).append(arr[i][1]).append(Symbol.COMMA);
		}
		return updateStr.toString();
	}
	
	/**
	 *  根据条件获取数组中数据
	 * zzb 2015年4月30日下午4:02:48 
	 * @param arr
	 * @param itemId
	 * @return 
	 */
	public static int getArrayByCondition(int [] [] arr , int itemId) 
	{
		if(arr == null)
			return -1;
		
		for (int i = 0; i < arr.length; i++) 
		{
			if(arr[i][0] == itemId)
				return arr[i][1] ;
		}
		return -1;
	}

	/**
	 * zzb 2014-4-27 下午1:29:58
	 * @param obj 原内容
	 * @param val 要添加的内容
	 * @return 返回一个 添加好的内容数组
	 */
	public static String addArrayStr(String obj, int val) {
		StringBuffer updateStr = null;
		if (obj == null)
			updateStr = new StringBuffer();
		else
			updateStr = new StringBuffer(obj);

		updateStr.append(val);
		updateStr.append(",");
		return updateStr.toString();
	}
	

	/**
	 * zzb 2014-4-27 下午4:32:34
	 * 返回修改指定逗位置有序的字符串
	 * @param useEquipArr 传入的字符串数组
	 * @param val 入值
	 * @param post 传入的改变的下标
	 * @return 返回修改后的字符串
	 */
	public static String addArrayStr(String useEquipArr, int val, int post) {
		String[] arr = useEquipArr.split(Symbol.COMMA);
		arr[post] = val + Symbol.EMPTY;

		StringBuffer updateStrBuf = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			updateStrBuf.append(arr[i]);
			updateStrBuf.append(",");
		}
		return updateStrBuf.toString();
	}
	

	public static String addArrayStr(String obj, String val) 
	{
		StringBuffer updateStr = null;
		if (obj == null)
			updateStr = new StringBuffer();
		else
			updateStr = new StringBuffer(obj);

		updateStr.append(val);
		updateStr.append(",");
		return updateStr.toString();
	}
	

	public static String setArrayStr(String [] arr, int val, int post) {
		arr[post] = val + Symbol.EMPTY;

		StringBuffer updateStrBuf = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			updateStrBuf.append(arr[i]);
			updateStrBuf.append(Symbol.COMMA);
		}
		return updateStrBuf.toString();
	}

	/**
	 *  转换数组为逗号字符串
	 * zzb 2014-4-27 下午4:32:06
	 * @param arr 传入的字符串数组
	 * @return 返回数组的字符串
	 */
	public static String toArrayString(Object[] arr)
	{
		StringBuffer updateStrBuf = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			updateStrBuf.append(arr[i]);
			updateStrBuf.append(Symbol.COMMA);
		}
		return updateStrBuf.toString();
	}
	
	public static String toArrayString(int[] arr)
	{
		StringBuffer updateStrBuf = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			updateStrBuf.append(arr[i]);
			updateStrBuf.append(Symbol.COMMA);
		}
		return updateStrBuf.toString();
	}
	
	
	public static String arrayToStr(int[] arr) {
		if(arr == null)
			return null;
		StringBuffer updateStrBuf = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			updateStrBuf.append(arr[i]);
			updateStrBuf.append(Symbol.COMMA);
		}
		return updateStrBuf.toString();
	}
	
	public static String longArrayToStr(long[] arr) {
		if(arr == null)
			return null;
		StringBuffer updateStrBuf = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			updateStrBuf.append(arr[i]);
			updateStrBuf.append(Symbol.COMMA);
		}
		return updateStrBuf.toString();
	}
	
	/**
	 * 将一个字符串逗号数组，解析成一个int数组
	 * <br/>zzb 2014年5月16日 下午4:32:25<br/>
	 * @param strArr 传入的字符串逗号隔开的数组字符串
	 * @return 返回一个int类型的数组
	 */
	public static long[] parseStrArrToLongArr(String str)
	{
		return parseStrArrToLongArr(str , Symbol.COMMA);
	}
	
	public static int[] parseStrArrToIntArr(String str)
	{
		return parseStrArrToIntArr(str , Symbol.COMMA);
	}
	
	public static String[] parseStrArrToObjectArr(String str)
	{
		return parseStrArrToObjectArr(str , Symbol.COMMA);
	}
	
	/**
	 * 将一个字符串根据指定的分割符，转换成数组，解析成一个int数组
	 * zzb 2015年5月15日上午11:15:36 
	 * @param str
	 * @param split 根据这个字符串分割成数组
	 * @return 
	 */
	public static int[] parseStrArrToIntArr(String str , String split)
	{
		if(str == null || str.equals(Symbol.EMPTY))
			return null ;
		
		String [] strArr = str.split(split);
		
		int [] intArr = new int [strArr.length];
		
		for (int i = 0; i < intArr.length; i++) 
			intArr[i] = Integer.parseInt(strArr[i]);
		
		return intArr;
	}
	
	public static String[] parseStrArrToObjectArr(String str , String split)
	{
		if(str == null || str.equals(Symbol.EMPTY))
			return null ;
		
		String [] strArr = str.split(split);
		
		String [] intArr = new String [strArr.length];
		
		for (int i = 0; i < intArr.length; i++) 
			intArr[i] = String.valueOf(strArr[i]);
		
		return intArr;
	}
	
	public static float[] parseStrArrToFloatArr(String str , String split)
	{
		if(str == null || str.equals(Symbol.EMPTY))
			return null ;
		
		String [] strArr = str.split(split);
		
		float [] intArr = new float [strArr.length];
		
		for (int i = 0; i < intArr.length; i++) 
			intArr[i] = Float.valueOf(strArr[i]);
		
		return intArr;
	}
	
	public static long[] parseStrArrToLongArr(String str , String split)
	{
		if(str == null || str.equals(Symbol.EMPTY))
			return null ;
		
		String [] strArr = str.split(split);
		
		long [] intArr = new long [strArr.length];
		
		for (int i = 0; i < intArr.length; i++) 
			intArr[i] = Long.parseLong(strArr[i]);
		
		return intArr;
	}
	
	
	public static int [] parseTimeStrToIntArr(String str )
	{
		int [] arr = null;
		String [] time = str.split(Symbol.COLON);
	    arr = new int[time.length];
        for (int i = 0; i < time.length; i++) 
        	arr[i] = Integer.parseInt(time[i]);
        return arr;
	}
	
	/**
	 * @param str
	 * @return 返回以逗号隔开的字符串整型数组 
	 */
	public static int[] strToArray(String str)
	{
		String [] arr =str.split(Symbol.COMMA);
		int [] val = new int[arr.length];
		
		for (int i = 0; i < arr.length; i++) 
			val[i] = Integer.parseInt(arr[i]);
		return val;
	}
	
	
	/**
	 * @param arrayString 要分割数组的字符串内容
	 * @param symbol1 1维数组分割字符
	 * @param symbol2 2维数组分割字符
	 * @return 根据指定的符号，返回一个对应的二维数组数值
	 */
	public static int[] [] getArrayBySymble(String arrayString , String symbol1 , String symbol2)
	{
		if(StringTools.empty(arrayString))
			return null;
		
		String [] arrayStrArr = arrayString.split(symbol1);
		String [] itemStrArr = arrayStrArr[0].split(symbol2);
		
		int [] []  arrayValue = new int [arrayStrArr.length][itemStrArr.length];
		
		for (int i = 0; i < arrayValue.length; i++) 
		{
			itemStrArr = arrayStrArr[i].split(symbol2);
			for (int j = 0; j < itemStrArr.length; j++) 
				arrayValue [i][j] = Integer.parseInt(itemStrArr[j]);
		}
		return arrayValue;
	}
	
	/**
	 * @param arrayString 要分割数组的字符串内容
	 * @param symbol1 1维数组分割字符
	 * @param symbol2 2维数组分割字符
	 * @return 根据指定的符号，返回一个对应的二维数组数值
	 */
	public static int[] [] parseArrayBySymble(String arrayString , String symbol1 , String symbol2)
	{
		return getArrayBySymble(arrayString, symbol1 , symbol2);
	}
	
	
//	/**
//	 * 
//	 * zzb 
//	 * @param string 要分割数组的字符串内容
//	 * @param splitArray [1维数组分割字符 , 2维数组分割字符 , 3维数组分割字符 ....]
//	 * @return  返回一个根据多个分割符的对应数组
//	 */
//	public static <T> T parseStrArrToIntArrBySplitArray(String string , String ...splitArray)
//	{
//		if(StringTools.empty(string) || splitArray == null)
//			return null ;
//		
//		int [] intArray = new int[splitArray.length];
//		
//		for (int i = 0; i < splitArray.length; i++)
//		{
//			String split = splitArray[i];
//			
//			String [] splitString1 = string.split(split);
//			
//			for (int j = 0; j < intArray.length; j++)
//			{
//				
//			}
//		}
//	}
	
	public static void main(String[] args) 
	{
		int [] [] arr = {{10001,1},{1222,10}};
		String n = ArrayString.costArrayByCondition(arr, 10001);
		System.out.println(n);
		
		int s = ArrayString.getArrayByCondition(arr, 10001);
		System.out.println(s);
		
		int [][] value = getArrayBySymble("1000003;1,4200007;10,4200006;50,3;58888", Symbol.COMMA, Symbol.SEMICOLON);
		
		System.out.println();
	
	}
}
