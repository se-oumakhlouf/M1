#pragma once
#include "PhoneBookEntry.hpp"

#include <iostream>
#include <string>
#include <vector>

class PhoneBook
{
private:
    std::vector<PhoneBookEntry> _pb;

public:
    bool add_entry(const PhoneBookEntry& pbe)
    {
        if (pbe.get_number().is_valid() == false or get_number(pbe.get_name()) != nullptr)
        {
            return false;
        }
        _pb.emplace_back(pbe);
        return true;
    }

    const PhoneNumber* get_number(const std::string& name) const
    {
        for (auto& pbe : _pb)
        {
            auto pbe_name = pbe.get_name();
            if (name == pbe_name)
            {
                return &pbe.get_number();
            }
        }
        return nullptr;
    }
};