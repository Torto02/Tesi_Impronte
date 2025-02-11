import numpy as np
import json
from matplotlib.colors import ListedColormap
import matplotlib.pyplot as plt
import matplotlib as mpl
import seaborn as sns
import math
from scipy.spatial import distance
import scipy
import networkx as nx
import copy

#legge lo scheletro dal file prodotto sa SourceAFIS
def readSkelton(path, name_file):
    file_json = open(path + "/transparency_"+ name_file +".tif/042-ridges-thinned-skeleton.json",'r')
    data = json.load(file_json)
    #print(data['dimensions'])
    with open(path + "/transparency_"+ name_file +'.tif/043-ridges-thinned-skeleton.dat', mode='r') as f: # b is important -> binary
        d = np.fromfile(f,dtype=bool,count=data['dimensions'][0]*data['dimensions'][1]).reshape(data['dimensions'][0], data['dimensions'][1])
    d = d.astype(int)
    return d

#Elimina i bordi con rumore dello scheletro
def skelton_erosion(skelton_):
    skelton_eroded = copy.deepcopy(skelton_)
    struct1 = scipy.ndimage.generate_binary_structure(2, 1)
    skelton_eroded = scipy.ndimage.binary_dilation(skelton_eroded, structure = struct1, iterations = 20).astype(skelton_eroded.dtype)
    skelton_eroded = scipy.ndimage.binary_erosion(skelton_eroded, structure=np.ones((70,70))).astype(skelton_eroded.dtype)
    for i in range(0,skelton_.shape[0]):
        for j in range(0,skelton_.shape[1]):
            if skelton_[i,j] == 1 and skelton_eroded[i,j] == 0:
                skelton_[i,j] = 0
    return skelton_

def isMinutia(coord, minutiae_data):
    for minutia in minutiae_data:
        if minutia['x'] == coord[0] and  minutia['y'] == coord[1]:
            return True
    return False

def isEndMinutia(coord, minutiae_data):
    for minutia in minutiae_data:
        if minutia['x'] == coord[0] and  minutia['y'] == coord[1]:
            if minutia['type'] == 'ending' :
                return True
            else:
                return False
    return False

def isOutOfBound(coord, skelton):
    shape = skelton.shape
    #print(shape)
    if (coord[0] < 0 or coord[0] >= shape[0]) or (coord[1] < 0 or coord[1] >= shape[1]):
        return True
    return False


#Trova tutte le minuzie dello scheletro con il metodo del crossing number
def find_all_minutia(skelton):
    
    minutiae = []

    l_n = np.array([[-1,-1],[-1,0],[-1,1],[0,1],[1,1],[1,0],[1,-1],[0,-1]])
    shape = skelton.shape

    exceptions = [[0,1,1,1,0,0,0,0],
                  [0,0,0,1,1,1,0,0],
                  [0,0,0,0,0,1,1,1],
                  [1,1,0,0,0,0,0,1]]

    for x in range(0, shape[0]):
        for y in range(0, shape[1]):
            
            if skelton[x,y] == 1:

                neigh_score = []
                neigh = []
                
                for i in range(1,9):
                    i_mod = i%8
                    vi_1 = np.array([x,y]) + l_n[i-1]
                    vi_mod = np.array([x,y]) + l_n[i_mod]
                    neigh_score.append(abs(skelton[vi_1[0],vi_1[1]] - skelton[vi_mod[0],vi_mod[1]]))
                    neigh.append(skelton[vi_1[0],vi_1[1]])

                cn = sum(neigh_score)/2
                
                if cn == 1 and neigh:
                    isEndMinutia = True
                    for ex in exceptions:
                        temp = np.array(neigh) - np.array(ex)
                        if -1 not in temp:
                            isEndMinutia = False
                    if isEndMinutia:
                        minutiae.append({'x':x, 'y': y, 'type': 'ending'})
                else:
                    if cn > 2:
                        minutiae.append({'x':x, 'y': y, 'type': 'bifurcation'})
                    else:
                        isBifurcation = False
                        for ex in exceptions:
                            temp = np.array(neigh) - np.array(ex)
                            if -1 not in temp:
                                isBifurcation = True
                        if isBifurcation:
                            minutiae.append({'x':x, 'y': y, 'type': 'bifurcation'})
    
    return minutiae

