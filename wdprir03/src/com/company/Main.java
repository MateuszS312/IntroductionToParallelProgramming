package com.company;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;


public class Main {

    private static int partition(Vector<Integer> arr, int begin, int end)
    {
        // last element of an array is an pivot
        int pivot = arr.get(end);

        int ii = (begin-1);

        for (int jj = begin; jj < end; jj++)
        {
            if (arr.get(jj) <= pivot)
            {
                ii++;
                int swapTemp = arr.get(ii);
                arr.set(ii,arr.get(jj));
                arr.set(jj,swapTemp);
            }
        }

        int swapTemp = arr.get(ii+1);
        arr.set(ii+1,arr.get(end));
        arr.set(end,swapTemp);

        return ii+1;
    }
    public static void quickSort(Vector<Integer> arr, int begin, int end) {
        if (begin < end)
        {
            int partitionIndex = partition(arr, begin, end);
            quickSort(arr, begin, partitionIndex-1);
            quickSort(arr, partitionIndex+1, end);
        }
    }

    public static double measure_time_quick_sort(int size, int nn)
    {
        double[] timings=new double[nn];
        for(int ii=0;ii<nn;ii++)
        {
            Vector<Integer> arr = new Vector<Integer>(size);
            fill_vector(arr,size);
            long start_time=System.nanoTime();
            quickSort(arr,0,arr.size()-1);
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


    public static Vector<Integer> sample_sort(Vector<Integer> arr, int p, ExecutorService ex)
    {

//        prepare data structure for p bins
        Vector<Vector<Integer>> vec_of_vectors = new Vector<Vector<Integer>>(p);
        for(int ii=0;ii<p;ii++)
            vec_of_vectors.add(new Vector<Integer>());

//        select p-1 splitters from sample to create p bins
        Vector<Integer>  spliters=new Vector<Integer>(p-1);
        Random random=new Random();
        for(int ii=0;ii<p-1;ii++)
            spliters.add(arr.get(random.nextInt(arr.size()-1)));

//        sort splitters
        quickSort(spliters,0,spliters.size()-1);

        for (var value : arr)
        {
            for(int jj=0;jj<p-1;jj++) {

                if (value <= spliters.get(jj) )
                {
                    vec_of_vectors.get(jj).add(value);
                    break;
                }
                else if (value>=spliters.get(p-2))
                {
                    vec_of_vectors.get(p-1).add(value);
                    break;
                }


            }
        }

//        create threads to sort every bins
        for(var vec : vec_of_vectors)
        {
            ex.submit(()->
            {

                quickSort(vec,0,vec.size()-1);
            });
        }
        ex.shutdown();
        try {
            ex.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        create output vector as concatenation of all of the bins
        Vector<Integer>  output=new Vector<Integer>();
        for(var vec :vec_of_vectors)
        {
            for(var value :vec)
            {
                output.add(value);
            }
        }

        return output;

    }

    public static double measure_time_sample_sort(int size, int p, int nn)
    {
        double[] timings=new double[nn];
        Vector<Integer> arr;
        for(int ii=0;ii<nn;ii++)
        {
            ExecutorService ex = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            arr= new Vector<Integer>(size);
            fill_vector(arr,size);
            long start_time=System.nanoTime();
            sample_sort(arr,4,ex);
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
    public static void fill_vector(Vector<Integer> arr, int size)
    {
        Random random = new Random();
        for(int ii=0;ii<size;ii++)
        {
            arr.add(ii,(random.nextInt(100)));
        }
    }
    public static void write_output(String filename, int[] sizes, double[] time)
    {
        BufferedWriter outputWriter = null;
        try
        {
            outputWriter = new BufferedWriter(new FileWriter(filename));

            for (int ii = 0; ii < sizes.length; ii++) {
                // Maybe:
                outputWriter.write(sizes[ii] + "\t" + time[ii]);
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
//        int[] sizes={100,1000,10000,100000,1000000,1000000};
        int[] sizes={100,500,1000,5000,10000,50000,100000,500000,1000000};
        int end=(int)Math.pow(10,6);
        int step=100;
        int start=100;
        double[] timings_sample=new double[sizes.length];
        double[] timings_quick=new double[sizes.length];
        for(int ii=0;ii<sizes.length;ii++)
        {
            System.out.println(sizes[ii]);
            timings_sample[ii]=measure_time_sample_sort(sizes[ii],8,10);
            timings_quick[ii]=measure_time_quick_sort(sizes[ii],10);
        }
        System.out.println(Arrays.toString(timings_quick));
        System.out.println(Arrays.toString(timings_sample));
        write_output("out_sample.txt", sizes, timings_sample);
        write_output("out_quick.txt", sizes, timings_quick);

    }
}
