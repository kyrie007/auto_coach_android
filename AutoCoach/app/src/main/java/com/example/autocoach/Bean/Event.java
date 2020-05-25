package com.example.autocoach.Bean;


import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.IntStream;

import biz.source_code.dsp.filter.FilterPassType;
import biz.source_code.dsp.filter.IirFilterCoefficients;
import biz.source_code.dsp.filter.IirFilterDesignExstrom;

public class Event implements Serializable {
    private static final long serialVersionUID = 123456L;
    private Queue<double[]> rawData;
    private long start;
    private long end;
    private int type;
    private Double[] list0 = null;
    private Double[] list1 = null;
    private Double[] list2 = null;
    private Double[] list3 = null;
    private Double[] list4 = null;
    private Double[] list5 = null;
    private Double[] list6 = null;
    private transient IirFilterCoefficients iirFilterCoefficients = IirFilterDesignExstrom.design(FilterPassType.lowpass, 5,
            10.0/340, 10.0 / 170);
    private int classification;
    private String letter;
    private String[] patternAlphbet = {"a","b","c","h","i","j","o","p","q","v","w","x"};
    //private Queue<double[]> rawData;


    public Event(long start, int type){
        this.start = start;
        this.type = type;
        this.end = 0;
        this.rawData = new LinkedList<>();
        this.classification = -1;
        this.letter = "";
    }

    //double[] v = [x[0] for x in rawData];

    //double[][] array = (double[][]) rawData.toArray();

    private void getByColumn(double[][] rawData){
        int rowlength = rawData.length;
        //list0 -->time
        //list1 -->speed
        //list2 --> x

        list0 = new Double[rowlength];
        list1 = new Double[rowlength];
        list2 = new Double[rowlength];
        list3 = new Double[rowlength];
        list4 = new Double[rowlength];
        list5 = new Double[rowlength];
        list6 = new Double[rowlength];

        for(int i=0;i<rowlength;i++){
            list0[i] = rawData[i][0];
            list1[i] = rawData[i][1];
            list2[i] = rawData[i][2];
            list3[i] = rawData[i][3];
            list4[i] = rawData[i][4];
            list5[i] = rawData[i][5];
            list6[i] = rawData[i][6];

        }
        //return templist;
    }


    /**
     * 标准差σ=sqrt(s^2)
     * @param x
     * @return
     */
    private double getStandardDiviation(Double[] x) {
        int m = x.length;
        double sum = 0;
        for (int i = 0; i < m; i++) {// 求和
            sum += x[i];
        }
        double dAve = sum / m;// 求平均值
        double dVar = 0;
        for (int i = 0; i < m; i++) {// 求方差
            dVar += (x[i] - dAve) * (x[i] - dAve);
        }
        return Math.sqrt(dVar / m);
    }

    /**
     * 平均数
     * @param x
     * @return
     */
    private double getAverage(Double[] x) {
        double sum = 0.0;
        double mean = 0.0;

        for (int i = 0; i < x.length; i++) {
            sum += x[i];
        }
        mean = sum/x.length;
        return mean;
    }

    /**
     * 绝对值
     * @param x
     * @return
     */
    private Double[] getAbs(Double[] x) {
        Double[] y = new Double[x.length];
        for (int i = 0; i < x.length; i++) {
            if(x[i] < 0) {
                x[i] = -x[i];
            }
            y[i] = x[i];
        }
        return y;
    }

