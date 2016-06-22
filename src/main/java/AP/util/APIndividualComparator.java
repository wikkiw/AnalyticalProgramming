package AP.util;

import AP.model.AP_Individual;
import java.util.Comparator;

/**
 *
 * @author wiki on 22/06/2016
 */
public class APIndividualComparator implements Comparator<AP_Individual> {
    
    @Override
    public int compare(AP_Individual t, AP_Individual t1) {

        if(t.fitness < t1.fitness) {
            return -1;
        }
        else if(t.fitness == t1.fitness){
            return 0;
        }
        else {
            return 1;
        }

    }
    
}
