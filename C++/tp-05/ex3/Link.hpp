#pragma once

#include "Person.hpp"

class LinkedList;

class Link
{
public:
    Link(Person person);
    Link(Link* prev, Person person);
    Link(Link* prev, Person person, Link* next);

    const Person& value() const;
    Person&       value();
    
    Link*  _prev;
    Link*  _next;
    Person _value;
};