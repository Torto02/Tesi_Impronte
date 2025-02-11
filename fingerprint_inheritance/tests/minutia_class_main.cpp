#include <iostream>
#include <fstream>
#include <algorithm>
#include <string>
#include <set>


#include "../lib/json/include/json.h"
#include "../include/names_retriever.h"
#include "../include/fp_minutia.h"
//#include "../include/fp_edge.h"
//#include "../include/fp_edges_table.h"

int
main()
{

	std::string folder_path;

	names_retriever names_retriever;

	int minutia_number = 0;

	folder_path = "/mnt/c/Users/franc/Desktop/Ago_fp/Enhanced/transparency_";
	folder_path = folder_path + names_retriever.get_name(0) + "/";
	std::string minutiae_file = folder_path + "076-shuffled-minutiae.json";

	fp_minutia fp_minutia;
	fp_minutia.set_minutia(minutiae_file, minutia_number);

	std::cout << "id: " << fp_minutia.get_id() << " x: " << fp_minutia.get_x() << " y: " << fp_minutia.get_y() << " direction: " << fp_minutia.get_direction() << " type: ";
	if (fp_minutia.get_type()) {
		std::cout << "bifurcation" << std::endl;
	} else {
		std::cout << "ending" << std::endl;
	}

	minutia_number = 1;
	fp_minutia.set_minutia(minutiae_file, minutia_number);
	std::cout << "id: " << fp_minutia.get_id() << " x: " << fp_minutia.get_x() << " y: " << fp_minutia.get_y() << " direction: " << fp_minutia.get_direction() << " type: ";
	if (fp_minutia.get_type()) {
		std::cout << "bifurcation" << std::endl;
	} else {
		std::cout << "ending" << std::endl;
	}




	return 0;
}