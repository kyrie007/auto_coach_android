package com.example.autocoach.SVM;

import java.io.IOException;

public class train {
    public static void main(String[] args) throws IOException {

        System.out.println(System.getProperty("user.dir"));
        String filapath = System.getProperty("user.dir")+"/app/src/main/java/com/example/autocoach/SVM/";

        String[] arg = {"-s", "0", "-c", "2", "-g", "1","-b","1", filapath+ "trainData4j.txt", "svm_model.txt"};
        String[] parg = {"-b","1", filapath+"trainData4j.txt","svm_model.txt","out.txt"};
        svm_train.main(arg);
        svm_predict.main(parg);


    }


}
