package com.example.android.autocoach;


import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

import com.example.android.autocoach.Sync.SensorReaderUtils;

public class MainActivity extends AppCompatActivity {

    TextView message_x,message_y,message_z,message_gx,message_gy,message_gz;
    //int a;
    public static MainActivity mainActivity;

    private LocationManager locationManager;

    private String provider;

    private int speed;
    private boolean flag;



    public MainActivity(){
        mainActivity = this;
        //this.a = 2;

        RunnableD R1SVM = new RunnableD( "Thread-1");
        R1SVM.start();

        RunnableD R2LDA = new RunnableD( "Thread-2");
        R2LDA.start();

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

        /**
         * Create SyncAccount at launch, if needed.
         *
         * <p>This will create a new account with the system for our application, register our
         * SyncService with it, and establish a sync schedule.
         */
        SensorReaderUtils.CreateSyncAccount(this);

        /**
         * This will start the service for collection of sensor data into the SQLite DB
         */
        SensorReaderUtils.initialize(this);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            // 关闭程序时将监听器移除
            locationManager.removeUpdates(locationListener);
        }
    }

    //locationListener中其他3个方法新手不太用得到，笔者在此也不多说了，有兴趣的可以自己去了解一下
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
            // 更新当前设备的位置信息
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

    public static MainActivity getMainActivity(){
        return mainActivity;
    }

    class RunnableD implements Runnable {
        private Thread t;
        private String threadName;

        RunnableD( String name) {
            threadName = name;
            System.out.println("Creating " +  threadName );
        }

        public void run() {
            System.out.println("Running " +  threadName );
            try {
                for(;;) {
                    //System.out.println("Thread: " + threadName + ", " + i);
                    // 让线程睡眠一会
                    Thread.sleep(500);
                }
            }catch (InterruptedException e) {
                System.out.println("Thread " +  threadName + " interrupted.");
            }
            System.out.println("Thread " +  threadName + " exiting.");
        }

        public void start () {
            System.out.println("Starting " +  threadName );
            if (t == null) {
                t = new Thread (this, threadName);
                t.start ();
            }
        }
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



//    public class TestThread {
//
//        public static void main(String args[]) {
//            RunnableDemo R1 = new RunnableDemo( "Thread-1");
//            R1.start();
//
//            RunnableDemo R2 = new RunnableDemo( "Thread-2");
//            R2.start();
//        }
//    }

}
