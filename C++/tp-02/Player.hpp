#pragma once
#include "Card.hpp"
#include <vector>
#include <algorithm>
#include <random>

class Player
{
public:
    Player(std::string name);

    static void deal_all_cards(Player &p1, Player &p2);

    Card operator[](unsigned int index) const;

    inline static int turn_number = 0;

    static bool play(Player& p1, Player& p2);

    unsigned int score() const;

private:
    std::string _name;
    std::vector<Card> _cards;
    unsigned int _score = 0u;
};