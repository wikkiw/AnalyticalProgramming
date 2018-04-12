package AP.example;

import AP.algorithm.Algorithm;
import AP.algorithm.cma.ap.AP_IPOP_CMA_ES;
import AP.algorithm.de.ap.AP_DEbest;
import AP.algorithm.de.ap.AP_ShaDE;
import AP.algorithm.pso.ap.AP_HCLPSO;
import AP.model.AP_Individual;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.DoubleStream;
import AP.model.ap.objects.AP_Const_static;
import AP.model.ap.objects.AP_Div;
import AP.model.ap.objects.AP_Multiply;
import AP.model.ap.objects.AP_Plus;
import AP.model.ap.objects.AP_Sub;
import AP.model.ap.objects.AP_object;
import AP.model.ap.objects.AP_x1;
import AP.model.tf.ap.APtf;
import AP.model.tf.ap.regression.APdataset;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * This class describes the example of using Analytical Programming with constants to
 * perform a symbolic regression of a sextic problem.
 * 
 * @author wiki on 02/06/2016
 */
public class sexticProblemExample {

    /**
     * Sextic problem example solved by AP powered by Differential Evolution
     */
    public static void sexticProblemDE() {
        
        /**
         * Settings of the evolutionary algorithm - Differential Evolution
         */
        
        Algorithm de;
        int dimension = 60; //Length of an individual - when using functions in GFS with maximum number of required arguments max_arg = 2, 2/3 are designated for program, 1/3 for constant values - for 60 it is 40 and 20
        int NP = 10; //Size of the population - number of competitive solutions
        int generations = 1000; //Stopping criterion - number of generations in evolution
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
        double[][] dataset_points = new double[][]{{-1.,0.},{-0.959184,0.00588331},{-0.918367,0.0206835},{-0.877551,0.0407041},{-0.836735,0.0629588},{-0.795918,0.0850978},{-0.755102,0.105338},{-0.714286,0.122398},{-0.673469,0.135431},{-0.632653,0.14397},{-0.591837,0.147866},{-0.55102,0.147239},{-0.510204,0.142426},{-0.469388,0.133934},{-0.428571,0.122398},{-0.387755,0.10854},{-0.346939,0.0931342},{-0.306122,0.0769704},{-0.265306,0.0608273},{-0.22449,0.0454442},{-0.183673,0.0314981},{-0.142857,0.0195837},{-0.102041,0.0101966},{-0.0612245,0.00372039},{-0.0204082,0.000416146},{0.0204082,0.000416146},{0.0612245,0.00372039},{0.102041,0.0101966},{0.142857,0.0195837},{0.183673,0.0314981},{0.22449,0.0454442},{0.265306,0.0608273},{0.306122,0.0769704},{0.346939,0.0931342},{0.387755,0.10854},{0.428571,0.122398},{0.469388,0.133934},{0.510204,0.142426},{0.55102,0.147239},{0.591837,0.147866},{0.632653,0.14397},{0.673469,0.135431},{0.714286,0.122398},{0.755102,0.105338},{0.795918,0.0850978},{0.836735,0.0629588},{0.877551,0.0407041},{0.918367,0.0206835},{0.959184,0.00588331},{1.,0.}};
        //General Functional Set settings
        ArrayList<AP_object> GFS = new ArrayList<>();
        GFS.add(new AP_Plus()); //Addition
        GFS.add(new AP_Sub()); //Subtraction
        GFS.add(new AP_Multiply()); //Mulitplication
        GFS.add(new AP_Div()); //Divison
        GFS.add(new AP_x1()); //Independent variable x1
        GFS.add(new AP_Const_static()); //Constant object - value is evolved in the extension of the individuals
        //More functions and terminals for GFS can be found or added to model.ap.objects
        
        double const_min = -10; //Minimum value of constants
        double const_max = 10; //Maximum value of constants
        
        APtf tf = new APdataset(dataset_points, GFS, const_min, const_max); //Dataset constructor

        /**
         * Additional variables
         */
        
        int runs = 10; //Number of runs of the regression
        
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
     * Sextic problem example solved by AP powered by Success-History based Adaptive Differential Evolution
     */
    public static void sexticProblemSHADE() {
        
        /**
         * Settings of the evolutionary algorithm - Differential Evolution
         */
        
        Algorithm de;
        int dimension = 60; //Length of an individual - when using functions in GFS with maximum number of required arguments max_arg = 2, 2/3 are designated for program, 1/3 for constant values - for 60 it is 40 and 20
        int NP = 10; //Size of the population - number of competitive solutions
        int generations = 1000; //Stopping criterion - number of generations in evolution
        int MAXFES = generations * NP; //Number of fitness function evaluations
        int H = NP; //Historical memory size
        AP.util.random.Random generator = new AP.util.random.UniformRandom(); //Random number generator
        
        /**
         * Symbolic regression part
         * 
         * Settings of the dataset which is regressed
         * Example: Sextic problem
         */
        
        // 50 points from range <-1, 1>, another type of data providing is possible
        double[][] dataset_points = new double[][]{{-1.,0.},{-0.959184,0.00588331},{-0.918367,0.0206835},{-0.877551,0.0407041},{-0.836735,0.0629588},{-0.795918,0.0850978},{-0.755102,0.105338},{-0.714286,0.122398},{-0.673469,0.135431},{-0.632653,0.14397},{-0.591837,0.147866},{-0.55102,0.147239},{-0.510204,0.142426},{-0.469388,0.133934},{-0.428571,0.122398},{-0.387755,0.10854},{-0.346939,0.0931342},{-0.306122,0.0769704},{-0.265306,0.0608273},{-0.22449,0.0454442},{-0.183673,0.0314981},{-0.142857,0.0195837},{-0.102041,0.0101966},{-0.0612245,0.00372039},{-0.0204082,0.000416146},{0.0204082,0.000416146},{0.0612245,0.00372039},{0.102041,0.0101966},{0.142857,0.0195837},{0.183673,0.0314981},{0.22449,0.0454442},{0.265306,0.0608273},{0.306122,0.0769704},{0.346939,0.0931342},{0.387755,0.10854},{0.428571,0.122398},{0.469388,0.133934},{0.510204,0.142426},{0.55102,0.147239},{0.591837,0.147866},{0.632653,0.14397},{0.673469,0.135431},{0.714286,0.122398},{0.755102,0.105338},{0.795918,0.0850978},{0.836735,0.0629588},{0.877551,0.0407041},{0.918367,0.0206835},{0.959184,0.00588331},{1.,0.}};
        //General Functional Set settings
        ArrayList<AP_object> GFS = new ArrayList<>();
        GFS.add(new AP_Plus()); //Addition
        GFS.add(new AP_Sub()); //Subtraction
        GFS.add(new AP_Multiply()); //Mulitplication
        GFS.add(new AP_Div()); //Divison
        GFS.add(new AP_x1()); //Independent variable x1
        GFS.add(new AP_Const_static()); //Constant object - value is evolved in the extension of the individuals
        //More functions and terminals for GFS can be found or added to model.ap.objects
        
        double const_min = -10; //Minimum value of constants
        double const_max = 10; //Maximum value of constants
        
        APtf tf = new APdataset(dataset_points, GFS, const_min, const_max); //Dataset constructor

        /**
         * Additional variables
         */
        
        int runs = 10; //Number of runs of the regression
        
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
     * Sextic problem example solved by AP powered by HCLPSO
     */
    public static void sexticProblemHCLPSO() {
        
        /**
         * Settings of the evolutionary algorithm - Differential Evolution
         */
        
        Algorithm hclpso;
        int dimension = 60; //Length of an individual - when using functions in GFS with maximum number of required arguments max_arg = 2, 2/3 are designated for program, 1/3 for constant values - for 60 it is 40 and 20
        int NP = 10; //Size of the population - number of competitive solutions
        int generations = 1000; //Stopping criterion - number of generations in evolution
        int MAXFES = generations * NP; //Number of fitness function evaluations
        AP.util.random.Random generator = new AP.util.random.UniformRandom(); //Random number generator
        
        /**
         * Symbolic regression part
         * 
         * Settings of the dataset which is regressed
         * Example: Sextic problem
         */
        
        // 50 points from range <-1, 1>, another type of data providing is possible
        double[][] dataset_points = new double[][]{{-1.,0.},{-0.959184,0.00588331},{-0.918367,0.0206835},{-0.877551,0.0407041},{-0.836735,0.0629588},{-0.795918,0.0850978},{-0.755102,0.105338},{-0.714286,0.122398},{-0.673469,0.135431},{-0.632653,0.14397},{-0.591837,0.147866},{-0.55102,0.147239},{-0.510204,0.142426},{-0.469388,0.133934},{-0.428571,0.122398},{-0.387755,0.10854},{-0.346939,0.0931342},{-0.306122,0.0769704},{-0.265306,0.0608273},{-0.22449,0.0454442},{-0.183673,0.0314981},{-0.142857,0.0195837},{-0.102041,0.0101966},{-0.0612245,0.00372039},{-0.0204082,0.000416146},{0.0204082,0.000416146},{0.0612245,0.00372039},{0.102041,0.0101966},{0.142857,0.0195837},{0.183673,0.0314981},{0.22449,0.0454442},{0.265306,0.0608273},{0.306122,0.0769704},{0.346939,0.0931342},{0.387755,0.10854},{0.428571,0.122398},{0.469388,0.133934},{0.510204,0.142426},{0.55102,0.147239},{0.591837,0.147866},{0.632653,0.14397},{0.673469,0.135431},{0.714286,0.122398},{0.755102,0.105338},{0.795918,0.0850978},{0.836735,0.0629588},{0.877551,0.0407041},{0.918367,0.0206835},{0.959184,0.00588331},{1.,0.}};
        //General Functional Set settings
        ArrayList<AP_object> GFS = new ArrayList<>();
        GFS.add(new AP_Plus()); //Addition
        GFS.add(new AP_Sub()); //Subtraction
        GFS.add(new AP_Multiply()); //Mulitplication
        GFS.add(new AP_Div()); //Divison
        GFS.add(new AP_x1()); //Independent variable x1
        GFS.add(new AP_Const_static()); //Constant object - value is evolved in the extension of the individuals
        //More functions and terminals for GFS can be found or added to model.ap.objects
        
        double const_min = -10; //Minimum value of constants
        double const_max = 10; //Maximum value of constants
        
        APtf tf = new APdataset(dataset_points, GFS, const_min, const_max); //Dataset constructor

        /**
         * Additional variables
         */
        
        int runs = 10; //Number of runs of the regression
        
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
            
            hclpso = new AP_HCLPSO(dimension, NP, MAXFES, tf, generator);

            hclpso.run();

            bestArray[k] = hclpso.getBest().fitness - tf.optimum();

            /**
             * AP best result value and equation.
             */

            System.out.println("=================================");
            System.out.println("Best obtained fitness function value: \n" + (hclpso.getBest().fitness - tf.optimum()));
            System.out.println("Equation: \n" + ((AP_Individual) hclpso.getBest()).equation);
            System.out.println("Vector: \n" + Arrays.toString(((AP_Individual) hclpso.getBest()).vector));
            System.out.println("=================================");
            
            for(AP_Individual ind : ((AP_HCLPSO)hclpso).getBestHistory()){
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
     * Sextic problem example solved by AP powered by IPOP-CMA-ES
     */
    public static void sexticProblemCMAES() {
        
        /**
         * Settings of the evolutionary algorithm - Differential Evolution
         */
        
        Algorithm cma;
        int dimension = 60; //Length of an individual - when using functions in GFS with maximum number of required arguments max_arg = 2, 2/3 are designated for program, 1/3 for constant values - for 60 it is 40 and 20
        int NP = 10; //Size of the population - number of competitive solutions
        int generations = 1000; //Stopping criterion - number of generations in evolution
        int MAXFES = generations * NP; //Number of fitness function evaluations
        AP.util.random.Random generator = new AP.util.random.UniformRandom(); //Random number generator
        
        /**
         * Symbolic regression part
         * 
         * Settings of the dataset which is regressed
         * Example: Sextic problem
         */
        
        // 50 points from range <-1, 1>, another type of data providing is possible
        double[][] dataset_points = new double[][]{{-1.,0.},{-0.959184,0.00588331},{-0.918367,0.0206835},{-0.877551,0.0407041},{-0.836735,0.0629588},{-0.795918,0.0850978},{-0.755102,0.105338},{-0.714286,0.122398},{-0.673469,0.135431},{-0.632653,0.14397},{-0.591837,0.147866},{-0.55102,0.147239},{-0.510204,0.142426},{-0.469388,0.133934},{-0.428571,0.122398},{-0.387755,0.10854},{-0.346939,0.0931342},{-0.306122,0.0769704},{-0.265306,0.0608273},{-0.22449,0.0454442},{-0.183673,0.0314981},{-0.142857,0.0195837},{-0.102041,0.0101966},{-0.0612245,0.00372039},{-0.0204082,0.000416146},{0.0204082,0.000416146},{0.0612245,0.00372039},{0.102041,0.0101966},{0.142857,0.0195837},{0.183673,0.0314981},{0.22449,0.0454442},{0.265306,0.0608273},{0.306122,0.0769704},{0.346939,0.0931342},{0.387755,0.10854},{0.428571,0.122398},{0.469388,0.133934},{0.510204,0.142426},{0.55102,0.147239},{0.591837,0.147866},{0.632653,0.14397},{0.673469,0.135431},{0.714286,0.122398},{0.755102,0.105338},{0.795918,0.0850978},{0.836735,0.0629588},{0.877551,0.0407041},{0.918367,0.0206835},{0.959184,0.00588331},{1.,0.}};
        //General Functional Set settings
        ArrayList<AP_object> GFS = new ArrayList<>();
        GFS.add(new AP_Plus()); //Addition
        GFS.add(new AP_Sub()); //Subtraction
        GFS.add(new AP_Multiply()); //Mulitplication
        GFS.add(new AP_Div()); //Divison
        GFS.add(new AP_x1()); //Independent variable x1
        GFS.add(new AP_Const_static()); //Constant object - value is evolved in the extension of the individuals
        //More functions and terminals for GFS can be found or added to model.ap.objects
        
        double const_min = -10; //Minimum value of constants
        double const_max = 10; //Maximum value of constants
        
        APtf tf = new APdataset(dataset_points, GFS, const_min, const_max); //Dataset constructor

        /**
         * Additional variables
         */
        
        int runs = 10; //Number of runs of the regression
        
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
            
            cma = new AP_IPOP_CMA_ES(dimension, MAXFES, tf, generator);

            cma.run();

            bestArray[k] = cma.getBest().fitness - tf.optimum();

            /**
             * AP best result value and equation.
             */

            System.out.println("=================================");
            System.out.println("Best obtained fitness function value: \n" + (cma.getBest().fitness - tf.optimum()));
            System.out.println("Equation: \n" + ((AP_Individual) cma.getBest()).equation);
            System.out.println("Vector: \n" + Arrays.toString(((AP_Individual) cma.getBest()).vector));
            System.out.println("=================================");
            
            for(AP_Individual ind : ((AP_IPOP_CMA_ES)cma).getBestHistory()){
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
        
        /**
         * Example run with IPOP_CMA_ES algorithm
         */
        sexticProblemCMAES();
        
        /**
         * Example run with HCLPSO algorithm
         */
        //sexticProblemHCLPSO();
        
        /**
         * Example run with DE algorithm
         */
        //sexticProblemDE();
        
        /**
         * Example run with SHADE algorithm
         */
        //sexticProblemSHADE();
        
    }
    
}