#Trova le minuzie dello scheletro guardando il numero di vicini
def find_all_minutia_1(skelton):
    l_n = np.array([[-1,-1],[-1,0],[-1,1],[0,-1],[0,1],[1,-1],[1,0],[1,1]])
    list_bifurcation = [[1,0,1,0,0,1,0,0],
                        [1,0,1,0,0,0,1,0],
                        [1,0,1,0,0,0,0,1],
                        [1,0,0,0,1,0,1,0],
                        [1,0,0,0,1,1,0,0],
                        [1,0,0,0,0,1,0,1],
                        [0,1,0,1,0,0,0,1],
                        [0,1,0,1,1,0,0,0],
                        [0,1,0,1,0,0,1,0],
                        [0,1,0,0,1,0,1,0],
                        [0,1,0,0,1,1,0,0],
                        [0,1,0,0,0,1,0,1],
                        [0,0,1,1,0,0,1,0],
                        [0,0,1,1,0,0,0,1],
                        [0,0,1,0,0,1,0,1],
                        [0,0,0,1,1,0,1,0]]
    list_ending = [[0,0,0,0,0,1,1,0],
                   [0,0,0,0,0,0,1,1],
                   [0,0,0,0,1,0,0,1],
                   [0,0,1,0,1,0,0,0],
                   [0,1,1,0,0,0,0,0],
                   [1,1,0,0,0,0,0,0],
                   [1,0,0,1,0,0,0,0],
                   [0,0,0,1,0,1,0,0],
                   [1,1,1,0,0,0,0,0],
                   [0,0,1,0,1,0,0,1],
                   [0,0,0,0,0,1,1,1],
                   [1,0,0,1,0,1,0,0]]
    minutiae = []
    for x in range(0,skelton.shape[0]):
        for y in range(0,skelton.shape[1]):
            #print(x,y)
            if skelton[x][y] == 1:
                neigh = []
                for n in l_n:
                    temp_neigh = [x,y] + n
                    #print(temp_neigh)
                    if not isOutOfBound(temp_neigh, skelton):
                        if skelton[temp_neigh[0],temp_neigh[1]] == 1:
                            neigh.append(1)
                        else:
                            neigh.append(0)
                    #print(neigh)
                if sum(neigh) == 1 or neigh in list_ending:
                    minutiae.append({'x':x,'y':y,'type':'ending'})
                if sum(neigh) == 3:
                    if neigh in list_bifurcation:
                        minutiae.append({'x':x,'y':y,'type':'bifurcation'})
                if sum(neigh) > 3:

                    index_to_del = []
                    for n1 in range(0, len(neigh)):
                        if neigh[n1]:
                            for n2 in range(n1+1, len(neigh)):
                                if neigh[n2]:
                                    temp = np.array(l_n[n1]) - np.array(l_n[n2])
                                    if 0 in temp and (-1 in temp or 1 in temp):
                                        temp1 = np.array([0,0]) - np.array(l_n[n1])
                                        if 0 in temp1:
                                            index_to_del.append(n2)
                                        else:
                                            index_to_del.append(n1)
                    set_index_del = set(index_to_del)
                    for index in set_index_del:
                        neigh[index] = 0

                    if neigh in list_bifurcation or sum(neigh) > 3:
                        minutiae.append({'x':x,'y':y,'type':'bifurcation'})

    return minutiae

def get_neighbours(coord, visited_skelton, skelton, minutiae_data, pixels):
    l_n = np.array([[-1,-1],[-1,0],[-1,1],[0,-1],[0,1],[1,-1],[1,0],[1,1]])
    list_neig = []
    for n in l_n:
        c_n  = np.array(coord) + n
        if not isOutOfBound(c_n, skelton):
            c_n = c_n.tolist()
            if (visited_skelton[c_n[0],c_n[1]] == 0 and skelton[c_n[0],c_n[1]] == 1 and c_n not in pixels) or (isMinutia(c_n, minutiae_data) and c_n not in pixels): #and not isEndMinutia(c_n, minutiae_data)):
                if isMinutia(c_n, minutiae_data):
                    list_neig.insert(0,c_n)
                else:
                    list_neig.append(c_n)
    return list_neig

