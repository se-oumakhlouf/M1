#pragma once

#include "Entity.hpp"
#include "Logger.hpp"
#include "Potion.hpp"
#include "Trap.hpp"

#include <iostream>

class Character : public Entity
{
public:
    Character(int x, int y)
        : Entity(x, y)
    {
        _life = 2;
    }

    char get_representation() const override
    {
        switch (_life)
        {
        case 2:
            return '0';
        case 1:
            return 'o';
        default:
            return ' ';
        }
    }

    void interact_with(Entity& other) override
    {
        auto* trap = dynamic_cast<Trap*>(&other);
        if (trap != nullptr)
        {
            _life--;
            trap->consume();
            return;
        }

        auto* potion = dynamic_cast<Potion*>(&other);
        if (potion != nullptr && _life < 2)
        {
            _life++;
            potion->consume();
            return;
        }
    }

    bool should_destroy() const override { return _life == 0; }

    ~Character()
    {
        logger << "A character died at position (" << get_x() << ", " << get_y() << ")" << std::endl;
    }

private:
    int _life;
};