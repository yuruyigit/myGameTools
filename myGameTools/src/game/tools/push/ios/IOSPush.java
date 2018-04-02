package game.tools.push.ios;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import game.tools.log.LogUtil;
import game.tools.utils.Util;
import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;

public class IOSPush {

	private static int badge = 1;													// 图标小红圈的数值
	private static String sound = "default";										// 铃音
	private static String certificatePath = "conf/push_development_cn.gamechef.skyfall.p12";				//证书路径
	private static String certificatePassword = "123456";							// 此处注意导出的证书密码不能为空因为空密码会报错
	private static boolean isProd = true;											// true：表示的是产品发布推送服务 false：表示的是产品测试推送服务
	
	
	private static PushNotificationManager pushManager = new PushNotificationManager();
	
	static
	{
		try 
		{
			
			Properties propertie = new Properties();
			propertie.load(new FileInputStream("conf/IOSPush.properties"));
			
//			badge = Integer.valueOf(propertie.getProperty("badge", "conf/push_development_cn.gamechef.skyfall.p12"));
//			sound = propertie.getProperty("sound","default");
//			certificatePath = propertie.getProperty("certificatePath","conf/push_development.p12");
//			certificatePassword = propertie.getProperty("certificatePassword", "false");
//			isProd = Boolean.valueOf(propertie.getProperty("isProd", "false"));
			 
			
			// true：表示的是产品发布推送服务 false：表示的是产品测试推送服务
//			pushManager.initializeConnection(new AppleNotificationServerBasicImpl(certificatePath, certificatePassword, isProd));
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			LogUtil.error(e);
		}
	}
	
	
	public static boolean Push(String alert , String ...deviceTokenArr) 
	{
		List<String> tokens = new ArrayList<String>(deviceTokenArr.length);
		for (int i = 0; i < deviceTokenArr.length; i++) 
		{
			tokens.add(deviceTokenArr[i]);
		}
		
		return Push(alert , tokens);
	}
	
	/**
	 * IOS 的消息推送
	 * @param deviceToken 设备的token
	 * @param alert 要推送的消息内容
	 */
	private static boolean Push(String alert , List<String> tokens) 
	{
//		List<String> tokens = new ArrayList<String>();
//		tokens.add(deviceToken);
		
		boolean result = false;
		
		try 
		{
			PushNotificationPayload payLoad = new PushNotificationPayload();
			payLoad.addAlert(alert); // 消息内容
			payLoad.addBadge(badge); // iphone应用图标上小红圈上的数值
			if (!Util.stringIsEmpty(sound))
			{
				payLoad.addSound(sound);// 铃音
			}
			
//			pushManager.initializePreviousConnection();
			
			PushNotificationManager pushManager = new PushNotificationManager();
			// true：表示的是产品发布推送服务 false：表示的是产品测试推送服务
			pushManager.initializeConnection(new AppleNotificationServerBasicImpl(certificatePath, certificatePassword, isProd));
			List<PushedNotification> notifications = new ArrayList<PushedNotification>();
			
			// 发送push消息
			if (tokens.size() == 1)
			{
				Device device = new BasicDevice();
				device.setToken(tokens.get(0));
				PushedNotification notification = pushManager.sendNotification(device, payLoad, true);
				notifications.add(notification);
			} 
			else 
			{
				List<Device> device = new ArrayList<Device>();
				for (String token : tokens) 
				{
					device.add(new BasicDevice(token));
				}
				notifications = pushManager.sendNotifications(payLoad, device);
			}
			
			List<PushedNotification> failedNotifications = PushedNotification.findFailedNotifications(notifications);
			List<PushedNotification> successfulNotifications = PushedNotification.findSuccessfulNotifications(notifications);
			
			int failed = failedNotifications.size();
			int successful = successfulNotifications.size();
			
			pushManager.stopConnection();
			
			
			if(failed >= 1)
			{
				PushedNotification pnf = failedNotifications.get(0);
				System.out.println("failed " + pnf.getPayload().getExpiry());
				result = false;
			}
			if(successful >= 1)
			{
				PushedNotification pnf = successfulNotifications.get(0);
				System.out.println("successful " + pnf.getPayload().getExpiry());
				result = true; 
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			LogUtil.error(e);
			
			return result;
		}
		
		return result;
	}
	
	
	public static boolean Push1(String deviceToken, String alert) 
	{
		return false;
	}
	

	public static void main(String[] args) throws Exception 
	{
		String deviceToken = "a5a9a648fd8a1053effa74ed8e4a231c6d1163b2991b34d809fb78c219099d17";
		String alert = "中央人民广播电视台: 您已被中南海锁定, 请及时配合调查。";// push的内容

		for (int i = 0; i < 10; i++) 
		{
			long startTime = System.currentTimeMillis();
			
			boolean result = Push(alert , deviceToken);
			
//			IosPushUtil.queue(new String []{deviceToken},alert, 1, null, null);
			
			long endTime = System.currentTimeMillis();
			
			System.out.println((endTime - startTime) + "  result = " );
		}
		
	}

}