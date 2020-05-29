package com.example.autocoach;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autocoach.Bean.Event;
import com.example.autocoach.Sync.FeedbackService;
import com.example.autocoach.Sync.FileUtils;
import com.example.autocoach.Sync.SensorReaderUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String provider;
    private int speed;

    public static MainActivity mainActivity;
    private LocationManager locationManager;

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
            //nothing
        }
    };

    private MyReceiver detect_recevier;

    private TextView currentscore;
    private TextView currentscore_n;
    private TextView totalcoins;
    private TextView totalcoins_n;
    private TextView tripscore;
    private TextView tripscore_n;
    private TextView add_coins_n;
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
    private ImageView plus_icon;

    private ImageView brake_icon;
    private ImageView turn_icon;
    private ImageView swerve_icon;
    private ImageView acc_icon;
    private ImageView car;
    private ImageView coinsbox;
    private ImageView feedback_icon;
    private ImageView add_coins;

    public MainActivity(){
        mainActivity = this;
    }

    public static MainActivity getMainActivity(){
        return mainActivity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //initial the stuffs
        currentscore_n=(TextView)findViewById(R.id.score_n);
        totalcoins_n=(TextView)findViewById(R.id.totalcoins_n);
        tripscore_n=(TextView)findViewById(R.id.tripscore_n);
        currentscore=(TextView)findViewById(R.id.currentscore);
        totalcoins=(TextView)findViewById(R.id.totalcoins);
        tripscore=(TextView)findViewById(R.id.tripscore);
        add_coins_n=(TextView)findViewById(R.id.add_coins_n);
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
        add_coins=(ImageView) findViewById(R.id.add_coins);
        plus_icon=(ImageView) findViewById(R.id.plus_icon);
        add_coins.setVisibility(View.INVISIBLE);
        add_coins_n.setVisibility(View.INVISIBLE);
        plus_icon.setVisibility(View.INVISIBLE);



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
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
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

    public class MyReceiver extends BroadcastReceiver {

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
    protected void onResume() {
        super.onResume();
        initPermission();//针对6.0以上版本做权限适配
    }

    private void initPermission() {
        //检查权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 1);
        }

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
//            //请求权限
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
//        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
//                &&ActivityCompat.checkSelfPermission(this, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS) != PackageManager.PERMISSION_GRANTED) {
//            //请求权限
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 1);
//        }
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

    public void initial_carandcoinbox(){
        car.setVisibility(View.VISIBLE);
        coinsbox.setVisibility(View.VISIBLE);
        String overall = String.valueOf(0);
        String coin = String.valueOf(0);
        String trip = String.valueOf(0);
        currentscore_n.setText(overall);
        totalcoins_n.setText(coin);
        tripscore_n.setText(trip);
        currentscore.setVisibility(View.VISIBLE);
        totalcoins.setVisibility(View.VISIBLE);
        tripscore.setVisibility(View.VISIBLE);


    }
    public void change_tripscore(int trip_score){
        String trip = String.valueOf(trip_score);
        tripscore_n.setText(trip);
    }
    public void change_currentscore(int current_score){
        String current = String.valueOf(current_score);
        currentscore_n.setText(current);
    }
    public void change_totalcoins(int coins){
        String  coin = String.valueOf(coins);
        totalcoins_n.setText(coin);
    }

    public void display_bar(String type,int score){
        switch (type) {
            case "acc":
                change_acc_bar(score);
                break;
            case "brake":
                change_brake_bar(score);
                break;
            case "turn":
                change_turn_bar(score);
                break;
            case "swerve":
                change_swerve_bar(score);
                break;
        }
    }


    public void setFeedback_icon(int feedbackIndex){
        switch (feedbackIndex){
            case -1:
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        feedback_icon.setImageResource(R.drawable.zan1);
                    }
                });
                break;
            case 0:
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        feedback_icon.setImageResource(R.drawable.acc_red);
                    }
                });
                break;
            case 1:
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        feedback_icon.setImageResource(R.drawable.brake_red);
                    }
                });
                break;
            case 2:
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        feedback_icon.setImageResource(R.drawable.turn_red);
                    }
                });
                break;
            case 3:
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        feedback_icon.setImageResource(R.drawable.swerve_red);
                    }
                });
                break;
        }
    }

    public void initial_acc(){
        acc_bar_1.setImageResource(R.drawable.a0);
        acc_bar_2.setImageResource(R.drawable.a0);
        acc_bar_3.setImageResource(R.drawable.a0);
        acc_bar_4.setImageResource(R.drawable.a0);
        acc_bar_5.setImageResource(R.drawable.a0);
        acc_bar_6.setImageResource(R.drawable.a0);
        acc_bar_7.setImageResource(R.drawable.a0);
        acc_bar_8.setImageResource(R.drawable.a0);
        acc_bar_9.setImageResource(R.drawable.a0);
        acc_bar_10.setImageResource(R.drawable.a0);
        acc_icon.setVisibility(View.VISIBLE);
    }
    public void initial_brake(){
        brake_bar_1.setImageResource(R.drawable.a0);
        brake_bar_2.setImageResource(R.drawable.a0);
        brake_bar_3.setImageResource(R.drawable.a0);
        brake_bar_4.setImageResource(R.drawable.a0);
        brake_bar_5.setImageResource(R.drawable.a0);
        brake_bar_6.setImageResource(R.drawable.a0);
        brake_bar_7.setImageResource(R.drawable.a0);
        brake_bar_8.setImageResource(R.drawable.a0);
        brake_bar_9.setImageResource(R.drawable.a0);
        brake_bar_10.setImageResource(R.drawable.a0);
        brake_icon.setVisibility(View.VISIBLE);
    }
    public void initial_turn(){
        turn_bar_1.setImageResource(R.drawable.a0);
        turn_bar_2.setImageResource(R.drawable.a0);
        turn_bar_3.setImageResource(R.drawable.a0);
        turn_bar_4.setImageResource(R.drawable.a0);
        turn_bar_5.setImageResource(R.drawable.a0);
        turn_bar_6.setImageResource(R.drawable.a0);
        turn_bar_7.setImageResource(R.drawable.a0);
        turn_bar_8.setImageResource(R.drawable.a0);
        turn_bar_9.setImageResource(R.drawable.a0);
        turn_bar_10.setImageResource(R.drawable.a0);
        turn_icon.setVisibility(View.VISIBLE);
    }

    public void initial_swerve(){
        swerve_bar_1.setImageResource(R.drawable.a0);
        swerve_bar_2.setImageResource(R.drawable.a0);
        swerve_bar_3.setImageResource(R.drawable.a0);
        swerve_bar_4.setImageResource(R.drawable.a0);
        swerve_bar_5.setImageResource(R.drawable.a0);
        swerve_bar_6.setImageResource(R.drawable.a0);
        swerve_bar_7.setImageResource(R.drawable.a0);
        swerve_bar_8.setImageResource(R.drawable.a0);
        swerve_bar_9.setImageResource(R.drawable.a0);
        swerve_bar_10.setImageResource(R.drawable.a0);
        swerve_icon.setVisibility(View.VISIBLE);
    }

    public void change_acc_bar(int score) {
        initial_acc();
        if (score <= 100&&score>=90) {
            acc_bar_10.setImageResource(R.drawable.a10);
        } else if (score <= 90&&score>=80) {
            acc_bar_10.setImageResource(R.drawable.a10);
            acc_bar_9.setImageResource(R.drawable.a9);
        } else if (score <= 80&&score>=70) {
            acc_bar_10.setImageResource(R.drawable.a10);
            acc_bar_9.setImageResource(R.drawable.a9);
            acc_bar_8.setImageResource(R.drawable.a8);
        } else if (score <= 70&&score>=60) {
            acc_bar_10.setImageResource(R.drawable.a10);
            acc_bar_9.setImageResource(R.drawable.a9);
            acc_bar_8.setImageResource(R.drawable.a8);
            acc_bar_7.setImageResource(R.drawable.a7);
        } else if (score <= 60&&score>=50) {
            acc_bar_10.setImageResource(R.drawable.a10);
            acc_bar_9.setImageResource(R.drawable.a9);
            acc_bar_8.setImageResource(R.drawable.a8);
            acc_bar_7.setImageResource(R.drawable.a7);
            acc_bar_6.setImageResource(R.drawable.a6);
        } else if (score <= 50&&score>=40) {
            acc_bar_10.setImageResource(R.drawable.a10);
            acc_bar_9.setImageResource(R.drawable.a9);
            acc_bar_8.setImageResource(R.drawable.a8);
            acc_bar_7.setImageResource(R.drawable.a7);
            acc_bar_6.setImageResource(R.drawable.a6);
            acc_bar_5.setImageResource(R.drawable.a5);
        } else if (score <= 40&&score>=30) {
            acc_bar_10.setImageResource(R.drawable.a10);
            acc_bar_9.setImageResource(R.drawable.a9);
            acc_bar_8.setImageResource(R.drawable.a8);
            acc_bar_7.setImageResource(R.drawable.a7);
            acc_bar_6.setImageResource(R.drawable.a6);
            acc_bar_5.setImageResource(R.drawable.a5);
            acc_bar_4.setImageResource(R.drawable.a4);
        } else if (score <= 30&&score>=20) {
            acc_bar_10.setImageResource(R.drawable.a10);
            acc_bar_9.setImageResource(R.drawable.a9);
            acc_bar_8.setImageResource(R.drawable.a8);
            acc_bar_7.setImageResource(R.drawable.a7);
            acc_bar_6.setImageResource(R.drawable.a6);
            acc_bar_5.setImageResource(R.drawable.a5);
            acc_bar_4.setImageResource(R.drawable.a4);
            acc_bar_3.setImageResource(R.drawable.a3);
        } else if (score <= 20&&score>=10) {
            acc_bar_10.setImageResource(R.drawable.a10);
            acc_bar_9.setImageResource(R.drawable.a9);
            acc_bar_8.setImageResource(R.drawable.a8);
            acc_bar_7.setImageResource(R.drawable.a7);
            acc_bar_6.setImageResource(R.drawable.a6);
            acc_bar_5.setImageResource(R.drawable.a5);
            acc_bar_4.setImageResource(R.drawable.a4);
            acc_bar_3.setImageResource(R.drawable.a3);
            acc_bar_2.setImageResource(R.drawable.a2);
        } else if (score <= 10) {
            acc_bar_10.setImageResource(R.drawable.a10);
            acc_bar_9.setImageResource(R.drawable.a9);
            acc_bar_8.setImageResource(R.drawable.a8);
            acc_bar_7.setImageResource(R.drawable.a7);
            acc_bar_6.setImageResource(R.drawable.a6);
            acc_bar_5.setImageResource(R.drawable.a5);
            acc_bar_4.setImageResource(R.drawable.a4);
            acc_bar_3.setImageResource(R.drawable.a3);
            acc_bar_2.setImageResource(R.drawable.a2);
            acc_bar_1.setImageResource(R.drawable.a1);

        }
    }

    public void change_brake_bar(int score){
        initial_brake();
        if (score <= 100&&score>=90) {
            brake_bar_10.setImageResource(R.drawable.a10);
        } else if (score <= 90&&score>=80) {
            brake_bar_10.setImageResource(R.drawable.a10);
            brake_bar_9.setImageResource(R.drawable.a9);
        } else if (score <= 80&&score>=70) {
            brake_bar_10.setImageResource(R.drawable.a10);
            brake_bar_9.setImageResource(R.drawable.a9);
            brake_bar_8.setImageResource(R.drawable.a8);
        } else if (score <= 70&&score>=60) {
            brake_bar_10.setImageResource(R.drawable.a10);
            brake_bar_9.setImageResource(R.drawable.a9);
            brake_bar_8.setImageResource(R.drawable.a8);
            brake_bar_7.setImageResource(R.drawable.a7);
        } else if (score <= 60&&score>=50) {
            brake_bar_10.setImageResource(R.drawable.a10);
            brake_bar_9.setImageResource(R.drawable.a9);
            brake_bar_8.setImageResource(R.drawable.a8);
            brake_bar_7.setImageResource(R.drawable.a7);
            brake_bar_6.setImageResource(R.drawable.a6);
        } else if (score <= 50&&score>=40) {
            brake_bar_10.setImageResource(R.drawable.a10);
            brake_bar_9.setImageResource(R.drawable.a9);
            brake_bar_8.setImageResource(R.drawable.a8);
            brake_bar_7.setImageResource(R.drawable.a7);
            brake_bar_6.setImageResource(R.drawable.a6);
            brake_bar_5.setImageResource(R.drawable.a5);
        } else if (score <= 40&&score>=30) {
            brake_bar_10.setImageResource(R.drawable.a10);
            brake_bar_9.setImageResource(R.drawable.a9);
            brake_bar_8.setImageResource(R.drawable.a8);
            brake_bar_7.setImageResource(R.drawable.a7);
            brake_bar_6.setImageResource(R.drawable.a6);
            brake_bar_5.setImageResource(R.drawable.a5);
            brake_bar_4.setImageResource(R.drawable.a4);
        } else if (score <= 30&&score>=20) {
            brake_bar_10.setImageResource(R.drawable.a10);
            brake_bar_9.setImageResource(R.drawable.a9);
            brake_bar_8.setImageResource(R.drawable.a8);
            brake_bar_7.setImageResource(R.drawable.a7);
            brake_bar_6.setImageResource(R.drawable.a6);
            brake_bar_5.setImageResource(R.drawable.a5);
            brake_bar_4.setImageResource(R.drawable.a4);
            brake_bar_3.setImageResource(R.drawable.a3);
        } else if (score <= 20&&score>=10) {
            brake_bar_10.setImageResource(R.drawable.a10);
            brake_bar_9.setImageResource(R.drawable.a9);
            brake_bar_8.setImageResource(R.drawable.a8);
            brake_bar_7.setImageResource(R.drawable.a7);
            brake_bar_6.setImageResource(R.drawable.a6);
            brake_bar_5.setImageResource(R.drawable.a5);
            brake_bar_4.setImageResource(R.drawable.a4);
            brake_bar_3.setImageResource(R.drawable.a3);
            brake_bar_2.setImageResource(R.drawable.a2);
        } else if (score <= 10) {
            brake_bar_10.setImageResource(R.drawable.a10);
            brake_bar_9.setImageResource(R.drawable.a9);
            brake_bar_8.setImageResource(R.drawable.a8);
            brake_bar_7.setImageResource(R.drawable.a7);
            brake_bar_6.setImageResource(R.drawable.a6);
            brake_bar_5.setImageResource(R.drawable.a5);
            brake_bar_4.setImageResource(R.drawable.a4);
            brake_bar_3.setImageResource(R.drawable.a3);
            brake_bar_2.setImageResource(R.drawable.a2);
            brake_bar_1.setImageResource(R.drawable.a1);

        }
    }

    public void change_turn_bar(int score) {
        initial_turn();
        if (score <= 100&&score>=90) {
            turn_bar_10.setImageResource(R.drawable.a10);
        } else if (score <= 90&&score>=80) {
            turn_bar_10.setImageResource(R.drawable.a10);
            turn_bar_9.setImageResource(R.drawable.a9);
        } else if (score <= 80&&score>=70) {
            turn_bar_10.setImageResource(R.drawable.a10);
            turn_bar_9.setImageResource(R.drawable.a9);
            turn_bar_8.setImageResource(R.drawable.a8);
        } else if (score <= 70&&score>=60) {
            turn_bar_10.setImageResource(R.drawable.a10);
            turn_bar_9.setImageResource(R.drawable.a9);
            turn_bar_8.setImageResource(R.drawable.a8);
            turn_bar_7.setImageResource(R.drawable.a7);
        } else if (score <= 60&&score>=50) {
            turn_bar_10.setImageResource(R.drawable.a10);
            turn_bar_9.setImageResource(R.drawable.a9);
            turn_bar_8.setImageResource(R.drawable.a8);
            turn_bar_7.setImageResource(R.drawable.a7);
            turn_bar_6.setImageResource(R.drawable.a6);
        } else if (score <= 50&&score>=40) {
            turn_bar_10.setImageResource(R.drawable.a10);
            turn_bar_9.setImageResource(R.drawable.a9);
            turn_bar_8.setImageResource(R.drawable.a8);
            turn_bar_7.setImageResource(R.drawable.a7);
            turn_bar_6.setImageResource(R.drawable.a6);
            turn_bar_5.setImageResource(R.drawable.a5);
        } else if (score <= 40&&score>=30) {
            turn_bar_10.setImageResource(R.drawable.a10);
            turn_bar_9.setImageResource(R.drawable.a9);
            turn_bar_8.setImageResource(R.drawable.a8);
            turn_bar_7.setImageResource(R.drawable.a7);
            turn_bar_6.setImageResource(R.drawable.a6);
            turn_bar_5.setImageResource(R.drawable.a5);
            turn_bar_4.setImageResource(R.drawable.a4);
        } else if (score <= 30&&score>=20) {
            turn_bar_10.setImageResource(R.drawable.a10);
            turn_bar_9.setImageResource(R.drawable.a9);
            turn_bar_8.setImageResource(R.drawable.a8);
            turn_bar_7.setImageResource(R.drawable.a7);
            turn_bar_6.setImageResource(R.drawable.a6);
            turn_bar_5.setImageResource(R.drawable.a5);
            turn_bar_4.setImageResource(R.drawable.a4);
            turn_bar_3.setImageResource(R.drawable.a3);
        } else if (score <= 20&&score>=10) {
            turn_bar_10.setImageResource(R.drawable.a10);
            turn_bar_9.setImageResource(R.drawable.a9);
            turn_bar_8.setImageResource(R.drawable.a8);
            turn_bar_7.setImageResource(R.drawable.a7);
            turn_bar_6.setImageResource(R.drawable.a6);
            turn_bar_5.setImageResource(R.drawable.a5);
            turn_bar_4.setImageResource(R.drawable.a4);
            turn_bar_3.setImageResource(R.drawable.a3);
            turn_bar_2.setImageResource(R.drawable.a2);
        } else if (score <= 10) {
            turn_bar_10.setImageResource(R.drawable.a10);
            turn_bar_9.setImageResource(R.drawable.a9);
            turn_bar_8.setImageResource(R.drawable.a8);
            turn_bar_7.setImageResource(R.drawable.a7);
            turn_bar_6.setImageResource(R.drawable.a6);
            turn_bar_5.setImageResource(R.drawable.a5);
            turn_bar_4.setImageResource(R.drawable.a4);
            turn_bar_3.setImageResource(R.drawable.a3);
            turn_bar_2.setImageResource(R.drawable.a2);
            turn_bar_1.setImageResource(R.drawable.a1);

        }
    }
    public void change_swerve_bar(int score) {
        initial_swerve();
        if (score <= 100&&score>=90) {
            swerve_bar_10.setImageResource(R.drawable.a10);
        } else if (score <= 90&&score>=80) {
            swerve_bar_10.setImageResource(R.drawable.a10);
            swerve_bar_9.setImageResource(R.drawable.a9);
        } else if (score <= 80&&score>=70) {
            swerve_bar_10.setImageResource(R.drawable.a10);
            swerve_bar_9.setImageResource(R.drawable.a9);
            swerve_bar_8.setImageResource(R.drawable.a8);
        } else if (score <= 70&&score>=60) {
            swerve_bar_10.setImageResource(R.drawable.a10);
            swerve_bar_9.setImageResource(R.drawable.a9);
            swerve_bar_8.setImageResource(R.drawable.a8);
            swerve_bar_7.setImageResource(R.drawable.a7);
        } else if (score <= 60&&score>=50) {
            swerve_bar_10.setImageResource(R.drawable.a10);
            swerve_bar_9.setImageResource(R.drawable.a9);
            swerve_bar_8.setImageResource(R.drawable.a8);
            swerve_bar_7.setImageResource(R.drawable.a7);
            swerve_bar_6.setImageResource(R.drawable.a6);
        } else if (score <= 50&&score>=40) {
            swerve_bar_10.setImageResource(R.drawable.a10);
            swerve_bar_9.setImageResource(R.drawable.a9);
            swerve_bar_8.setImageResource(R.drawable.a8);
            swerve_bar_7.setImageResource(R.drawable.a7);
            swerve_bar_6.setImageResource(R.drawable.a6);
            swerve_bar_5.setImageResource(R.drawable.a5);
        } else if (score <= 40&&score>=30) {
            swerve_bar_10.setImageResource(R.drawable.a10);
            swerve_bar_9.setImageResource(R.drawable.a9);
            swerve_bar_8.setImageResource(R.drawable.a8);
            swerve_bar_7.setImageResource(R.drawable.a7);
            swerve_bar_6.setImageResource(R.drawable.a6);
            swerve_bar_5.setImageResource(R.drawable.a5);
            swerve_bar_4.setImageResource(R.drawable.a4);
        } else if (score <= 30&&score>=20) {
            swerve_bar_10.setImageResource(R.drawable.a10);
            swerve_bar_9.setImageResource(R.drawable.a9);
            swerve_bar_8.setImageResource(R.drawable.a8);
            swerve_bar_7.setImageResource(R.drawable.a7);
            swerve_bar_6.setImageResource(R.drawable.a6);
            swerve_bar_5.setImageResource(R.drawable.a5);
            swerve_bar_4.setImageResource(R.drawable.a4);
            swerve_bar_3.setImageResource(R.drawable.a3);
        } else if (score <= 20&&score>=10) {
            swerve_bar_10.setImageResource(R.drawable.a10);
            swerve_bar_9.setImageResource(R.drawable.a9);
            swerve_bar_8.setImageResource(R.drawable.a8);
            swerve_bar_7.setImageResource(R.drawable.a7);
            swerve_bar_6.setImageResource(R.drawable.a6);
            swerve_bar_5.setImageResource(R.drawable.a5);
            swerve_bar_4.setImageResource(R.drawable.a4);
            swerve_bar_3.setImageResource(R.drawable.a3);
            swerve_bar_2.setImageResource(R.drawable.a2);
        } else if (score <= 10) {
            swerve_bar_10.setImageResource(R.drawable.a10);
            swerve_bar_9.setImageResource(R.drawable.a9);
            swerve_bar_8.setImageResource(R.drawable.a8);
            swerve_bar_7.setImageResource(R.drawable.a7);
            swerve_bar_6.setImageResource(R.drawable.a6);
            swerve_bar_5.setImageResource(R.drawable.a5);
            swerve_bar_4.setImageResource(R.drawable.a4);
            swerve_bar_3.setImageResource(R.drawable.a3);
            swerve_bar_2.setImageResource(R.drawable.a2);
            swerve_bar_1.setImageResource(R.drawable.a1);

        }
    }

    public void downloadToast(){
        Toast toast = Toast.makeText(getApplicationContext(),
                "Downloading model files", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void downloadFinishToast(){
        Toast toast = Toast.makeText(getApplicationContext(),
                "finished", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void calibratedToast(){
        Toast toast = Toast.makeText(getApplicationContext(),
                "calibrated", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void preparedToast(){
        Toast toast = Toast.makeText(getApplicationContext(),
                "start", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void add_coins(int score) {
        add_coins.setImageResource(R.drawable.coin_add);
        plus_icon.setImageResource(R.drawable.plus_icon);
        String  coin = String.valueOf(score);
        add_coins_n.setText(coin);
    }

    public void getCoin(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                add_coins.setImageResource(R.drawable.coin_add);
//                plus_icon.setImageResource(R.drawable.plus_icon);
                String  coin = "1";
                add_coins_n.setText(coin);
                add_coins.setVisibility(View.VISIBLE);
                plus_icon.setVisibility(View.VISIBLE);
                add_coins_n.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    try {
                        Thread.sleep(10000);
                        add_coins.setVisibility(View.INVISIBLE);
                        plus_icon.setVisibility(View.INVISIBLE);
                        add_coins_n.setVisibility(View.INVISIBLE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

            }
        });
    }





}
