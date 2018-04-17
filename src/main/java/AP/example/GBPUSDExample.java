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
 * perform a symbolic regression of a GBP/USD dataset.
 * 
 * @author wiki on 14/07/2016
 */
public class GBPUSDExample {

    private static final int run_count = 30;
    private static final int gen = 2000;
    private static final int dim = 150;
    //private static double[][] dataset = {{0.,0.643704},{0.00334448,0.639670},{0.00668896,0.640348},{0.0100334,0.640201},{0.0133779,0.640880},{0.0167224,0.640880},{0.0200669,0.641694},{0.0234114,0.642616},{0.0267559,0.641264},{0.0301003,0.641616},{0.0334448,0.644999},{0.0367893,0.644675},{0.0401338,0.644675},{0.0434783,0.643700},{0.0468227,0.641820},{0.0501672,0.640152},{0.0535117,0.640843},{0.0568562,0.640773},{0.0602007,0.640156},{0.0635452,0.640156},{0.0668896,0.640603},{0.0702341,0.641275},{0.0735786,0.641754},{0.0769231,0.642164},{0.0802676,0.645155},{0.083612,0.645460},{0.0869565,0.645460},{0.090301,0.644646},{0.0936455,0.641874},{0.09699,0.641345},{0.100334,0.640470},{0.103679,0.640086},{0.107023,0.639272},{0.110368,0.639272},{0.113712,0.639742},{0.117057,0.639793},{0.120401,0.638262},{0.123746,0.638048},{0.12709,0.637221},{0.130435,0.637174},{0.133779,0.637174},{0.137124,0.636444},{0.140468,0.634893},{0.143813,0.640256},{0.147157,0.646969},{0.150502,0.649294},{0.153846,0.649722},{0.157191,0.649722},{0.160535,0.649340},{0.16388,0.651396},{0.167224,0.653595},{0.170569,0.654543},{0.173913,0.656879},{0.177258,0.659105},{0.180602,0.659105},{0.183946,0.656491},{0.187291,0.651668},{0.190635,0.650267},{0.19398,0.649598},{0.197324,0.647849},{0.200669,0.648116},{0.204013,0.648116},{0.207358,0.647958},{0.210702,0.649283},{0.214047,0.648984},{0.217391,0.644176},{0.220736,0.641571},{0.22408,0.643913},{0.227425,0.643913},{0.230769,0.644158},{0.234114,0.647115},{0.237458,0.653351},{0.240803,0.655650},{0.244147,0.657581},{0.247492,0.658805},{0.250836,0.658805},{0.254181,0.658137},{0.257525,0.659392},{0.26087,0.659872},{0.264214,0.660629},{0.267559,0.659446},{0.270903,0.658827},{0.274247,0.658827},{0.277592,0.658438},{0.280936,0.658812},{0.284281,0.654546},{0.287625,0.652793},{0.29097,0.651615},{0.294314,0.652806},{0.297659,0.652806},{0.301003,0.651808},{0.304348,0.654275},{0.307692,0.651487},{0.311037,0.646162},{0.314381,0.646871},{0.317726,0.647721},{0.32107,0.647721},{0.324415,0.646915},{0.327759,0.646394},{0.331104,0.647585}};
    //private static double[][] dataset = {{0.167224,0.653595},{0.170569,0.654543},{0.173913,0.656879},{0.177258,0.659105},{0.180602,0.659105},{0.183946,0.656491},{0.187291,0.651668},{0.190635,0.650267},{0.19398,0.649598},{0.197324,0.647849},{0.200669,0.648116},{0.204013,0.648116},{0.207358,0.647958},{0.210702,0.649283},{0.214047,0.648984},{0.217391,0.644176},{0.220736,0.641571},{0.22408,0.643913},{0.227425,0.643913},{0.230769,0.644158},{0.234114,0.647115},{0.237458,0.653351},{0.240803,0.655650},{0.244147,0.657581},{0.247492,0.658805},{0.250836,0.658805},{0.254181,0.658137},{0.257525,0.659392},{0.26087,0.659872},{0.264214,0.660629},{0.267559,0.659446},{0.270903,0.658827},{0.274247,0.658827},{0.277592,0.658438},{0.280936,0.658812},{0.284281,0.654546},{0.287625,0.652793},{0.29097,0.651615},{0.294314,0.652806},{0.297659,0.652806},{0.301003,0.651808},{0.304348,0.654275},{0.307692,0.651487},{0.311037,0.646162},{0.314381,0.646871},{0.317726,0.647721},{0.32107,0.647721},{0.324415,0.646915},{0.327759,0.646394},{0.331104,0.647585},{0.334448,0.648269},{0.337793,0.650314},{0.341137,0.652946},{0.344482,0.652946},{0.347826,0.651995},{0.351171,0.652141},{0.354515,0.653576},{0.35786,0.654652},{0.361204,0.651164},{0.364548,0.648147},{0.367893,0.648147},{0.371237,0.647685},{0.374582,0.648794},{0.377926,0.649033},{0.381271,0.652339},{0.384615,0.660243},{0.38796,0.660243},{0.391304,0.664479},{0.394649,0.663036},{0.397993,0.661732},{0.401338,0.659239},{0.404682,0.657432},{0.408027,0.657085},{0.411371,0.656291},{0.414716,0.656281},{0.41806,0.657525},{0.421405,0.657977},{0.424749,0.657348},{0.428094,0.654733},{0.431438,0.655325},{0.434783,0.658300},{0.438127,0.658302},{0.441472,0.659774},{0.444816,0.661846},{0.448161,0.662421},{0.451505,0.661836},{0.454849,0.663570},{0.458194,0.665064},{0.461538,0.665066},{0.464883,0.665239},{0.468227,0.663161},{0.471572,0.665418},{0.474916,0.667385},{0.478261,0.661360},{0.481605,0.661828},{0.48495,0.661822},{0.488294,0.662939},{0.491639,0.665666},{0.494983,0.663550},{0.498328,0.659128}};
    //private static double[][] dataset = {{0.334448,0.648269},{0.337793,0.650314},{0.341137,0.652946},{0.344482,0.652946},{0.347826,0.651995},{0.351171,0.652141},{0.354515,0.653576},{0.35786,0.654652},{0.361204,0.651164},{0.364548,0.648147},{0.367893,0.648147},{0.371237,0.647685},{0.374582,0.648794},{0.377926,0.649033},{0.381271,0.652339},{0.384615,0.660243},{0.38796,0.660243},{0.391304,0.664479},{0.394649,0.663036},{0.397993,0.661732},{0.401338,0.659239},{0.404682,0.657432},{0.408027,0.657085},{0.411371,0.656291},{0.414716,0.656281},{0.41806,0.657525},{0.421405,0.657977},{0.424749,0.657348},{0.428094,0.654733},{0.431438,0.655325},{0.434783,0.658300},{0.438127,0.658302},{0.441472,0.659774},{0.444816,0.661846},{0.448161,0.662421},{0.451505,0.661836},{0.454849,0.663570},{0.458194,0.665064},{0.461538,0.665066},{0.464883,0.665239},{0.468227,0.663161},{0.471572,0.665418},{0.474916,0.667385},{0.478261,0.661360},{0.481605,0.661828},{0.48495,0.661822},{0.488294,0.662939},{0.491639,0.665666},{0.494983,0.663550},{0.498328,0.659128},{0.501672,0.659250},{0.505017,0.656674},{0.508361,0.656673},{0.511706,0.659245},{0.51505,0.660794},{0.518395,0.665327},{0.521739,0.669441},{0.525084,0.670583},{0.528428,0.671243},{0.531773,0.671276},{0.535117,0.670979},{0.538462,0.672610},{0.541806,0.673260},{0.545151,0.671193},{0.548495,0.670177},{0.551839,0.670177},{0.555184,0.670177},{0.558528,0.670691},{0.561873,0.672986},{0.565217,0.674548},{0.568562,0.675525},{0.571906,0.678489},{0.575251,0.678489},{0.578595,0.678489},{0.58194,0.678721},{0.585284,0.680585},{0.588629,0.682659},{0.591973,0.684744},{0.595318,0.685293},{0.598662,0.688803},{0.602007,0.688819},{0.605351,0.687567},{0.608696,0.690432},{0.61204,0.692332},{0.615385,0.694194},{0.618729,0.696294},{0.622074,0.701373},{0.625418,0.701376},{0.628763,0.700572},{0.632107,0.702252},{0.635452,0.705998},{0.638796,0.705542},{0.64214,0.701164},{0.645485,0.701088},{0.648829,0.701095},{0.652174,0.700867},{0.655518,0.701423},{0.658863,0.698663},{0.662207,0.699536},{0.665552,0.698692}};
    private static double[][] dataset = {{0.501672,0.659250},{0.505017,0.656674},{0.508361,0.656673},{0.511706,0.659245},{0.51505,0.660794},{0.518395,0.665327},{0.521739,0.669441},{0.525084,0.670583},{0.528428,0.671243},{0.531773,0.671276},{0.535117,0.670979},{0.538462,0.672610},{0.541806,0.673260},{0.545151,0.671193},{0.548495,0.670177},{0.551839,0.670177},{0.555184,0.670177},{0.558528,0.670691},{0.561873,0.672986},{0.565217,0.674548},{0.568562,0.675525},{0.571906,0.678489},{0.575251,0.678489},{0.578595,0.678489},{0.58194,0.678721},{0.585284,0.680585},{0.588629,0.682659},{0.591973,0.684744},{0.595318,0.685293},{0.598662,0.688803},{0.602007,0.688819},{0.605351,0.687567},{0.608696,0.690432},{0.61204,0.692332},{0.615385,0.694194},{0.618729,0.696294},{0.622074,0.701373},{0.625418,0.701376},{0.628763,0.700572},{0.632107,0.702252},{0.635452,0.705998},{0.638796,0.705542},{0.64214,0.701164},{0.645485,0.701088},{0.648829,0.701095},{0.652174,0.700867},{0.655518,0.701423},{0.658863,0.698663},{0.662207,0.699536},{0.665552,0.698692},{0.668896,0.702067},{0.672241,0.702074},{0.675585,0.699411},{0.67893,0.694172},{0.682274,0.690873},{0.685619,0.685110},{0.688963,0.687981},{0.692308,0.689568},{0.695652,0.689570},{0.698997,0.691203},{0.702341,0.692732},{0.705686,0.690269},{0.70903,0.690296},{0.712375,0.690171},{0.715719,0.689425},{0.719064,0.689423},{0.722408,0.690279},{0.725753,0.694597},{0.729097,0.699526},{0.732441,0.698500},{0.735786,0.698439},{0.73913,0.694464},{0.742475,0.694421},{0.745819,0.703846},{0.749164,0.709077},{0.752508,0.716402},{0.755853,0.717368},{0.759197,0.716684},{0.762542,0.720911},{0.765886,0.720908},{0.769231,0.720562},{0.772575,0.716980},{0.77592,0.714659},{0.779264,0.709333},{0.782609,0.705552},{0.785953,0.702902},{0.789298,0.702904},{0.792642,0.704054},{0.795987,0.702506},{0.799331,0.703812},{0.802676,0.703013},{0.80602,0.698837},{0.809365,0.695328},{0.812709,0.695328},{0.816054,0.696648},{0.819398,0.703275},{0.822742,0.707519},{0.826087,0.697156},{0.829431,0.691008},{0.832776,0.690761}};
    
    /**
     * GBP/USD problem example solved by AP powered by Differential Evolution
     */
    public static void gbpusdDErand(String dir) {
        
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
    public static void gbpusdDEbest(String dir) {
        
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
     */
    public static void gbpusdSHADE(String dir) {
        
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
    public static void gbpusdLSHADE(String dir) {
        
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
        
        //String rozsah = "1-100";
        //String rozsah = "51-150";
        //String rozsah = "101-200";
        String rozsah = "151-250";
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nRAND");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");

        gbpusdDErand("E:\\results\\GBPUSD\\" + rozsah + "\\DErand\\");
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nBEST");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        

        gbpusdDEbest("E:\\results\\GBPUSD\\" + rozsah + "\\DEbest\\");
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nSHADE");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");

        gbpusdSHADE("E:\\results\\GBPUSD\\" + rozsah + "\\SHADE\\");
        
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\nLSHADE");
        System.out.println(new Date());
        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        
        gbpusdLSHADE("E:\\results\\GBPUSD\\" + rozsah + "\\LSHADE\\");
        
    }
    
}
