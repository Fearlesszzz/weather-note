package com.example.rain.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.rain.MainActivity;
import com.example.rain.R;

public class MyService extends Service {
    public MyService() { }
    static int notificationId=1;
    //如果服务是提供给自有应用专用的，
    // 并且Service(服务端)与客户端相同的进程中运行
    //使用Binder技术绑定。则应通过扩展 Binder 类并从 onBind() 返回它的一个实例来创建接口。
    // 客户端收到 Binder 后，可利用它直接访问 Binder 实现中方法（业务）
    //不采用Binder方式创建接口的唯一原因是，服务被其他应用或不同的进程调用。
    //创建服务代理类，服务可提供的业务逻辑代码可以写在其方法中
    public class MyBinder extends Binder {
        public void operate1(){
            //新的线程
            new Thread(new Runnable() {
              @Override
              public void run(){
                  try {
                      Thread.sleep(1000);
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }

                  // 点击通知后，进入的主界面
                  Intent activityIntent = new Intent(
                          MyService.this, MainActivity.class);
                  activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                          | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                  PendingIntent pendingIntent = PendingIntent.getActivity(
                          MyService.this, 0,activityIntent, 0);
                  //获取channelid
                  String channelId=getString(R.string.channel_Id);
                  NotificationCompat.Builder builder = new NotificationCompat.Builder(
                          MyService.this, channelId)
                          .setSmallIcon(R.drawable.add)
                          .setContentTitle("新的一天")
                          .setContentText("写点什么吧")
                          .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                          .setContentIntent(pendingIntent)
                          //setAutoCancel(true):用户点击通知后，自动移除通知
                          .setAutoCancel(true);
                  //NotificationManagerCompat.from(Context context)
                  //context可以是activity、fragment和service等
                  NotificationManagerCompat notificationManager =
                          NotificationManagerCompat.from(MyService.this);
                  // notificationId：对每个通知，指定一个唯一的整数值
                  notificationManager.notify(notificationId, builder.build());
              }
          }).start();
        }
    }
    //以绑定方式启动服务时，执行onBind方法，
    // 类似于onCreate，它最多执行一次，服务会缓存OnBind方法返回的IBinder对象
    // 多个客户端多次绑定服务时，同一个IBinder对象会传递给客户端，
    // 即：ServiceBindConnection类onServiceConnected的第二个参数IBinder。
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("绑定服务方式启动服务","调用onBind");
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("onCreate","onCreate");
    }
}