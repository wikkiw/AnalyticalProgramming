package AP.algorithm.de.ap;

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
    
    /**
     * 
     * @param D
     * @param NP
     * @param MAXFES
     * @param f
     * @param rndGenerator
     * @param F
     * @param CR 
     */
    public AP_DEbest(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator, double F, double CR) {
        super(D, NP, MAXFES, f, rndGenerator, F, CR);
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
        int index = -1;
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
