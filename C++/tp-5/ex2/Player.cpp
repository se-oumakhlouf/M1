#include "Player.hpp"

#include <algorithm> // shuffle
#include <iostream>  // cout, endl
#include <random>    // default_random_engine, random_device
#include <string>
#include <thread> // this_thread

Player::Player(const std::string& nom)
{
    _name = nom;
}

void Player::deal_all_cards(Player& p1, Player& p2)
{
    // initialiser toutes les cartes
    std::vector<Card> all_cards;
    for (int valeur = 7; valeur <= 14; ++valeur)
        for (const auto& couleur : { "Coeur", "Pique", "Trèfle", "Carreau" })
            all_cards.emplace_back(valeur, couleur);

    // mélanger le paquet
    std::random_device rd;
    std::shuffle(all_cards.begin(), all_cards.end(), std::default_random_engine(rd()));

    for (int i = 0, s = all_cards.size(); i < s; i += 2)
    {
        p1.push_front(std::move(all_cards[i]));
        p2.push_front(std::move(all_cards[i + 1]));
    }
}

void Player::push_front(Card card)
{
    _cards.insert(_cards.begin(), std::move(card));
}

Card Player::pop_back()
{
    auto res = std::move(_cards.back());
    _cards.pop_back();
    return res;
}

bool Player::play(Player& p1, Player& p2, std::vector<Card> stake)
{
    static std::random_device rd;

    if (p1._cards.empty() || p2._cards.empty())
    {
        if (!stake.empty())
            std::cout << "Egalité, mais pas assez de cartes pour terminer !!" << std::endl;
        return false;
    }

    if (stake.empty())
        std::cout << "Tour n°" << ++Player::turn_number << std::endl;
    else
        std::cout << "\tEgalité !!" << std::endl;

    Card p1_card = p1.pop_back();
    Card p2_card = p2.pop_back();
    std::cout << "\t" << p1._name << " joue " << p1_card << " \t" << p2._name << " joue " << p2_card
              << std::endl;

    bool p2_wins = p1_card < p2_card;
    bool p1_wins = p2_card < p1_card;
    stake.push_back(std::move(p1_card));
    stake.push_back(std::move(p2_card));

    if (p1_wins)
    {
        std::shuffle(stake.begin(), stake.end(), std::default_random_engine(rd()));
        for (Card& c : stake)
            p2.push_front(std::move(c));
        std::cout << "\t" << p2._name << " remporte le pli (" << stake.size() << " cartes)" << std::endl;
    }
    else if (p2_wins)
    {
        std::shuffle(stake.begin(), stake.end(), std::default_random_engine(rd()));
        for (Card& c : stake)
            p1.push_front(std::move(c));
        std::cout << "\t" << p1._name << " remporte le pli (" << stake.size() << " cartes)" << std::endl;
    }
    else
    {
        std::this_thread::sleep_for(std::chrono::milliseconds(200));
        return play(p1, p2, std::move(stake));
    }
    return true;
}