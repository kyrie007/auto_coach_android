package com.example.autocoach.LDA;

public class Main {
    public static void main(String args[]){
        String filapath = System.getProperty("user.dir")+"/app/src/main/java/com/example/autocoach/models/";
        System.out.println(filapath);
        String[] arg = {"-est","-alpha", "0.5","-beta", "0.2","-ntopics","4","-twords","15","-dir",filapath,"-dfile","LDATrain.txt","-wordmap", "wordmap.txt"};
        LDA.main(arg);
    }
}
