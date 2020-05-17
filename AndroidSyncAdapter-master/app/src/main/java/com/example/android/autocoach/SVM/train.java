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

    public  calculate_feature(self,vect):
    maxAX = max(vect[:, 3])
    maxAY = max(vect[:, 2])
    minAX = min(vect[:, 3])
    minAY = min(vect[:, 2])
    maxAccX = max(abs(vect[:, 3]))
    maxAccY = max(abs(vect[:, 2]))

    rangeAX = maxAX - minAX
            rangeAY = maxAY - minAY

    stdAX = np.std(vect[:, 3])
    stdAY = np.std(vect[:, 2])
    meanAX = np.mean(vect[:, 3])
    meanAY = np.mean(vect[:, 2])
    meanOX = np.mean(vect[:, 5])
    maxOX = max(abs(vect[:, 5]))
    maxOY = max(abs(vect[:, 6]))
    maxOri = max(maxOX, maxOY)
    t = (vect[-1, 1] - vect[0, 1]) / 1000
    meanSP = np.mean(vect[:, 1])
    differenceSP = vect[-1, 1] - vect[0, 1]
    StartEndAccx = vect[0, 3] + vect[-1, 3]
    StartEndAccy = vect[0, 2] + vect[-1, 2]
    axis = vect[0, -1]
            return [rangeAX, rangeAY, stdAX, stdAY, meanAX, meanAY, meanOX, maxOri, maxAX, minAX, maxAccY, differenceSP,
    meanSP, StartEndAccx, StartEndAccy, t, axis]  # 99% 86%
}
