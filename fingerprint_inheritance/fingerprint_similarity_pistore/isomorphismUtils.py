import json
import networkx as nx
from networkx.algorithms import tournament
import skeltonUtils
import os
import numpy as np
from scipy.spatial import distance
from matplotlib.colors import ListedColormap
import matplotlib.pyplot as plt

import math


#Costruisce il grafo con le prorpietà aggiunte con il centro di figura (ridge count e angolo)
def make_modified_graph(folder_studies_path, name, center_minutia):
    current_path = folder_studies_path + '/' + name
    
    file_json = open(current_path +"/nodes_graph.json",'r')
    data_nodes = json.load(file_json)
    data_nodes = data_nodes['nodes']


    file_json = open(current_path +"/edges_graph.json",'r')
    data_edges = json.load(file_json)
    data_edges = data_edges['edges']

    file_json = open(folder_studies_path + '/ridge_count_files/' + name + '.json','r')
    data_ridge_count = json.load(file_json)
    data_ridge_count = data_ridge_count['ridge_count']

    coord_center = data_nodes[center_minutia]['coordinates']

    G = nx.Graph()

    for i_node in range(0, len(data_nodes)):
        coord_node = data_nodes[i_node]['coordinates']
        dx = coord_node[0] - coord_center[0]
        dy = coord_node[1] - coord_center[1]
        angle = math.atan2(dy, dx)
        G.add_node(i_node, type = data_nodes[i_node]['type'], angle = angle, pos = [data_nodes[i_node]['coordinates'][1],data_nodes[i_node]['coordinates'][0]],  length = data_ridge_count[str(i_node)])

    for i_edge in range(0, len(data_edges)):
        G.add_edge(data_edges[i_edge]['nodes'][0], data_edges[i_edge]['nodes'][1], type = data_edges[i_edge]['type'])

    return G


#Costruisce il grafo senza ridge count
def build_graph_from_files(folder_path, name, minutia_center, weight_added, weight_ridges):
    
    file_json = open(folder_path + "/"+ name +"/nodes_graph.json",'r')
    data_nodes = json.load(file_json)
    data_nodes = data_nodes['nodes']

    file_json = open(folder_path + "/"+ name +"/edges_graph.json",'r')
    data_edges = json.load(file_json)

    data_edges = data_edges['edges']

    edges = []
    type_edges = []
    length_edges = []
    weight_edge = []
    
    type_nodes = []
    coord_nodes = []
    radiants_nodes = []

    for node in data_nodes:
        type_nodes.append(node['type'])
        coord_nodes.append(node['coordinates'])
        dx = node['coordinates'][0] - data_nodes[minutia_center]['coordinates'][0]
        dy = node['coordinates'][1] - data_nodes[minutia_center]['coordinates'][1]
        radiants_nodes.append(math.atan2(dy, dx))

    for edge in data_edges:
        edges.append(edge['nodes'])
        type_edges.append(edge['type'])
        length_edges.append(edge['distance'])
        if edge['type'] == 'border' or edge['type'] == 'added':
            weight_edge.append(weight_added)
        else:
            weight_edge.append(weight_ridges)
    
    relative_pos_nodes_sin = []
    relative_pos_nodes_cos = []
    coord_center = data_nodes[minutia_center]['coordinates']

    for coord_node in coord_nodes:
        dx = coord_node[0] - coord_center[0]
        dy = coord_node[1] - coord_center[1]
        radiants_nodes.append(math.atan2(dy, dx))
        if coord_node[0] <= coord_center[0]:
            relative_pos_nodes_sin.append(coord_center[0] - coord_node[0])
        else:
            relative_pos_nodes_sin.append(coord_node[0] - coord_center[0])
        
        if coord_node[1] <= coord_center[1]:
            relative_pos_nodes_cos.append(coord_center[1] - coord_node[1])
        else:
            relative_pos_nodes_cos.append(coord_node[1] - coord_center[1])

    G = nx.Graph()

    for edge_i in range(0, len(edges)):
        G.add_edge(edges[edge_i][0], edges[edge_i][1], type = type_edges[edge_i], weight = weight_edge[edge_i])

    for i in range(0, len(data_nodes)):
        G.add_node(i, type = type_nodes[i], pos = (coord_nodes[i][1],coord_nodes[i][0]), angle = radiants_nodes[i], relative_pos_sin = relative_pos_nodes_sin[i], relative_pos_cos = relative_pos_nodes_cos[i])

    for node in G.nodes:
        length = nx.shortest_path_length(G, source=minutia_center, target=node, weight='weight')
        nx.set_node_attributes(G, {node: length}, name="length")
        
    
    return G


