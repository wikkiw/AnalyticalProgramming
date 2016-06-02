package model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Sigmoid implements AP_object{
    
    public double a;

    public AP_Sigmoid() {
    }
    
    public AP_Sigmoid(double a) {
        this.a = a;
    }
    
    @Override
    public double compute(){
        return 1.0/(double)(1.0+Math.exp((-1.0*a)));
    }

    @Override
    public int argCount() {
        return 1;
    }
    
    @Override
    public String toString(){
        return "Sigmoid";
    }

    @Override
    public double compute(List<Double> array) {
        return 1.0/(double)(1.0+Math.exp((-1.0*array.get(0))));
    }

    @Override
    public String createEq(List<String> array) {
        return "Divide[1, Plus[1, Exp[Times[-1, " + array.get(0) + "]]]]";
    }
    
}
