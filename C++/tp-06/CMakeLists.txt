cmake_minimum_required(VERSION 3.16)
project(TP6)

add_executable(ex1
    1-recipes/main.cpp
    1-recipes/Material.hpp
    1-recipes/ProgramData.cpp
    1-recipes/ProgramData.hpp
    1-recipes/Recipe.hpp
)
target_compile_features(ex1 PRIVATE cxx_std_17)

add_executable(ex2
    2-ptr/main.cpp
    2-ptr/Object.hpp
    2-ptr/CopyablePtr.hpp
)

target_compile_features(ex2 PRIVATE cxx_std_17)
