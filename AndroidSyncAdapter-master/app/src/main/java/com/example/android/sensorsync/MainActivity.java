package com.example.android.sensorsync;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.sensorsync.Sync.SensorUtils;

public class MainActivity extends AppCompatActivity {

    TextView ms1,ms2,ms3,ms4,ms5,ms6;
    //int a;
    public static MainActivity mainActivity1;

    public MainActivity(){
        mainActivity1 = this;
        //this.a = 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Intent intent = new Intent(this, SensorService.class);
        //startService(intent);




        ms1 = findViewById(R.id.ms_x);
        ms2 = findViewById(R.id.ms_y);
        ms3 = findViewById(R.id.ms_z);

        ms4 = findViewById(R.id.ms_gx);
        ms5 = findViewById(R.id.ms_gy);
        ms6 = findViewById(R.id.ms_gz);


        /**
         * Create SyncAccount at launch, if needed.
         *
         * <p>This will create a new account with the system for our application, register our
         * SyncService with it, and establish a sync schedule.
         */
        SensorUtils.CreateSyncAccount(this);

        /**
         * This will start the service for collection of sensor data into the SQLite DB
         */
        SensorUtils.initialize(this);
    }

    public void setText(int index, float a) {
        if(index==1){
            ms1.setText("X：" + (float) (a / 100));
        }else if (index==2){
            ms2.setText("Y：" + (float) (a / 100));
        }else if (index == 3) {
            ms3.setText("Z：" + (float) (a / 100));
        }else if (index == 4) {
            ms4.setText(" X Angular velocity\n" + (float) a);
        }else if (index == 5) {
            ms5.setText(" Y Angular velocity\n" + (float) a);
        }else if (index == 6) {
            ms6.setText(" Z Angular velocity\n" + (float) a);
        }

    }

    public static MainActivity getMainActivity(){
        return mainActivity1;
    }

}
