%%%%  clingo digitN1.asp digitN2.asp colourordergraph.asp iso100.asp %%%%

{iso(A,B)} :- node(G1,T1,A,_,_), colored(G1,A,C1), C1 != 100, node(G2,T2,B,_,_), colored(G2,B,C2), C2!=100, G1 != G2, T1 == T2.

:- iso(A,B), iso(A,C), B != C, colored(_,A,_), colored(_,B,_), colored(_,C,_).
:- iso(B,A), iso(C,A), B != C, colored(_,A,_), colored(_,B,_), colored(_,C,_).

{iso100(A,B)} :- node(G1,T1,A,_,_), colored(G1,A,100), node(G2,T2,B,_,_), colored(G2,B,100), G1 != G2, T1 == T2.

:- iso100(A,B), iso100(A,C), B != C, colored(_,A,100), colored(_,B,100), colored(_,C,100).
:- iso100(B,A), iso100(C,A), B != C, colored(_,A,100), colored(_,B,100), colored(_,C,100).

isok(A,B) :- iso(A,B).
isok(A,B) :- iso100(A,B).

ok(A,B) :- isok(A,B), colored(G1,A,C1), colored(G2,B,C2), edge(G1,A,C,T1,_,L1), colored(G1,C,C3), C1==C2, edge(G2,B,D,T2,_,L2), colored(G2,D,C4), C2 == C4, isok(C,D), G1 != G2, T1 == T2, L1 < (L2+15), L2 < (L1+15).

:- iso(A,B), not ok(A,B).
:- iso100(A,B), not ok(A,B).

%%%% Solution %%%%
sizeiso(N) :- N = #count{A,B : iso(A,B)}.
#maximize {N : sizeiso(N)}.
#show sizeiso/1.
%#show iso/2.

sizeiso100(N1) :- N1 = #count{A,B : iso100(A,B)}.
#maximize {N1 : sizeiso100(N1)}.
#show sizeiso100/1.
%#show iso100/2.
