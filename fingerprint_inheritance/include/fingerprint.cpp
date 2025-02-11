/*
 * File:   files_generator.cpp
 * Author: Francesco
 *
 * Created on May 31, 2019, 4:05 PM
 */
#include "fingerprint.h"
#include "files.h"
#include "minutia.h"

//fingerprint::fingerprint()
//{
//	Json::Value minutiae_file;
//	std::ifstream config_doc(files::get_instance().get_minutiae_file(), std::ifstream::binary);
//	config_doc >> minutiae_file;
//	const Json::Value minutiae_json = minutiae_file["minutiae"];
//	m_minutiae_number = minutiae_json.size();
//	for (unsigned short i = 0; i < m_minutiae_number; ++i) {
//		m_minutiae.insert(minutia(minutiae_json, i));
//	}
//
//	change_minutiae_pos();
//}

fingerprint::fingerprint(bool is_BB)
{
	m_is_BB = is_BB;
	m_skeleton.set(m_is_BB);

	if (!m_is_BB) {
		Json::Value minutiae_file;
		std::ifstream config_doc(files::get_instance().get_minutiae_file(), std::ifstream::binary);
		config_doc >> minutiae_file;
		const Json::Value minutiae_json = minutiae_file["minutiae"];
		m_minutiae_number = minutiae_json.size();
		minutia temp_min;
		for (unsigned short i = 0; i < m_minutiae_number; ++i) {
			temp_min = minutia(minutiae_json, i);
			m_minutiae.insert(temp_min);
			m_minutiae_vec.push_back(temp_min);
		}

		change_minutiae_pos(m_minutiae);
	}
}

void fingerprint::change_minutiae_pos(minutiae to_move)
{
	minutiae::const_iterator it_min;
	//minutiae to_add;
	for (it_min = to_move.begin(); it_min != to_move.end();) {
		minutia tmp = *it_min;

		if (!m_skeleton[tmp.get_x()][tmp.get_y()]) {
			it_min = to_move.erase(it_min);
			to_move.insert(it_min, move(tmp));
		} else {
			++it_min;
		}
	}
}

minutia fingerprint::move(minutia to_move)
{
	int x = to_move.get_x();
	int y = to_move.get_y();
	bool find = false;
	int j = 1;
	int new_x = x;
	int new_y = y;
	if (m_skeleton[y][x]) {
		find = true;
	}
	while (!find) {
		for (int i = y - j; i < y + j; i++) {
			for (int z = x - j; z < x + j; z++) {
				if (i >= 0 && i < m_skeleton.get_y() && z < m_skeleton.get_x() && z >= 0) {
					//Controllo se sono in un nero
					if (m_skeleton[i][z]) {
						//Controllo il quadrato 3X3 contenete al centro il punto [i][z]
						int somma = 0;
						for (int a = -1; a < 2; a++) {
							for (int b = -1; b < 2; b++) {
								if (m_skeleton[i + a][z + b]) {
									somma++;
								}
							}
						}
						//controllo che sia una biforcazione
						if (to_move.get_type().compare("bifurcation") == 0 && somma == 4) {
							find = true;
							new_y = i;
							new_x = z;
							break;
						}
						//controllo che sai ending
						if (to_move.get_type().compare("ending") == 0 && somma == 2) {
							find = true;
							new_y = i;
							new_x = z;
							break;
						}
					}
				}
			}
			if (find) {
				break;
			}
		}
		j++;
	}
	if (find) {
		to_move.set_x(new_x);
		to_move.set_y(new_y);
	}

	return to_move;
}

void fingerprint::generate_edges_skeleton_ridges(unsigned short max, unsigned short max_dist)
{
	minutiae::const_iterator it_min;
	for (it_min = m_minutiae.begin(); it_min != m_minutiae.end();) {
		skeleton visited_skeleton;
		unsigned short find = 0;
		generate_edges_skeleton_ridges(*it_min, visited_skeleton, it_min->get_y(), it_min->get_x(), max, max_dist, find, 0);
	}
}

