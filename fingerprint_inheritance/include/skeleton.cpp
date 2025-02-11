#include "skeleton.h"
#include "files.h"
#include <fstream>
#include <iostream>
#include<bits/stdc++.h>

my_point::my_point(unsigned short x, unsigned short y)
{

	set_x(x);
	set_y(y);
}

void my_point::set_x(unsigned short x)
{
	m_x = x;
}

void my_point::set_y(unsigned short y)
{
	m_y = y;
}

unsigned short my_point::get_x() const
{
	return m_x;
}

unsigned short my_point::get_y() const
{
	return m_y;
}

bool my_point::operator<(const my_point& to_check) const
{
	if (get_x() == to_check.get_x())
	{
		return get_y() < to_check.get_y();
	}
	return get_x() < to_check.get_x();

}

bool my_point::operator=(const my_point& to_set)
{
	set_x(to_set.get_x());
	set_y(to_set.get_y());
	return true;

}

bool my_point::operator==(const my_point& to_check) const
{
	if (get_x() == to_check.get_x() && get_y() == to_check.get_y())
		return true;
	return false;
}

skeleton::skeleton()
{
	// std::cerr << "Costruttore_from_file(1)\n";
	Json::Value dim_file;
	std::ifstream config_doc(files::get_instance().get_skeleton_description(), std::ifstream::binary);

	config_doc >> dim_file;

	const Json::Value dimension = dim_file["dimensions"];

	m_x = dimension[1].asInt();
	m_y = dimension[0].asInt();
	m_ridge = ridge(m_x, m_y);
	//set_from_file();

}

void skeleton::set(bool is_BB)
{
	if (!is_BB) {
		set_from_file();
	}
}

const boolean_matrix & skeleton::get_data() const
{
	return m_data;
}

const boolean_matrix & skeleton::get_data_filled() const
{
	return m_data_filled;
}

unsigned short skeleton::get_x() const
{
	return m_x;
}

unsigned short skeleton::get_y() const
{
	return m_y;
}

void skeleton::set_from_file()
{
	//std::ofstream myfile;
	//myfile.open ("skeleton_ini.txt");

	my_row row;
	std::ifstream fin;
	fin.open(files::get_instance().get_skeleton_data());
	if (fin.is_open()) {
		for (int i = 0; i < m_y; i++) {
			row.clear();
			for (int j = 0; j < m_x; j++) {
				//int temp = fin.get();
				//if( temp > 0){
				row.push_back(fin.get());

				//}
				//else{
				//row.push_back(false);
				//
				//				}
			}
			//my_row::const_iterator it_row;
			//std::cout << "DEBUG: row = ";
			//			for(it_row = row.begin(); it_row != row.end(); ++it_row)
			//			{
			//				std::cout << *it_row;
			//			}
			//			std::cout << "\n";
			m_data.push_back(row);
		}
	}

	fin.close();


	//	std::ifstream fin2;
	//	fin2.open(files::get_instance().get_skeleton_data());
	//		if (fin2.is_open()) {
	//			for (int i = 0; i < m_y; i++) {
	//				for (int j = 0; j < m_x; j++) {
	//					int temp = fin2.get();
	//					myfile << temp;
	//				}
	//				myfile << "\n";
	//			}
	//		}
	//
	//	fin2.close();
	//	myfile.close();
}

