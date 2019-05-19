/**
 * @author wen
 * data: 2019/5/19
 */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import java.util.HashMap;


public class WordNet {
    private HashMap<Integer, String> idToSynsets;
    private HashMap<String, Integer> wordToId;
    private SAP sap;

    /**
     * constructor takes the name of the two input files
     */
    public WordNet(String synsets, String hypernyms) {
        isLegal(synsets);
        isLegal(hypernyms);
        In synsetsIn = new In(synsets);
        In hypernymsIn = new In(hypernyms);
        idToSynsets = new HashMap<>();
        wordToId = new HashMap<>();
        parseSynsets(synsetsIn);
        Digraph G = parseHypernyms(hypernymsIn);
        sap = new SAP(G);
    }

    private void parseSynsets(In in) {
        while (!in.isEmpty()) {
            String[] line = in.readLine().split(",");
            int id = Integer.parseInt(line[0]);
            String synsets = line[1];
            idToSynsets.put(id, synsets);

            for (String numn : synsets.split(" ")) {
                wordToId.put(numn, id);
            }
        }
    }

    private Digraph parseHypernyms(In in) {
        Digraph G = new Digraph(idToSynsets.size());
        while (!in.isEmpty()) {
            String[] line = in.readLine().split(",");
            int v = Integer.parseInt(line[0]);
            for (int i = 1; i < line.length; i++) {
                int w = Integer.parseInt(line[i]);
                G.addEdge(v, w);
            }
        }
        isRootedDAG(G);
        return G;
    }

    private void isRootedDAG(Digraph G) {
        DirectedCycle cycleG = new DirectedCycle(G);
        int count = 0;
        for (int v = 0; v < G.V(); v++) {
            if (G.adj(v) == null) {
                count++;
            }
        }
        if (cycleG.hasCycle() || count != 1) {
            throw new java.lang.IllegalArgumentException();
        }
    }

    /**
     * returns all WordNet nouns
     */
    public Iterable<String> nouns() {
        return wordToId.keySet();
    }

    /**
     * is the word a WordNet noun?
     */
    public boolean isNoun(String word) {
        isLegal(word);
        return wordToId.containsKey(word);
    }

    /** distance between nounA and nounB (defined below) */
    public int distance(String nounA, String nounB) {
        isLegal(nounA);
        isLegal(nounB);
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new java.lang.IllegalArgumentException();
        return sap.length(wordToId.get(nounA), wordToId.get(nounB));
    }

    /** a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
     in a shortest ancestral path (defined below) */
    public String sap(String nounA, String nounB) {
        isLegal(nounA);
        isLegal(nounB);
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new java.lang.IllegalArgumentException();
        return idToSynsets.get(sap.ancestor(wordToId.get(nounA), wordToId.get(nounB)));
    }

    private void isLegal(Object object) {
        if (object == null) {
            throw new java.lang.IllegalArgumentException();
        }
    }



    // do unit testing of this class
    //public static void main(String[] args)


}