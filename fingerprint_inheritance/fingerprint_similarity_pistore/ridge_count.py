import numpy as np
import networkx as nx
import skeltonUtils
import json
import os

print('Enter the path of skelton folders: ')
path_folder_skelton = str(input())

print('Enter the path of minutiae folders: ')
folder_studies_path = str(input())

file_json = open(folder_studies_path +"/Center_minutiae.json",'r')
data_centers = json.load(file_json)
data_centers = data_centers['id_minutia_center']

save_path =  "ridge_count_files"

if not os.path.exists(save_path):
    os.makedirs(save_path)

name_fingerprint = open(path_folder_skelton + "/Names.txt", 'r').readlines()
n_f = len(name_fingerprint)

for i in range(0,n_f):
    name_fingerprint[i] = name_fingerprint[i][:-5]

for n_name in range(0,n_f):
    name = name_fingerprint[n_name]
    
    print(name)

    M = skeltonUtils.readSkelton(path_folder_skelton, name)

    file_json = open(folder_studies_path + "/" + name +"/nodes_graph.json",'r')
    data_minutiae = json.load(file_json)
    data_minutiae = data_minutiae['nodes']

    center = tuple(data_minutiae[data_centers[name]]['coordinates'])

    ridge_count = {}

    for id_minutia in range(0, len(data_minutiae)):

        arrive = tuple(data_minutiae[id_minutia]['coordinates'])
        center = np.array(center)
        arrive = np.array(arrive)
        path = [tuple(center.tolist())]
        activate = False
        count = 0



        n = 1000
        for t in range(1,n+1):
            x_new = round(center[0] + (t/n)*(arrive[0]-center[0]))
            y_new = round(center[1] + (t/n)*(arrive[1]-center[1]))
            
            if (x_new, y_new) not in path:
                coord_old = path[-1]

                if abs(x_new-coord_old[0]) == 1 and abs(y_new-coord_old[1]) and M[(x_new, y_new)] == 0 and M[coord_old] == 0:
                    if M[coord_old[0], y_new] == 1 and M[x_new, coord_old[1]] == 1:
                        if not activate:
                            activate = True
                            count = count + 1

                node = (x_new, y_new)
                path.append(node)
                if M[node] == 1 :
                    if not activate:
                        activate = True
                        count = count + 1 
                else:
                    if activate:
                        activate = False

                #print([x_new, y_new])
        
        #print(str(id_minutia) + ": " + str(count))
        ridge_count[id_minutia] = count
    
    with open(save_path + '/'+ name +'.json', 'w') as file_json_out:
        json.dump({'ridge_count': ridge_count},file_json_out)

