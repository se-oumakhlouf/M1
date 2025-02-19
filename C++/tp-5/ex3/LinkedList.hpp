#pragma once

#include "Link.hpp"
#include "Person.hpp"

class LinkedList
{
public:
    friend std::ostream& operator<<(std::ostream& o, const LinkedList& list);

    size_t size() const;
    bool   empty() const;
    void   push_back(Person);

    Person& back();
    const Person& back() const;

    Person& front();
    const Person& front() const;

    void clear();
    ~LinkedList();

    LinkedList() = default;

    LinkedList(const LinkedList&);
    LinkedList& operator=(const LinkedList&);

    LinkedList(LinkedList&&);
    LinkedList& operator=(LinkedList&&);

    // void concatenate_back(LinkedList);

    // bool contains(Person);

    // void push_back_if_absent(Person);

    // static void swap(LinkedList, LinkedList);

private:
    Link* _front = nullptr;
    Link* _back  = nullptr;
    int   _size  = 0;
};