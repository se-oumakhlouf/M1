#include <iostream>
#include <vector>

void ajoute_double(std::vector<int> &v)
{
    std::vector<int> copy;
    for (auto n : v)
    {
        copy.emplace_back(n * 2);
    }
    for (auto n : copy)
    {
        v.emplace_back(n);
    }
}

void affiche(std::vector<int> &v)
{
    std::cout << "Le tableau contient les valeurs :";
    for (auto num : v)
    {
        std::cout << " " << num;
    }
    std::cout << std::endl;
}

int main()
{
    std::vector<int> entiers;

    while (true)
    {
        auto num = -1;
        std::cout << "Value : ";
        ;
        if (std::cin >> num)
        {
            if (num == 0)
            {
                break;
            }
            else if (num < 0)
            {
                entiers.pop_back();
            }
            else if (num > 0)
            {
                entiers.emplace_back(num);
            }
        }
        else
        {
            std::cerr << "Ceci n'est pas un entier" << std::endl;
            return 1;
        }
    }

    affiche(entiers);

    std::cout << entiers.front() << " " << entiers.back() << std::endl;

    ajoute_double(entiers);

    affiche(entiers);

    return 0;
}

/*
1 - effectue *2 pour chaque valeur du tableau et essayent de les ajouter
    en fin de tableau sans supprimer les précédentes mais ce
    n'est pas une référence donc ce n'est pas enregistrés
*/