def ridge_exists(ridge, ridges):
    m_init = ridge['minutiae'][0]
    m_end = ridge['minutiae'][1]
    for r in ridges:
        if [m_init, m_end] == r['minutiae'] or [m_end, m_init] == r['minutiae']:
            set1 = {tuple(x) for x in ridge['pixels']}
            set2 = {tuple(x) for x in r['pixels']}
            if set1 == set2:
                return True
    return False

def find_ridges_minutiae(minutiae_data, skelton):
    ridges = []
    visited_skelton = np.zeros(skelton.shape).astype(int)
    edges_minutiae = []
    wrong_ridges_index = []

    for minutia in minutiae_data:
        coord_minutia = [minutia['x'], minutia['y']]
        #visited_skelton[coord_minutia[0],coord_minutia[1]] = 1
        neig_minutia = get_neighbours(coord_minutia,visited_skelton, skelton, minutiae_data, [])
        #print(neig_minutia)
        """ if isEndMinutia(coord_minutia, minutiae_data):
            if len(neig_minutia) > 1:
                for neig in neig_minutia:
                    temp_ = np.array(neig) - np.array(coord_minutia)
                    if 0 in temp_:
                        neig_minutia = [neig]
        else:  """
        if len(neig_minutia) > 0:
            index_to_del = []
            for n1 in range(0, len(neig_minutia)):
                if visited_skelton[neig_minutia[n1][0],neig_minutia[n1][1]] == 1:
                    if n1 not in index_to_del:
                        #print('1 del index',n1)
                        index_to_del.append(n1)
                for n2 in range(n1+1, len(neig_minutia)):
                    temp = np.array(neig_minutia[n1]) - np.array(neig_minutia[n2])
                    if 0 in temp and (-1 in temp or 1 in temp):
                        temp1 = np.array(coord_minutia) - np.array(neig_minutia[n1])
                        if 0 in temp1:
                            if n2 not in index_to_del:
                                #print('2 del index',n2)
                                index_to_del.append(n2)
                        else:
                            if n1 not in index_to_del:
                                #print('2 del index',n1)
                                index_to_del.append(n1)
            #set_index_del = set(index_to_del)
            index_to_del.sort()
            index_to_del.reverse()
            #print(index_to_del)
            for index in index_to_del:
                del neig_minutia[index]

        # set_neig = {tuple(x) for x in neig_minutia}
        # intersections = []
        # for n in neig_minutia:
        #     neig_neig = get_neighbours(n,visited_skelton, skelton, minutiae_data, [])
        #     set1 = {tuple(x) for x in neig_neig}
        #     intersec = set_neig.intersection(set1)
        #     intersec = [list(x) for x in intersec]
        #     intersections = intersections + intersec
        # for el in intersections:
        #     index = neig_minutia.index(el)
        #     del neig_minutia[index]

        #print(minutia)
        #print(neig_minutia)
        for neigh in neig_minutia:
            minutiae_ridge = []
            pixels = []
            neighbours = []
            minutiae_ridge.append(coord_minutia)
            pixels.append(coord_minutia)
            coord = neigh
            while len(minutiae_ridge) < 2:
                if isMinutia(coord, minutiae_data):
                    #visited_skelton[coord[0],coord[1]] = 1
                    # if len(minutiae_ridge) == 0:
                    #     l_neigh = get_neighbours(coord,visited_skelton,skelton,minutiae_data,pixels)
                    #     l_neigh.reverse()
                    #     neighbours.extend(l_neigh)
                    #         if len(neighbours) > 0:
                    #         minutiae_ridge.append(coord)
                    #         pixels.append(coord)
                    #         coord = neighbours[-1]
                    #         neighbours = neighbours[0:-1]
                    #     else:
                    #         minutiae_ridge.extend([[-1,-1],[-1,-1]])
                    # else:
                    minutiae_ridge.append(coord)
                    pixels.append(coord)
                else:
                    visited_skelton[coord[0],coord[1]] = 1
                    l_neigh = get_neighbours(coord,visited_skelton,skelton,minutiae_data,pixels+neig_minutia+neighbours)
                    l_neigh.reverse()

                    l_good = []
                    if len(l_neigh) > 1:
                        for ln in l_neigh:
                            temp_ = np.array(ln) - np.array(coord)
                            if 0 in temp_:
                                l_good.append(ln)
                    if len(l_good) > 0:
                        l_neigh = l_good

                    neighbours.extend(l_neigh)
                    if len(neighbours) > 0:
                        pixels.append(coord)
                        coord = neighbours[-1]
                        neighbours = neighbours[0:-1]
                    else:
                        #minutiae_ridge.append(coord)
                        pixels.append(coord)
                        minutiae_ridge.append([-1,-1])
            if len(pixels) > 1:
                if not ridge_exists({'pixels':pixels, 'minutiae': minutiae_ridge}, ridges):
                    if [-1,-1] in minutiae_ridge:
                        del minutiae_ridge[-1]
                        minutiae_ridge.append(pixels[-1])
                        minutiae_data.append({'x':pixels[-1][0], 'y':pixels[-1][1], 'type': 'bifurcation'})
                        """ print('ERROR')
                        print(minutiae_data.index(minutia))
                        print(len(minutiae_data)-1)
                        print(neig_minutia)
                        print(neigh) """
                        wrong_ridges_index.append(len(ridges))
                    ridges.append({'pixels':pixels, 'minutiae': minutiae_ridge})
                    edges_minutiae.append(minutiae_ridge)
        visited_skelton[minutia['x'],minutia['y']] = 1
    return [ridges, wrong_ridges_index]


