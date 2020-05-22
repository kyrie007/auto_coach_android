package com.example.android.autocoach.Sync;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.example.android.autocoach.Bean.Event;
import com.example.android.autocoach.LDA.Inferencer;
import com.example.android.autocoach.LDA.LDACmdOption;
import com.example.android.autocoach.LDA.Model;
import com.example.android.autocoach.MainActivity;

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
    private Queue<Event> eventQueue = new LinkedList<>(); //queue contains event from detection
    private Lock eventQueueLock =  new ReentrantLock();
    private double[] durationMap = new double[12];  //use to calculate score for each bar
    private Lock durationLock = new ReentrantLock();
    private StringBuffer LDAPattern = new StringBuffer(); //buffer from lda to svm
    private Inferencer inferencer = new Inferencer(); //LDA model
    private svm_model model = null; //svm lock
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
        LDACmdOption ldaOption = new LDACmdOption();
        ldaOption.inf = true;
        String filapath = System.getProperty("user.dir")+"/app/src/main/java/com/example/android/autocoach/models/";
        ldaOption.dir = filapath;
        ldaOption.modelName = "model-final";
        ldaOption.niters = 200;

        inferencer.init(ldaOption);

        //load SVM model
        try {
            model = svm.svm_load_model("model");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //start threads
        startSVM();
        startLDA();
        startFeedback();
    }

    public void startSVM(){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                Event eventFromDetect = null;
                while(true){
                        if(!eventQueue.isEmpty()){
                            eventQueueLock.lock();
                            eventFromDetect = eventQueue.poll();
                            eventQueueLock.unlock();

                            //to-do, process data: filter,

                            int m = 17;
                            svm_node[] x = new svm_node[m];

                            ArrayList<Double> a = eventFromDetect.getArray();

                            a = eventFromDetect.normalize(a); //normalize the data to 0~1

                            for(int j=0;j<m;j++)
                            {
                                x[j] = new svm_node();
                                x[j].index = j + 1; // atoi(st.nextToken());
                                x[j].value = a.get(j); // atof(st.nextToken());
                            }

                            double level = svm.svm_predict(model,x); //predict_label

                            eventFromDetect.setClassification((int) level);

                            double duration = eventFromDetect.getDuration();

                            //log the duration into map
                            durationLock.lock();
                            durationMap[(int) level]= Math.max(durationMap[(int) level], duration);
                            durationLock.unlock();

                            //add the letter to the pattern
                            LDAPattern.append(eventFromDetect.getLetter());


                        }
                }

            }
        }).start();
    }

    public void startLDA(){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                double score = 0;
//                String filapath = System.getProperty("user.dir")+"/app/src/main/java/com/example/android/autocoach/LDA/";
                while(true){
                    long startTime = System.currentTimeMillis();
                    String pattern = LDAPattern.toString();
                    //clear the pattern buffer
                    LDAPattern.setLength(0);
                    //calculate the pattern score
                    String [] test = {pattern};
                    if (!LDAPattern.toString().equals("")) {
                        Model newModel = inferencer.inference(test);
                        ArrayList<Double> result =newModel.modelTwords();
                        score = scorePattern(result);
                    }else{
                        score = 100;
                    }

                    long endTime = System.currentTimeMillis();
                    long dur = endTime - startTime;
                    try {
                        Thread.sleep(20000-dur);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public double scorePattern(ArrayList<Double> ldaResult){
        double sum = ldaResult.stream().mapToDouble(Double::doubleValue).sum();
        double score = 0;
        int index = 0;
        for(double lda: ldaResult){
            lda = lda/sum;
            if(index==0){
                score+=lda*75;
            }else if(index==1){
                score+=lda*100;
            }else if(index==2){
                score+=lda*50;
            }else{
                score+=lda*25;
            }
            index++;
        }
        return score;
    }

    public void startFeedback(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                double[] durationCopy = new double[12];
                while(true){
                    long startTime = System.currentTimeMillis();
                    durationLock.lock();
                    durationCopy = durationMap.clone();
                    durationMap = new double[12];
                    durationLock.unlock();

                    //bar score part
                    double accScore = 0;
                    double brakeScore = 0;
                    double turnScore = 0;
                    double swerveScore = 0;
                    for(int i = 2;i>=0;i--){
                        if(durationCopy[i]>0){
                            accScore = getBarScore(i, durationCopy[i]);
                        }
                    }
                    for(int i = 5;i>=3;i--){
                        if(durationCopy[i]>0){
                            brakeScore = getBarScore(i, durationCopy[i]);
                        }
                    }
                    for(int i = 8;i>=6;i--){
                        if(durationCopy[i]>0){
                            turnScore = getBarScore(i, durationCopy[i]);
                        }
                    }
                    for(int i = 11;i>=9;i--){
                        if(durationCopy[i]>0) {
                            turnScore = getBarScore(i, durationCopy[i]);
                        }
                    }

                    // feedback part



                    long endTime = System.currentTimeMillis();
                    long duration = endTime-startTime;
                    try {
                        Thread.sleep(10000-duration);
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

    public double getBarScore(int riskLevel, double duration){
        double score = 0;
        if(riskLevel==2){
            score = (100-74)*(1-Math.min(duration, 10)/ 10) + 74;
        }else if(riskLevel==1){
            score = (74-49)*(1-Math.min(duration, 10)/ 10) + 49;
        }else{
            score = 49*(1-Math.min(duration, 10)/ 10);
        }
        return score;
    }
}
