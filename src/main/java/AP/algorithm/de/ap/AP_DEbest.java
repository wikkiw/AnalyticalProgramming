package AP.algorithm.de.ap;

import AP.model.AP_Individual;
import java.util.ArrayList;
import java.util.List;
import AP.model.tf.TestFunction;
import AP.util.random.Random;

/**
 *
 * DEbest algorithm for analytical programming solving
 * 
 * @author wiki on 22/06/2016
 */
public class AP_DEbest extends AP_DErand1bin {
    
    protected List<AP_Individual> initial_population;
    
    /**
     * 
     * @param D
     * @param NP
     * @param MAXFES
     * @param f
     * @param rndGenerator
     * @param F
     * @param CR 
     * @param initPop 
     */
    public AP_DEbest(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR, List<AP_Individual> initPop) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR);
        
        this.initial_population = initPop;
    }
    
    /**
     *
     */
    @Override
    protected void initializePopulation() {

        P = new ArrayList<>();
        double[] vector;
        int initPopSize, popIt = 0;
        
        if(this.initial_population == null){
            initPopSize = 0;
        } else {
            initPopSize = this.initial_population.size();
        }


        for (int i = 0; i < NP; i++) {

            if (checkFES()) {
                return;
            }
            
            if(popIt < initPopSize) {
                vector = this.initial_population.get(popIt).vector.clone();
                popIt++;
            } else {
                vector = tf.generateTrial(D);
            }
            P.add(makeIndividualFromVector(vector));

        }

    }
    
    /**
     *
     * List of parents for mutation x, a, b, c
     *
     * @param xIndex
     * @return
     */
    @Override
    protected AP_Individual[] getParents(int xIndex) {

        AP_Individual[] parrentArray = new AP_Individual[4];
        List<Integer> indexes = new ArrayList<>();
        int index;
        
        parrentArray[0] = P.get(xIndex);
        
        /**
         * best
         */
        index = getBestIndividualInPopulation();
        parrentArray[1] = P.get(index);

        for (int i = 0; i < NP; i++) {
            if (i != xIndex && i != index) {
                indexes.add(i);
            }
        }

        /**
         * b
         */
        index = rndGenerator.nextInt(indexes.size());
        parrentArray[2] = P.get(indexes.get(index));

        indexes.remove(index);

        /**
         * c
         */
        index = rndGenerator.nextInt(indexes.size());
        parrentArray[3] = P.get(indexes.get(index));

        return parrentArray;

    }

    /**
     *
     * @return
     */
    protected int getBestIndividualInPopulation() {

        double fitness = Double.MAX_VALUE;
        int index = 0;
        int i = 0;

        for (AP_Individual ind : P) {

            if (ind.fitness < fitness) {
                fitness = ind.fitness;
                index = i;
            }
            i++;
        }

        return index;

    }
    
}
