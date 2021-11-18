package com.company;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Main {

    public static boolean check_condition(double aa, double bb, int max_itr)
    {

        double re=0;
        double im=0;
        for(int ii=0;ii<max_itr;ii++)
        {
            double re_o=re;
            double im_o=im;
            re=re_o*re_o-im_o*im_o+aa;
            im=2*re_o*im_o+bb;

            if(Math.sqrt(re_o*re_o+im_o*im_o)>=2)
            {
                return false;
            }
        }
        return true;
    }
    public static BufferedImage fill_picture(int width, int height,double[] range_re,double[] range_im, int max_itr)
    {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//        System.out.println(width);

        for(int xx=0;xx<width; xx++)
        {
            for(int yy=0;yy<height; yy++)
            {
                double aa=range_re[0]+xx*(range_re[1]-range_re[0])/(width-1);
                double bb=range_im[0]+yy*(range_im[1]-range_im[0])/(height-1);
                if(check_condition(aa,bb,max_itr))
                    image.setRGB(xx, yy, Color.BLACK.getRGB());
                else
                    image.setRGB(xx, yy, Color.WHITE.getRGB());

            }
        }

        return image;
    }
    public static BufferedImage fill_picture(int width, int height)
    {
        return  fill_picture(width,height,new double[] {-2.1,0.6}, new double[]  {-1.2,1.2},200);
    }
    public static void save_picture(BufferedImage image, String name)
    {
        File imageFile = new File("wdprir1_" + new SimpleDateFormat("dd_MM_yyyy_HH_mm").format(new java.util.Date()) + ".png");

        try {
            ImageIO.write(image, "png", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static double measure_time(int width, int height, int nn)
    {
        double[] timings=new double[nn];
        for(int ii=0;ii<nn;ii++)
        {
            long start_time=System.nanoTime();
            BufferedImage pixels=fill_picture(width, height);
            long finish_time=System.nanoTime();
            timings[ii]=finish_time-start_time;
        }
        double avg_time=0;
        for(var value:timings)
        {
            avg_time+=value;
        }
        avg_time/=nn;
        return avg_time*Math.pow(10,-9);
    }
    public static void write_output(String filename, int[] dimensions, double[] time)
    {
        BufferedWriter outputWriter = null;
        try
        {
            outputWriter = new BufferedWriter(new FileWriter(filename));

            for (int ii = 0; ii < dimensions.length; ii++) {
                // Maybe:
                outputWriter.write(dimensions[ii] + "\t" + time[ii]);
    //                outputWriter.write(Integer.toString(x[i]);
                outputWriter.newLine();
            }
            outputWriter.flush();
            outputWriter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public static void main(String[] args)
    {
	// write your code here
//        System.out.println("hello");
        int[] dimensions=new int[] { 32, 64, 128, 256, 512, 1024, 2048, 4096 ,8192};
        double[] time= new double[dimensions.length];
        int width=1920;
        int height=1920;
        System.out.println();
        for(int ii=0;ii<dimensions.length;ii++)
        {
            time[ii]=measure_time(dimensions[ii],dimensions[ii],200);
            System.out.println(time[ii]);
        }
        write_output("out.txt",dimensions,time);
        System.out.println(time.toString());
//        save_picture(pixels,"elo");


    }


}
