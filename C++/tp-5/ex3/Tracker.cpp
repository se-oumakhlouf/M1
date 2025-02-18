#include "Tracker.hpp"

#include <iostream>
#include <string>

int copies_at_last_check = 0;

void assert_no_copy(std::string const& test_name)
{
    copies_at_last_check = Tracker::copies();
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

void assert_copy_count_since_last_check(std::string const& test_name, int n)
{
    int new_copies       = Tracker::copies() - copies_at_last_check;
    copies_at_last_check = Tracker::copies();

    if (new_copies != n)
    {
        std::string top =
            "==================== Failed at assert named \"" + test_name + "\" ====================";
        std::string bot(top.size(), '=');
        std::cerr << std::endl;
        std::cerr << top << std::endl;
        std::cerr << " - Copies depuis dernier check: " << new_copies << " (" << n << " attendues)"
                  << std::endl;
        std::cerr << " - En mémoire: " << Tracker::count() << std::endl;
        std::cerr << bot << std::endl;
        exit(1);
    }
}

void assert_no_copy_since_last_check(std::string const& test_name)
{
    assert_copy_count_since_last_check(test_name, 0);
}

void assert_mem_count(std::string const& test_name, int count)
{
    copies_at_last_check = Tracker::copies();
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

void assert_copy_count(std::string const& test_name, int count)
{
    copies_at_last_check = Tracker::copies();
    if (Tracker::copies() != count)
    {
        std::string top =
            "==================== Failed at assert named \"" + test_name + "\" ====================";
        std::string bot(top.size(), '=');
        std::cerr << std::endl;
        std::cerr << top << std::endl;
        std::cerr << " - Copies: " << Tracker::copies() << "(" << count << " attendues)" << std::endl;
        std::cerr << " - En mémoire: " << Tracker::count() << std::endl;
        std::cerr << bot << std::endl;
        exit(1);
    }
}

void assert_both_count(std::string const& test_name, int count, int copies)
{
    copies_at_last_check = Tracker::copies();
    if (Tracker::count() != count || Tracker::copies() != copies)
    {
        std::string top =
            "==================== Failed at assert named \"" + test_name + "\" ====================";
        std::string bot(top.size(), '=');
        std::cerr << std::endl;
        std::cerr << top << std::endl;
        std::cerr << " - Copies: " << Tracker::copies() << "(" << copies << " attendues)" << std::endl;
        std::cerr << " - En mémoire: " << Tracker::count() << "(" << count << " attendues)" << std::endl;
        std::cerr << bot << std::endl;
        exit(1);
    }
}