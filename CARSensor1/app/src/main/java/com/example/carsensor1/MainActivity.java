package com.example.carsensor1;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";


    private TextView ms1,ms2,ms3,ms4,ms5,ms6;
//    private TextView ms_x = (TextView) findViewById(R.id.ms_x);
//    private TextView ms_y = (TextView) findViewById(R.id.ms_y);
//    private TextView ms_z = (TextView) findViewById(R.id.ms_z);

    private SensorManager sensorManager;
    Sensor accelerometer;
    Sensor gyro;

    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp;

//    private SensorManager sm;
    private Button mWriteButton,mStopButton;
    private TextView mTextView;
    private Boolean doWrite = false;
    private int count=0;

    public MainActivity() throws IOException {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.d(TAG, "onCreate: initilizing sensor services");


//        sensorManager = (sensorManager) getSystemService(context.sensor)
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(MainActivity.this, accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(MainActivity.this, gyro,SensorManager.SENSOR_DELAY_NORMAL);


        Log.d(TAG, "onCreate: registed sensor accelerometer and gyro");

        ms1 = (TextView) findViewById(R.id.ms_x);
        ms2 = (TextView) findViewById(R.id.ms_y);
        ms3 = (TextView) findViewById(R.id.ms_z);

        ms4 = (TextView) findViewById(R.id.ms_gx);
        ms5 = (TextView) findViewById(R.id.ms_gy);
        ms6 = (TextView) findViewById(R.id.ms_gz);



    }

    @Override
    public void onSensorChanged(SensorEvent event) {


//        ms1.setText("X：" + (float) (Math.round(event.values[0] * 100)) / 100);
//        ms2.setText("Y：" + (float) (Math.round(event.values[1] * 100)) / 100) ;
//        ms3.setText("Z：" + (float) (Math.round(event.values[2] * 100)) / 100) ;


//        ms4.setText("X：" + (float) (Math.round(event.values[4] * 100)) / 100);
//        ms5.setText("Y：" + (float) (Math.round(event.values[5] * 100)) / 100) ;
//        ms6.setText("Z：" + (float) (Math.round(event.values[6] * 100)) / 100) ;



        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            ms1.setText("X：" + (float) (Math.round(event.values[0] * 100)) / 100);
            ms2.setText("Y：" + (float) (Math.round(event.values[1] * 100)) / 100) ;
            ms3.setText("Z：" + (float) (Math.round(event.values[2] * 100)) / 100) ;

        } else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            //需要将弧度转为角度
            float X = (float) Math.toDegrees(event.values[0]);
            float Y = (float) Math.toDegrees(event.values[1]);
            float Z = (float) Math.toDegrees(event.values[2]);
            
            ms4.setText(" X Angular velocity\n" + X);
            ms5.setText(" Y Angular velocity\n" + Y);
            ms6.setText(" Z Angular velocity\n" + Z);
        }



        //Log.d(TAG, "onSensorChanged: X:" + event.values[0] + " Y: " + event.values[1] + " Z: " + event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

//
//    /**
//     * 信息写入手机SD卡
//     * write into SD card
//     * @param message 信息
//     */
//    private void writeFileSdcard(String message) {
//        try {
//            // 如果手机插入了SD卡，而且应用程序具有访问SD的权限
//            // if SD card inserted, the application should have access to it
//            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                // 获取SD卡的目录
//                // gain directory of SD card
//                //File sdCardDir = Environment.getExternalStorageDirectory();
//                File SDCARD = Environment.getExternalStorageDirectory();
//
//                File targitFile = new File(SDCARD.getCanonicalPath() + "/SensorData"+count+".txt");
//                // 以指定文件创建 RandomAccessFile对象
//                // create RandomAccessFile object
//                RandomAccessFile raf = new RandomAccessFile(targitFile, "rw");
//                // 将文件记录指针移动到最后
//                // put the fire record pointer to the end
//                raf.seek(targitFile.length());
//                // 输出文件内容
//                // write file content
//                raf.write(message.getBytes());
//                // 关闭RandomAccessFile
//                // close RandomAccessFile
//                raf.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.Button_Write) {
//            doWrite = true;
//            count++;
//            mTextView.setText("Writing...SensorData" + count);
//        }
//        if (v.getId() == R.id.Button_Stop) {
//            doWrite = false;
//            mTextView.setText("SensorData" + count + " state：stop");
//        }
//    }


//    // 在SD卡目录下创建文件
//    File file = new File(Environment.getExternalStorageDirectory(), "mysdcard.txt");
//    Log.d(TAG, "file.exists():" + file.exists() + " file.getAbsolutePath():"+ file.getAbsolutePath());
//    if (file.exists()) {
//        file.delete();
//        file.createNewFile();
//    }
//    // Toast.makeText(MainActivity.this, "SD卡目录下创建文件成功...", Toast.LENGTH_LONG).show();
//    Log.d(TAG, "SD卡目录下创建文件成功...");
//
//
//    // 在SD卡目录下的文件，写入内容
//    FileWriter fw = new FileWriter(file);
//    fw.write("我的sdcard内容.....");
//    fw.close();
//    // Toast.makeText(MainActivity.this, "SD卡写入内容完成...",Toast.LENGTH_LONG).show();
//    Log.d(TAG, "SD卡写入内容完成...");


}
