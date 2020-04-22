import biz.source_code.dsp.filter.*;
import biz.source_code.dsp.filter.IirFilterCoefficients;
import biz.source_code.dsp.filter.IirFilterDesignExstrom;

import java.io.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args){
                    //FILTER]

        try{

            String relativepath=System.getProperty("user.dir");

            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(relativepath+"/src/data.txt"))));

            double[] rawData;
            ArrayList<Double> datalist = new ArrayList<>();
            String temp;
            while((temp=in.readLine())!=null){

                datalist.add(Double.parseDouble(temp));
            }
            rawData = new double[datalist.size()];
            System.out.println(datalist.size());
            for(int i = 0; i<datalist.size();i++){
                rawData[i] = datalist.get(i);

            }


            IirFilterCoefficients iirFilterCoefficients;
            iirFilterCoefficients = IirFilterDesignExstrom.design(FilterPassType.lowpass, 5,
                    10.0/340, 10.0 / 170);

            for(double a : iirFilterCoefficients.a){
                System.out.println(a);
            }

            double[] outputData  = IIRFilter(rawData, iirFilterCoefficients.a, iirFilterCoefficients.b);
            System.out.println(outputData.length);


            File file = new File("output.txt");
            if (!file.isFile()) {
                file.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            int i = 0;
            for(double data: outputData){
                writer.write(data+"\n");
                System.out.println(data);
            }
            writer.close();

        }catch(IOException e){
            System.out.println("bad");
        }



    }

    public static synchronized double[] IIRFilter(double[] signal, double[] a, double[] b) {

        double[] in = new double[b.length];
        double[] out = new double[a.length-1];

        double[] outData = new double[signal.length];

        for (int i = 0; i < signal.length; i++) {

            System.arraycopy(in, 0, in, 1, in.length - 1);
            in[0] = signal[i];

            //calculate y based on a and b coefficients
            //and in and out.
            float y = 0;
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
