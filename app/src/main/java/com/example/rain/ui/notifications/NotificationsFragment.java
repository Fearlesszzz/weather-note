package com.example.rain.ui.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.rain.R;
import com.example.rain.service.MyService;

public class NotificationsFragment extends Fragment {

    private RadioGroup radioGroup;
    private RadioGroup radioGroup2;
    private RadioButton offButton;
    private RadioButton onButton;
    private RadioButton onButtonTime;
    private RadioButton offButtonTime;
    private MyService.MyBinder myBinder;//包含业务逻辑
    //用于连接服务
    private ServiceBindConnection serviceBindConnection;
    private Intent intent;
    private static final String enableService = "enableService_shp";
    private static final String enable = "enable_shp";
    private static final String displayTime = "displayTime_shp";
    private static final String display = "display_shp";
    class ServiceBindConnection implements ServiceConnection {
        //成功绑定服务时被自动调用，参数IBinder service的值为：
        // MyService服务类的onBind方法的返回值(会被缓存)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder=( MyService.MyBinder ) service;
        }
        //当服务失去连接时调用
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("服务失去连接","服务失去连接");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        radioGroup = root.findViewById(R.id.service);
        radioGroup2 = root.findViewById(R.id.display_time);
        offButton = root.findViewById(R.id.off);
        onButton = root.findViewById(R.id.on);
        onButtonTime = root.findViewById(R.id.onButtonTime);
        offButtonTime = root.findViewById(R.id.offButtonTime);
        //创建连接对象
        if(serviceBindConnection==null)
            serviceBindConnection=new ServiceBindConnection();
        intent=new Intent(getActivity(), MyService.class);
        //绑定服务，BIND_AUTO_CREATE表示如果服务未创建，则先创建服务
        getActivity().bindService(intent,serviceBindConnection,Context.BIND_AUTO_CREATE);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(enableService, Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences2 = requireActivity().getSharedPreferences(displayTime, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == onButton.getId()) {
                    editor.putBoolean(enable, true);
                    //执行服务业务逻辑，服务第一次绑定时，需要创建服务，需要一定时间，不能马上执行服务业务的调用
                    if(myBinder==null)
                        Log.i("绑定服务方式启动服务","服务尚未启动完成！");
                    else{
                        Log.i("绑定服务方式启动服务","服务启动完成！");
                        myBinder.operate1();
                    }
                }
                if(checkedId == offButton.getId()) {
                    editor.putBoolean(enable, false);
                }
                editor.apply();
            }
        });
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == onButtonTime.getId()) {
                    editor2.putBoolean(display, true);
                }
                if (checkedId == offButtonTime.getId()) {
                    editor2.putBoolean(display, false);
                }
                editor2.apply();
            }
        });
        createNotificationChannel();

        if (sharedPreferences.getBoolean(enable, true)) {
            onButton.setChecked(true);
        }else {
            offButton.setChecked(true);
        }
        if (sharedPreferences2.getBoolean(display, true)) {
            onButtonTime.setChecked(true);
        }else {
            offButtonTime.setChecked(true);
        }
        return root;
    }

    //在组件死亡之前，要解绑服务
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceBindConnection != null)
            getActivity().unbindService(serviceBindConnection);//解绑服务
    }

    //创建通知Channel
    private void createNotificationChannel() {
        //API 26+才需要创建通知Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = this.getString(R.string.channel_name);
            String description = this.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            //channel_Id为用户自定义的channel名称
            NotificationChannel channel = new NotificationChannel(
                    getString(R.string.channel_Id), name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = requireContext().getSystemService(
                    NotificationManager.class);
            //注册channel
            notificationManager.createNotificationChannel(channel);
        }
    }
}