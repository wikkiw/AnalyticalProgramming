package AP.model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Zero implements AP_object {
    
    public double t;

    public AP_Zero() {
    }
    
    public AP_Zero(double t) {
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
        return Double.toString(0);
    }

    @Override
    public double compute(List<Double> a) {
        return 0;
    }
    
    @Override
    public String createEq(List<String> array) {
        return Double.toString(0);
    }
    
}
