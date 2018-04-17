/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AP.algorithm.pso.ap;

import AP.algorithm.Algorithm;
import AP.model.AP_Individual;
import AP.model.tf.TestFunction;
import AP.model.tf.ap.APtf;
import AP.util.APIndividualComparator;
import AP.util.random.Random;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ned
 */
public class AP_HCLPSO implements Algorithm {

    public int sizePop;
    public int sizeDim;
    private int FES;
    public int MAXFES;
    private TestFunction tf;
    private List<AP_HCLPSOIndividual> pop;
    private AP_Individual gBest;
    private List<AP_Individual> bestHistory;
    private AP.util.random.Random rndGenerator;
    
    /*
        Custom variables of HCLPSO
    */
    public double[] vMax;           //every dimension has own vMax
    public InertiaWeight w;
    public InertiaWeight c;
    public InertiaWeight c1;
    public InertiaWeight c2;
    public int gap;
    private int sizeSub1 = 0;
    private int sizeSub2 = 0;
    
    
    
    public AP_HCLPSO(int sizeDim, int sizePop, int MAXFES, TestFunction f, Random rndGenerator) {
        
        this.sizePop = sizePop;
        this.sizeDim = sizeDim;
        this.MAXFES = MAXFES;
        this.tf = f;
        this.rndGenerator = rndGenerator;
        
        FES = 0;
        int gen = MAXFES / sizePop;
        w = new InertiaWeight(0.99, 0.2, gen);
        c = new InertiaWeight(3, 1.5, gen);
        c1 = new InertiaWeight(0.5, 2.5, gen);
        c2 = new InertiaWeight(2.5, 0.5, gen);
        gap = 5;
        
        vMax = new double[sizeDim];
        for(int i = 0; i < sizeDim ; i++) {
            vMax[i] = (0.2 * Math.abs(f.max(i) - f.min(i)));
        }
        
        pop = new ArrayList<>();
        bestHistory = new ArrayList<>();
        
    }
    
    @Override
    public AP_Individual run() {
        
        populationInit();
        if (checkFES()) {
            return gBest;
        }
        
        int it = 0;
        while(true) {
            
            w.update(it);
            c.update(it);
            c1.update(it);
            c2.update(it);
            
            for(AP_HCLPSOIndividual p : pop) {
                
                if(p.flag >= gap) {
                    p.flag = 0;
                    selectExemplar(p);
                }
                
                //calculate speed
                calculateSpeed(p);
                
                //calculate position
                calculatePosition(p);
                
                //fitness
                fitness(p);
                if (checkFES()) {
                    return gBest;
                }
                
            }
            
            it++;
            
        }
 
    }
    
    private void calculateSpeed(AP_HCLPSOIndividual p) {
    
        for(int j=0; j<sizeDim; j++) {
            
            AP_HCLPSOIndividual best = getParticle(p.pBestF[j]);
            double bestV = best.pBest.vector[j];
            
            if (p.subpopulation == 0) {
                    //popSpeed[i][j] = popSpeed[i][j] * w + (c*randomUniform(0, 1)*(pBest[pBestF[i][j]][j] - pop[i][j]));
                    p.popSpeed[j] = p.popSpeed[j] * w.getW() + (c.getW() * rndGenerator.nextDouble() * (bestV - p.vector[j]));
            }
            else {
                    //popSpeed[i][j] = popSpeed[i][j] * w + (c1*randomUniform(0, 1)*(pBest[pBestF[i][j]][j] - pop[i][j])) + (c2*randomUniform(0, 1)*(gBest[j] - pop[i][j]));
                    p.popSpeed[j] = p.popSpeed[j] * w.getW() + (c1.getW() * rndGenerator.nextDouble() * (bestV - p.vector[j])) + (c2.getW() * rndGenerator.nextDouble() * (gBest.vector[j] - p.vector[j]));
            }
            
            if(p.popSpeed[j] > vMax[j]) {
                p.popSpeed[j] = vMax[j];
            }
            else if (p.popSpeed[j] < -vMax[j]) {
                p.popSpeed[j] = -vMax[j];
            }
        }
    }
    
    private void calculatePosition(AP_HCLPSOIndividual p) {
        for(int i=0; i<sizeDim; i++) {
            p.vector[i] += p.popSpeed[i];
        }
        tf.constrain(p);
    }
    
    private void populationInit() {
    
        double[] vector;
        int id = 0;
        double sub = sizePop - Math.ceil(sizePop * 0.625);
        for(int i = 0; i < sizePop; i++) {
            
            vector = tf.generateTrial(sizeDim);
            AP_HCLPSOIndividual particle = new AP_HCLPSOIndividual();
            particle.id = id;
            id++;
            particle.vector = vector;
            tf.constrain(particle);
            fitness(particle);
            particle.equation = ((APtf) tf).ap.equation;
            particle.pBestF = new int[sizeDim];                 //TODO init
            
            particle.popSpeed = new double[sizeDim];
            for(int j=0; j<sizeDim; j++) {
                particle.popSpeed[j] = 0;
            }
            particle.flag = 0;
            particle.pc = 0.05 + 0.45*((Math.exp((10*(i + 1))/(sizePop + 1)) - 1) / (Math.exp(10) - 1));
            if(i <= sub) {
                particle.subpopulation = 0;
                sizeSub1++;
            } else {
                particle.subpopulation = 1;
                sizeSub2++;
            }
            
            pop.add(particle);
        }
        for(AP_HCLPSOIndividual p : pop) {
            selectExemplar(p);
        }
    }
    
