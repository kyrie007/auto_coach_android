package com.example.android.autocoach.Sync;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;

import com.example.android.autocoach.Bean.Event;
import com.example.android.autocoach.LDA.Corpus;
import com.example.android.autocoach.LDA.LdaGibbsSampler;
import com.example.android.autocoach.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import libsvm.*;

public class FeedbackService extends Service {
    private int eventType = 0;
    final Messenger detectMessager = new Messenger(new MessagerHandler());
    private Queue<Event> eventQueue = new LinkedList<>();
    private Lock eventQueueLock =  new ReentrantLock();
    private double[][] phi = new double[4][];  //LDA model
    private StringBuffer LDAPattern = new StringBuffer();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        return new MyBinder();
        return detectMessager.getBinder();
    }

    public class MyBinder extends Binder {
        public FeedbackService getService(){
            return FeedbackService.this;
        }
    }


    @SuppressLint("HandlerLeak")
    class MessagerHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1){
                Bundle bundle = msg.getData();
                Event driving_event = (Event)bundle.getSerializable("event");
                eventQueueLock.lock();
                eventQueue.offer(driving_event);
                eventQueueLock.unlock();
                eventType = driving_event.getType();
                System.out.print("event="+driving_event.getType());
            }
            super.handleMessage(msg);
        }
    }



    @Override
    public void onCreate(){
        super.onCreate();

        //load LDA model
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File("LDAModel.txt")));
            String line;  //一行数据
            int row=0;
            while((line = in.readLine()) != null){
                String[] temp = line.split("\t");
                if(row==0){
                    phi = new double[4][temp.length];
                }
                for(int j=0;j<temp.length;j++){
                    phi[row][j] = Double.parseDouble(temp[j]);
                }
                row++;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //load SVM model


        //start threads
        startSVM();
        startLDA();
        startFeedback();
    }

    public void startSVM(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Event eventFromDetect = null;
                while(true){
                    try {
                        if(!eventQueue.isEmpty()){
                            eventQueueLock.lock();
                            eventFromDetect = eventQueue.poll();
                            eventQueueLock.unlock();


                            //to-do, process data: filter,
                        }
//                        svm_model model = svm.svm_load_model("model");



                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    public void startLDA(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filapath = System.getProperty("user.dir")+"/app/src/main/java/com/example/android/autocoach/LDA/";
                while(true){
                    long startTime = System.currentTimeMillis();

                    if (!LDAPattern.toString().equals("")){
                        Corpus corpus = null;
                        try {
                            FileWriter out = new FileWriter(new File(filapath+"data/max/LDATest.txt"));
                            out.write(LDAPattern.toString());
                            out.close();


                            corpus = Corpus.load(filapath+"data/max");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        double[] result = LdaGibbsSampler.inference(phi, corpus.getDocument()[0]);
                    }else{

                    }




                    long endTime = System.currentTimeMillis();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    public void startFeedback(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(1500);
                        String feedback = "";
                        if (eventType == 0){
                            feedback = "slow down";
                        }else if(eventType == 1){
                            feedback = "don't brake suddenly";
                        }else if(eventType == 2){
                            feedback = "turn slowly";
                        }else{
                            feedback = "swerve slowly";
                        }
                        // change ui on screen
                        MainActivity.getMainActivity().setFeedbackText(feedback);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }
}
