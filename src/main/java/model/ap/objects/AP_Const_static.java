package model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Const_static implements AP_object {
    
    public double t;

    public AP_Const_static() {
    }
    
    public AP_Const_static(double t) {
        this.t = t;
    }
    
    @Override
    public double compute(){
        return t;
    }

    @Override
    public int argCount() {
        return 0;
    }
    
    @Override
    public String toString(){
        return Double.toString(t);
    }

    @Override
    public double compute(List<Double> a) {
        t = a.get(0);
        return t;
    }

    @Override
    public String createEq(List<String> array) {
        return array.get(0);
    }
    
}