def get_reachable_closer_node(G, node, minutia_center):
    closer_node = 0
    distance_closer_node = distance.euclidean(G.nodes[node]['pos'], G.nodes[closer_node]['pos'])
    for n in G.nodes:
        if n != node:
            distance_n = distance.euclidean(G.nodes[node]['pos'], G.nodes[n]['pos'])
            if distance_n < distance_closer_node and distance_n < 100:
                if nx.has_path(G, minutia_center, n):
                    distance_closer_node = distance_n
                    closer_node = n
    return closer_node


#Costruisce il grafo senza nodi e archi aggiunti
def build_simple_graph_from_files(folder_path, name, minutia_center, weight_added, weight_ridges):
    
    file_json = open(folder_path + "/"+ name +"/nodes_graph.json",'r')
    data_nodes = json.load(file_json)
    data_nodes = data_nodes['nodes']

    file_json = open(folder_path + "/"+ name +"/edges_graph.json",'r')
    data_edges = json.load(file_json)
    data_edges = data_edges['edges']

    edges = []
    type_edges = []
    length_edges = []
    weight_edge = []
    
    type_nodes = []
    coord_nodes = []
    radiants_nodes = []

    for node in data_nodes:
        type_nodes.append(node['type'])
        coord_nodes.append(node['coordinates'])
        dx = node['coordinates'][0] - data_nodes[minutia_center]['coordinates'][0]
        dy = node['coordinates'][1] - data_nodes[minutia_center]['coordinates'][1]
        radiants_nodes.append(math.atan2(dy, dx))

    for edge in data_edges:
        edges.append(edge['nodes'])
        type_edges.append(edge['type'])
        length_edges.append(edge['distance'])
        if edge['type'] == 'border' or edge['type'] == 'added':
            weight_edge.append(weight_added)
        else:
            weight_edge.append(weight_ridges)
    
    relative_pos_nodes_sin = []
    relative_pos_nodes_cos = []
    coord_center = data_nodes[minutia_center]['coordinates']

    for coord_node in coord_nodes:
        dx = coord_node[0] - coord_center[0]
        dy = coord_node[1] - coord_center[1]
        radiants_nodes.append(math.atan2(dy, dx))
        if coord_node[0] <= coord_center[0]:
            relative_pos_nodes_sin.append(coord_center[0] - coord_node[0])
        else:
            relative_pos_nodes_sin.append(coord_node[0] - coord_center[0])
        
        if coord_node[1] <= coord_center[1]:
            relative_pos_nodes_cos.append(coord_center[1] - coord_node[1])
        else:
            relative_pos_nodes_cos.append(coord_node[1] - coord_center[1])

    G_c = nx.Graph()

    for edge_i in range(0, len(edges)):
        G_c.add_edge(edges[edge_i][0], edges[edge_i][1], type = type_edges[edge_i], weight = weight_edge[edge_i])

    for i in range(0, len(data_nodes)):
        G_c.add_node(i, type = type_nodes[i], pos = (coord_nodes[i][1],coord_nodes[i][0]), angle = radiants_nodes[i], relative_pos_sin = relative_pos_nodes_sin[i], relative_pos_cos = relative_pos_nodes_cos[i])

    length_coord = {}
    for node in G_c.nodes:
        if nx.has_path(G_c, minutia_center, node):
            length = nx.shortest_path_length(G_c, source=minutia_center, target=node, weight='weight')
        else:
            closer_node = get_reachable_closer_node(G_c, node, minutia_center)
            length = nx.shortest_path_length(G_c, source=minutia_center, target=closer_node, weight='weight') + 1
        nx.set_node_attributes(G_c, {node: length}, name="length")
        length_coord[G_c.nodes[node]['pos']] = length


    file_json = open(folder_path + "/"+ name +"/minutiae.json",'r')
    data_minutiae = json.load(file_json)
    data_minutiae = data_minutiae['minutiae']
    #print(data_minutiae)

    file_json = open(folder_path + "/"+ name +"/ridge.json",'r')
    data_ridge = json.load(file_json)
    data_ridge = data_ridge['ridges']
    #print(data_ridge)

    coord_minutia = []
    type_minutia = []

    for minutia in data_minutiae:
        coord_minutia.append([minutia['x'], minutia['y']])
        type_minutia.append(minutia['type'])

    edges = []
    
    for ridge in data_ridge:
        edges.append([coord_minutia.index(ridge['minutiae'][0]), coord_minutia.index(ridge['minutiae'][1])])


    radiants_nodes_simple = []
    relative_pos_nodes_sin = []
    relative_pos_nodes_cos = []
    length_simple = []

    for coord_node in coord_minutia:
        dx = coord_node[0] - coord_center[0]
        dy = coord_node[1] - coord_center[1]
        radiants_nodes_simple.append(math.atan2(dy, dx))
        if (coord_node[0], coord_node[1]) in length_coord:
            length_simple.append(length_coord[(coord_node[0], coord_node[1])])
        else:
            length_simple.append(length_coord[(coord_node[1], coord_node[0])])
        if coord_node[0] <= coord_center[0]:
            relative_pos_nodes_sin.append(coord_center[0] - coord_node[0])
        else:
            relative_pos_nodes_sin.append(coord_node[0] - coord_center[0])
        
        if coord_node[1] <= coord_center[1]:
            relative_pos_nodes_cos.append(coord_center[1] - coord_node[1])
        else:
            relative_pos_nodes_cos.append(coord_node[1] - coord_center[1])
    
    G = nx.Graph()

    for edge_i in range(0, len(edges)):
        G.add_edge(edges[edge_i][0], edges[edge_i][1], type = 'ridge')

    for i in range(0, len(data_minutiae)):
        G.add_node(i, type = type_minutia[i], angle = radiants_nodes_simple[i], pos = (coord_minutia[i][1],coord_minutia[i][0]), relative_pos_sin = relative_pos_nodes_sin[i], relative_pos_cos = relative_pos_nodes_cos[i], length = length_simple[i])
    
    
    return G