def make_distances(ridges):
    for ridge in ridges:
        l_n = np.array([[-1,-1],[-1,0],[-1,1],[0,-1],[0,1],[1,-1],[1,0],[1,1]])
        #print(ridge)
        init_minutia = ridge['minutiae'][0]
        end_minutia = ridge['minutiae'][1]
        visited_ridge = np.zeros(len(ridge['pixels']))
        current_pixel =init_minutia.copy()
        distance_ = []
        #print(current_pixel != end_minutia)
        while current_pixel!= end_minutia:
            #print(current_pixel)
            ind = ridge['pixels'].index(current_pixel)
            visited_ridge[ind] = 1
            list_neigh = []
            for n in l_n:
                temp_neig = current_pixel + n
                temp_neig = temp_neig.tolist()
                if temp_neig in ridge['pixels']:
                    if temp_neig != end_minutia or sum(visited_ridge) == len(visited_ridge)-1:
                        temp_ind = ridge['pixels'].index(temp_neig)
                        if visited_ridge[temp_ind] == 0:
                            list_neigh.append(n)
            if len(list_neigh) > 1:
                good = False
                list_good = []
                for el in list_neigh:
                    if 0 in el:
                        list_good.append(el)
                if len(list_good) == 1:
                    distance_.append(1)
                    current_pixel = current_pixel + list_good[0]
                    good = True
                if not good:
                    print('!!!!!!!!!!!!!!!!!Problema!!!!!!!!!!!!!!!!!!!!!!!')
                    print(list_neigh)

            else:
                if 0 in list_neigh[0]:
                    distance_.append(1)
                else:
                    distance_.append(math.sqrt(2))
                current_pixel = current_pixel + list_neigh[0]
            current_pixel = current_pixel.tolist()
        distance_.append(distance_[-1])
        ridge['distance'] = sum(distance_)
        #print(sum(distance_))
    return


def indexRidge(coord_, ridges_, nodes_, edges_):
    if coord_ in nodes_:
        index_coord = nodes_.index(coord_)
        list_connection = []
        for i in range(0, len(edges_)):
            if index_coord in edges_[i]:
                list_connection.append(i)
        return list_connection
    for i in range(0,len(ridges_)):
        if coord_ in ridges_[i]['pixels']:
            return [i]
        
