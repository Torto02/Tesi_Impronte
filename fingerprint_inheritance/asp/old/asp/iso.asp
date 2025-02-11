%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% ASP Program for coarsest subgraph isomorphism
%%% 19 march 2019
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%%% Structure Constraints %%%%
nodo(A) :- node(_,_,A,_,_).
{iso(A,B)} :- node(G1,T,A,_,_), node(G2,T,B,_,_), G1 != G2.

:- iso(A,B), iso(A,C), B != C, nodo(A), nodo(B), nodo(C).
:- iso(B,A), iso(C,A), B != C, nodo(A), nodo(B), nodo(C).

%%%% Isomorphism Constraints %%%%
%nonleaf(A) :- edge(_,A,B). Not oriented edges
%ok(A,B) :- iso(A,B), nonleaf(A), nonleaf(B), nodo(A), nodo(B).

ok(A,B) :- iso(A,B), edge(G1,A,C,T,_,L1), edge(G2,B,D,T,_,L2), iso(C,D), G1!=G2.
:- iso(A,B), not ok(A,B), nodo(A), nodo(B).

%%%% Solution %%%%
sizeiso(N) :- N = #count{A,B : iso(A,B)}.
#maximize {N : sizeiso(N)}.
#show sizeiso/1.
%#show iso/2.