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

    void remove_employee(Employee &emp)
    {
        // parcourir les départements à la recherche de l'employé, et le supprimer; comme un
        // employé travaille pour un seul département, on s'arrête après la première tentative fructueuse
        for (auto &dep : _departments)
            if (dep.remove_employee(emp))
            {
                std::cout << "removed employee " << emp.get_name() << std::endl;
                dep.print_employees();
                break;
            }
    }

private:
    std::list<Department> _departments;
};
