#pragma once
#include <chrono>
#include <cstddef>

struct Metric {
  long id;
  double val;
  std::chrono::time_point<std::chrono::system_clock, std::chrono::nanoseconds>
      timestamp;
};
