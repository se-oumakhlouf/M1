#include "Card.hpp"    // Card
#include "Player.hpp"  // Player
#include "Tracker.hpp" // assert_no_copy

#include <cassert>  // assert
#include <iostream> // cout
#include <thread>   // this_thread

int main(int argc, char** argv)
{
    {
        Player p1 { "Gerald" };
        Player p2 { "Julie" };
        Player::deal_all_cards(p1, p2);
        assert(p1.cards().size() == 16);
        assert(p2.cards().size() == 16);

        int max_turn = (argc > 1) ? atoi(argv[1]) : -1;
        std::cout << "Début de la partie" << std::endl;
        if (max_turn > 1)
            std::cout << "Nombre de tours: " << max_turn << std::endl;
        std::cout << "------------------" << std::endl;

        while (Player::play(p1, p2) && Player::turn_number != max_turn)
        {
            std::this_thread::sleep_for(std::chrono::seconds(1));
        }

        std::cout << "Fin de partie: ";
        if (p1.cards().size() > p2.cards().size())
            std::cout << p1.name() << " gagne !" << std::endl;
        else if (p1.cards().size() < p2.cards().size())
            std::cout << p2.name() << " gagne !" << std::endl;
        else
            std::cout << "égalité parfaite !!" << std::endl;
    }
    assert_no_copy("Après déallocation totale");
    assert_mem_count("Après déallocation totale", 0);
}
