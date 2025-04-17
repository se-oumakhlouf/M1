#pragma once

#include "../lib/image_lib.hpp"

struct RGBA
{

    uint8_t r, g, b, a;
};

struct RGB
{

    uint8_t r, g, b;
};

struct Luma
{

    uint8_t gray;
};

template <typename To, typename From>
To convert(const From& stuff);

// - C
template <>
RGBA convert<RGBA, RGBA>(const RGBA& pixel)
{
    return pixel;
}

template <>
RGB convert<RGB, RGB>(const RGB& pixel)
{
    return pixel;
}

template <>
Luma convert<Luma, Luma>(const Luma& pixel)
{
    return pixel;
}

template <>
RGB convert<RGB, RGBA>(const RGBA& pixel)
{
    return RGB { pixel.r, pixel.g, pixel.b };
}

template <>
RGBA convert<RGBA, RGB>(const RGB& pixel)
{
    return RGBA { pixel.r, pixel.g, pixel.b, 255 };
}

template <>
Luma convert<Luma, RGB>(const RGB& pixel)
{
    return Luma { image_lib::rgb_to_grayscale(pixel.r, pixel.g, pixel.b) };
}

template <>
RGB convert<RGB, Luma>(const Luma& pixel)
{
    const auto data = image_lib::grayscale_to_rgb(pixel.gray);
    return RGB { data[0], data[1], data[2] };
}

template <>
Luma convert<Luma, RGBA>(const RGBA& pixel)
{
    return convert<Luma, RGB>(convert<RGB, RGBA>(pixel));
}

template <>
RGBA convert<RGBA, Luma>(const Luma& pixel)
{
    return convert<RGBA, RGB>(convert<RGB, Luma>(pixel));
}

// - D

RGBA operator+(const RGBA& lhs, const RGBA& rhs)
{
    const auto data = image_lib::mix_color(lhs.r, lhs.g, lhs.b, lhs.a, rhs.r, rhs.g, rhs.b, rhs.a);
    return { data[0], data[1], data[2], data[3] };
}

RGB operator+(const RGB& lhs, const RGBA& rhs)
{
    const auto new_lhs = convert<RGBA, RGB>(lhs);
    return convert<RGB, RGBA>(new_lhs + rhs);
}

RGB operator+(const Luma& lhs, const RGBA& rhs)
{
    const auto new_lhs = convert<RGBA, Luma>(lhs);
    return convert<RGB, RGBA>(new_lhs + rhs);
}

RGBA operator+(const RGBA& lhs, const RGB& rhs)
{
    const auto new_rhs = convert<RGBA, RGB>(rhs);
    return lhs + rhs;
}

RGB operator+(const RGB& lhs, const RGB& rhs)
{
    const auto new_lhs = convert<RGBA, RGB>(lhs);
    const auto new_rhs = convert<RGBA, RGB>(rhs);
    return convert<RGB, RGBA>(new_lhs + new_rhs);
}

RGB operator+(const Luma& lhs, const RGB& rhs)
{
    const auto new_lhs = convert<RGBA, Luma>(lhs);
    const auto new_rhs = convert<RGBA, RGB>(rhs);
    return convert<RGB, RGBA>(new_lhs + new_rhs);
}

template <typename P>
RGBA operator+(const P& lhs, const Luma& mask)
{
    auto rgba = convert<RGBA, P>(lhs);
    rgba.a    = rgba.a * mask.gray / 255;
    return rgba;
}
