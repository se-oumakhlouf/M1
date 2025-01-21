#pragma once
#include <string>
#include <iostream>

class Card
{
public:
    Card(unsigned int value, std::string color);

    void print() const;

    bool operator==(Card other) const;

    bool operator<(Card other) const;

private:
    unsigned int _value = 0u;
    std::string _color;
};