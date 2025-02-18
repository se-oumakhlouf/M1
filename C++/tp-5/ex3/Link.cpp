#include "Link.hpp"

Link::Link(Person person)
    : _value { std::move(person) }
{}

Link::Link(Link* prev, Person person)
    : _prev { prev }
    , _value { std::move(person) }
{}

Link::Link(Link* prev, Person person, Link* next)
    : _prev { prev }
    , _value { std::move(person) }
    , _next { next }
{}

const Person& Link::value() const
{
    return _value;
}

Person& Link::value()
{
    return _value;
};
