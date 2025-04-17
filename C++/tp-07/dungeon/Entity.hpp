#pragma once

#include "Random.hpp"

class Entity
{
public:
    Entity() = default;

    Entity(int x, int y)
        : _x { x }
        , _y { y }
    {}

    int get_x() const { return _x; }
    int get_y() const { return _y; }

    virtual char get_representation() const = 0;

    virtual void update() { random_move(_x, _y); }

    virtual void interact_with(Entity& other) {}

    virtual bool should_destroy() const { return false; }

    virtual ~Entity() {};

private:
    int _x = 0;
    int _y = 0;
};
