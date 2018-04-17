/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AP.algorithm.cma.ap;

import AP.algorithm.Algorithm;
import AP.model.AP_Individual;
import AP.model.tf.TestFunction;
import AP.model.tf.ap.APtf;
import AP.util.random.Random;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ned
 */
public class AP_IPOP_CMA_ES implements Algorithm{
    
    private TestFunction tf;
    private AP_Individual gBest;
    private List<AP_Individual> bestHistory;
    private AP.util.random.Random rndGenerator;
    
    private final int sizeDim;
    private int FEs = 0;
    private final int MAXFES;
    
    //CMA variables
    CMAEvolutionStrategy cma;
    double[] fitness;
    double[][] pop;
    
    public AP_IPOP_CMA_ES(int sizeDim, int MAXFES, TestFunction f, Random rndGenerator) {
    
        this.tf = f;
        this.rndGenerator = rndGenerator;
        this.sizeDim = sizeDim;
        bestHistory = new ArrayList<>();
        this.MAXFES = MAXFES;
        gBest = null;
        
        //init CMA
        cma = new CMAEvolutionStrategy();
        cma.readProperties();
        cma.setCustomGenerator(rndGenerator);
        cma.setDimension(sizeDim);
        cma.setInitialX(getBoundaryLow(), getBoundaryHigh());
        cma.setInitialStandardDeviation(0.2);
        
        //adapter for APIndividual
        fitness = cma.init();
    }
    
    @Override
    public AP_Individual run() {
        
        FEs = 0;
        
        while(cma.stopConditions.getNumber() == 0) {

			// core iteration step 
			pop = cma.samplePopulation(); 
			for(int i = 0; i < pop.length; ++i) {    
				
                            while(!isFeasible(pop[i])) {
                                pop[i] = cma.resampleSingle(i);
                            }
				
                            fitness[i] = tf.fitness(getApIndividual(pop[i], fitness[i]));        //to AP_Individual
                            
                            FEs++;
                            
                            if(gBest == null || fitness[i] <= gBest.fitness) {
                                gBest = getApIndividual(pop[i], fitness[i]);
                                if(bestHistory.isEmpty()) {
                                    bestHistory.add(gBest);
                                } else {
                                    AP_Individual last = bestHistory.get(bestHistory.size()-1);
                                    if(last.fitness > gBest.fitness) {
                                        bestHistory.add(gBest);
                                    } else {
                                        bestHistory.add(last);
                                    }
                                }
                            }
                            
                            if(FEs >= MAXFES) {
                                return gBest;
                            }
                            
			}	                                     
			cma.updateDistribution(fitness);         

		}
        
        return gBest;
    }
    
    private AP_Individual getApIndividual(double[] particle, double fitness) {
        AP_Individual p = new AP_Individual(String.valueOf(0), particle, fitness);
        p.equation = ((APtf) tf).ap.equation;
        return p;
    }
    
    private boolean isFeasible(double[] particle) {
        for(int i=0; i<sizeDim; i++) {
            if(particle[i] < tf.min(i) || particle[i] > tf.max(i)) {
                return false;
            }
        }
        return true;
    }
    
    private double[] getBoundaryLow() {
        double[] ret = new double[sizeDim];
        for(int i=0; i<sizeDim; i++) {
            ret[i] = tf.min(i);
        }
        return ret;
    }
    
    private double[] getBoundaryHigh() {
        double[] ret = new double[sizeDim];
        for(int i=0; i<sizeDim; i++) {
            ret[i] = tf.max(i);
        }
        return ret;
    }
    
    public List<AP_Individual> getBestHistory() {
        return bestHistory;
    }
    
    @Override
    public List<? extends AP_Individual> getPopulation() {
        
        List<AP_Individual> ret = new ArrayList<>();
        for(int i=0; i<pop.length; i++) {
            ret.add(
                getApIndividual(pop[i], fitness[i])
            );
        }
        
        return ret;
    }

    @Override
    public TestFunction getTestFunction() {
        return tf;
    }

    @Override
    public String getName() {
        return "AP_IPOP_CMA_ES";
    }
    
}
