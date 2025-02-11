#include <queue>

#include "files.h"
#include "fp_edges_percentage_generator_plp.h"
#include "../lib/json/include/json.h"

bool is_first;

double fp_edges_percentage_generator_plp::get_percentage(const fp_edge & edge, double angle_bif) const
{
	double percentage = 100;
	percentage = percentage - (std::abs(PI - edge.get_neighbor_angle()) * 10);
	return percentage;
}

void fp_edges_percentage_generator_plp::generate_single(int index, const fp_total_minutiae & total_minutiae, std::ofstream& outputFile)
{
	//To keep track of the existing edges

	fp_minutia reference = total_minutiae.get_minutia(index);

	int minutiae_count = total_minutiae.get_total_number_of_minutiae();
	is_first = true;
	for (int i = 0; i < minutiae_count; i++) {
		if (i != index) {
			fp_minutia neigh;
			neigh.set_minutia(total_minutiae.get_minutia(i));
			write_edge(outputFile, fp_edge(reference, neigh));

		}
	}
	if (!is_first)
		outputFile << ".\n";
}

void fp_edges_percentage_generator_plp::generate_plp()
{
	//Generate the .json file to store the graph edges (the nodes are the minutiae)
	std::ofstream outputFile;
	outputFile.open(files::get_instance().get_output_folder() + "/edges_score.plp");

	Json::Value minutiae_file;
	std::ifstream config_doc(files::get_instance().get_output_folder(), std::ifstream::binary);
	config_doc >> minutiae_file;
	const Json::Value minutiae = minutiae_file["minutiae"];

	fp_total_minutiae total_minutiae;

	int size = total_minutiae.get_total_number_of_minutiae();

	//Iterate on the edges to find the incoming edge for each minutia
	for (int i = 0; i < size; ++i) {
		outputFile << "%Minutia " << i << " edges\n";
		generate_single(i, total_minutiae, outputFile);
	}
}

void fp_edges_percentage_generator_plp::write_edge(std::ofstream& outputFile, const fp_edge& edge) const
{
	if (edge.get_length() < 40) {
		if (!is_first)
			outputFile << "; ";
		outputFile << "edge(m" << edge.get_reference_minutia().get_id() << ",m" << edge.get_neighbor_minutia().get_id() << "): " << get_percentage(edge, 0) / 10000;
		is_first = false;
	}
}