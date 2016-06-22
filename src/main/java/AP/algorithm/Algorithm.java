package AP.algorithm;

import AP.model.Individual;
import AP.model.tf.TestFunction;

import java.util.List;

/**
 * Created by jakub on 27/10/15.
 */
public interface Algorithm {

    Individual run();

    List<? extends Individual> getPopulation();

    TestFunction getTestFunction();

    default Individual getBest() {
        Individual best = getPopulation().get(0);
        for (Individual individual : getPopulation()) {
            if (individual.fitness < best.fitness) best = individual;
        }
        return best;
//        getPopulation().sort((o1, o2) -> Double.compare(o1.fitness, o2.fitness));
//        return getPopulation().get(0);
    }

    String getName();
}
