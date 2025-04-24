#pragma once

#include <array>
#include <cstddef>

template <typename ElementType, size_t size>
class HybridArray
{
public:
    constexpr static size_t static_size() { return size; }

    ElementType& push_back(const ElementType& elem)
    {
        return _elements[_size++] = elem;
    }

private:
    size_t                        _size = 0u;
    std::array<ElementType, size> _elements;
};