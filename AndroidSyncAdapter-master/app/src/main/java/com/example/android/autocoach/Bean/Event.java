package com.example.android.autocoach.Bean;


import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

public class Event implements Serializable {
    private static final long serialVersionUID = 123456L;
    private long start;
    private long end;
    private int type;
    private int feature;
    private Queue<double[]> rawData;
    public Event(long start, int type){
        this.start = start;
        this.type = type;
        this.end = 0;
        this.feature = 0;
        this.rawData = new LinkedList<>();
    }

    public void add_Value(double[] data){
        this.rawData.offer(data);
    }


    public int getFeature(){
        return this.feature;
    }

    public void setType(int type){
        this.type = type;
    }

    public int getType(){
        return this.type;
    }

    public void setEnd(long end){
        this.end = end;
    }

}
