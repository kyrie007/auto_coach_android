package com.example.android.autocoach.LDA;

import java.io.IOException;
import java.util.Map;

public class ldaTrain {
    public static void main(String[] args) throws IOException {
        // 1. 从磁盘载入语料
        String filapath = System.getProperty("user.dir")+"/app/src/main/java/com/example/android/autocoach/LDA/";
        Corpus corpus = Corpus.load(filapath+"data/mini");
// 2. 创建 LDA 采样器
        LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());
// 3. 训练，目标10个主题
        ldaGibbsSampler.gibbs(10);
// 4. phi 矩阵是唯一有用的东西，用 LdaUtil 来展示最终的结果
        double[][] phi = ldaGibbsSampler.getPhi();
        Map<String, Double>[] topicMap = LdaUtil.translate(phi, corpus.getVocabulary(), 10);
        LdaUtil.explain(topicMap);

        Corpus corpus2 = Corpus.load(filapath+"data/max");
        double[] result = ldaGibbsSampler.inference(phi, corpus2.getDocument()[0]);
        for(double r: result){
            System.out.print(r);
            System.out.print(" ");
        }
    }
}
