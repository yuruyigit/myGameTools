package game.tools.push.ios;

import org.apache.commons.lang3.StringUtils;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import javapns.notification.ResponsePacket;

public class PushPNS1 {
 
	public static void main(String[] args) {
		try {
			String token ="94d1a2774a773c8134b8a401742edd2accf5d3bbf7156e5fc2aef63acf963581";
			int badge=1;
			String sound="defalt";
			//String url = "dev.p12";
			String url = "conf/key.p12";
			String password = "a1234";
			PushNotificationPayload payload=new PushNotificationPayload();
			payload.addAlert("您有一个视频呼叫");
			payload.addBadge(badge);
			payload.addCustomDictionary("remote_id", "11");
			payload.addCustomDictionary("type", "video");
			if(!StringUtils.isEmpty(sound)) {
				payload.addSound(sound);
			}
			PushNotificationManager pushManager=new PushNotificationManager();
			pushManager.initializeConnection(new AppleNotificationServerBasicImpl(url, password, false));
			//List<PushedNotification> notifications=new ArrayList<PushedNotification>();
			Device device=new BasicDevice();
			device.setToken(token);
			PushedNotification notification=pushManager.sendNotification(device, payload,true);
			if(notification.isSuccessful()) {
				ResponsePacket theErrorResponse = notification.getResponse();
				if(theErrorResponse != null && theErrorResponse.isErrorResponsePacket()) {
					System.out.println("APNS server cant send message to IOS");
					pushManager.stopConnection();
				}
				pushManager.stopConnection();
			}else {
				pushManager.stopConnection();
				System.out.println("cannot send message to APNS server");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
}