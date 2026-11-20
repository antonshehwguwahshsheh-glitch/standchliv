cmake_minimum_required(VERSION 3.18)
project(microsoft)
add_library(microsoft SHARED native-lib.cpp)
target_link_libraries(microsoft log dl)
