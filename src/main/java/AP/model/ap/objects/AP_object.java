package AP.model.ap.objects;

import java.util.List;

/**
 *
 * @author adam
 */
public interface AP_object {
    
    public int argCount();
    public double compute();
    public double compute(List<Double> array);
    public String createEq(List<String> array);
    
    @Override
    public String toString();
    
}