void fingerprint::generate_edges_skeleton_ridges(const minutia & reference, skeleton & visited_skeleton, unsigned short i, unsigned short k, unsigned short max, unsigned short max_dist, unsigned short & find, unsigned short dist)
{
	minutiae::const_iterator it_mi;
	minutia mi_temp;
	if (find < max && dist < max_dist) {
		//controllo se nell'immagine sono su un ridge che non ho ancora visitato nella DFS (visited!=0)
		if (m_skeleton[i][k] == 1 && visited_skeleton[i][k] != 0) {
			dist++;
			bool flag = false;
			visited_skeleton[i][k] = 0;

			if (reference.get_x() != k && reference.get_y() != i) {
				mi_temp.set_x(k);
				mi_temp.set_y(i);

				it_mi = m_minutiae.find(mi_temp);

				if (it_mi != m_minutiae.end()) {
					flag = true;
					find++;
					m_edges.push_back(edge(reference, *it_mi));
				}
				//se trovo esco dal ciclo per controllare le minuzie
				//					if (flag) {
				//						break;
				//					}
			}


			if (!flag) {
				//Non avendo trovato cerco nei pixel adiacenti
				for (int z = -1; z < 2; z++) {
					for (int w = -1; w < 2; w++) {
						if ((i + z) > 0 && (i + z) < m_skeleton.get_x() && (k + w) > 0 && (k + w) < m_skeleton.get_y()) {

							generate_edges_skeleton_ridges(reference, visited_skeleton, i + z, k + w, max, max_dist, find, dist);
						}
					}
				}
			}
		}
	}
}

void fingerprint::generate_edges_skeleton_ridges_DFS(bool java_BB)
{
	unsigned short minutia_counter = 0;
	if (m_is_BB) {
		std::vector<my_point> my_point_min;
		if (!java_BB) {
			my_point_min = m_skeleton.create_eroded_skeleton();
		} else {
			my_point_min = m_skeleton.read_eroded_skeleton();
		}
		std::vector<my_point>::const_iterator it_mp;
		for (it_mp = my_point_min.begin(); it_mp != my_point_min.end(); it_mp++) {
			m_minutiae.insert(minutia(minutia_counter, it_mp->get_x(), it_mp->get_y(), 0.0, "edge"));
			minutia_counter++;
		}
	}

	//minutia_counter = 0;
	//int counter_min= 0;

	Json::Value minutiae_file;
	std::ifstream config_doc(files::get_instance().get_minutiae_internalBB_file(), std::ifstream::binary);
	config_doc >> minutiae_file;
	const Json::Value minutiae_json = minutiae_file["minutiae"];
	minutia temp_min;
	for (unsigned short i = 0; i < minutiae_json.size(); ++i) {
		temp_min = minutia(minutiae_json, i, i + minutia_counter, "internal");
		if (m_skeleton[temp_min.get_y()][temp_min.get_x()]) {
			//			counter_min++;
			m_minutiae_plus_internal.insert(temp_min);
		}
	}
	//change_minutiae_pos(m_minutiae_plus_internal);

	minutiae::const_iterator it_min;
	for (it_min = m_minutiae.begin(); it_min != m_minutiae.end(); it_min++) {
		//	counter_min++;
		m_minutiae_plus_internal.insert(*it_min);
	}

	//	std::cout << "Counter Minutiae: " << counter_min << std::endl;

	skeleton visited_skeleton = m_skeleton;
	//visited_skeleton.set(true);
	for (int skeleton_y = 0; skeleton_y < visited_skeleton.get_y(); skeleton_y++) {
		for (int skeleton_x = 0; skeleton_x < visited_skeleton.get_x(); skeleton_x++) {
			visited_skeleton[skeleton_y][skeleton_x] = 0;
		}
	}

	int repetitions = m_minutiae_plus_internal.size();
	bool found = false;
	minutiae new_minutiae;
	for (it_min = m_minutiae.begin(); it_min != m_minutiae.end(); it_min++) {
		generate_edges_skeleton_ridges_DFS(*it_min, visited_skeleton, found, repetitions, new_minutiae);
		found = false;
	}

	/*if (!java_BB) {
		generate_edges_bb_DFS();
		std::cerr << "\n\nDEBUG:Here";
	}*/


	for (it_min = new_minutiae.begin(); it_min != new_minutiae.end(); it_min++) {
		//	counter_min++;
		m_minutiae_plus_internal.insert(*it_min);
	}


	//clockwise_sort_minutiae_easy();
	print_typed_minutiae("/internal_minutiae.json", "internal");
	print_typed_minutiae("/edge_minutiae.json", "edge");


}

