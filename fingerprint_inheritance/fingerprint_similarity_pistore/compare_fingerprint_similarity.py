import isomorphismUtils as isoUtils
import json
import networkx as nx
from matplotlib.colors import ListedColormap
import matplotlib.pyplot as plt
import skeltonUtils
import os

print('Enter the path for skelton folders: ')
skelton_folder = str(input())

print('Enter the path for comparison folders: ')
comparison_folders = str(input())

print('Enter the path for graph folders: ')
folders_nodes = str(input())

folders_save_images = 'compare_fingerprint_isomorhish_images'

print('Enter name of fingerprint 1: ')
name1 = str(input())

print('Enter name of fingerprint 2: ')
name2 = str(input())

skelton1 = skeltonUtils.readSkelton(skelton_folder, name1)
skelton2 = skeltonUtils.readSkelton(skelton_folder, name2)

folder_comparison = comparison_folders + '/' +  name1 + '_' + name2
save_images = folders_save_images + '/' +  name1 + '_' + name2

file_center_node = folders_nodes + '/Center_minutiae.json'

file_json = open(file_center_node,'r')
data_center = json.load(file_json)
data_center = data_center['id_minutia_center']
#print(data_center)

if not os.path.exists(save_images):
    os.makedirs(save_images)

folders = os.listdir(folder_comparison)

folders.remove('data_matches.json')

#clusters = []

file_node1 = folders_nodes + '/' + name1 + '/nodes_graph.json' 
file_node2 = folders_nodes + '/' + name2 + '/nodes_graph.json' 

file_json = open(file_node1,'r')
data_nodes1 = json.load(file_json)
data_nodes1 = data_nodes1['nodes']

file_json = open(file_node2,'r')
data_nodes2 = json.load(file_json)
data_nodes2 = data_nodes2['nodes']

#print(data_nodes1)
#print(data_nodes2)

G1 = nx.Graph()

for i in range(0, len(data_nodes1)):
        G1.add_node(i, type = data_nodes1[i]['type'], pos = (data_nodes1[i]['coordinates'][1],data_nodes1[i]['coordinates'][0]))

G2 = nx.Graph()
for i in range(0, len(data_nodes2)):
        G2.add_node(i, type = data_nodes2[i]['type'], pos = (data_nodes2[i]['coordinates'][1],data_nodes2[i]['coordinates'][0]))

center1 = data_center[name1]
center2 = data_center[name2]

print(len(folders))
for n_file in range(0,len(folders)):
    name_file = str(n_file) + '.json'
    
    if name_file in folders:
        print(name_file)

        file_json = open(folder_comparison + '/' + name_file,'r')
        data_subgraphs = json.load(file_json)
        data_subgraphs = data_subgraphs['subgraphs']
        #print(data_subgraphs)

        node_sub_1 = data_subgraphs['subgraph_g1']['nodes']
        node_sub_2 = data_subgraphs['subgraph_g2']['nodes']
        #print(node_sub_1, node_sub_2)

        node_sub_1.append(center1)
        node_sub_2.append(center2)
        
        sub_g1 = G1.subgraph(node_sub_1)
        sub_g2 = G2.subgraph(node_sub_2)

        pos_g1 = nx.get_node_attributes(sub_g1, 'pos')
        pos_g2 = nx.get_node_attributes(sub_g2, 'pos')

        #print(sub_g1)
        #print(sub_g2)

        color_node_g1 = []
        color_node_g2 = []

        for node in sub_g1.nodes:
            if node == center1:
                 color_node_g1.append('yellow')
            else:
                if sub_g1.nodes[node]['type'] == 'bifurcation':
                    color_node_g1.append('blue')
                if sub_g1.nodes[node]['type'] == 'ending':
                    color_node_g1.append('green')
                if sub_g1.nodes[node]['type'] == 'border':
                    color_node_g1.append('purple')
                if sub_g1.nodes[node]['type'] == 'added':
                    color_node_g1.append('red')
        
        for node in sub_g2.nodes:
            if node == center2:
                 color_node_g2.append('yellow')
            else:
                if sub_g2.nodes[node]['type'] == 'bifurcation':
                    color_node_g2.append('blue')
                if sub_g2.nodes[node]['type'] == 'ending':
                    color_node_g2.append('green')
                if sub_g2.nodes[node]['type'] == 'border':
                    color_node_g2.append('purple')
                if sub_g2.nodes[node]['type'] == 'added':
                    color_node_g2.append('red')

        subax1 = plt.subplot(121)
        subax1.set_title(name1)
        cmapmine = ListedColormap(['w', 'black'], N=2)
        plt.imshow(skelton1, cmap=cmapmine, vmin=0, vmax=1)
        nx.draw(sub_g1, pos_g1, node_color=color_node_g1, with_labels=True, node_size = 1,font_size = 1)

        subax2 = plt.subplot(122)
        subax2.set_title(name2)
        plt.imshow(skelton2, cmap=cmapmine, vmin=0, vmax=1)
        nx.draw(sub_g2, pos_g2, node_color=color_node_g2, with_labels=True, node_size = 1, font_size = 1)
        plt.savefig(save_images + '/' + str(n_file),dpi=700)

        plt.clf()
        plt.close()
        