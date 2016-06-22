package AP.model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Div implements AP_object{
    
    public double a;
    public double b;

    public AP_Div() {
    }
    
    public AP_Div(double a, double b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public double compute(){
        if(b == 0){
            return Double.NaN;
        }
        
        double res = a / b;
        
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
        return "Div";
    }

    @Override
    public double compute(List<Double> array) {
        if(array.get(0) == 0){
            return Double.NaN;
        }
        
        double res = array.get(1) / array.get(0);
        
//        if(Double.isNaN(res) || Double.isInfinite(res)) {
//            return 0;
//        }
        
        return res;
    }
    
    @Override
    public String createEq(List<String> array) {
//        if(Double.parseDouble(array.get(1)) == 0){
//            return "0";
//        }
        
        return "Divide[" + array.get(1) + "," + array.get(0) + "]";
    }
    
}
