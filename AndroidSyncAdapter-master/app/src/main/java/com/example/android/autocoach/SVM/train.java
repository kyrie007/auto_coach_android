package com.example.android.autocoach.SVM;

import com.example.android.autocoach.libsvm.*;

import java.io.IOException;

public class train {
    public static void main(String[] args) throws IOException {

        System.out.println(System.getProperty("user.dir"));
        String filapath = System.getProperty("user.dir")+"/app/src/main/java/com/example/android/autocoach/SVM/";

        String[] arg = {"-s", "0", "-c", "1", "-g", "1","-b","1", filapath+ "trainData4j.txt", "model"};
        String[] parg = {"-b","1", filapath+"testData4j.txt","model","out.txt"};
        svm_train.main(arg);
        svm_predict.main(parg);


    }


}
