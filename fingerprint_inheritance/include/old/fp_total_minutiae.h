#pragma once

#include <vector>

#include "../lib/json/include/json.h"
#include "fp_minutia.h"
#include "matrix_from_file.h"

class fp_total_minutiae
{
public:
    //fp_total_minutiae(const Json::Value minutiae);
    fp_total_minutiae();

    int get_total_number_of_minutiae() const;
    minutia get_minutia(int index) const;

    void set_total_number_of_minutiae(int total_number_of_minutiae);
    void insert_minutia(const minutia & to_insert, int index);

    void change_minutiae_pos();


private:

    int m_total_number_of_minutiae;
    std::vector<minutia> m_minutiae;

    void move(minutia & minutia, matrix_from_file matrix);
    //void write_minutia_mod(std::ofstream& outputFile, const fp_minutia & minutia) const;
    void write_minutia(std::ofstream& outputFile, const minutia & minutia) const;
};