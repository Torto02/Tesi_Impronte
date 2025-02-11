#pragma once
#include <iostream>
#include "../lib/json/include/json.h"

class minutia
{
public:

    minutia();
    minutia(const Json::Value minutiae, unsigned short index);
    minutia(const Json::Value minutiae, unsigned short index, std::string type);
    minutia(const Json::Value minutiae, unsigned short index, unsigned short id, std::string type);
    minutia(unsigned short index);
    minutia(unsigned short id, unsigned short x, unsigned short y, double direction, std::string type);

    unsigned short get_id() const;
    unsigned short get_x() const;
    unsigned short get_y() const;
    double get_direction() const;
    std::string get_type() const;


    void set_id(unsigned short id);
    void set_x(unsigned short x);
    void set_y(unsigned short y);
    void set_direction(double direction);
    void set_type(std::string type);
    void set_type_str(std::string type);
    void set_minutia(const minutia & to_set);
    bool operator=(const minutia& to_set);
    bool operator<(const minutia& to_set) const;
    // bool operator==(const minutia& to_compare) const;


    //Set minutia from a json file that contains an array of minutiae (given the position in the array)
    void set_minutia(unsigned short id);
    void set_minutia(const Json::Value & minutiae, unsigned short id);
    void set_minutia(const Json::Value & minutiae, unsigned short index, unsigned short id);

    void print() const;

private:

    unsigned short m_id;
    unsigned short m_x;
    unsigned short m_y;
    double m_direction;
    std::string m_type;
};