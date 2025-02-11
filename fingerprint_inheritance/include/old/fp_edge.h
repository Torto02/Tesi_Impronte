#pragma once
#include <iostream>
#include "fp_minutia.h"

extern const double PI;

class fp_edge
{
public:
    //the idea is to construct the complete table of edges
    fp_edge(const minutia & reference, const minutia & neighbor);
    fp_edge(const minutia & reference, const minutia & neighbor, int length, double reference_angle, double neighbor_angle);

     const minutia &  get_reference_minutia() const;
     const minutia &  get_neighbor_minutia() const;
    int get_length() const;
    double get_reference_angle() const;
    double get_neighbor_angle() const;

    void set_reference_minutia(const minutia & m_exit);
    void set_neighbor_minutia(const minutia & neighbor);
    void set_length(int length);
    void set_reference_angle(double reference_angle);
    void set_neighbor_angle(double neighbor_angle);

    void print() const;
private:
    /*
    Fields given by "Edge table" of "SourceAFIS"
    https://sourceafis.machinezoo.com/transparency/edge-table
     */
    minutia m_reference_minutia;
    minutia m_neighbor_minutia;
    int m_length;
    double m_reference_angle;
    double m_neighbor_angle;

    //Introdurre calcolo dell'angolo dell'edge
};