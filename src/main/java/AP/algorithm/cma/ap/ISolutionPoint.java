/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AP.algorithm.cma.ap;


    /** solution point in search space, single-objective case
     *  
     * */
public interface ISolutionPoint {
    /** objective function value (fitness) of the search point x */ 
    public double getFitness();  
    /** count at what evaluation number the search point x was evaluated */
    public long getEvaluationNumber();
    /** value of the point in search space, that is in the 
     * preimage of the objective function to be optimized */ 
    public double[] getX();
    
    /** objective function value (fitness) of the search point x */ 
    public void setFitness( double fitness); // TODO better FunctionValue than Fitness ? 
    /** count at what evaluation number the search point x was evaluated */
    public void setEvaluationNumber( long evaluation);
    /** value of the solution point in search space, the 
     * preimage of the objective function to be optimized */ 
    public void setX(double[] x);
} 