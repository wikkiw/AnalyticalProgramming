package AP.example;

import AP.algorithm.Algorithm;
import AP.algorithm.de.ap.AP_DEbest;
import AP.algorithm.de.ap.AP_DErand1bin;
import AP.algorithm.de.ap.AP_ShaDE;
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
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
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

    static String home_dir = "";
    static String path = "E:\\results\\Zuzka_data\\";
    
    /**
     * Sextic problem example solved by AP powered by Differential Evolution
     */
    public static void sexticProblemDE() throws FileNotFoundException, UnsupportedEncodingException {
        
        /**
         * Settings of the evolutionary algorithm - Differential Evolution
         */
        
        Algorithm de;
        int dimension = 60; //Length of an individual - when using functions in GFS with maximum number of required arguments max_arg = 2, 2/3 are designated for program, 1/3 for constant values - for 60 it is 40 and 20
        int NP = 50; //Size of the population - number of competitive solutions
        int generations = 4000; //Stopping criterion - number of generations in evolution
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
        
        int runs = 30; //Number of runs of the regression
        
        double min; //Helping variable for best individual in terms of fitness function value
        double[] bestArray = new double[runs]; //Array for statistics
        int best; //Additional helping variables

        /**
         * Runs of the algorithm with statistical analysis
         */
        for (int k = 0; k < runs; k++) {

            best = 0;

            min = Double.MAX_VALUE;
            
            de = new AP_DErand1bin(dimension, NP, MAXFES, tf, generator, f, cr);

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
            
            PrintWriter writer = new PrintWriter(home_dir + path + "DE_sextic-" + k + ".txt", "UTF-8");

            writer.print("{");

            for (int i = 0; i < ((AP_DErand1bin)de).getBestHistory().size(); i++) {

                writer.print(String.format(Locale.US, "%.10f", ((AP_DErand1bin)de).getBestHistory().get(i).fitness));

                if (i != ((AP_DErand1bin)de).getBestHistory().size() - 1) {
                    writer.print(",");
                }

            }

            writer.print("}");

            writer.close();
            
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
     * Quintic problem example solved by AP powered by Differential Evolution
     */
    public static void quinticProblemDE() throws FileNotFoundException, UnsupportedEncodingException {
        
        /**
         * Settings of the evolutionary algorithm - Differential Evolution
         */
        
        Algorithm de;
        int dimension = 60; //Length of an individual - when using functions in GFS with maximum number of required arguments max_arg = 2, 2/3 are designated for program, 1/3 for constant values - for 60 it is 40 and 20
        int NP = 50; //Size of the population - number of competitive solutions
        int generations = 4000; //Stopping criterion - number of generations in evolution
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
        double[][] dataset_points = new double[][]{{-1.,0.},{-0.959184,-0.00613366},{-0.918367,-0.022522},{-0.877551,-0.0463838},{-0.836735,-0.0752434},{-0.795918,-0.106918},{-0.755102,-0.139502},{-0.714286,-0.171357},{-0.673469,-0.201095},{-0.632653,-0.227565},{-0.591837,-0.249843},{-0.55102,-0.267212},{-0.510204,-0.279155},{-0.469388,-0.285338},{-0.428571,-0.285595},{-0.387755,-0.27992},{-0.346939,-0.268446},{-0.306122,-0.251437},{-0.265306,-0.229272},{-0.22449,-0.202433},{-0.183673,-0.17149},{-0.142857,-0.137086},{-0.102041,-0.0999269},{-0.0612245,-0.0607664},{-0.0204082,-0.0203912},{0.0204082,0.0203912},{0.0612245,0.0607664},{0.102041,0.0999269},{0.142857,0.137086},{0.183673,0.17149},{0.22449,0.202433},{0.265306,0.229272},{0.306122,0.251437},{0.346939,0.268446},{0.387755,0.27992},{0.428571,0.285595},{0.469388,0.285338},{0.510204,0.279155},{0.55102,0.267212},{0.591837,0.249843},{0.632653,0.227565},{0.673469,0.201095},{0.714286,0.171357},{0.755102,0.139502},{0.795918,0.106918},{0.836735,0.0752434},{0.877551,0.0463838},{0.918367,0.022522},{0.959184,0.00613366},{1.,0.}};
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
        
        int runs = 30; //Number of runs of the regression
        
        double min; //Helping variable for best individual in terms of fitness function value
        double[] bestArray = new double[runs]; //Array for statistics
        int best; //Additional helping variables

        /**
         * Runs of the algorithm with statistical analysis
         */
        for (int k = 0; k < runs; k++) {

            best = 0;
            min = Double.MAX_VALUE;
            
            de = new AP_DErand1bin(dimension, NP, MAXFES, tf, generator, f, cr);

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
            
            PrintWriter writer = new PrintWriter(home_dir + path +  "DE_quintic-" + k + ".txt", "UTF-8");

            writer.print("{");

            for (int i = 0; i < ((AP_DErand1bin)de).getBestHistory().size(); i++) {

                writer.print(String.format(Locale.US, "%.10f", ((AP_DErand1bin)de).getBestHistory().get(i).fitness));

                if (i != ((AP_DErand1bin)de).getBestHistory().size() - 1) {
                    writer.print(",");
                }

            }

            writer.print("}");

            writer.close();
            
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
    public static void sexticProblemSHADE() throws FileNotFoundException, UnsupportedEncodingException {
        
        /**
         * Settings of the evolutionary algorithm - Differential Evolution
         */
        
        Algorithm de;
        int dimension = 60; //Length of an individual - when using functions in GFS with maximum number of required arguments max_arg = 2, 2/3 are designated for program, 1/3 for constant values - for 60 it is 40 and 20
        int NP = 50; //Size of the population - number of competitive solutions
        int generations = 4000; //Stopping criterion - number of generations in evolution
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
        
        int runs = 30; //Number of runs of the regression
        
        double min; //Helping variable for best individual in terms of fitness function value
        double[] bestArray = new double[runs]; //Array for statistics
        int best; //Additional helping variables

        /**
         * Runs of the algorithm with statistical analysis
         */
        for (int k = 0; k < runs; k++) {

            best = 0;
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
            
            PrintWriter writer = new PrintWriter(home_dir + path + "SHADE_sextic-" + k + ".txt", "UTF-8");

            writer.print("{");

            for (int i = 0; i < ((AP_ShaDE)de).getBestHistory().size(); i++) {

                writer.print(String.format(Locale.US, "%.10f", ((AP_ShaDE)de).getBestHistory().get(i).fitness));

                if (i != ((AP_ShaDE)de).getBestHistory().size() - 1) {
                    writer.print(",");
                }

            }

            writer.print("}");

            writer.close();
            
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
    public static void quinticProblemSHADE() throws FileNotFoundException, UnsupportedEncodingException {
        
        /**
         * Settings of the evolutionary algorithm - Differential Evolution
         */
        
        Algorithm de;
        int dimension = 60; //Length of an individual - when using functions in GFS with maximum number of required arguments max_arg = 2, 2/3 are designated for program, 1/3 for constant values - for 60 it is 40 and 20
        int NP = 50; //Size of the population - number of competitive solutions
        int generations = 4000; //Stopping criterion - number of generations in evolution
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
        double[][] dataset_points = new double[][]{{-1.,0.},{-0.959184,-0.00613366},{-0.918367,-0.022522},{-0.877551,-0.0463838},{-0.836735,-0.0752434},{-0.795918,-0.106918},{-0.755102,-0.139502},{-0.714286,-0.171357},{-0.673469,-0.201095},{-0.632653,-0.227565},{-0.591837,-0.249843},{-0.55102,-0.267212},{-0.510204,-0.279155},{-0.469388,-0.285338},{-0.428571,-0.285595},{-0.387755,-0.27992},{-0.346939,-0.268446},{-0.306122,-0.251437},{-0.265306,-0.229272},{-0.22449,-0.202433},{-0.183673,-0.17149},{-0.142857,-0.137086},{-0.102041,-0.0999269},{-0.0612245,-0.0607664},{-0.0204082,-0.0203912},{0.0204082,0.0203912},{0.0612245,0.0607664},{0.102041,0.0999269},{0.142857,0.137086},{0.183673,0.17149},{0.22449,0.202433},{0.265306,0.229272},{0.306122,0.251437},{0.346939,0.268446},{0.387755,0.27992},{0.428571,0.285595},{0.469388,0.285338},{0.510204,0.279155},{0.55102,0.267212},{0.591837,0.249843},{0.632653,0.227565},{0.673469,0.201095},{0.714286,0.171357},{0.755102,0.139502},{0.795918,0.106918},{0.836735,0.0752434},{0.877551,0.0463838},{0.918367,0.022522},{0.959184,0.00613366},{1.,0.}};
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
        
        int runs = 30; //Number of runs of the regression
        
        double min; //Helping variable for best individual in terms of fitness function value
        double[] bestArray = new double[runs]; //Array for statistics
        int best; //Additional helping variables

        /**
         * Runs of the algorithm with statistical analysis
         */
        for (int k = 0; k < runs; k++) {

            best = 0;
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
            
            PrintWriter writer = new PrintWriter(home_dir + path + "SHADE_quintic-" + k + ".txt", "UTF-8");

            writer.print("{");

            for (int i = 0; i < ((AP_ShaDE)de).getBestHistory().size(); i++) {

                writer.print(String.format(Locale.US, "%.10f", ((AP_ShaDE)de).getBestHistory().get(i).fitness));

                if (i != ((AP_ShaDE)de).getBestHistory().size() - 1) {
                    writer.print(",");
                }

            }

            writer.print("}");

            writer.close();
            
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
    public static void threeSineProblemSHADE() throws FileNotFoundException, UnsupportedEncodingException {
        
        /**
         * Settings of the evolutionary algorithm - Differential Evolution
         */
        
        Algorithm de;
        int dimension = 180; //Length of an individual - when using functions in GFS with maximum number of required arguments max_arg = 2, 2/3 are designated for program, 1/3 for constant values - for 60 it is 40 and 20
        int NP = 100; //Size of the population - number of competitive solutions
        int generations = 4000; //Stopping criterion - number of generations in evolution
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
        double[][] dataset_points = new double[][]{{-3.14159,0.},{-3.01336,-0.24949},{-2.88514,-0.45862},{-2.75691,-0.593997},{-2.62868,-0.635061},{-2.50045,-0.577911},{-2.37222,-0.436474},{-2.24399,-0.240787},{-2.11577,-0.0326137},{-1.98754,0.140974},{-1.85931,0.235095},{-1.73108,0.214526},{-1.60285,0.0599631},{-1.47463,-0.22787},{-1.3464,-0.62698},{-1.21817,-1.09598},{-1.08994,-1.57889},{-0.961712,-2.0123},{-0.833484,-2.33377},{-0.705255,-2.49055},{-0.577027,-2.44713},{-0.448799,-2.19064},{-0.320571,-1.73339},{-0.192342,-1.11196},{-0.0641141,-0.383106},{0.0641141,0.383106},{0.192342,1.11196},{0.320571,1.73339},{0.448799,2.19064},{0.577027,2.44713},{0.705255,2.49055},{0.833484,2.33377},{0.961712,2.0123},{1.08994,1.57889},{1.21817,1.09598},{1.3464,0.62698},{1.47463,0.22787},{1.60285,-0.0599631},{1.73108,-0.214526},{1.85931,-0.235095},{1.98754,-0.140974},{2.11577,0.0326137},{2.24399,0.240787},{2.37222,0.436474},{2.50045,0.577911},{2.62868,0.635061},{2.75691,0.593997},{2.88514,0.45862},{3.01336,0.24949},{3.14159,0.}};
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
        
        int runs = 30; //Number of runs of the regression
        
        double min; //Helping variable for best individual in terms of fitness function value
        double[] bestArray = new double[runs]; //Array for statistics
        int best; //Additional helping variables

        /**
         * Runs of the algorithm with statistical analysis
         */
        for (int k = 0; k < runs; k++) {

            best = 0;
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
            
            PrintWriter writer = new PrintWriter(home_dir + path + "SHADE_3sine-" + k + ".txt", "UTF-8");

            writer.print("{");

            for (int i = 0; i < ((AP_ShaDE)de).getBestHistory().size(); i++) {

                writer.print(String.format(Locale.US, "%.10f", ((AP_ShaDE)de).getBestHistory().get(i).fitness));

                if (i != ((AP_ShaDE)de).getBestHistory().size() - 1) {
                    writer.print(",");
                }

            }

            writer.print("}");

            writer.close();
            
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
    
    public static void fourSineProblemSHADE() throws FileNotFoundException, UnsupportedEncodingException {
        
        /**
         * Settings of the evolutionary algorithm - Differential Evolution
         */
        
        Algorithm de;
        int dimension = 180; //Length of an individual - when using functions in GFS with maximum number of required arguments max_arg = 2, 2/3 are designated for program, 1/3 for constant values - for 60 it is 40 and 20
        int NP = 100; //Size of the population - number of competitive solutions
        int generations = 4000; //Stopping criterion - number of generations in evolution
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
        double[][] dataset_points = new double[][]{{-3.14159,0.},{-3.01336,0.241228},{-2.88514,0.396523},{-2.75691,0.405489},{-2.62868,0.251538},{-2.50045,-0.0323762},{-2.37222,-0.372404},{-2.24399,-0.674671},{-2.11577,-0.852786},{-1.98754,-0.854406},{-1.85931,-0.679317},{-1.73108,-0.383585},{-1.60285,-0.067914},{-1.47463,0.147397},{-1.3464,0.154851},{-1.21817,-0.108797},{-1.08994,-0.640426},{-0.961712,-1.36407},{-0.833484,-2.14261},{-0.705255,-2.80566},{-0.577027,-3.18741},{-0.448799,-3.16557},{-0.320571,-2.69206},{-0.192342,-1.80764},{-0.0641141,-0.636761},{0.0641141,0.636761},{0.192342,1.80764},{0.320571,2.69206},{0.448799,3.16557},{0.577027,3.18741},{0.705255,2.80566},{0.833484,2.14261},{0.961712,1.36407},{1.08994,0.640426},{1.21817,0.108797},{1.3464,-0.154851},{1.47463,-0.147397},{1.60285,0.067914},{1.73108,0.383585},{1.85931,0.679317},{1.98754,0.854406},{2.11577,0.852786},{2.24399,0.674671},{2.37222,0.372404},{2.50045,0.0323762},{2.62868,-0.251538},{2.75691,-0.405489},{2.88514,-0.396523},{3.01336,-0.241228},{3.14159,0.}};
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
        
        int runs = 30; //Number of runs of the regression
        
        double min; //Helping variable for best individual in terms of fitness function value
        double[] bestArray = new double[runs]; //Array for statistics
        int best; //Additional helping variables

        /**
         * Runs of the algorithm with statistical analysis
         */
        for (int k = 0; k < runs; k++) {

            best = 0;
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
            
            PrintWriter writer = new PrintWriter(home_dir + path + "SHADE_4sine-" + k + ".txt", "UTF-8");

            writer.print("{");

            for (int i = 0; i < ((AP_ShaDE)de).getBestHistory().size(); i++) {

                writer.print(String.format(Locale.US, "%.10f", ((AP_ShaDE)de).getBestHistory().get(i).fitness));

                if (i != ((AP_ShaDE)de).getBestHistory().size() - 1) {
                    writer.print(",");
                }

            }

            writer.print("}");

            writer.close();
            
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
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        
//        System.out.println("\n\n\n------------------------------------");
//        System.out.println("-----------------DE-------------------");
//        System.out.println("------------------------------------\n\n\n");
//        
//        /**
//         * Example run with DE algorithm
//         */
//        quinticProblemDE();
//
//        /**
//         * Example run with DE algorithm
//         */
//        sexticProblemDE();
        
        System.out.println("\n\n\n------------------------------------");
        System.out.println("-----------------SHADE-------------------");
        System.out.println("------------------------------------\n\n\n");

        /**
         * Example run with SHADE algorithm
         */
        threeSineProblemSHADE();
        
        /**
         * Example run with SHADE algorithm
         */
        fourSineProblemSHADE();
        
    }
    
}
