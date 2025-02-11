#include <iostream>
#include <string>
#include <cmath>


#include "../include/names_retriever.h"
#include "../include/fp_edges_score_generator.h"
#include "../include/fp_edges_percentage_generator.h"
#include "../include/fp_edges_percentage_generator_plp.h"

int
main()
{

	names_retriever retriever;

	std::string folder_path;
	//int size = retriever.get_names_size();

	//for (int i = 0; i < size; ++i) {

	//std::string name_temp = retriever.get_name(i);

	folder_path = "/home/giacomo/Desktop/Tirocinio-Tesi/FINGERPRINT/Sourceafis/Passaggi/transparency_medio_sx_mio-3_1_Modificata/";

	//folder_path = folder_path + name_temp + "/";

	//std::string minutiae_file = folder_path + "076-shuffled-minutiae.json";
	//std::string edges_file = folder_path + "my_edges.json";

	// fp_edges_score_generator score_gen = fp_edges_score_generator(folder_path);
	// fp_edges_percentage_generator percentage_gen = fp_edges_percentage_generator(folder_path);
	// fp_edges_percentage_generator_plp plp_gen = fp_edges_percentage_generator_plp(folder_path);

	// score_gen.generate_follow_minutiae_sequentially(0);
	// score_gen.generate_follow_minutiae_file_order();
	// percentage_gen.generate();
	// plp_gen.generate_plp();


	//std::cout << "Main normale fatto";
	//std::cout << name_temp + " done.\n";



	fp_edges_table et = fp_edges_table();


	//Crea grafo non orientato in "edges_follow_ridges.json"
	et.incoming_minutia_follow_ridges(50, 300);

	//Crea grafo orientato in "edges_follow_ridges.json"
	//et.minutia_follow_direction(folder_path,"edges_follow_ridges.json", 50, 300);
	//}

	return 0;
}