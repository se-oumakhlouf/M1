#pragma once
#include "PhoneNumber.hpp"

#include <string>

class PhoneBookEntry
{
public:
    PhoneBookEntry(const std::string& name, const PhoneNumber& pn)
        : _name { name }
        , _pn { pn }
    {}

    const std::string& get_name() const { return _name; }

    const PhoneNumber& get_number() const { return _pn; }

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