package AP.algorithm.de.ap;

import AP.algorithm.Algorithm;
import java.util.ArrayList;
import java.util.List;
import AP.model.AP_Individual;
import AP.model.tf.TestFunction;
import AP.model.tf.ap.APtf;
import AP.util.OtherDistributionsUtil;

/**
 *
 * ShaDE algorithm
 *
 * @see <a href="http://goo.gl/eYB26Z">Original paper from CEC2013</a>
 *
 * @author adam on 16/11/2015 update 25/11/2015
 */
public class AP_ShaDE implements Algorithm {
    
    int D;
    int G;
    int NP;
    List<AP_Individual> Aext;
    List<AP_Individual> P;
    int FES;
    int MAXFES;
    TestFunction f;
    AP_Individual best;
    List<AP_Individual> bestHistory;
    double[] M_F;
    double[] M_CR;
    List<Double> S_F;
    List<Double> S_CR;
    int H;
    AP.util.random.Random rndGenerator;
    int id;
    List<Double> avgGenerationLength;
    
    public AP_ShaDE(int D, int NP, int MAXFES, TestFunction f, AP.util.random.Random rndGenerator, int H) {
        this.D = D;
        this.MAXFES = MAXFES;
        this.f = f;
        this.H = H;
        this.NP = NP;
        this.rndGenerator = rndGenerator;
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
        int r, Psize;
        double Fg, CRg;
        List<AP_Individual> newPop, pBestArray;
        double[] v, pbest, pr1, pr2, u;
        int[] rIndexes;
        AP_Individual trial;
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
                pbest = this.getBestFromList(pBestArray).vector.clone();
                rIndexes = this.genRandIndexes(i, this.NP, this.NP + this.Aext.size());
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
//                    lengthValues[i] = trial.length;
                    this.S_F.add(Fg);
                    this.S_CR.add(CRg);
                    this.Aext.add(x);
                    wS.add(Math.abs(trial.fitness - x.fitness));
                } else {
                    newPop.add(x);
//                    lengthValues[i] = x.length;
                }

                this.FES++;
                this.isBest(trial);
                this.writeHistory();
                if (this.FES >= this.MAXFES) {
                    break;
                }

            }
            
