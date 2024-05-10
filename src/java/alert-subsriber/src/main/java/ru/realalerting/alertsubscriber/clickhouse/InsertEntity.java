package ru.realalerting.alertsubscriber.clickhouse;

import org.apache.http.entity.AbstractHttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author Karbayev Saruar
 */
public class InsertEntity extends AbstractHttpEntity {

    private final byte[] rows;

    public InsertEntity(byte[] rows) {
        this.rows = rows;
    }

    public static InsertEntity of(ByteBuffer buffer) {
        buffer.flip();
        final byte[] bb = new byte[buffer.remaining()];
        buffer.get(bb);
        return new InsertEntity(buffer.array());
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public long getContentLength() {
        return -1;
    }

    @Override
    public InputStream getContent() throws IOException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(rows);
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

}