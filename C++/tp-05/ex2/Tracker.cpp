#include "Tracker.hpp"

#include <iostream>
#include <string>

void assert_no_copy(std::string const& test_name)
{
    if (Tracker::copies() != 0)
    {
        std::string top =
            "==================== Failed at assert named \"" + test_name + "\" ====================";
        std::string bot(top.size(), '=');
        std::cerr << std::endl;
        std::cerr << top << std::endl;
        std::cerr << " - Copies: " << Tracker::copies() << " (0 attendues)" << std::endl;
        std::cerr << " - En mémoire: " << Tracker::count() << std::endl;
        std::cerr << bot << std::endl;
        exit(1);
    }
}

void assert_mem_count(std::string const& test_name, int count)
{
    if (Tracker::count() != count)
    {
        std::string top =
            "==================== Failed at assert named \"" + test_name + "\" ====================";
        std::string bot(top.size(), '=');
        std::cerr << std::endl;
        std::cerr << top << std::endl;
        std::cerr << " - Copies: " << Tracker::copies() << std::endl;
        std::cerr << " - En mémoire: " << Tracker::count() << "(" << count << " attendues)" << std::endl;
        std::cerr << bot << std::endl;
        exit(1);
    }
}