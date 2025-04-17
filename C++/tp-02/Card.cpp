#include "Card.hpp"

Card::Card(unsigned int value, std::string color)
    : _value{value}, _color{color} {}

void Card::print() const
{
    std::cout << _value << " de " << _color;
}

bool Card::operator==(Card other) const
{
    return _value == other._value;
}

bool Card::operator<(Card other) const
{
    return _value < other._value;
}
