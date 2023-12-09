#include <csignal>
#include <cstdint>
#include <fstream>
#include <thread>

#include "Aeron.h"
#include "Config.hpp"

std::string Config::content =
    get_file_contents(R"(../config/SubscriberConfig.yml)");
ryml::Tree Config::tree =
    ryml::parse_in_place(ryml::to_substr(Config::content));

#include "FragmentAssembler.h"
#include "concurrent/SleepingIdleStrategy.h"
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

static const std::chrono::duration<long, std::milli> IDLE_SLEEP_MS(1);
static const int FRAGMENTS_LIMIT = 10;

struct Settings {
  std::string dirPrefix;
  std::string channel;
  std::int32_t streamId;
  static std::vector<size_t> ids;
};

std::vector<size_t> Settings::ids;

Settings parseCmdLine(CommandOptionParser &cp, int argc, char **argv) {
  cp.parse(argc, argv);

  Settings s;

  Config::tree["metricConfig"]["metricClient"]["config"]["channel"] >>
      s.channel;

  s.dirPrefix = cp.getOption(optPrefix).getParam(0, s.dirPrefix);

  Config::tree["metricConfig"]["metricClient"]["config"]["streamid"] >>
      s.streamId;

  for (auto child :
       Config::tree["metricConfig"]["metricClient"]["metrics"].children()) {
    size_t id;
    child["id"] >> id;
    s.ids.push_back(id);
  }
  return s;
}

fragment_handler_t printStringMessage() {
  return [&](const AtomicBuffer &buffer, util::index_t offset,
             util::index_t length, const Header &header) {
    Metric metric = *(reinterpret_cast<Metric *>(buffer.buffer() + offset));
    // Metric MetricArray[Settings::ids.size()];
    // for (size_t i = 0; i < Settings::ids.size(); ++i) {
    //   MetricArray[i] =
    //       *(reinterpret_cast<Metric *>(buffer.buffer() + offset) + i);
    // }
    std::chrono::time_point<std::chrono::system_clock, std::chrono::nanoseconds>
        tmp(std::chrono::system_clock::now());
    std::cout << "Message to stream " << header.streamId() << " from session "
              << header.sessionId() << "(" << length << "@" << offset << ") <<{"
              << metric.id << ", " << metric.val << ", "
              << std::chrono::duration_cast<std::chrono::milliseconds>(
                     std::chrono::system_clock::now() - metric.timestamp)
                     .count()
              // << std::chrono::system_clock::to_time_t(tmp - metric.timestamp)
              << "}>>\n";
    //           << "Metrics: \n";
    // for (size_t i = 0; i < Settings::ids.size(); ++i) {
    //   std::cout << "\t{" << MetricArray[i].id << ", " << MetricArray[i].val
    //             << "}>>\n";
    // }
  };
}

int main(int argc, char **argv) {
  CommandOptionParser cp;
  cp.addOption(
      CommandOption(optHelp, 0, 0, "            Displays help information."));
  cp.addOption(CommandOption(optPrefix, 1, 1,
                             "dir         Prefix directory for aeron driver."));
  cp.addOption(CommandOption(optChannel, 1, 1, "channel     Channel."));
  cp.addOption(CommandOption(optStreamId, 1, 1, "streamId    Stream ID."));

  try {
    Settings settings = parseCmdLine(cp, argc, argv);

    std::cout << "Subscribing to channel " << settings.channel
              << " on Stream ID " << settings.streamId << std::endl;

    aeron::Context context;

    if (!settings.dirPrefix.empty()) {
      context.aeronDir(settings.dirPrefix);
    }

    context.newSubscriptionHandler([](const std::string &channel,
                                      std::int32_t streamId,
                                      std::int64_t correlationId) {
      std::cout << "Subscription: " << channel << " " << correlationId << ":"
                << streamId << std::endl;
    });

    context.availableImageHandler([](Image &image) {
      std::cout << "Available image correlationId=" << image.correlationId()
                << " sessionId=" << image.sessionId();
      std::cout << " at position=" << image.position() << " from "
                << image.sourceIdentity() << std::endl;
    });

    context.unavailableImageHandler([](Image &image) {
      std::cout << "Unavailable image on correlationId="
                << image.correlationId() << " sessionId=" << image.sessionId();
      std::cout << " at position=" << image.position() << " from "
                << image.sourceIdentity() << std::endl;
    });

    std::shared_ptr<Aeron> aeron = Aeron::connect(context);
    signal(SIGINT, sigIntHandler);
    // add the subscription to start the process
    std::int64_t id =
        aeron->addSubscription(settings.channel, settings.streamId);

    std::shared_ptr<Subscription> subscription = aeron->findSubscription(id);
    // wait for the subscription to be valid
    while (!subscription) {
      std::this_thread::yield();
      subscription = aeron->findSubscription(id);
    }

    const std::int64_t channelStatus = subscription->channelStatus();

    std::cout << "Subscription channel status (id="
              << subscription->channelStatusId() << ") "
              << (channelStatus ==
                          ChannelEndpointStatus::CHANNEL_ENDPOINT_ACTIVE
                      ? "ACTIVE"
                      : std::to_string(channelStatus))
              << std::endl;

    FragmentAssembler fragmentAssembler(printStringMessage());
    fragment_handler_t handler = fragmentAssembler.handler();
    SleepingIdleStrategy idleStrategy(IDLE_SLEEP_MS);

    while (running) {
      const int fragmentsRead = subscription->poll(handler, FRAGMENTS_LIMIT);
      idleStrategy.idle(fragmentsRead);
    }
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