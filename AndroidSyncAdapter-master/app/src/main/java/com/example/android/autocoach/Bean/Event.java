package com.example.android.autocoach.Bean;


import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.IntStream;

public class Event implements Serializable {
    private static final long serialVersionUID = 123456L;
    private Queue<double[]> rawData;
    private double[][] array = (double[][]) rawData.toArray();
    private long start;
    private long end;
    private int type;
    //private Queue<double[]> rawData;


    public Event(long start, int type){
        this.start = start;
        this.type = type;
        this.end = 0;
        this.rawData = new LinkedList<>();
    }

    //double[] v = [x[0] for x in rawData];

    //double[][] array = (double[][]) rawData.toArray();

    public Double[] getByColumn(Queue<double[]> rawData, int column){
        int rowlength = rawData.size();
        //double[][] array = (double[][]) rawData.toArray();

        //Object[] array = Collection.toArray();
        //int columnlength = rawData[0].size();

        List<Double> templist = new ArrayList();
        for(int i=0;i<rowlength;i++)
            templist.add(array[i][column]);
        Object[] temp = templist.toArray();
        return (Double[]) temp;
        //return templist;
    }

    //GetArrayByColumn ipp = new GetArrayByColumn();
    //List list0 = getByColumn(rawData, 0);//得到列数据，其中第二个参数可以通过终端输入修改；


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
    public ArrayList getArray() {
//        List list0 = getByColumn(rawData, 0);//得到列数据，其中第二个参数可以通过终端输入修改
//        List list1 = getByColumn(rawData, 1);//得到列数据，其中第二个参数可以通过终端输入修改
        Double[] list0 = getByColumn(rawData, 0);//得到列数据，其中第二个参数可以通过终端输入修改
        Double[] list1 = getByColumn(rawData, 1);//得到列数据，其中第二个参数可以通过终端输入修改
        Double[] list2 = getByColumn(rawData, 2);//得到列数据，其中第二个参数可以通过终端输入修改
        Double[] list3 = getByColumn(rawData, 3);//得到列数据，其中第二个参数可以通过终端输入修改
        Double[] list4 = getByColumn(rawData, 4);//得到列数据，其中第二个参数可以通过终端输入修改
        Double[] list5 = getByColumn(rawData, 5);//得到列数据，其中第二个参数可以通过终端输入修改
        Double[] list6 = getByColumn(rawData, 6);//得到列数据，其中第二个参数可以通过终端输入修改


//        def calculate_feature(self,vect):
//        maxAX = max(vect[:, 3]) 最大值
        int index1 = IntStream.range(0, list3.length).reduce((i, j) -> list3[i] > list3[j] ? i : j).getAsInt();
        double maxAX = list3[index1];//Math.max(list3);
//        maxAY = max(vect[:, 2])
        int index2 = IntStream.range(0, list2.length).reduce((i, j) -> list2[i] > list2[j] ? i : j).getAsInt();
        double maxAY = list2[index2]; //max((list2));
//        minAX = min(vect[:, 3]) 最小值
        int index3 = IntStream.range(0, list3.length).reduce((i, j) -> list3[i] > list3[j] ? j : i).getAsInt();
        double minAX = list3[index3];//Math.min(list3);
//        minAY = min(vect[:, 2])
        int index4 = IntStream.range(0, list2.length).reduce((i, j) -> list2[i] > list2[j] ? j : i).getAsInt();
        double minAY = list2[index4]; //max((list2));


//        rangeAX = maxAX - minAX
        double rangeAX = maxAX - minAX;
//        rangeAY = maxAY - minAY
        double rangeAY = maxAY - maxAY;
//
//        stdAX = np.std(vect[:, 3]) 标准差
        double stdAX = getStandardDiviation(list3);
//        stdAY = np.std(vect[:, 2])
        double stdAY = getStandardDiviation(list2);

//        meanAX = np.mean(vect[:, 3])平均值
        double meanAX = getAverage(list3);
//        meanAY = np.mean(vect[:, 2])
        double meanAY = getAverage(list2);
//        meanOX = np.mean(vect[:, 5])
        double meanOX = getAverage(list5);
//        meanSP = np.mean(vect[:, 1])
        double meanSP = getAverage(list1);


//        t = (vect[-1, 1] - vect[0, 1]) / 1000
        double t = (array[array.length - 1][1] - array[0][1])/1000;
//        differenceSP = vect[-1, 1] - vect[0, 1]
        double differenceSP = array[array.length - 1][1] - array[0][1];
//        StartEndAccx = vect[0, 3] + vect[-1, 3]
        double StartEndAccx = array[0][3] - array[array.length - 1][3];
//        StartEndAccy = vect[0, 2] + vect[-1, 2]
        double StartEndAccy = array[0][2] - array[array.length - 1][2];
//        axis = vect[0, -1]
        double axis = array[0][array[0].length - 1];

//        maxOX = max(abs(vect[:, 5]))
        Double[] list51 = getAbs(list5);
        int index5 = IntStream.range(0, list51.length).reduce((i, j) -> list51[i] > list51[j] ? i : j).getAsInt();
        double maxOX = list51[index5];
//        maxOY = max(abs(vect[:, 6]))
        Double[] list61 = getAbs(list6);
        int index6 = IntStream.range(0, list61.length).reduce((i, j) -> list61[i] > list61[j] ? i : j).getAsInt();
        double maxOY = list61[index6];
//        maxOri = max(maxOX, maxOY)
        double maxOri = Math.max(maxOX,maxOY);


//        maxAccX = max(abs(vect[:, 3])) 绝对值
        Double[] list31 = getAbs(list3);
        int index7 = IntStream.range(0, list31.length).reduce((i, j) -> list31[i] > list31[j] ? i : j).getAsInt();
        double maxAccX = list51[index5];
//        maxAccY = max(abs(vect[:, 2]))
        Double[] list21 = getAbs(list2);
        int index8 = IntStream.range(0, list21.length).reduce((i, j) -> list21[i] > list21[j] ? i : j).getAsInt();
        double maxAccY = list51[index5];

        List<Double> a = Arrays.asList(rangeAX, rangeAY, stdAX, stdAY, meanAX, meanAY, meanOX, maxOri, maxAX, minAX, maxAccY, differenceSP,
        meanSP, StartEndAccx, StartEndAccy, t, axis);  //# 99% 86%rawData
        return (ArrayList) a;
    }



    public void add_Value(double[] data){
        this.rawData.offer(data);
    }

    public double[][] get_Value(){
        double[][] value = (double[][]) this.rawData.toArray();
        return value;
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

}
