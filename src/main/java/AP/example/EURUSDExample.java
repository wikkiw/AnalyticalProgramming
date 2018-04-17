package AP.example;

import AP.algorithm.Algorithm;
import AP.algorithm.de.ap.AP_DEbest;
import AP.algorithm.de.ap.AP_DErand1bin;
import AP.algorithm.de.ap.AP_LSHADE;
import AP.algorithm.de.ap.AP_ShaDE;
import AP.model.AP_Individual;
import AP.model.ap.objects.AP_Abs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.DoubleStream;
import AP.model.ap.objects.AP_Cos;
import AP.model.ap.objects.AP_Cubic;
import AP.model.ap.objects.AP_Div;
import AP.model.ap.objects.AP_Exp;
import AP.model.ap.objects.AP_Ln;
import AP.model.ap.objects.AP_Log10;
import AP.model.ap.objects.AP_Mod;
import AP.model.ap.objects.AP_Multiply;
import AP.model.ap.objects.AP_Plus;
import AP.model.ap.objects.AP_Quad;
import AP.model.ap.objects.AP_Sigmoid;
import AP.model.ap.objects.AP_Sin;
import AP.model.ap.objects.AP_Sqrt;
import AP.model.ap.objects.AP_Sub;
import AP.model.ap.objects.AP_Tan;
import AP.model.ap.objects.AP_aTOb;
import AP.model.ap.objects.AP_object;
import AP.model.ap.objects.AP_x1;
import AP.model.tf.ap.APtf;
import AP.model.tf.ap.regression.APdataset;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * This class describes the example of using Analytical Programming to
 * perform a symbolic regression of a EUR/USD dataset for 2016.
 * 
 * @author wiki on 14/07/2016
 */
public class EURUSDExample {

    private static final int run_count = 30;
    private static final int gen = 2000;
    private static final int dim = 150;

