#include <array>
#include <iostream>
#include <sstream>
#include <string>
#include <vector>

template <unsigned N>
struct priority_tag : priority_tag<N - 1>
{};

template <>
struct priority_tag<0>
{};

priority_tag<3> priority_highest_value;

// Défaut
template <typename T>
auto to_string(const T& data, priority_tag<0>)
{
    std::stringstream ss;
    ss << "<" << typeid(data).name() << ": " << &data << ">";
    return ss.str();
}

// 1 - Opérateur de flux <<
template <typename T, typename = decltype(std::declval<std::ostream&>() << std::declval<T>())>
std::string to_string(const T& data, priority_tag<1>)
{
    std::stringstream ss;
    ss << data;
    return ss.str();
}

// 2 - Compatible std::to_string()
template <typename T>
std::enable_if_t<std::is_same_v<decltype(std::to_string(std::declval<T>())), std::string>, std::string>
to_string(const T& data, priority_tag<2>)
{
    return std::to_string(data);
}

// 3 - Fonction membre to_string()
template <typename T, typename Ret = decltype(std::declval<T>().to_string())>
Ret to_string(const T& data, priority_tag<3>)
{
    return data.to_string();
}

auto to_string(const std::string& data)
{
    return data;
}

auto to_string(const char* data)
{
    return std::string(data);
}

template <typename T>
auto to_string(const T& data)
{
    return to_string(data, priority_highest_value);
}

class Empty
{};

class Streamable
{
public:
    friend std::ostream& operator<<(std::ostream& out, const Streamable& s)
    {
        return out << "Streamable @" << &s;
    }
};

class Convertible
{
public:
    std::string to_string() const
    {
        std::stringstream ss;
        ss << "Convertible @" << this;
        return ss.str();
    }
};

class Both : public Streamable, public Convertible
{};

template <typename T>
void print_test(std::string type, T&& value)
{
    std::cout << type << std::endl;
    std::cout << "** Error: value is not a std::string" << std::endl;
}

void print_test(std::string type, std::string value)
{
    std::cout << type << std::endl;
    std::cout << " -> " << value << std::endl;
}

int main()
{
    // De base on a l'adresse des objets
    print_test("Vide", to_string(Empty {}));

    // Q1: on veut gérer les chaines de caractères
    print_test("std::string", to_string(std::string { "Machin" }));
    print_test("char*", to_string("Bidule"));

    // Q2: Puis les conversions simples fournies par la fonction std::to_string
    print_test("int", to_string(3));

    // Q4: On s'attaque maintenant aux conteneurs
    print_test("std::vector<int>", to_string(std::vector<int> { 1, 2, 3, 4 }));
    print_test("std::vector<std::vector<int>>",
               to_string(std::vector<std::vector<int>> { std::vector<int> { 0, 1, 2, 3 },
                                                         std::vector<int> { 4, 5, 6, 7 } }));
    // Q4b: avec une spécialisation pour les std::array
    print_test("std::array<int, 4>", to_string(std::array<int, 4> { 1, 2, 3, 4 }));

    // Q5: Si un objet présente une fonction de conversion, on l'utilise
    print_test("Convertible", to_string(Convertible {}));

    // Q6: S'il est streamable (en implémentant l'opérateur << sur ostream), idem
    print_test("Streamable", to_string(Streamable {}));

    // Commenter cette ligne si elle bloque la compilation trop tot dans le TP
    print_test("Both", to_string(Both {}));
    return 0;
}
