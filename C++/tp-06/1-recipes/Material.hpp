#pragma once

#include <ostream>
#include <iostream>

class Material
{
public:
    // Affiche le nom d'un materiau.
    friend std::ostream &operator<<(std::ostream &stream, const Material &material)
    {
        stream << material._name;
        return stream;
    }

    Material(const std::string &name)
        : _name{std::move(name)}
    {
        std::cout << _name << " was created" << std::endl;
    }

    ~Material()
    {
        std::cout << _name << " was destroyed" << std::endl;
    }

    const std::string &get_name() const { return _name; }

private:
    std::string _name;
};
