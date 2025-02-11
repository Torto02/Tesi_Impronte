#include "fp_edge.h"

#include <cmath>

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

fp_edge::fp_edge(const minutia & reference, const minutia & neighbor)
{
	//Angle of the edge between the two minutiae (https://stackoverflow.com/questions/2339487/calculate-angle-of-2-points)
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
	set_reference_angle(normalize_radians_angle(reference_angle));
	set_neighbor_angle(normalize_radians_angle(neighbor_angle));
}

fp_edge::fp_edge(const minutia & reference, const minutia & neighbor, int length, double reference_angle, double neighbor_angle)
{
	set_reference_minutia(reference);
	set_neighbor_minutia(neighbor);
	set_length(length);
	set_reference_angle(reference_angle);
	set_neighbor_angle(neighbor_angle);
}

const minutia & fp_edge::get_reference_minutia() const
{
	return m_reference_minutia;
}

const minutia & fp_edge::get_neighbor_minutia() const
{
	return m_neighbor_minutia;
}

int fp_edge::get_length() const
{
	return m_length;
}

double fp_edge::get_reference_angle() const
{
	return m_reference_angle;
}

double fp_edge::get_neighbor_angle() const
{
	return m_neighbor_angle;
}

void fp_edge::set_reference_minutia(const minutia & reference)
{
	m_reference_minutia.set_minutia(reference);
}

void fp_edge::set_neighbor_minutia(const minutia & neighbor)
{
	m_neighbor_minutia.set_minutia(neighbor);
}

void fp_edge::set_length(int length)
{
	m_length = length;
}

void fp_edge::set_reference_angle(double reference_angle)
{
	m_reference_angle = reference_angle;
}

void fp_edge::set_neighbor_angle(double neighbor_angle)
{
	m_neighbor_angle = neighbor_angle;
}

void fp_edge::print() const
{
	std::cout << "Reference_ID: " << get_reference_minutia().get_id() << std::endl;
	std::cout << "Neighbor_ID: " << get_neighbor_minutia().get_id() << std::endl;
	std::cout << "Length: " << get_length() << std::endl;
	std::cout << "ReferenceAngle: " << get_reference_angle() << std::endl;
	std::cout << "NeighborAngle: " << get_neighbor_angle() << std::endl << std::endl;
}
