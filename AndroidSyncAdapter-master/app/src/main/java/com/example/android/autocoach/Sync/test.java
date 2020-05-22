package com.example.android.autocoach.Sync;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.android.autocoach.LDA.Model;
import com.google.common.collect.EvictingQueue;

import java.util.ArrayList;
import java.util.Queue;
import java.util.stream.IntStream;

public class test {
    private final Object lock=null;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void main(String[] args){
//        Queue<double[]> dataQueue = EvictingQueue.create(4);
//        dataQueue.add(new double[]{12312,134134,435});
//        dataQueue.add(new double[]{12312,134134,435});
//        dataQueue.add(new double[]{12312,134134,435});
//        Object[] data = dataQueue.toArray();
//        System.out.println(data.length);
//        double[] result = (double[]) dataQueue.toArray()[0];
//        System.out.println(result.length);
//        for(double r: result){
//            System.out.println(r);
//        }
//        double[] a = {8, 6, 3, 10};
//        int index = IntStream.range(0, a.length).reduce((i, j) -> a[i] > a[j] ? i : j).getAsInt();
//        System.out.println(index);
        test t = new test();
        t.startLDA();
        t.startFeedback();
    }

    private void startLDA(){
        new Thread(() -> {
            double score = 0;
//                String filapath = System.getProperty("user.dir")+"/app/src/main/java/com/example/android/autocoach/LDA/";
            while(true){
                synchronized (lock){

                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    public void startFeedback(){
        new Thread(() -> {
            while(true){

            }

        }).start();
    }
}
