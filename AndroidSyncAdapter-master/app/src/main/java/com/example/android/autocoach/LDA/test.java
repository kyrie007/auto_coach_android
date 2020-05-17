package com.example.android.autocoach.LDA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class test {
    public static void  main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(new File("LDAModel.txt")));  //
        String line;  //一行数据
        int row=0;
        double[][] phi = new double[4][];
        //逐行读取，并将每个数组放入到数组中
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

        System.out.println(phi.length);
        System.out.println(phi[0].length);
//        for(int i=0;i<phi.length;i++){
//            for(int j=0;j<phi[0].length;j++){
//                System.out.print(phi[i][j]+"\t");
//            }
//            System.out.println();
//        }
        String filapath = System.getProperty("user.dir")+"/app/src/main/java/com/example/android/autocoach/LDA/";
//        FileWriter out = new FileWriter(new File(filapath+"data/max/LDATest.txt"));
//        out.write("h");
//        out.close();
        Corpus corpus1 = Corpus.load(filapath+"data/LDA");
        Corpus corpus2 = Corpus.load(filapath+"data/max");
        System.out.println(Corpus.loadDocument(filapath+"data/max/LDATest.txt", corpus1.getVocabulary())[0]);
        double[] result = LdaGibbsSampler.inference(phi, Corpus.loadDocument(filapath+"data/max/LDATest.txt", corpus1.getVocabulary()));
        for(double r: result){
            System.out.print(r);
            System.out.print(" ");
        }
    }
}