void fingerprint::generate_edges_skeleton_ridges_DFS(const minutia & to_check, skeleton & visited_skeleton, bool & found, int & repetitions, minutiae & new_minutiae)
{

	int x = to_check.get_x();
	int y = to_check.get_y();

	minutia to_add;
	minutia mi_temp;
	std::forward_list<my_point> neighbors, diramations_points;

	bool no_neighbor = false;
	//if (m_skeleton[x][y] == 1){
	while (!no_neighbor) {

		if (visited_skeleton[y][x] == 0) {

			//counter1++;
			visited_skeleton[y][x] = 1;

			neighbors = get_neighbors(x, y, visited_skeleton);
			if (neighbors.empty()) {
				//counter2++;
				mi_temp.set_x(x);
				mi_temp.set_y(y);

				if (is_minutia(mi_temp)) {
					if (!(to_check.get_x() == mi_temp.get_x() && to_check.get_y() == mi_temp.get_y())) {

						to_add = to_check;

						if (!found) {
							m_edges.push_back(edge(to_add, mi_temp));
							found = true;
						} else {

							//This line change the repetitions ID
							//to_add.set_id(repetitions++);
							new_minutiae.insert(to_add);
							m_edges.push_back(edge(to_add, mi_temp));
						}
						/*if (to_check.get_id() == 1 || to_check.get_id() == 0 || to_check.get_id() == 7) {
							std::cerr << "Added edge between " << to_check.get_id() << " and " << mi_temp.get_id() << "\n";
						} else if (mi_temp.get_id() == 1 || mi_temp.get_id() == 0 || mi_temp.get_id() == 7) {
							std::cerr << "Added edge between " << to_check.get_id() << " and " << mi_temp.get_id() << "\n";
						}*/
						//std::cerr << "\n\n\ncID: " << to_check.get_id() << " cX: " << to_check.get_x() << " cY: " << to_check.get_y() << std::endl;
						//std::cerr << "iID: " << mi_temp.get_id() << " iX: " << mi_temp.get_x() << " iY: " << mi_temp.get_y() << std::endl;
					}
				}
				if (diramations_points.empty()) {
					no_neighbor = true;

				} else {
					x = diramations_points.front().get_x();
					y = diramations_points.front().get_y();
					diramations_points.pop_front();
					//std::cout << "\nDEBUG: Backtracking\n";
				}
			} else {



				x = neighbors.front().get_x();
				y = neighbors.front().get_y();
				neighbors.pop_front();

				diramations_points.splice_after(diramations_points.before_begin(), neighbors);


				neighbors.clear();


			}
		} else {
			if (!diramations_points.empty()) {
				x = diramations_points.front().get_x();
				y = diramations_points.front().get_y();
				diramations_points.pop_front();
				//std::cout << "\nDEBUG: Backtracking\n";
			} else {
				no_neighbor = true;
			}
		}

	}


}

