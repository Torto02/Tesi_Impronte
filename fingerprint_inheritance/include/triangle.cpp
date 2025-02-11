#include "triangle.h"

#include <cmath>
#include <iostream>
#include <fstream>

////// Constructors

triangle::triangle()
{
  set_semilength(0);
  set_base(0);
  set_height(0);
  set_area(0);
  set_perimeter(0);
  set_bk(0);
  set_ratio(0);
}

////// Getters

my_point triangle::get_m1() const
{
  return t_m1;
}

my_point triangle::get_m2() const
{
  return t_m2;
}

my_point triangle::get_k() const
{
  return t_k;
}

int triangle::get_semilength() const
{
  return t_semilength;
}

int triangle::get_base() const
{
  return t_base;
}

int triangle::get_height() const
{
  return t_height;
}

int triangle::get_area() const
{
  return t_area;
}

int triangle::get_perimeter() const
{
  return t_perimeter;
}

int triangle::get_bk() const
{
  return t_bk;
}

double triangle::get_ratio() const
{
  return t_ratio;
}

////// Setters

void triangle::set_m1(const my_point &m1)
{
  t_m1 = m1;
}

void triangle::set_m2(const my_point &m2)
{
  t_m2 = m2;
}

void triangle::set_k(const my_point &k)
{
  t_k = k;
}

void triangle::set_semilength(int semilength)
{
  t_semilength = semilength;
}

void triangle::set_base(int base)
{
  t_base = base;
}

void triangle::set_height(int height)
{
  t_height = height;
}

void triangle::set_area(int area)
{
  t_area = area;
}

void triangle::set_perimeter(int perimeter)
{
  t_perimeter = perimeter;
}

void triangle::set_bk(int bk)
{
  t_bk = bk;
}

void triangle::set_ratio(double ratio)
{
  t_ratio = ratio;
}

////// Methods

void triangle::calc_triangle()
{
  int diffx_m1_k = t_m1.get_x() - t_k.get_x();
  int diffy_m1_k = t_m1.get_y() - t_k.get_y();
  int diffx_m2_k = t_m2.get_x() - t_k.get_x();
  int diffy_m2_k = t_m2.get_y() - t_k.get_y();
  int m1_k = sqrt(diffx_m1_k * diffx_m1_k + diffy_m1_k * diffy_m1_k);
  int m2_k = sqrt(diffx_m2_k * diffx_m2_k + diffy_m2_k * diffy_m2_k);
  int perimeter = t_base + m1_k + m2_k;
  set_perimeter(perimeter);

  int b_x = (t_m1.get_x() + t_m2.get_x()) / 2;
  int b_y = (t_m1.get_y() + t_m2.get_y()) / 2;
  int diffx_b_k = b_x - t_k.get_x();
  int diffy_b_k = b_y - t_k.get_y();
  int bk = sqrt(diffx_b_k * diffx_b_k + diffy_b_k * diffy_b_k);
  set_bk(bk);

  int height;
  if (t_m1.get_x() - t_m2.get_x() != 0)
  {
    double m = (t_m1.get_y() - t_m2.get_y()) / (double)(t_m1.get_x() - t_m2.get_x());
    double q = (t_m1.get_x() * t_m2.get_y() - t_m2.get_x() * t_m1.get_y()) / (double)(t_m1.get_x() - t_m2.get_x());
    height = abs(t_k.get_y() - (m * t_k.get_x() + q)) / sqrt(1 + m * m);
  }
  else
    height = abs(t_m1.get_x() - t_k.get_x());
  set_height(height);

  int area = t_base * t_height / 2;
  set_area(area);

  double ratio = t_height / (double)t_base;
  set_ratio(ratio);
}

void triangle::print_triangle_info() const
{
  std::cout << "----------------" << std::endl;
  std::cout << "--- TRIANGLE ---" << std::endl;
  std::cout << "m1: (" << t_m1.get_x() << ", " << t_m1.get_y() << ")" << std::endl;
  std::cout << "m2: (" << t_m2.get_x() << ", " << t_m2.get_y() << ")" << std::endl;
  std::cout << "k: (" << t_k.get_x() << ", " << t_k.get_y() << ")" << std::endl;
  std::cout << "base: " << t_base << std::endl;
  std::cout << "height: " << t_height << std::endl;
  std::cout << "area: " << t_area << std::endl;
  std::cout << "perimeter: " << t_perimeter << std::endl;
  std::cout << "bk: " << t_bk << std::endl;
  std::cout << "ratio: " << t_ratio << std::endl;
  std::cout << "---------------- \n"
            << std::endl;
}

void triangle::print_triangle_file(std::ofstream &bitmapFile) const
{
  bitmapFile << "    {\n";
  bitmapFile << "      \"m1\": (" << t_m1.get_x() << ", " << t_m1.get_y() << ")"
             << ",\n";
  bitmapFile << "      \"m2\": (" << t_m2.get_x() << ", " << t_m2.get_y() << ")"
             << ",\n";
  bitmapFile << "      \"k\": (" << t_k.get_x() << ", " << t_k.get_y() << ")"
             << ",\n";
  bitmapFile << "      \"base\":" << t_base << ",\n";
  bitmapFile << "      \"height\":" << t_height << ",\n";
  bitmapFile << "      \"area\":" << t_area << ",\n";
  bitmapFile << "      \"perimeter\":" << t_perimeter << ",\n";
  bitmapFile << "      \"bk\":" << t_bk << ",\n";
  bitmapFile << "      \"ratio\":" << t_ratio << ",\n";
  bitmapFile << "     }";
}
