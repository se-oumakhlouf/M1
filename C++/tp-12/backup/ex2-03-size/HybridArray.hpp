#pragma once

#include <array>
#include <cstddef>
#include <stdexcept>

template <typename ElementType, size_t Tsize>
class HybridArray
{
public:
    constexpr static size_t static_size() { return Tsize; }

    ElementType& push_back(const ElementType& elem) { return _elements[_size++] = elem; }

    size_t size() const { return _size; }

    ElementType& operator[](const size_t index) { return _elements[index]; }

private:
    size_t                         _size = 0;
    std::array<ElementType, Tsize> _elements;
};