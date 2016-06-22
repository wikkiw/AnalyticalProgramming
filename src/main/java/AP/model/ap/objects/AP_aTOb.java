package AP.model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_aTOb implements AP_object{
    
    public double a;
    public double b;

    public AP_aTOb() {
    }
    
    public AP_aTOb(double a, double b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public double compute(){
        double res = Math.pow(a,b);
//        if(Double.isNaN(res) || Double.isInfinite(res)){
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
        return "a^b";
    }

    @Override
    public double compute(List<Double> array) {
        double res = Math.pow(array.get(1),array.get(0));
//        if(Double.isNaN(res) || Double.isInfinite(res)){
//            return 0;
//        }
        
        return res;
    }
    
    @Override
    public String createEq(List<String> array) {
//        if(Double.parseDouble(array.get(1)) == 0){
//            return "0";
//        }
        
        return "Power[" + array.get(1) + "," + array.get(0) + "]";
    }
    
}
