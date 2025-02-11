#pragma once
#include<iostream>
#include<fstream>

#include "fp_minutia.h"
#include "fp_total_minutiae.h"
#include "fp_edge.h"

class fp_edges_percentage_generator_plp
{
public:

    void generate_plp();
private:

    double get_percentage(const fp_edge & edge, double angle_bif) const;
    void generate_single(int index, const fp_total_minutiae & total_minutiae, std::ofstream& outputFile);

    void write_edge(std::ofstream& outputFile, const fp_edge & edge) const;

};