#pragma once

#include "../lib/json/include/json.h"
#include <vector>

class matrix_from_file
{
public:

    matrix_from_file();
    const std::vector < std::vector <bool> > & get_matrix() const;

    int get_x() const;
    int get_y() const;

    std::vector<bool>& operator[](int);

private:
    std::vector< std::vector<bool> > m_matrix;
    int m_x;
    int m_y;

    void set_from_file();

};


