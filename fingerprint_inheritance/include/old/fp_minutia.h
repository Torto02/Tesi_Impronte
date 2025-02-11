#pragma once
#include <iostream>
#include "../lib/json/include/json.h"

class minutia
{
public:

    minutia();
    minutia(const Json::Value minutiae, int index);
    minutia(int index);
    minutia(int id, int x, int y, double direction, bool type);
    minutia(int id, int x, int y, double direction, std::string type);

    int get_id() const;
    int get_x() const;
    int get_y() const;
    double get_direction() const;
    bool get_type() const;

    void set_id(int id);
    void set_x(int x);
    void set_y(int y);
    void set_direction(double direction);
    void set_type(bool type);
    void set_type(std::string type);
    void set_minutia(const minutia & minutia);
    //Set minutia from a json file that contains an array of minutiae (given the position in the array)
    void set_minutia(int id);
    void set_minutia(const Json::Value & minutiae, int id);

    void print() const;

private:

    int m_id;
    int m_x;
    int m_y;
    double m_direction;
    /*
            0 means @type:ENDING
            1 means @type:BIFURCATION
     */
    bool m_type;
};