void fingerprint::generate_edges_bb_DFS()
{
	//std::cerr << "\nDEBUG: Inside edge_bb DFS" << std::endl;
	bool found_right = false, found_left = false;
	minutia max_right;
	minutia max_left;
	minutiae to_visit_minutiae = m_minutiae;

	minutia max_y = *m_minutiae.begin();
	minutiae::const_iterator it_min;
	for (it_min = m_minutiae.begin(); it_min != m_minutiae.end(); it_min++) {
		if (max_y.get_y() > it_min->get_y()) {
			max_y = *it_min;
		}

	}
	to_visit_minutiae.erase(max_y);


	for (it_min = to_visit_minutiae.begin(); it_min != to_visit_minutiae.end(); it_min++) {
		if (it_min->get_x() < max_y.get_x()) {
			if (!found_left) {
				max_left = *it_min;
				found_left = true;
			} else if (max_left.get_y() > it_min->get_y()) {
				max_left = *it_min;
			}
		} else {
			if (!found_right) {
				max_right = *it_min;
				found_right = true;
			} else if (max_right.get_y() > it_min->get_y()) {
				max_right = *it_min;
			}
		}

	}
	minutia ending_left, ending_right;
	if (found_right) {
		to_visit_minutiae.erase(max_right);
		m_edges.push_back(edge(max_right, max_y));
		generate_edges_bb_DFS(max_right, to_visit_minutiae, false, max_y.get_x(), ending_right);
	}
	if (found_left) {
		to_visit_minutiae.erase(max_left);
		m_edges.push_back(edge(max_left, max_y));
		generate_edges_bb_DFS(max_left, to_visit_minutiae, true, max_y.get_x(), ending_left);
	}
	m_edges.push_back(edge(ending_left, ending_right));


}

void fingerprint::generate_edges_bb_DFS(const minutia & start, minutiae & to_visit_minutiae, bool is_left, unsigned short max_x, minutia & ending)
{
	//std::cerr << "\nDEBUG: Inside edge_bb Nested DFS" << std::endl;

	bool found = false;
	minutiae::const_iterator it_min;
	minutia local_max; // = *to_visit_minutiae.begin();
	for (it_min = to_visit_minutiae.begin(); it_min != to_visit_minutiae.end(); it_min++) {
		if (is_left) {
			if (it_min->get_x() < max_x) {
				if (!found) {
					local_max = *it_min;
					found = true;
				} else if (local_max.get_y() > it_min->get_y()) {
					local_max = *it_min;
				}
			}
		} else {
			if (it_min->get_x() >= max_x) {
				if (!found) {
					local_max = *it_min;
					found = true;
				} else if (local_max.get_y() > it_min->get_y()) {
					local_max = *it_min;
				}
			}
		}
	}

	if (found) {
		to_visit_minutiae.erase(local_max);
		m_edges.push_back(edge(local_max, start));
		generate_edges_bb_DFS(local_max, to_visit_minutiae, is_left, max_x, ending);
	} else {
		ending = start;
	}
}

// per assegnare valore a edge::real_length

void fingerprint::generate_edges_distance()
{
	std::ofstream bitmapFile;
	bitmapFile.open(files::get_instance().get_output_folder() + "/ridges.json");
	bitmapFile << "{\n  \"ridges\": [\n";

	for (unsigned short i = 0; i < m_edges.size(); i++) {
		if (m_edges[i].put_real_length(m_skeleton, bitmapFile)) {

			if (i < m_edges.size() - 1) {
				bitmapFile << ",";
			}
			bitmapFile << "\n";
		}
	}

	bitmapFile << "  ]\n}";

	bitmapFile.close();

}

std::forward_list<my_point> fingerprint::get_neighbors(int x, int y, skeleton & visited_skeleton)
{
	std::forward_list<my_point> ret;

	//int x = mp.get_x();
	//int y = mp.get_y();
	//std::cout << "\nMinutia X: " << x << " Y: " << y <<"\n";

	for (int k = -1; k < 2; k++) {
		for (int h = -1; h < 2; h++) {
			if ((x + h) >= 0 && (x + h) < m_skeleton.get_x() && (y + k) >= 0 && (y + k) < m_skeleton.get_y()) {
				if (m_skeleton[y + k][x + h] && !visited_skeleton[y + k][x + h]) {
					//std::cout << "\nDEBUG: Found One\n";
					ret.push_front(my_point(x + h, y + k));
				}
			}
		}
	}
	return ret;

}