def make_ridge(init_minutia, end_minutia, ridge_, visited_ridge):
    l_n = np.array([[-1,-1],[-1,0],[-1,1],[0,-1],[0,1],[1,-1],[1,0],[1,1]])
    #visited_ridge = np.zeros(len(ridge_['pixels']))
    current_pixel =init_minutia.copy()
    distance_ = []
    pixels = []

    if init_minutia not in ridge_['pixels']:
        print('***********WRONG INIT***************')
    if end_minutia not in ridge_['pixels']:
        print('***********WRONG END***************')

    #print(current_pixel != end_minutia)
    while current_pixel!= end_minutia:
        """ if (current_pixel == init_minutia):
            print('init_minutia')
        else:
            print(current_pixel) """
        ind = ridge_['pixels'].index(current_pixel)
        visited_ridge[ind] = 1
        list_neigh = []
        for n in l_n:
            temp_neig = current_pixel + n
            temp_neig = temp_neig.tolist()
            if temp_neig in ridge_['pixels']:
                temp_ind = ridge_['pixels'].index(temp_neig)
                if visited_ridge[temp_ind] == 0:
                    list_neigh.append(n)
        if len(list_neigh) > 1:
            good = False
            list_good = []
            for el in list_neigh:
                if 0 in el:
                    list_good.append(el)
            if len(list_good) == 1:
                distance_.append(1)
                pixels.append(current_pixel)
                current_pixel = current_pixel + list_good[0]
                good = True
            if not good:
                print('!!!!!!!!!!!!!!!!!Problema!!!!!!!!!!!!!!!!!!!!!!!')
                print(list_good)
        else:
            if 0 in list_neigh[0]:
                distance_.append(1)
            else:
                distance_.append(math.sqrt(2))
            pixels.append(current_pixel)
            current_pixel = current_pixel + list_neigh[0]
        current_pixel = current_pixel.tolist()
    ind = ridge_['pixels'].index(current_pixel)
    visited_ridge[ind] = 1
    pixels.append(current_pixel)
    distance_.append(distance_[-1])
    return {'pixels': pixels, 'minutiae': [init_minutia, end_minutia], 'distance': sum(distance_)}

