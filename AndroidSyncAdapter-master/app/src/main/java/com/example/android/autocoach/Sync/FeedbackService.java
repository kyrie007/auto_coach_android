package com.example.android.autocoach.Sync;

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
import com.example.android.autocoach.MainActivity;

public class FeedbackService extends Service {
    private int eventType = 0;
    final Messenger detectMessager = new Messenger(new MessagerHandler());
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


    class MessagerHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1){
                Bundle bundle = msg.getData();
                Event driving_event = (Event)bundle.getSerializable("event");
                eventType = driving_event.getType();
                System.out.print("event="+driving_event.getType());
                System.out.println("   feature="+driving_event.getFeature());
            }
            super.handleMessage(msg);
        }
    }



    @Override
    public void onCreate(){
        super.onCreate();
        startSVM();
        startFeedback();
    }

    public void startSVM(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    System.out.println("SVM");
                    try {
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
                while(true){
                    System.out.println("LDA");
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
                        }else{
                            feedback = "turn slowly";
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
