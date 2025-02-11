#pragma once
#include <iostream>
#include <set>
#include <fstream>

#include "files.h"
#include "minutia.h"
#include "skeleton.h"
#include "triangle.h"

extern const double PI;

class edge
{
public:
    // the idea is to construct the complete table of edges
    edge(const minutia &reference, const minutia &neighbor);
    edge(const minutia &reference, const minutia &neighbor, const skeleton &skeleton);
    edge(const minutia &reference, const minutia &neighbor, int length, double reference_angle, double neighbor_angle);
    edge(const minutia &reference, const minutia &neighbor, int length, double reference_angle, double neighbor_angle, int Real_length);

    const minutia &get_reference_minutia() const;
    const minutia &get_neighbor_minutia() const;
    int get_length() const;
    double get_reference_angle() const;
    double get_neighbor_angle() const;
    int get_new_angle() const;
    const std::vector<my_point> &get_path() const;
    const my_point &get_k_coord() const;
    const triangle &get_triangle() const;

    void set_reference_minutia(const minutia &m_exit);
    void set_neighbor_minutia(const minutia &neighbor);
    void set_length(int length);
    void set_reference_angle(double reference_angle);
    void set_neighbor_angle(double neighbor_angle);
    void set_path(const std::vector<my_point> &path);
    void set_triangle();

    bool operator=(const edge &to_set);

    void print() const;

    // Calcolo distanza tra reference e neighbor nello skeleton
    bool put_real_length(const skeleton &skeleton, std::ofstream &outputFile);

    int get_real_length() const;

private:
    /* Fields given by "Edge table" of "SourceAFIS"
    https://sourceafis.machinezoo.com/transparency/edge-table */
    minutia m_reference_minutia;
    minutia m_neighbor_minutia;
    int m_length;
    double m_reference_angle;
    double m_neighbor_angle;
    std::vector<my_point> m_path;
    triangle m_triangle;
    // Distanza tra reference e neighbor
    int real_length = 0;
    void put_DFS(unsigned short x, unsigned short y, std::set<my_point> &visited, const std::vector<std::vector<bool>> &matrice, int tmp_res, bool &found, const std::vector<my_point> &edge_pixel, std::ofstream &outputFile);
    void set_real_length(int length);
    void print_edges_pixel(const std::vector<my_point> &edge_pixel, std::ofstream &outputFile) const;

    // Introdurre calcolo dell'angolo dell'edge
};