#include "Person.hpp"

std::ostream& operator<<(std::ostream& o, const Person& p)
{
    if (!p._given_name.empty())
        o << p._given_name[0];
    else
        o << "?";
    if (!p._family_name.empty())
        o << p._family_name[0];
    else
        o << "?";
    return o << ":" << p._age;
}

bool Person::operator==(const Person& other) const
{
    return _given_name == other._given_name && _family_name == other._family_name && _age == other._age;
}