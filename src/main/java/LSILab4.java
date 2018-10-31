import Jama.Matrix;
import Jama.SingularValueDecomposition;

import java.util.ArrayList;
import java.util.List;

public class LSILab4 {
    private Matrix matrix;
    private Matrix queryVector;

    private LSILab4() {
        this.matrix = MatrixUtils.readMatrix("data.txt");
        this.queryVector = MatrixUtils.readMatrix("query.txt");
    }

    public static void main(String[] args) {
        new LSILab4().run();
    }

    private void run() {
        if (matrix != null && queryVector != null) {
            printMatrixAndVector();

            List<Double> similarity4 = decomposeAndCountSimilarities(4);
            printSimilarity(similarity4, 4);

            List<Double> similarity2 = decomposeAndCountSimilarities(2);
            printSimilarity(similarity2, 2);
        } else {
            System.out.println("Błąd odczytu z pliku ");
        }
    }

    private List<Double> decomposeAndCountSimilarities(int sCount) {
        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);
        // get K, S, and D
        Matrix k = svd.getU();
        Matrix s = svd.getS(); //macierz diagonalna
        Matrix d = svd.getV().transpose();

        return countSimilarity(k, s, d, sCount);
    }

    private List<Double> countSimilarity(Matrix k, Matrix s, Matrix d, int sCount) {
        List<Double> similarityList = new ArrayList<Double>();
        System.out.println("Poczatek przetwarzania ");
        // cut off appropriate columns and rows from K, S, and D
        Matrix croppedS = s.getMatrix(0, sCount - 1, 0, sCount - 1);
        Matrix croppedK = k.getMatrix(0, k.getRowDimension() - 1, 0, sCount - 1);
        Matrix croppedD = d.getMatrix(0, sCount - 1, 0, d.getColumnDimension() - 1);

        // transform the query vector
        Matrix qPrim = queryVector.transpose().times(croppedK).times(croppedS.inverse());

        // count ABS of Q prim vector
        double qABS = MatrixUtils.geMatrixtABS(qPrim, qPrim.getColumnDimension());

        for (int i = 0; i < croppedD.getColumnDimension() - 1; i++) {
            // count ABS of document vector
            double dABS = MatrixUtils.geMatrixtABS(croppedD, croppedD.getRowDimension());

            Matrix document = croppedD.getMatrix(0, croppedD.getRowDimension() - 1, i, i);

            double documentTimesQprim = 0d;
            for (int n = 0; n < document.getRowDimension() - 1; n++) {
                documentTimesQprim = documentTimesQprim + document.get(n, 0) * qPrim.get(0, n);
            }

            double cosSim = documentTimesQprim / (qABS * dABS);
            similarityList.add(cosSim);
        }
        return similarityList;
    }

    private void printSimilarity(List<Double> similarity, int sCount) {
        System.out.println("\nDla najwiekszej wartosci wlasnej macierzy S rownej " + sCount + " podobienstwo wynosi: ");
        for (int i = 0; i < similarity.size() - 1; i++) {
            System.out.println("Document: " + i + " - Similarity: " + similarity.get(i));
        }
    }

    private void printMatrixAndVector() {
        System.out.println("Matrix:");
        matrix.print(3, 2);

        // print the dimensions of the matrix
        System.out.println("matrix: " + MatrixUtils.dim(matrix));

        System.out.println("Query:");
        queryVector.print(3, 2);
        System.out.println("Q: " + MatrixUtils.dim(queryVector));
    }
}
