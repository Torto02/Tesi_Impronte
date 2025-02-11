import numpy as np
import bezier
import matplotlib.pyplot as plt
import json
import math
import sys
import time
from matplotlib.colors import ListedColormap
from scipy.spatial import distance
import pickle

#x = coordinate x del ridge
#y = coordinate y del ridge
#id1,id2 = identificatore del ridge
#start,end = indici di inzio e fine ridge dopo aver analizzato i cambi di direzione
def generateCurve(x,y,k,id1,id2,start,end):
	
	#genero la curva di Bezier dalle coordinate x,y dei punti del ridge
	#k è il numero di punti del ridge
	
	
	

	#while True:
	#start = 0
	#end = k
	print(f"Ridge {id1}-{id2} k:{k}")	
	time_stamp_init = time.time() 
	discreteCurve(id1,id2,x,y,start,end)
	time_stamp_end = time.time() 	
		
	print(f"***************** Timestamp discreteCurve: {time_stamp_end-time_stamp_init}")
	
	#print(f"Ridge {id1}-{id2} cp:{cp} count:{count}/{k}-> {count*100/k}%")
		
	
	
	
	
	
	
	
	#plot coordinate iniziali in rosso
	
	#plt.scatter(x,y,s=1,c='red',marker='s')
	#plt.scatter(x2,y2,s=1,c='blue',marker='s')
	#plt.scatter(x_c,y_c,s=10,c='green',marker='s')
	
	
	
