arc(G,I,J) :- node(G,edge,I,_,_), node(G,edge,J,_,_), edge(G,I,J,edge,A,_), I != J+1, J != I+1, A<5.
arc(G,I,J) :- node(G,edge,I,_,_), node(G,edge,J,_,_), edge(G,I,J,edge,A,_), I != J+1, J != I+1, 175<A, A<185.
arc(G,I,J) :- node(G,edge,I,_,_), node(G,edge,J,_,_), edge(G,I,J,edge,A,_), I != J+1, J != I+1, 355<A.

nodo(G,A) :- node(G,_,A,_,_), not arc(G,A,_), not arc(G,_,A).
arco(G,S,F,P) :- edge(G,S,F,_,_,P), nodo(G,S), nodo(G,F), S != F+1, F != S+1.

path(G,A,B,W) :- nodo(G,A), nodo(G,B), arco(G,A,B,W).
path(G,A,B,W) :- nodo(G,A), nodo(G,B), nodo(G,C), arco(G,A,C,V), path(G,C,B,U), W = U+V.
:- path(G,A,B,V1), path(G,A,B,V2), V1 != V2.

strong(G,A,B) :- A != B, path(G,A,B,O1), path(G,B,A,O2).
strong(G,A,B) :-  A != B, B != C, A != C, strong(G,A,C), strong(G,C,B).

nocfc(G,E) :- nodo(G,E), not strong(G,E,_).

%#show nocfc/2.

colours(1..99).
1 {scc(G,A,C) :colours(C)} 1 :- nodo(G,A).
:- scc(G,A,C1), scc(G,B,C2), strong(G,A,B), colours(C1), colours(C2), C1 != C2.

%#show scc/3.

colour(100).
1 {arcs(G,I,J,C) : colour(C)} 1 :- arc(G,I,J).

%#show arcs/4.

colored(G,N,C) :- scc(G,N,C).
colored(G,I,C) :- arcs(G,I,_,C).
colored(G,J,C) :- arcs(G,_,J,C).

%#show colored/3.

coloredge(G,I,J,C) :-  edge(G,I,J,_,_,_), colored(G,I,C1), colored(G,J,C2), C1 == C2, C = C1.
coloredge(G,I,J,C) :- edge(G,I,J,_,_,_), colored(G,I,C1), colored(G,J,C2), C1 != C2, C = 0.

#show coloredge/4.
