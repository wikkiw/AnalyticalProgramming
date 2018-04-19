/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AP.algorithm.de.ap;

import AP.model.AP_Individual;
import AP.algorithm.Algorithm;
import AP.model.tf.TestFunction;
import AP.util.random.Random;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 *
 * @author adam 5/12/2018
 */
public class AP_jDE implements Algorithm {

    protected int D;
    protected int G;
    protected int NP;
    protected List<AP_Individual> P;
    protected int FES;
    protected int MAXFES;
    protected TestFunction tf;
    protected AP_Individual best;
    protected List<AP_Individual> bestHistory;
    protected AP.util.random.Random rndGenerator;
    protected int id;
    protected double[] F;
    protected double[] CR;
    
    protected double tau1 = 0.1, tau2 = 0.1;
    protected double Fl = 0.1, Fu = 0.9;

    /**
     * Population diversity
     */
    List<Double> P_div_history;

    public AP_jDE(int D, int NP, int MAXFES, TestFunction f, Random rndGenerator) {
        this.D = D;
        this.G = 0;
        this.NP = NP;
        this.MAXFES = MAXFES;
        this.tf = f;
        this.rndGenerator = rndGenerator;
        this.id = 0;
        this.F = new double[NP];
        this.CR = new double[NP];
        for(int i = 0; i < NP; i++) {
            this.F[i] = Fl + rndGenerator.nextDouble()*Fu;
            this.CR[i] = rndGenerator.nextDouble();
        }
    }
    
    /**
     * Population diversity according to Polakova
     * @param pop
     * @return 
     */
    public double calculateDiversity(List<AP_Individual> pop) {
        
        if(pop == null || pop.isEmpty()) {
            return -1;
        }
        
        double[] means = new double[this.D];
        for(int i = 0; i < this.D; i++) {
              means[i] = 0;  
        }
        pop.stream().forEach((ind) -> {
            for(int i = 0; i < this.D; i++) {
                means[i] += (ind.vector[i]/(double) pop.size());
            }
        });
        
        double DI = 0;
        
        for(AP_Individual ind : pop) {
            for(int i = 0; i < this.D; i++) {
                DI += Math.pow(ind.vector[i] - means[i], 2);
            }
        }
        
        DI = Math.sqrt((1.0 / (double) pop.size())*DI);
        
        
        return DI;
        
    }

    @Override
    public AP_Individual run() {

        /**
         * Initial population
         */
        initializePopulation();
        if (checkFES()) {
            return best;
        }

        List<AP_Individual> newPop;
        AP_Individual x, trial;
        double[] u, v;
        AP_Individual[] parrentArray;
        double Fval, CRval;
        
        /**
         * Diversity
         */
        this.P_div_history = new ArrayList<>();
        this.P_div_history.add(this.calculateDiversity(this.P));

        /**
         * generation itteration
         */
        while (true) {

            G++;
            newPop = new ArrayList<>();

            /**
             * Iteration through all individuals in generation.
             */
            for (int xIter = 0; xIter < NP; xIter++) {

                /**
                 * Parent selection
                 */
                parrentArray = getParents(xIter);
                x = parrentArray[0];

                /**
                 * Mutation
                 */
                if(rndGenerator.nextDouble() < tau1) {
                    Fval = Fl + rndGenerator.nextDouble()*Fu;
                } else {
                    Fval = this.F[xIter];
                }
                v = mutation(parrentArray, Fval);

                /**
                 * Crossover
                 */
                if(rndGenerator.nextDouble() < tau2) {
                    CRval = rndGenerator.nextDouble();
                } else {
                    CRval = this.CR[xIter];
                }
                u = crossover(x.vector, v, CRval);

                /**
                 * Constrain check
                 */
                u = constrainCheck(u, x.vector);
                
                /**
                 * Trial
                 */
                trial = new AP_Individual(x.id, u, tf.fitness(u));

                /**
                 * New generation building
                 */
                if (trial.fitness < x.fitness) {
                    newPop.add(trial);
                    /**
                     * Adaptation
                     */
                    this.F[xIter] = Fval;
                    this.CR[xIter] = CRval;
                } else {
                    newPop.add(x);
                }
                
                this.FES++;
                this.isBest(trial);
                this.writeHistory();
                if (checkFES()) {
                    break;
                }

            }
            
            if (checkFES()) {
                break;
            }

            P = newPop;
            
            /**
             * Diversity and clustering
             */
            this.P_div_history.add(this.calculateDiversity(this.P));

        }
        
        return best;
    }
    
