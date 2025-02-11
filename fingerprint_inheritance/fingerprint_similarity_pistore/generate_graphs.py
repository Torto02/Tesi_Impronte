import numpy as np
import json
import matplotlib
matplotlib.use('Agg')
from matplotlib.colors import ListedColormap
import matplotlib.pyplot as plt
from scipy.spatial import distance
import networkx as nx
import skeltonUtils
import os



print('Enter the path of fingerprint data folders: ')
folder_path = str(input())

save_path = folder_path + "_studies"

if not os.path.exists(save_path):
    os.makedirs(save_path)

#name_fingerprint = open("fingerprint_images/Names.txt", 'r').readlines()
name_fingerprint = open(folder_path + "/Names.txt", 'r').readlines()
n_f = len(name_fingerprint)

for i in range(0,n_f):
    name_fingerprint[i] = name_fingerprint[i][:-5]

for n_name in range(0,n_f):

    print('*************** FINGERPRINT ' + name_fingerprint[n_name] + ' ********************')
    name_folder = save_path + "/" + name_fingerprint[n_name]

    if not os.path.exists(name_folder):
        os.makedirs(name_folder)

    skelton = skeltonUtils.readSkelton(folder_path,name_fingerprint[n_name])
    """ 
    print('Stampa scheletro impronta')
    cmapmine = ListedColormap(['w', 'r'], N=2)
    plt.imshow(skelton, cmap=cmapmine, vmin=0, vmax=1)
    plt.savefig(name_folder + '/skelton.png', dpi = 400) """

    print('Inizio creazione ridges')
    skelton_data = skeltonUtils.generate_ridge_skelton(skelton)
    print('Fine creazione ridges')

    ridges = skelton_data['ridges']
    minutiae_data = skelton_data['minutiae_data']
    wrong_ridges = skelton_data['wrong_ridges']

    skelton_modified = np.zeros(skelton.shape).astype(int)
    for ridge in ridges:
        for pixel in ridge['pixels']:
            skelton_modified[pixel[0],pixel[1]] = 1

    skelton = skelton_modified.copy()

    """ print('Stampa scheletro impronta modificato')
    cmapmine = ListedColormap(['w', 'r'], N=2)
    plt.imshow(skelton, cmap=cmapmine, vmin=0, vmax=1)
    plt.savefig(name_folder + '/skelton_modified.png', dpi = 400) """
    #print(ridges)

    edge_ridges = []

    for r in ridges:
        edge_ridges.append({'minutiae': r['minutiae']})

    data_ridges = {'ridges': edge_ridges}
    with open(name_folder + '/ridge.json', 'w') as file_json_out:
        json.dump(data_ridges,file_json_out)

    data_minutiae = {'minutiae': minutiae_data}
    with open(name_folder + '/minutiae.json', 'w') as file_json_out:
        json.dump(data_minutiae,file_json_out) 

    ################################ GENERATE COLORED RIDGE IMAGE ###########################################
    print('STAMPA RIDGE SCHELETRO')


    x_e = []
    x_b = []
    y_e = []
    y_b = []
    for minutia in minutiae_data:
        if minutia['type'] == 'ending':
            x_e.append(minutia['x'])
            y_e.append(minutia['y'])
        else:
            x_b.append(minutia['x'])
            y_b.append(minutia['y'])


    all_ridges_matrix = np.zeros(skelton.shape).astype(int)
    count = 10
    meno = -1
    for ridge in ridges:
        for pixel in ridge['pixels']:
            if all_ridges_matrix[pixel[0], pixel[1]] == 0:
                all_ridges_matrix[pixel[0], pixel[1]] = 1 + count*meno
            else:
                all_ridges_matrix[pixel[0], pixel[1]] = all_ridges_matrix[pixel[0], pixel[1]] + 1 + count*meno
        count = count + 10
        meno = meno*(-1)

    """plt.clf
    plt.imshow(all_ridges_matrix, cmap = 'gist_ncar')
    #plt.savefig(name_folder + "/ridges.png", dpi = 500)
    plt.scatter(y_e,x_e,s=0.1,c='g')
    plt.scatter(y_b,x_b,s=0.1,c='b')
    plt.savefig(name_folder + "/ridges_minutiae.png", dpi = 500) """
    ###########################################################################################################

    ######################################## CONTROLLO WRONG RIDGES #########################################
    print('CONTROLLO WRONG RIDGES')

    if len(wrong_ridges) > 0:

        print('ridge sbagliati presenti')

        w_x = []
        w_y = []

        for index in wrong_ridges:
            for pixel in ridges[index]['pixels']:
                w_x.append(pixel[0])
                w_y.append(pixel[1])

        """ plt.scatter(w_y,w_x,s=0.1,c='black')
        plt.savefig(name_folder + "/wrong_ridges.png", dpi = 500) """

    ###########################################################################################################

    ######################################## CONTROLLO 1 ######################################################
    print('CONTROLLO RIDGE 1')
    common_pixels = []
    for index_r in range(0,len(ridges)):
        for index2_r in range(index_r+1,len(ridges)):
            temp_list = [list(x) for x in set(tuple(x) for x in ridges[index_r]['pixels']).intersection(set(tuple(x) for x in ridges[index2_r]['pixels']))]
            if len(temp_list) > 0:
                common_pixels.append(temp_list)

    for l_pixels in common_pixels:
        for pixel in l_pixels:
            #print(pixel)
            if not skeltonUtils.isMinutia(pixel, minutiae_data):
                print(False)
    #########################################################################################################

    ######################################## CONTROLLO 2 ######################################################
    print('CONTROLLO RIDGE 2')
    two_minutiae_x = []
    two_minutiae_y = []
    first_wrong_x = []
    first_wrong_y = []
    for ridge in ridges:
        if len(ridge['minutiae']) != 2 :
            print(ridge)
            first_wrong_x.append(ridge['pixels'][0][0])
            first_wrong_y.append(ridge['pixels'][0][1])
            for pixel in ridge['pixels']:
                two_minutiae_x.append(pixel[0])
                two_minutiae_y.append(pixel[1])

    """ if len(first_wrong_x) > 0 or len(two_minutiae_x) > 0:
        print('******* PROBLEMA ************')
        plt.imshow(all_ridges_matrix, cmap = 'gist_ncar')
        plt.scatter(two_minutiae_y,two_minutiae_x,s=0.1,c='black')
        plt.savefig(name_folder + "/ridges_wrong.png", dpi = 500) """
    #########################################################################################################

    ######################################## CONTROLLO 3 ######################################################
    print('CONTROLLO RIDGE 3')
    all_ridges_control = np.zeros(skelton.shape).astype(int)
    for ridge in ridges:
        for pixel in ridge['pixels']:
            all_ridges_control[pixel[0],pixel[1]] = 1

    difference_matrix = abs(skelton - all_ridges_control)
    if sum(sum(difference_matrix)) > 0:
        print('pixels non presenti nei ridge')
    #########################################################################################################

    ######################################## CONTROLLO 4 ######################################################
    print('CONTROLLO RIDGE 4')
    wrong_pixels_x = []
    wrong_pixels_y = []
    type_wrong = []
    count_wrong_pixels = []
    for i in range(0,skelton.shape[0]):
        for j in range(0,skelton.shape[1]):
            if skelton[i,j] == 1:
                count = 0
                for ridge in ridges:
                    if [i,j] in ridge['pixels']:
                        count = count + 1
                if skeltonUtils.isMinutia([i,j], minutiae_data):
                    if skeltonUtils.isEndMinutia([i,j], minutiae_data):
                        if count != 1:
                            wrong_pixels_x.append(i)
                            wrong_pixels_y.append(j)
                            type_wrong.append('end')
                            count_wrong_pixels.append(count)
                    else:
                        if count < 3:
                            wrong_pixels_x.append(i)
                            wrong_pixels_y.append(j)
                            type_wrong.append('bifurcation')
                            count_wrong_pixels.append(count)
                else:
                    if count != 1:
                        wrong_pixels_x.append(i)
                        wrong_pixels_y.append(j)
                        type_wrong.append('normal')
                        count_wrong_pixels.append(count)

    """ if len(wrong_pixels_x) > 0:
        plt.clf()
        plt.imshow(all_ridges_matrix, cmap = 'gist_ncar')
        plt.scatter(y_e,x_e,s=0.1,c='g')
        plt.scatter(y_b,x_b,s=0.1,c='b')
        plt.scatter(wrong_pixels_y,wrong_pixels_x,s=0.1,c='')
        plt.savefig(name_folder + "/pixels_wrong.png", dpi = 500)

        print('Pixel con numero ridge sbagliati')
        print(len(wrong_pixels_x))
        print('Ending minuzia')
        print(type_wrong.count('end'))
        print('Bifurcation minuzia')
        print(type_wrong.count('bifurcation'))
        print('Normal pixel')
        print(type_wrong.count('normal'))
        for i in range(0, len(wrong_pixels_x)):
            print(type_wrong[i])
            print(count_wrong_pixels[i]) """



    #########################################################################################################



    skeltonUtils.make_distances(ridges)

    limit = 8

    print('INIZIO COSTRUZIONE GRAFO')

    print('Inizio costruzione archi interni')
    internal_graph = skeltonUtils.find_internal_edges (ridges, minutiae_data, limit, skelton)
    print('Fine costruzione archi interni')


    new_nodes = internal_graph['nodes']
    new_nodes_type = internal_graph['nodes_type']
    new_edges = internal_graph['edges']
    new_edges_type = internal_graph['edges_type']
    new_distances = internal_graph['edges_distance']

    #print('pixel sbagliati:')
    #print(internal_graph['wrong_pixels'])

    print('Inizio costruzione archi di bordo')
    skeltonUtils.find_border_minutiae(skelton, new_nodes, new_nodes_type)
    border_edges = skeltonUtils.find_border_edges(new_nodes, new_nodes_type)
    new_edges = new_edges + border_edges
    for edge in border_edges:
        node1 = np.array(new_nodes[edge[0]])
        node2 = np.array(new_nodes[edge[1]])
        new_distances.append(distance.euclidean(node1,node2))
    new_edges_type = new_edges_type + ['border' for e in border_edges]

    print('FINE COSTRUZIONE GRAFO')

    data_nodes = []
    for i_node in range(0, len(new_nodes)):
        data_nodes.append({'coordinates': [int(new_nodes[i_node][0]),int(new_nodes[i_node][1])], 'type': new_nodes_type[i_node]})
        #data_nodes.append({'type': new_nodes_type[i_node]})
    
    data_edges = []
    for i_edge in range(0, len(new_edges)):
        data_edges.append({'nodes': [int(new_edges[i_edge][0]), int(new_edges[i_edge][1])], 'type': new_edges_type[i_edge], 'distance': float(new_distances[i_edge])})
    
    data_nodes = {'nodes': data_nodes}
    with open(name_folder + '/nodes_graph.json', 'w') as file_json_out:
        json.dump(data_nodes,file_json_out)

    data_edges = {'edges': data_edges}
    with open(name_folder + '/edges_graph.json', 'w') as file_json_out:
        json.dump(data_edges,file_json_out)


    ######################################## CONTROLLO ######################################################
    print('CONTROLLO 1')
    wrong_p_x = []
    wrong_p_y = []

    for pixel in internal_graph['wrong_pixels']:
        wrong_p_x.append(pixel[0])
        wrong_p_y.append(pixel[1])

    if len(wrong_p_x) > 0:
        print("ERRORE PIXEL NON PRESENTI")
        plt.clf()
        plt.imshow(all_ridges_matrix, cmap = 'gist_ncar')
        plt.scatter(y_e,x_e,s=0.1,c='g')
        plt.scatter(y_b,x_b,s=0.1,c='b')
        plt.scatter(wrong_p_y, wrong_p_x,s=0.1,c='black')
        plt.savefig(name_folder + '/wrong_pixels_find_edges.png', dpi = 1000)
    #########################################################################################################

    ############################################### STAMPA GRAFI ############################################

    print('INIZIO STAMPE GRAFO')

    ########################### Archi di bordo #####################
    """ plt.clf()
    plt.close()
    plt.figure().clear()

    new_graph = nx.Graph()

    border_nodes = []

    for edge in border_edges:
        border_nodes.append(new_nodes[edge[0]])

    color_map = []
    for i in range(0,len(border_nodes)):
            new_graph.add_node(i, pos=(border_nodes[i][1],border_nodes[i][0]))
            color_map.append('purple')
    pos=nx.get_node_attributes(new_graph,'pos')

    for j in range(0, len(border_edges)-1):
        new_graph.add_edge(j,j+1)


    #print(graph[int(name)].size())
    #plt.figure(2)
    cmapmine = ListedColormap(['w', 'black'], N=2)
    plt.imshow(skelton, cmap=cmapmine, vmin=0, vmax=1)
    nx.draw(new_graph, pos, node_color=color_map, edge_color = 'r', with_labels=True, node_size = 1, width=0.4, font_size = 1)
    plt.savefig(name_folder + '/graph_border_edges.png',dpi=500)
    plt.clf() """






    ########################### Grafo completo #####################
    """ plt.clf()
    plt.close()
    plt.figure().clear()

    new_graph = nx.Graph()

    color_map = []
    for i in range(0,len(new_nodes)):
        new_graph.add_node(i, pos=(new_nodes[i][1],new_nodes[i][0]))
        if new_nodes_type[i] == "bifurcation" :
            color_map.append('blue')
        else:
            if new_nodes_type[i] == "ending":
                color_map.append('green')
            else:
                if new_nodes_type[i] == "border":
                    color_map.append('purple')
                else:
                    color_map.append('red')

    pos=nx.get_node_attributes(new_graph,'pos')

    for j in range(0, len(new_edges)):
        if new_edges_type[j] == 'border':
            new_graph.add_edge(new_edges[j][0],new_edges[j][1], color = 'purple')
        else:
            if new_edges_type[j] == 'ridge':
                new_graph.add_edge(new_edges[j][0],new_edges[j][1], color = 'blue')
            else:
                new_graph.add_edge(new_edges[j][0],new_edges[j][1], color = 'red')

    edges = new_graph.edges()
    colors = [new_graph[u][v]['color'] for u,v in edges]

    #print(graph[int(name)].size())
    #plt.figure(2)
    cmapmine = ListedColormap(['w', 'black'], N=2)
    plt.imshow(skelton, cmap=cmapmine, vmin=0, vmax=1)
    nx.draw(new_graph, pos, node_color=color_map, edge_color = colors, with_labels=True, node_size = 1, width=0.4, font_size = 1)
    plt.savefig(name_folder + '/complete_graph.png',dpi=700)
    plt.clf() """




    ########################### Nodi Grafo #####################
    """ plt.clf()
    plt.close()
    plt.figure().clear()

    new_graph = nx.Graph()

    color_map = []
    for i in range(0,len(new_nodes)):
        new_graph.add_node(i, pos=(new_nodes[i][1],new_nodes[i][0]))
        if new_nodes_type[i] == "bifurcation" :
            color_map.append('blue')
        else:
            if new_nodes_type[i] == "ending":
                color_map.append('green')
            else:
                if new_nodes_type[i] == "border":
                    color_map.append('purple')
                else:
                    color_map.append('red')

    pos=nx.get_node_attributes(new_graph,'pos')


    #print(graph[int(name)].size())
    #plt.figure(2)
    cmapmine = ListedColormap(['w', 'black'], N=2)
    plt.imshow(skelton, cmap=cmapmine, vmin=0, vmax=1)
    nx.draw(new_graph, pos, node_color=color_map, with_labels=True, node_size = 1, width=0.2, font_size = 0.5)
    plt.savefig(name_folder + '/graph_nodes.png',dpi=700)
    plt.clf() """




    ########################### Archi aggiunti #####################
    """plt.clf()
    plt.close()
    plt.figure().clear()

    new_graph = nx.Graph()

    color_map = []
    for i in range(0,len(new_nodes)):
        new_graph.add_node(i, pos=(new_nodes[i][1],new_nodes[i][0]))
        if new_nodes_type[i] == "bifurcation" :
            color_map.append('blue')
        else:
            if new_nodes_type[i] == "ending":
                color_map.append('green')
            else:
                if new_nodes_type[i] == "border":
                    color_map.append('purple')
                else:
                    color_map.append('red')

    pos=nx.get_node_attributes(new_graph,'pos')

    for j in range(0, len(new_edges)):
        if new_edges_type[j] == 'added':
            new_graph.add_edge(new_edges[j][0],new_edges[j][1], color = 'red')

    edges = new_graph.edges()
    colors = [new_graph[u][v]['color'] for u,v in edges]

    #print(graph[int(name)].size())
    #plt.figure(2)
    cmapmine = ListedColormap(['w', 'black'], N=2)
    plt.imshow(skelton, cmap=cmapmine, vmin=0, vmax=1)
    nx.draw(new_graph, pos, node_color=color_map, edge_color = colors, with_labels=True, node_size = 1, width=0.4, font_size = 1)
    plt.savefig(name_folder + '/graph_added_edges.png',dpi=700)
    plt.clf() """







    ########################### Archi di ridge #####################
    """ plt.clf()
    plt.close()
    plt.figure().clear()

    new_graph = nx.Graph()

    color_map = []
    for i in range(0,len(new_nodes)):
        new_graph.add_node(i, pos=(new_nodes[i][1],new_nodes[i][0]))
        if new_nodes_type[i] == "bifurcation" :
            color_map.append('blue')
        else:
            if new_nodes_type[i] == "ending":
                color_map.append('green')
            else:
                if new_nodes_type[i] == "border":
                    color_map.append('purple')
                else:
                    color_map.append('red')

    pos=nx.get_node_attributes(new_graph,'pos')

    for j in range(0, len(new_edges)):
        if new_edges_type[j] == 'ridge':
            new_graph.add_edge(new_edges[j][0],new_edges[j][1], color = 'blue')

    edges = new_graph.edges()
    colors = [new_graph[u][v]['color'] for u,v in edges]

    #print(graph[int(name)].size())
    #plt.figure(2)
    cmapmine = ListedColormap(['w', 'black'], N=2)
    plt.imshow(skelton, cmap=cmapmine, vmin=0, vmax=1)
    nx.draw(new_graph, pos, node_color=color_map, edge_color = colors, with_labels=True, node_size = 1, width=0.4, font_size = 1)
    plt.savefig(name_folder + '/graph_ridge_edges.png',dpi=700)
    plt.clf() """


    ########################### Grafo completo senza scheletro#####################
    """ plt.clf()
    plt.close()
    plt.figure().clear()

    new_graph = nx.Graph()

    color_map = []
    for i in range(0,len(new_nodes)):
        new_graph.add_node(i, pos=(new_nodes[i][1],new_nodes[i][0]))
        if new_nodes_type[i] == "bifurcation" :
            color_map.append('blue')
        else:
            if new_nodes_type[i] == "ending":
                color_map.append('green')
            else:
                if new_nodes_type[i] == "border":
                    color_map.append('purple')
                else:
                    color_map.append('red')

    pos=nx.get_node_attributes(new_graph,'pos')

    for j in range(0, len(new_edges)):
        if new_edges_type[j] == 'border':
            new_graph.add_edge(new_edges[j][0],new_edges[j][1], color = 'purple')
        else:
            if new_edges_type[j] == 'ridge':
                new_graph.add_edge(new_edges[j][0],new_edges[j][1], color = 'blue')
            else:
                new_graph.add_edge(new_edges[j][0],new_edges[j][1], color = 'red')

    edges = new_graph.edges()
    colors = [new_graph[u][v]['color'] for u,v in edges]

    #print(graph[int(name)].size())
    #plt.figure(2)
    nx.draw(new_graph, pos, node_color=color_map, edge_color = colors, with_labels=True, node_size = 1, width=0.4, font_size = 1)
    plt.savefig(name_folder + '/complete_graph_w_s.png',dpi=700)
    plt.clf()
 """
    ########################### Grafo completo senza scheletro e senza posizione nodi #####################
    """ plt.clf()
    plt.close()
    plt.figure().clear()

    new_graph = nx.Graph()

    color_map = []
    for i in range(0,len(new_nodes)):
        new_graph.add_node(i)
        if new_nodes_type[i] == "bifurcation" :
            color_map.append('blue')
        else:
            if new_nodes_type[i] == "ending":
                color_map.append('green')
            else:
                if new_nodes_type[i] == "border":
                    color_map.append('purple')
                else:
                    color_map.append('red')

    #pos=nx.get_node_attributes(new_graph,'pos')

    for j in range(0, len(new_edges)):
        if new_edges_type[j] == 'border':
            new_graph.add_edge(new_edges[j][0],new_edges[j][1], color = 'purple')
        else:
            if new_edges_type[j] == 'ridge':
                new_graph.add_edge(new_edges[j][0],new_edges[j][1], color = 'blue')
            else:
                new_graph.add_edge(new_edges[j][0],new_edges[j][1], color = 'red')

    edges = new_graph.edges()
    colors = [new_graph[u][v]['color'] for u,v in edges]

    #print(graph[int(name)].size())
    #plt.figure(2)
    nx.draw(new_graph, node_color=color_map, edge_color = colors, with_labels=True, node_size = 1, width=0.4, font_size = 1)
    plt.savefig(name_folder + '/complete_graph_w_pos.png',dpi=700)
    plt.clf()
 """

    ########################### Archi di ridge senza scheletro#####################
    """ plt.clf()
    plt.close()
    plt.figure().clear()

    new_graph = nx.Graph()

    color_map = []
    for i in range(0,len(new_nodes)):
        new_graph.add_node(i, pos=(new_nodes[i][1],new_nodes[i][0]))
        if new_nodes_type[i] == "bifurcation" :
            color_map.append('blue')
        else:
            if new_nodes_type[i] == "ending":
                color_map.append('green')
            else:
                if new_nodes_type[i] == "border":
                    color_map.append('purple')
                else:
                    color_map.append('red')

    pos=nx.get_node_attributes(new_graph,'pos')

    for j in range(0, len(new_edges)):
        if new_edges_type[j] == 'ridge':
            new_graph.add_edge(new_edges[j][0],new_edges[j][1], color = 'blue')

    edges = new_graph.edges()
    colors = [new_graph[u][v]['color'] for u,v in edges]

    #print(graph[int(name)].size())
    #plt.figure(2)
    nx.draw(new_graph, pos, node_color=color_map, edge_color = colors, with_labels=True, node_size = 1, width=0.4, font_size = 1)
    plt.savefig(name_folder + '/graph_ridge_edges_s_w.png',dpi=700)
    plt.clf() """

    #########################################################################################################