std::vector<my_point> skeleton::create_eroded_skeleton()
{
	my_row row;
	int first, last;
	std::ifstream fin;
	fin.open(files::get_instance().get_skeleton_data());
	if (fin.is_open()) {
		for (int i = 0; i < m_y; i++) {
			row.clear();
			first = 0;
			last = 0;
			for (int j = 0; j < m_x; j++) {

				int temp = fin.get();
				row.push_back(temp);

				if (temp > 0) {
					if (first == 0) {
						first = j;
					}
					last = j;
				}
			}

			m_data.push_back(row);

			for (int fill = first; fill <= last; fill++) {
				row[fill] = 1;
				if (i > 1) {
					m_data_filled[i - 2][fill] = 1;
					if (fill > 0) {
						m_data_filled[i - 2][fill - 1] = 1;
					}
					if (fill < m_x) {
						m_data_filled[i - 2][fill + 1] = 1;
					}
				} else if (i > 0) {
					m_data_filled[i - 1][fill] = 1;
					if (fill > 0) {
						m_data_filled[i - 1][fill - 1] = 1;
					}
					if (fill < m_x) {
						m_data_filled[i - 1][fill + 1] = 1;
					}
				}

			}
			m_data_filled.push_back(row);
		}
	}

	fin.close();

	boolean_matrix eroded = m_data_filled;
	for (int k = 0; k < files::get_instance().get_erosion_time(); ++k) {
		for (int i = 0; i < m_y; i++) {
			for (int j = 0; j < m_x; j++) {
				if (has_null_neigh(j, i)) {
					eroded[i][j] = 0;
				}
			}
		}
		m_data_filled = eroded;
	}

	std::ofstream outputFile;
	std::vector<my_point> edge_minutiae;
	outputFile.open(files::get_instance().get_output_folder() + "/059B-valleys-thinned-skeleton-bounded.txt");
	//myfile.open("");
	for (int i = 0; i < m_y; i++) {
		for (int j = 0; j < m_x; j++) {
			//int temp = fin2.get();
			if (m_data[i][j] && m_data_filled[i][j]) {
				outputFile << "1";
				m_data[i][j] = 1;
				if (has_all_but_one_null_neigh(j, i)) {
					edge_minutiae.push_back(my_point(j, i));
				}
			} else {
				outputFile << "0";
				m_data[i][j] = 0;
			}
		}
		//		if (i != m_y -1) {
		//			outputFile << "\n";
		//		}
	}

	outputFile.close();
	//print_edge_minutiae(edge_minutiae);
	return edge_minutiae;
	//print_edge_minutiae(edge_minutiae);
}

std::vector<my_point> skeleton::read_eroded_skeleton()
{
	my_row row;
	std::ifstream fin;
	fin.open(files::get_instance().get_skeleton_data());
	if (fin.is_open()) {
		for (int i = 0; i < m_y; i++) {
			row.clear();
			for (int j = 0; j < m_x; j++) {
//				char temp =(char) fin.get();
//				int value = temp - '0';
				row.push_back(fin.get()-48);
			}
			//std::cout << std::endl;
			m_data.push_back(row);
		}

		fin.close();
	}


	std::vector<my_point> edge_minutiae;

	Json::Value minutiae_file;
	std::ifstream config_doc(files::get_instance().get_minutiae_file(), std::ifstream::binary);
	config_doc >> minutiae_file;
	const Json::Value minutiae_json = minutiae_file["minutiae"];

	for (unsigned short i = 0; i < minutiae_json.size(); ++i) {
		edge_minutiae.push_back(my_point(minutiae_json[i]["x"].asInt(), minutiae_json[i]["y"].asInt()));
	}

	//print_edge_minutiae(edge_minutiae);
	return edge_minutiae;
}

void skeleton::print_edge_minutiae(std::vector<my_point> edge_minutiae) const
{

	std::ofstream outputFile;
	outputFile.open(files::get_instance().get_output_folder() + "/edge_minutiae.json");
	outputFile << "{" << '\n';
	outputFile << "  \"width\": " << m_x << "," << '\n';
	outputFile << "  \"height\": " << m_y << "," << '\n';
	outputFile << "  \"minutiae\": [" << '\n';
	std::vector<my_point>::const_iterator it_min;
	for (it_min = edge_minutiae.begin(); it_min != edge_minutiae.end(); it_min++) {
		outputFile << "    {" << '\n';
		//outputFile << "      \"ID\": " << it_min->get_id() << ", " << '\n';
		outputFile << "      \"x\": " << it_min->get_x() << ", " << '\n';
		outputFile << "      \"y\": " << it_min->get_y() << ", " << '\n';
		outputFile << "      \"direction\": 0.0, " << '\n';
		outputFile << "      \"type\": ";
		outputFile << "\"edge\"";
		outputFile << '\n';
		outputFile << "    }";
		if (std::next(it_min, 1) != edge_minutiae.end()) {
			outputFile << ",";
		}
		outputFile << "\n";
	}
	outputFile << "  ]\n}";
	outputFile.close();
}

bool skeleton::has_null_neigh(int x, int y)
{
	for (int k = -1; k < 2; k++) {
		for (int h = -1; h < 2; h++) {
			if ((x + h) >= 0 && (x + h) < m_x && (y + k) >= 0 && (y + k) < m_y) {
				if (m_data_filled[y + k][x + h] == 0) {
					return true;
				}
			}
		}
	}
	return false;
}

