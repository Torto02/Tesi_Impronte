#pragma once
#include "skeleton.h"

class triangle
{
private:
  my_point t_m1 = my_point(0, 0);
  my_point t_m2 = my_point(0, 0);
  my_point t_k = my_point(0, 0);

  int t_semilength;
  int t_base;
  int t_height;
  int t_area;
  int t_perimeter;
  int t_bk;

  double t_ratio;

public:
  triangle();

  my_point get_m1() const;
  my_point get_m2() const;
  my_point get_k() const;

  int get_semilength() const;
  int get_base() const;
  int get_height() const;
  int get_area() const;
  int get_perimeter() const;
  int get_bk() const;

  double get_ratio() const;

  void set_m1(const my_point &m1);
  void set_m2(const my_point &m2);
  void set_k(const my_point &k);

  void set_semilength(int semilength);
  void set_base(int base);
  void set_height(int height);
  void set_area(int area);
  void set_perimeter(int perimeter);
  void set_bk(int bk);

  void set_ratio(double ratio);

  void calc_triangle();

  void print_triangle_info() const;
  void print_triangle_file(std::ofstream &bitmapFile) const;
};