#pragma once

#include <string>

class DerivedString : public Base
{
public:
    DerivedString(std::string chaine)
        : _data { chaine }
        , Base { "String" }
    {}

    const std::string& data() { return _data; }

    bool is_null() const override { return _data == ""; }

    std::string to_string() const override { return _data; }

private:
    std::string _data;
};