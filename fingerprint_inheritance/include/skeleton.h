#pragma once
#include <vector>
#include "../lib/json/include/json.h"

typedef std::vector < bool > my_row;
typedef std::vector < my_row > boolean_matrix;

class my_point
{
private:
    unsigned short m_x;
    unsigned short m_y;

public:
    my_point(unsigned short x, unsigned short y);

    void set_x(unsigned short x);
    void set_y(unsigned short y);

    unsigned short get_x() const;
    unsigned short get_y() const;

    bool operator<(const my_point& to_check) const;
    bool operator=(const my_point& to_set);
    bool operator==(const my_point& to_check) const;
};

class ridge
{
private:
    int max_x;
    int max_y;
    std::vector<std::vector<int>> r_matrix;
    std::vector<std::vector<my_point>> all_ridges_points;
    std::vector<std::vector<my_point>> ridges_points;
    std::vector<std::vector<my_point>> triangles_points;

    my_point find_third_point(int x, int y, int ridge_index);

public:
    ridge();
    ridge(int x, int y);

    void read_all_ridges();
    void choose_ridges_points();
    void create_matrix();

    void find_triangles_points();

    void print_triangles_points();
    void print_triangles_points_json();
    void print_matrix();
    void print_matrix_on_file();
};

class skeleton
{
public:

    skeleton();
    void set(bool is_BB);
    const boolean_matrix & get_data() const;


    unsigned short get_x() const;
    unsigned short get_y() const;

    my_row& operator[](unsigned short);

    bool operator=(const skeleton & to_set);

    std::vector<my_point> create_eroded_skeleton();
    std::vector<my_point> read_eroded_skeleton();

    void ridges_triangulation();


private:
    boolean_matrix m_data;
    boolean_matrix m_data_filled;
    unsigned short m_x;
    unsigned short m_y;
    ridge m_ridge;

    const boolean_matrix & get_data_filled() const;


    void set_from_file();
    void erode_matrix();

    std::vector<my_point> info_eroded_matrix();

    bool has_null_neigh(int x, int y);
    bool has_all_but_one_null_neigh(int x, int y);
    void print_edge_minutiae(std::vector<my_point> edge_minutiae) const;
};