bool skeleton::has_all_but_one_null_neigh(int x, int y)
{
	int counter = 0;
	bool side = false;
	for (int k = -1; k < 2; k++) {
		for (int h = -1; h < 2; h++) {
			if ((x + h) >= 0 && (x + h) < m_x && (y + k) >= 0 && (y + k) < m_y) {
				counter += (m_data[y + k][x + h] && m_data_filled[y + k][x + h]);
				if (m_data_filled[y + k][x + h] == 0) {
					side = true;
				}
			}
		}
	}
	//Himself and another
	//1 not good enough (just a point)
	if (counter == 2 && side) {
		return true;
	}
	return false;
}

my_row& skeleton::operator[](unsigned short i)
{
	if (i >= 0 && i <= m_data.size())
		return m_data[i];

	std::cerr << "\nExceeded matrix boundaries\n";
	exit(1);
}

bool skeleton::operator=(const skeleton & to_set)
{
	m_x = to_set.get_x();
	m_y = to_set.get_y();

	my_row row;

	for (int i = 0; i < m_y; i++) {
		for (int j = 0; j < m_x; j++) {
			row.push_back(to_set.get_data()[i][j]);
		}
		m_data.push_back(row);
	}

	return true;
}

void skeleton::ridges_triangulation() {
	m_ridge.read_all_ridges();
	m_ridge.choose_ridges_points();
	m_ridge.create_matrix();

	m_ridge.find_triangles_points();

	//m_ridge.print_matrix();
	m_ridge.print_matrix_on_file();

	//m_ridge.print_triangles_points();
	m_ridge.print_triangles_points_json();
}

ridge::ridge() {}

// x e y sono scambiate
ridge::ridge(int x, int y) {
	max_x = y;
	max_y = x;
}

void ridge::read_all_ridges() {
	Json::Value file;
	std::ifstream config_doc(files::get_instance().get_input_folder() + "/inheritance/ridges.json", std::ifstream::binary);
	config_doc >> file;

	std::vector<my_point> temp;
	const Json::Value ridges = file["ridges"];
	for (unsigned short i = 0; i < ridges.size(); ++ i) {
		temp.clear();
		const Json::Value ridge = ridges[i];
		const Json::Value pixels = ridge["pixels"];
		for (unsigned short j = 0; j < pixels.size(); ++ j) {
			const Json::Value pixel = pixels[j];
			my_point point = my_point(pixel["X"].asInt(), pixel["Y"].asInt());
			temp.push_back(point);
		}
		all_ridges_points.push_back(temp);
	}
}

void ridge::choose_ridges_points() {
	// k è il numero di punti del ridge da saltare (+1) a ogni step
	int k = 7;

	if (all_ridges_points.empty())
		return;

	std::vector<my_point> temp;
	for (unsigned short i = 0; i < all_ridges_points.size(); ++ i) {
		temp.clear();
		for (unsigned short j = 0; j < all_ridges_points[i].size(); ++ j) {
			int pos = j*k;
			if (pos >= (int) all_ridges_points[i].size())
				break;
			temp.push_back(all_ridges_points[i][pos]);
		}
		ridges_points.push_back(temp);
	}

	// rimuove ridge con un solo punto: non possono formare triangoli
	for (std::vector<std::vector<my_point>>::iterator i = ridges_points.begin(); i != ridges_points.end(); ++ i) {
		if ((*i).size() <= 1) {
			ridges_points.erase(i);
			-- i;
		}
	}
}

void ridge::create_matrix() {
	std::cout << max_x << std::endl;
	std::cout << max_y << std::endl;

	std::vector<int> temp;
	for (unsigned short i = 0; i < max_x; ++ i) {
		temp.clear();
		for (unsigned short j = 0; j < max_y; ++ j) {
			bool check = false;
			for (unsigned short k = 0; k < ridges_points.size(); ++ k) {
				if (std::find(ridges_points[k].begin(), ridges_points[k].end(), my_point(i,j)) != ridges_points[k].end()) {
					temp.push_back(k);
					check = true;
					break;
				}
			}
			if (!check) {
				temp.push_back(-1);
			}
		}
		r_matrix.push_back(temp);
	}
}

