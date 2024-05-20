package ru.realalerting.protocol;

import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static java.util.Objects.isNull;


/**
 * @author Karbayev Saruar
 */
public class RealAlertingDriverContext implements AutoCloseable {
    private final Logger LOG = LoggerFactory.getLogger(RealAlertingDriverContext.class);
    private Aeron aeron;
    private MediaDriver mediaDriver;

    private void initializeFromFile(String mediaPath) throws IOException {
        final MediaDriver.Context mediaDriverCtx = new MediaDriver.Context()
                .spiesSimulateConnection(true)
                .aeronDirectoryName(mediaPath)
                .dirDeleteOnStart(true)
                .dirDeleteOnShutdown(true)
                .threadingMode(ThreadingMode.SHARED);
        try {
            mediaDriver = MediaDriver.launchEmbedded(mediaDriverCtx);
            try {
                aeron = Aeron.connect(new Aeron.Context().aeronDirectoryName(mediaDriver.aeronDirectoryName()));
            } catch (Exception e) {
                aeron.close();
                LOG.warn("Failed to connect to aeron: {}", mediaPath);
                throw e;
            }
        } catch (Exception e) {
            mediaDriver.close();
            LOG.warn("Failed to launch media driver: {}", mediaPath);
            throw e;
        }
    }

    public RealAlertingDriverContext(String mediaPath) throws IOException {
        initializeFromFile(mediaPath);
    }

    public RealAlertingDriverContext(Aeron aeron, MediaDriver mediaDriver) {
        this.aeron = aeron;
        this.mediaDriver = mediaDriver;
    }

    public Aeron getAeron() {
        return aeron;
    }

    public MediaDriver getMediaDriver() {
        return mediaDriver;
    }

    @Override
    public void close() throws IOException {
        if (!isNull(aeron)) {
            aeron.close();
            mediaDriver.close();
        }
    }
}
