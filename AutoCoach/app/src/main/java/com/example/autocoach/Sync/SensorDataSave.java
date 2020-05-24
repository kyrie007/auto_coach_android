package com.example.autocoach.Sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.autocoach.Database.SensorContract;

import java.util.ArrayList;

/**
 * Created by sandeepchawan on 2017-10-26.
 */

public class SensorDataSave {

    synchronized public static void syncSensor(Context context, ArrayList<ContentValues> values) {
        Log.d("SensorDataSave", "&&&&&&&&&&&& Calling syncSensor &&&&&&&&&&&&");
        try {

            if (values != null && values.size() != 0) {
                /* Get a handle on the ContentResolver to insert data */
                ContentResolver sensorContentResolver = context.getContentResolver();

                /* Convert the arraylist to normal array */
                ContentValues[] cvalues = new ContentValues[values.size()];
                for (int i=0; i<values.size(); i++) {
                    cvalues[i] = values.get(i);
                    /* Insert our new sensor data into Sensor's ContentProvider */
                    sensorContentResolver.insert(
                            SensorContract.SensorEntry.CONTENT_URI,
                            values.get(i));
                    if(values.get(i).get(SensorContract.SensorEntry.COLUMN_ACC_X)==null){
                        System.out.println("x value become null");
                    }

                }
//                sensorContentResolver.bulkInsert(SensorContract.SensorEntry.CONTENT_URI, cvalues);



            }

            Log.d("SensorDataSave", "&&&&&&&&&&&& Bulk Insert completed &&&&&&&&&&&&");
            /* If the code reaches this point, we have successfully performed our insert into localDB */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}