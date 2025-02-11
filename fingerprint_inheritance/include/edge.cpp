#include "edge.h"

#include <cmath>
#include <set>

const double PI = 3.141592653589793238463;

double normalize_radians_angle(double x)
{
	x = fmod(x, 2 * M_PI);
	if (x < 0)
		x += 2 * M_PI;
	return x;
}

double normalize_degrees_angle(double x)
{
	x = fmod(x, 360);
	if (x < 0)
		x += 360;
	return x;
}

edge::edge(const minutia &reference, const minutia &neighbor)
{
	// Angle of the edge between the two minutiae (https://stackoverflow.com/questions/2339487/calculate-angle-of-2-points)
	const double edge_angle_reference = std::atan2((neighbor.get_y() - reference.get_y()), (neighbor.get_x() - reference.get_x()));
	const double edge_angle_neigh = std::atan2((reference.get_y() - neighbor.get_y()), (reference.get_x() - neighbor.get_x()));

	const double x_diff = neighbor.get_x() - reference.get_x();
	const double y_diff = neighbor.get_y() - reference.get_y();
	int length = std::sqrt(x_diff * x_diff + y_diff * y_diff);

	double reference_angle = reference.get_direction() - edge_angle_reference;
	double neighbor_angle = neighbor.get_direction() - edge_angle_neigh;

	set_reference_minutia(reference);
	set_neighbor_minutia(neighbor);
	set_length(length);
	set_reference_angle(normalize_degrees_angle(reference_angle));
	set_neighbor_angle(normalize_degrees_angle(neighbor_angle));
}

edge::edge(const minutia &reference, const minutia &neighbor, int length, double reference_angle, double neighbor_angle)
{
	set_reference_minutia(reference);
	set_neighbor_minutia(neighbor);
	set_length(length);
	set_reference_angle(reference_angle);
	set_neighbor_angle(neighbor_angle);
}

edge::edge(const minutia &reference, const minutia &neighbor, int length, double reference_angle, double neighbor_angle, int Real_length)
{
	set_reference_minutia(reference);
	set_neighbor_minutia(neighbor);
	set_length(length);
	set_reference_angle(reference_angle);
	set_neighbor_angle(neighbor_angle);
	set_real_length(Real_length);
}

const minutia &edge::get_reference_minutia() const
{
	return m_reference_minutia;
}

const minutia &edge::get_neighbor_minutia() const
{
	return m_neighbor_minutia;
}

int edge::get_length() const
{
	return m_length;
}

double edge::get_reference_angle() const
{
	return m_reference_angle;
}

double edge::get_neighbor_angle() const
{
	return m_neighbor_angle;
}

int edge::get_new_angle() const
{

	float angle_num = get_neighbor_minutia().get_x() - get_reference_minutia().get_x();
	float angle_den = get_neighbor_minutia().get_y() - get_reference_minutia().get_y();

	if (angle_den == 0.0)
	{
		if (0 < angle_num)
			return 0;
		else
			return 180;
	}

	float angle = atan((angle_num / angle_den));
	angle = angle * 180 / PI;
	//	if (angle<0) angle=+360;
	return angle;
}

const std::vector<my_point> &edge::get_path() const
{
	return m_path;
}

const my_point &edge::get_k_coord() const
{
	if (m_path.size() > 1)
		return m_path.at(m_path.size() / 2 - 1);
	else
		return m_path.at(m_path.size() / 2);
}

const triangle &edge::get_triangle() const
{
	return m_triangle;
}

void edge::set_reference_minutia(const minutia &reference)
{
	m_reference_minutia.set_minutia(reference);
}

void edge::set_neighbor_minutia(const minutia &neighbor)
{
	m_neighbor_minutia.set_minutia(neighbor);
}

void edge::set_length(int length)
{
	m_length = length;
}

void edge::set_reference_angle(double reference_angle)
{
	m_reference_angle = reference_angle;
}

void edge::set_neighbor_angle(double neighbor_angle)
{
	m_neighbor_angle = neighbor_angle;
}

void edge::set_path(const std::vector<my_point> &path)
{
	m_path = path;
}

void edge::set_triangle()
{
	m_triangle.set_m1(my_point(get_reference_minutia().get_x(), get_reference_minutia().get_y()));
	m_triangle.set_m2(my_point(get_neighbor_minutia().get_x(), get_neighbor_minutia().get_y()));
	m_triangle.set_k(get_k_coord());
	m_triangle.set_semilength(get_real_length() / 2);
	m_triangle.set_base(get_length());
	m_triangle.calc_triangle();
}

