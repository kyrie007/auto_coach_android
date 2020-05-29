package com.example.autocoach.Sync;

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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.autocoach.Bean.Event;
import com.example.autocoach.LDA.Inferencer;
import com.example.autocoach.LDA.LDACmdOption;
import com.example.autocoach.LDA.Model;
import com.example.autocoach.MainActivity;
import com.example.autocoach.libsvm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class FeedbackService extends Service {
    final Messenger detectMessager = new Messenger(new MessagerHandler());
    private Queue<Event> eventQueue = new LinkedList<>(); //queue contains event from detection
    private Lock eventQueueLock =  new ReentrantLock();
    private double[] durationMap = new double[12];  //use to calculate score for each bar
    private Lock durationLock = new ReentrantLock();
    private StringBuffer LDAPattern = new StringBuffer(); //buffer from lda to svm
    private Inferencer inferencer = new Inferencer(); //LDA model
    private svm_model model = null; //svm lock
    private int patternNum = 0;
    private double tripScore = 0;
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
    class MessagerHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1){
                Bundle bundle = msg.getData();
                Event driving_event = (Event)bundle.getSerializable("event");
                eventQueueLock.lock();
                eventQueue.offer(driving_event);
                eventQueueLock.unlock();
                System.out.print("event="+driving_event.getType());
            }
            super.handleMessage(msg);
        }
    }

    public void downLoad(final String path, final String FileName) {
        try {
            URL url = new URL(path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(5000);
            con.setConnectTimeout(5000);
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestMethod("GET");
            if (con.getResponseCode() == 200) {
                InputStream is = con.getInputStream();//获取输入流
                FileOutputStream fileOutputStream = null;//文件输出流
                if (is != null) {
                    String storepath = getApplicationContext().getExternalFilesDir(null).getPath()+"/";
                    FileUtils fileUtils = new FileUtils(storepath);
                    fileOutputStream = new FileOutputStream(fileUtils.createFile(FileName));//指定文件保存路径，代码看下一步
                    byte[] buf = new byte[1024];
                    int ch;
                    while ((ch = is.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, ch);//将获取到的流写入文件中
                    }
                }
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onCreate(){
        super.onCreate();

        if(!new File(getApplicationContext().getExternalFilesDir(null).getPath()+"/svm_model.txt").exists()){
            MainActivity.getMainActivity().downloadToast();
            System.out.println("start download");

            //download model file
            downLoad("http://175.24.40.162:8081/AutoCoach/wordmap.txt", "wordmap.txt");
            downLoad("http://175.24.40.162:8081/AutoCoach/model-final.others", "model-final.others");
            downLoad("http://175.24.40.162:8081/AutoCoach/model-final.tassign", "model-final.tassign");
            downLoad("http://175.24.40.162:8081/AutoCoach/svm_model.txt", "svm_model.txt");
            System.out.println("download finished");
            MainActivity.getMainActivity().downloadFinishToast();
        }


        //load LDA model
        LDACmdOption ldaOption = new LDACmdOption();
        ldaOption.inf = true;
        String filepath = getApplicationContext().getExternalFilesDir(null).getPath()+"/" ;
        ldaOption.dir = filepath;
        ldaOption.modelName = "model-final";
        ldaOption.niters = 200;

        inferencer.init(ldaOption);

        //load SVM model
        try {
            model = svm.svm_load_model(filepath+"svm_model.txt");
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

                        int m = 16;
                        svm_node[] x = new svm_node[m];

                        ArrayList<Double> a = eventFromDetect.getArray();

                        for(int j=0;j<m;j++){
                            System.out.print(j+":"+ a.get(j)+"  ");
                        }
                        System.out.println("");

                        a = eventFromDetect.normalize(a); //normalize the data to 0~1

                        for(int j=0;j<m;j++)
                        {
                            x[j] = new svm_node();
                            x[j].index = j + 1; // atoi(st.nextToken());
                            x[j].value = a.get(j); // atof(st.nextToken());
//                            System.out.print(x[j].index+x[j].value);
                        }

                        double level = svm.svm_predict(model,x); //predict_label

                        eventFromDetect.setClassification((int) level);

                        double duration = eventFromDetect.getDuration();

                        System.out.println("Event:"+level+" --  duration:"+duration);
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

    int coin = 0;
    int counter = 0;
    boolean flag = false;

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
                    String[] test = {pattern};
                    if (!pattern.equals("")) {
                        if(inferencer.globalDict.contains(pattern)){ //if pattern in dictionary
                            Model newModel = inferencer.inference(test);
                            ArrayList<Double> result =newModel.modelTwords();
                            score = scorePattern(result);
                        }else{  //if pattern not in dictionary
                            score = 0;
                            for(int i = 0;i<pattern.length();i++){
                                Model newModel = inferencer.inference(new String[]{pattern.substring(i,i+1)});
                                ArrayList<Double> result = newModel.modelTwords();
                                score+=scorePattern(result);
                            }
                            score = score/pattern.length();
                            double punish = ((double)(1+pattern.length()-1)*(pattern.length()-1))/2;
                            score = score * ((100-punish)/100);  //[2 letters:98%] [3 letters:95%] [4 letters: 91%] ...
                        }

                    }else{ //if no event happen
                        score = 100;
                    }

                    patternNum++;
                    tripScore+=score;

                    MainActivity.getMainActivity().change_tripscore((int) (tripScore/patternNum));



                    System.out.println("pattern is:["+pattern+"]");
                    System.out.println("***score:  "+score+"  ***");
                    MainActivity.getMainActivity().change_currentscore((int) score);


                    if(score < 60) {
                        counter = 0;
                        flag = true;
                    }else if(flag == true){
                        counter++;
                        if(counter == 3) {
                            coin++;
                            MainActivity.getMainActivity().change_totalcoins(coin);
                            MainActivity.getMainActivity().getCoin();
                            counter = 0;
                            flag = false;
                        }
                    }
//
//                    System.out.println("score = " + score);
                    System.out.println("counter = " + counter);

                    long endTime = System.currentTimeMillis();
                    long dur = endTime - startTime;
                    try {
                        Thread.sleep(40000-dur);
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
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                double[] durationCopy = new double[12];
                while(true){
                    long startTime = System.currentTimeMillis();
                    durationLock.lock();
                    durationCopy = durationMap.clone();
                    durationMap = new double[12];
                    durationLock.unlock();

//                    int index = 0;
//                    for(double duration: durationCopy){
//                        System.out.print(index+":"+duration+" ");
//                        index++;
//                    }

                    //bar score part
                    double accScore = 100;
                    double brakeScore = 100;
                    double turnScore = 100;
                    double swerveScore = 100;
                    for(int i = 2;i>=0;i--){
                        if(durationCopy[i]>0){
                            accScore = getBarScore(i, durationCopy[i]);
                            break;
                        }
                    }
                    for(int i = 5;i>=3;i--){
                        if(durationCopy[i]>0){
                            brakeScore = getBarScore(i-3, durationCopy[i]);
                            break;
                        }
                    }
                    for(int i = 8;i>=6;i--){
                        if(durationCopy[i]>0){
                            turnScore = getBarScore(i-6, durationCopy[i]);
                            break;
                        }
                    }
                    for(int i = 11;i>=9;i--){
                        if(durationCopy[i]>0) {
                            swerveScore = getBarScore(i-9, durationCopy[i]);
                            break;
                        }
                    }

                    System.out.println("accScore"+accScore);
                    System.out.println("brakeScore"+brakeScore);

                    MainActivity.getMainActivity().display_bar("acc", (int) accScore);
                    MainActivity.getMainActivity().display_bar("brake", (int) brakeScore);
                    MainActivity.getMainActivity().display_bar("turn", (int) turnScore);
                    MainActivity.getMainActivity().display_bar("swerve", (int) swerveScore);

                    // feedback part
                    double[] scoreList = {accScore, brakeScore, turnScore, swerveScore};
                    int feedbackIndex = IntStream.range(0, scoreList.length).reduce((i, j) -> scoreList[i] > scoreList[j] ? j : i).getAsInt();


                    if(scoreList[feedbackIndex]<70){
                        MainActivity.getMainActivity().setFeedback_icon(feedbackIndex);
                    }else{

                        MainActivity.getMainActivity().setFeedback_icon(-1);
                    }


                    long endTime = System.currentTimeMillis();
                    long duration = endTime-startTime;
                    try {
                        Thread.sleep(10000-duration);

                        // change ui on screen
//                        MainActivity.getMainActivity().setFeedbackText(feedback);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    public double getBarScore(int riskLevel, double duration){
        double score = 0;
        if(riskLevel==0){
            score = (100-74)*(1- Math.min(duration, 10)/ 10) + 74;
        }else if(riskLevel==1){
            score = (74-49)*(1- Math.min(duration, 10)/ 10) + 49;
        }else{
            score = 49*(1- Math.min(duration, 10)/ 10);
        }
        return score;
    }


}
