#pragma once

#include <string>

class Base
{
public:
    virtual bool        is_null() const   = 0;
    virtual std::string to_string() const = 0;
};

inline std::ostream& operator<<(std::ostream& stream, const Base& base)
{
    return stream << base.to_string();
}