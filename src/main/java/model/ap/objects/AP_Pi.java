package model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Pi implements AP_object {
    
    public double t;

    public AP_Pi() {
    }
    
    public AP_Pi(double t) {
        this.t = t;
    }
    
    @Override
    public double compute(){
        return Math.PI;
    }

    @Override
    public int argCount() {
        return 0;
    }
    
    @Override
    public String toString(){
        return "Pi";
    }

    @Override
    public double compute(List<Double> a) {
        return Math.PI;
    }
    
    @Override
    public String createEq(List<String> array) {
        return "Pi";
    }
    
}
