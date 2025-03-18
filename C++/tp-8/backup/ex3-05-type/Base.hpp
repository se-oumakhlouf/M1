#pragma once

#include <string>

class Base
{
public:
    Base(std::string type)
        : _type { type }
    {}

    virtual bool        is_null() const   = 0;
    virtual std::string to_string() const = 0;
    std::string         type() const { return _type; }

private:
    std::string _type;
};

inline std::ostream& operator<<(std::ostream& stream, const Base& base)
{
    return stream << base.to_string();
}