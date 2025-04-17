#include <iomanip>
#include <iostream>

int main()
{
    std::cout << "Entre ton nom: ";

    // char name[20] = "";
    std::string name;
    // std::cin >> std::setw(20) >> name;
    std::cin >> name;
    std::cout << "Bonjour " << name << " !" << std::endl;

    return 0;
}

// la taille de nom est 20 et setw est setup à 20
// donc les caractères après 20 ne sont pas enregistrées

// avec le type std::string il n'y a pas besoin de setw pour la taille

// pour avoir executable bonjour ->
// g++ -std=c++17 - bonjour main.cpp