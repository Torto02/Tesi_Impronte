%%% ASP Program for coarsest subgraph isomorphism %%%
%%%
%
% F1,F2: Fingerprint -> Name
% A,B,C,D: Minutia -> ID
% T1,T2: Minutia -> Type
% L1,L2: Minutia -> Lenght
% R1,R2: Minutia -> Ratio
% W,W1,W2: Iso -> Weight
%
%%%

#const lenght = 10.

#const ratio_min = 20.
#const ratio_max = 100.
#const ratio_fmax = 200.
#const ratio_umax = 1000.

#const ratio_low = 2.
#const ratio_mid = 10.
#const ratio_high = 20.
#const ratio_fhigh = 100.
#const ratio_uhigh = 200.

#const weight_1 = 100.
#const weight_2 = 80.
#const weight_3 = 20.
#const weight_4 = 10.

node(F1,T1,A) :- node(F1,T1,A,_,_).
edge(F1,A,C,T1,L1,R1) :- edge(F1,A,C,T1,_,L1,R1).
{iso_gen(F1,A,F2,B)} :- node(F1,T1,A), node(F2,T2,B), F1>F2, T1==T2.

{iso(F1,A,F2,B,W)} :- iso_gen(F1,A,F2,B), colored(F1,A,C1), colored(F2,B,C2), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), colored(F1,C,C3), colored(F2,D,C4), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1==L2, R1==R2, W=weight_1.
{iso(F1,C,F2,D,W)} :- iso_gen(F1,A,F2,B), colored(F1,A,C1), colored(F2,B,C2), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), colored(F1,C,C3), colored(F2,D,C4), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1==L2, R1==R2, W=weight_1.

{iso(F1,A,F2,B,W)} :- iso_gen(F1,A,F2,B), colored(F1,A,C1), colored(F2,B,C2), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), colored(F1,C,C3), colored(F2,D,C4), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<(L2+lenght), L2<(L1+lenght), R1==R2, W=weight_2.
{iso(F1,C,F2,D,W)} :- iso_gen(F1,A,F2,B), colored(F1,A,C1), colored(F2,B,C2), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), colored(F1,C,C3), colored(F2,D,C4), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<(L2+lenght), L2<(L1+lenght), R1==R2, W=weight_2.

{iso(F1,A,F2,B,W)} :- iso_gen(F1,A,F2,B), colored(F1,A,C1), colored(F2,B,C2), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), colored(F1,C,C3), colored(F2,D,C4), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<(L2+lenght), L2<(L1+lenght), R1>0, R1<=ratio_min, R1<=(R2+ratio_low), R2<=(R1+ratio_low), W=weight_3.
{iso(F1,C,F2,D,W)} :- iso_gen(F1,A,F2,B), colored(F1,A,C1), colored(F2,B,C2), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), colored(F1,C,C3), colored(F2,D,C4), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<(L2+lenght), L2<(L1+lenght), R1>0, R1<=ratio_min, R1<=(R2+ratio_low), R2<=(R1+ratio_low), W=weight_3.

{iso(F1,A,F2,B,W)} :- iso_gen(F1,A,F2,B), colored(F1,A,C1), colored(F2,B,C2), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), colored(F1,C,C3), colored(F2,D,C4), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<(L2+lenght), L2<(L1+lenght), R1>ratio_min, R1<=ratio_max, R1<=(R2+ratio_mid), R2<=(R1+ratio_mid), W=weight_3.
{iso(F1,C,F2,D,W)} :- iso_gen(F1,A,F2,B), colored(F1,A,C1), colored(F2,B,C2), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), colored(F1,C,C3), colored(F2,D,C4), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<(L2+lenght), L2<(L1+lenght), R1>ratio_min, R1<=ratio_max, R1<=(R2+ratio_mid), R2<=(R1+ratio_mid), W=weight_3.

{iso(F1,A,F2,B,W)} :- iso_gen(F1,A,F2,B), colored(F1,A,C1), colored(F2,B,C2), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), colored(F1,C,C3), colored(F2,D,C4), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<(L2+lenght), L2<(L1+lenght), R1>ratio_max, R1<=ratio_fmax, R1<=(R2+ratio_high), R2<=(R1+ratio_high), W=weight_3.
{iso(F1,C,F2,D,W)} :- iso_gen(F1,A,F2,B), colored(F1,A,C1), colored(F2,B,C2), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), colored(F1,C,C3), colored(F2,D,C4), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<(L2+lenght), L2<(L1+lenght), R1>ratio_max, R1<=ratio_fmax, R1<=(R2+ratio_high), R2<=(R1+ratio_high), W=weight_3.

{iso(F1,A,F2,B,W)} :- iso_gen(F1,A,F2,B), colored(F1,A,C1), colored(F2,B,C2), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), colored(F1,C,C3), colored(F2,D,C4), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<(L2+lenght), L2<(L1+lenght), R1>ratio_fmax, R1<=ratio_umax, R1<=(R2+ratio_fhigh), R2<=(R1+ratio_fhigh), W=weight_4.
{iso(F1,C,F2,D,W)} :- iso_gen(F1,A,F2,B), colored(F1,A,C1), colored(F2,B,C2), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), colored(F1,C,C3), colored(F2,D,C4), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<(L2+lenght), L2<(L1+lenght), R1>ratio_fmax, R1<=ratio_umax, R1<=(R2+ratio_fhigh), R2<=(R1+ratio_fhigh), W=weight_4.

{iso(F1,A,F2,B,W)} :- iso_gen(F1,A,F2,B), colored(F1,A,C1), colored(F2,B,C2), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), colored(F1,C,C3), colored(F2,D,C4), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<(L2+lenght), L2<(L1+lenght), R1>ratio_umax, R1<=(R2+ratio_uhigh), R2<=(R1+ratio_uhigh), W=weight_4.
{iso(F1,C,F2,D,W)} :- iso_gen(F1,A,F2,B), colored(F1,A,C1), colored(F2,B,C2), edge(F1,A,C,T1,L1,R1), edge(F2,B,D,T2,L2,R2), colored(F1,C,C3), colored(F2,D,C4), iso_gen(F1,C,F2,D), F1!=F2, T1==T2, L1<(L2+lenght), L2<(L1+lenght), R1>ratio_umax, R1<=(R2+ratio_uhigh), R2<=(R1+ratio_uhigh), W=weight_4.

:- iso_gen(F1,A,F2,B), iso_gen(F1,A,F2,C), B!=C, colored(_,A,_), colored(_,B,_), colored(_,C,_).
:- iso_gen(F1,B,F2,A), iso_gen(F1,C,F2,A), B!=C, colored(_,A,_), colored(_,B,_), colored(_,C,_).

:- iso(F1,A,F2,B,W), iso(F1,A,F2,C,W), B!=C, colored(_,A,_), colored(_,B,_), colored(_,C,_).
:- iso(F1,B,F2,A,W), iso(F1,C,F2,A,W), B!=C, colored(_,A,_), colored(_,B,_), colored(_,C,_).

:- iso(F1,A,F2,B,W1), iso(F1,A,F2,B,W2), W1!=W2.

weightiso(M) :- M = #sum{W,F1,A,F2,B : iso(F1,A,F2,B,W)}.
sizeiso(N) :- N = #count{F1,A,F2,B,W : iso(F1,A,F2,B,W)}.

#maximize {N : sizeiso(N)}.
#maximize {N : weightiso(N)}.

#show weightiso/1.
#show sizeiso/1.
#show iso/5.
