cmake_minimum_required(VERSION 3.16)
project(tp05)

add_executable(ex2
    ex2/main.cpp
    ex2/Card.cpp
    ex2/Player.cpp
    ex2/Tracker.cpp
)

target_compile_features(ex2 PRIVATE cxx_std_17)

add_executable(ex3
    ex3/main.cpp
    ex3/LinkedList.cpp
    ex3/Link.cpp
    ex3/Tracker.cpp
    ex3/Person.cpp
)

target_compile_features(ex3 PRIVATE cxx_std_17)
target_compile_features(ex3 PRIVATE )

