package game.tools.push.ios;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import game.tools.log.LogUtil;
import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.notification.PushedNotification;
import javapns.notification.ResponsePacket;

public class NotificationEngine  {
    // 10 个线程在后台发送通知
    public static final int EXCE_THREAD_POOL_SIZE = 10;

    private void shutdownEngine()
    {
        System.out.println("------------shutdownEngine-----------------------------");
        // 关闭 ExecutorService
        if(!exec.isShutdown())
        {
            exec.shutdown();
        }
    }
    
    public void init(){
        // 注册 jvm 关闭时操作
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run() 
            {
            	NotificationEngine.this.shutdownEngine();
            }
        });
    }
    
    public void destroy()
    {
    	
        Thread t = new Thread()
        {
            public void run() 
            {
                NotificationEngine.this.shutdownEngine();
            }
        };
        t.start();
    }
    
    private ExecutorService exec = Executors.newFixedThreadPool(EXCE_THREAD_POOL_SIZE);
    
    private FutureTask<List<PushedNotification>> doSendNotificationList(final String message, final int badge, final String sound, final String keystore, final String password, final boolean production, final List<String> tokens){    
        // 带返回结果发送动作
        FutureTask<List<PushedNotification>> future = new FutureTask<List<PushedNotification>>(
                new Callable<List<PushedNotification>>() {
                    public List<PushedNotification> call() {
                        List<PushedNotification> notifications = null;
                        try {
                            notifications = Push.combined(message,badge,sound,keystore,password,production,tokens);
                        } catch (CommunicationException e) {
                            e.printStackTrace();
                        } catch (KeystoreException e) {
                            e.printStackTrace();
                        }
                        return notifications;
                    }
                });
        // 提交到执行框架执行
        exec.submit(future);
        return future;
    }
    
    public void sendNotificationList(final String message, final int badge, final String sound, final String keystore, final String password, final boolean production, final List<String> tokens)
    {    
        FutureTask<List<PushedNotification>> futrue = doSendNotificationList(message, badge,sound,keystore,password,production,tokens);
        try 
        {
            // 阻塞至接收到返回结果。
            List<PushedNotification> list = futrue.get();
            for (PushedNotification notification : list) 
            {
                if (notification.isSuccessful()) 
                {
                    /* Apple accepted the notification and should deliver it */  
                    System.out.println("Push notification sent successfully to: " + notification.getDevice().getToken());
                    /* Still need to query the Feedback Service regularly */  
                } 
                else 
                {
                    String invalidToken = notification.getDevice().getToken();
                    /* Add code here to remove invalidToken from your database */  

                    /* Find out more about what the problem was */  
                    Exception theProblem = notification.getException();
                    //theProblem.printStackTrace();
                    LogUtil.info(theProblem.toString());

                    /* If the problem was an error-response packet returned by Apple, get it */  
                    ResponsePacket theErrorResponse = notification.getResponse();
                    if (theErrorResponse != null) 
                    {
                    	 LogUtil.info(theErrorResponse.getMessage());
                    }
                }
            }
        }
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        } 
        catch (ExecutionException e) 
        {
            e.printStackTrace();
        }
        
    }
}