import Jama.Matrix;
import Jama.SingularValueDecomposition;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class LSILab4 {

    public static void main(String[] args) {
        new LSILab4().run();
    }

    private void run() {
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
        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);

        Matrix k = svd.getU();
        Matrix s = svd.getS(); //macierz diagonalna
        Matrix d = svd.getV().transpose();
        // get K, S, and D
        List<Double> similarity4 = new ArrayList<Double>();
        List<Double> similarity2 = new ArrayList<Double>();

        System.out.println("Poczatek przetwarzania ");
        // set number of largest singular values to be considered
        int sCount = 4;
        countSimilarity(queryVector, k, s, d, similarity4, sCount);
        printSimilarity(similarity4, sCount);

        sCount = 2;
        countSimilarity(queryVector, k, s, d, similarity2, sCount);
        printSimilarity(similarity2, sCount);

        System.out.println("Koniec przetwarzania ");
    }

    private void countSimilarity(Matrix queryVector, Matrix k, Matrix s, Matrix d, List<Double> similarityList, int sCount) {
        // cut off appropriate columns and rows from K, S, and D
        Matrix croppedS = s.getMatrix(0, sCount - 1, 0, sCount - 1);
        Matrix croppedK = k.getMatrix(0, k.getRowDimension() - 1, 0, sCount - 1);
        Matrix croppedD = d.getMatrix(0, sCount - 1, 0, d.getColumnDimension() - 1);

        // transform the query vector
        Matrix qPrim = queryVector.transpose().times(croppedK).times(croppedS.inverse());
        // compute similaraty of the query and each of the documents, using cosine measure

        double qABS = 0d;
        for (int j = 0; j < qPrim.getColumnDimension() - 1; j++) {
            qABS = qABS + Math.pow(qPrim.get(0, j), 2);
        }
        qABS = Math.sqrt(qABS);

        for (int i = 0; i < croppedD.getColumnDimension() - 1; i++) {
            double dABS = 0d;
            for (int j = 0; j < croppedD.getRowDimension() - 1; j++) {
                dABS = dABS + Math.pow(croppedD.get(0, j), 2);
            }
            dABS = Math.sqrt(dABS);

            Matrix document = croppedD.getMatrix(0, croppedD.getRowDimension() - 1, i, i);

            double documentTimesQprim = 0d;

            for (int n = 0; n < document.getRowDimension() - 1; n++) {
                documentTimesQprim = documentTimesQprim + document.get(n, 0) * qPrim.get(0, n);
            }

            double cosSim = documentTimesQprim / (qABS * dABS);
            similarityList.add(cosSim);
        }
    }

    private void printSimilarity(List<Double> similarity, int ownValue) {
        System.out.println("\nDla najwiekszej wartosci wlasnej macierzy S rownej " + ownValue + " podobienstwo wynosi: ");
        for (int i = 0; i < similarity.size() -1; i++) {
            System.out.println("Document: " + i + " - Similarity: " + similarity.get(i));
        }
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
