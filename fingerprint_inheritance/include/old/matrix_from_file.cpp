#include "matrix_from_file.h"
#include "files.h"
#include <fstream>
#include <iostream>

matrix_from_file::matrix_from_file()
{
	// std::cerr << "Costruttore_from_file(1)\n";
	Json::Value dim_file;
	std::ifstream config_doc(files::get_instance().get_skeleton_description(), std::ifstream::binary);

	config_doc >> dim_file;

	const Json::Value dimension = dim_file["dimensions"];

	int x = dimension[0].asInt();
	int y = dimension[1].asInt();
	m_x = x;
	m_y = y;
	m_matrix = {x, std::vector<bool>(y, false)};
	set_from_file();
}

const std::vector < std::vector <bool> > & matrix_from_file::get_matrix() const
{
	return m_matrix;
}

int matrix_from_file::get_x() const
{
	return m_x;
}

int matrix_from_file::get_y() const
{
	return m_y;
}

void matrix_from_file::set_from_file()
{
	std::ifstream fin;
	fin.open(files::get_instance().get_skeleton_data());
	if (fin.is_open()) {
		for (int i = 0; i < get_x(); i++) {
			for (int j = 0; j < get_y(); j++) {
				m_matrix[i][j] = fin.get();
			}
		}
	}
	fin.close();
}

std::vector<bool>& matrix_from_file::operator[](int i)
{
	if (i >= 0 && i <= m_matrix.size())
		return m_matrix[i];

	std::cout << "Exceeded matrix boundaries";
	exit(1);
}
