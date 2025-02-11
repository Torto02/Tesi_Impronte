/*
 * File:   files.cpp
 * Author: Francesco
 *
 * Created on May 27, 2019, 11:49 AM
 */

#include "files.h"

files::files()
{
}

files& files::get_instance()
{
	static files instance;
	return instance;
}

void files::set_input_folder(std::string name)
{
	m_input_folder_name = name;
}

void files::set_output_folder(std::string name)
{
	m_output_folder_name = name;
}

void files::set_minutiae_file(std::string name)
{
	m_minutiae_file_name = name;
}

void files::set_minutiae_internalBB_file(std::string name)
{
	m_minutiae_internalBB_file_name = name;
}

void files::set_skeleton_description(std::string name)
{
	m_skeleton_description_name = name;
}

void files::set_skeleton_data(std::string name)
{
	m_skeleton_data_name = name;
}

void files::set_fp_name(std::string fp_name)
{
	m_fp_name = fp_name;
}

void files::set_erosion_time(int erosion_time)
{
	m_erosion = erosion_time;
}

std::string files::get_input_folder() const
{
	return m_input_folder_name;
}

std::string files::get_output_folder() const
{
	return m_output_folder_name;
}

std::string files::get_minutiae_file() const
{
	return m_minutiae_file_name;
}

std::string files::get_minutiae_internalBB_file() const
{
	return m_minutiae_internalBB_file_name;
}

std::string files::get_skeleton_description() const
{
	return m_skeleton_description_name;
}

std::string files::get_skeleton_data() const
{
	return m_skeleton_data_name;
}

std::string files::get_fp_name() const
{
	return m_fp_name;
}

int files::get_erosion_time() const
{
	return m_erosion;
}

