#pragma once

#include <list>
#include <iostream>
#include <string>
#include <algorithm> // find

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

    void remove_subordinate(const Employee &sub)
    {
        // trouver la référence à sub parmi les subordonnés
        auto it = std::find(_subordinates.begin(), _subordinates.end(), &sub);
        // la supprimer de la liste si elle y est
        if (it != _subordinates.end())
        {
            _subordinates.erase(it);
        }
    }

    // Exercice 3.4
    /*
     *  Renvoie true si other est un subordonné de l'employé, false sinon.
     */
    bool manages(const Employee &other) const
    {
        auto it = std::find(_subordinates.begin(), _subordinates.end(), &other);
        return it != _subordinates.end();
    }

    friend std::ostream &operator<<(std::ostream &, const Employee &);

    const std::string &get_name() const
    {
        return _name;
    };

    unsigned int get_salary() const
    {
        return _salary;
    };

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
