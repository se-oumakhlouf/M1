#pragma once

#include <list>
#include <iostream>
#include <string>

class Employee
{
public:
    Employee(const std::string &name, unsigned int salary)
        : _name{name}, _salary{salary}
    {
    }

    void add_subordinate(Employee &subordinate)
    {
        _subordinates.push_back(&subordinate);
    }

    void print_subordinates() const
    {
        std::cout << _name << "'s subordinates ->" << std::endl;
        for (const auto &subordinate : _subordinates)
        {
            std::cout << "\t" << *subordinate << std::endl;
        }
    }

    friend std::ostream &operator<<(std::ostream &, const Employee &);

private:
    std::string _name;
    unsigned int _salary = 0;
    std::list<Employee *> _subordinates;
};

inline std::ostream &operator<<(std::ostream &stream, const Employee &employee)
{
    const auto is_manager = !employee._subordinates.empty();
    return stream << employee._name
                  << " (salary: " << employee._salary
                  << "| manager: " << (is_manager ? "yes" : "no")
                  << ")";
}
