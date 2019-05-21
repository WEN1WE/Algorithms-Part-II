import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;


public class SeamCarver {
    private Picture picture;
    private double[][] energy;

    /**
     * create a seam carver object based on the given picture.
     */
    public SeamCarver(Picture picture) {
        this.picture = new Picture(picture);
        energy = new double[width()][height()];

        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) {
                    energy[x][y] = 1000;
                } else {
                    energy[x][y] = Math.sqrt(xGradient(x, y) + yGradient(x, y));
                }
            }
        }
    }

    /**
     * current picture.
     */
    public Picture picture() {
        return new Picture(picture);
    }

    /**
     * width of current picture.
     */
    public int width() {
        return picture.width();
    }

    /**
     * height of current picture.
     */
    public int height() {
        return picture.height();
    }

    /**
     * energy of pixel at column x and row y.
     */
    public double energy(int x, int y) {
        return energy[x][y];
    }

    private double xGradient(int x, int y) {
        Color left = picture.get(x - 1, y);
        Color right = picture.get(x + 1, y);
        int Rx = left.getRed() - right.getRed();
        int Gx = left.getGreen() - right.getGreen();
        int Bx = left.getBlue() - right.getBlue();
        return Rx * Rx + Gx * Gx + Bx * Bx;
    }

    private double yGradient(int x, int y) {
        Color up = picture.get(x, y - 1);
        Color down = picture.get(x, y + 1);
        int Ry = up.getRed() - down.getRed();
        int Gy = up.getGreen() - down.getGreen();
        int By = up.getBlue() - down.getBlue();
        return Ry * Ry + Gy * Gy + By * By;
    }

    /**
     * sequence of indices for Vertical seam.
     */
    public int[] findVerticalSeam() {
        double[][] distTo = new double[width()][height()];
        int[][] edgeTo = new int[width()][height()];
        int[] verticalSeam = new int[height()];
        int seamEnd = 0;

        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                if (y == 0) {
                    distTo[x][y] = 1000;
                } else {
                    distTo[x][y] = Double.POSITIVE_INFINITY;
                }
            }
        }

        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                relaxX(distTo, edgeTo, x, y, x - 1, y + 1);
                relaxX(distTo, edgeTo, x, y, x, y + 1);
                relaxX(distTo, edgeTo, x, y, x + 1, y + 1);
            }
        }

        for (int x = 0; x < width(); x++) {
            if (distTo[seamEnd][height() - 1] > distTo[x][height() - 1]) {
                seamEnd = x;
            }
        }
        verticalSeam[height() - 1] = seamEnd;
        for (int y = height() - 1; y > 0; y--) {
            verticalSeam[y - 1] = edgeTo[verticalSeam[y]][y];
        }
        return verticalSeam;
    }

    /**
     * sequence of indices for horizontal seam.
     */
    public int[] findHorizontalSeam() {
        double[][] distTo = new double[width()][height()];
        int[][] edgeTo = new int[width()][height()];
        int[] horizontalSeam = new int[width()];
        int seamEnd = 0;

        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                if (x == 0) {
                    distTo[x][y] = 1000;
                } else {
                    distTo[x][y] = Double.POSITIVE_INFINITY;
                }
            }
        }

        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                relaxY(distTo, edgeTo, x, y, x + 1, y - 1);
                relaxY(distTo, edgeTo, x, y, x + 1, y);
                relaxY(distTo, edgeTo, x, y, x + 1, y + 1);
            }
        }

        for (int y = 0; y < height(); y++) {
            if (distTo[width() - 1][seamEnd] > distTo[width() - 1][y]) {
                seamEnd = y;
            }
        }

        horizontalSeam[width() - 1] = seamEnd;
        for (int x = width() - 1; x > 0; x--) {
            horizontalSeam[x - 1] = edgeTo[x][horizontalSeam[x]];
        }

        return horizontalSeam;
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < width() && y >= 0 && y < height();
    }

    private void relaxX(double[][] distTo, int[][] edgeTo, int x, int y, int nextX, int nextY) {
        if (isValid(nextX, nextY)) {
            double total = distTo[x][y] + energy[nextX][nextY];
            if (total < distTo[nextX][nextY]) {
                edgeTo[nextX][nextY] = x;
                distTo[nextX][nextY] = total;
            }
        }
    }

    private void relaxY(double[][] distTo, int[][] edgeTo, int x, int y, int nextX, int nextY) {
        if (isValid(nextX, nextY)) {
            double total = distTo[x][y] + energy[nextX][nextY];
            if (total < distTo[nextX][nextY]) {
                edgeTo[nextX][nextY] = y;
                distTo[nextX][nextY] = total;
            }
        }
    }

    /** remove horizontal seam from current picture. */
    public void removeHorizontalSeam(int[] seam) {
        int[] horizontalSeam = findHorizontalSeam();
        Picture newPicture = new Picture(width(), height() - 1);
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height() - 1; y++) {
                newPicture.set(x, y, picture.get(x, y));
                if (y == horizontalSeam[x]) {
                    continue;
                }
                newPicture.set(x, y, picture.get(x, y));
            }
        }
    }

    /** remove vertical seam from current picture. */
    public void removeVerticalSeam(int[] seam) {

    }



    public static void main(String[] args) {
        StdOut.printf("%9.0f ", Math.sqrt(52024));
    }

}