def edges_from_nodes(G, node, visited_edges):
    edges_from_node = []
    angles_edges = []

    for edge in G.edges: 
        if node in edge: 

            node_arrive = edge[1 - edge.index(node)]

            if edge not in visited_edges:

                dx = G.nodes[node]['pos'][0] - G.nodes[node_arrive]['pos'][0]
                dy = G.nodes[node]['pos'][1] - G.nodes[node_arrive]['pos'][1]

                #print(str(node) + " --> " + str(node_arrive) + ' : ' + str(math.degrees(math.atan2(dy, dx))))
                angles_edges.append(math.atan2(dy, dx))
                edges_from_node.append(edge)
    
    return edges_from_node, angles_edges


def node_match(G1, G2, node_g1, node_g2, first, difference_ridge_count):

    #print('node_match(' + str(node_g1)  + ', ' + str(node_g2) + ')')

    angle_difference = abs(math.atan2(math.sin(G1.nodes[node_g1]['angle'] - G2.nodes[node_g2]['angle']), math.cos(G1.nodes[node_g1]['angle'] - G2.nodes[node_g2]['angle'])))
    length_difference = abs(G1.nodes[node_g1]['length'] - G2.nodes[node_g2]['length'])
    #relative_pos_difference_sin = abs(G1.nodes[node_g1]['relative_pos_sin'] - G2.nodes[node_g2]['relative_pos_sin'])
    #relative_pos_difference_cos = abs(G1.nodes[node_g1]['relative_pos_cos'] - G2.nodes[node_g2]['relative_pos_cos'])

    #return G1.nodes[node_g1]['type'] == G2.nodes[node_g2]['type'] and angle_difference <= math.radians(10) and relative_pos_difference_sin <= 5 and relative_pos_difference_cos <= 5
    #return angle_difference <= math.radians(10) and relative_pos_difference_sin <= 5 and relative_pos_difference_cos <= 5
    #return angle_difference <= math.radians(10) and length_difference <= 1 and G1.nodes[node_g1]['type'] == G2.nodes[node_g2]['type'] and relative_pos_difference_sin <= 20 and relative_pos_difference_cos <= 20

    max_angle_difference = math.radians(15)
    if length_difference >= 5:
        max_angle_difference = math.radians(5) 

    if first:
        return angle_difference <= max_angle_difference and length_difference <= difference_ridge_count and G1.nodes[node_g1]['type'] == G2.nodes[node_g2]['type'] 
    else:
        return angle_difference <= max_angle_difference and G1.nodes[node_g1]['type'] == G2.nodes[node_g2]['type'] 