bool fingerprint::is_minutia(minutia & mi)
{
	//Funziona perchè l'operatore < delle minuzie è basato su x e y
	minutiae::const_iterator it_mi;
	for (it_mi = m_minutiae_plus_internal.begin(); it_mi != m_minutiae_plus_internal.end(); it_mi++) {
		if (it_mi->get_x() > mi.get_x()) {
			return false;
		} else if (it_mi->get_x() == mi.get_x()) {
			if (it_mi->get_y() > mi.get_y()) {
				return false;
			} else if (it_mi->get_y() == mi.get_y()) {
				mi = *it_mi;
				return true;
			}
		}

	}
	return false;
}

/*bool fingerprint::is_minutia(minutia & mi)
{
	//Funziona perchè l'operatore < delle minuzie è basato su x e y
	minutiae::const_iterator it_mi;
	it_mi = m_minutiae_plus_internal.find(mi);
	if (it_mi != m_minutiae_plus_internal.end()) {
		mi = *it_mi;
		return true;
	}
	return false;
}*/


void fingerprint::print_json_edges(bool is_BB) const
{
	std::ofstream outputFile;
	outputFile.open(files::get_instance().get_output_folder() + "/graph_c++.json");
	outputFile << "{" << '\n';
	outputFile << "  \"edges\": [" << '\n';
	for (unsigned short i = 0; i < m_edges.size(); i++) {

		outputFile << "    {" << '\n';
		outputFile << "      \"x1\": " << m_edges[i].get_reference_minutia().get_x() << ", " << '\n';
		outputFile << "      \"y1\": " << m_edges[i].get_reference_minutia().get_y() << ", " << '\n';
		outputFile << "      \"x2\": " << m_edges[i].get_neighbor_minutia().get_x() << ", " << '\n';
		outputFile << "      \"y2\": " << m_edges[i].get_neighbor_minutia().get_y() << ", " << '\n';
		outputFile << "      \"ID1\": " << m_edges[i].get_reference_minutia().get_id() << ", " << '\n';
		outputFile << "      \"ID2\": " << m_edges[i].get_neighbor_minutia().get_id() << ", " << '\n';

		if (is_BB) {
			if ((m_edges[i].get_neighbor_minutia().get_type().compare("internal") == 0)) {
				outputFile << "      \"type\": \"internal\" \n";
			} else {
				outputFile << "      \"type\": \"edge\" \n";
			}
		}
		outputFile << "    },\n";
	}
	outputFile << "  ]" << '\n';
	outputFile << "}";
	outputFile.close();
}

void fingerprint::print_json_minutiae() const
{

	print_minutiae(m_minutiae, "/updated_minutiae.json", false);

}

void fingerprint::print_minutiae_internal() const
{
	print_minutiae(m_minutiae_plus_internal, "/internal_minutiae.json", true);
}

void fingerprint::print_minutiae(minutiae to_print, std::string name, bool is_internal) const
{

	std::ofstream outputFile;
	outputFile.open(files::get_instance().get_output_folder() + name);
	outputFile << "{" << '\n';
	outputFile << "  \"width\": " << m_skeleton.get_y() << "," << '\n';
	outputFile << "  \"height\": " << m_skeleton.get_x() << "," << '\n';
	outputFile << "  \"minutiae\": [" << '\n';
	minutiae::const_iterator it_min;
	for (it_min = to_print.begin(); it_min != to_print.end(); it_min++) {
		outputFile << "    {" << '\n';
		outputFile << "      \"ID\": " << it_min->get_id() << ", " << '\n';
		outputFile << "      \"x\": " << it_min->get_x() << ", " << '\n';
		outputFile << "      \"y\": " << it_min->get_y() << ", " << '\n';
		outputFile << "      \"direction\": " << it_min->get_direction() << ", " << '\n';
		outputFile << "      \"type\": ";
		if (is_internal) {
			outputFile << "\"internal\"";
		} else if (!it_min->get_type().compare("bifurcation")) {
			outputFile << "\"bifurcation\"";
		} else {
			outputFile << "\"ending\"";
		}
		outputFile << '\n';
		outputFile << "    }";
		if (std::next(it_min, 1) != to_print.end()) {
			outputFile << ",";
		}
		outputFile << '\n';

	}
	outputFile << "  ]\n}";
	outputFile.close();
}

