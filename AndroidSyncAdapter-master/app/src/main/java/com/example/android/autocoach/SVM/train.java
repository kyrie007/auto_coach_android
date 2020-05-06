package com.example.android.autocoach.SVM;

import libsvm.*;

import java.io.IOException;

public class train {
    public static void main(String[] args) throws IOException {

        System.out.println(System.getProperty("user.dir"));
        String filapath = System.getProperty("user.dir")+"/app/src/main/java/com/example/android/autocoach/SVM/";

        String[] arg = {"-s", "0", "-c", "5", "-g", "0.05", filapath+ "trainData4j.txt", "model"};
//        String[] parg = {filapath+"letter.scale.t","model","out.txt"};
        svm_train.main(arg);
//        svm_predict.main(parg);


    }
}