def get_compatible_nodes(G1, G2, node_g2, difference_ridge_count):
    compatible_nodes = []
    for node_g1 in G1.nodes:

        if node_match(G1,G2, node_g1, node_g2, True, difference_ridge_count):
            compatible_nodes.append(node_g1)

    return compatible_nodes


#Trova gli isomorfismi partendo da due nodi dei due grafi
def find_equal_path(G1, G2, node_g1, node_g2, visited_edges_g1, visited_edges_g2, difference_ridge_count):
    
    #print(visited_edges_g1)
    #print('node_g2: ' + str(node_g2) + ", node_g1: " + str(node_g1))
    
    #if node_g1 not in visited_nodes_g1 and node_g2 not in visited_nodes_g2:
    
    edges_from_node_g2, angles_edges_g2 = edges_from_nodes(G2, node_g2, visited_edges_g2)

    edges_from_node_g1, angles_edges_g1 = edges_from_nodes(G1, node_g1, visited_edges_g1)



    if len(edges_from_node_g1) == 0 or len(edges_from_node_g2) == 0:
        #print(node_g1)
        #print(node_g2)

        sub_g1 = nx.Graph()
        sub_g2 = nx.Graph()
        
        sub_g1.add_node(node_g1, type = G1.nodes[node_g1]['type'], pos = G1.nodes[node_g1]['pos'])
        sub_g2.add_node(node_g2, type = G2.nodes[node_g2]['type'], pos = G2.nodes[node_g2]['pos'])
        
        #print([sub_g1.edges, sub_g2.edges])
        return [[sub_g1, sub_g2]], [{}]

    #compatible_edges_g2 = {}
    compatible_graphs = []
    matches_edges = []
    for ind_edge_g2 in range(0, len(edges_from_node_g2)):
        #print('edge_g2:')
        #print(edges_from_node_g2[ind_edge_g2])
        for ind_edge_g1 in range(0, len(edges_from_node_g1)):
            
            #print('edge_g1:')
            #print(edges_from_node_g1[ind_edge_g1])

            type_edge_g1 = G1.edges[edges_from_node_g1[ind_edge_g1]]['type']
            type_edge_g2 = G2.edges[edges_from_node_g2[ind_edge_g2]]['type']

            angle_difference = abs(math.atan2(math.sin(angles_edges_g1[ind_edge_g1] - angles_edges_g2[ind_edge_g2]), math.cos(angles_edges_g1[ind_edge_g1] - angles_edges_g2[ind_edge_g2])))
            node_arrive_g2 = edges_from_node_g2[ind_edge_g2][1 - edges_from_node_g2[ind_edge_g2].index(node_g2)]
            node_arrive_g1 = edges_from_node_g1[ind_edge_g1][1 - edges_from_node_g1[ind_edge_g1].index(node_g1)]
            
            edges_already_matched = False

            for couple_match in matches_edges:
                if edges_from_node_g2[ind_edge_g2] in couple_match:
                    if couple_match[edges_from_node_g2[ind_edge_g2]] == edges_from_node_g1[ind_edge_g1]:
                        edges_already_matched = True

            #print(node_arrive_g2)
            #print(node_arrive_g1)

            #print('node_match(' + str(node_arrive_g1)  + ', ' + str(node_arrive_g2) + ')')
            #if type_edge_g1 == type_edge_g2 and type_edge_g1!= 'border' and type_edge_g2!= 'border' and angle_difference <= math.radians(10) and node_match(G1, G2, node_arrive_g1, node_arrive_g2, False, difference_ridge_count) and not edges_already_matched:
            if type_edge_g1 == type_edge_g2 and angle_difference <= math.radians(10) and node_match(G1, G2, node_arrive_g1, node_arrive_g2, False, difference_ridge_count) and not edges_already_matched:
                #print('compatibles:')
                #print(edges_from_node_g1[ind_edge_g1])
                #print(edges_from_node_g2[ind_edge_g2])
                #print('Node arrive g2: ' + str(node_arrive_g2))
                #print('Node arrive g1: ' + str(node_arrive_g1))
            
                
                #print(visited_nodes_g1)
                #print(visited_nodes_g2)
                
                visited_edges_g1.add(edges_from_node_g1[ind_edge_g1])
                visited_edges_g2.add(edges_from_node_g2[ind_edge_g2])
                visited_edges_g1.add((edges_from_node_g1[ind_edge_g1][1],edges_from_node_g1[ind_edge_g1][0]))
                visited_edges_g2.add((edges_from_node_g2[ind_edge_g2][1],edges_from_node_g2[ind_edge_g2][0]))

                #print('Visited_edges_g1:')
                #print(visited_edges_g1)
                #print('Visited_edges_g2:')
                #print(visited_edges_g2)
                

                graphs_couples, couples_edge_match = find_equal_path(G1, G2, node_arrive_g1, node_arrive_g2, visited_edges_g1.copy(), visited_edges_g2.copy(), difference_ridge_count)
                            
                couples_edge_match_copy = [couples_edge_match[0].copy()]
                graphs_couples_copy = [graphs_couples[0].copy()]


                for couple in couples_edge_match_copy:
                    index_couple = couples_edge_match_copy.index(couple)
                    for j in range(0, len(couples_edge_match)):

                        #print(couples_edge_match_copy)
                        
                        edge_g2_i = set(couple.keys())
                        edge_g1_i = set(couple.values())

                        edge_g2_j = set(couples_edge_match[j].keys())
                        edge_g1_j = set(couples_edge_match[j].values())

                        if len(edge_g2_i.intersection(edge_g2_j)) == 0 and len(edge_g1_i.intersection(edge_g1_j)) == 0:
                            couple.update(couples_edge_match[j])
                            graphs_couples_copy[index_couple][0] = nx.compose(graphs_couples_copy[index_couple][0], graphs_couples[j][0])
                            graphs_couples_copy[index_couple][1] = nx.compose(graphs_couples_copy[index_couple][1], graphs_couples[j][1])

                        else:
                            
                            already_in_couple = False
                            for couple_ in couples_edge_match_copy:
                                in_current_couple = True
                                for key in couples_edge_match[j]:
                                    if key in couple_:
                                        if couple_[key] == couples_edge_match[j][key]:
                                            in_current_couple = in_current_couple and True
                                            break
                                        else:
                                            in_current_couple = in_current_couple and False
                                    else:
                                        in_current_couple = in_current_couple and False
                                        break

                                already_in_couple = already_in_couple or in_current_couple
                            if not already_in_couple:
                                couples_edge_match_copy.append(couples_edge_match[j])
                                graphs_couples_copy.append(graphs_couples_copy[index_couple])

                couples_edge_match = couples_edge_match_copy.copy()
                graphs_couples = graphs_couples_copy.copy()

                for graph_couple in graphs_couples:
                    graph_couple[0].add_node(node_g1, type = G1.nodes[node_g1]['type'], pos = G1.nodes[node_g1]['pos'])
                    graph_couple[0].add_edge(edges_from_node_g1[ind_edge_g1][0], edges_from_node_g1[ind_edge_g1][1], type = type_edge_g1)
                    graph_couple[1].add_node(node_g2, type = G2.nodes[node_g2]['type'], pos = G2.nodes[node_g2]['pos'])
                    graph_couple[1].add_edge(edges_from_node_g2[ind_edge_g2][0], edges_from_node_g2[ind_edge_g2][1], type = type_edge_g2)
                    compatible_graphs.append(graph_couple)
                
                for couple_match in couples_edge_match:
                    couple_match[edges_from_node_g2[ind_edge_g2]] = edges_from_node_g1[ind_edge_g1]

                    matches_edges.append(couple_match)

                    #print(graph_couple[0].edges, graph_couple[1].edges)
                #compatible_edges_g1.append({edges_from_node_g1[ind_edge_g1]: compatibles})
                #print(len(graphs_couples))
        #compatible_edges_g2[edges_from_node_g2[ind_edge_g2]] = compatible_edges_g1
    #return {node_g1 : compatible_edges_g2}
    if len(compatible_graphs) == 0:
        sub_g1 = nx.Graph()
        sub_g2 = nx.Graph()
        
        sub_g1.add_node(node_g1, type = G1.nodes[node_g1]['type'], pos = G1.nodes[node_g1]['pos'])
        sub_g2.add_node(node_g2, type = G2.nodes[node_g2]['type'], pos = G2.nodes[node_g2]['pos'])

        compatible_graphs.append([sub_g1,sub_g2])
        matches_edges.append({})

    #print(len(matches_edges))
    return compatible_graphs, matches_edges



