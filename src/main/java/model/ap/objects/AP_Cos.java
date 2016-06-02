package model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Cos implements AP_object {
    
    public double a;

    public AP_Cos() {
    }
    
    public AP_Cos(double a) {
        this.a = a;
    }
    
    @Override
    public double compute(){
        return Math.cos(a);
    }

    @Override
    public int argCount() {
        return 1;
    }
    
    @Override
    public String toString(){
        return "Cos";
    }

    @Override
    public double compute(List<Double> array) {
        return Math.cos(array.get(0));
    }

    @Override
    public String createEq(List<String> array) {
        return "Cos[" + array.get(0) + "]";
    }

    
}
