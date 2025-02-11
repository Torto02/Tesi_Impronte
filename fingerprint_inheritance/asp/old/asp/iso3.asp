%% clingo digit1.asp digit2.asp arch.asp iso3.asp  %%

{iso(A,B)} :- node(G1,T1,A,_,_), node(G2,T2,B,_,_), G1 != G2, T1 == T2.

:- iso(A,B), iso(A,C), B != C, colored(_,A,_), colored(_,B,_), colored(_,C,_).
:- iso(B,A), iso(C,A), B != C, colored(_,A,_), colored(_,B,_), colored(_,C,_).

ok(A,B) :- iso(A,B), colored(G1,A,C1), colored(G2,B,C2), edge(G1,A,C,T1,_,L1), colored(G1,C,C3), edge(G2,B,D,T2,_,L2), colored(G2,D,C4), iso(C,D), G1 != G2, T1 == T2, L1 < (L2+20), L2 < (L1+20).

:- iso(A,B), not ok(A,B).

%%%% Solution %%%%
sizeiso(N) :- N = #count{A,B : iso(A,B)}.
#maximize {N : sizeiso(N)}.
#show sizeiso/1.
%#show iso/2.
