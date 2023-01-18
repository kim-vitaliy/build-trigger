package com.jetbrains.buildtrigger.http;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Обёртка с метаданными над SC-200 ответом команд, которые возвращают файл
 */
public class ResponseInputStream extends InputStream {

    @Nonnull
    private final InputStream inputStream;

    private final String contentType;

    private final String contentDisposition;

    private ResponseInputStream(@Nonnull InputStream inputStream,
                                String contentType,
                                String contentDisposition) {
        this.inputStream = requireNonNull(inputStream, "inputStream");
        this.contentType = contentType;
        this.contentDisposition = contentDisposition;
    }

    public static ResponseInputStream of(@Nonnull InputStream inputStream) {
        return new ResponseInputStream(inputStream, null, null);
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public int read(@NotNull byte[] b) throws IOException {
        return inputStream.read(b);
    }

    @Override
    public int read(@NotNull byte[] b, int off, int len) throws IOException {
        return inputStream.read(b, off, len);
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        return inputStream.readAllBytes();
    }

    @Override
    public byte[] readNBytes(int len) throws IOException {
        return inputStream.readNBytes(len);
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) throws IOException {
        return inputStream.readNBytes(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return inputStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public void mark(int readlimit) {
        inputStream.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        inputStream.reset();
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        return inputStream.transferTo(out);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Optional<String> getContentType() {
        return Optional.ofNullable(contentType);
    }

    public Optional<String> getContentDisposition() {
        return Optional.ofNullable(contentDisposition);
    }

    public static class Builder {

        private InputStream inputStream;

        private String contentType;

        private String contentDisposition;

        private Builder() {
        }

        public Builder inputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder contentDisposition(String contentDisposition) {
            this.contentDisposition = contentDisposition;
            return this;
        }

        public ResponseInputStream build() {
            return new ResponseInputStream(inputStream, contentType, contentDisposition);
        }
    }
}