    private void fitness(AP_HCLPSOIndividual particle) {
        //fitness
        particle.fitness = tf.fitness(particle.vector);
        //check pBest
        if(particle.pBest == null || particle.pBest.fitness >= particle.fitness) {
            particle.pBest = new AP_Individual(null, particle.vector, particle.fitness);
            particle.flag = 0;
        } else {
            particle.flag++;
        }
        //check gBest
        if(gBest == null || gBest.fitness >= particle.fitness) {
            gBest = new AP_Individual(null, particle.vector, particle.fitness);
        }
        //log history gBest
        FES++;
        bestHistory.add(gBest);
    }
    
    /**
     * Select the pBestF
     * @param particle 
     */
    private void selectExemplar(AP_HCLPSOIndividual particle) {
        boolean unique = false;
	//AD2
	for (int j = 0; j < sizeDim; j++) {

		//AD1
		if (rndGenerator.nextDouble() < particle.pc) {
			unique = true;

			int f1 = -1;
			int f2 = -1;
			while (f1 == -1 || f1 == particle.id)
			{
				//f1 = (int)ceil(randomUniform(0, 1) * (sizePop - 1));
				if (particle.subpopulation == 0) {
					f1 = (int)Math.ceil(rndGenerator.nextDouble() * (sizeSub1 - 1));
				}
				else {
					f1 = (int)Math.ceil(rndGenerator.nextDouble() * (sizePop - 1));
				}
			}
			while (f2 == -1 || f2 == particle.id)
			{
				//f2 = (int)ceil(randomUniform(0, 1) * (sizePop - 1));
				if (particle.subpopulation == 0) {
					f2 = (int)Math.ceil(rndGenerator.nextDouble() * (sizeSub1 - 1));
				}
				else {
					f2 = (int)Math.ceil(rndGenerator.nextDouble() * (sizePop - 1));
				}
			}

			//AD4
			if (getParticle(f1).pBest.fitness < getParticle(f2).pBest.fitness/*pBestCF[f1] < pBestCF[f2]*/) {
				//AD41
				//pBestF[i][j] = f1;
                                particle.pBestF[j] = f1;
			}
			else {
				//AD42
				//pBestF[i][j] = f2;
                                particle.pBestF[j] = f2;
			}

		}
		else {
			//AD12
			//use own pBest for this dimension
			//pBestF[i][j] = i;
                        particle.pBestF[j] = particle.id;
		}

	}
	if (!unique) {
		//at least one dim must be from another particle
		int f1 = -1;
		while (f1 == -1 || f1 == particle.id)
		{
			//f1 = (int)ceil(randomUniform(0, 1) * (sizePop - 1));
			if (particle.subpopulation == 0) {
				f1 = (int)Math.ceil(rndGenerator.nextDouble() * (sizeSub1 - 1));
			}
			else {
				f1 = (int)Math.ceil(rndGenerator.nextDouble() * (sizePop - 1));
			}
		}
		int d = (int)Math.ceil(rndGenerator.nextDouble() * (sizeDim - 1));
		//pBestF[i][d] = f1;
                particle.pBestF[d] = f1;
	}
    }
    
    private AP_HCLPSOIndividual getParticle(int i) {
        for(AP_HCLPSOIndividual p : pop) {
            if(p.id == i) {
                return p;
            }
        }
        return null;
    }
    
    protected boolean checkFES() {
        return (FES >= MAXFES);
    }
    
    public List<AP_Individual> getBestHistory() {
        return bestHistory;
    }
    
    @Override
    public List<? extends AP_Individual> getPopulation() {
        return pop;
    }

    @Override
    public TestFunction getTestFunction() {
        return tf;
    }

    @Override
    public String getName() {
        return "AP_HCLPSO";
    }

}

/**
 * Class representing the inertia weight w
 * @author Ned
 */
class InertiaWeight {
    protected double w;
    private final double wStart;
    private final double wEnd;
    private final int maxGeneration;

    /**
     * 
     * @param wStart Initial value 
     * @param wEnd End value
     * @param maxGeneration Number of generations
     */
    public InertiaWeight(double wStart, double wEnd, int maxGeneration) {
        this.wStart = wStart;
        this.wEnd = wEnd;
        this.maxGeneration = maxGeneration;
    }

    /**
     * Update the value
     * @param generation Actual genration
     */
    public void update(int generation) {
        w = wStart - ((wStart - wEnd)*generation / maxGeneration);
    }
    
    public double getW() {
        return w;
    }
}

/**
 * Added support for general HCLPSO. 
 *  - pBest
 *  - the speed of particle
 *  - the sub-population index
 *  - flag (number of generations a particle has not improve its own pBest)
 * @see     AP_Individual
 */
class AP_HCLPSOIndividual extends AP_Individual {
    
    public double[] popSpeed;
    public int subpopulation;
    public AP_Individual pBest;
    public int flag;
    public double pc;
    public int[] pBestF;
    public int id;
    
    public AP_HCLPSOIndividual() {
        super();
    }
    
    public AP_HCLPSOIndividual(AP_HCLPSOIndividual individual) {
        super(individual);
        this.pBest = individual.pBest;  //?
        this.popSpeed = individual.popSpeed.clone();
        this.pBestF = individual.pBestF.clone();
        this.subpopulation = individual.subpopulation;
        this.flag = individual.flag;
        this.pc = individual.pc;
    }
}