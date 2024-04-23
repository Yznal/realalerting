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
public class AeronContext implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(AeronContext.class);
    private static Aeron aeron;
    private static MediaDriver mediaDriver;

    private static AeronContext context;

    public static AeronContext getInstance() {
        if (isNull(context)) {
            throw new IllegalStateException("AeronContext has not been initialized");
        }
        return context;
    }

    public static void initialize(String mediaPath) throws IOException {
        if (!isNull(context)) {
            throw new IllegalStateException("AeronContext has not been initialized");
        }
        final MediaDriver.Context mediaDriverCtx = new MediaDriver.Context()
                .aeronDirectoryName(mediaPath)
                .dirDeleteOnStart(true)
                .dirDeleteOnShutdown(true)
                .threadingMode(ThreadingMode.SHARED);
        Aeron aeron = null;
        MediaDriver mediaDriver = null;
        try {
            mediaDriver = MediaDriver.launchEmbedded(mediaDriverCtx);
            try {
                aeron = Aeron.connect(new Aeron.Context().aeronDirectoryName(mediaPath));
                context = new AeronContext(aeron, mediaDriver);
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

    private AeronContext(Aeron aeron, MediaDriver mediaDriver) {
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
