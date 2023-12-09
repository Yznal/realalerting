#include <array>
#include <cinttypes>
#include <csignal>
#include <cstdint>
#include <cstdio>
#include <fstream>
#include <thread>
#include <vector>

#include "Aeron.h"
#include "Config.hpp"

std::string Config::content =
    get_file_contents(R"(../config/PublisherConfig.yml)");
ryml::Tree Config::tree =
    ryml::parse_in_place(ryml::to_substr(Config::content));

#include "metric.hpp"
#include "util/CommandOptionParser.h"

using namespace aeron::util;
using namespace aeron;

std::atomic<bool> running(true);

void sigIntHandler(int) { running = false; }

static const char optHelp = 'h';
static const char optPrefix = 'p';
static const char optChannel = 'c';
static const char optStreamId = 's';
static const char optMessages = 'm';
static const char optLinger = 'l';

struct Settings {
  std::string dirPrefix;
  std::string channel;
  std::int32_t streamId;
  // int lingerTimeoutMs = configuration::DEFAULT_LINGER_TIMEOUT_MS;
};

typedef std::array<std::uint8_t, 256> buffer_t;

Settings parseCmdLine(CommandOptionParser &cp, int argc, char **argv) {
  cp.parse(argc, argv);
  if (cp.getOption(optHelp).isPresent()) {
    cp.displayOptionsHelp(std::cout);
    exit(0);
  }

  Settings s;

  s.dirPrefix = cp.getOption(optPrefix).getParam(0, s.dirPrefix);
  Config::tree["metricConfig"]["metricClient"]["config"]["channel"] >>
      s.channel;
  Config::tree["metricConfig"]["metricClient"]["config"]["streamid"] >>
      s.streamId;
  // s.lingerTimeoutMs = cp.getOption(optLinger).getParamAsInt(
  //     0, 0, 60 * 60 * 1000, s.lingerTimeoutMs);

  return s;
}

int main(int argc, char **argv) {
  CommandOptionParser cp;
  cp.addOption(CommandOption(optHelp, 0, 0,
                             "                Displays help information."));
  cp.addOption(CommandOption(
      optPrefix, 1, 1, "dir             Prefix directory for aeron driver."));
  cp.addOption(CommandOption(optChannel, 1, 1, "channel         Channel."));
  cp.addOption(CommandOption(optStreamId, 1, 1, "streamId        Stream ID."));
  cp.addOption(
      CommandOption(optMessages, 1, 1, "number          Number of Messages."));
  cp.addOption(CommandOption(
      optLinger, 1, 1, "milliseconds    Linger timeout in milliseconds."));

  try {
    Settings settings = parseCmdLine(cp, argc, argv);

    std::cout << "Publishing to channel " << settings.channel
              << " on Stream ID " << settings.streamId << std::endl;

    aeron::Context context;

    if (!settings.dirPrefix.empty()) {
      context.aeronDir(settings.dirPrefix);
    }

    context.newPublicationHandler(
        [](const std::string &channel, std::int32_t streamId,
           std::int32_t sessionId, std::int64_t correlationId) {
          std::cout << "Publication: " << channel << " " << correlationId << ":"
                    << streamId << ":" << sessionId << std::endl;
        });

    std::shared_ptr<Aeron> aeron = Aeron::connect(context);
    signal(SIGINT, sigIntHandler);
    // add the publication to start the process
    std::int64_t id =
        aeron->addPublication(settings.channel, settings.streamId);

    std::shared_ptr<Publication> publication = aeron->findPublication(id);
    // wait for the publication to be valid
    while (!publication) {
      std::this_thread::yield();
      publication = aeron->findPublication(id);
    }

    const std::int64_t channelStatus = publication->channelStatus();

    std::cout << "Publication channel status (id="
              << publication->channelStatusId() << ") "
              << (channelStatus ==
                          ChannelEndpointStatus::CHANNEL_ENDPOINT_ACTIVE
                      ? "ACTIVE"
                      : std::to_string(channelStatus))
              << std::endl;

    AERON_DECL_ALIGNED(buffer_t buffer, 16);
    concurrent::AtomicBuffer srcBuffer(&buffer[0], buffer.size());

    std::vector<size_t> ids;
    for (auto child :
         Config::tree["metricConfig"]["metricClient"]["metrics"].children()) {
      size_t id;
      child["id"] >> id;
      ids.push_back(id);
    }
    size_t i = 0;
    while (running) {
      // Metric MetricArray[ids.size()];
      // auto dummy = MetricArray;
      // for (size_t id : ids) {
      //   *dummy++ = {
      //       id, static_cast<double>(rand()) / static_cast<double>(RAND_MAX)};
      // }
      // dummy = MetricArray;
      // int curid = 0;
      // while (dummy - MetricArray < 256 - sizeof(Metric) &&
      //        curid++ < ids.size()) {
      //   dummy++;
      // }

      // srcBuffer.putBytes(0, reinterpret_cast<std::uint8_t *>(MetricArray),
      //                    sizeof(Metric) * curid);
      Metric metric{ids[rand() % ids.size()],
                    static_cast<double>(rand()) / static_cast<double>(RAND_MAX),
                    std::chrono::system_clock::now()};

      srcBuffer.putBytes(0, reinterpret_cast<std::uint8_t *>(&metric),
                         sizeof(Metric));

      std::cout << "offering " << i++ << " - ";
      std::cout.flush();

      // const std::int64_t result =
      //     publication->offer(srcBuffer, 0, sizeof(Metric) * curid);
      const std::int64_t result =
          publication->offer(srcBuffer, 0, sizeof(Metric));

      if (result > 0) {
        std::cout << "send\n";

      } else if (BACK_PRESSURED == result) {
        std::cout << "Offer failed due to back pressure" << std::endl;
      } else if (NOT_CONNECTED == result) {
        std::cout
            << "Offer failed because publisher is not connected to a subscriber"
            << std::endl;
      } else if (ADMIN_ACTION == result) {
        std::cout
            << "Offer failed because of an administration action in the system"
            << std::endl;
      } else if (PUBLICATION_CLOSED == result) {
        std::cout << "Offer failed because publication is closed" << std::endl;
      } else {
        std::cout << "Offer failed due to unknown reason " << result
                  << std::endl;
      }

      if (!publication->isConnected()) {
        std::cout << "No active subscribers detected" << std::endl;
      }

      std::this_thread::sleep_for(std::chrono::milliseconds(50));
    }

    std::cout << "Done sending." << std::endl;

    // if (settings.lingerTimeoutMs > 0) {
    //   std::cout << "Lingering for " << settings.lingerTimeoutMs
    //             << " milliseconds." << std::endl;
    //   std::this_thread::sleep_for(
    //       std::chrono::milliseconds(settings.lingerTimeoutMs));
    // }
  } catch (const CommandOptionException &e) {
    std::cerr << "ERROR: " << e.what() << std::endl << std::endl;
    cp.displayOptionsHelp(std::cerr);
    return -1;
  } catch (const SourcedException &e) {
    std::cerr << "FAILED: " << e.what() << " : " << e.where() << std::endl;
    return -1;
  } catch (const std::exception &e) {
    std::cerr << "FAILED: " << e.what() << " : " << std::endl;
    return -1;
  }

  return 0;
}