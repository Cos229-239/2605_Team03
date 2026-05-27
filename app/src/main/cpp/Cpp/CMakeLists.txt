cmake_minimum_required(VERSION 3.22.1)
project("nudgie")

# Tell CMake to compile these two files into a library named "nudgie-logic"
add_library(nudgie-logic SHARED
            native-lib.cpp
            NudgiePet.cpp)

# Link standard Android libraries
find_library(log-lib log)
target_link_libraries(nudgie-logic ${log-lib})