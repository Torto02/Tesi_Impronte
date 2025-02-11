#include "fp_edges_score_generator.h"

#include <queue>

#include "../lib/json/include/json.h"

std::tuple<int, int> fp_edges_score_generator::generate_single(int index, const fp_total_minutiae & total_minutiae, std::ofstream& outputFile)
{
	//To keep track of the existing edges

	fp_minutia reference = total_minutiae.get_minutia(index);

	std::tuple<int, int> best_edges = fp_edges_table::get_instance().get_best_outgoing_edges(reference, total_minutiae);

	write_edges(outputFile, reference, best_edges);


	return best_edges;
}

void fp_edges_score_generator::generate_follow_minutiae_file_order()
{

	//To keep track of the existing edges

	//Generate the .json file to store the graph edges (the nodes are the minutiae)
	std::ofstream outputFile;
	outputFile.open(files::get_instance().get_output_folder() + "/edges_score_originalOrder.json");
	outputFile << "{" << '\n';
	outputFile << "  \"edges\": [" << '\n';

	Json::Value minutiae_file;
	std::ifstream config_doc(files::get_instance().get_minutiae_file(), std::ifstream::binary);
	config_doc >> minutiae_file;
	const Json::Value minutiae = minutiae_file["minutiae"];

	fp_total_minutiae total_minutiae;

	int size = total_minutiae.get_total_number_of_minutiae();

	//Iterate on the edges to find the incoming edge for each minutia
	for (int i = 0; i < size; ++i) {
		generate_single(i, total_minutiae, outputFile);
	}

	outputFile << "  ]" << '\n';
	outputFile << "}";

	fp_edges_table::get_instance().clear();
}

void fp_edges_score_generator::generate_follow_minutiae_sequentially(int starting_minutia)
{
	std::queue<int> to_visit;
	to_visit.push(starting_minutia);

	//Generate the .json file to store the graph edges (the nodes are the minutiae)
	std::ofstream outputFile;
	outputFile.open(files::get_instance().get_output_folder() + "/edges_score_sequentialOrder.json");
	outputFile << "{" << '\n';
	outputFile << "  \"edges\": [" << '\n';

	Json::Value minutiae_file;
	std::ifstream config_doc(files::get_instance().get_minutiae_file(), std::ifstream::binary);
	config_doc >> minutiae_file;
	const Json::Value minutiae = minutiae_file["minutiae"];

	fp_total_minutiae total_minutiae;

	int reached_minutia;

	while (!to_visit.empty()) {

		std::tuple<int, int> reached_minutiae = generate_single(to_visit.front(), total_minutiae, outputFile);
		to_visit.pop();

		reached_minutia = std::get<0>(reached_minutiae);
		if (reached_minutia >= 0) {
			to_visit.push(reached_minutia);
			reached_minutia = std::get<1>(reached_minutiae);
			if (reached_minutia >= 0) {
				to_visit.push(reached_minutia);
			}
		}
	}

	outputFile << "  ]" << '\n';
	outputFile << "}";

	fp_edges_table::get_instance().clear();
}

void fp_edges_score_generator::write_edge(std::ofstream& outputFile, int ref_x, int ref_y, const fp_minutia & best_neigh) const
{
	//std::cout << "Added to incoming: " << best_neigh.get_id() << std::endl;
	fp_edges_table::get_instance().add_incoming(best_neigh.get_id());
	outputFile << "    {" << '\n';
	outputFile << "      \"x1\": " << ref_x << ", " << '\n';
	outputFile << "      \"y1\": " << ref_y << ", " << '\n';
	outputFile << "      \"x2\": " << best_neigh.get_x() << ", " << '\n';
	outputFile << "      \"y2\": " << best_neigh.get_y() << '\n';
	outputFile << "    },\n";
}

void fp_edges_score_generator::write_edges(std::ofstream& outputFile, const fp_minutia & reference, const std::tuple<int, int> & best_edges) const
{
	fp_minutia best_neigh;
	int best_edge1 = std::get<0>(best_edges);
	if (best_edge1 >= 0) {
		best_neigh.set_minutia(files::get_instance().get_minutiae_file(), best_edge1);
		write_edge(outputFile, reference.get_x(), reference.get_y(), best_neigh);
		if (reference.get_type()) //Bifurcation
		{
			int best_edge2 = std::get<1>(best_edges);
			if (best_edge2 >= 0) {
				best_neigh.set_minutia(files::get_instance().get_minutiae_file(), best_edge2);
				write_edge(outputFile, reference.get_x(), reference.get_y(), best_neigh);
			}
		}
		fp_edges_table::get_instance().add_outgoing(reference.get_id());
	}
}