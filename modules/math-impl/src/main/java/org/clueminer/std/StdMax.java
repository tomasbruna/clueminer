package org.clueminer.std;

import org.clueminer.math.Standardisation;
import org.openide.util.lookup.ServiceProvider;

/**
 * Divide each attribute value of a row by maximum value of that attribute.
 * This will put all values to an interval between −1 and 1.
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Standardisation.class)
public class StdMax extends Standardisation {

    public static final String name = "Maximum";

    @Override
    public String getName(){
        return name;
    }

    @Override
    public double[][] optimize(double[][] A, int m, int n) {
        double[] maxVal = new double[n];
        int i,j;
        double[][] res = new double[m][n];
        double value;
        /**
         * finds max in each column
         */
        for (j = 0; j < n; j++) {
            for (i = 0; i < m; i++) {
                value = Math.abs(A[i][j]); //intetionally
                if (value > maxVal[j]) {
                    maxVal[j] = value;
                }
            }
        }

        for (i = 0; i < m; i++) {
            for (j = 0; j < n; j++) {
                res[i][j] = A[i][j] / maxVal[j];
            }
        }
        return res;
    }

}
