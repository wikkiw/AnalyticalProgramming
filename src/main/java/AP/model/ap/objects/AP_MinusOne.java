package AP.model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_MinusOne implements AP_object {
    
    public double t;

    public AP_MinusOne() {
    }
    
    public AP_MinusOne(double t) {
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
        return Double.toString(-1);
    }

    @Override
    public double compute(List<Double> a) {
        return -1;
    }
    
    @Override
    public String createEq(List<String> array) {
        return Double.toString(-1);
    }
    
}
