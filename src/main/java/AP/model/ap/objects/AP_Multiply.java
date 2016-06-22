package AP.model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Multiply implements AP_object{
    
    public double a;
    public double b;

    public AP_Multiply() {
    }
    
    public AP_Multiply(double a, double b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public double compute(){
        return a*b;
    }

    @Override
    public int argCount() {
        return 2;
    }
    
    @Override
    public String toString(){
        return "Multiply";
    }

    @Override
    public double compute(List<Double> array) {
        return array.get(1)*array.get(0);
    }
    
    @Override
    public String createEq(List<String> array) {

        return "Times[" + array.get(1) + "," + array.get(0) + "]";
    }
    
}
