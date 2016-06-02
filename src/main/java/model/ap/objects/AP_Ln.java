package model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Ln implements AP_object{
    
    public double a;

    public AP_Ln() {
    }
    
    public AP_Ln(double a) {
        this.a = a;
    }
    
    @Override
    public double compute(){
        double res = Math.log(a);
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
        return "Ln";
    }

    @Override
    public double compute(List<Double> array) {
        Double res = Math.log(array.get(0));
//        if(Double.isNaN(res) || Double.isInfinite(res)){
//            return 0;
//        }
        
        return res;
    }

    @Override
    public String createEq(List<String> array) {
        return "Log[" + array.get(0) + "]";
    }
    
}
