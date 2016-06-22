package AP.model.ap.objects;

import java.util.List;
import AP.util.RandomUtil;

/**
 *
 * @author adam
 */
public class AP_Const implements AP_object {
    
    public double t;

    public AP_Const() {
    }
    
    public AP_Const(double t) {
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
        return Double.toString(t);
    }

    @Override
    public double compute(List<Double> a) {
        t = RandomUtil.nextDouble(-10, 10);
        return t;
    }

    @Override
    public String createEq(List<String> array) {
        return Double.toString(t);
    }
    
}
