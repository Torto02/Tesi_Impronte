node(F1,T1,A) :- node(F1,T1,A,_,_).
edge(F1,A,C,T1) :- edge(F1,A,C,T1,_,_,_).

arch_gen(F1,I,J) :- node(F1,edge,I), node(F1,edge,J), edge(F1,I,J,edge), I!=J, I!=J+1, J!=I+1.
arch_gen(F1,I,J) :- node(F1,edge,I), node(F1,internal,K), node(F1,edge,J), edge(F1,I,K,internal), edge(F1,K,J,internal), I!=K, K!=J, I!=J.

arch(F1,I,J) :- arch_gen(F1,I,J), I>J.
arch(F1,I,J) :- arch_gen(F1,J,I), I>J.

noarch(F1,I) :- node(F1,edge,I), not arch(F1,I,_).
noarch(F1,K) :- node(F1,internal,K), arch(F1,I,J), not edge(F1,I,K,internal), not edge(F1,K,J,internal).

multiarch_gen(F1,I1,I2,N) :- arch(F1,I1,J1), arch(F1,I2,J2), I1>I2, J2>J1, I1+J1+3>I2+J2, I2+J2+3>I1+J1, N=2.
multiarch_gen(F1,I1,IN,N) :- arch(F1,I1,J1), arch(F1,I2,J2), multiarch_gen(F1,I2,IN,N1), I1>I2, J2>J1, I1+J1+3>I2+J2, I2+J2+3>I1+J1, N=N1+1.

multiarch(F1,I1,IN1,N1) :- multiarch_gen(F1,I1,IN1,N1), multiarch_gen(F1,I2,IN2,N2), N1>N2.

colours(1..100).
1 {cluster(F1,I,C) : colours(C)} 1 :- node(F1,edge,I), not noarch(F1,I).
:- cluster(F1,I,C1), cluster(F1,J,C2), multiarch(F1,I,J,_), colours(C1), colours(C2), C1!=C2.

colored(F1,I,C) :- cluster(F1,I,C).
colored(F1,I,C) :- noarch(F1,I), C=I+100.

coloredge(F1,I,J,C) :- edge(F1,I,J,_), colored(F1,I,C1), colored(F1,J,C2), C1==C2, C=C1.
coloredge(F1,I,J,C) :- edge(F1,I,J,_), colored(F1,I,C1), colored(F1,J,C2), C1!=C2, C=0.

#show colored/3.
sizecolor(N) :- N = #count{F1,A,B : colored(F1,A,B)}.
#minimize {N : sizecolor(N)}.
#show sizecolor/1.
