#pragma once

#include <ostream>
#include <vector>

class Recipe
{
    // Affiche l'identifiant et la formule d'une recette.
    friend std::ostream &operator<<(std::ostream &stream, const Recipe &recipe)
    {
        stream << "(" << recipe._id << ") ";
        for (const auto &material : recipe._materials)
        {
            stream << material << " ";
        }
        stream << "=> ";
        for (const auto &product : recipe._products)
        {
            stream << product << " ";
        }
        return stream;
    }

public:
    Recipe(size_t id, std::vector<std::string> materials, std::vector<std::string> products)
        : _id{id}, _materials{std::move(materials)}, _products{std::move(products)} {}

    const std::vector<std::string> &get_materials() const { return _materials; }

    const std::vector<std::string> &get_products() const { return _products; }

private:
    std::vector<std::string> _materials;
    std::vector<std::string> _products;
    size_t _id;
};
