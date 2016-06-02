package model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Tan implements AP_object{
    
    public double a;

    public AP_Tan() {
    }
    
    public AP_Tan(double a) {
        this.a = a;
    }
    
    @Override
    public double compute(){
        
        double res = Math.tan(a);
//        if(Double.isNaN(res) || Double.isInfinite(res)){
//            return 0;
//        }
        
        return res;
        
    }

    @Override
    public int argCount() {
        return 1;
    }
    
    @Override
    public String toString(){
        return "Tan";
    }

    @Override
    public double compute(List<Double> array) {
        double res = Math.tan(array.get(0));
//        if(Double.isNaN(res) || Double.isInfinite(res)){
//            return 0;
//        }
        
        return res;
    }
    
    @Override
    public String createEq(List<String> array) {
        return "Tan[" + array.get(0) + "]";
    }
    
}
