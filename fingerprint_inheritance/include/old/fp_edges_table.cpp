#include "fp_edges_table.h"
#include "files.h"

#include <algorithm>
#include <stdlib.h>
#include <fstream>
#include<cmath>

fp_edges_table::fp_edges_table()
{
}

fp_edges_table& fp_edges_table::get_instance()
{
	static fp_edges_table instance;
	return instance;
}

/*Types:
	0: ENDING
	1: BIFURCATION
 */
bool fp_edges_table::exists_outgoing(int id) const
{

	std::set<int>::iterator it = m_minutiae_with_outgoing_edges.find(id);

	if (it == m_minutiae_with_outgoing_edges.end())
		return false;

	return true;
}

bool fp_edges_table::exists_incoming(int id) const
{

	std::set<int>::iterator it = m_minutiae_with_incoming_edges.find(id);

	if (it == m_minutiae_with_incoming_edges.end())
		return false;

	return true;
}

void fp_edges_table::add_outgoing(int id)
{
	m_minutiae_with_outgoing_edges.insert(id);
}

void fp_edges_table::add_incoming(int id)
{
	m_minutiae_with_incoming_edges.insert(id);
}

void fp_edges_table::print_outgoing() const
{
	std::set<int>::iterator it = m_minutiae_with_outgoing_edges.begin();

	// Iterate till the end of set
	while (it != m_minutiae_with_outgoing_edges.end()) {
		// Print the element
		std::cout << (*it) << ",";
		//Increment the iterator
		it++;
	}
	std::cout << std::endl;
}

void fp_edges_table::print_incoming() const
{
	std::set<int>::iterator it = m_minutiae_with_incoming_edges.begin();

	// Iterate till the end of set
	while (it != m_minutiae_with_incoming_edges.end()) {
		// Print the element
		std::cout << (*it) << ",";
		//Increment the iterator
		it++;
	}
	std::cout << std::endl;
}

void fp_edges_table::clear()
{
	m_minutiae_with_outgoing_edges.clear();
	m_minutiae_with_incoming_edges.clear();
}

double fp_edges_table::get_degrees(double radians) const
{
	return(radians * 180) / PI;
}

double fp_edges_table::get_radians(double degrees) const
{
	return(degrees * PI) / 180;
}



/*
Score of the edge based on:
	-Angle(s) of exit
 *Best at 0 degree if @type:ENDING (0)
 *Best at +/-@angle_bif degrees if @type:BIFURCATION (1)
			#To get the right couple check the similiraty of the two exiting angles
	-Angle of entrance
 *Best at -180 degrees
	?Lenght?

	Every minutia MUST have one and only one entrance edge.

	Brute force: Generate all the graphs selecting each minutia as starting point and pick the best?
 */

//Minutiae map to find the best score
typedef std::map<int, double> evaluated_neighbors;

bool value_comparer(evaluated_neighbors::value_type &i1, evaluated_neighbors::value_type &i2)
{
	return i1.second < i2.second;
}

double fp_edges_table::get_score(const fp_edge & edge, double angle_bif) const
{

	double score = 100;

	score = score - (std::abs(PI - edge.get_neighbor_angle())*10);

	if (exists_outgoing(edge.get_reference_minutia().get_id()))
		score = -1;
	if (exists_incoming(edge.get_neighbor_minutia().get_id()))
		score = -1;

	return score;
}