def find_all_isomorphism(G1, G2, difference_ridge_count):
    type_node_not_consider = ['border', 'added']
    visited_g1 = set()
    visited_g2 = set()
    couples = []
    for node_g2 in G2.nodes:
        if G2.nodes[node_g2]['type'] not in type_node_not_consider:
            #print('NODE G2 ' + str(node_g2))
            compatible_nodes = get_compatible_nodes(G1, G2, node_g2, difference_ridge_count)
            #print('Compatible_nodes')
            #print(compatible_nodes)
            #print(compatible_nodes)
            for node_g1 in compatible_nodes:
                #print('Visited edges G1 e G2')
                #print(visited_g1)
                #print(visited_g2)

                #print('         NODE COMPATIBLE G1 ' + str(node_g1))
                graphs_couples, edges_matches = find_equal_path(G1, G2, node_g1, node_g2, visited_g1.copy(), visited_g2.copy(), difference_ridge_count)
                for graph_couple_index in range(0, len(graphs_couples)):
                    if len(graphs_couples[graph_couple_index][0].edges) > 0 or len(graphs_couples[graph_couple_index][1].edges) > 0:
                        couples.append(graphs_couples[graph_couple_index])
                        #print([graphs_couples[graph_couple_index][0].edges, graphs_couples[graph_couple_index][1].edges])
                        #print(edges_matches[graph_couple_index])
                        for edge_g1 in graphs_couples[graph_couple_index][0].edges:
                            visited_g1.add(edge_g1)
                            visited_g1.add((edge_g1[1],edge_g1[0]))
                        for edge_g2 in graphs_couples[graph_couple_index][1].edges:
                            visited_g2.add(edge_g2)
                            visited_g2.add((edge_g2[1],edge_g2[0]))
    return edges_matches, couples


