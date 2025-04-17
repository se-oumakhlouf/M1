#include "Player.hpp"

Player::Player(std::string name)
    : _name{name} {}

void Player::deal_all_cards(Player &p1, Player &p2)
{
    std::vector<Card> all_cards;

    // add all possible cards
    for (unsigned int i = 7; i <= 14; i++)
    {
        all_cards.emplace_back(Card{i, "Coeur"});
        all_cards.emplace_back(Card{i, "Carreau"});
        all_cards.emplace_back(Card{i, "Pique"});
        all_cards.emplace_back(Card{i, "Trèfle"});
    }

    // shuffles the deck
    std::random_device rd;
    std::shuffle(all_cards.begin(), all_cards.end(), std::default_random_engine(rd()));

    auto i = 0;
    auto half = all_cards.size() / 2;
    p1._cards.assign(all_cards.begin(), all_cards.begin() + half);
    p2._cards.assign(all_cards.begin() + half, all_cards.end());
}

Card Player::operator[](unsigned int index) const
{
    return _cards.at(index);
}

bool Player::play(Player &p1, Player &p2)
{
    std::cout << p1._name << " -> ";
    p1._cards.back().print();
    std::cout << "\n";

    std::cout << p2._name << " -> ";
    p2._cards.back().print();
    std::cout << std::endl;

    if (p1._cards.back() < p2._cards.back())
    {
        p2._score++;
        std::cout << p2._name << " remporte la bataille" << std::endl;
    }
    else if (p2._cards.back() < p1._cards.back())
    {
        p1._score++;
        std::cout << p1._name << " remporte la bataille" << std::endl;
    }
    else
    {
        std::cout << "Egalité" << std::endl;
    }

    Player::turn_number++;
    p1._cards.pop_back();
    p2._cards.pop_back();
    return not(p1._cards.empty() && p2._cards.empty());
}

unsigned int Player::score() const
{
    return _score;
}