void fingerprint::print_typed_minutiae(std::string name, std::string type) const
{
	bool printed = false;
	std::ofstream outputFile;
	outputFile.open(files::get_instance().get_output_folder() + name);
	outputFile << "{" << '\n';
	outputFile << "  \"width\": " << m_skeleton.get_y() << "," << '\n';
	outputFile << "  \"height\": " << m_skeleton.get_x() << "," << '\n';
	outputFile << "  \"minutiae\": [" << '\n';
	minutiae::const_iterator it_min;
	for (it_min = m_minutiae_plus_internal.begin(); it_min != m_minutiae_plus_internal.end(); it_min++) {
		if (it_min->get_type().compare(type) == 0) {
			outputFile << "    {" << '\n';
			outputFile << "      \"ID\": " << it_min->get_id() << ", " << '\n';
			outputFile << "      \"x\": " << it_min->get_x() << ", " << '\n';
			outputFile << "      \"y\": " << it_min->get_y() << ", " << '\n';
			outputFile << "      \"direction\": " << it_min->get_direction() << ", " << '\n';
			outputFile << "      \"type\": \"" << it_min->get_type() << "\"" << '\n';
			outputFile << "    }";
			printed = true;
		}
		if (printed && std::next(it_min, 1) != m_minutiae_plus_internal.end() && std::next(it_min, 1)->get_type().compare(type) == 0) {
			outputFile << ",\n";
			printed = false;
		} else if (std::next(it_min, 1) == m_minutiae_plus_internal.end()) {
			outputFile << '\n';
		}

	}
	outputFile << "  ]\n}";
	outputFile.close();
}

void fingerprint::print_asp() const
{
	std::ofstream outputFile;
	std::string fp_name = files::get_instance().get_fp_name();
	//std::cerr << "\nDEBUG: fp_name->" << fp_name;

	outputFile.open(files::get_instance().get_output_folder() + "/" + fp_name + ".asp");
	outputFile << "%%%% Minutiae %%%%\n";

	minutiae::const_iterator it_min;
	bool exist = false;

	for (it_min = m_minutiae_plus_internal.begin(); it_min != m_minutiae_plus_internal.end(); ++it_min) {
		exist = false;
		for (unsigned short i = 0; i < m_edges.size(); i++)
			if (m_edges[i].get_reference_minutia().get_id() == it_min->get_id() || m_edges[i].get_neighbor_minutia().get_id() == it_min->get_id())
			{
				exist = true;
				break;
			}
		if (exist)
		{
			outputFile << "node(";
			outputFile << fp_name << ", ";
			outputFile << it_min->get_type() << ", ";
			// Utilizzavo la direzione per distinguere le minuzione interne dalle minuzie sul perimetro
			/*direction=it_min->get_direction();
			if (direction > 0.0) {
				outputFile << "internal, ";
			} else if (direction == 0.0) {
				outputFile << "edge, ";
			} else outputFile << direction << ", ";*/

			// Utilizzavo la posizione come ID univoco visto che getID ritorna 0.
			outputFile << it_min->get_id();

			// Controllo posizione spaziale nodi
			outputFile << ", " << it_min->get_x() << ", " << it_min->get_y();

			outputFile << ").\n";
		}
	}
	outputFile << std::endl << std::endl;

	outputFile << "%%%% Edges %%%%\n";
	for (unsigned short i = 0; i < m_edges.size(); i++) {
		outputFile << "\nedge(";
		outputFile << fp_name << ", ";
		outputFile << m_edges[i].get_reference_minutia().get_id() << ", ";
		outputFile << m_edges[i].get_neighbor_minutia().get_id() << ", ";
		if (m_edges[i].get_reference_minutia().get_type().compare("internal") == 0 || m_edges[i].get_neighbor_minutia().get_type().compare("internal") == 0) {
			outputFile << "internal";
		} else {
			outputFile << "edge";
		}
		// angoli
		outputFile << ", " << (int) m_edges[i].get_reference_angle();

		// distanza
		outputFile << ", " << m_edges[i].get_length();
		outputFile << ", " << (int) (m_edges[i].get_triangle().get_ratio() * 100);
		outputFile << ").";
	}
	outputFile.close();
}

