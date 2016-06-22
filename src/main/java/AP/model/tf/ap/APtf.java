package AP.model.tf.ap;

import java.util.ArrayList;
import AP.model.Individual;
import AP.model.ap.APconst;
import AP.model.tf.TestFunction;

/**
 *
 * @author wiki on 02/06/2016
 */
public class APtf implements TestFunction {

    public APconst ap;
    /**
     * Change generator.
     */
    AP.util.random.Random generator = new AP.util.random.UniformRandom();
    
    public void setGFS(ArrayList GFS, double min, double max) {
        
        if(GFS == null || GFS.isEmpty()){
            System.err.println("GFS cannot be empty!");
        }
        
        this.ap = new APconst(GFS, min, max);
    }
    
    @Override
    public double fitness(Individual individual) {
        return this.fitness(individual.vector);
    }

    @Override
    public double fitness(double[] vector) {
        
        return this.getDistance(vector);
        
    }

    protected double getDistance(double[] vector){
        return 0;
    }
    
    @Override
    public void constrain(Individual individual) {
        for (int i = 0; i < individual.vector.length; i++) {

            if (individual.vector[i] > this.max(individual.vector.length)) {
                individual.vector[i] = this.max(individual.vector.length);
            }
            if (individual.vector[i] < this.min(individual.vector.length)) {
                individual.vector[i] = this.min(individual.vector.length);
            }

        }
    }

    @Override
    public double[] generateTrial(int dim) {
        double[] trial = new double[dim];
        for (int i = 0; i < dim; i++) {
            trial[i] = generator.nextDouble(this.min(dim), this.max(dim));
        }
        return trial;
    }

    @Override
    public double max(int dim) {
        return ap.max;
    }

    @Override
    public double min(int dim) {
        return ap.min;
    }
    
    public int countLength(double[] vector){
        
        Integer[] v, gfs_code;
        int length = vector.length;
        
        v = ap.discretizeVector(vector);
        gfs_code = ap.getGFScode(v);

        for(int j = 0; j < vector.length; j++){
            if(gfs_code[j] == -1){
                length = j;
                break;
            }
        }
        
        return length;
        
    }
    @Override
    public double optimum() {
        return 0;
    }

    @Override
    public double fixedAccLevel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String name() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
