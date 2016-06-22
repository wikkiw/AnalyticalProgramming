package AP.util;

import AP.model.AP_Individual;

/**
 * Created by jakub on 27/10/15.
 */
public class IndividualUtil {

    public static AP_Individual clipInBounds(AP_Individual individual, double min, double max) {
        for (int d = 0; d < individual.vector.length; d++)
            if (individual.vector[d] > max) individual.vector[d] = max;
            else if (individual.vector[d] < min) individual.vector[d] = min;
        return individual;
    }

    public static AP_Individual randIfOutOfBounds(AP_Individual individual, double min, double max) {
        for (int d = 0; d < individual.vector.length; d++)
            if (individual.vector[d] > max || individual.vector[d] < min)
                individual.vector[d] = RandomUtil.nextDouble(min, max);
        return individual;
    }
}
