#include "minutia.h"
#include "files.h"
#include <fstream>

minutia::minutia()
{
	set_id(0);
	set_x(0);
	set_y(0);
	set_direction(0);
	set_type("");
}

minutia::minutia(const Json::Value minutiae, unsigned short index)
{
	set_minutia(minutiae, index);
}

minutia::minutia(const Json::Value minutiae, unsigned short index, std::string type)
{
	set_minutia(minutiae, index);
	set_type(type);
}

minutia::minutia(const Json::Value minutiae, unsigned short index, unsigned short id, std::string type)
{
	set_minutia(minutiae, index, id);
	set_type(type);
}

minutia::minutia(unsigned short index)
{
	Json::Value minutiae_file;
	std::ifstream config_doc(files::get_instance().get_minutiae_file(), std::ifstream::binary);
	config_doc >> minutiae_file;
	const Json::Value minutiae = minutiae_file["minutiae"];

	set_minutia(minutiae, index);
}

minutia::minutia(unsigned short id, unsigned short x, unsigned short y, double direction, std::string type)
{
	set_id(id);
	set_x(x);
	set_y(y);
	set_direction(direction);
	set_type(type);
}

unsigned short minutia::get_id() const
{
	return m_id;
}

unsigned short minutia::get_x() const
{
	return m_x;
}

unsigned short minutia::get_y() const
{
	return m_y;
}

double minutia::get_direction() const
{
	return m_direction;
}

std::string minutia::get_type() const
{
	return m_type;
}

void minutia::set_id(unsigned short id)
{
	m_id = id;
}

void minutia::set_x(unsigned short x)
{
	m_x = x;
}

void minutia::set_y(unsigned short y)
{
	m_y = y;
}

void minutia::set_direction(double direction)
{
	m_direction = direction;
}

void minutia::set_type(std::string type)
{
	m_type = type;
}

void minutia::set_minutia(const minutia & minutia)
{
	set_id(minutia.get_id());
	set_x(minutia.get_x());
	set_y(minutia.get_y());
	set_direction(minutia.get_direction());
	set_type(minutia.get_type());
}

bool minutia::operator=(const minutia& to_set)
{
	set_minutia(to_set);
	return true;
}

bool minutia::operator<(const minutia& to_compare) const
{
	if (m_x < to_compare.get_x()) {
		return true;
	} else if (m_x == to_compare.get_x()) {
		if (m_y < to_compare.get_y()) {
			return true;
		} else if (m_y == to_compare.get_y()) {
			return(m_id < to_compare.get_id());
		}
	}
	return false;
}

/*bool minutia::operator==(const minutia& to_compare) const{

//std way
	if (!((*this) < to_compare) && !(to_compare < (*this))) {
		return true;
	}
	return false;
}*/

void minutia::set_minutia(unsigned short id)
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

void minutia::set_minutia(const Json::Value & minutiae, unsigned short index, unsigned short id)
{
	set_id(id);
	set_x(minutiae[index]["x"].asInt());
	set_y(minutiae[index]["y"].asInt());
	set_direction(minutiae[index]["direction"].asDouble());
	set_type(minutiae[index]["type"].asString());
}

void minutia::set_minutia(const Json::Value & minutiae, unsigned short id)
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
