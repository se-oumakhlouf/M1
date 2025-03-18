#pragma once

#include <sstream>

class DerivedInt : public Base
{
public:
    DerivedInt(int number)
        : _int { number }
    {}

    int data() { return _int; }

    bool is_null() const override { return _int == 0; }

    std::string to_string() const override
    {
        std::stringstream ss;
        ss << _int;
        return ss.str();
    }

private:
    int _int;
};