    /**
     * GBP/USD problem example solved by AP powered by Differential Evolution
     */
    public static void eurusdDErand(String dir, double[][] dataset) {
        
        /**
         * Settings of the evolutionary algorithm - Differential Evolution
         */
        
        Algorithm de;
        int dimension = dim; //Length of an individual - when using functions in GFS with maximum number of required arguments max_arg = 2, 2/3 are designated for program, 1/3 for constant values - for 60 it is 40 and 20
        int NP = 75; //Size of the population - number of competitive solutions
        int generations = gen; //Stopping criterion - number of generations in evolution
        int MAXFES = generations * NP; //Number of fitness function evaluations
        double f = 0.5, cr = 0.8; //Scaling factor f and crossover rate cr
        AP.util.random.Random generator = new AP.util.random.UniformRandom(); //Random number generator
        
        /**
         * Symbolic regression part
         * 
         * Settings of the dataset which is regressed
         * Example: Sextic problem
         */
        
        // 50 points from range <-1, 1>, another type of data providing is possible
        double[][] dataset_points = dataset;
        ArrayList<AP_object> GFS = new ArrayList<>();
        GFS.add(new AP_Plus()); //Addition
        GFS.add(new AP_Sub()); //Subtraction
        GFS.add(new AP_Multiply()); //Mulitplication
        GFS.add(new AP_Div()); //Divison
        GFS.add(new AP_Abs());
        GFS.add(new AP_Cos());
        GFS.add(new AP_Cubic());
        GFS.add(new AP_Exp());
        GFS.add(new AP_Ln());
        GFS.add(new AP_Log10());
        GFS.add(new AP_Mod());
        GFS.add(new AP_Quad());
        GFS.add(new AP_Sin());
        GFS.add(new AP_Sigmoid());
        GFS.add(new AP_Sqrt());
        GFS.add(new AP_Tan());
        GFS.add(new AP_aTOb());
        GFS.add(new AP_x1()); //Independent variable x1
//        GFS.add(new AP_Const_static()); //Constant object - value is evolved in the extension of the individuals
        //More functions and terminals for GFS can be found or added to model.ap.objects
        
        double const_min = -10; //Minimum value of constants
        double const_max = 10; //Maximum value of constants
        
        APtf tf = new APdataset(dataset_points, GFS, const_min, const_max); //Dataset constructor

        /**
         * Additional variables
         */
        
        int runs = run_count; //Number of runs of the regression
        
        double min; //Helping variable for best individual in terms of fitness function value
        double[] bestArray = new double[runs]; //Array for statistics
        int i, best; //Additional helping variables

        try {
            
            PrintWriter eq_writer = new PrintWriter(dir + "equations.txt", "UTF-8");
        
        
        /**
         * Runs of the algorithm with statistical analysis
         */
        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            de = new AP_DErand1bin(dimension, NP, MAXFES, tf, generator, f, cr);

            de.run();

            PrintWriter writer;
            
            try {
                writer = new PrintWriter(dir + tf.name() + "-DErand" + k + ".txt", "UTF-8");

                writer.print("{");
                
                for (int ii = 0; ii < ((AP_DErand1bin)de).getBestHistory().size(); ii++) {

                    writer.print(String.format(Locale.US, "%.10f", ((AP_DErand1bin)de).getBestHistory().get(ii).fitness));

                    if (ii != ((AP_DErand1bin)de).getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(AP_LSHADE.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            bestArray[k] = de.getBest().fitness - tf.optimum();

            /**
             * AP best result value and equation.
             */

            System.out.println("=================================");
            System.out.println("Best obtained fitness function value: \n" + (de.getBest().fitness - tf.optimum()));
            System.out.println("Equation: \n" + ((AP_Individual) de.getBest()).equation);
            System.out.println("Vector: \n" + Arrays.toString(((AP_Individual) de.getBest()).vector));
            System.out.println("=================================");
            
            for(AP_Individual ind : ((AP_DErand1bin)de).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
                if(ind.fitness == 0){
                    System.out.println("Solution found in " + i + " CFE");
                    break;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            System.out.println("\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
            
          eq_writer.println(((AP_Individual) de.getBest()).equation + ";");
                
            }
            
            eq_writer.close();
        
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(EURUSDExample.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        
    }
    
    /**
     * GBP/USD problem example solved by AP powered by Differential Evolution best
     */
    public static void eurusdDEbest(String dir, double[][] dataset) {
        
        /**
         * Settings of the evolutionary algorithm - Differential Evolution
         */
        
        Algorithm de;
        int dimension = dim; //Length of an individual - when using functions in GFS with maximum number of required arguments max_arg = 2, 2/3 are designated for program, 1/3 for constant values - for 60 it is 40 and 20
        int NP = 75; //Size of the population - number of competitive solutions
        int generations = gen; //Stopping criterion - number of generations in evolution
        int MAXFES = generations * NP; //Number of fitness function evaluations
        double f = 0.5, cr = 0.8; //Scaling factor f and crossover rate cr
        AP.util.random.Random generator = new AP.util.random.UniformRandom(); //Random number generator
        
        /**
         * Symbolic regression part
         * 
         * Settings of the dataset which is regressed
         * Example: Sextic problem
         */
        
        // 50 points from range <-1, 1>, another type of data providing is possible
        double[][] dataset_points = dataset;
        ArrayList<AP_object> GFS = new ArrayList<>();
        GFS.add(new AP_Plus()); //Addition
        GFS.add(new AP_Sub()); //Subtraction
        GFS.add(new AP_Multiply()); //Mulitplication
        GFS.add(new AP_Div()); //Divison
        GFS.add(new AP_Abs());
        GFS.add(new AP_Cos());
        GFS.add(new AP_Cubic());
        GFS.add(new AP_Exp());
        GFS.add(new AP_Ln());
        GFS.add(new AP_Log10());
        GFS.add(new AP_Mod());
        GFS.add(new AP_Quad());
        GFS.add(new AP_Sin());
        GFS.add(new AP_Sigmoid());
        GFS.add(new AP_Sqrt());
        GFS.add(new AP_Tan());
        GFS.add(new AP_aTOb());
        GFS.add(new AP_x1()); //Independent variable x1
//        GFS.add(new AP_Const_static()); //Constant object - value is evolved in the extension of the individuals
        //More functions and terminals for GFS can be found or added to model.ap.objects
        
        double const_min = -10; //Minimum value of constants
        double const_max = 10; //Maximum value of constants
        
        APtf tf = new APdataset(dataset_points, GFS, const_min, const_max); //Dataset constructor

        /**
         * Additional variables
         */
        
        int runs = run_count; //Number of runs of the regression
        
        double min; //Helping variable for best individual in terms of fitness function value
        double[] bestArray = new double[runs]; //Array for statistics
        int i, best; //Additional helping variables

        try {
            
            PrintWriter eq_writer = new PrintWriter(dir + "equations.txt", "UTF-8");
        
        /**
         * Runs of the algorithm with statistical analysis
         */
        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            de = new AP_DEbest(dimension, NP, MAXFES, tf, generator, f, cr, null);

            de.run();

            PrintWriter writer;

            try {
                writer = new PrintWriter(dir + tf.name() + "-DEbest" + k + ".txt", "UTF-8");

                writer.print("{");
                
                for (int ii = 0; ii < ((AP_DEbest)de).getBestHistory().size(); ii++) {

                    writer.print(String.format(Locale.US, "%.10f", ((AP_DEbest)de).getBestHistory().get(ii).fitness));

                    if (ii != ((AP_DEbest)de).getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(AP_LSHADE.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            bestArray[k] = de.getBest().fitness - tf.optimum();

            /**
             * AP best result value and equation.
             */

            System.out.println("=================================");
            System.out.println("Best obtained fitness function value: \n" + (de.getBest().fitness - tf.optimum()));
            System.out.println("Equation: \n" + ((AP_Individual) de.getBest()).equation);
            System.out.println("Vector: \n" + Arrays.toString(((AP_Individual) de.getBest()).vector));
            System.out.println("=================================");
            
            for(AP_Individual ind : ((AP_DEbest)de).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
                if(ind.fitness == 0){
                    System.out.println("Solution found in " + i + " CFE");
                    break;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            System.out.println("\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
            
           eq_writer.println(((AP_Individual) de.getBest()).equation + ";");
                
            }
            
            eq_writer.close();
        
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(EURUSDExample.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        
    }
    
    /**
     * GBP/USD problem example solved by AP powered by Success-History based Adaptive Differential Evolution
     * @param dir
     * @param dataset
     */
    public static void eurusdSHADE(String dir, double[][] dataset) {
        
        /**
         * Settings of the evolutionary algorithm - Differential Evolution
         */
        
        Algorithm de;
        int dimension = dim; //Length of an individual - when using functions in GFS with maximum number of required arguments max_arg = 2, 2/3 are designated for program, 1/3 for constant values - for 60 it is 40 and 20
        int NP = 75; //Size of the population - number of competitive solutions
        int generations = gen; //Stopping criterion - number of generations in evolution
        int MAXFES = generations * NP; //Number of fitness function evaluations
        int H = 20; //Historical memory size
        AP.util.random.Random generator = new AP.util.random.UniformRandom(); //Random number generator
        
        /**
         * Symbolic regression part
         * 
         * Settings of the dataset which is regressed
         * Example: Sextic problem
         */
        
        // 50 points from range <-1, 1>, another type of data providing is possible
        double[][] dataset_points = dataset;
        ArrayList<AP_object> GFS = new ArrayList<>();
        GFS.add(new AP_Plus()); //Addition
        GFS.add(new AP_Sub()); //Subtraction
        GFS.add(new AP_Multiply()); //Mulitplication
        GFS.add(new AP_Div()); //Divison
        GFS.add(new AP_Abs());
        GFS.add(new AP_Cos());
        GFS.add(new AP_Cubic());
        GFS.add(new AP_Exp());
        GFS.add(new AP_Ln());
        GFS.add(new AP_Log10());
        GFS.add(new AP_Mod());
        GFS.add(new AP_Quad());
        GFS.add(new AP_Sin());
        GFS.add(new AP_Sigmoid());
        GFS.add(new AP_Sqrt());
        GFS.add(new AP_Tan());
        GFS.add(new AP_aTOb());
        GFS.add(new AP_x1()); //Independent variable x1
//        GFS.add(new AP_Const_static()); //Constant object - value is evolved in the extension of the individuals
        //More functions and terminals for GFS can be found or added to model.ap.objects
        
        double const_min = -10; //Minimum value of constants
        double const_max = 10; //Maximum value of constants
        
        APtf tf = new APdataset(dataset_points, GFS, const_min, const_max); //Dataset constructor

        /**
         * Additional variables
         */
        
        int runs = run_count; //Number of runs of the regression
        
        double min; //Helping variable for best individual in terms of fitness function value
        double[] bestArray = new double[runs]; //Array for statistics
        int i, best; //Additional helping variables

        try {
            
            PrintWriter eq_writer = new PrintWriter(dir + "equations.txt", "UTF-8");
        
        /**
         * Runs of the algorithm with statistical analysis
         */
        for (int k = 0; k < runs; k++) {

            best = 0;
            i = 0;
            min = Double.MAX_VALUE;
            
            de = new AP_ShaDE(dimension, NP, MAXFES, tf, generator, H);

            de.run();
            
            PrintWriter writer;

            try {
                writer = new PrintWriter(dir + tf.name() + "-shade" + k + ".txt", "UTF-8");

                writer.print("{");
                
                for (int ii = 0; ii < ((AP_ShaDE)de).getBestHistory().size(); ii++) {

                    writer.print(String.format(Locale.US, "%.10f", ((AP_ShaDE)de).getBestHistory().get(ii).fitness));

                    if (ii != ((AP_ShaDE)de).getBestHistory().size() - 1) {
                        writer.print(",");
                    }

                }

                writer.print("}");

                writer.close();

            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(AP_LSHADE.class.getName()).log(Level.SEVERE, null, ex);
            }

            bestArray[k] = de.getBest().fitness - tf.optimum();

            /**
             * AP best result value and equation.
             */

            System.out.println("=================================");
            System.out.println("Best obtained fitness function value: \n" + (de.getBest().fitness - tf.optimum()));
            System.out.println("Equation: \n" + ((AP_Individual) de.getBest()).equation);
            System.out.println("Vector: \n" + Arrays.toString(((AP_Individual) de.getBest()).vector));
            System.out.println("=================================");
            
            for(AP_Individual ind : ((AP_ShaDE)de).getBestHistory()){
                i++;
                if(ind.fitness < min){
                    min = ind.fitness;
                    best = i;
                }
                if(ind.fitness == 0){
                    System.out.println("Solution found in " + i + " CFE");
                    break;
                }
            }
            System.out.println("Best solution found in " + best + " CFE");
            
            System.out.println("\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
            
            eq_writer.println(((AP_Individual) de.getBest()).equation + ";");
                
            }
            
            eq_writer.close();
        
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(EURUSDExample.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        
    }
    
    /**
     * GBP/USD problem example solved by AP powered by Success-History based Adaptive Differential Evolution with Linear decrease in population size
     */

    /**
     * GBP/USD problem example solved by AP powered by Success-History based Adaptive Differential Evolution with Linear decrease in population size
     * @param dir
     */

    /**
     * GBP/USD problem example solved by AP powered by Success-History based Adaptive Differential Evolution with Linear decrease in population size
     * @param dir
     * @param dataset
     */
    public static void eurusdLSHADE(String dir, double[][] dataset) {
        
        /**
         * Settings of the evolutionary algorithm - Differential Evolution
         */
        
        Algorithm de;
        int dimension = dim; //Length of an individual - when using functions in GFS with maximum number of required arguments max_arg = 2, 2/3 are designated for program, 1/3 for constant values - for 60 it is 40 and 20
        int NP = 75; //Size of the population - number of competitive solutions
        int NPfinal = 10;
        int generations = gen; //Stopping criterion - number of generations in evolution
        int MAXFES = generations * NP; //Number of fitness function evaluations
        int H = 20; //Historical memory size
        AP.util.random.Random generator = new AP.util.random.UniformRandom(); //Random number generator
        
        /**
         * Symbolic regression part
         * 
         * Settings of the dataset which is regressed
         * Example: Sextic problem
         */
        
        // 50 points from range <-1, 1>, another type of data providing is possible
        double[][] dataset_points = dataset;
        ArrayList<AP_object> GFS = new ArrayList<>();
        GFS.add(new AP_Plus()); //Addition
        GFS.add(new AP_Sub()); //Subtraction
        GFS.add(new AP_Multiply()); //Mulitplication
        GFS.add(new AP_Div()); //Divison
        GFS.add(new AP_Abs());
        GFS.add(new AP_Cos());
        GFS.add(new AP_Cubic());
        GFS.add(new AP_Exp());
        GFS.add(new AP_Ln());
        GFS.add(new AP_Log10());
        GFS.add(new AP_Mod());
        GFS.add(new AP_Quad());
        GFS.add(new AP_Sin());
        GFS.add(new AP_Sigmoid());
        GFS.add(new AP_Sqrt());
        GFS.add(new AP_Tan());
        GFS.add(new AP_aTOb());
        GFS.add(new AP_x1()); //Independent variable x1
//        GFS.add(new AP_Const_static()); //Constant object - value is evolved in the extension of the individuals
        //More functions and terminals for GFS can be found or added to model.ap.objects
        
        double const_min = -10; //Minimum value of constants
        double const_max = 10; //Maximum value of constants
        
        APtf tf = new APdataset(dataset_points, GFS, const_min, const_max); //Dataset constructor

        /**
         * Additional variables
         */
        
        int runs = run_count; //Number of runs of the regression
        
        double min; //Helping variable for best individual in terms of fitness function value
        double[] bestArray = new double[runs]; //Array for statistics
        int i, best; //Additional helping variables
        
        try {
            
            PrintWriter eq_writer = new PrintWriter(dir + "equations.txt", "UTF-8");

            /**
             * Runs of the algorithm with statistical analysis
             */
            for (int k = 0; k < runs; k++) {

                best = 0;
                i = 0;
                min = Double.MAX_VALUE;

                de = new AP_LSHADE(dimension, MAXFES, tf, H, NP, generator, NPfinal);

                de.run();

                PrintWriter writer;

                try {
                    writer = new PrintWriter(dir + tf.name() + "-lshade" + k + ".txt", "UTF-8");

                    writer.print("{");

                    for (int ii = 0; ii < ((AP_LSHADE)de).getBestHistory().size(); ii++) {

                        writer.print(String.format(Locale.US, "%.10f", ((AP_LSHADE)de).getBestHistory().get(ii).fitness));

                        if (ii != ((AP_LSHADE)de).getBestHistory().size() - 1) {
                            writer.print(",");
                        }

                    }

                    writer.print("}");

                    writer.close();

                } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                    Logger.getLogger(AP_LSHADE.class.getName()).log(Level.SEVERE, null, ex);
                }

                bestArray[k] = de.getBest().fitness - tf.optimum();

                /**
                 * AP best result value and equation.
                 */

                System.out.println("=================================");
                System.out.println("Best obtained fitness function value: \n" + (de.getBest().fitness - tf.optimum()));
                System.out.println("Equation: \n" + ((AP_Individual) de.getBest()).equation);
                System.out.println("Vector: \n" + Arrays.toString(((AP_Individual) de.getBest()).vector));
                System.out.println("=================================");

                for(AP_Individual ind : ((AP_LSHADE)de).getBestHistory()){
                    i++;
                    if(ind.fitness < min){
                        min = ind.fitness;
                        best = i;
                    }
                    if(ind.fitness == 0){
                        System.out.println("Solution found in " + i + " CFE");
                        break;
                    }
                }
                System.out.println("Best solution found in " + best + " CFE");

                System.out.println("\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");

                eq_writer.println(((AP_Individual) de.getBest()).equation + ";");
                
            }
            
            eq_writer.close();
        
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(EURUSDExample.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("=================================");
        System.out.println("Min: " + DoubleStream.of(bestArray).min().getAsDouble());
        System.out.println("Max: " + DoubleStream.of(bestArray).max().getAsDouble());
        System.out.println("Mean: " + new Mean().evaluate(bestArray));
        System.out.println("Median: " + new Median().evaluate(bestArray));
        System.out.println("Std. Dev.: " + new StandardDeviation().evaluate(bestArray));
        System.out.println("=================================");
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
  
        double[][] dataset_1 = {{0.,0.192393},{0.012987,0.207193},{0.025974,0.116964},{0.038961,0.},{0.0519481,0.118873},{0.0649351,0.199077},{0.0779221,0.267027},{0.0909091,0.261458},{0.103896,0.211489},{0.116883,0.124761},{0.12987,0.226448},{0.142857,0.212922},{0.155844,0.276257},{0.168831,0.233291},{0.181818,0.22056},{0.194805,0.32543},{0.207792,0.225016},{0.220779,0.135264},{0.233766,0.0669955},{0.246753,0.0985041},{0.25974,0.162158},{0.272727,0.176162},{0.285714,0.214831},{0.298701,0.285487},{0.311688,0.128899},{0.324675,0.157384},{0.337662,0.264163},{0.350649,0.262412},{0.363636,0.538033},{0.376623,0.710216},{0.38961,0.62317},{0.402597,0.609325},{0.415584,0.730586},{0.428571,0.866645},{0.441558,0.864577},{0.454545,0.872533},{0.467532,0.772438},{0.480519,0.752387},{0.493506,0.640993},{0.506494,0.666454},{0.519481,0.618078},{0.532468,0.599618},{0.545455,0.572406},{0.558442,0.572884},{0.571429,0.467855},{0.584416,0.424729},{0.597403,0.449395},{0.61039,0.495067},{0.623377,0.259071},{0.636364,0.293762},{0.649351,0.210694},{0.662338,0.173297},{0.675325,0.174411},{0.688312,0.313176},{0.701299,0.380649},{0.714286,0.382877},{0.727273,0.423775},{0.74026,0.356461},{0.753247,0.361712},{0.766234,0.665818},{0.779221,0.617441},{0.792208,0.650382},{0.805195,0.573679},{0.818182,0.559675},{0.831169,0.752546},{0.844156,0.906747},{0.857143,0.818746},{0.87013,0.832272},{0.883117,0.811108},{0.896104,0.734564},{0.909091,0.679504},{0.922078,0.653246},{0.935065,0.65436},{0.948052,0.658657},{0.961039,0.709421}};
        double[][] dataset_2 = {{0.,0.763989},{0.012987,0.741212},{0.025974,0.756456},{0.038961,0.704448},{0.0519481,0.75825},{0.0649351,0.704448},{0.0779221,0.787303},{0.0909091,0.785509},{0.103896,0.773314},{0.116883,0.70911},{0.12987,0.523314},{0.142857,0.516141},{0.155844,0.586083},{0.168831,0.555775},{0.181818,0.625359},{0.194805,0.697095},{0.207792,0.576399},{0.220779,0.586442},{0.233766,0.437052},{0.246753,0.484577},{0.25974,0.530129},{0.272727,0.592539},{0.285714,0.635043},{0.298701,0.735473},{0.311688,0.870875},{0.324675,0.883788},{0.337662,1.},{0.350649,0.908178},{0.363636,0.921808},{0.376623,0.767934},{0.38961,0.756636},{0.402597,0.766858},{0.415584,0.726686},{0.428571,0.734756},{0.441558,0.803802},{0.454545,0.717898},{0.467532,0.605093},{0.480519,0.597023},{0.493506,0.61693},{0.506494,0.561514},{0.519481,0.444225},{0.532468,0.417683},{0.545455,0.416069},{0.558442,0.47023},{0.571429,0.417683},{0.584416,0.326937},{0.597403,0.371593},{0.61039,0.384326},{0.623377,0.248745},{0.636364,0.232425},{0.649351,0.316356},{0.662338,0.267934},{0.675325,0.42127},{0.688312,0.326758},{0.701299,0.681133},{0.714286,0.655667},{0.727273,0.683824},{0.74026,0.700861},{0.753247,0.770086},{0.766234,0.582138},{0.779221,0.492826},{0.792208,0.499641},{0.805195,0.572812},{0.818182,0.420552},{0.831169,0.519907},{0.844156,0.509684},{0.857143,0.636836},{0.87013,0.71736},{0.883117,0.637733},{0.896104,0.526363},{0.909091,0.665531},{0.922078,0.},{0.935065,0.0591822},{0.948052,0.0984577},{0.961039,0.147956}};
        double[][] dataset_3 = {{0.,0.441152},{0.012987,0.458876},{0.025974,0.445029},{0.038961,0.221545},{0.0519481,0.344226},{0.0649351,0.303517},{0.0779221,0.20936},{0.0909091,0.227638},{0.103896,0.292163},{0.116883,0.248962},{0.12987,0.368042},{0.142857,0.439214},{0.155844,0.207699},{0.168831,0.255331},{0.181818,0.294378},{0.194805,0.109388},{0.207792,0.178067},{0.220779,0.144835},{0.233766,0.00166159},{0.246753,0.},{0.25974,0.0429244},{0.272727,0.072833},{0.285714,0.279424},{0.298701,0.328441},{0.311688,0.555525},{0.324675,0.571033},{0.337662,0.553586},{0.350649,0.666574},{0.363636,0.477707},{0.376623,0.44946},{0.38961,0.295209},{0.402597,0.363334},{0.415584,0.313487},{0.428571,0.494046},{0.441558,0.544724},{0.454545,0.466076},{0.467532,0.541124},{0.480519,0.515924},{0.493506,0.618665},{0.506494,0.834949},{0.519481,0.942398},{0.532468,1.},{0.545455,0.928275},{0.558442,0.844088},{0.571429,0.990861},{0.584416,0.908613},{0.597403,0.826364},{0.61039,0.88175},{0.623377,0.569925},{0.636364,0.632789},{0.649351,0.555248},{0.662338,0.5054},{0.675325,0.508723},{0.688312,0.635558},{0.701299,0.509554},{0.714286,0.538632},{0.727273,0.505123},{0.74026,0.773747},{0.753247,0.770147},{0.766234,0.839657},{0.779221,0.726945},{0.792208,0.748269},{0.805195,0.723345},{0.818182,0.700083},{0.831169,0.7527},{0.844156,0.7455},{0.857143,0.493769},{0.87013,0.540847},{0.883117,0.548601},{0.896104,0.492384},{0.909091,0.614511},{0.922078,0.625312},{0.935065,0.70756},{0.948052,0.712822},{0.961039,0.751592}};
        double[][] dataset_4 = {{0.,0.946711},{0.012987,0.952899},{0.025974,0.913133},{0.038961,0.939147},{0.0519481,0.915425},{0.0649351,0.81389},{0.0779221,0.88815},{0.0909091,0.906601},{0.103896,0.835778},{0.116883,0.742952},{0.12987,0.711208},{0.142857,0.720605},{0.155844,0.646688},{0.168831,0.665712},{0.181818,0.708457},{0.194805,0.673848},{0.207792,0.65173},{0.220779,0.575521},{0.233766,0.546871},{0.246753,0.533578},{0.25974,0.546642},{0.272727,0.558217},{0.285714,0.570594},{0.298701,0.579647},{0.311688,0.672702},{0.324675,0.64898},{0.337662,0.640385},{0.350649,0.761976},{0.363636,0.818244},{0.376623,0.793835},{0.38961,0.751776},{0.402597,0.769081},{0.415584,0.732982},{0.428571,1.},{0.441558,0.600046},{0.454545,0.57724},{0.467532,0.499198},{0.480519,0.452899},{0.493506,0.396975},{0.506494,0.38815},{0.519481,0.338758},{0.532468,0.234357},{0.545455,0.216479},{0.558442,0.221178},{0.571429,0.258882},{0.584416,0.256475},{0.597403,0.161701},{0.61039,0.192757},{0.623377,0.236191},{0.636364,0.293262},{0.649351,0.227252},{0.662338,0.264841},{0.675325,0.229544},{0.688312,0.316755},{0.701299,0.299221},{0.714286,0.171442},{0.727273,0.404653},{0.74026,0.366835},{0.753247,0.42551},{0.766234,0.235045},{0.779221,0.145771},{0.792208,0.175109},{0.805195,0.265528},{0.818182,0.267706},{0.831169,0.126633},{0.844156,0.0308274},{0.857143,0.0420582},{0.87013,0.0672702},{0.883117,0.},{0.896104,0.0115746},{0.909091,0.040683},{0.922078,0.0529452},{0.935065,0.0606234},{0.948052,0.0650928},{0.961039,0.0443502}};

        String datasetName = "dataset_1";
        double[][] dataset = dataset_1.clone();
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
        System.out.println(datasetName);
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nRAND");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");

        eurusdDErand("E:\\results\\EURUSD_2016\\" + datasetName + "\\DErand\\", dataset);
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nBEST");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        

        eurusdDEbest("E:\\results\\EURUSD_2016\\" + datasetName + "\\DEbest\\", dataset);
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nSHADE");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");

        eurusdSHADE("E:\\results\\EURUSD_2016\\" + datasetName + "\\SHADE\\", dataset);
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nLSHADE");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        
        eurusdLSHADE("E:\\results\\EURUSD_2016\\" + datasetName + "\\LSHADE\\", dataset);
        
        datasetName = "dataset_2";
        dataset = dataset_2.clone();
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
        System.out.println(datasetName);
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nRAND");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");

        eurusdDErand("E:\\results\\EURUSD_2016\\" + datasetName + "\\DErand\\", dataset);
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nBEST");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        

        eurusdDEbest("E:\\results\\EURUSD_2016\\" + datasetName + "\\DEbest\\", dataset);
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nSHADE");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");

        eurusdSHADE("E:\\results\\EURUSD_2016\\" + datasetName + "\\SHADE\\", dataset);
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nLSHADE");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        
        eurusdLSHADE("E:\\results\\EURUSD_2016\\" + datasetName + "\\LSHADE\\", dataset);
        
        datasetName = "dataset_3";
        dataset = dataset_3.clone();
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
        System.out.println(datasetName);
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nRAND");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");

        eurusdDErand("E:\\results\\EURUSD_2016\\" + datasetName + "\\DErand\\", dataset);
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nBEST");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        

        eurusdDEbest("E:\\results\\EURUSD_2016\\" + datasetName + "\\DEbest\\", dataset);
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nSHADE");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");

        eurusdSHADE("E:\\results\\EURUSD_2016\\" + datasetName + "\\SHADE\\", dataset);
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nLSHADE");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        
        eurusdLSHADE("E:\\results\\EURUSD_2016\\" + datasetName + "\\LSHADE\\", dataset);
        
        datasetName = "dataset_4";
        dataset = dataset_4.clone();
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
        System.out.println(datasetName);
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nRAND");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");

        eurusdDErand("E:\\results\\EURUSD_2016\\" + datasetName + "\\DErand\\", dataset);
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nBEST");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        

        eurusdDEbest("E:\\results\\EURUSD_2016\\" + datasetName + "\\DEbest\\", dataset);
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nSHADE");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");

        eurusdSHADE("E:\\results\\EURUSD_2016\\" + datasetName + "\\SHADE\\", dataset);
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nLSHADE");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        
        eurusdLSHADE("E:\\results\\EURUSD_2016\\" + datasetName + "\\LSHADE\\", dataset);
        
    }
    
}