def discreteCurve(id1,id2,x,y,start,end):
	
	
	
	
	match = 0.0 #percentuale di confronti corretti
	cp = 3 #numero minimo control points, parto da 3,4,5,6,7,8,9,10
	#controlli di fine ciclo alla fine del while
	while True:
		#diminuisco tempi di esecuzione, con più di 100 punti si arriva sempre a cp = 10 e poi si suddivide il ridge
		if end-start == 1:
			cp = 1
		if end-start == 2:
			cp = 2
		if end-start > 100:
			cp = 10
		
		x_c = np.empty(shape=0,dtype=int) #array di x dove inserisco le coordinate corrette
		y_c = np.empty(shape=0, dtype=int) #array di y dove inserisco le coordinate corrette
		dev_std = np.empty(shape=0)
		dist_euclidea = np.empty(shape=0)
		max_min_dist = 0
		index_max_min_dist = 0
		
		#creo x1 e y1 con cp punti presi da x e y in modo equidistante
		#esempio cp = 4 e k = 10 -> b = 0,3,6,9
		b = list(np.linspace(start, end-1, cp, dtype=int))
		
		#creo la curva
		nodes = np.asfortranarray([x[b],y[b]])
		curve = bezier.Curve(nodes, degree=cp-1)
		
		#print(f"Punti utilizzati per generare la curva: {nodes}")
		
		#valuto la curva in k punti per poi fare il confronto con le coordinate iniziali
		
		s_vals = np.linspace(0.0, 1.0, (end-start)*4)
		x2=curve.evaluate_multi(s_vals)[0]
		y2=curve.evaluate_multi(s_vals)[1]
		
		#print(f"Punti valutati dalla curva creata: {x2} {y2}")
		
		
		
		#valuto l'errore
		a = 0.4 #soglia errore
		i = start
		j = 0
		count = 0 #contatore confronti corretti
		index = 0
		
		time_stamp_init_calc_dist = time.time() 
		
		
		while i < end:
			j=0
			while j < len(x2):
							
				dist_euclidea = np.append(dist_euclidea,distance.euclidean([x2[j],y2[j]],[x[i],y[i]]))
				j += 1
				
			min_dist = min(dist_euclidea)
			index = np.argmin(dist_euclidea)
			
			if min_dist <= a:
				count = count + 1
			else:
				if min_dist > max_min_dist:
					max_min_dist = min_dist
					index_max_min_dist = i
				
			x_c = np.append(x_c,[round(x2[index])])
			y_c = np.append(y_c,[round(y2[index])])
			
			#print(f"index:{index} dist min:{min_dist} control point:{cp}")
			#print(f"x:{x2[index]} y:{y2[index]}")
			dist_euclidea = np.empty(shape=0)
			i += 1
		#print(f"ridge {id1}-{id2} cp:{cp} count:{count}/{k} -> {count*100/k}%")
		time_stamp_end_calc_dist = time.time() 
		print(f"***************** Timestamp calcolo distanze con {cp} cp su {end-start} punti: {time_stamp_end_calc_dist-time_stamp_init_calc_dist}")
		
		#calcolo match
		match = count*100/(end-start)
		#print(f"Ridge {id1}-{id2} da {start} a {end} con cp:{cp} match:{match}")
		#print(f"Ridge {id1}-{id2} cp:{cp} count:{count}/{k}-> {count*100/k}%")
		#print("Standard Deviation of arr is ", np.std(dev_std))
		if cp < 10:
			if cp+1 <= (end-start) and match <= 99.9:
				cp = cp + 1
			else:
				print(f"Ridge {id1}-{id2} cp:{cp} count:{count}/{(end-start)}-> {count*100/(end-start)}%")
				#plt.scatter(x,y,s=1,c='red',marker='s')
				#col = (np.random.random(), np.random.random(), np.random.random())
				#plt.scatter(x2,y2,s=30,c=[col],marker='s')
				#plt.scatter(x_c,y_c,s=10,c='green',marker='s')
				#print(x_c)
				#print(y_c)
				m[x_c,y_c] = 1
				pickle.dump(curve,f)
				pickle.dump((end-start),f)
				break
		else:
			if cp == 10 and match <= 99.9:
				print(f"Distanza peggiore: {max_min_dist} nel punto numero:{index_max_min_dist} -> {x[index_max_min_dist]},{y[index_max_min_dist]}")
				print(f"Spezzo Ridge in {x[index_max_min_dist]},{y[index_max_min_dist]}")
				discreteCurve(id1,id2,x,y,start,index_max_min_dist+1)
				discreteCurve(id1,id2,x,y,index_max_min_dist,end)
				break
			else:
				print(f"Ridge {id1}-{id2} cp:{cp} count:{count}/{(end-start)}-> {count*100/(end-start)}%")
				#plt.scatter(x,y,s=1,c='red',marker='s')
				#col = (np.random.random(), np.random.random(), np.random.random())
				#plt.scatter(x2,y2,s=30,c=[col],marker='s')
				#plt.scatter(x_c,y_c,s=10,c='green',marker='s')
				#print(x_c)
				#print(y_c)
				m[x_c,y_c] = 1
				pickle.dump(curve,f)
				pickle.dump((end-start),f)
				break
	
	
	
	#print(f"Ridge {id1}-{id2} da {start}-{end} cp:{cp} count:{count}/{k-start} -> {count*100/(k-start)}%")	
	#print(f"Ridge {id1}-{id2} cp:{cp} count:{count}/{k}-> {count*100/k}%")
	
	
def direction_lookup(destination_x, origin_x, destination_y, origin_y):

    deltaX = destination_x - origin_x

    deltaY = destination_y - origin_y

    degrees_temp = math.atan2(deltaX, deltaY)/math.pi*180

    if degrees_temp < 0:

        degrees_final = 360 + degrees_temp

    else:

        degrees_final = degrees_temp

    compass_brackets = ["N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"]

    compass_lookup = round(degrees_final / 45)

    return  degrees_final #compass_brackets[compass_lookup]#,
    
def genDir(x,y,k):
	i = 0
	direction=np.empty(shape=0, dtype=int)
	
	while i < k:
		if i > 0:
			direction = np.append(direction,[direction_lookup(x[i],x[i-1],y[i],y[i-1])])
		i = i + 1
		
	return direction	
	
def controlDir(i,k,direction,id1,id2):
	
	if direction.size == 0:
		return k
	direction[0] = direction[i]
	
	while i < k-1:
		if i > 0:
			if abs(direction[0] - direction[i]) == 180:
				#print("----Cambio di direzione -----")
				#print(f"Direzione iniziale: {direction[0]}")
				#print(f"Cambio direzione: {direction[i]}")
				#print("-----Spezzo ridge -----")
				return i+1
		i = i + 1
	return k


time_stamp_i = time.time() 

