package game.tools.push.ios;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;

public class PushPNS {
    
    public static void main(String[] args) throws Exception {
        
        // 设备的 Token 值
        String deviceToken = "94d1a2774a773c8134b8a401742edd2accf5d3bbf7156e5fc2aef63acf963581";
        // push的内容
        String alert = "我的push测试";
        // 图标小红圈的数值
        int badge = 1;
        // 铃音
        String sound = "default";

        List<String> tokens = new ArrayList<String>();
        tokens.add(deviceToken);
        
        // 推送证书的路径
        String certificatePath = "conf/cert_dev.p12";
        // 证书的密码
        String certificatePassword = "a1234";// 此处注意导出的证书密码不能为空因为空密码会报错
        boolean sendCount = true;

        try {
            
            PushNotificationPayload payLoad = new PushNotificationPayload();
            payLoad.addAlert(alert);         // 消息内容
            payLoad.addBadge(badge);         // iphone应用图标上小红圈上的数值、
            
            if (!StringUtils.isBlank(sound)) {
                payLoad.addSound(sound);    // 铃音
            }
            PushNotificationManager pushManager = new PushNotificationManager();
            // true：表示的是产品发布推送服务 false：表示的是产品测试推送服务
            pushManager
                    .initializeConnection(new AppleNotificationServerBasicImpl(
                            certificatePath, certificatePassword, false));
            List<PushedNotification> notifications = new ArrayList<PushedNotification>();
            // 发送push消息
            if (sendCount) {
                
                Device device = new BasicDevice();
                device.setToken(tokens.get(0));
                PushedNotification notification = pushManager.sendNotification(
                        device, payLoad, true);
                notifications.add(notification);
            }
            else {
                
                List<Device> device = new ArrayList<Device>();
                
                for (String token : tokens) {
                    
                    device.add(new BasicDevice(token));
                }
                
                notifications = pushManager.sendNotifications(payLoad, device);
            }
            List<PushedNotification> failedNotifications = PushedNotification
                    .findFailedNotifications(notifications);
            List<PushedNotification> successfulNotifications = PushedNotification
                    .findSuccessfulNotifications(notifications);
            int failed = failedNotifications.size();
            int successful = successfulNotifications.size();
            
            pushManager.stopConnection();
            
            System.out.println("successful = " + successful + " failed " + failed);
        }
        catch (Exception e) {
            
            e.printStackTrace();
        }
    }
}