#pragma once
#define RYML_SINGLE_HDR_DEFINE_NOW
#include <fstream>

#include "rapidyaml-0.5.0.hpp"

std::string get_file_contents(const char *filename) {
  std::ifstream in(filename, std::ios::in | std::ios::binary);
  if (!in) {
    std::cerr << "could not open " << filename << std::endl;
    exit(1);
  }
  std::ostringstream contents;
  contents << in.rdbuf();
  return contents.str();
}
struct Config {
  static std::string content;
  static ryml::Tree tree;
};

std::string Config::content = get_file_contents(R"(../config/PublisherConfig.yml)");
ryml::Tree Config::tree =
    ryml::parse_in_place(ryml::to_substr(Config::content));