f=open("curve.txt","wb")	
data = json.load(open("../Sourceafis/transparency_012_3_2.tif/inheritance/ridges.json"))
ridges = len(data["ridges"])
# matrice finale m 480 righe e 504 colonne
m=np.zeros((480,504),dtype=int)
# matrice iniziale m 480 righe e 504 colonne
m_init=np.zeros((480,504),dtype=int)
#estraggo le coordinate dei punti per ogni ridge e chiamo la funzione generateCurve
i=0
k=0
j=0
while i < ridges:#ridges
	time_stamp_i_ridge = time.time()
	points = len(data["ridges"][i]["pixels"])
	while k < points:
		if k == 0:
			x = np.array([data["ridges"][i]["pixels"][k]["X"]])
			y = np.array([data["ridges"][i]["pixels"][k]["Y"]])
		else:
			x = np.r_[x,[data["ridges"][i]["pixels"][k]["X"]]]
			y = np.r_[y,[data["ridges"][i]["pixels"][k]["Y"]]]
		k=k+1
	print(f"---------Inizio ridge {data['ridges'][i]['ID1']}-{data['ridges'][i]['ID2']}------------")
	direction = genDir(x,y,k)
	index = controlDir(j,k,direction,data["ridges"][i]["ID1"],data["ridges"][i]["ID2"])
	print(f"Intervallo ridge: 0-{index}")
	if index < k:
		generateCurve(x,y,k,data["ridges"][i]["ID1"],data["ridges"][i]["ID2"],0,index+1)
	else:
		generateCurve(x,y,k,data["ridges"][i]["ID1"],data["ridges"][i]["ID2"],0,index)
	while index != k:
		indexApp = index
		index = controlDir(index,k,direction,data["ridges"][i]["ID1"],data["ridges"][i]["ID2"])
		print(f"Intervallo ridge: {indexApp}-{index}")
		if index < k:
			generateCurve(x,y,k,data["ridges"][i]["ID1"],data["ridges"][i]["ID2"],indexApp,index+1)
		else:
			generateCurve(x,y,k,data["ridges"][i]["ID1"],data["ridges"][i]["ID2"],indexApp,index)
	print(f"---------Fine ridge {data['ridges'][i]['ID1']}-{data['ridges'][i]['ID2']}------------")
	k=0
	m_init[x,y] = 1
	time_stamp_e_ridge = time.time() 
	print(f"***************** Timestamp ridge: {time_stamp_e_ridge-time_stamp_i_ridge}")
	i=i+1
	
time_stamp_e = time.time() 	
print(f"***************** Timestamp totale: {time_stamp_e-time_stamp_i}")	

m_diff = m-m_init

fx = open("matrice_finale.txt","w") 
for line in m:
    print ('  '.join(map(str, line)), file=fx)
fx.close()
fx = open("matrice_iniziale.txt","w") 
for line in m_init:
    print ('  '.join(map(str, line)), file=fx)
fx.close()
fx = open("matrice_differenza.txt","w") 
for line in m_diff:
    print ('  '.join(map(str, line)), file=fx)
fx.close()

# -1 rosso punto corretto matrice iniziale che non ha riscontro in finale
# 0 white corretto (anche punti fuori dall'impronta)
# 1 blu punto matrice finale non corretto rispetto a matr iniziale
cmapmine = ListedColormap(['r', 'w', 'b'], N=3)
ax1 = plt.imshow(m_diff, cmap=cmapmine, vmin=-1, vmax=1)
plt.savefig('FingerPrintOFDifference.png',dpi=600)
plt.show()
cmapmine = ListedColormap(['w', 'r'], N=2)
ax2 = plt.imshow(m_init, cmap=cmapmine, vmin=0, vmax=1)
plt.savefig('FingerPrintOriginal.png',dpi=600)
plt.show()
cmapmine = ListedColormap(['w', 'b'], N=2)
ax3 = plt.imshow(m, cmap=cmapmine, vmin=0, vmax=1)
plt.savefig('FingerPrintFinal.png',dpi=600)
plt.show()



#plt.xlim(0,500)
#plt.ylim(0,500)





#plt.savefig('figure.png')
sys.exit(0);
