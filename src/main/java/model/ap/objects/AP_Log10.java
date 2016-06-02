package model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Log10 implements AP_object{
    
    public double a;

    public AP_Log10() {
    }
    
    public AP_Log10(double a) {
        this.a = a;
    }
    
    @Override
    public double compute(){
        double res = Math.log10(a);
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
        return "Log";
    }

    @Override
    public double compute(List<Double> array) {
        Double res = Math.log10(array.get(0));
//        if(Double.isNaN(res) || Double.isInfinite(res)){
//            return 0;
//        }
        
        return res;
    }

    @Override
    public String createEq(List<String> array) {
        return "Log10[" + array.get(0) + "]";
    }
    
}