//            this.avgGenerationLength.add(new Mean().evaluate(lengthValues));
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
            this.Aext = this.resizeAext(this.Aext, this.NP);

        }

        return this.best;

    }
    
    protected double[] mutation(List<double[]> parents, double F){
        
        /**
         * Parents:
         * x
         * pbest
         * pr1
         * pr2
         */
        
        double[] v = new double[this.D];
        for (int j = 0; j < this.D; j++) {

            v[j] = parents.get(0)[j] + F * (parents.get(1)[j] - parents.get(0)[j]) + F * (parents.get(2)[j] - parents.get(3)[j]);
            
        }
        
        return v;
        
    }
    
    /**
     * 
     * @param u
     * @param x
     * @return 
     */
    protected double[] constrainCheck(double[] u, double[] x){

        for (int i = 0; i < u.length; i++) {

            if (u[i] > f.max(u.length)) {
                u[i] = f.max(u.length);
            }
            if (u[i] < f.min(u.length)) {
                u[i] = f.min(u.length);
            }

        }
        
        return u;
    }
    
    /**
     * 
     * @param CR
     * @param v
     * @param x
     * @return 
     */
    protected double[] crossover(double CR, double[] v, double[] x){
        
        /**
         * Crossover
         */
        double[] u = new double[this.D];
        int jrand = rndGenerator.nextInt(this.D);

        for (int j = 0; j < this.D; j++) {
            if (getRandomCR() <= CR || j == jrand) {
                u[j] = v[j];
            } else {
                u[j] = x[j];
            }

        }
        
        return u;
        
    }
    
    /**
     * 
     * @return 
     */
    protected double getRandomCR(){
        return rndGenerator.nextDouble();
    }

    /**
     * Creation of initial population.
     */
    protected void initializePopulation(){
        
        /**
         * Initial population
         */
        id = 0;
        double[] features;
        this.P = new ArrayList<>();
        AP_Individual ind;

        for (int i = 0; i < this.NP; i++) {
            id = i;
            features = this.f.generateTrial(this.D).clone();
            ind = new AP_Individual(String.valueOf(id), features, this.f.fitness(features));
            ind.equation = ((APtf) f).ap.equation;
            ind.length = ((APtf) f).countLength(features);
            this.isBest(ind);
            this.P.add(ind);
            this.FES++;
            this.writeHistory();
        }
        
    }
    
    @Override
    public List<? extends AP_Individual> getPopulation() {
        return this.P;
    }

    @Override
    public TestFunction getTestFunction() {
        return this.f;
    }

    @Override
    public String getName() {
        return "AP_ShaDE";
    }

    /**
     *
     * @param list
     * @param size
     * @return
     */
    protected List<AP_Individual> resizeAext(List<AP_Individual> list, int size) {

        List<AP_Individual> toRet = new ArrayList<>();
        toRet.addAll(list);
        int index;

        while (toRet.size() > size) {

            index = rndGenerator.nextInt(toRet.size());
            toRet.remove(index);

        }

        return toRet;

    }

    /**
     *
     * @param list
     * @param size
     * @return
     */
    protected List<AP_Individual> resize(List<AP_Individual> list, int size) {

        List<AP_Individual> toRet = new ArrayList<>();
        List<AP_Individual> tmp = new ArrayList<>();
        tmp.addAll(list);
        int bestIndex;

        for (int i = 0; i < size; i++) {
            bestIndex = this.getIndexOfBestFromList(tmp);
            toRet.add(tmp.get(bestIndex));
            tmp.remove(bestIndex);
        }

        return toRet;

    }

    /**
     *
     * @param list
     * @return
     */
    protected int getIndexOfBestFromList(List<AP_Individual> list) {

        AP_Individual b = null;
        int i = 0;
        int index = -1;

        for (AP_Individual ind : list) {

            if (b == null) {
                b = ind;
                index = i;
            } else if (ind.fitness < b.fitness) {
                b = ind;
                index = i;
            }
            i++;
        }

        return index;

    }

    /**
     *
     * @param list
     * @return
     */
    protected AP_Individual getBestFromList(List<AP_Individual> list) {

        AP_Individual b = null;

        for (AP_Individual ind : list) {

            if (b == null) {
                b = ind;
            } else if (ind.fitness < b.fitness) {
                b = ind;
            }

        }

        return b;

    }

    /**
     *
     * @param index
     * @param max1
     * @param max2
     * @return
     */
    protected int[] genRandIndexes(int index, int max1, int max2) {

        int a, b;

        a = rndGenerator.nextInt(max1);
        b = rndGenerator.nextInt(max2);

        while (a == b || a == index || b == index) {
            a = rndGenerator.nextInt(max1);
            b = rndGenerator.nextInt(max2);
        }

        return new int[]{a, b};
    }

    /**
     *
     */
    protected void writeHistory() {
        this.bestHistory.add(this.best);
    }

    /**
     *
     * @param ind
     * @return
     */
    protected boolean isBest(AP_Individual ind) {

        if (this.best == null || ind.fitness < this.best.fitness) {
            this.best = ind;
            return true;
        }

        return false;

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

    public List<AP_Individual> getAext() {
        return Aext;
    }

    public void setAext(List<AP_Individual> Aext) {
        this.Aext = Aext;
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

    public TestFunction getF() {
        return f;
    }

    public void setF(TestFunction f) {
        this.f = f;
    }

    public AP_Individual getBest() {
        return best;
    }

    public void setBest(AP_Individual best) {
        this.best = best;
    }

    public List<AP_Individual> getBestHistory() {
        return bestHistory;
    }

    public void setBestHistory(List<AP_Individual> bestHistory) {
        this.bestHistory = bestHistory;
    }

    public double[] getM_F() {
        return M_F;
    }

    public void setM_F(double[] M_F) {
        this.M_F = M_F;
    }

    public double[] getM_CR() {
        return M_CR;
    }

    public void setM_CR(double[] M_CR) {
        this.M_CR = M_CR;
    }

    public List<Double> getS_F() {
        return S_F;
    }

    public void setS_F(List<Double> S_F) {
        this.S_F = S_F;
    }

    public List<Double> getS_CR() {
        return S_CR;
    }

    public void setS_CR(List<Double> S_CR) {
        this.S_CR = S_CR;
    }

    public int getH() {
        return H;
    }

    public void setH(int H) {
        this.H = H;
    }

    public AP.util.random.Random getRndGenerator() {
        return rndGenerator;
    }

    public void setRndGenerator(AP.util.random.Random rndGenerator) {
        this.rndGenerator = rndGenerator;
    }
    
    public List<Double> getAvgGenerationLength() {
        return avgGenerationLength;
    }

    public void setAvgGenerationLength(List<Double> avgGenerationLength) {
        this.avgGenerationLength = avgGenerationLength;
    }
    
    //</editor-fold>

}
