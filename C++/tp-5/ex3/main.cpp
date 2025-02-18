#include "Link.hpp"
#include "LinkedList.hpp"
#include "Person.hpp"
#include "Tracker.hpp"

#include <iostream>

int main()
{
    std::cout << "== Partie 1 ====================================" << std::endl;
    {
        Person p1 { "Victor", "Marsault" };
        Link   link1 { p1 };
        std::cout << p1 << " | on attend: VM:0" << std::endl;
        std::cout << link1.value() << " | on attend: VM:0" << std::endl;
        ++(link1.value()); // On peut modifier la valeur de retour de Link::value()
        std::cout << link1.value() << " | on attend: VM:1" << std::endl;
        // On a fait qu'une copie pour tout ceci
        assert_copy_count_since_last_check("Link 1", 1);
        // Et on a que 2 Person en mémoire
        assert_mem_count("Link 2", 2);

        const Link link2 { Person { "Céline", "Noël" } };
        std::cout << link2.value() << std::endl; // On peut appeler Link::value() sur un const Link
        // Ces deux dernières instructions n'ont provoqué aucune copie
        assert_no_copy_since_last_check("Link 3");
        // et on a maintenant 3 personnes en mémoire
        assert_mem_count("Link 4", 3);
    }
    // On a plus aucune Person en mémoire
    assert_mem_count("Link fin", 0);

    std::cout << std::endl;
    std::cout << "== Partie 2 ====================================" << std::endl;
    {
        LinkedList list;
        std::cout << std::boolalpha << list.empty() << " | on attend:  true" << std::endl;
        std::cout << list.size() << " | on attend:  0" << std::endl;

        Person vm { "Victor", "Marsault" };
        list.push_back(vm);
        std::cout << std::boolalpha << list.empty() << " | on attend:  false" << std::endl;
        std::cout << list.size() << " | on attend:  1" << std::endl;
        // Ceci a provoqué la copie de vm, mais une seule fois
        assert_copy_count_since_last_check("Basic 1", 1);

        list.push_back(Person { "Céline", "Noël" });
        Person hd { "Henri", "Derycke" };
        list.push_back(std::move(hd));
        std::cout << list.size() << " | on attend:  3" << std::endl;
        // Ces déplacement n'ont pas provoqué de copies
        assert_no_copy_since_last_check("Basic 2");
        // On a ici 5 personnes en mémoire (les trois dans la liste) plus vm, plus la "hd" vide
        assert_mem_count("Basic 3", 5);

        const LinkedList& list_ref = list;
        std::cout << list_ref.front() << " | on attend VM:0" << std::endl;
        std::cout << list_ref.back() << " | on attend HD:0" << std::endl;

        std::cout << list_ref << " | on attend { CN:0, VM:0, HD:0 } " << std::endl;
        // Pas besoin de copie en plus.
        assert_no_copy_since_last_check("Basic 2");
    }
    // On a plus aucune Person en mémoire
    assert_mem_count("Basic fin", 0);

    std::cout << std::endl;
    std::cout << "== Partie 3 ====================================" << std::endl;
    {
        // LinkedList list1;
        // list1.push_back(Person { "Victor", "Marsault" });
        // list1.push_back(Person { "Henri", "Dericke" });
        // list1.push_back(Person { "Anthony", "Labarre" });
        // ++(list1.front());

        // LinkedList list2 { list1 }; // Copions list1
        // ++(list2.front());
        // std::cout << list2.size() << " | on attend:  3" << std::endl;
        // std::cout << list2 << " | on attend { VM:2, HD:0, HD:0 } " << std::endl;
        // // On a besoin de trois copies (les trois Person dans list1)
        // assert_copy_count_since_last_check("Copy 1", 3);
        // //                 et on a 6 Person en mémoire
        // assert_mem_count("Copy 2", 6);
    }
    // On a plus aucune Person en mémoire
    assert_mem_count("copy fin", 0);

    std::cout << std::endl;
    std::cout << "== Partie 4 ====================================" << std::endl;
    {
        // LinkedList list1;
        // list1.push_back(Person { "Victor", "Marsault" });
        // list1.push_back(Person { "Henri", "Dericke" });
        // list1.push_back(Person { "Anthony", "Labarre" });
        // ++(list1.front());

        // LinkedList list2 { std::move(list1) }; // Déplaçons list1 dans list2
        // ++(list2.front());
        // std::cout << list2.size() << " | on attend 3 " << std::endl;
        // std::cout << list2 << " | on attend { VM:2, HD:0, AL:0 } " << std::endl;
        // std::cout << list1.size() << " | on attend 0 " << std::endl;
        // std::cout << list1 << " | on attend { }" << std::endl;
        // // On a besoin d'aucune copie pour ce déplacement
        // assert_no_copy_since_last_check("Move 1");
        // //                 et on a 3 Person en mémoire
        // assert_mem_count("Move 2", 3);
    }
    // On a plus aucune Person en mémoire
    assert_mem_count("Move fin", 0);

    std::cout << std::endl;
    std::cout << "== Partie 5 ====================================" << std::endl;
    {
        // LinkedList list1;
        // list1.push_back(Person { "Victor", "Marsault" });
        // list1.push_back(Person { "Henri", "Dericke" });
        // LinkedList list2;
        // list2.push_back(Person { "Anthony", "Labarre" });
        // list2.push_back(Person { "Céline", "Noël" });

        // list1.concatenate_back(list2);
        // ++(list2.front());
        // std::cout << list1 << " | on attend { VM:0, HD:0, AL:0, CN:0 } " << std::endl;
        // std::cout << list2 << " | on attend { AL:1, CN:0 } " << std ::endl;
        // // On a donc fait 2 copies (les deux Person dans list2)
        // assert_copy_count_since_last_check("Concatenate 1", 2);
        // //                        et on a 6 Person en mémoire
        // assert_mem_count("Concatenate 2", 6);

        // list1.concatenate_back(std::move(list2));
        // std::cout << list1 << " | on attend { VM:0, HD:0, AL:0, CN:0, AL:1, CN:0 }" << std ::endl;
        // std::cout << list2 << " | on attend { }" << std ::endl;
        // // Ici, on déplace, donc pas de copie en plus
        // assert_no_copy_since_last_check("Concatenate 3");
        // //               et on a toujours 6 Person en mémoire
        // assert_mem_count("Concatenate 4", 6);
    }
    // On a plus aucune Person en mémoire
    assert_mem_count("Concatenate fin", 0);

    std::cout << std::endl;
    std::cout << "== Partie 6 ====================================" << std::endl;
    {
        // LinkedList list1;
        // list1.push_back(Person { "Victor", "Marsault" });
        // list1.push_back(Person { "Henri", "Dericke" });

        // LinkedList list2;
        // list2.push_back(Person { "Anthony", "Labarre" });

        // // On veut échanger les contenus des deux listes
        // LinkedList::swap(list1, list2);

        // std::cout << list1 << " | on attend { AL:0 }" << std::endl;
        // std::cout << list2 << " | on attend { VM:0, HD:0 }" << std::endl;

        // // ... sans copies
        // assert_no_copy_since_last_check("Swap 1");
    }
    // On a plus aucune Person en mémoire
    assert_mem_count("Concatenate fin", 0);

    std::cout << std::endl;
    std::cout << "== Partie 7 ====================================" << std::endl;
    {
        // LinkedList list;
        // list.push_back(Person { "Victor", "Marsault" });
        // list.push_back(Person { "Henri", "Dericke" });
        // list.push_back(Person { "Anthony", "Labarre" });

        // // Contains renvoie true si la Person recherchée est dans la liste
        // std::cout << std::boolalpha << list.contains(Person { "Victor", "Marsault" }) << " | on attend:
        // true"
        //           << std::endl;
        // const Person cn { "Céline", "Noël" };
        // std::cout << std::boolalpha << list.contains(cn) << " | on attend: false" << std::endl;

        // // Tout ceci sans copies, évidemment
        // assert_no_copy_since_last_check("Contains 1");
    }
    // On a plus aucune Person en mémoire
    assert_mem_count("Contains fin", 0);
    //       On a bien libéré toutes les Person ^

    std::cout << std::endl;
    std::cout << "== Partie 8 ====================================" << std::endl;
    {
        // LinkedList list;
        // list.push_back(Person { "Victor", "Marsault" });

        // // On veut que LinkedList::push_back_if_absent ajoute une Personn seulement
        // // si elle n'est pas dedans.  Ici on est dans un cas où il est plus difficile
        // // d'éviter toutes les copies inutiles car on ne sait pas à l'avance si
        // // on aura besoin d'une copie !

        // // Cas 1 -> passage d'une R-value et pas besoin d'ajouter la Person
        // list.push_back_if_absent(Person { "Victor", "Marsault" });
        // std::cout << list.size() << " | on attend:  1 " << std::endl;
        // // Pas besoin de copie dans ce cas
        // assert_no_copy_since_last_check("if_absent 1");

        // // Cas 2 -> passage d'une L-value et pas besoin d'ajouter la Person
        // Person vm { "Victor", "Marsault" };
        // list.push_back_if_absent(vm);
        // // Pas besoin de copie dans ce cas
        // assert_no_copy_since_last_check("if_absent 2");

        // // Cas 3 -> passage d'une R-value et il faut ajouter la Person
        // list.push_back_if_absent(Person { "Henri", "Derycke" });
        // std::cout << list.size() << " | on attend:  2 " << std::endl;
        // // Pas besoin de copie dans ce cas
        // assert_no_copy_since_last_check("if_absent 3");

        // // Cas 4 -> passage d'une L-value et il faut ajouter la Person
        // Person al { "Anthony", "Labarre" };
        // list.push_back_if_absent(al);
        // std::cout << list.size() << " | on attend:  3" << std::endl;
        // // Dans ce cas, on est bien obligé de faire une copie :'(
        // assert_copy_count_since_last_check("if_absent 4", 1);

        // std::cout << list << " | on attend { VM:0, HD:0, AL:0 } " << std::endl;
    }
    // On a plus aucune Person en mémoire
    assert_mem_count("if_absent fin", 0);
}
