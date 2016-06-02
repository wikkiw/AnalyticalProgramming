package model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Sin implements AP_object{
    
    public double a;

    public AP_Sin() {
    }
    
    public AP_Sin(double a) {
        this.a = a;
    }
    
    @Override
    public double compute(){
        return Math.sin(a);
    }

    @Override
    public int argCount() {
        return 1;
    }
    
    @Override
    public String toString(){
        return "Sin";
    }

    @Override
    public double compute(List<Double> array) {
        return Math.sin(array.get(0));
    }
    
    @Override
    public String createEq(List<String> array) {
        return "Sin[" + array.get(0) + "]";
    }
    
}
