/**
 * @author wen
 * data: 2019/5/19
 */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import java.util.HashMap;

public class WordNet {
    private final HashMap<Integer, String> id2Synsets;
    private final HashMap<String, Bag<Integer>> word2Id;
    private final SAP sap;

    /**
     * constructor takes the name of the two input files
     */
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new java.lang.IllegalArgumentException();
        }
        In synsetsIn = new In(synsets);
        In hypernymsIn = new In(hypernyms);
        id2Synsets = new HashMap<>();
        word2Id = new HashMap<>();
        parseSynsets(synsetsIn);
        Digraph g = parseHypernyms(hypernymsIn);
        sap = new SAP(g);
    }

    private void parseSynsets(In in) {
        while (!in.isEmpty()) {
            String[] line = in.readLine().split(",");
            int id = Integer.parseInt(line[0]);
            String synsets = line[1];
            id2Synsets.put(id, synsets);
            for (String numn : synsets.split(" ")) {
                if (word2Id.containsKey(numn)) {
                    word2Id.get(numn).add(id);
                } else {
                    Bag<Integer> idBag = new Bag<>();
                    idBag.add(id);
                    word2Id.put(numn, idBag);
                }
            }
        }
    }

    private Digraph parseHypernyms(In in) {
        Digraph G = new Digraph(id2Synsets.size());
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
            if (G.outdegree(v) == 0) {
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
        return word2Id.keySet();
    }

    /**
     * is the word a WordNet noun?
     */
    public boolean isNoun(String word) {
        if (word == null) {
            throw new java.lang.IllegalArgumentException();
        }
        return word2Id.containsKey(word);
    }

    /**
     * distance between nounA and nounB (defined below)
     */
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new java.lang.IllegalArgumentException();
        }
        return sap.length(word2Id.get(nounA), word2Id.get(nounB));
    }

    /**
     * a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
     * in a shortest ancestral path (defined below)
     */
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new java.lang.IllegalArgumentException();
        }
        return id2Synsets.get(sap.ancestor(word2Id.get(nounA), word2Id.get(nounB)));
    }

    /*
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);

        System.out.println(wordnet.id2Synsets.get(77));
        System.out.println(wordnet.word2Id.get("Abney_level").size());
        Iterable<Integer> b = wordnet.G.adj(38003);
        System.out.println(wordnet.G.adj(38003));


        Iterable<Integer> t = wordnet.word2Id.get("horse");
        for (Integer i : t) {
            System.out.println(i);
        }
        System.out.println(wordnet.word2Id.size());
        Iterable<Integer> t2 = wordnet.word2Id.get("zebra");
        for (Integer i : t2) {
            System.out.println(i);
        }
        System.out.println(wordnet.sap.length(wordnet.word2Id.get("horse"), wordnet.word2Id.get("horse")));
        //System.out.println(wordnet.word2Id.get("horse"));
        //System.out.println(wordnet.word2Id.get("zebra"));
    }
    */
}