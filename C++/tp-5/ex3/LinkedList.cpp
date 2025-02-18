#include "LinkedList.hpp"

std::ostream& operator<<(std::ostream& o, const LinkedList& list)
{
    if (list.empty())
    {
        return o << "{ }";
    }
    o << "{ ";
    for (Link* it = list._front; it != nullptr; it = it->_next)
    {
        if (it != list._front)
        {
            o << ", ";
        }
        o << it->value();
    }

    return o << " }";
}

size_t LinkedList::size() const
{
    size_t size = 0;
    for (Link* it = _front; it != nullptr; it = it->_next)
    {
        size++;
    }
    return size;
}

bool LinkedList::empty() const
{
    return _front == nullptr;
}

void LinkedList::push_back(Person person)
{
    Link* link = new Link { _back, std::move(person) };
    if (_back == nullptr)
    {
        _front = link;
    }
    else
    {
        _back->_next = link;
    }
    _back = link;
}

const Person& LinkedList::back() const
{
    return _back->value();
}

Person& LinkedList::back()
{
    return _back->value();
}

Person& LinkedList::front()
{
    return _front->value();
}

const Person& LinkedList::front() const
{
    return _front->value();
}

LinkedList::~LinkedList()
{
    Link* tmp = nullptr;
    for (Link* it = _front; it != nullptr; it = it->_next) {
        delete tmp;
        tmp = it;
    }
    delete tmp;
}
