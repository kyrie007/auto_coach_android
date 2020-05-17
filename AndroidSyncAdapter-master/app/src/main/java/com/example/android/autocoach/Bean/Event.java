package com.example.android.autocoach.Bean;


import java.io.Serializable;

public class Event implements Serializable {
    private static final long serialVersionUID = 123456L;
    private long start;
    private long end;
    private int type;
    private int feature;
    public Event(long start, int type){
        this.start = start;
        this.type = type;
        this.end = 0;
        this.feature = 0;
    }

    public void add_Value(int data){
        this.feature +=data;
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

}
