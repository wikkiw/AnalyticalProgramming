package AP.model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Abs implements AP_object{
    
    public double a;

    public AP_Abs() {
    }
    
    public AP_Abs(double a) {
        this.a = a;
    }
    
    @Override
    public double compute(){
        return Math.abs(a);
    }

    @Override
    public int argCount() {
        return 1;
    }
    
    @Override
    public String toString(){
        return "Abs";
    }

    @Override
    public double compute(List<Double> array) {
        return Math.abs(array.get(0));
    }

    @Override
    public String createEq(List<String> array) {
        return "Abs[" + array.get(0) + "]";
    }
    
}
