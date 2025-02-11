#include "fp_edges_percentage_generator.h"

#include <queue>

#include "../lib/json/include/json.h"
#include "files.h"

double fp_edges_percentage_generator::get_percentage(const fp_edge & edge, double angle_bif) const
{

	double percentage = 100;

	percentage = percentage - (std::abs(PI - edge.get_neighbor_angle()) * 10);

	return percentage;
}

void fp_edges_percentage_generator::generate_single(int index, const fp_total_minutiae & total_minutiae, std::ofstream& outputFile)
{
	//To keep track of the existing edges

	fp_minutia reference = total_minutiae.get_minutia(index);

	for (int i = 0; i < total_minutiae.get_total_number_of_minutiae(); i++) {
		if (i != index) {
			fp_minutia neigh;
			neigh.set_minutia(total_minutiae.get_minutia(i));
			write_edge(outputFile, fp_edge(reference, neigh));
		}
	}
}

void fp_edges_percentage_generator::generate()
{
	//Generate the .json file to store the graph edges (the nodes are the minutiae)
	std::ofstream outputFile;
	outputFile.open(files::get_instance().get_output_folder() + "/edges_percentage.json");
	outputFile << "{" << '\n';
	outputFile << "  \"table\": [" << '\n';

	Json::Value minutiae_file;
	std::ifstream config_doc(files::get_instance().get_minutiae_file(), std::ifstream::binary);
	config_doc >> minutiae_file;
	const Json::Value minutiae = minutiae_file["minutiae"];

	fp_total_minutiae total_minutiae;

	int size = total_minutiae.get_total_number_of_minutiae();

	//Iterate on the edges to find the incoming edge for each minutia
	for (int i = 0; i < size; ++i) {
		outputFile << "    {\n";
		outputFile << "      \"reference\": " << i << ",\n";
		outputFile << "      \"edges\": [\n";
		generate_single(i, total_minutiae, outputFile);
		outputFile << "      ]\n";
		outputFile << "    },\n";
	}

	outputFile << "  ]" << '\n';
	outputFile << "}";
}

void fp_edges_percentage_generator::write_edge(std::ofstream& outputFile, fp_edge edge) const
{
	outputFile << "        {" << '\n';
	outputFile << "          \"neighbor\": " << edge.get_neighbor_minutia().get_id() << "," << '\n';
	//TODO: Angolo bif
	outputFile << "          \"percentage\": " << get_percentage(edge, 0) << "\n";
	outputFile << "        },\n";
}