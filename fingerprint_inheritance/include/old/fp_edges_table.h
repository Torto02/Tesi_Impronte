#pragma once
#include <iostream>
#include <set>
#include <tuple>

#include "../lib/json/include/json.h"
#include "fp_minutia.h"
#include "fp_total_minutiae.h"
#include "fp_edge.h"

class fp_edges_table
{
public:
    static fp_edges_table& get_instance();

    void add_outgoing(int id);
    void add_incoming(int id);

    void print_outgoing() const;
    void print_incoming() const;

    double get_score(const fp_edge & edge, double angle_bif) const;

    void clear();

    std::tuple<int, int> get_best_outgoing_edges(const minutia & reference, const fp_total_minutiae & minutiae_table) const;

    void incoming_minutia_follow_ridges(int max, int max_dist);
    void minutia_follow_direction(int max, int max_dist);

    //Set to store the minutiae that already have been selected as exiting point
    std::set<int> m_minutiae_with_outgoing_edges;
    //Set to store the minutiae that already have been selected as entry point
    std::set<int> m_minutiae_with_incoming_edges;

private:
    fp_edges_table();

    bool exists_outgoing(int id) const;
    bool exists_incoming(int id) const;

    double get_degrees(double radian) const;
    double get_radians(double degrees) const;

    void incoming_minutia_follow_ridges(const minutia & reference, fp_total_minutiae & minutiae_table, matrix_from_file & matrix_file, matrix_from_file & matrix_visited, int i, int k, int max, int max_dist, int & find, int dist, std::ofstream & outputFile);
    void minutia_follow_direction(const minutia & reference, fp_total_minutiae & minutiae_table, matrix_from_file & matrix_file, matrix_from_file & matrix_visited, int i, int k, int max, int max_dist, int & find, int dist, std::ofstream & outputFile);

    fp_edges_table(fp_edges_table const&) = delete;
    void operator=(fp_edges_table const&) = delete;
};