#Costruisce il grafo con i nodi e archi aggiunti
def find_internal_edges (ridges, minutiae_data, limit, skelton):
    nodes = []
    node_type = []
    for minutia in minutiae_data:
        nodes.append([minutia['x'],minutia['y']])
        node_type.append(minutia['type'])
    #print(nodes)
    #print(node_type)

    edges = []
    edges_type = []
    edges_distance = []
    for ridge in ridges:
        node_start = nodes.index(ridge['minutiae'][0])
        node_end = nodes.index(ridge['minutiae'][1])
        edges.append([node_start,node_end])
        edges_type.append('ridge')
        edges_distance.append(ridge['distance'])
    #print(edges)
    #print(edges_type)
    #print(edges_distance)

    new_nodes = copy.deepcopy(nodes)
    new_nodes_type = copy.deepcopy(node_type)
    new_edges = copy.deepcopy(edges)
    new_edges_type = copy.deepcopy(edges_type)
    new_distances = copy.deepcopy(edges_distance)
    new_ridges = copy.deepcopy(ridges)


    # COLLEGAMENTI --> LISTA
    # collegamenti[index] -> [{'coord': [1,2], 'type': 'minutia', 'coll' : {3,4,5}, 'distance': 12},
    #                         {'coord': [3,4], 'type': 'pixel', 'coll' : {2}, 'distance': 12},
    #                        ]

    wrong_pixels = []

    first = True
    for node in nodes:
        #if first:
            first = False
            collegamenti = []
            index_ridge_node = set(indexRidge(node, new_ridges, new_nodes, new_edges))
            #print('*****************************NODE ' + str(new_nodes.index(node)) + '***************************************')
            #print('Index ridge of current node '+str(new_nodes.index(node)))
            #print(index_ridge_node)
            #for l in range(1, limit+1):
            for i in range(-limit,limit+1):
                for j in range(-limit,limit+1):
                    if [i,j] != [0,0]:
                        coord = node - np.array([i,j])
                        coord = coord.tolist()
                        if not isOutOfBound(coord, skelton):
                            if skelton[coord[0], coord[1]] == 1:
                                #print(len(new_ridges))
                                list_index_ridge = indexRidge(coord, new_ridges, new_nodes, new_edges)
                                #print(list_index_ridge)
                                #print(coord)
                                index_ridge_coord = set(list_index_ridge)
                                #print('Index coord connections')
                                #print(index_ridge_coord)
                                intersec = index_ridge_coord.intersection(index_ridge_node)
                                #print('intersection node and coord')
                                #print(intersec)
                                if len(intersec) == 0: #or coord in new_nodes:
                                    #print('Index ridge of neighbours coord')
                                    #print(index_ridge_coord)
                                    new_dist = distance.euclidean(node,coord)

                                    index_intersection = []
                                    for index in range(0, len(collegamenti)):
                                        if len(index_ridge_coord.intersection(collegamenti[index]['coll'])) > 0:
                                            index_intersection.append(index)
                                    #print('Numero ridge già presenti nel collegamenti')
                                    #print(len(index_intersection))

                                    if coord in new_nodes:
                                        new_collegamento = {'coll': index_ridge_coord, 'coord': coord, 'type': 'minutia', 'distance': new_dist}
                                    else:
                                        new_collegamento = {'coll': index_ridge_coord, 'coord': coord, 'type': 'pixel', 'distance': new_dist}

                                    if len(index_intersection) > 0:
                                        for id_col  in index_intersection:
                                            #print('collegamento ' + str(id_col))
                                            #print(collegamenti[id_col])
                                            if collegamenti[id_col]['type'] == 'pixel':
                                                if new_collegamento['type'] == 'pixel':
                                                    if new_collegamento['distance'] > collegamenti[id_col]['distance']:
                                                        new_collegamento['coord'] = collegamenti[id_col]['coord']
                                                        new_collegamento['distance'] = collegamenti[id_col]['distance']
                                            else:
                                                if new_collegamento['type'] == 'minutia': 
                                                    if new_collegamento['distance'] > collegamenti[id_col]['distance']:
                                                        new_collegamento['coord'] = collegamenti[id_col]['coord']
                                                        new_collegamento['type'] = collegamenti[id_col]['type']
                                                        new_collegamento['distance'] = collegamenti[id_col]['distance']
                                                        new_collegamento['coll'] = new_collegamento['coll'].union(collegamenti[id_col]['coll'])
                                                    else:
                                                        new_collegamento['coll'] = new_collegamento['coll'].union(collegamenti[id_col]['coll'])
                                                else:
                                                    new_collegamento['coord'] = collegamenti[id_col]['coord']
                                                    new_collegamento['type'] = collegamenti[id_col]['type']
                                                    new_collegamento['distance'] = collegamenti[id_col]['distance']
                                                    new_collegamento['coll'] = new_collegamento['coll'].union(collegamenti[id_col]['coll'])
                                        
                                        index_intersection = list(index_intersection)
                                        index_intersection.sort()
                                        index_intersection.reverse()
                                        for id in index_intersection:
                                            del collegamenti[id]
                                        
                                        collegamenti.append(new_collegamento)
                                        #print('Collegamenti aggiornati')
                                        #print(collegamenti)

                                    else:
                                        #print('Aggiungo collegamento non presente')
                                        collegamenti.append(new_collegamento)
                                        #print('Collegamenti aggiornati')
                                        #print(collegamenti)

            #print('COLLEGAMENTI')
            #print(collegamenti)
            if len(collegamenti) > 0:

                for index_coll in range(0,len(collegamenti)):

                    el_coll = collegamenti[index_coll]
                    #c = el_coll['coll']

                    c = el_coll['coll'].pop()
                    el_coll['coll'].add(c)

                    if el_coll['type'] == 'pixel' and len(el_coll['coll']) > 1:
                        print('PROBLEMA PIXEL CON DUE COLLEGAMENTI')
                    
                    max_length = math.sqrt(2*8*8)

                    if el_coll['type'] == 'pixel':
                        init = new_ridges[c]['minutiae'][0]
                        end = new_ridges[c]['minutiae'][1]
                        dist_init = distance.euclidean(el_coll['coord'], init)
                        dist_end = distance.euclidean(el_coll['coord'], end)
                        if dist_init <= max_length or dist_end <= max_length:
                            #print('COLLEGAMENTO CAMBIATO')
                            if dist_init < dist_end:
                                collegamenti[index_coll] = {'coll':c, 'coord': init, 'type': 'minutia', 'distance': distance.euclidean(init,node)}
                            else:
                                collegamenti[index_coll] = {'coll':c, 'coord': end, 'type': 'minutia', 'distance': distance.euclidean(end,node)}
                    
                    el_coll = collegamenti[index_coll] 


                    if el_coll['type'] == 'pixel':
                        
                        
                        init_m = new_ridges[c]['minutiae'][0]
                        end_m = new_ridges[c]['minutiae'][1]

                        """ print('First added edge pixel')
                        print([new_nodes.index(init_m), len(new_nodes)])
                        print('pixels:')
                        print([init_m, el_coll['coord']])
                        print('Second added edge pixel')
                        print([len(new_nodes), new_nodes.index(end_m)])
                        print('pixels:')
                        print([el_coll['coord'], end_m]) """

                        visited_ridge = np.zeros(len(new_ridges[c]['pixels']))

                        ridge1 = make_ridge(init_m, el_coll['coord'], new_ridges[c], visited_ridge)
                        ridge2 = make_ridge(el_coll['coord'], end_m, new_ridges[c], visited_ridge)

                        if sum(visited_ridge) != len(visited_ridge):
                            print('PROBLEMA: DIVISIONE RIDGE SBAGLIATA')
                            ridges_pixels = np.array(new_ridges[c]['pixels'])
                            wrong_list = np.array(np.where(visited_ridge == 0)[0])
                            
                            return {'nodes': new_nodes, 'nodes_type': new_nodes_type, 'edges': new_edges, 'edges_type': new_edges_type, 'edges_distance': new_distances, 'wrong_pixels': ridges_pixels[wrong_list]}
                        
                        #print('sto eliminando ridge e edge numero ' + str(c))
                        del new_ridges[c]
                        del new_edges[c]
                        del new_edges_type[c]
                        del new_distances[c]

                        new_ridges.append(ridge1)
                        new_ridges.append(ridge2)
                        
                        #print('Numero ridge aggiornato')
                        #print(len(new_ridges))

                        new_nodes.append(el_coll['coord'])
                        new_nodes_type.append('added')

                        new_edges.append([new_nodes.index(init_m), len(new_nodes)-1])
                        new_edges_type.append('ridge')
                        new_distances.append(ridge1['distance'])


                        new_edges.append([len(new_nodes)-1, new_nodes.index(end_m)])
                        new_edges_type.append('ridge')
                        new_distances.append(ridge2['distance'])
                    
                        #print(new_edges[-2:])

                        new_edges.append([new_nodes.index(node), len(new_nodes)-1])
                        new_edges_type.append('added')
                        new_distances.append(el_coll['distance'])
                        new_ridges.append({'pixels': [], 'minutiae': [node,new_nodes[-1]], 'distance': el_coll['distance']})

                        for ind_coll in range(index_coll+1, len(collegamenti)):
                            list_add = []
                            for el in collegamenti[ind_coll]['coll']:
                                if el > c:
                                    list_add.append(el)
                            for el in list_add:
                                collegamenti[ind_coll]['coll'].remove(el)
                            for el in list_add:
                                collegamenti[ind_coll]['coll'].add(el-1)
                    else:
                        if [new_nodes.index(node), new_nodes.index(el_coll['coord'])] not in new_edges and [new_nodes.index(el_coll['coord']), new_nodes.index(node)] not in new_edges:
                            new_edges.append([new_nodes.index(node), new_nodes.index(el_coll['coord'])])
                            new_edges_type.append('added')
                            new_distances.append(el_coll['distance'])
                            new_ridges.append({'pixels': [], 'minutiae': [node,el_coll['coord']], 'distance': el_coll['distance']})

    return {'nodes': new_nodes, 'nodes_type': new_nodes_type, 'edges': new_edges, 'edges_type': new_edges_type, 'edges_distance': new_distances, 'wrong_pixels': wrong_pixels}


