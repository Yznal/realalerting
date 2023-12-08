cmake_minimum_required(VERSION 3.5)

project(test LANGUAGES CXX)


set(C_CLIENT_LINK_LIB "aeron")
set(CLIENT_LINK_LIB "aeron_client_shared")
add_definitions(-DCLIENT_SHARED)

set(HEADERS
    Configuration.h
    RateReporter.h)

add_executable(BasicPublisher BasicPublisher.cpp ${HEADERS})
add_executable(BasicSubscriber BasicSubscriber.cpp ${HEADERS})

target_include_directories(BasicPublisher
    PUBLIC ${AERON_CLIENT_SOURCE_PATH})
target_link_libraries(BasicPublisher
    ${CLIENT_LINK_LIB})

target_include_directories(BasicSubscriber
    PUBLIC ${AERON_CLIENT_SOURCE_PATH})
target_link_libraries(BasicSubscriber
    ${CLIENT_LINK_LIB})