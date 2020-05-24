package com.example.android.autocoach.libsvm;


import com.example.android.autocoach.libsvm.svm_node;

public class svm_problem implements java.io.Serializable
{
	public int l;
	public double[] y;
	public svm_node[][] x;
}
