%%% ASP Program for coarsest subgraph isomorphism %%%
%%%
%
% F1,F2: Fingerprint -> Name
% A,B,C,D: Minutia -> ID
% T1,T2: Minutia -> Type
% L1,L2: Minutia -> Lenght
% R1,R2: Minutia -> Ratio
%
%%%

node(F1,T1,A) :- node(F1,T1,A,_,_).
edge(F1,A,C,T1,L1,R1) :- edge(F1,A,C,T1,_,L1,R1).
{iso_gen(F1,A,F2,B)} :- node(F1,T1,A), node(F2,T2,B), F1>F2, T1==T2.

{iso(F1,A,F2,B)} :- iso_gen(F1,A,F2,B), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1==L2, R1==R2.
{iso(F1,C,F2,D)} :- iso_gen(F1,A,F2,B), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1==L2, R1==R2.

:- iso_gen(F1,A,F2,B), iso_gen(F1,A,F2,C), B!=C.
:- iso_gen(F1,B,F2,A), iso_gen(F1,C,F2,A), B!=C.

:- iso(F1,A,F2,B), iso(F1,A,F2,C), B!=C.
:- iso(F1,B,F2,A), iso(F1,C,F2,A), B!=C.

sizeiso(N) :- N = #count{F1,A,F2,B : iso(F1,A,F2,B)}.
#maximize {N : sizeiso(N)}.

#show sizeiso/1.
#show iso/4.
