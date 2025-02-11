/* 
 * File:   files.h
 * Author: Francesco
 *
 * Created on May 27, 2019, 11:49 AM
 */
#include <string>

#pragma once

class files
{
private:

    std::string m_input_folder_name;
    std::string m_output_folder_name;
    std::string m_minutiae_file_name;
    std::string m_minutiae_internalBB_file_name;
    std::string m_skeleton_description_name;
    std::string m_skeleton_data_name;
    std::string m_fp_name = "fp";
    int m_erosion = 10;


    files();

public:

    static files& get_instance();

    void set_input_folder(std::string name);
    void set_output_folder(std::string name);
    void set_minutiae_file(std::string name);
    void set_minutiae_internalBB_file(std::string name);
    void set_skeleton_description(std::string name);
    void set_skeleton_data(std::string name);
    void set_fp_name(std::string name);
    void set_erosion_time(int erosion_time);


    std::string get_input_folder() const;
    std::string get_output_folder() const;
    std::string get_minutiae_file() const;
    std::string get_minutiae_internalBB_file() const;
    std::string get_skeleton_description() const;
    std::string get_skeleton_data() const;
    std::string get_fp_name() const;
    int get_erosion_time() const;



    files(files const&) = delete;
    void operator=(files const&) = delete;
};