def find_border_minutiae(skelton_, nodes, nodes_type):
    
    skelton_eroded = copy.deepcopy(skelton_)

    struct1 = scipy.ndimage.generate_binary_structure(2, 1)
    skelton_eroded = scipy.ndimage.binary_dilation(skelton_eroded, structure = struct1, iterations = 20).astype(skelton_eroded.dtype)
    skelton_eroded = scipy.ndimage.binary_erosion(skelton_eroded, structure=np.ones((35,35))).astype(skelton_eroded.dtype)
    for i in range(0,skelton_.shape[0]):
        for j in range(0,skelton_.shape[1]):
            if skelton_[i,j] == 1 and skelton_eroded[i,j] == 0 and [i,j] in nodes:
                if nodes_type[nodes.index([i,j])] == 'ending':
                    nodes_type[nodes.index([i,j])] = 'border'
    return
    

def find_border_edges(nodes, nodes_type):
    border_edges = []

    index_border = []

    index_top = 0
    index_bottom = 0
    index_left = 0
    index_right = 0

    for ind_node in range(0, len(nodes)):
        if nodes_type[ind_node] == 'border':
            index_border.append(ind_node)
            if nodes[ind_node][0] < nodes[index_top][0] or (nodes[ind_node][0] == nodes[index_top][0] and nodes[ind_node][1] < nodes[index_top][1]):
                index_top = ind_node
            if nodes[ind_node][0] > nodes[index_bottom][0] or (nodes[ind_node][0] == nodes[index_bottom][0] and nodes[ind_node][1] > nodes[index_bottom][1]):
                index_bottom = ind_node
            if nodes[ind_node][1] < nodes[index_left][1] or (nodes[ind_node][1] == nodes[index_left][1] and nodes[ind_node][0] > nodes[index_left][0]):
                index_left = ind_node
            if nodes[ind_node][1] > nodes[index_right][1] or (nodes[ind_node][1] == nodes[index_right][1] and nodes[ind_node][0] < nodes[index_right][0]):
                index_right = ind_node

    index_border = np.array(index_border)

    center_x = (nodes[index_top][0] + nodes[index_bottom][0]) / 2
    center_y = (nodes[index_left][1] + nodes[index_right][1]) / 2

    """ plt.clf()
    plt.close()
    plt.figure().clear()

    new_graph = nx.Graph()

    border_nodes = []

    for ind_node in range(0,len(nodes)):
        if nodes_type[ind_node] == 'border':
            border_nodes.append(nodes[ind_node])

    color_map = []
    for i in range(0,len(border_nodes)):
            new_graph.add_node(i, pos=(border_nodes[i][1],border_nodes[i][0]))
            color_map.append('purple')
    new_graph.add_node('center', pos=(center_y, center_x))
    color_map.append('yellow')

    pos=nx.get_node_attributes(new_graph,'pos')

    for j in range(0, len(border_edges)-1):
        new_graph.add_edge(j,j+1)


    #print(graph[int(name)].size())
    #plt.figure(2)
    nx.draw(new_graph, pos, node_color=color_map, edge_color = 'r', with_labels=True, node_size = 1, width=0.4, font_size = 1)
    plt.savefig('graph_border_nodes_center.png',dpi=500)
    plt.clf() """


    radiants = []

    for index in index_border:
        dx = nodes[index][0] - center_x
        dy = nodes[index][1] - center_y
        radiants.append(math.atan2(dy, dx))

    radiants_index_sorted = np.argsort(radiants)
    sorted_border_nodes = index_border[radiants_index_sorted]

    for ind_node in range(0, len(sorted_border_nodes)-1):
        border_edges.append([sorted_border_nodes[ind_node], sorted_border_nodes[ind_node + 1]])
    border_edges.append([sorted_border_nodes[0], sorted_border_nodes[-1]])

    return border_edges

