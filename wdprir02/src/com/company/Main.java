package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {


    public static int[] return_RGB(BufferedImage img, int xx, int yy)
    {
        Color clr = new Color(img.getRGB(xx, yy));
        int red = clr.getRed();
        int green = clr.getGreen();
        int blue = clr.getBlue();
        return new int[] {red,green,blue};
    }
    public static int norm_color(int color_part)
    {
        return Math.min(255,Math.max(0,color_part));
    }
    public static float norm_color(float color_part)
    {
        return Math.min(1,Math.max(0,color_part));
    }

    public static void apply_kernel(BufferedImage img, BufferedImage output_img, int xx, int yy, int [][] kernel)
    {
        int[] current_RGB={0,0,0};

        for(int ii=0;ii<3;ii++)
        {
            for(int jj=0;jj<3;jj++)
            {
                int[] rgb_ii_jj = return_RGB(img, xx + ii - 1, yy + jj - 1);
                for(int kk=0;kk<3;kk++)
                {

                    current_RGB[kk] += rgb_ii_jj[kk] * kernel[ii][jj];
                }
            }

        }
        output_img.setRGB(xx,yy,new Color(norm_color(current_RGB[0]),norm_color(current_RGB[1]),norm_color(current_RGB[2])).getRGB());
    }


    public static void process_online_img(Element e, int[][]kernel) throws IOException {
        URL url = new URL(e.attr("abs:href"));
        BufferedImage img;
        img=ImageIO.read(url);
        BufferedImage processedImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        for (int xx = 1; xx < img.getWidth()-1; xx++)
        {
            for (int yy = 1; yy < img.getHeight()-1; yy++)
            {
                apply_kernel(img, processedImage, xx, yy, kernel);
            }

        }
        ImageIO.write(processedImage,"png", new File(e.attr("href")));
    }
    public static void main(String[] args)
    {
        System.out.println("hello");


        int [][] kernel={{-1,-1,-1},{-1,8,-1},{-1,-1,-1}};
//        kernel= new int[][]{{0,-1,0},{-1,4,-1},{0,-1,0}};
//        kernel=new int[][] {{1,2,1},{2,4,2},{1,2,1}};
//        kernel=new int[][] {{1,1,1},{1,1,1},{1,1,1}};
        System.out.println(Arrays.deepToString(kernel));
        long start_time=System.nanoTime();
        Document document;
        try
        {
            document = Jsoup.connect("http://www.if.pw.edu.pl/~mrow/dyd/wdprir/").get();
            for (Element e : document.select("a[href$=.png]"))
            {
                process_online_img(e,kernel);

            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        long finish_time=System.nanoTime();
        System.out.println((finish_time-start_time)*Math.pow(10,-9));


        ExecutorService ex = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        start_time=System.nanoTime();
        try
        {
            document = Jsoup.connect("http://www.if.pw.edu.pl/~mrow/dyd/wdprir/").get();
            for (Element e : document.select("a[href$=.png]"))
            {
                ex.submit(()->
                {

                    try
                    {
                        process_online_img(e,kernel);
                    } catch (IOException ee)
                    {
                        ee.printStackTrace();
                    }
                });

            }
            ex.shutdown();
            try {
                ex.awaitTermination(1, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        finish_time=System.nanoTime();
        System.out.println((finish_time-start_time)*Math.pow(10,-9));
    }
}