std::tuple<int, int> fp_edges_table::get_best_outgoing_edges(const minutia & reference, const fp_total_minutiae & minutiae_table) const
{
	//Intialize a map to use for comparison (to retrieve the best incoming edge)
	std::map<int, double> for_comparsion;

	//Iterate on all the reference edges and store | After the loop -> select the best(s) (1 or 2 depending on the type)
	//If and edge is good but the neighbor minutia is already taken discard the edge (for now)
	for (int j = 0; j < minutiae_table.get_total_number_of_minutiae(); ++j) {
		if (reference.get_id() != j) {
			minutia neighbor;
			neighbor.set_minutia(minutiae_table.get_minutia(j));
			fp_edge edge = fp_edge(reference, neighbor);

			for_comparsion.emplace(neighbor.get_id(), get_score(edge, 0.0));
		}
	}
	//Get the best edge for reference minutia -> the one with the highest score
	evaluated_neighbors::iterator ev_neigh_it = std::max_element(for_comparsion.begin(), for_comparsion.end(), value_comparer);
	if (reference.get_type()/* == 1*/) //Bifurcation
	{
		int first_edge = ev_neigh_it->first;
		double first_score = ev_neigh_it->second;
		for_comparsion.erase(ev_neigh_it->first);
		ev_neigh_it = std::max_element(for_comparsion.begin(), for_comparsion.end(), value_comparer);
		if ((first_score < 0) || (ev_neigh_it->second < 0))
			return {
			-1, -1
		};
		return
		{
			first_edge, ev_neigh_it->first
		};
	} else {
		//Use -1 to indicate that there is only one branch
		if (ev_neigh_it->second < 0)
			return {
			-1, -1
		};
		return
		{
			ev_neigh_it->first, -1
		};
	}
}


//TODO: Venturini
//Usare questo file o cambiarlo nel java "edges_follow_ridges.json"
//Crea nel file l'elenco degli archi che formano il grafo non orientato

void fp_edges_table::incoming_minutia_follow_ridges(int max, int max_dist)
{
	matrix_from_file matrix_original;

	fp_total_minutiae total;
	std::ofstream outputFile;
	outputFile.open(files::get_instance().get_output_folder() + "/graph_following_ridges.json");
	outputFile << "{" << '\n';
	outputFile << "  \"edges\": [" << '\n';
	for (int i = 0; i < total.get_total_number_of_minutiae(); i++) {
		matrix_from_file matrix_visited;

		int find = 0;
		incoming_minutia_follow_ridges(total.get_minutia(i), total, matrix_original, matrix_visited, total.get_minutia(i).get_y(), total.get_minutia(i).get_x(), max, max_dist, find, 0, outputFile);
	}
	outputFile << "  ]" << '\n';
	outputFile << "}";
	outputFile.close();
}

void fp_edges_table::incoming_minutia_follow_ridges(const minutia & reference, fp_total_minutiae & minutiae_table, matrix_from_file & matrix_file, matrix_from_file & matrix_visited, int i, int k, int max, int max_dist, int & find, int dist, std::ofstream & outputFile)
{

	if (find < max && dist < max_dist) {
		//controllo se nell'immagine sono su un ridge che non ho ancora visitato nella DFS (visited!=0)
		if (matrix_file[i][k] == 1 && matrix_visited[i][k] != 0) {
			dist++;
			bool flag = false;
			matrix_visited[i][k] = 0;
			//in tal caso scandisco l'intero vettore delle minuzie per vedere se esiste una minuzia in tale posizione
			for (int j = 0; j < minutiae_table.get_total_number_of_minutiae(); ++j) {
				//cotrollo che la minuzia non sia quella di partenza
				if (reference.get_id() != j) {
					minutia neighbor;
					neighbor.set_minutia(minutiae_table.get_minutia(j));
					//Controllo se neighbor è in posizione [i][j]
					if (neighbor.get_x() == k && neighbor.get_y() == i) {
						flag = true;
						find++;
						outputFile << "    {" << '\n';
						//outputFile << "      \"ID\": " << reference.get_id() << ", " << '\n';
						outputFile << "      \"x1\": " << reference.get_x() << ", " << '\n';
						outputFile << "      \"y1\": " << reference.get_y() << ", " << '\n';
						//outputFile << "      \"ID\": " << neighbor.get_id() << ", " << '\n';
						outputFile << "      \"x2\": " << neighbor.get_x() << ", " << '\n';
						outputFile << "      \"y2\": " << neighbor.get_y() << '\n';
						outputFile << "    },\n";
						//outputFile.close();
					}
					//se trovo esco dal ciclo per controllare le minuzie
					if (flag) {
						break;
					}
				}
			}
			if (!flag) {
				//Non avendo trovato cerco nei pixel adiacenti
				for (int z = -1; z < 2; z++) {
					for (int w = -1; w < 2; w++) {
						if ((i + z) > 0 && (i + z) < matrix_file.get_x() && (k + w) > 0 && (k + w) < matrix_file.get_y()) {
							incoming_minutia_follow_ridges(reference, minutiae_table, matrix_file, matrix_visited, i + z, k + w, max, max_dist, find, dist, outputFile);
						}
					}
				}
			}
		}
	}
}

