import Jama.Matrix;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

public class MatrixUtils {
    public static double geMatrixtABS(Matrix qPrim, int columnDimension) {
        double qABS = 0d;
        for (int j = 0; j < columnDimension - 1; j++) {
            qABS = qABS + Math.pow(qPrim.get(0, j), 2);
        }
        qABS = Math.sqrt(qABS);
        return qABS;
    }

    public static String dim(Matrix matrix) {
        return matrix.getRowDimension() + "x" + matrix.getColumnDimension();
    }

    public static Matrix readMatrix(String filename) {
        Vector<Vector<Double>> vectors = new Vector<Vector<Double>>();
        int colums = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            while (br.ready()) {
                Vector<Double> row = new Vector<Double>();
                vectors.add(row);
                String line = br.readLine().trim();
                StringTokenizer st = new StringTokenizer(line, ", ");
                colums = 0;
                while (st.hasMoreTokens()) {
                    row.add(Double.parseDouble(st.nextToken()));
                    colums++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int rows = vectors.size();
        Matrix matrix = new Matrix(rows, colums);
        int rowI = 0;
        for (Vector<Double> vector : vectors) {
            int colI = 0;
            for (Double d : vector) {
                matrix.set(rowI, colI, d);
                colI++;
            }
            rowI++;
        }
        return matrix;
    }
}
