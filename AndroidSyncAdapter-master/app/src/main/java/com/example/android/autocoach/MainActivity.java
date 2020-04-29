package com.example.android.autocoach;


import java.util.List;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;

import com.example.android.autocoach.Bean.Event;
import com.example.android.autocoach.Sync.FeedbackService;
import com.example.android.autocoach.Sync.SensorReaderUtils;

public class MainActivity extends AppCompatActivity {
    private String provider;
    private int speed;
    private boolean flag;
    TextView message_x,message_y,message_z,message_gx,message_gy,message_gz;
    private Button start_button;
    private TextView feedbackText;

    public static MainActivity mainActivity;

    private LocationManager locationManager;

    private FeedbackService feedbackService;
    private Messenger toFeedbackMessenger = null;
    // service connection to bind feedback service: communicate with svm lda and feedback
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            System.out.println("service is connected");
//            feedbackService = ((FeedbackService.MyBinder)iBinder).getService();
            toFeedbackMessenger = new Messenger(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private MyReceiver detect_recevier;



    public MainActivity(){
        mainActivity = this;
    }

    public static MainActivity getMainActivity(){
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Intent intent = new Intent(this, SensorDetectService.class);
        //startService(intent);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 获取所有可用的位置提供器
        //如果GPS可以用就用GPS，GPS不能用则用网络
        //都不能用的情况下弹出Toast提示用户
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "No location provider to use",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //使用getLastKnownLocation就可以获取到记录当前位置信息的Location对象了
        //并且用showLocation()显示当前设备的位置信息
        //requestLocationUpdates用于设置位置监听器
        //此处监听器的时间间隔为5秒，距离间隔是5米
        //也就是说每隔5秒或者每移动5米，locationListener中会更新一下位置信息
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            //获取定位服务
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //获取当前可用的位置控制器
            List<String> list = locationManager.getProviders(true);

            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {

                updateSpeedByLocation(location);
            }
            locationManager.requestLocationUpdates(provider, 5000, 1,
                    locationListener);
        }

        message_x = findViewById(R.id.ms_x);
        message_y = findViewById(R.id.ms_y);
        message_z = findViewById(R.id.ms_z);

        message_gx = findViewById(R.id.ms_gx);
        message_gy = findViewById(R.id.ms_gy);
        message_gz = findViewById(R.id.ms_gz);

        feedbackText = findViewById(R.id.feedbackText);


        start_button = findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedbackService.startSVM();
                feedbackService.startLDA();
                feedbackService.startFeedback();
            }
        });
        Intent feedback_intent  = new Intent(this, FeedbackService.class);
        bindService(feedback_intent, serviceConnection, BIND_AUTO_CREATE);

        // this receiver is register to receive data from event detection
        detect_recevier = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.test.service.RECEIVER");
        registerReceiver(detect_recevier, intentFilter);

        /**
         * Create SyncAccount at launch, if needed.
         * <p>This will create a new account with the system for our application, register our
         * SyncService with it, and establish a sync schedule.
         */
        SensorReaderUtils.CreateSyncAccount(this);

        /**
         * This will start the service for collection of sensor data into the SQLite DB
         */
        SensorReaderUtils.initialize(this);
    }



    //locationListener
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            // update speed by current location
            updateSpeedByLocation(location);
        }


    };

    private void updateSpeedByLocation(Location location) {
        speed = (int) (location.getSpeed() * 3.6); // m/s --> Km/h

    }

    public int getSpeed() {
        return speed;
    }

    public void setText(int index, float a) {
        if(index==1){
            message_x.setText("X：" + (float) (a / 100));
        }else if (index==2){
            message_y.setText("Y：" + (float) (a / 100));
        }else if (index == 3) {
            message_z.setText("Z：" + (float) (a / 100));
        }else if (index == 4) {
            message_gx.setText(" X Angular velocity\n" + (float) a);
        }else if (index == 5) {
            message_gy.setText(" Y Angular velocity\n" + (float) a);
        }else if (index == 6) {
            message_gz.setText(" Z Angular velocity\n" + (float) a);
        }

    }

    public void setFeedbackText(String text){
        this.feedbackText.setText(text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPermission();//针对6.0以上版本做权限适配
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                flag = true;
            }
        } else {
            flag = true;
        }
    }

    public class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            assert bundle != null;
            Event driving_event = (Event)bundle.getSerializable("event");
            System.out.println("receive");
            System.out.println(driving_event.getFeature());

//            Bundle svmBundle = new Bundle();
//            svmBundle.putInt("test", 555);
            Message msg = Message.obtain(null,1,0);
            msg.setData(bundle);
            try{
                toFeedbackMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        unregisterReceiver(detect_recevier);
        if (locationManager != null) {
            // 关闭程序时将监听器移除
            locationManager.removeUpdates(locationListener);
        }
    }

}