#Restituisce in output le minuzie e i ridge trovati
def generate_ridge_skelton(skelton):
    """ print('Stampa scheletro impronta')
    cmapmine = ListedColormap(['w', 'r'], N=2)
    plt.imshow(skelton, cmap=cmapmine, vmin=0, vmax=1)
    plt.savefig('skelton.png', dpi = 400) """

    skelton = skelton_erosion(skelton)

    """ print('Stampa scheletro impronta con erosione')
    plt.clf()
    plt.imshow(skelton, cmap=cmapmine, vmin=0, vmax=1)
    plt.savefig('eroded_skelton.png', dpi = 400) """

    print('Inizio costruzione ridges')
    #print(1)
    minutiae_data = find_all_minutia(skelton)
    ridges_data = find_ridges_minutiae(minutiae_data, skelton)
    """ skelton_modified = np.zeros(skelton.shape).astype(int)
    for ridge in ridges:
        #if len(ridge['pixels']) > 1:
            for pixel in ridge['pixels']:
                skelton_modified[pixel[0],pixel[1]] = 1 
    print(2)
    minutiae_data = []
    skelton = skelton_modified.copy()
    minutiae_data = find_all_minutia(skelton)
    ridges = find_ridges_minutiae(minutiae_data, skelton) """

    return {'minutiae_data':minutiae_data, 'ridges':ridges_data[0], 'wrong_ridges': ridges_data[1]}