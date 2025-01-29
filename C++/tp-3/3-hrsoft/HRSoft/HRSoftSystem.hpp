#pragma once

#include "Department.hpp"

#include <list>
#include <string>

class HRSoftSystem
{
public:
    Department &add_department(const std::string &name)
    {
        return _departments.emplace_back(name);
    }

    void print_all_departments() const
    {
        std::cout << "All Departments: " << std::endl;
        for (const auto &department : _departments)
        {
            std::cout << "\t" << department << std::endl;
        }
    }

    void print_all_employees() const
    {
        std::cout << "All Employees: " << std::endl;
        for (const auto &department : _departments)
        {
            department.print_employees();
        }
    }

private:
    std::list<Department> _departments;
};
