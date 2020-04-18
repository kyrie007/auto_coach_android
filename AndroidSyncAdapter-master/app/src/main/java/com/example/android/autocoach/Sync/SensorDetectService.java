package com.example.android.autocoach.Sync;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import biz.source_code.dsp.filter.*;
import biz.source_code.dsp.filter.IirFilterCoefficients;
import biz.source_code.dsp.filter.IirFilterDesignExstrom;

import com.example.android.autocoach.MainActivity;
import com.example.android.autocoach.Database.SensorContract;

import java.util.ArrayList;

/**
 * Created by sandeepchawan on 2017-10-26.
 */

public class SensorDetectService extends Service implements SensorEventListener {

//    private TextView ms1,ms2,ms3,ms4,ms5,ms6;
    //private TextView mTextView;

    private SensorManager mSensorManager;
    private LocationManager locationManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mMagnetometer;
    private int count = 0;
    public ArrayList<ContentValues> values = new ArrayList<>();
    ContentValues value = new ContentValues();


    /**
     * a tag for logging
     */
    private static final String TAG = SensorDetectService.class.getSimpleName();

    public SensorDetectService() {

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //SENSOR_DELAY_FASTEST (10ms), SENSOR_DELAY_GAME(20ms), SENSOR_DELAY_UI(65ms), SENSOR_DELAY_NORMAL(200ms)
        Log.d(TAG, "In OnStartCommand\n");
        if (mAccelerometer != null)
            mSensorManager.registerListener(this, mAccelerometer, 50000);//(SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "Registered Accelerometer!\n");
        if (mGyroscope != null)
            mSensorManager.registerListener(this, mGyroscope, 50000);//SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "Registered Gyrometer!\n");
        if (mMagnetometer != null)
            mSensorManager.registerListener(this, mMagnetometer, 50000);//SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "Registered Magnetometer!\n");
        return START_STICKY;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //do nothing
    }

    public synchronized double[] IIRFilter(double[] signal, double[] a, double[] b) {

        double[] in = new double[b.length];
        double[] out = new double[a.length-1];

        double[] outData = new double[signal.length];

        for (int i = 0; i < signal.length; i++) {

            System.arraycopy(in, 0, in, 1, in.length - 1);
            in[0] = signal[i];

            //calculate y based on a and b coefficients
            //and in and out.
            float y = 0;
            for(int j = 0 ; j < b.length ; j++){
                y += b[j] * in[j];

            }

            for(int j = 0;j < a.length-1;j++){
                y -= a[j+1] * out[j];
            }

            //shift the out array
            System.arraycopy(out, 0, out, 1, out.length - 1);
            out[0] = y;

            outData[i] = y;


        }
        return outData;
    }


    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;

//        final double alpha = 0.8;
//        double gravity[] = new double[3];
//
//        if (sensor.getType() == Sensor.TYPE_GRAVITY) {
//            gravity[0] =  event.values[0];
//            gravity[1] =  event.values[2];
//            gravity[2] =  event.values[2];
//
//        }
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //Log.d(TAG, "Accelerometer changed at: " + System.currentTimeMillis() + " \n");
            //Log.d(TAG, "Count is: " + count +  "\n");
            //Log.d(TAG, "Accelerometer: " + String.valueOf(event.values[0]) + String.valueOf(event.values[1]) + String.valueOf(event.values[2]));

            //final float alpha = 0.8;
//            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
//            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
//            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            double accx = event.values[0]; //- gravity[0];
            double accy = event.values[1]; //- gravity[1];
            double accz = event.values[2]; //- gravity[2];

            value.put(SensorContract.SensorEntry.COLUMN_ACC_X, accx);
            value.put(SensorContract.SensorEntry.COLUMN_ACC_Y, accy);
            value.put(SensorContract.SensorEntry.COLUMN_ACC_Z, accz);

