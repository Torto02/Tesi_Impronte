#pragma once
#include<iostream>
#include<fstream>
#include<tuple>

#include "files.h"
#include "fp_minutia.h"
#include "fp_total_minutiae.h"
#include "fp_edges_table.h"

class fp_edges_score_generator
{
public:

    void generate_follow_minutiae_file_order();
    void generate_follow_minutiae_sequentially(int starting_minutia);

private:

    void write_edge(std::ofstream& outputFile, int ref_x, int ref_y, const fp_minutia & best_neigh) const;
    void write_edges(std::ofstream& outputFile, const fp_minutia & reference, const std::tuple<int, int> & best_edges) const;
    std::tuple<int, int> generate_single(int index, const fp_total_minutiae & total_minutiae, std::ofstream& outputFile);
};