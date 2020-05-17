package com.example.android.autocoach.Sync;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.common.collect.EvictingQueue;

import java.util.Queue;
import java.util.stream.IntStream;

public class test {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void main(String[] args){
        Queue<double[]> dataQueue = EvictingQueue.create(4);
        dataQueue.add(new double[]{12312,134134,435});
        dataQueue.add(new double[]{12312,134134,435});
        dataQueue.add(new double[]{12312,134134,435});
        Object[] data = dataQueue.toArray();
        System.out.println(data.length);
        double[] result = (double[]) dataQueue.toArray()[0];
        System.out.println(result.length);
        for(double r: result){
            System.out.println(r);
        }
        double[] a = {8, 6, 3, 10};
        int index = IntStream.range(0, a.length).reduce((i, j) -> a[i] > a[j] ? i : j).getAsInt();
        System.out.println(index);
    }
}