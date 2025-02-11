# fingerprint_inheritance

## Goal
Realize an algorithm to check fingerprint inheritance. Key idea is to use the minutiae to construct a topologically meaningful graph to exploit as base for the matching.

## The situation:
- We extract minutiae and other info related to the fingerprint with the software sourceAFIS (https://sourceafis.machinezoo.com/);
- from this information (mostly in .json and .dat) we create the minutiae and edges in the c++ program;
- *thesis Venturini*:
  - we correct the position of the minutiae that are not corretly placed (with skeleton image as reference);
  - we construct the graph where the edges follow the ridges in the skeleton (always using the skeleton image as binary matrix).
  
 ## Future works
- Optimize the graph:
  - edges as flow to exploit direction;
  - collapse non-usefull ridges;
- Use two graphs to execute (max) bisimulation to extract similar patterns.

- In case the only data avaiable are minutiae use plp to construct the graph.
 
 ## TODO
- Check code added by Venturini:
  - paths not as variables;
  - optimization;
  - clean the code.
