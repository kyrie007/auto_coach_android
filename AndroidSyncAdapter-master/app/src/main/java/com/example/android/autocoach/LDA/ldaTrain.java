package com.example.android.autocoach.LDA;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class ldaTrain {
    public static void main(String[] args) throws IOException {
        // 1. 从磁盘载入语料
        String filapath = System.getProperty("user.dir")+"/app/src/main/java/com/example/android/autocoach/LDA/";
        Corpus corpus = Corpus.load(filapath+"data/LDA");
// 2. 创建 LDA 采样器
        LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());
        ldaGibbsSampler.configure(10000, 2000, 100, 10);
// 3. 训练，目标10个主题
        ldaGibbsSampler.gibbs(4);
// 4. phi 矩阵是唯一有用的东西，用 LdaUtil 来展示最终的结果
        double[][] phi = ldaGibbsSampler.getPhi();
        Map<String, Double>[] topicMap = LdaUtil.translate(phi, corpus.getVocabulary(), 10);
        LdaUtil.explain(topicMap);

        Corpus corpus2 = Corpus.load(filapath+"data/max");
        double[] result = LdaGibbsSampler.inference(phi, corpus2.getDocument()[0]);
        for(double r: result){
            System.out.print(r);
            System.out.print(" ");
        }

        File file = new File("LDAModel.txt");  //存放数组数据的文件

        FileWriter out = new FileWriter(file);  //文件写入流


        //将数组中的数据写入到文件中。每行各数据之间TAB间隔
        for(int i=0;i<phi.length;i++){
            for(int j=0;j<phi[0].length;j++){
                out.write(phi[i][j]+"\t");
            }
            out.write("\r\n");
        }
        out.close();
    }
}
