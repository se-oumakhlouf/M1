#pragma once

#include <array>
#include <cstddef>
#include <vector>

template <typename ElementType, size_t Tsize>
class HybridArray
{
public:
    constexpr static size_t static_size() { return Tsize; }

    ElementType& push_back(const ElementType& elem)
    {
        if (_size < Tsize)
        {
            return _elements[_size++] = elem;
        }
        else if (_size == Tsize && _dynamic_elements.empty())
        {
            for (auto& value : _elements)
            {
                _dynamic_elements.push_back(value);
            }
        }
        _dynamic_elements.push_back(elem);
        return _dynamic_elements.back();
    }

    size_t size() const { return _dynamic_elements.empty() ? _size : _dynamic_elements.size(); }

    ElementType& operator[](const size_t index)
    {
        return _dynamic_elements.empty() ? _elements[index] : _dynamic_elements[index];
    }

    const ElementType& operator[](const size_t index) const
    {
        return _dynamic_elements.empty() ? _elements[index] : _dynamic_elements[index];
    }

private:
    size_t                         _size = 0;
    std::array<ElementType, Tsize> _elements;
    std::vector<ElementType>       _dynamic_elements;
};

template <typename TValue>
class HybridArray<TValue, 0u> : public std::vector<TValue> {
    using std::vector<TValue>::vector;
};
