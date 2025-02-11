/* 
 * File:   files_generator.h
 * Author: Francesco
 *
 * Created on May 31, 2019, 4:05 PM
 */
#pragma once
#include<iostream>
#include<fstream>
#include<vector>
#include<set>
#include<forward_list>


#include "minutia.h"
#include "edge.h"
#include "skeleton.h"


typedef std::set<minutia> minutiae;
typedef std::vector<edge> edges;

class fingerprint
{
private:
    minutiae m_minutiae;
    //Vector for support to link in a faster way id and minutia
    std::vector<minutia> m_minutiae_vec;
    minutiae m_minutiae_plus_internal;

    bool m_is_BB = false;

    unsigned short m_minutiae_number;
    edges m_edges;
    skeleton m_skeleton;

    void change_minutiae_pos(minutiae to_move);
    minutia move(minutia to_move);

    void generate_edges_skeleton_ridges(const minutia & reference, skeleton & visited_skeleton, unsigned short i, unsigned short k, unsigned short max, unsigned short max_dist, unsigned short & find, unsigned short dist);

    void print_minutiae(minutiae to_print, std::string name, bool is_internal) const;

public:

    fingerprint(bool is_BBzz);

    int get_number_of_minutiae() const;
    minutia get_minutia(unsigned short index) const;

    //void generate_edges_percentage_file();
    //void generate_edges_plp();

    //void generate_edges_score_order();
    //void generate_edges_score_sequentially(int starting_minutia);

    void read_afis_edges();


    void generate_edges_skeleton_ridges(unsigned short max, unsigned short max_dist);


    void generate_edges_skeleton_ridges_DFS(bool java_BB);
    void generate_edges_skeleton_ridges_DFS(const minutia & to_check, skeleton & visited_skeleton, bool & found, int & repetitions, minutiae & new_minutiae);

    void generate_edges_bb_DFS();
    void generate_edges_bb_DFS(const minutia & start, minutiae & to_visit_minutiae, bool is_left, unsigned short max_x, minutia & ending);

    // per assegnare valore a edge::real_length
    void generate_edges_distance();

    std::forward_list<my_point> get_neighbors(int x, int y, skeleton & visited_skeleton);

    bool is_minutia(minutia & mi);

    //void print_plp_fp();
    void print_json_edges(bool is_BB) const;
    void print_json_minutiae() const;
    void print_minutiae_internal() const;
    void print_typed_minutiae(std::string name, std::string type) const;



    void print_asp() const;
    void clockwise_sort_minutiae();
    void clockwise_sort_minutiae_easy();

    void generate_triangulation();
};