void fingerprint::clockwise_sort_minutiae_easy()
{

	std::set<minutia> minutiaes = m_minutiae_plus_internal;
	std::set<minutia>::iterator it_min;
	minutiaes.clear();

	//punto di taglio verticale impronta
	unsigned short maxY = 0;
	unsigned short taglioX;
	for (it_min = m_minutiae_plus_internal.begin(); it_min != m_minutiae_plus_internal.end(); ++it_min) {
		if (it_min->get_type().compare("edge") == 0 && it_min->get_y() > maxY) {
			maxY = it_min->get_y();
			taglioX = it_min->get_x();
		}
	}
	unsigned short newID = 0;
	for (it_min = m_minutiae_plus_internal.begin(); it_min != m_minutiae_plus_internal.end(); ++it_min) {
		if (it_min->get_y() == maxY && it_min->get_x() == taglioX) {
			minutia M = (*it_min);
			M.set_id(newID++);
			minutiaes.insert(M);
		}
	}

	//ordino parte destra
	unsigned short yupper = maxY;
	unsigned short ylower = 0;
	unsigned short x;
	unsigned int fine = 0;
	while (fine++ < m_minutiae_plus_internal.size()) {

		for (it_min = m_minutiae_plus_internal.begin(); it_min != m_minutiae_plus_internal.end(); ++it_min) {
			if (it_min->get_type().compare("edge") == 0 && it_min->get_x() > taglioX)
				if (it_min->get_y() > ylower && it_min->get_y() < yupper) {
					ylower = it_min->get_y();
				}
		}
		for (it_min = m_minutiae_plus_internal.begin(); it_min != m_minutiae_plus_internal.end(); ++it_min) {
			if (it_min->get_y() == ylower) {
				minutia M = (*it_min);
				M.set_id(newID++);
				minutiaes.insert(M);
				//	std::cerr << "inserito " <<newID<<" fine:"<<fine<<std::endl;
			}
		}
		yupper = ylower;
		ylower = 0;
	}

	//ordino parte sinistra
	yupper = maxY;
	ylower = maxY;
	for (it_min = m_minutiae_plus_internal.begin(); it_min != m_minutiae_plus_internal.end(); ++it_min) {
		if (it_min->get_type().compare("edge") == 0 && it_min->get_x() <= taglioX) {
			if (it_min->get_y() < ylower) {
				ylower = it_min->get_y();
				x = it_min->get_x();
			}
		}
	}
	for (it_min = m_minutiae_plus_internal.begin(); it_min != m_minutiae_plus_internal.end(); ++it_min) {
		if (it_min->get_y() == ylower && it_min->get_x() == x) {
			minutia M = (*it_min);
			M.set_id(newID++);
			minutiaes.insert(M);
		}
	}

	while (fine-- != 0) {
		for (it_min = m_minutiae_plus_internal.begin(); it_min != m_minutiae_plus_internal.end(); ++it_min) {
			if (it_min->get_type().compare("edge") == 0 && it_min->get_x() < taglioX)
				if (it_min->get_y() > ylower && it_min->get_y() < yupper) {
					yupper = it_min->get_y();
				}
		}
		for (it_min = m_minutiae_plus_internal.begin(); it_min != m_minutiae_plus_internal.end(); ++it_min) {
			if (it_min->get_y() == yupper) {
				minutia M = (*it_min);
				M.set_id(newID++);
				minutiaes.insert(M);
				//	std::cerr << "inserito " <<newID<<std::endl;
			}
		}
		ylower = yupper;
		yupper = maxY;
	}

	for (it_min = m_minutiae_plus_internal.begin(); it_min != m_minutiae_plus_internal.end(); ++it_min) {
		if (it_min->get_type().compare("internal") == 0) {
			minutia M = (*it_min);
			M.set_id(newID++);
			minutiaes.insert(M);
		}
	}

	m_minutiae_plus_internal.clear();
	for (it_min = minutiaes.begin(); it_min != minutiaes.end(); ++it_min) {
		minutia M = (*it_min);
		m_minutiae_plus_internal.insert(M);
	}
}

