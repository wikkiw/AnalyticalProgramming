package AP.util.random;

/**
 *
 * @author adam on 24/11/2015
 */
public class UniformRandom implements Random {

    final private static java.util.Random rnd = new java.util.Random();

    @Override
    public double nextDouble() {
        return rnd.nextDouble();
    }

    @Override
    public int nextInt(int bound) {
        return rnd.nextInt(bound);
    }
    
    @Override
    public String toString() {
        return "Uniform";
    }

}
