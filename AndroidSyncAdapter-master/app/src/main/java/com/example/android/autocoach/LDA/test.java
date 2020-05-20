package com.example.android.autocoach.LDA;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;

class Test {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void main(String args[]){
        LDACmdOption ldaOption = new LDACmdOption();
        ldaOption.inf = true;
        String filapath = System.getProperty("user.dir")+"/app/src/main/java/com/example/android/autocoach/models/";
        ldaOption.dir = filapath;
        ldaOption.modelName = "model-final";
        ldaOption.niters = 200;

        Inferencer inferencer = new Inferencer();
        inferencer.init(ldaOption);

//        String [] test = {"politics bill clinton", "law court", "football match"};
        String [] test = {"a"};
        Model newModel = inferencer.inference(test);
        System.out.println(inferencer.globalDict.contains("a"));
        newModel.saveModelTwords(filapath+"inference");
        ArrayList<Double> result =newModel.modelTwords();
        for(double relation: result){
            System.out.println(relation);
        }

        double sum = result.stream().mapToDouble(Double::doubleValue).sum();
        double score = 0;
        int index = 0;
        for(double lda: result){
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
            System.out.println(lda);
        }
        System.out.println(score);

    }
}
