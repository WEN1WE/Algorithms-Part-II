/**
 * @author wen
 * data: 2019/5/19
 */

public class Outcast {
    private final WordNet wordnet;

    /**
     * constructor takes a WordNet object
     */
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    /**
     * given an array of WordNet nouns, return an outcast
     */
    public String outcast(String[] nouns) {
        if (nouns == null) {
            throw new java.lang.IllegalArgumentException();
        }
        int sum = 0;
        int maxDistance = 0;
        String outcast = null;
        for (String noun : nouns) {
            for (String s : nouns) {
                sum += wordnet.distance(noun, s);
            }
            if (sum > maxDistance) {
                maxDistance = sum;
                outcast = noun;
            }
        }
        return outcast;
    }

    /*
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        In in = new In(args[2]);
        String[] nouns = in.readAllStrings();
        System.out.println((outcast.outcast(nouns)));

        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }

     */

}