void ridge::find_triangles_points() {
	std::vector<my_point> temp;
	for (unsigned short i = 0; i < ridges_points.size(); ++ i) {
		for (unsigned short j = 0; j < (ridges_points[i].size() - 1); ++ j) {
			temp.clear();
			temp.push_back(ridges_points[i][j]);
			temp.push_back(ridges_points[i][j+1]);
			my_point third = find_third_point(ridges_points[i][j].get_x(), ridges_points[i][j].get_y(), i);
			temp.push_back(third);
			// evita la creazione di triangoli uguali (esistono infatti ridge diversi contenenti gli stessi punti)
			if (std::find(triangles_points.begin(), triangles_points.end(), temp) == triangles_points.end()) {
					triangles_points.push_back(temp);
			}
		}
	}
}

my_point ridge::find_third_point(int x, int y, int ridge_index) {
	int max_neigh;
	if (max_x > max_y)
		max_neigh = max_x;
	else
		max_neigh = max_y;

	for (unsigned short i = 1; i < max_neigh; i ++) {

		for (unsigned short m = y-i; m <= (y+i); m ++) {
			if (m>=0 && m<max_y) {
				if ((x-i)>=0 && r_matrix[x-i][m] != -1 && r_matrix[x-i][m] != ridge_index) {
						return my_point(x-i,m);
				}
			}
			else break;
		}

		for (unsigned short n = x; n >= (x-i); n --) {
			if (n>=0 && n<max_x) {
				if ((y-i)>=0 && r_matrix[n][y-i] != -1 && r_matrix[n][y-i] != ridge_index) {
					return my_point(n,y-i);
				}
				if ((y+i)<max_y && r_matrix[n][y+i] != -1 && r_matrix[n][y+i] != ridge_index) {
					return my_point(n,y+i);
				}
			}
			else break;
		}

	}

	return my_point(-1,-1);
}

void ridge::print_triangles_points() {
	for (unsigned short i = 0; i < triangles_points.size(); ++ i) {
        for (unsigned short j = 0; j < triangles_points[i].size(); ++ j) {
					std::cout << triangles_points[i][j].get_x() << " ";
					std::cout << triangles_points[i][j].get_y() << std::endl;
        }
        std::cout << std::endl;
    }
}

void ridge::print_triangles_points_json() {
	std::ofstream outputFile;
	outputFile.open(files::get_instance().get_output_folder() + "/triangles_points.json");
	outputFile << "{" << "\n";
	outputFile << "  \"triangles\": [" << "\n";
	for (unsigned short i = 0; i < triangles_points.size(); ++ i) {
			outputFile << "    {" << "\n";
			outputFile << "      \"points\": [" << "\n";
        for (unsigned short j = 0; j < triangles_points[i].size(); ++ j) {
					outputFile << "        {" << "\n";
					outputFile << "          \"X\": " << triangles_points[i][j].get_x() << ", " << "\n";
					outputFile << "          \"Y\": " << triangles_points[i][j].get_y() << "\n";
					outputFile << "        }";
					if (j != triangles_points[i].size()-1)
						outputFile << ",";
					outputFile << "\n";
        }
			outputFile << "      ]" << "\n";
			outputFile << "    }";
			if (i != triangles_points.size()-1)
						outputFile << ",";
			outputFile << "\n";
    }
		outputFile << "  ]" << "\n";
		outputFile << "}" << "\n";
}

void ridge::print_matrix() {
	for (unsigned short i = 0; i < max_x; ++ i) {
    for ( unsigned short j = 0; j < max_y; ++ j) {
			if (r_matrix[i][j] == -1)
				std::cout << " ";
			else
				// std::cout << r_matrix[i][j]; l'immagine è deformata ma si vedono i numeri dei ridge
        std::cout << "1";
    }
    std::cout << std::endl;
  }
}

void ridge::print_matrix_on_file() {
	std::ofstream outputFile;
	outputFile.open(files::get_instance().get_output_folder() + "/059B-valleys-thinned-skeleton-bounded-triangle.txt");
	for (unsigned short i = 0; i < max_x; ++ i) {
    for (unsigned short j = 0; j < max_y; ++ j) {
			if (r_matrix[i][j] == -1)
				outputFile << " ";
			else
				// outputFile << r_matrix[i][j]; l'immagine è deformata ma si vedono i numeri dei ridge
        outputFile << "1";
    }
		outputFile << "\n";
  }
}
