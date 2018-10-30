import Jama.Matrix;
import Jama.SingularValueDecomposition;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

public class LSILab4 {

    public static void main(String[] args) {
        LSILab4 lsi = new LSILab4();
        lsi.go();
    }

    private void go() {
        // init the matrix and the query
        Matrix matrix = readMatrix("data.txt");
        Matrix queryVector = readMatrix("query.txt");

        if (matrix != null && queryVector != null) {
            System.out.println("Matrix:");
            matrix.print(3, 2);

            // print the dimensions of the matrix
            System.out.println("matrix: " + dim(matrix));

            System.out.println("Query:");
            queryVector.print(3, 2);
            System.out.println("Q: " + dim(queryVector));

            svd(matrix, queryVector);
        } else {
            System.out.println("Macierz pusta, błąd odczytu z pliku ");
        }
    }

    private void svd(Matrix matrix, Matrix queryVector) {

        //TODO implement your solution

        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);

        Matrix k = matrix.arrayTimes(queryVector);
        Matrix d = queryVector.arrayTimes(matrix);
        Matrix s = svd.getS();
        // get K, S, and D

        // set number of largest singular values to be considered
        int sCount = 4;

        // cut off appropriate columns and rows from K, S, and D

        // transform the query vector

        // compute similaraty of the query and each of the documents, using cosine measure

    }


    // returns the dimensions of a matrix
    private String dim(Matrix matrix) {
        return matrix.getRowDimension() + "x" + matrix.getColumnDimension();
    }

    // reads a matrix from a file
    private Matrix readMatrix(String filename) {
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
