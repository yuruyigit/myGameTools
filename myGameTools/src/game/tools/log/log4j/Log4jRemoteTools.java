package game.tools.log.log4j;

import org.apache.log4j.Logger;

public class Log4jRemoteTools 
{
	private static final Logger logger ;
	
	static
	{
		logger = Logger.getLogger(Log4jRemoteTools.class);
	}
	
	public static void error(String message)
	{
		logger.error(message);
	}
	
	public static void info(String message)
	{
		logger.info(message);
	}
	
	
	public static void main(String[] args) 
	{
		String message = "作为一名Java程序员，我们开发了很多Java应用程序，包括桌面应用、WEB应用以及移动应用。然而日志系统是一个成熟Java应用所必不可少的，在开发和调试阶段，日志可以帮助我们更好更快地定位bug；在运行维护阶段，日志系统又可以帮我们记录大部分的异常信息，从而帮助我们更好的完善系统。本文要来分享一些Java程序员最常用的Java日志框架组件。"
				+ "Log4j是一款基于Java的开源日志组件，Log4j功能非常强大，我们可以将日志信息输出到控制台、文件、用户界面，也可以输出到操作系统的事件记录器和一些系统常驻进程。更值得一提的是，Log4j可以允许你非常便捷地自定义日志格式和日志等级，可以帮助开发人员全方位地掌控日志信息"
				+ "Log4j是一款基于Java的开源日志组件，Log4j功能非常强大，我们可以将日志信息输出到控制台、文件、用户界面，也可以输出到操作系统的事件记录器和一些系统常驻进程。更值得一提的是，Log4j可以允许你非常便捷地自定义日志格式和日志等级，可以帮助开发人员全方位地掌控日志信息"
				+ "Log4j是一款基于Java的开源日志组件，Log4j功能非常强大，我们可以将日志信息输出到控制台、文件、用户界面，也可以输出到操作系统的事件记录器和一些系统常驻进程。更值得一提的是，Log4j可以允许你非常便捷地自定义日志格式和日志等级，可以帮助开发人员全方位地掌控日志信息"
				+ "Log4j是一款基于Java的开源日志组件，Log4j功能非常强大，我们可以将日志信息输出到控制台、文件、用户界面，也可以输出到操作系统的事件记录器和一些系统常驻进程。更值得一提的是，Log4j可以允许你非常便捷地自定义日志格式和日志等级，可以帮助开发人员全方位地掌控日志信息"
				+ "Log4j是一款基于Java的开源日志组件，Log4j功能非常强大，我们可以将日志信息输出到控制台、文件、用户界面，也可以输出到操作系统的事件记录器和一些系统常驻进程。更值得一提的是，Log4j可以允许你非常便捷地自定义日志格式和日志等级，可以帮助开发人员全方位地掌控日志信息"
				+ "Log4j是一款基于Java的开源日志组件，Log4j功能非常强大，我们可以将日志信息输出到控制台、文件、用户界面，也可以输出到操作系统的事件记录器和一些系统常驻进程。更值得一提的是，Log4j可以允许你非常便捷地自定义日志格式和日志等级，可以帮助开发人员全方位地掌控日志信息";
		
		error(message);
		
//		for (int i = 0; i < 1000; i++) {
//			
//			error(i + message);
//		}
	}
}
