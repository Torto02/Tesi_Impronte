import numpy as np
import bezier
import matplotlib.pyplot as plt
import json
import math
import sys
import shapely.geometry as geom
import time
from matplotlib.colors import ListedColormap
from sympy import *
from math import cos, sin, radians
from numpy import array, dot
from scipy.spatial import distance
from scipy.optimize import fmin_cobyla
from matplotlib import colors
import pickle

curve = bezier.Curve

s_vals=0
# matrice finale m 480 righe e 504 colonne
m=np.zeros((480,504),dtype=int)

def loadall(filename):
    i=0
    with open(filename, "rb") as f:
        while True:
            try:
                 curve = bezier.Curve.copy(pickle.load(f))
                 n_points = int(pickle.load(f))
                 d = curve.degree
                 cp = d+1
                 #print(f"degree:{d}")
                 #print(curve.nodes.shape)
                 s_vals = np.linspace(0.0, 1.0, n_points)
                 x=curve.evaluate_multi(s_vals)[0]
                 y=curve.evaluate_multi(s_vals)[1]
                 x = np.round(x,0)
                 x = x.astype(int)
                 y = np.round(y,0)
                 y = y.astype(int)
                 #print(x,y)
                 m[x,y]=1
                 #plt.plot(x,y)
                 #plt.show()
                 #return curve
                 #i = i+1
                 #print(i)
            except EOFError:
                break
                
f="curve.txt"

loadall(f)

fx = open("matrice_estratta.txt","w") 
for line in m:
    print ('  '.join(map(str, line)), file=fx)
fx.close()


count = 0
count1 = 0
tot = 0
tot_me = 0
# matrice iniziale m 480 righe e 504 colonne
m_init = np.zeros((480,504),dtype=int)
m_init = np.loadtxt('matrice_iniziale.txt',dtype=int)

# matrice generata m 480 righe e 504 colonne
m = np.zeros((480,504),dtype=int)
m = np.loadtxt('matrice_estratta.txt',dtype=int)

m_diff = m-m_init

for x in m_diff:
	for i in x:
		if i == -1:
			count = count + 1
		if i == 1:
			count1 = count1 + 1
    	
for x in m_init:
	for i in x:
		if i == 1:
			tot = tot + 1
			
for x in m:
	for i in x:
		if i == 1:
			tot_me = tot_me + 1

fx = open("ExtractionInfo.txt","w")
print(f"FingerPrintOriginal number of points: {tot}", file=fx)
print(f"FingerPrintExtracted number of points: {tot_me}", file=fx)
print(f"Punti in più matrice estratta: {tot_me - tot}", file=fx)
print(f"Punti posizionati male: {count}/{tot_me} -> {count*100/tot_me}%", file=fx)
fx.close()  

#m[0,0] = 1

cmapmine = ListedColormap(['w', 'b'], N=2)
ax2 = plt.imshow(m, cmap=cmapmine, vmin=0, vmax=1,extent=[0,504,480,0])
plt.savefig('FingerPrintExtracted.png', dpi=600)
plt.show()



		
sys.exit(0);
