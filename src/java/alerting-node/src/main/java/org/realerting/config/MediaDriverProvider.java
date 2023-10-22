package org.realerting.config;

import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;

@Getter
@ApplicationScoped
public class MediaDriverProvider implements AutoCloseable {

    private final MediaDriver mediaDriver;
    private final Aeron aeron;

    MediaDriverProvider() {
        mediaDriver = MediaDriver.launchEmbedded();
        aeron = Aeron.connect(new Aeron.Context().aeronDirectoryName(mediaDriver.aeronDirectoryName()));
    }

    @Override
    public void close() throws Exception {
        aeron.close();
        mediaDriver.close();
    }
}