void fingerprint::clockwise_sort_minutiae()
{

	minutiae::const_iterator it_min;
	std::vector<minutia> edge_min;


	// Creo vettore con tutte e sole minuzie egde
	for (it_min = m_minutiae_plus_internal.begin(); it_min != m_minutiae_plus_internal.end(); ++it_min)
		if (it_min->get_type().compare("edge") == 0)
			edge_min.push_back(*it_min);
	// Trovo la coordinata con y piu' alta per separare l'impronta in due
	int MAX = 0, test, divisore;
	for (std::vector<minutia>::iterator iter_min0 = edge_min.begin(); iter_min0 != edge_min.end(); ++iter_min0) {
		test = iter_min0->get_y();
		if (test > MAX) {
			MAX = test;
			divisore = iter_min0->get_x();
		}
	}

	// Divido in due vettori le minuzie egde in base se si trovano a sinistra o destra rispetto al separatore
	std::vector<minutia> destro, sinistro;
	std::vector<minutia>::iterator iter_min;
	for (iter_min = edge_min.begin(); iter_min != edge_min.end(); ++iter_min) {
		if (divisore > iter_min->get_x())
			sinistro.push_back(*iter_min);
		else
			destro.push_back(*iter_min);
	}

	// Ordino i vettori i base alle coordinate y, e scambio gli id -> nodi ordinati in ordine orario
	int fine = sinistro.size();
	unsigned short ID = 0, MAX_ID = 0;
	for (unsigned short j = 0; j < fine; j++) {
		for (unsigned short i = 0; i < fine; i++)
			if (j != i && sinistro[j].get_y() > sinistro[i].get_y()) ID++;
		sinistro[j].set_id(ID);
		if (ID > MAX_ID) MAX_ID = ID;
		ID = 0;
	}

	ID = 0;
	fine = destro.size();
	for (unsigned short j = 0; j < fine; j++) {
		for (unsigned short i = 0; i < fine; i++)
			if (j != i && destro[j].get_y() < destro[i].get_y()) ID++;
		ID += MAX_ID + 1;
		destro[j].set_id(ID);
		ID = 0;
	}

	// Memorizzo il nuovo vettore ordinato in m_minutiae_plus_internal
	for (it_min = m_minutiae_plus_internal.begin(); it_min != m_minutiae_plus_internal.end(); ++it_min)
		if (it_min->get_type().compare("edge") != 0)
			sinistro.push_back(*it_min);

	for (iter_min = destro.begin(); iter_min != destro.end(); ++iter_min)
		sinistro.push_back(*iter_min);

	m_minutiae_plus_internal.clear();

	for (iter_min = sinistro.begin(); iter_min != sinistro.end(); ++iter_min)
		m_minutiae_plus_internal.insert(*iter_min);
}

void fingerprint::read_afis_edges()
{
	Json::Value edge_file;
	std::ifstream config_doc(files::get_instance().get_input_folder() + "077-edge-table.json", std::ifstream::binary);
	config_doc >> edge_file;

	//std::cout << "Edges file number: " << edge_file.size();
	for (unsigned short i = 0; i < edge_file.size(); ++i) {
		const Json::Value edges_json = edge_file[i];
		for (unsigned short j = 0; j < edges_json.size(); ++j) {
			const Json::Value edges_json_internal = edges_json[j];
			//	std::cout << "\nEdge between " << i << " and " << edges_json_internal["neighbor"].asInt();
			m_edges.push_back(edge(m_minutiae_vec[i], m_minutiae_vec[edges_json_internal["neighbor"].asInt()]));
		}
	}
}

void fingerprint::generate_triangulation()
{
	m_skeleton.ridges_triangulation();
}