            MainActivity.getMainActivity().setText(1,(Math.round(accx * 100))/100);
            MainActivity.getMainActivity().setText(2,(Math.round(accy * 100))/100);
            MainActivity.getMainActivity().setText(3,(Math.round(accz * 100))/100);


//            //FILTER
//            double accx2 = accx,accy2 = accy,accz2 = accz;
//            IirFilterCoefficients iirFilterCoefficients;
//            iirFilterCoefficients = IirFilterDesignExstrom.design(FilterPassType.lowpass, 10,
//                    10.0 / 50, 13.0 / 50);
//            accx2  = IIRFilter(accx2, iirFilterCoefficients.a, iirFilterCoefficients.b);
//
//
//            value.put(SensorContract.SensorEntry.COLUMN_MAGNETO_X, d);
//            value.put(SensorContract.SensorEntry.COLUMN_MAGNETO_Y, e);
//            value.put(SensorContract.SensorEntry.COLUMN_MAGNETO_Z, f);
            count++;


        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            //Log.d(TAG, "Gyro changed at: " + System.currentTimeMillis() + " \n");
            //Log.d(TAG, "Gyro: " + String.valueOf(event.values[0]) + String.valueOf(event.values[1]) + String.valueOf(event.values[2]));
            value.put(SensorContract.SensorEntry.COLUMN_GYRO_X, (float) Math.toDegrees(event.values[0]));
            value.put(SensorContract.SensorEntry.COLUMN_GYRO_Y, (float) Math.toDegrees(event.values[1]));
            value.put(SensorContract.SensorEntry.COLUMN_GYRO_Z, (float) Math.toDegrees(event.values[2]));

//            if (Math.toDegrees(event.values[0]) > 10) { // anticlockwise
//                MainActivity.getMainActivity().getWindow().getDecorView().setBackgroundColor(Color.GREEN);
//            } else if (Math.toDegrees(event.values[0]) < 10 ) { // clockwise
//                MainActivity.getMainActivity().getWindow().getDecorView().setBackgroundColor(Color.RED);
//            }

//            if (Math.toDegrees(event.values[1]) > 10) { // anticlockwise
//                MainActivity.getMainActivity().getWindow().getDecorView().setBackgroundColor(Color.GREEN);
//            } else if (Math.toDegrees(event.values[1]) < 10 ) { // clockwise
//                MainActivity.getMainActivity().getWindow().getDecorView().setBackgroundColor(Color.RED);
//            }

            if (Math.toDegrees(event.values[2]) > 30) { // anticlockwise
                MainActivity.getMainActivity().getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            } else if (Math.toDegrees(event.values[2]) < 30 ) { // clockwise
                MainActivity.getMainActivity().getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
            }

            MainActivity.getMainActivity().setText(4,(float) Math.toDegrees(event.values[0]));
            MainActivity.getMainActivity().setText(5,(float) Math.toDegrees(event.values[1]));
            MainActivity.getMainActivity().setText(6,(float) Math.toDegrees(event.values[2]));
            //Log.d(TAG, "Count is: " + count +  "\n");
            count++;
        } //else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//            //Log.d(TAG, "Magnetometer changed at: " + System.currentTimeMillis() + " \n");
//            //Log.d(TAG, "Magneto: " + String.valueOf(event.values[0]) + String.valueOf(event.values[1]) + String.valueOf(event.values[2]));
//            value.put(SensorContract.SensorEntry.COLUMN_MAGNETO_X, event.values[0] + 0);
//            value.put(SensorContract.SensorEntry.COLUMN_MAGNETO_Y, event.values[1] + 0);
//            value.put(SensorContract.SensorEntry.COLUMN_MAGNETO_Z, event.values[2] + 0);
//            //Log.d(TAG, "Count is: " + count +  "\n");
            //count++;
        //}

        if (count == 2) {
           // Log.d(TAG, "*** Count is: " + count +  " *** Pushing it to array list\n");
            count = 0;
            value.put(SensorContract.SensorEntry.COLUMN_DATE, System.currentTimeMillis());
            value.put(SensorContract.SensorEntry.COLUMN_SPEED, MainActivity.getMainActivity().getSpeed());
            value.put(SensorContract.SensorEntry.COLUMN_GPS_LAT, 0);
            value.put(SensorContract.SensorEntry.COLUMN_GPS_LONG, 0);
            value.put(SensorContract.SensorEntry.COLUMN_CLASSIFICATION, 0);

            ContentValues copy_cv = new ContentValues(value);
            values.add(copy_cv);

            if (copy_cv == null) Log.e(TAG, "CONTENT VALUE IS NULL- while pushing into arrayList ");

            value.clear();

            /* TO_DO: COMMENT THESE OUT */
            //mSensorManager.unregisterListener(this);
            //stopSelf();
        }

        if (values.size() > 100) {
            Log.d(TAG, "~~~~~~ Starting Bulk Sync ~~~~ \n");
            //create a copy of arraylist and insert
            ArrayList<ContentValues> copy_list = new ArrayList<>(values);
            bulk_insert(this, copy_list);
            values.clear();
        }

    }

    public void bulk_insert (Context context, ArrayList<ContentValues> values) {
        SensorDataSave.syncSensor(context, values);
    }

    public void onDestroy () {
        //This is called when stopService() is called. Not called when app is terminated by user.
        //Call stopService() in MainActivity to catch app terminations by user and take appropriate actions
        //TODO: Check if you want to sync pending items in arraylist to the DB
        Log.w(TAG, "App terminated by user!! ");
    }
}
