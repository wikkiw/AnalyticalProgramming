package model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Euler implements AP_object {
    
    public double t;

    public AP_Euler() {
    }
    
    public AP_Euler(double t) {
        this.t = t;
    }
    
    @Override
    public double compute(){
        return Math.E;
    }

    @Override
    public int argCount() {
        return 0;
    }
    
    @Override
    public String toString(){
        return "E";
    }

    @Override
    public double compute(List<Double> a) {
        return Math.E;
    }
    
    @Override
    public String createEq(List<String> array) {
        return "E";
    }
    
}
