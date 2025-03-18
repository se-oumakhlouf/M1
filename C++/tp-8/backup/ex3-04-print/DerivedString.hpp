#pragma once

#include <string>

class DerivedString : public Base
{
public:
    DerivedString(std::string chaine)
        : _str { chaine }
    {}

    const std::string& data() { return _str; }

    bool is_null() const override { return _str == ""; }

    std::string to_string() const override { return _str; }

private:
    std::string _str;
};