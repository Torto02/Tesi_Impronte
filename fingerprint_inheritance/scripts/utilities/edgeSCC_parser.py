#!/usr/bin/python

#execution example: "python3 out_reader.py output" --- rember not to put the txt extension, is considered like that as default

import sys
import re
from collections import defaultdict
from sortedcontainers import SortedSet, SortedDict


def clean(string):
	replaced = re.sub('coloredge\(', '', string)
	replaced = re.sub('\)', '', replaced)
	replaced = re.sub(' ', '', replaced)
	return replaced

def initialize_map(edge, edges_map):
	edge = clean(edge)
	edge = edge.split(',')
	edges_map[edge[3]] = SortedSet()

def generate_map(edge, edges_map):
	edge = clean(edge)
	edge = edge.split(',')
	edges_map[edge[3]].add(edge[1] + '_' + edge[2])

outputfile = open('scc_color.json', 'w')
n_edge = re.compile('coloredge\(\S+\)')


print('{\n  \"edges\": [',end="\n", file = outputfile)

with open(sys.argv[1], 'r') as n:
	n = n.read()

	edges_map = SortedDict()
	edges = re.findall(n_edge, n)
	for edge in edges:
		initialize_map(edge,edges_map)

	for edge in edges:
		generate_map(edge,edges_map)

	counter_map = 0
	#print("\n//SUBGRAPHS List:", end ="\n", file = outputfile)
	for key,values in edges_map.items():
		#print(key, end ="")
		#print('{rank = ' + str(counter_rank) + '; ', end ="", file = outputfile)
		for val in values:
			print('\t{', end ="\n", file = outputfile)
			to_print = val.split('_')
			print('\t  \"ID1\": ' + to_print[0], end =",\n", file = outputfile)
			print('\t  \"ID2\": ' + to_print[1], end =",\n", file = outputfile)
			print('\t  \"color\": ' + str(counter_map), file = outputfile)
			print('\t}',end="", file = outputfile)
			if values.index(val) != len(values)-1:
				print(',\n', end ="", file = outputfile)
		counter_map+=1
		if counter_map < len(edges_map):
			print(',\n', end ="", file = outputfile)

print('\n  ]', file = outputfile)
print('}', file = outputfile)

outputfile.close()
