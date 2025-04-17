#pragma once
#include <ostream>

class PhoneNumber
{
public:
    PhoneNumber(int e1, int e2, int e3, int e4, int e5)
        : _numbers { e1, e2, e3, e4, e5 }
    {}

    bool is_valid() const
    {
        for (auto number : _numbers)
        {
            if (number < 0 or number > 99)
            {
                return false;
            }
        }
        return true;
    }

    int operator[](const int index) const
    {
        if (index < 0 or index >= 5)
        {
            return -1;
        }
        return _numbers[index];
    }

    friend std::ostream& operator<<(std::ostream& stream, const PhoneNumber& pn);

private:
    int _numbers[5];
};

std::ostream& operator<<(std::ostream& stream, const PhoneNumber& pn)
{
    for (auto number : pn._numbers)
    {
        if (number < 10)
        {
            stream << "0" << number;
        }
        else
        {
            stream << number;
        }
    }
    return stream;
}