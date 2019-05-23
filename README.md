### Programming Assignment 1: WordNet
#### Tips:
- Use HashMap to parse file.
- A noun may appear in more than one synset
- Use DirectedCycle to check DAG.
- Use G.outdegree(root) == 0 to check only one root.

### Programming Assignment 2: Seam Carving
#### Tips:
- Don’t use an explicit EdgeWeightedDigraph. Instead, execute the topological sort algorithm directly on the pixels.
- It's not a good idea to transpose the Picture or int[][].(Hard to fix consecutive horizontal seam removals)
- Creating Color objects can be a bottleneck, so use int[][] to store picture.(32-bit int  encodes the color)
- Cache energy array.