bool edge::operator=(const edge &to_set)
{
	set_length(to_set.get_length());
	set_neighbor_angle(to_set.get_neighbor_angle());
	set_neighbor_minutia(to_set.get_neighbor_minutia());
	set_reference_angle(to_set.get_reference_angle());
	set_reference_minutia(to_set.get_reference_minutia());
	set_real_length(to_set.get_real_length());
	return true;
}

void edge::print() const
{
	std::cout << "Reference_ID: " << get_reference_minutia().get_id() << std::endl;
	std::cout << "Neighbor_ID: " << get_neighbor_minutia().get_id() << std::endl;
	std::cout << "Length: " << get_length() << std::endl;
	std::cout << "RealLength: " << get_real_length() << std::endl;
	std::cout << "ReferenceAngle: " << get_reference_angle() << std::endl;
	std::cout << "NeighborAngle: " << get_neighbor_angle() << std::endl
						<< std::endl;
}

void edge::set_real_length(int length)
{
	real_length = length;
}

void edge::put_DFS(unsigned short x, unsigned short y, std::set<my_point> &visited, const std::vector<std::vector<bool>> &matrice, int tmp_res, bool &found, const std::vector<my_point> &edge_pixel, std::ofstream &bitmapFile)
{
	visited.insert(my_point(x, y));

	if (x == get_neighbor_minutia().get_x() && y == get_neighbor_minutia().get_y())
	{
		set_real_length(tmp_res);

		std::vector<my_point> new_edge_pixel;
		for (unsigned short t = 0; t < edge_pixel.size(); t++)
		{
			new_edge_pixel.push_back(edge_pixel[t]);
		}
		set_path(new_edge_pixel);
		set_triangle();
		// get_triangle().print_triangle_info();
		// new_edge_pixel.push_back(my_point(get_reference_minutia().get_x(), get_reference_minutia().get_y()));
		// new_edge_pixel.push_back(my_point(get_neighbor_minutia().get_x(), get_neighbor_minutia().get_y()));

		print_edges_pixel(new_edge_pixel, bitmapFile);
		found = true;
	}
	else
	{
		unsigned short i, j;
		for (unsigned short k = 0; k < 9; ++k)
		{
			i = k % 3;
			j = (short)k / 3;
			i += x - 1;
			j += y - 1;
			if (j < matrice.size() && i < matrice[0].size())
			{
				if (j >= 0 && i >= 0)
				{
					if (matrice[j][i])
					{
						if ((visited.insert(my_point(i, j))).second)
						{ // if (j!=y || i!=x)
							if (!found)
							{
								std::vector<my_point> new_edge_pixel;
								for (unsigned short t = 0; t < edge_pixel.size(); t++)
								{
									new_edge_pixel.push_back(edge_pixel[t]);
								}
								new_edge_pixel.push_back(my_point(i, j));
								put_DFS(i, j, visited, matrice, tmp_res + 1, found, new_edge_pixel, bitmapFile);
							}
						}
					}
				}
			}
		}
	}
}

bool edge::put_real_length(const skeleton &skeleton, std::ofstream &bitmapFile)
{
	const std::vector<std::vector<bool>> matrice = skeleton.get_data();

	//	for (unsigned short i = 0; i < matrice.size(); i++) {
	//		for (unsigned short j = 0; j < matrice[1].size(); j++) {
	//			if (matrice[i][j])
	//				std::cerr << " ";
	//			else
	//				std::cerr << "1";
	//		}
	//		std::cerr << "\n";
	//	}

	bool found = false;
	std::set<my_point> visited;
	unsigned short x_begin = get_reference_minutia().get_x();
	unsigned short y_begin = get_reference_minutia().get_y();
	std::vector<my_point> edge_pixel;
	if (matrice[y_begin][x_begin])
	{
		put_DFS(x_begin, y_begin, visited, matrice, 0, found, edge_pixel, bitmapFile);
	}

	return found;
}

int edge::get_real_length() const
{
	return real_length;
}

void edge::print_edges_pixel(const std::vector<my_point> &edge_pixel, std::ofstream &bitmapFile) const
{

	bitmapFile << "    {\n";
	bitmapFile << "      \"ID1\":" << get_reference_minutia().get_id() << ",\n";
	bitmapFile << "      \"ID2\":" << get_neighbor_minutia().get_id() << ",\n";
	bitmapFile << "      \"pixels\": [\n";
	for (unsigned short i = 0; i < edge_pixel.size(); i++)
	{
		// Inverted for the matrix
		bitmapFile << "        {\"X\" : " << edge_pixel[i].get_y() << ", \"Y\" : " << edge_pixel[i].get_x() << "}";
		if (i < edge_pixel.size() - 1)
		{
			bitmapFile << ",";
		}
		bitmapFile << "\n";
	}
	bitmapFile << "      ]\n";
	bitmapFile << "    }";
}
