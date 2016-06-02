package model.tf.ap.regression;

import java.util.ArrayList;
import model.tf.ap.APtf;
import org.apache.commons.math3.stat.descriptive.summary.Sum;

/**
 *
 * universal dataset class
 * 
 * @author wiki on 02/06/2016
 */
public class APdataset extends APtf {

    private double[][] points;
    
    public APdataset(double[][] points, ArrayList GFS, double min, double max) {
        this.points = points;
        super.setGFS(GFS, min, max);
    }

    @Override
    protected double getDistance(double[] vector) {

        double sum = 0, a;
        double[] distance_array = new double[points.length];
        
        double[] single_point;
        int point_length = this.points[0].length;
        double point_output;
        
        for (int i = 0; i < points.length; i++) {
            single_point = new double[point_length - 1];
            
            System.arraycopy(points[i], 0, single_point, 0, point_length-1);
            
            a = ap.dsh(vector, single_point);
            if(Double.isNaN(a) || Double.isInfinite(a)){
                return Double.MAX_VALUE;
            }
            point_output = points[i][point_length-1];

            distance_array[i] = Math.abs(a - point_output);
        }
        
        return new Sum().evaluate(distance_array);

    }

    @Override
    public String name() {
        return "AP_universal_dataset";
    }

    
}
