import edu.princeton.cs.algs4.Picture;
import java.lang.IllegalArgumentException;

public class SeamCarver {
    private double[][] energy;
    private int[][] color;

    /**
     * create a seam carver object based on the given picture.
     */
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }
        color = getColor(picture);
        energy = calcEnergy(color);
    }

    /** current picture. */
    public Picture picture() {
        return picture(color);
    }

    /**
     * width of current picture.
     */
    public int width() {
        return energy.length;
    }

    /**
     * height of current picture.
     */
    public int height() {
        return energy[0].length;
    }

    /**
     * energy of pixel at column x and row y.
     */
    public double energy(int x, int y) {
        if (x >= 0 && x < width() && y >= 0 && y < height()) {
            throw new IllegalArgumentException();
        }
        return energy[x][y];
    }

    /**
     * sequence of indices for Vertical seam.
     */
    public int[] findVerticalSeam() {
        return findVerticalSeam(energy);
    }

    /**
     * sequence of indices for horizontal seam.
     */
    public int[] findHorizontalSeam() {
        return findVerticalSeam(transposeEnergy(energy));
    }

    /** remove vertical seam from current picture. */
    public void removeVerticalSeam(int[] seam) {
        color = removeVerticalSeam(color, seam);
        energy = calcEnergy(color);
    }

    /** remove horizontal seam from current picture. */
    public void removeHorizontalSeam(int[] seam) {
        color = removeVerticalSeam(transposeColor(color), seam);
        color = transposeColor(color);
        energy = calcEnergy(color);
    }

    private static double[][] transposeEnergy(double[][] energy) {
        int width = energy[0].length;
        int height = energy.length;
        double[][] transposeEnergy = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                transposeEnergy[x][y] = energy[y][x];
            }
        }
        return transposeEnergy;
    }

    private static int[][] transposeColor(int[][] color) {
        int width = color[0].length;
        int height = color.length;
        int[][] transposeColor = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                transposeColor[x][y] = color[y][x];
            }
        }
        return transposeColor;
    }



    private static Picture picture(int[][] color) {
        int width = color.length;
        int height = color[0].length;
        Picture picture = new Picture(width, height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                picture.setRGB(x, y, color[x][y]);
            }
        }
        return picture;
    }

    private static int[][] getColor(Picture picture) {
        int width = picture.width();
        int height = picture.height();
        int[][] color = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                color[x][y] = picture.getRGB(x, y);
            }
        }
        return color;
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

    private static double[][] calcEnergy(int[][] color) {
        int width = color.length;
        int height = color[0].length;
        double[][] energy = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    energy[x][y] = 1000;
                } else {
                    energy[x][y] = Math.sqrt(xGradientSquare(color, x, y) + yGradientSquare(color, x, y));
                }
            }
        }
        return energy;
    }

    private static double xGradientSquare(int[][] color, int x, int y) {
        int left = color[x - 1][y];
        int right = color[x + 1][y];
        int dR = getRed(left) - getRed(right);
        int dG = getGreen(left) - getGreen(right);
        int dB = getBlue(left) - getBlue(right);
        return dR * dR + dG * dG + dB * dB;
    }

    private static double yGradientSquare(int[][] color, int x, int y) {
        int up = color[x][y - 1];
        int down = color[x][y + 1];
        int dR = getRed(up) - getRed(down);
        int dG = getGreen(up) - getGreen(down);
        int dB = getBlue(up) - getBlue(down);
        return dR * dR + dG * dG + dB * dB;
    }

    private static int[] findVerticalSeam(double[][] energy) {
        int width = energy.length;
        int height = energy[0].length;
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
                relax(energy, distTo, edgeTo, x, y, x - 1, y + 1);
                relax(energy, distTo, edgeTo, x, y, x, y + 1);
                relax(energy, distTo, edgeTo, x, y, x + 1, y + 1);
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

    private static void relax(double[][] energy, double[][] distTo, int[][] edgeTo, int x, int y, int nextX, int nextY) {
        int width = energy.length;
        int height = energy[0].length;
        if (nextX >= 0 && nextX < width && nextY >= 0 && nextY < height) {
            double total = distTo[x][y] + energy[nextX][nextY];
            if (total < distTo[nextX][nextY]) {
                edgeTo[nextX][nextY] = x;
                distTo[nextX][nextY] = total;
            }
        }
    }

    private static int[][] removeVerticalSeam(int[][] color, int[] seam) {
        int width = color.length;
        int height = color[0].length;

        if (seam == null || width <= 1 || seam.length != height) {
            throw new java.lang.IllegalArgumentException();
        }

        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= width) {
                throw new java.lang.IllegalArgumentException();
            }

            if (i < seam.length - 1) {
                if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                    throw new java.lang.IllegalArgumentException();
                }
            }
        }

        int[][] newColor = new int[width - 1][height];
        for (int y = 0; y < height; y++) {
            int sign = 0;
            for (int x = 0; x < width - 1; x++) {
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
        return newColor;
    }
}
