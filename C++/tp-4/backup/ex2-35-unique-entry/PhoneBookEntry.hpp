#pragma once
#include "PhoneNumber.hpp"

#include <string>

class PhoneBookEntry
{
public:
    PhoneBookEntry(std::string name, PhoneNumber pn)
        : _name { name }
        , _pn { pn }
    {}

    std::string& get_name() { return _name; }

    PhoneNumber& get_number() { return _pn; }

    std::string get_name() const { return _name; }

    PhoneNumber get_number() const { return _pn; }

    bool operator==(const PhoneBookEntry& other) const
    {
        if (_name == other.get_name())
        {
            return true;
        }
        return false;
    }

private:
    std::string _name;
    PhoneNumber _pn;
};