    /**
     * Writes population diversity history into a file
     * 
     * @param path 
     */
    public void writePopDiversityHistory(String path) {
        
        try {
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            
            writer.print("{");
            
            for(int i = 0; i < this.P_div_history.size(); i++) {
                
                
                writer.print(String.format(Locale.US, "%.10f", this.P_div_history.get(i)));
                
                if(i != this.P_div_history.size()-1) {
                    writer.print(",");
                }
                
            }
            
            writer.print("}");
            
            writer.close();
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(AP_jDE.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }

    /**
     * 
     * @param u
     * @param x
     * @return 
     */
    protected double[] constrainCheck(double[] u, double[] x){
        /**
         * Constrain check
         */
        for (int d = 0; d < this.D; d++) {
            if (u[d] < this.tf.min(this.D)) {
                u[d] = (this.tf.min(this.D) + x[d]) / 2.0;
            } else if (u[d] > this.tf.max(this.D)) {
                u[d] = (this.tf.max(this.D) + x[d]) / 2.0;
            }
        }
        
        return u;
    }
    
    /**
     *
     * @param x
     * @param u
     * @param CR
     * @return
     */
    protected double[] crossover(double[] x, double[] u, double CR) {

        double[] v = new double[D];
        int jrand = rndGenerator.nextInt(D);

        for (int i = 0; i < D; i++) {

            if (i == jrand || rndGenerator.nextDouble() < CR) {
                v[i] = u[i];
            } else {
                v[i] = x[i];
            }

        }

        return v;

    }

    protected void constrain(AP_Individual individual){
        
        tf.constrain(individual);
        
    }
    
    /**
     *
     * @param parentArray
     * @param F
     * @return
     */
    protected double[] mutation(AP_Individual[] parentArray, double F) {

        double[] u = new double[D];
        double[] a = parentArray[1].vector;
        double[] b = parentArray[2].vector;
        double[] c = parentArray[3].vector;

        for (int i = 0; i < D; i++) {

            u[i] = a[i] + F * (b[i] - c[i]);

        }

        return u;

    }

    /**
     *
     * List of parents for mutation x, a, b, c
     *
     * @param xIndex
     * @return
     */
    protected AP_Individual[] getParents(int xIndex) {

        AP_Individual[] parrentArray = new AP_Individual[4];
        List<Integer> indexes = new ArrayList<>();
        int index;

        for (int i = 0; i < NP; i++) {
            if (i != xIndex) {
                indexes.add(i);
            }
        }

        parrentArray[0] = P.get(xIndex);

        /**
         * a
         */
        index = rndGenerator.nextInt(indexes.size());
        parrentArray[1] = P.get(indexes.get(index));

        indexes.remove(index);

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
     * Creation of initial population.
     */
    protected void initializePopulation(){
        
        /**
         * Initial population
         */
        id = 0;
        double[] features = new double[this.D];
        this.P = new ArrayList<>();
        AP_Individual ind;

        for (int i = 0; i < this.NP; i++) {
            id = i;
            features = this.tf.generateTrial(this.D).clone();
            ind = new AP_Individual(String.valueOf(id), features, this.tf.fitness(features));
            this.isBest(ind);
            this.P.add(ind);
            this.FES++;
            this.writeHistory();
        }
        
    }

    /**
     *
     * @return
     */
    protected boolean checkFES() {
        return (FES > MAXFES);
    }

    /**
     *
     * @param vector
     * @return
     */
    protected AP_Individual makeAP_IndividualFromVector(double[] vector) {

        AP_Individual ind = new AP_Individual();
        ind.id = String.valueOf(id);
        id++;
        ind.vector = vector;
        constrain(ind);
        ind.fitness = tf.fitness(vector);
        FES++;
        isBest(ind);
        writeHistory();

        return (ind);
    }

    /**
     *
     */
    protected void writeHistory() {
        if (bestHistory == null) {
            bestHistory = new ArrayList<>();
        }
        bestHistory.add(best);
    }

    /**
     *
     * @param ind
     * @return
     */
    protected boolean isBest(AP_Individual ind) {

        if (best == null || ind.fitness < best.fitness) {
            best = ind;
            return true;
        }

        return false;

    }

    @Override
    public List<? extends AP_Individual> getPopulation() {
        return P;
    }

    @Override
    public TestFunction getTestFunction() {
        return tf;
    }

    @Override
    public String getName() {
        return "AP_jDE";
    }

    // <editor-fold defaultstate="collapsed" desc="getters and setters">
    public int getD() {
        return D;
    }

    public void setD(int D) {
        this.D = D;
    }

    public int getG() {
        return G;
    }

    public void setG(int G) {
        this.G = G;
    }

    public int getNP() {
        return NP;
    }

    public void setNP(int NP) {
        this.NP = NP;
    }

    public List<AP_Individual> getP() {
        return P;
    }

    public void setP(List<AP_Individual> P) {
        this.P = P;
    }

    public int getFES() {
        return FES;
    }

    public void setFES(int FES) {
        this.FES = FES;
    }

    public int getMAXFES() {
        return MAXFES;
    }

    public void setMAXFES(int MAXFES) {
        this.MAXFES = MAXFES;
    }

    public TestFunction getTf() {
        return tf;
    }

    public void setTf(TestFunction f) {
        this.tf = f;
    }

    public List<AP_Individual> getBestHistory() {
        return bestHistory;
    }

    public void setBestHistory(List<AP_Individual> bestHistory) {
        this.bestHistory = bestHistory;
    }

    public Random getRndGenerator() {
        return rndGenerator;
    }

    public void setRndGenerator(Random rndGenerator) {
        this.rndGenerator = rndGenerator;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double[] getF() {
        return F;
    }

    public void setF(double[] F) {
        this.F = F;
    }

    public double[] getCR() {
        return CR;
    }

    public void setCR(double[] CR) {
        this.CR = CR;
    }
    //</editor-fold>


}

