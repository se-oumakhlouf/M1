#pragma once

#include <sstream>

class DerivedInt : public Base
{
public:
    DerivedInt(int number)
        : _data { number }
        , Base { "Int" }
    {}

    int data() { return _data; }

    bool is_null() const override { return _data == 0; }

    std::string to_string() const override
    {
        std::stringstream ss;
        ss << _data;
        return ss.str();
    }

private:
    int _data;
};