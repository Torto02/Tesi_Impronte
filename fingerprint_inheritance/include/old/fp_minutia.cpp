#include "fp_minutia.h"
#include "files.h"
#include <fstream>

minutia::minutia()
{
	set_id(0);
	set_x(0);
	set_y(0);
	set_direction(0);
	set_type(0);
}

minutia::minutia(const Json::Value minutiae, int index)
{
	set_minutia(minutiae, index);
}

minutia::minutia(int index)
{
	Json::Value minutiae_file;
	std::ifstream config_doc(files::get_instance().get_minutiae_file(), std::ifstream::binary);
	config_doc >> minutiae_file;
	const Json::Value minutiae = minutiae_file["minutiae"];

	set_minutia(minutiae, index);
}

minutia::minutia(int id, int x, int y, double direction, bool type)
{
	set_id(id);
	set_x(x);
	set_y(y);
	set_direction(direction);
	set_type(type);
}

minutia::minutia(int id, int x, int y, double direction, std::string type)
{
	set_id(id);
	set_x(x);
	set_y(y);
	set_direction(direction);
	set_type(type);
}

int minutia::get_id() const
{
	return m_id;
}

int minutia::get_x() const
{
	return m_x;
}

int minutia::get_y() const
{
	return m_y;
}

double minutia::get_direction() const
{
	return m_direction;
}

bool minutia::get_type() const
{
	return m_type;
}

void minutia::set_id(int id)
{
	m_id = id;
}

void minutia::set_x(int x)
{
	m_x = x;
}

void minutia::set_y(int y)
{
	m_y = y;
}

void minutia::set_direction(double direction)
{
	m_direction = direction;
}

void minutia::set_type(bool type)
{
	m_type = type;
}

void minutia::set_type(std::string type)
{
	if (type.compare("bifurcation") == 0)
		m_type = 1;
	else
		m_type = 0;
}

void minutia::set_minutia(const minutia & minutia)
{
	set_id(minutia.get_id());
	set_x(minutia.get_x());
	set_y(minutia.get_y());
	set_direction(minutia.get_direction());
	set_type(minutia.get_type());
}

void minutia::set_minutia(int id)
{
	Json::Value minutiae_file;
	std::ifstream config_doc(files::get_instance().get_minutiae_file(), std::ifstream::binary);
	config_doc >> minutiae_file;

	//Retrieve all the edges for each minutia (and also the minutia info)
	const Json::Value minutiae = minutiae_file["minutiae"];

	set_id(id);
	set_x(minutiae[id]["x"].asInt());
	set_y(minutiae[id]["y"].asInt());
	set_direction(minutiae[id]["direction"].asDouble());
	set_type(minutiae[id]["type"].asString());
}

void minutia::set_minutia(const Json::Value & minutiae, int id)
{
	set_id(id);
	set_x(minutiae[id]["x"].asInt());
	set_y(minutiae[id]["y"].asInt());
	set_direction(minutiae[id]["direction"].asDouble());
	set_type(minutiae[id]["type"].asString());
}

void minutia::print() const
{
	std::cout << "ID: " << get_id() << std::endl;
	std::cout << "x: " << get_x() << std::endl;
	std::cout << "y: " << get_y() << std::endl;
	std::cout << "Direction: " << get_direction() << std::endl;
	std::cout << "type: " << get_type() << std::endl << std::endl;
}