%%% ASP Program for coarsest subgraph isomorphism %%%
%%%
%
% F1,F2: Fingerprint -> Name
% A,B,C,D: Minutia -> ID
% T1,T2: Minutia -> Type
% L1,L2: Minutia -> Lenght
% R1,R2: Minutia -> Ratio
%
% W: Weight (ISO)
%
%%%

#const weight = 900.
#const weight1 = 850.
#const weight2 = 800.
#const weight3 = 750.
#const weight4 = 700.
#const weight5 = 650.
#const weight6 = 600.

#const lenght = 15.

#const ratio1 = 10.
#const ratio2 = 20.
#const ratio3 = 60.
#const ratio4 = 120.

#const r0 = 0.
#const r1 = 30.
#const r2 = 100.
#const r3 = 250.

node(F1,T1,A) :- node(F1,T1,A,_,_).
edge(F1,A,C,T1,L1,R1) :- edge(F1,A,C,T1,_,L1,R1).

{iso_gen(F1,A,F2,B)} :- node(F1,T1,A), node(F2,T2,B), F1>F2, T1==T2.

{iso(F1,A,F2,B,W)} :- iso_gen(F1,A,F2,B), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), iso_gen(F1,C,F2,D), F1!=F2, A==B, C==D, T1==T2, L1==L2, R1==R2, W=weight.
{iso(F1,C,F2,D,W)} :- iso_gen(F1,A,F2,B), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), iso_gen(F1,C,F2,D), F1!=F2, A==B, C==D, T1==T2, L1==L2, R1==R2, W=weight.

{iso(F1,A,F2,B,W)} :- iso_gen(F1,A,F2,B), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1==L2, R1==R2, W=weight1.
{iso(F1,C,F2,D,W)} :- iso_gen(F1,A,F2,B), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1==L2, R1==R2, W=weight1.

{iso(F1,A,F2,B,W)} :- iso_gen(F1,A,F2,B), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<=(L2+lenght), L2<=(L1+lenght), R1==R2, W=weight2.
{iso(F1,C,F2,D,W)} :- iso_gen(F1,A,F2,B), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<=(L2+lenght), L2<=(L1+lenght), R1==R2, W=weight2.

{iso(F1,A,F2,B,W)} :- iso_gen(F1,A,F2,B), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<=(L2+lenght), L2<=(L1+lenght), R1>r0, R1<=r1, R1<=(R2+ratio1), R2<=(R1+ratio1), W=weight3.
{iso(F1,C,F2,D,W)} :- iso_gen(F1,A,F2,B), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<=(L2+lenght), L2<=(L1+lenght), R1>r0, R1<=r1, R1<=(R2+ratio1), R2<=(R1+ratio1), W=weight3.

{iso(F1,A,F2,B,W)} :- iso_gen(F1,A,F2,B), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<=(L2+lenght), L2<=(L1+lenght), R1>r1, R1<=r2, R1<=(R2+ratio2), R2<=(R1+ratio2), W=weight4.
{iso(F1,C,F2,D,W)} :- iso_gen(F1,A,F2,B), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<=(L2+lenght), L2<=(L1+lenght), R1>r1, R1<=r2, R1<=(R2+ratio2), R2<=(R1+ratio2), W=weight4.

{iso(F1,A,F2,B,W)} :- iso_gen(F1,A,F2,B), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<=(L2+lenght), L2<=(L1+lenght), R1>r2, R1<=r3, R1<=(R2+ratio3), R2<=(R1+ratio3), W=weight5.
{iso(F1,C,F2,D,W)} :- iso_gen(F1,A,F2,B), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<=(L2+lenght), L2<=(L1+lenght), R1>r2, R1<=r3, R1<=(R2+ratio3), R2<=(R1+ratio3), W=weight5.

{iso(F1,A,F2,B,W)} :- iso_gen(F1,A,F2,B), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<=(L2+lenght), L2<=(L1+lenght), R1>r3, R1<=(R2+ratio4), R2<=(R1+ratio4), W=weight6.
{iso(F1,C,F2,D,W)} :- iso_gen(F1,A,F2,B), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<=(L2+lenght), L2<=(L1+lenght), R1>r3, R1<=(R2+ratio4), R2<=(R1+ratio4), W=weight6.

:- iso_gen(F1,A,F2,B), iso_gen(F1,A,F2,C), B!=C.
:- iso_gen(F1,B,F2,A), iso_gen(F1,C,F2,A), B!=C.

:- iso(F1,A,F2,B,W), iso(F1,A,F2,C,W), B!=C.
:- iso(F1,B,F2,A,W), iso(F1,C,F2,A,W), B!=C.

:- iso(F1,A,F2,B,W1), iso(F1,A,F2,B,W2), W1!=W2.

weightiso(N) :- N = #sum{W,F1,A,F2,B : iso(F1,A,F2,B,W)}.
sizeiso(N) :- N = #count{W,F1,A,F2,B : iso(F1,A,F2,B,W)}.

%%% fingerprint -> "fp" and "fp_copy"
sizef1(N) :- N = #count{T1,A : node(fp,T1,A)}.
sizef2(N) :- N = #count{T1,A : node(fp_copy,T1,A)}.

sizefp(X) :- sizef1(X), sizef2(Y), X<=Y.
sizefp(Y) :- sizef1(X), sizef2(Y), X>Y.

score(X/Y) :- weightiso(X), sizefp(Y).

#maximize {N@150 : score(N)}.
% #maximize {N@100 : weightiso(N)}.
% #maximize {N@50 : sizeiso(N)}.

#show score/1.
% #show iso/5.
