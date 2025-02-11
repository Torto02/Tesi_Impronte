#include "fp_total_minutiae.h"
#include "files.h"


#include <iostream>
#include <fstream>

//TODO: Adatta la stringa e falla dinamica, magari passa direttamente la matrice

/*fp_total_minutiae::fp_total_minutiae(const Json::Value minutiae)
{
	m_total_number_of_minutiae = minutiae.size();
	std::vector<fp_minutia> minutiae_vec(m_total_number_of_minutiae, fp_minutia());
	m_minutiae = minutiae_vec;

	for (int i = 0; i < m_total_number_of_minutiae; ++i) {
		insert_minutia(fp_minutia(minutiae, i), i);
	}

	change_minutiae_pos();

}*/

fp_total_minutiae::fp_total_minutiae()
{
	Json::Value minutiae_file;
	std::ifstream config_doc(files::get_instance().get_minutiae_file(), std::ifstream::binary);
	config_doc >> minutiae_file;
	const Json::Value minutiae = minutiae_file["minutiae"];

	m_total_number_of_minutiae = minutiae.size();
	std::vector<minutia> minutiae_vec(m_total_number_of_minutiae, minutia());
	m_minutiae = minutiae_vec;

	for (int i = 0; i < m_total_number_of_minutiae; ++i) {
		insert_minutia(minutia(minutiae, i), i);
	}

	change_minutiae_pos();

}

int fp_total_minutiae::get_total_number_of_minutiae() const
{
	return m_total_number_of_minutiae;
}

minutia fp_total_minutiae::get_minutia(int index) const
{
	minutia ret;
	ret.set_minutia(m_minutiae.at(index));
	return ret;
}

void fp_total_minutiae::set_total_number_of_minutiae(int total_number_of_minutiae)
{
	m_total_number_of_minutiae = total_number_of_minutiae;
}

void fp_total_minutiae::insert_minutia(const minutia & to_insert, int index)
{
	m_minutiae[index].set_minutia(to_insert);
}

//Venturini
//Migliorare tutto, soprattuto le stringhe

void fp_total_minutiae::change_minutiae_pos()
{

	matrix_from_file matrix_file;
	//Cancella quello che era già scritto in risultati e a fine funzione inserisce le parentesi finali.
	std::ofstream output;
	output.open(files::get_instance().get_output_folder() + "/updated_minutiae.json");
	output << "{" << '\n';
	output << "  \"width\": " << matrix_file.get_y() << "," << '\n';
	output << "  \"height\": " << matrix_file.get_x() << "," << '\n';
	output << "  \"minutiae\": [" << '\n';
	std::vector < std::vector <bool> > data_matrix(matrix_file.get_matrix());
	for (int i = 0; i < m_total_number_of_minutiae; ++i) {
		minutia tmp = m_minutiae[i];
		if (!data_matrix[tmp.get_y()][tmp.get_x()]) {
			move(m_minutiae[i], matrix_file);
		}
		write_minutia(output, m_minutiae[i]);
	}
	output << "  ]\n}";
	output.close();

}

void fp_total_minutiae::write_minutia(std::ofstream& outputFile, const minutia & minutia) const
{
	outputFile << "    {" << '\n';
	//outputFile << "      \"ID\": " << minutia.get_id() << ", " << '\n';
	outputFile << "      \"x\": " << minutia.get_x() << ", " << '\n';
	outputFile << "      \"y\": " << minutia.get_y() << ", " << '\n';
	outputFile << "      \"direction\": " << minutia.get_direction() << ", " << '\n';
	outputFile << "      \"type\": ";
	if (!minutia.get_type()) {
		outputFile << "\"bifurcation\"";
	} else {
		outputFile << "\"ending\"";
	}
	outputFile << '\n';
	outputFile << "    },\n";
}

void fp_total_minutiae::move(minutia & minutia, matrix_from_file matrix)
{
	//Get x e y from minutia
	int x = minutia.get_y();
	int y = minutia.get_x();

	bool find = false;
	int j = 1;
	int new_x = x;
	int new_y = y;
	if (matrix[x][y]) {
		find = true;
	}
	while (!find) {
		for (int i = x - j; i < x + j; i++) {
			for (int z = y - j; z < y + j; z++) {
				if (i >= 0 && i < matrix.get_x() && z < matrix.get_y() && z >= 0) {
					//Controllo se sono in un nero
					if (matrix[i][z]) {
						//Controllo il quadrato 3X3 contenete al centro il punto [i][z]
						int somma = 0;
						for (int a = -1; a < 2; a++) {
							for (int b = -1; b < 2; b++) {
								if (matrix[i + a][z + b]) {
									somma++;
								}
							}
						}
						//controllo che sia una biforcazione
						if (minutia.get_type() == 0 && somma == 4) {
							find = true;
							new_x = i;
							new_y = z;
							break;
						}
						//controllo che sai ending
						if (minutia.get_type() == 1 && somma == 2) {
							find = true;
							new_x = i;
							new_y = z;
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
		minutia.set_x(new_y);
		minutia.set_y(new_x);
	}
	//write_minutia_mod(output, minutia);
}