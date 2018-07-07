package game.tools.utils;

import java.io.File;  
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.JarURLConnection;  
import java.net.URL;  
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;  
import java.util.HashSet;
import java.util.List;
import java.util.Set;  
import java.util.jar.JarEntry;  
import java.util.jar.JarFile;  
  
public class ClassUtils {  
    public static void main(String[] args) throws Exception 
    {  
    	String packageName = "org.objectweb.asm";  
//        Set<String> classNames = getClassName(packageName, false);  
        ArrayList<Object> list = getClassObjectList("game.tools.weight", false);
        
        System.out.println();
    }  
  
    public static <T> ArrayList<T> getClassObjectList(String packageName, boolean isRecursion) 
    {  
    	Set<String> classNames = getClassName(packageName, isRecursion);
    	
    	ArrayList<T> classList = new ArrayList<>(classNames.size());
    	
    	try 
    	{
	    	for (String className : classNames) 
	    	{
				Class<?> clzss = ClassLoader.getSystemClassLoader().loadClass(className);
				
				if(isCreate(clzss))
					classList.add((T)clzss.newInstance());
			}
    	}
    	catch (Exception e) 
    	{
    		e.printStackTrace();
    		game.tools.log.LogUtil.error(e);
    	}
    	
    	return classList;
    }
    
    
    public static ArrayList<Class<?>> getClassList(String packageName, boolean isRecursion) 
    {  
    	Set<String> classNames = getClassName(packageName, isRecursion);
    	
    	ArrayList<Class<?>> classList = new ArrayList<>(classNames.size());
    	
    	try 
    	{
	    	for (String className : classNames) 
	    	{
				Class<?> clzss = ClassLoader.getSystemClassLoader().loadClass(className);
				
				classList.add(clzss);
			}
    	}
    	catch (ClassNotFoundException e) 
    	{
    		e.printStackTrace();
    		game.tools.log.LogUtil.error(e);
    	}
    	
    	return classList;
    }
    
    /** 
     * 获取某包下所有类 
     * @param packageName 包名 
     * @param isRecursion 是否遍历子包 
     * @return 类的完整名称 
     */  
    public static Set<String> getClassName(String packageName, boolean isRecursion) {  
        Set<String> classNames = null;  
        ClassLoader loader = Thread.currentThread().getContextClassLoader();  
        String packagePath = packageName.replace(".", "/");  
  
        URL url = loader.getResource(packagePath);  
        if (url != null) {  
            String protocol = url.getProtocol();  
            if (protocol.equals("file")) {  
                classNames = getClassNameFromDir(url.getPath(), packageName, isRecursion);  
            } else if (protocol.equals("jar")) {  
                JarFile jarFile = null;  
                try{  
                    jarFile = ((JarURLConnection) url.openConnection()).getJarFile();  
                } catch(Exception e){  
                    e.printStackTrace();  
                }  
                  
                if(jarFile != null){  
                	classNames = getClassNameFromJar(jarFile.entries(), packageName, isRecursion);  
                }  
            }  
        } else {  
            /*从所有的jar包中查找包名*/  
            classNames = getClassNameFromJars(((URLClassLoader)loader).getURLs(), packageName, isRecursion);  
        }  
          
        return classNames;  
    }  
  
    /** 
     * 从项目文件获取某包下所有类 
     * @param filePath 文件路径 
     * @param className 类名集合 
     * @param isRecursion 是否遍历子包 
     * @return 类的完整名称 
     */  
    private static Set<String> getClassNameFromDir(String filePath, String packageName, boolean isRecursion) {  
        Set<String> className = new HashSet<String>();  
        File file = new File(filePath);  
        File[] files = file.listFiles();  
        for (File childFile : files) {  
            if (childFile.isDirectory()) {  
                if (isRecursion) {  
                    className.addAll(getClassNameFromDir(childFile.getPath(), packageName+"."+childFile.getName(), isRecursion));  
                }  
            } else {  
                String fileName = childFile.getName();  
                if (fileName.endsWith(".class") && !fileName.contains("$")) {  
                    className.add(packageName+ "." + fileName.replace(".class", ""));  
                }  
            }  
        }  
  
        return className;  
    }  
  
      
    /** 
     * @param jarEntries 
     * @param packageName 
     * @param isRecursion 
     * @return 
     */  
    private static Set<String> getClassNameFromJar(Enumeration<JarEntry> jarEntries, String packageName, boolean isRecursion){  
        Set<String> classNames = new HashSet<String>();  
          
        while (jarEntries.hasMoreElements()) {  
            JarEntry jarEntry = jarEntries.nextElement();  
            if(!jarEntry.isDirectory()){  
                /* 
                 * 这里是为了方便，先把"/" 转成 "." 再判断 ".class" 的做法可能会有bug 
                 * (FIXME: 先把"/" 转成 "." 再判断 ".class" 的做法可能会有bug) 
                 */  
                String entryName = jarEntry.getName().replace("/", ".");  
                if (entryName.endsWith(".class") && !entryName.contains("$") && entryName.startsWith(packageName)) {  
                    entryName = entryName.replace(".class", "");  
                    if(isRecursion){  
                        classNames.add(entryName);  
                    } else if(!entryName.replace(packageName+".", "").contains(".")){  
                        classNames.add(entryName);  
                    }  
                }  
            }  
        }  
          
        return classNames;  
    }  
      
    /** 
     * 从所有jar中搜索该包，并获取该包下所有类 
     * @param urls URL集合 
     * @param packageName 包路径 
     * @param isRecursion 是否遍历子包 
     * @return 类的完整名称 
     */  
    private static Set<String> getClassNameFromJars(URL[] urls, String packageName, boolean isRecursion) {  
        Set<String> classNames = new HashSet<String>();  
          
        for (int i = 0; i < urls.length; i++) {  
            String classPath = urls[i].getPath();  
              
            //不必搜索classes文件夹  
            if (classPath.endsWith("classes/")) {continue;}  
  
            JarFile jarFile = null;  
            try {  
                jarFile = new JarFile(classPath.substring(classPath.indexOf("/")));  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
  
            if (jarFile != null) {  
                classNames.addAll(getClassNameFromJar(jarFile.entries(), packageName, isRecursion));  
            }  
        }  
          
        return classNames;  
    }  
    
    
    
    /**
	 * @param content
	 * @return 返回这个对象所存在的泛型数组
	 */
	public static Class[] getGenericArray(Object content)
	{
		
		Type genType = content.getClass().getGenericSuperclass();   
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();   
		Class c =  (Class)params[0];  
		
		Class [] classArray = new Class[params.length];
		
		for (int i = 0; i < classArray.length; i++) 
		{
			classArray[i] = (Class<Object>)params[i];
		}
		return classArray;
	}
	
	
	public static String getLocalPath() 
	{
	    String path = System.getProperty("user.dir");
	    return path.replace("\\\\", "\\");
	}
	
	
	
	/**
	 * @return 是否有空的构建函数，用于创建对象使用
	 */
	public static boolean isCreate(Class clzss)
	{
		boolean isAbs = Modifier.isAbstract(clzss.getModifiers()) ;
		if(isAbs)			//如果是抽象类
			return false;
		
		boolean isInter = Modifier.isInterface(clzss.getModifiers());
		if(isInter)			//如果是接口
			return false;
		
		Constructor [] constrArray = clzss.getConstructors();
		for (Constructor constructor : constrArray) 
		{
			if(constructor.getParameterTypes().length == 0)		//如果有空的构造函数
				return true;
		}
		return false;
	}
}  