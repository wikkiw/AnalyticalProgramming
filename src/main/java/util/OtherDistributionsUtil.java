package util;

import org.apache.commons.math3.distribution.CauchyDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.RandomGenerator;

/**
 *
 * TEMPORARY SOLUTION FOR CAUCHY AND GAUSSIAN DISTRIBUTION IN SHADE.
 * 
 * @author wiki on 25/11/2015
 */
public class OtherDistributionsUtil {

    public static double cauchy(double a, double b){

        CauchyDistribution cauchy = new CauchyDistribution(a,b);    
        return cauchy.sample();
        
    }
    
    public static double cauchy(RandomGenerator rng, double a, double b){

        CauchyDistribution cauchy = new CauchyDistribution(rng, a,b);    
        return cauchy.sample();
        
    }
    
    public static double normal(double mu, double sigma){

        NormalDistribution normal = new NormalDistribution(mu,sigma);
        return normal.sample();
        
    }
    
    public static double normal(RandomGenerator rng, double mu, double sigma){

        NormalDistribution normal = new NormalDistribution(rng, mu,sigma);
        return normal.sample();
        
    }
    
}
