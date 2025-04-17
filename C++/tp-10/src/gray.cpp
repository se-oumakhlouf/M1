#include "../lib/image_lib.hpp"
#include "image.hpp"
#include "pixels.hpp"

#include <filesystem>
#include <iostream>
#include <string>

void usage()
{
    std::cout << "Usage:\n"
              << "./gray path/to/images/folder" << std::endl;
}

int main(int argc, char** argv)
{
    if (argc < 2)
    {
        usage();
        return -1;
    }

    const auto input_path = std::filesystem::path { argv[1] };

    {
        const auto base       = image_lib::load<RGBA, 499, 499>(input_path / "sonic_new_bg.png");
        const auto grayed_out = Image<RGBA, 499, 499>(
            [&base](size_t i, size_t j)
            {
                RGBA p = base(i, j);
                p.a    = 127u;
                return p;
            });
        image_lib::save(grayed_out, "images/sonic_new_bg_grayed_out.png");
    }

    return 0;
}