#Stampa gli isomorfismi sul grafo, senza lo scheletro
def print_all_graphs_W_S(G1, G2, name1, name2, path_save_images, couples, data_centers):
    if not os.path.exists(path_save_images):
        os.makedirs(path_save_images)

    path_current_images = path_save_images + '/' + name1 + '_' + name2

    if not os.path.exists(path_current_images):
        os.makedirs(path_current_images)

    subgraphs_iso_couples = couples
    name_image = 0

    for couple_subgraph in subgraphs_iso_couples:
        
        print('Stampa immagine per coppia ' + str(name_image) + '/' + str(len(subgraphs_iso_couples)-1))

        G1_copy = G1.copy()
        G2_copy = G2.copy()

        sub_g1 = couple_subgraph[0]
        sub_g2 = couple_subgraph[1]
        
        list_nodes_g1 = list(sub_g1.nodes)
        list_nodes_g2 = list(sub_g2.nodes)
        list_edges_g1 = list(sub_g1.edges)
        list_edges_g2 = list(sub_g2.edges)

        #print(types_node_sub_g1)

        color_node_g1 = []
        color_node_g2 = []
        color_edge_g1 = []
        color_edge_g2 = []

        node_size_g1 = []
        node_size_g2 = []
        edge_size_g1 = []
        edge_size_g2 = []
        
        #print(types_node_sub_g1)
        for node in G1_copy.nodes:
            if node in list_nodes_g1:
                if G1_copy.nodes[node]['type'] == 'bifurcation':
                    color_node_g1.append('blue')
                if G1_copy.nodes[node]['type'] == 'ending':
                    color_node_g1.append('green')
                if G1_copy.nodes[node]['type'] == 'border':
                    color_node_g1.append('purple')
                if G1_copy.nodes[node]['type'] == 'added':
                    color_node_g1.append('red')
                node_size_g1.append(3)
            else:
                if node == data_centers[name1]:
                    color_node_g1.append('yellow')
                    node_size_g1.append(3)
                else:
                    color_node_g1.append('gray')
                    node_size_g1.append(0)
        
        #print(types_node_sub_g2)
        for node in G2_copy.nodes:
            if node in list_nodes_g2:
                if G2_copy.nodes[node]['type'] == 'bifurcation':
                    color_node_g2.append('blue')
                if G2_copy.nodes[node]['type'] == 'ending':
                    color_node_g2.append('green')
                if G2_copy.nodes[node]['type'] == 'border':
                    color_node_g2.append('purple')
                if G2_copy.nodes[node]['type'] == 'added':
                    color_node_g2.append('red')
                node_size_g2.append(3)
            else:
                if node == data_centers[name2]:
                    color_node_g2.append('yellow')
                    node_size_g2.append(3)
                else:
                    color_node_g2.append('gray')
                    node_size_g2.append(0)
        
        #print(types_edge_sub_g1)
        #print(G1_copy.edges)
        #print(list_edges_g1)
        for edge in G1_copy.edges:
            if edge in list_edges_g1 or (edge[1], edge[0]) in list_edges_g1:
                if G1_copy.edges[edge]['type'] == 'ridge':
                    color_edge_g1.append('blue')
                if G1_copy.edges[edge]['type'] == 'border':
                    color_edge_g1.append('purple')
                if G1_copy.edges[edge]['type'] == 'added':
                    color_edge_g1.append('red')
                edge_size_g1.append(1)
            else:
                color_edge_g1.append('gray')
                edge_size_g1.append(0.4)
        
        #print(G2.edges)
        for edge in G2_copy.edges:
            if edge in list_edges_g2 or (edge[1], edge[0]) in list_edges_g2:
                if G2_copy.edges[edge]['type'] == 'ridge':
                    color_edge_g2.append('blue')
                if G2_copy.edges[edge]['type'] == 'border':
                    color_edge_g2.append('purple')
                if G2_copy.edges[edge]['type'] == 'added':
                    color_edge_g2.append('red')
                edge_size_g2.append(1)
            else:
                color_edge_g2.append('gray')
                edge_size_g2.append(0.4)
        
        pos_g1 = nx.get_node_attributes(G1_copy, 'pos')
        pos_g2 = nx.get_node_attributes(G2_copy, 'pos')
        
        subax1 = plt.subplot(121)
        nx.draw(G1_copy, pos_g1, node_color=color_node_g1, edge_color = color_edge_g1,with_labels=True, node_size = node_size_g1, width=edge_size_g1, font_size = 1)

        subax2 = plt.subplot(122)
        nx.draw(G2_copy, pos_g2, node_color=color_node_g2, edge_color = color_edge_g2, with_labels=True, node_size = node_size_g2, width=edge_size_g2, font_size = 1)
        plt.savefig(path_current_images + '/' + str(name_image),dpi=400)

        plt.clf()
        subgraphs_matching_data = {'subgraphs':
                                {'subgraph_g1':{'edges': list(couple_subgraph[0].edges), 
                                                'nodes': list(couple_subgraph[0].nodes),
                                                'edges_type': list(nx.get_edge_attributes(couple_subgraph[0], 'type').values()),
                                                'edges_angle': list(nx.get_edge_attributes(couple_subgraph[0], 'angle').values()),
                                                'node_type': list(nx.get_node_attributes(couple_subgraph[0], 'type').values()),
                                                'node_angle': list(nx.get_node_attributes(couple_subgraph[0], 'angle').values())}, 
                                    'subgraph_g2':{'edges': list(couple_subgraph[1].edges), 
                                                'nodes': list(couple_subgraph[1].nodes),
                                                'edges_type': list(nx.get_edge_attributes(couple_subgraph[1], 'type').values()),
                                                'edges_angle': list(nx.get_edge_attributes(couple_subgraph[1], 'angle').values()),
                                                'node_type': list(nx.get_node_attributes(couple_subgraph[1], 'type').values()),
                                                'node_angle': list(nx.get_node_attributes(couple_subgraph[1], 'angle').values())}}}
        print(subgraphs_matching_data)
        with open(path_current_images + '/'+ str(name_image) +'.json', 'w') as file_json_out:
            json.dump(subgraphs_matching_data,file_json_out)

        name_image = name_image + 1

    return
