package com.example.android.autocoach;


import java.io.File;
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
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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

    private TextView currentscore;
    private TextView currentscore_n;
    private TextView totalcoins;
    private TextView totalcoins_n;
    private TextView tripscore;
    private TextView tripscore_n;
    private ImageView acc_bar_1;
    private ImageView acc_bar_2;
    private ImageView acc_bar_3;
    private ImageView acc_bar_4;
    private ImageView acc_bar_5;
    private ImageView acc_bar_6;
    private ImageView acc_bar_7;
    private ImageView acc_bar_8;
    private ImageView acc_bar_9;
    private ImageView acc_bar_10;
    private ImageView brake_bar_1;
    private ImageView brake_bar_2;
    private ImageView brake_bar_3;
    private ImageView brake_bar_4;
    private ImageView brake_bar_5;
    private ImageView brake_bar_6;
    private ImageView brake_bar_7;
    private ImageView brake_bar_8;
    private ImageView brake_bar_9;
    private ImageView brake_bar_10;
    private ImageView turn_bar_1;
    private ImageView turn_bar_2;
    private ImageView turn_bar_3;
    private ImageView turn_bar_4;
    private ImageView turn_bar_5;
    private ImageView turn_bar_6;
    private ImageView turn_bar_7;
    private ImageView turn_bar_8;
    private ImageView turn_bar_9;
    private ImageView turn_bar_10;
    private ImageView swerve_bar_1;
    private ImageView swerve_bar_2;
    private ImageView swerve_bar_3;
    private ImageView swerve_bar_4;
    private ImageView swerve_bar_5;
    private ImageView swerve_bar_6;
    private ImageView swerve_bar_7;
    private ImageView swerve_bar_8;
    private ImageView swerve_bar_9;
    private ImageView swerve_bar_10;

    private ImageView brake_icon;
    private ImageView turn_icon;
    private ImageView swerve_icon;
    private ImageView acc_icon;
    private ImageView car;
    private ImageView coinsbox;
    private ImageView feedback_icon;

    public MainActivity(){
        mainActivity = this;
    }

    public static MainActivity getMainActivity(){
        return mainActivity;
    }


    public String mkdirs(String path) {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //SD卡可挂载
            //获取扩展存储设备的文件目录
            File rootFile = Environment.getExternalStorageDirectory();
            String tmpFilePath = rootFile.getPath() + "/"+path;
            File tmpFile = new File(tmpFilePath);
            if (!tmpFile.exists()) {
                System.out.println("created");
                tmpFile.mkdir();
            } else {
                System.out.print("tmpFile exists");
            }
            return tmpFilePath;
        }
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        currentscore_n=(TextView)findViewById(R.id.score_n);
        totalcoins_n=(TextView)findViewById(R.id.totalcoins_n);
        tripscore_n=(TextView)findViewById(R.id.tripscore_n);
        currentscore=(TextView)findViewById(R.id.currentscore);
        totalcoins=(TextView)findViewById(R.id.totalcoins);
        tripscore=(TextView)findViewById(R.id.tripscore);
        car=(ImageView)findViewById(R.id.car);
        coinsbox=(ImageView)findViewById(R.id.coinsbox);

        feedback_icon=(ImageView)findViewById(R.id.feedback_icon);
        acc_bar_1=(ImageView)findViewById(R.id.acc_bar_1);
        acc_bar_2=(ImageView)findViewById(R.id.acc_bar_2);
        acc_bar_3=(ImageView)findViewById(R.id.acc_bar_3);
        acc_bar_4=(ImageView)findViewById(R.id.acc_bar_4);
        acc_bar_5=(ImageView)findViewById(R.id.acc_bar_5);
        acc_bar_6=(ImageView)findViewById(R.id.acc_bar_6);
        acc_bar_7=(ImageView)findViewById(R.id.acc_bar_7);
        acc_bar_8=(ImageView)findViewById(R.id.acc_bar_8);
        acc_bar_9=(ImageView)findViewById(R.id.acc_bar_9);
        acc_bar_10=(ImageView) findViewById(R.id.acc_bar_10);
        brake_bar_1=(ImageView) findViewById(R.id.brake_bar_1);
        brake_bar_2=(ImageView) findViewById(R.id.brake_bar_2);
        brake_bar_3=(ImageView) findViewById(R.id.brake_bar_3);
        brake_bar_4=(ImageView) findViewById(R.id.brake_bar_4);
        brake_bar_5=(ImageView) findViewById(R.id.brake_bar_5);
        brake_bar_6=(ImageView) findViewById(R.id.brake_bar_6);
        brake_bar_7=(ImageView) findViewById(R.id.brake_bar_7);
        brake_bar_8=(ImageView) findViewById(R.id.brake_bar_8);
        brake_bar_9=(ImageView) findViewById(R.id.brake_bar_9);
        brake_bar_10=(ImageView) findViewById(R.id.brake_bar_10);
        turn_bar_1=(ImageView) findViewById(R.id.turn_bar_1);
        turn_bar_2 =(ImageView) findViewById(R.id.turn_bar_2);
        turn_bar_3 =(ImageView) findViewById(R.id.turn_bar_3);
        turn_bar_4 =(ImageView) findViewById(R.id.turn_bar_4);
        turn_bar_5 =(ImageView) findViewById(R.id.turn_bar_5);
        turn_bar_6 =(ImageView) findViewById(R.id.turn_bar_6);
        turn_bar_7 =(ImageView) findViewById(R.id.turn_bar_7);
        turn_bar_8 =(ImageView) findViewById(R.id.turn_bar_8);
        turn_bar_9 =(ImageView) findViewById(R.id.turn_bar_9);
        turn_bar_10 =(ImageView) findViewById(R.id.turn_bar_10);
        swerve_bar_1=(ImageView) findViewById(R.id.swerve_bar_1);
        swerve_bar_2=(ImageView) findViewById(R.id.swerve_bar_2);
        swerve_bar_3=(ImageView) findViewById(R.id.swerve_bar_3);
        swerve_bar_4=(ImageView) findViewById(R.id.swerve_bar_4);
        swerve_bar_5=(ImageView) findViewById(R.id.swerve_bar_5);
        swerve_bar_6=(ImageView) findViewById(R.id.swerve_bar_6);
        swerve_bar_7=(ImageView) findViewById(R.id.swerve_bar_7);
        swerve_bar_8=(ImageView) findViewById(R.id.swerve_bar_8);
        swerve_bar_9=(ImageView) findViewById(R.id.swerve_bar_9);
        swerve_bar_10=(ImageView) findViewById(R.id.swerve_bar_10);
        brake_icon=(ImageView) findViewById(R.id.brake_icon);
        acc_icon=(ImageView) findViewById(R.id.acc_icon);
        turn_icon=(ImageView) findViewById(R.id.turn_icon);
        swerve_icon=(ImageView) findViewById(R.id.swerve_icon);


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


        // bind service 2 : svm lda feedback
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
