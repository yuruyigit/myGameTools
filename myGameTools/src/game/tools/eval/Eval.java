package game.tools.eval;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import game.tools.utils.DateTools;


class JavaSourceFromString extends SimpleJavaFileObject 
{
	/** 源代码 */  
	final String code;
	
	JavaSourceFromString(String name, String code) 
	{
	    super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension), Kind.SOURCE);
	    this.code = code;
	}
	
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors)	{	    return code;	}
}

class JavaClassFileObject extends SimpleJavaFileObject 
{
    //用于存储class字节
    ByteArrayOutputStream outputStream;

    public JavaClassFileObject(String className, Kind kind) 
    {
        super(URI.create("string:///" + className.replace('.', '/') + kind.extension), kind);
        outputStream = new ByteArrayOutputStream();
    }

    @Override
    public OutputStream openOutputStream() throws IOException {        return outputStream;    }
    public byte[] getClassBytes() {        return outputStream.toByteArray();    }
}

class ClassFileManager extends ForwardingJavaFileManager 
{
    private JavaClassFileObject classFileObject;

    protected ClassFileManager(JavaFileManager fileManager)    {        super(fileManager);    }
    
    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,  FileObject sibling) throws IOException 
    {
        classFileObject = new JavaClassFileObject(className, kind);
        return classFileObject;
    }

    @Override
    //获得一个定制ClassLoader，返回我们保存在内存的类
    public ClassLoader getClassLoader(Location location) 
    {
        return new ClassLoader() 
        {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException 
            {
                byte[] classBytes = classFileObject.getClassBytes();
                return super.defineClass(name, classBytes, 0, classBytes.length);
            }
        };
    }
}

public class Eval 
{
	 /**
     * 从java6版本开始，已经支持动态编译了，你可以在运行期直接编译.java文件，执行.class文件，并且能够获得相关的输入输出，
     * 甚至还能监听相关的事件。
     * java的动态编译提供了多种渠道，比如，可以动态编译一个字符串，也可以是文本文件，也可以是编译过的字节码文件(.class文件)，
     * 甚至可以是存放在数据库中的明文代码或字节码，只要是符合java规范的就都可以在运行期动态加载，其实现方式就是实现JavaFileObject
     * 接口，重写getCharContent、openInputStream、openOutputStream，或者实现JDK
     * 已经提供的两个SimpleJavaFileObject、ForwardingJavaFileObject。下面我演示一下，如何动态编译一个字符串。
     * 
     * 当前编译器：注意，如果是用的jdk1.6的版本（建议使用jdk1.7，1.7是没有任何问题的），ToolProvider.
     * getSystemJavaCompiler()拿到的对象将会为null，
     * 原因是需要加载的Tools.jar不在jdk安装目录的jre目录下，需要手动将lib目录下的该jar包拷贝到jre下去，详情请参考：
     * http://www.cnblogs.com/fangwenyu/archive/2011/10/12/2209051.html
     */
    private static final JavaCompiler JAVA_CMP = ToolProvider.getSystemJavaCompiler();
    // Java标准文件管理器
    private static final ClassFileManager FILE_MANAGER = new ClassFileManager(JAVA_CMP.getStandardFileManager(null, null, null));
    
	public static Object eval(String sourceStr )
	{
		return eval(sourceStr, null);
	}
	
	public static Object eval(String sourceStr , String args)
	{
		String clsName = sourceStr.substring(sourceStr.indexOf("public class ") , sourceStr.indexOf("{")).split(" ")[2].trim();
		
		// Java文件对象
        SimpleJavaFileObject fileObject = new JavaSourceFromString(clsName, sourceStr);
        
        // 设置编译环境
        JavaCompiler.CompilationTask task = JAVA_CMP.getTask(null, FILE_MANAGER, null, null, null, Arrays.asList(fileObject));
        
        // 编译成功
        if (task.call())
        {
			try 
			{
				//获得ClassLoader，加载class文件
				ClassLoader classLoader = FILE_MANAGER.getClassLoader(null);
				
				Class clzss = classLoader.loadClass(clsName);
				
				Method m = clzss.getMethod("main" , String.class);
				
				return m.invoke(null , args);
			}
			catch (Exception e) 
			{
				System.err.println("not found method \"public static String main(String args)\"");
				e.printStackTrace();
			}
        }
        return null;
	}
	
  
    public static void main(String[] args) throws Exception 
    {
        // Java源代码
        String sourceStr = ""
        		+ "import game.tools.eval.Eval;"
        		+ "import game.tools.utils.DateTools;"
        		+ "public class Hello"
        		+ "{"
        		+ "		public static String main(String string)"
        		+ "		{"
        		+ "			System.out.println( \"Hello, \"+ string + \" \" + DateTools.getCurrentTimeMSString());"
        		+ "			return null;"
        		+ "		}"
        		+ "}"
				+ "";
        
        System.out.println(DateTools.getCurrentTimeMSString());
        Eval.eval(sourceStr , "SADFDSF");
        Eval.eval(sourceStr);
        
    }
}