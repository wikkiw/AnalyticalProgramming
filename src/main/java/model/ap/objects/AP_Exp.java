package model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Exp implements AP_object{
    
    public double a;

    public AP_Exp() {
    }
    
    public AP_Exp(double a) {
        this.a = a;
    }
    
    @Override
    public double compute(){
        return Math.exp(a);
    }

    @Override
    public int argCount() {
        return 1;
    }
    
    @Override
    public String toString(){
        return "Exp";
    }

    @Override
    public double compute(List<Double> array) {
        return Math.exp(array.get(0));
    }
    
    @Override
    public String createEq(List<String> array) {
        return "Exp[" + array.get(0) + "]";
    }
    
}
