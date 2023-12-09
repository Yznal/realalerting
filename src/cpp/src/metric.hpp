#pragma once
#include <cstddef>
#include <chrono>

struct Metric {
  size_t id;
  double val;
  std::chrono::time_point<std::chrono::system_clock, std::chrono::nanoseconds> timestamp;
};
