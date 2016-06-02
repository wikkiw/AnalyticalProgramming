package model.ap.objects;

import static java.lang.Double.NaN;
import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Mod implements AP_object{
    
    public double a;
    public double b;

    public AP_Mod() {
    }
    
    public AP_Mod(double a, double b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public double compute(){
        
        double res = a % b;
        
//        if(Double.isNaN(res) || Double.isInfinite(res)) {
//            return 0;
//        }
        
        return res;
    }

    @Override
    public int argCount() {
        return 2;
    }
    
    @Override
    public String toString(){
        return "Mod";
    }

    @Override
    public double compute(List<Double> array) {
        double res = array.get(1) % array.get(0);
        
//        if(Double.isNaN(res) || Double.isInfinite(res)) {
//            return 0;
//        }
        
        return res;
    }
    
    @Override
    public String createEq(List<String> array) {
//        if("0".equals(array.get(0))){
//            return Double.toString(0);
//        }
        
        return "Mod[" + array.get(1) + "," + array.get(0) + "]";
    }
    
}
