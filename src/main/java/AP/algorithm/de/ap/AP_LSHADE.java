package AP.algorithm.de.ap;

import java.util.ArrayList;
import java.util.List;
import AP.model.AP_Individual;
import AP.model.tf.TestFunction;
import AP.model.tf.ap.APtf;
import AP.util.OtherDistributionsUtil;
import AP.util.random.Random;

/**
 *
 * SHADE algorithm with linear decrease of population size.
 * Inidividuals that are going to be killed are selected based on their fitness value.
 * 
 * @author adam on 05/04/2016
 */
public class AP_LSHADE extends AP_ShaDE {

    protected final int minPopSize;
    protected final int maxPopSize;
    
    public AP_LSHADE(int D, int MAXFES, TestFunction f, int H, int NP, Random rndGenerator, int minPopSize) {
        super(D, NP, MAXFES, f, rndGenerator, H);
        this.minPopSize = minPopSize;
        this.maxPopSize = NP;
    }
 
    @Override
    public String getName() {
        return "AP_L-SHADE";
    }
    
    @Override
    public AP_Individual run() {

        /**
         * Initialization
         */
        this.G = 0;
        this.avgGenerationLength = new ArrayList<>();
        this.Aext = new ArrayList<>();
        this.best = null;
        this.bestHistory = new ArrayList<>();

        /**
         * Initial population
         */
        initializePopulation();

        this.M_F = new double[this.H];
        this.M_CR = new double[this.H];

        for (int h = 0; h < this.H; h++) {
            this.M_F[h] = 0.5;
            this.M_CR[h] = 0.5;
        }

        /**
         * Generation iteration;
         */
        int r, Psize, pbestIndex;
        double Fg, CRg;
        List<AP_Individual> newPop, pBestArray;
        double[] v, pbest, pr1, pr2, u;
        int[] rIndexes;
        AP_Individual trial, pbestInd;
        AP_Individual x;
        List<Double> wS;
        double wSsum, meanS_F1, meanS_F2, meanS_CR;
        int k = 0;
        double pmin = 2 / (double) this.NP;
        List<double[]> parents;

        this.avgGenerationLength.add((double)this.best.length);
        
        while (true) {

            this.G++;
            this.S_F = new ArrayList<>();
            this.S_CR = new ArrayList<>();
            wS = new ArrayList<>();

            newPop = new ArrayList<>();

            for (int i = 0; i < this.NP; i++) {

                x = this.P.get(i);
                r = rndGenerator.nextInt(this.H);
                Fg = OtherDistributionsUtil.cauchy(this.M_F[r], 0.1);
                while (Fg <= 0) {
                    Fg = OtherDistributionsUtil.cauchy(this.M_F[r], 0.1);
                }
                if (Fg > 1) {
                    Fg = 1;
                }
                

                CRg = OtherDistributionsUtil.normal(this.M_CR[r], 0.1);
                if (CRg > 1) {
                    CRg = 1;
                }
                if (CRg < 0) {
                    CRg = 0;
                }

                Psize = (int) (rndGenerator.nextDouble(pmin, 0.2) * this.NP);
                if (Psize < 2) {
                    Psize = 2;
                }

                pBestArray = new ArrayList<>();
                pBestArray.addAll(this.P);
                pBestArray = this.resize(pBestArray, Psize);

                /**
                 * Parent selection
                 */
                pbestInd = this.getRandBestFromList(pBestArray);
                pbestIndex = this.getPbestIndex(pbestInd);
                pbest = pbestInd.vector.clone();
                rIndexes = this.genRandIndexes(i, this.NP, this.NP + this.Aext.size(), pbestIndex);
                pr1 = this.P.get(rIndexes[0]).vector.clone();
                if (rIndexes[1] > this.NP - 1) {
                    pr2 = this.Aext.get(rIndexes[1] - this.NP).vector.clone();
                } else {
                    pr2 = this.P.get(rIndexes[1]).vector.clone();
                }
                parents = new ArrayList<>();
                parents.add(x.vector);
                parents.add(pbest);
                parents.add(pr1);
                parents.add(pr2);
                
                /**
                 * Mutation
                 */               
                v = mutation(parents, Fg);

                /**
                 * Crossover
                 */
                u = crossover(CRg, v, x.vector);

                /**
                 * Constrain check
                 */
                u = constrainCheck(u, x.vector);

                /**
                 * Trial ready
                 */
                id++;
                trial = new AP_Individual(String.valueOf(id), u, f.fitness(u));
                trial.equation = ((APtf) f).ap.equation;
                trial.length = ((APtf) f).countLength(u);
                
                /**
                 * Trial is better
                 */
                if (trial.fitness < x.fitness) {
                    newPop.add(trial);
                    this.S_F.add(Fg);
                    this.S_CR.add(CRg);
                    this.Aext.add(x);
                    wS.add(Math.abs(trial.fitness - x.fitness));
                    
                } else {
                    newPop.add(x);
                }

                this.FES++;
                this.isBest(trial);
                this.writeHistory();
                if (this.FES >= this.MAXFES) {
                    break;
                }

                this.Aext = this.resizeAext(this.Aext, this.NP);
                
            }

            this.avgGenerationLength.add((double)this.best.length);
            
            if (this.FES >= this.MAXFES) {
                break;
            }

            /**
             * Memories update
             */
            if (this.S_F.size() > 0) {
                wSsum = 0;
                for (Double num : wS) {
                    wSsum += num;
                }
                meanS_F1 = 0;
                meanS_F2 = 0;
                meanS_CR = 0;
                
                if(Double.isInfinite(wSsum)){
                    wSsum = Double.MAX_VALUE;
                }

                for (int s = 0; s < this.S_F.size(); s++) {
                    meanS_F1 += (wS.get(s) / wSsum) * this.S_F.get(s) * this.S_F.get(s);
                    meanS_F2 += (wS.get(s) / wSsum) * this.S_F.get(s);
                    meanS_CR += (wS.get(s) / wSsum) * this.S_CR.get(s);
                }

                this.M_F[k] = (meanS_F1 / meanS_F2);
                
                if(Double.isNaN(this.M_F[k])){
                    OtherDistributionsUtil.cauchy(this.M_F[k], 0.1);
                }
                
                this.M_CR[k] = meanS_CR;

                k++;
                if (k >= this.H) {
                    k = 0;
                }
            }
            
            /**
             * Resize of population and archives
             */
            this.P = new ArrayList<>();
            this.P.addAll(newPop);
            NP = (int) Math.round(this.maxPopSize - ((double) this.FES/(double) this.MAXFES)*(this.maxPopSize - this.minPopSize));
            P = this.resizePop(P, NP);

        }

        return this.best;

    }
    
