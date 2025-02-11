#!/usr/bin/python

#execution example: "python3 out_reader.py output"

import sys
import os
import re
from collections import defaultdict
from sortedcontainers import SortedSet, SortedDict


def clean(string):
	replaced = re.sub('match\(', '', string)
	replaced = re.sub('\)', '', replaced)
	replaced = re.sub(' ', '', replaced)
	return replaced

def initialize_map(edge, edges_map1, edges_map2):
	edge = clean(edge)
	edge = edge.split(',')
	edges_map1[edge[0] + '_' + edge[1]] = SortedSet()
	edges_map2[edge[0] + '_' + edge[1]] = SortedSet()


def generate_map(edge, edges_map1, edges_map2):
	edge = clean(edge)
	edge = edge.split(',')
	edges_map1[edge[0] + '_' + edge[1]].add(edge[0] + '_' + edge[1])
	edges_map2[edge[0] + '_' + edge[1]].add(edge[3] + '_' + edge[4])


n_edge = re.compile('match\(.+\)')

with open(sys.argv[1], 'r') as n:
	n = n.read()

	edges_map1 = SortedDict()
	edges_map2 = SortedDict()

	edges = re.findall(n_edge, n)
	for edge in edges:
		initialize_map(edge,edges_map1,edges_map2)


	for edge in edges:
		generate_map(edge,edges_map1,edges_map2)

	splitted_edge = clean(edge)
	splitted_edge = splitted_edge.split(',')
	outputfile1 = open('ridges_color'+splitted_edge[2]+'.json', 'w')
	outputfile2 = open('ridges_color'+splitted_edge[5]+'.json', 'w')

	print('{\n  \"ridges\": [',end="\n", file = outputfile1)
	print('{\n  \"ridges\": [',end="\n", file = outputfile2)

	color = 1
	#print("\n//SUBGRAPHS List:", end ="\n", file = outputfile)
	for key1,values1 in edges_map1.items():
		#print(key, end ="")
		#print('{rank = ' + str(counter_rank) + '; ', end ="", file = outputfile)
		for val1 in values1:
			print('\t{', end ="\n", file = outputfile1)
			to_print1 = val1.split('_')
			print('\t  \"ID1\": ' + to_print1[0], end =",\n", file = outputfile1)
			print('\t  \"ID2\": ' + to_print1[1], end =",\n", file = outputfile1)
			print('\t  \"color\": ' + str(color), file = outputfile1)
			print('\t}',end="", file = outputfile1)
			if values1.index(val1) != len(values1)-1:
				print(',\n', end ="", file = outputfile1)
			color+=1
			if color-1 < len(edges_map1):
				print(',\n', end ="", file = outputfile1)

	color = 1
	for key2,values2 in edges_map2.items():
		#print(key, end ="")
		#print('{rank = ' + str(counter_rank) + '; ', end ="", file = outputfile)
		for val2 in values2:
			print('\t{', end ="\n", file = outputfile2)
			to_print2 = val2.split('_')
			print('\t  \"ID1\": ' + to_print2[0], end =",\n", file = outputfile2)
			print('\t  \"ID2\": ' + to_print2[1], end =",\n", file = outputfile2)
			print('\t  \"color\": ' + str(color), file = outputfile2)
			print('\t}',end="", file = outputfile2)
			if values2.index(val2) != len(values2)-1:
				print(',\n', end ="", file = outputfile2)
		color+=1
		if color-1 < len(edges_map1):
			print(',\n', end ="", file = outputfile2)


print('\n  ]', file = outputfile1)
print('\n  ]', file = outputfile2)
print('}', file = outputfile1)
print('}', file = outputfile2)

outputfile1.close()
outputfile2.close()