//Crea nel file l'elenco degli archi che formano il grafo orientato

void fp_edges_table::minutia_follow_direction(int max, int max_dist)
{

	matrix_from_file mx;
	fp_total_minutiae total = fp_total_minutiae();
	std::ofstream outputFile;
	outputFile.open(files::get_instance().get_output_folder() + "/graph_following_direction.json");
	outputFile << "{" << '\n';
	outputFile << "  \"edges\": [" << '\n';
	for (int i = 0; i < total.get_total_number_of_minutiae(); i++) {
		matrix_from_file mv;
		int find = 0;
		minutia_follow_direction(total.get_minutia(i), total, mx, mv, total.get_minutia(i).get_y(), total.get_minutia(i).get_x(), max, max_dist, find, 0, outputFile);
	}
	outputFile << "  ]" << '\n';
	outputFile << "}";
	outputFile.close();
}

void fp_edges_table::minutia_follow_direction(const minutia & reference, fp_total_minutiae & minutiae_table, matrix_from_file & matrix_file, matrix_from_file & matrix_visited, int i, int k, int max, int max_dist, int & find, int dist, std::ofstream & outputFile)
{
	if (find < max && dist < max_dist) {
		//controllo se nell'immagine sono su un ridge che non ho ancora visitato nella DFS (visited!=0)
		if (matrix_file[i][k] == 1 && matrix_visited[i][k] != 0) {
			//controllo se sono arrivato al quinto passo della ricorsione
			bool direction_ok = true;
			if (dist == 5) {
				//Ottengo la direzione che avrei dovuto seguire
				double direction = reference.get_direction();
				direction = (direction * 180) / 3.14;
				//normalize direction and invert in case of bifurcation
				if (reference.get_type()) {
					direction = direction + 180;
				}
				direction = fmod(direction, 360);
				if (direction < 0) {
					direction += 360;
				}
				//Controllo di essermi mosso nella direzione giusta in modo tale da non andare avanti altrimenti
				if (((direction < 30 || direction > 330) && reference.get_x() < k) || ((direction > 150 && direction < 210) && reference.get_x() > k)) {
					direction_ok = false;
				} else if ((direction < 180 && reference.get_y() < i) || (direction > 180 && reference.get_y() > i)) {
					direction_ok = false;
				}
			}
			if (direction_ok) {
				dist++;
				bool flag = false;
				matrix_visited[i][k] = 0;
				//in tal caso scandisco l'intero vettore delle minuzie per vedere se esiste una minuzia in tale posizione
				for (int j = 0; j < minutiae_table.get_total_number_of_minutiae(); ++j) {
					//cotrollo che la minuzia non sia quella di partenza
					if (reference.get_id() != j) {
						minutia neighbor;
						neighbor.set_minutia(minutiae_table.get_minutia(j));
						//Controllo se neighbor è in posizione [i][j]
						if (neighbor.get_x() == k && neighbor.get_y() == i) {
							flag = true;
							find++;
							outputFile << "    {" << '\n';
							outputFile << "      \"x1\": " << reference.get_x() << ", " << '\n';
							outputFile << "      \"y1\": " << reference.get_y() << ", " << '\n';
							outputFile << "      \"x2\": " << neighbor.get_x() << ", " << '\n';
							outputFile << "      \"y2\": " << neighbor.get_y() << '\n';
							outputFile << "    },\n";
						}
						//se trovo esco dal ciclo per controllare le minuzie
						if (flag) {
							break;
						}
					}
				}
				if (!flag) {
					//Non avendo trovato cerco nei pixel adiacenti
					for (int z = -1; z < 2; z++) {
						for (int w = -1; w < 2; w++) {
							if ((i + z) > 0 && (i + z) < matrix_file.get_x() && (k + w) > 0 && (k + w) < matrix_file.get_y()) {
								minutia_follow_direction(reference, minutiae_table, matrix_file, matrix_visited, i + z, k + w, max, max_dist, find, dist, outputFile);
							}
						}
					}
				}
			}
		}
	}
}
