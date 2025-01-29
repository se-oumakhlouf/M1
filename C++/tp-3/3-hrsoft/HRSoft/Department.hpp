#pragma once

#include "Employee.hpp"

#include <list>
#include <string>
#include <iostream>

class Department
{
public:
    Department(const std::string &name)
        : _name{name}
    {
    }

    Employee &add_employee(const std::string &name, unsigned int salary, Employee *manager)
    {
        auto &employee = _employees.emplace_back(name, salary);

        if (manager != nullptr)
        {
            manager->add_subordinate(employee);
        }

        return employee;
    }

    void print_employees() const
    {
        std::cout << _name << " departement's employees: " << std::endl;
        for (const auto &employee : _employees)
        {
            std::cout << "\t" << employee << std::endl;
        }
    }

    bool remove_employee(const Employee &emp)
    {
        auto it = _employees.begin();

        for (; it != _employees.end(); ++it)
            if (it->get_name() == emp.get_name())
                break;

        if (it == _employees.end())
            return false;

        // employé trouvé, signalons d'abord à ses managers son licenciement...
        for (auto &manager : _employees)
            manager.remove_subordinate(emp);

        // ... puis supprimons-le
        _employees.erase(it);
        return true;
    }

    friend std::ostream &operator<<(std::ostream &, const Department &);

private:
    std::string _name;
    std::list<Employee> _employees;
};

inline std::ostream &operator<<(std::ostream &stream, const Department &department)
{
    return stream << department._name;
}