    /**
     *
     * @param index
     * @param max1
     * @param max2
     * @param pbest
     * @return
     */
    protected int[] genRandIndexes(int index, int max1, int max2, int pbest) {

        int a, b;

        a = rndGenerator.nextInt(max1);
        
        while(a == pbest || a == index){
            a = rndGenerator.nextInt(max1);
        }
        
        b = rndGenerator.nextInt(max2);

        while (b == a || b == index || b == pbest) {
            b = rndGenerator.nextInt(max2);
        }

        return new int[]{a, b};
    }
    
    /**
     * Gets the index of pbest in current population
     * 
     * @param pbest
     * @return 
     */
    protected int getPbestIndex(AP_Individual pbest) {
        
        int toRet = -1;
        AP_Individual cur;
        
        for(int i = 0; i < this.P.size(); i++){
            
            cur = this.P.get(i);
            
            if(cur == pbest){
                toRet = i;
            }
            
        }
        
        return toRet;
        
    }
    
    /**
     *
     * @param list
     * @return
     */
    protected AP_Individual getRandBestFromList(List<AP_Individual> list) {
        
        int index = rndGenerator.nextInt(list.size());

        return list.get(index);

    }
    
    /**
     *
     * @param list
     * @param size
     * @return
     */
    protected List<AP_Individual> resizePop(List<AP_Individual> list, int size) {

        if(size == list.size()){
            return list;
        }
        
        List<AP_Individual> toRet = new ArrayList<>();
        List<AP_Individual> tmp = new ArrayList<>();
        tmp.addAll(list);
        AP_Individual bestInd;

        for (int i = 0; i < size; i++) {
            bestInd = this.getBestFromList(tmp);
            toRet.add(bestInd);
            tmp.remove(bestInd);
        }

        return toRet;

    }
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {

    }
    
}
