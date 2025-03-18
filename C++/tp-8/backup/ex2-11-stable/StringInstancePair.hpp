#pragma once

#include "../lib/InstanceCounter.hpp"

#include <memory>
#include <string>

class StringInstancePair
{
public:
    StringInstancePair(std::string string)
        : _str(std::move(string))
        , _ic(std::make_unique<InstanceCounter>())
    {}

    const std::string&     get_str() const { return _str; }
    const InstanceCounter& get_instance_counter() const { return *_ic; }

    StringInstancePair(StringInstancePair&& other) = default;

    StringInstancePair(const StringInstancePair& other)
        : _str { other._str }
        , _ic { std::make_unique<InstanceCounter>(other.get_instance_counter()) }
    {}

private:
    std::string                      _str;
    std::unique_ptr<InstanceCounter> _ic;
};