    //写方法 返回列数据
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Double> getArray() {
        double[][] array = rawData.toArray(new double[0][0]);
//        List list0 = getByColumn(rawData, 0);//得到列数据，其中第二个参数可以通过终端输入修改
//        List list1 = getByColumn(rawData, 1);//得到列数据，其中第二个参数可以通过终端输入修改
        getByColumn(array);//得到列数据，其中第二个参数可以通过终端输入修改

        iirFilterCoefficients = IirFilterDesignExstrom.design(FilterPassType.lowpass, 5,
                10.0/340, 10.0 / 170);

        list2 = IIRFilter(list2, iirFilterCoefficients.a, iirFilterCoefficients.b);
        list3 = IIRFilter(list3, iirFilterCoefficients.a, iirFilterCoefficients.b);

//        System.out.println("acc avlue:");
//        for(double acc:list2){
//            System.out.print(acc);
//            System.out.print("  ");
//        }

//        def calculate_feature(self,vect):
//        maxAX = max(vect[:, 3]) 最大值
        int index1 = IntStream.range(0, list2.length).reduce((i, j) -> list2[i] > list2[j] ? i : j).getAsInt();
        double maxAX = list2[index1];//Math.max(list3);
//        maxAY = max(vect[:, 2])
        int index2 = IntStream.range(0, list3.length).reduce((i, j) -> list3[i] > list3[j] ? i : j).getAsInt();
        double maxAY = list3[index2]; //max((list2));
//        minAX = min(vect[:, 3]) 最小值
        int index3 = IntStream.range(0, list2.length).reduce((i, j) -> list2[i] > list2[j] ? j : i).getAsInt();
        double minAX = list2[index3];//Math.min(list3);
//        minAY = min(vect[:, 2])
        int index4 = IntStream.range(0, list3.length).reduce((i, j) -> list3[i] > list3[j] ? j : i).getAsInt();
        double minAY = list3[index4]; //max((list2));


//        rangeAX = maxAX - minAX
        double rangeAX = maxAX - minAX;
//        rangeAY = maxAY - minAY
        double rangeAY = maxAY - minAY;
//
//        stdAX = np.std(vect[:, 3]) 标准差
        double stdAX = getStandardDiviation(list2);
//        stdAY = np.std(vect[:, 2])
        double stdAY = getStandardDiviation(list3);

//        meanAX = np.mean(vect[:, 3])平均值
        double meanAX = getAverage(list2);
//        meanAY = np.mean(vect[:, 2])
        double meanAY = getAverage(list3);
//        meanOX = np.mean(vect[:, 5])
        double meanOX = getAverage(list5);
//        meanSP = np.mean(vect[:, 1])
        double meanSP = getAverage(list1);


//        t = (vect[-1, 1] - vect[0, 1]) / 1000
        double t = (array[array.length - 1][0] - array[0][0])/1000;
//        differenceSP = vect[-1, 1] - vect[0, 1]
        double differenceSP = array[array.length - 1][1] - array[0][1];
//        StartEndAccx = vect[0, 3] + vect[-1, 3]
        double StartEndAccx = array[0][2] - array[array.length - 1][2];
//        StartEndAccy = vect[0, 2] + vect[-1, 2]
        double StartEndAccy = array[0][3] - array[array.length - 1][3];
//        axis = vect[0, -1]
        double axis = 0;
        if(this.type==0||this.type==1){
            axis = 0;
        }else{
            axis = 1;
        }

//        maxOX = max(abs(vect[:, 5]))
        Double[] list51 = getAbs(list5);
        int index5 = IntStream.range(0, list51.length).reduce((i, j) -> list51[i] > list51[j] ? i : j).getAsInt();
        double maxOX = list51[index5];
//        maxOY = max(abs(vect[:, 6]))
//        Double[] list61 = getAbs(list6);
//        int index6 = IntStream.range(0, list61.length).reduce((i, j) -> list61[i] > list61[j] ? i : j).getAsInt();
//        double maxOY = list61[index6];
//        maxOri = max(maxOX, maxOY)
        double maxOri = maxOX; //Math.max(maxOX,maxOY);


//        maxAccX = max(abs(vect[:, 3])) 绝对值
//        Double[] list31 = getAbs(list3);
//        int index7 = IntStream.range(0, list31.length).reduce((i, j) -> list31[i] > list31[j] ? i : j).getAsInt();
//        double maxAccX = list51[index5];
//        maxAccY = max(abs(vect[:, 2]))
        Double[] list31 = getAbs(list3);
        int index8 = IntStream.range(0, list31.length).reduce((i, j) -> list31[i] > list31[j] ? i : j).getAsInt();
        double maxAccY = list31[index8];

        ArrayList<Double> a = new ArrayList<Double>(Arrays.asList(rangeAX, rangeAY, stdAX, stdAY, meanAX, meanAY, meanOX, maxOri, maxAX, minAX, maxAccY,
        meanSP, StartEndAccx, StartEndAccy, t, axis));  //# 99% 86%rawData
        return a;
    }

    public ArrayList<Double> normalize(ArrayList<Double> features){
        ArrayList<Double> newFeature = new ArrayList<>();
        double[] max = {6.3804, 5.55909, 2.1217, 1.9062, 2.77235, 2.6795, 21.5806, 78.3708, 4.79105, 1.22073, 5.2711, 85.9737, 5.3705, 2.4869, 35.337, 1.0};
        double[] min = {0.7251, 0.0867, 0.0491, 0.0277, -4.21123, -2.8765, -23.2157, 3.0795, -0.6511, -6.3957, 0.0968,  0,  -6.9642, -4.9924, 0.835, 0.0};
        int index = 0;
        for(double feature: features){
            newFeature.add((feature-min[index])/(max[index]-min[index]));
            index++;
        }
        return newFeature;
    }



    public void add_Value(double[] data){
        this.rawData.offer(data);
    }

    public double[][] get_Value(){
        return (double[][]) this.rawData.toArray();
    }


    public void setType(int type){
        this.type = type;
    }

    public int getType(){
        return this.type;
    }

    public void setEnd(long end){
        this.end = end;
    }

    public long getEnd(){
        return this.end;
    }

    public double getDuration(){
        return ((double)this.end-(double)this.start)/1000;
    }

    public void setClassification(int level){
        this.classification = level;
    }

    public int getClassification(){
        return this.classification;
    }

    public String getLetter(){
        return this.patternAlphbet[this.classification];
    }

    private synchronized Double[] IIRFilter(Double[] signal, double[] a, double[] b) {

        double[] in = new double[b.length];
        double[] out = new double[a.length-1];

        Double[] outData = new Double[signal.length];

        for (int i = 0; i < signal.length; i++) {

            System.arraycopy(in, 0, in, 1, in.length - 1);
            in[0] = (double) signal[i];

            //calculate y based on a and b coefficients
            //and in and out.
            Double y = (double) 0;
            for(int j = 0 ; j < b.length ; j++){
                y += b[j] * in[j];

            }

            for(int j = 0;j < a.length-1;j++){
                y -= a[j+1] * out[j];
            }

            //shift the out array
            System.arraycopy(out, 0, out, 1, out.length - 1);
            out[0] = y;

            outData[i] = y;


        }
        return outData;
    }

}
