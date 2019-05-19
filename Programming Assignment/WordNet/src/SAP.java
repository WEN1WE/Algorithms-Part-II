/**
 * @author wen
 * @data 2019/5/19
 */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.In;
import java.util.ArrayList;

public class SAP {
    private final Digraph G;

    public SAP(Digraph G) {
        this.G = new Digraph(G);
    }

    /**
     * Returns length of shortest ancestral path between v and w.
     * Returns -1 if no such path.
     */
    public int length(int v, int w) {
        AncestorBFS bfs = new AncestorBFS(G, v, w);
        return bfs.length;
    }

    /**
     * Returns a common ancestor of v and w that participates in a shortest ancestral path.
     * Returns -1 if no such path.
     */
    public int ancestor(int v, int w) {
        AncestorBFS bfs = new AncestorBFS(G, v, w);
        return bfs.ancestor;
    }

    /**
     * Returns length of shortest ancestral path between any vertex in v and any vertex in w.
     * Returns -1 if no such path.
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        AncestorBFS bfs = new AncestorBFS(G, v, w);
        return bfs.length;
    }

    /**
     * a common ancestor that participates in shortest ancestral path.
     * -1 if no such path.
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        AncestorBFS bfs = new AncestorBFS(G, v, w);
        return bfs.ancestor;
    }

    private static class AncestorBFS {
        private int length = -1;
        private int ancestor = -1;

        private AncestorBFS(Digraph G, int v, int w) {
            this(G, toIterable(v), toIterable(w));
        }

        private AncestorBFS(Digraph G, Iterable<Integer> v, Iterable<Integer> w) {
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
        }

        private static Iterable<Integer> toIterable(int i) {
            ArrayList<Integer> array = new ArrayList<>();
            array.add(i);
            return array;
        }

        private int length() {
            return length;
        }

        private int ancestor() {
            return ancestor;
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

