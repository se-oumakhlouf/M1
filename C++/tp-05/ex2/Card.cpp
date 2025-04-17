#include "Card.hpp" // Card

#include <iostream> // cout
#include <string>   // string
#include <vector>   // vector

Card::Card(unsigned v, const std::string& c)
{
    _value = v;
    _color = c;
}


void Card::print() const
{
    switch (_value)
    {
    case 11:
        std::cout << "Valet";
        break;
    case 12:
        std::cout << "Dame";
        break;
    case 13:
        std::cout << "Roi";
        break;
    case 14:
        std::cout << "As";
        break;
    default:
        std::cout << _value;
    };
    std::cout << " de " << _color;
}

bool Card::operator==(const Card& other) const
{
    return _value == other._value and _color == other._color;
}

bool Card::operator<(const Card& other) const
{
    return _value < other._value;
}

std::ostream& operator<<(std::ostream& sortie, const Card& carte)
{
    switch (carte._value)
    {
    case 11:
        sortie << "Valet";
        break;
    case 12:
        sortie << "Dame";
        break;
    case 13:
        sortie << "Roi";
        break;
    case 14:
        sortie << "As";
        break;
    default:
        sortie << carte._value;
    };
    sortie << " de " << carte._color;
    return sortie;
}
