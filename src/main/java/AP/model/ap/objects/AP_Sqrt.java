package AP.model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public class AP_Sqrt implements AP_object{
    
    public double a;

    public AP_Sqrt() {
    }
    
    public AP_Sqrt(double a) {
        this.a = a;
    }
    
    @Override
    public double compute(){
        return Math.sqrt(Math.abs(a));
    }

    @Override
    public int argCount() {
        return 1;
    }
    
    @Override
    public String toString(){
        return "Sqrt";
    }

    @Override
    public double compute(List<Double> array) {
        return Math.sqrt(Math.abs(array.get(0)));
    }
    
    @Override
    public String createEq(List<String> array) {
//        double d = Double.parseDouble(array.get(0));
        return "Sqrt[Abs[" + array.get(0) + "]]";
    }
    
}
