package util.random;

/**
 *
 * @author adam on 24/11/2015
 */
public interface Random {

    double nextDouble();

    int nextInt(int bound);

    default double nextDouble(double min, double max) {
        return (nextDouble() * (max - min) + min);
//        return (nextDouble() * (max - min + 1) + min) % (max - min + 1);
    }

    @Override
    public String toString();

}
