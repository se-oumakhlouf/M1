#pragma once

#include <algorithm>
#include <array>
#include <functional>

template <typename Pixeltype1, typename Pixeltype2>
using CombinedPixel = decltype(std::declval<Pixeltype1>() + std::declval<Pixeltype2>());

template <typename P, size_t W, size_t H>
class Image
{
public:
    Image() = default;

    Image(const P& pixel)
    {
        for (auto& line : _data)
        {
            line.fill(pixel);
        }
    }

    Image(const std::function<P(size_t i, size_t j)>& function)
    {
        for (int j = 0; j < H; j++)
        {
            for (int i = 0; i < W; i++)
            {
                (*this)(j, i) = function(j, i);
            }
        }
    }

    const P& operator()(const int& i, const int& j) const { return _data[j][i]; }
    P&       operator()(const int& i, const int& j) { return _data[j][i]; }

    template <typename OtherP>
    Image<CombinedPixel<P, OtherP>, W, H> operator+(const Image<OtherP, W, H>& other) const
    {
        Image<CombinedPixel<P, OtherP>, W, H> combination;
        for (int j = 0; j < H; j++)
        {
            for (int i = 0; i < W; i++)
            {
                combination(i, j) = (*this)(i, j) + other(i, j);
            }
        }
        return combination;
    }

private:
    std::array<std::array<P, W>, H> _data;
};