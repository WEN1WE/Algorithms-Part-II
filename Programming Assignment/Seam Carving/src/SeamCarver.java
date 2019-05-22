import edu.princeton.cs.algs4.Picture;
import java.lang.IllegalArgumentException;

public class SeamCarver {
    private double[][] energy;
    private int[][] color;
    private int width;
    private int height;

    /**
     * create a seam carver object based on the given picture.
     */
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }
        width = picture.width();
        height = picture.height();
        color = getColor(picture);
        energy = calcEnergy();
    }

    private int[][] getColor(Picture picture) {
        int[][] color = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                color[x][y] = picture.getRGB(x, y);
            }
        }
        return color;
    }

    /** current picture. */
    public Picture picture() {
        Picture picture = new Picture(width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                picture.setRGB(x, y, color[x][y]);
            }
        }
        return picture;
    }

    /**
     * width of current picture.
     */
    public int width() {
        return width;
    }

    /**
     * height of current picture.
     */
    public int height() {
        return height;
    }

    /**
     * energy of pixel at column x and row y.
     */
    public double energy(int x, int y) {
        if (!isValid(x, y)) {
            throw new IllegalArgumentException();
        }
        return energy[x][y];
    }

    /**
     * sequence of indices for Vertical seam.
     */
    public int[] findVerticalSeam() {
        double[][] distTo = new double[width][height];
        int[][] edgeTo = new int[width][height];
        int[] verticalSeam = new int[height];
        int seamEnd = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (y == 0) {
                    distTo[x][y] = 1000;
                } else {
                    distTo[x][y] = Double.POSITIVE_INFINITY;
                }
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                relaxX(distTo, edgeTo, x, y, x - 1, y + 1);
                relaxX(distTo, edgeTo, x, y, x, y + 1);
                relaxX(distTo, edgeTo, x, y, x + 1, y + 1);
            }
        }

        for (int x = 0; x < width; x++) {
            if (distTo[seamEnd][height - 1] > distTo[x][height - 1]) {
                seamEnd = x;
            }
        }
        verticalSeam[height - 1] = seamEnd;

        for (int y = height - 1; y > 0; y--) {
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

    /** remove vertical seam from current picture. */
    public void removeVerticalSeam(int[] seam) {
        validateSeam(seam, height, width);

        width = width - 1;
        int[][] newColor = new int[width][height];

        for (int y = 0; y < height; y++) {
            int sign = 0;
            for (int x = 0; x < width; x++) {
                if (x == seam[y]) {
                    sign = 1;
                }
                if (sign == 0) {
                    newColor[x][y] = color[x][y];
                } else {
                    newColor[x][y] = color[x + 1][y];
                }
            }
        }

        color = newColor;
        energy = calcEnergy();
    }

    /** remove horizontal seam from current picture. */
    public void removeHorizontalSeam(int[] seam) {
        validateSeam(seam, width, height);

        height = height - 1;
        int[][] newColor = new int[width][height];
        for (int x = 0; x < width; x++) {
            int sign = 0;
            for (int y = 0; y < height; y++) {
                if (y == seam[x]) {
                    sign = 1;
                }
                if (sign == 0) {
                    newColor[x][y] = color[x][y];
                } else {
                    newColor[x][y] = color[x][y + 1];
                }
            }
        }
        color = newColor;
        energy = calcEnergy();
    }

    private double[][] calcEnergy() {
        double[][] energy = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    energy[x][y] = 1000;
                } else {
                    energy[x][y] = pixelEnergy(x, y);
                }
            }
        }
        return energy;
    }

    private double pixelEnergy(int x, int y) {
        double total = 0;
        int left = color[x - 1][y];
        int right = color[x + 1][y];
        int up = color[x][y - 1];
        int down = color[x][y + 1];
        total += Math.pow(getRed(left) - getRed(right), 2);
        total += Math.pow(getGreen(left) - getGreen(right), 2);
        total += Math.pow(getBlue(left) - getBlue(right), 2);
        total += Math.pow(getRed(up) - getRed(down), 2);
        total += Math.pow(getGreen(up) - getGreen(down), 2);
        total += Math.pow(getBlue(up) - getBlue(down), 2);
        return Math.sqrt(total);
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private static void validateSeam(int[] seam, int length, int range) {
        if (seam == null || range <= 1 || seam.length != length) {
            throw new java.lang.IllegalArgumentException();
        }

        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= range) {
                throw new java.lang.IllegalArgumentException();
            }

            if (i < seam.length - 1) {
                if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                    throw new java.lang.IllegalArgumentException();
                }
            }
        }
    }

    private static int getRed(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    private static int getGreen(int rgb) {
        return (rgb >>  8) & 0xFF;
    }

    private static int getBlue(int rgb) {
        return (rgb >>  0) & 0xFF;
    }
}
