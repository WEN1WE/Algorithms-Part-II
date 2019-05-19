/**
 * @author wen
 * data: 2019/5/19
 */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.In;
import java.util.ArrayList;

public class SAP {
    private final Digraph G;

    public SAP(Digraph G) {
        if (G == null) {
            throw new java.lang.IllegalArgumentException();
        }
        this.G = new Digraph(G);
    }

    /**
     * Returns length of shortest ancestral path between v and w.
     * Returns -1 if no such path.
     */
    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        return getSAP(G, v, w)[0];
    }

    /**
     * Returns a common ancestor of v and w that participates in a shortest ancestral path.
     * Returns -1 if no such path.
     */
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        return getSAP(G, v, w)[1];
    }

    /**
     * Returns length of shortest ancestral path between any vertex in v and any vertex in w.
     * Returns -1 if no such path.
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new java.lang.IllegalArgumentException();
        }
        validateVertexs(v);
        validateVertexs(w);
        return getSAP(G, v, w)[0];
    }

    /**
     * a common ancestor that participates in shortest ancestral path.
     * -1 if no such path.
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new java.lang.IllegalArgumentException();
        }
        validateVertexs(v);
        validateVertexs(w);
        return getSAP(G, v, w)[1];
    }

    private static int[] getSAP(Digraph G, int v, int w) {
        return getSAP(G, toIterable(v), toIterable(w));
    }
    private static int[] getSAP(Digraph G, Iterable<Integer> v, Iterable<Integer> w) {
        int length = -1;
        int ancestor = -1;
        int[] result = new int[2];
        int nV = G.V();
        int total;
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);

        for (int i = 0; i < nV; i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                total = bfsV.distTo(i) + bfsW.distTo(i);
                if (length == -1 || length > total) {
                    length = total;
                    ancestor = i;
                }
            }
        }
        result[0] = length;
        result[1] = ancestor;
        return result;
    }

    private static Iterable<Integer> toIterable(int i) {
        ArrayList<Integer> array = new ArrayList<>();
        array.add(i);
        return array;
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= G.V()) {
            throw new java.lang.IllegalArgumentException();
        }
    }

    private void validateVertexs(Iterable<Integer> vertexs) {
        for (Integer v : vertexs) {
            if (v == null || v < 0 || v >= G.V()) {
                throw new java.lang.IllegalArgumentException();
            }
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        ArrayList<Integer> v = new ArrayList<>();
        v.add(13);
        v.add(23);
        v.add(24);
        ArrayList<Integer> w = new ArrayList<>();
        w.add(6);
        w.add(16);
        w.add(17);
        System.out.println(sap.length(v, w));
        System.out.println(sap.ancestor(v, w));
    }
}

