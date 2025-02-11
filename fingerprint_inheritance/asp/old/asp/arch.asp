ark(G,I,J) :- node(G,edge,I,_,_),node(G,edge,J,_,_), edge(G,I,J,edge,_,_), I != J+1, J != I+1.
ark(G,I,J) :- node(G,edge,I,_,_),node(G,internal,K,_,_), edge(G,I,K,internal,_,_), node(G,edge,J,_,_), edge(G,K,J,internal,_,_).
ark(G,I,J) :- ark(G,J,I).

arch(G,I,J) :- ark(G,I,J), I > J.

noarch(G,E) :- node(G,edge,E,_,_), not arch(G,E,_).
noarch(G,K) :- node(G,internal,K,_,_), arch(G,I,J), not edge(G,I,K,internal,_,_), not edge(G,K,J,internal,_,_).

%#show noarch/2.
%#show arch/3.

multiark(G,I1,I2,N) :- arch(G,I1,J1), arch(G,I2,J2), I1>I2, J2>J1, I1+J1+3 > I2+J2, I2+J2+3 > I1+J1, N = 2.
multiark(G,I1,In,N) :- arch(G,I1,J1), arch(G,I2,J2), multiark(G,I2,In,N1), I1>I2, J2>J1, I1+J1+3 > I2+J2, I2+J2+3 > I1+J1, N = N1+1.

multiarch(G,I1,In1,N1) :- multiark(G,I1,In1,N1),  multiark(G,I2,In2,N2), N1>N2.

%#show multiarch/4.

colours(1..100). 
1 {cluster(G,I,C) :colours(C)} 1 :- node(G,edge,I,_,_), not noarch(G,I).
:- cluster(G,A,C1), cluster(G,B,C2), multiarch(G,A,B,_), colours(C1), colours(C2), C1 != C2.

colored(G,I,C) :- cluster(G,I,C).
colored(G,E,C) :- noarch(G,E), C = E+100.

%#show colored/3.

coloredge(G,I,J,C) :-  edge(G,I,J,_,_,_), colored(G,I,C1), colored(G,J,C2), C1 == C2, C = C1.
coloredge(G,I,J,C) :- edge(G,I,J,_,_,_), colored(G,I,C1), colored(G,J,C2), C1 != C2, C = 0.

#show coloredge/4.