package com.squareup.mimecraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes.dex */
public interface Part {
    Map<String, String> getHeaders();

    void writeBodyTo(OutputStream outputStream) throws IOException;

    /* loaded from: classes.dex */
    public static class Builder {
        private static final int BUFFER_SIZE = 4096;
        private byte[] bodyBytes;
        private File bodyFile;
        private Multipart bodyMultipart;
        private InputStream bodyStream;
        private boolean hasBody = false;
        private String headerDisposition;
        private String headerEncoding;
        private String headerLanguage;
        int headerLength;
        private String headerType;

        private void checkSetBody() {
            if (this.hasBody) {
                throw new IllegalStateException("Only one body per part.");
            }
            this.hasBody = true;
        }

        public Builder contentType(String type) {
            Utils.isNotEmpty(type, "Type must not be empty.");
            Utils.isNull(this.headerType, "Type header already set.");
            Utils.isNull(this.bodyMultipart, "Type cannot be set with multipart body.");
            this.headerType = type;
            return this;
        }

        public Builder contentLength(int length) {
            if (length <= 0) {
                throw new IllegalStateException("Length must be greater than zero.");
            }
            Utils.isNotZero(this.headerLength, "Length header already set.");
            this.headerLength = length;
            return this;
        }

        public Builder contentLanguage(String language) {
            Utils.isNotEmpty(language, "Language must not be empty.");
            Utils.isNull(this.headerLanguage, "Language header already set.");
            this.headerLanguage = language;
            return this;
        }

        public Builder contentEncoding(String encoding) {
            Utils.isNotEmpty(encoding, "Encoding must not be empty.");
            Utils.isNull(this.headerEncoding, "Encoding header already set.");
            this.headerEncoding = encoding;
            return this;
        }

        public Builder contentDisposition(String disposition) {
            Utils.isNotEmpty(disposition, "Disposition must not be empty.");
            Utils.isNull(this.headerDisposition, "Disposition header already set.");
            this.headerDisposition = disposition;
            return this;
        }

        public Builder body(File body) {
            Utils.isNotNull(body, "File body must not be null.");
            checkSetBody();
            this.bodyFile = body;
            return this;
        }

        public Builder body(InputStream body) {
            Utils.isNotNull(body, "Stream body must not be null.");
            checkSetBody();
            this.bodyStream = body;
            return this;
        }

        public Builder body(String body) {
            Utils.isNotNull(body, "String body must not be null.");
            checkSetBody();
            try {
                byte[] bytes = body.getBytes("UTF-8");
                this.bodyBytes = bytes;
                this.headerLength = bytes.length;
                return this;
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Unable to convert input to UTF-8: " + body, e);
            }
        }

        public Builder body(byte[] body) {
            Utils.isNotNull(body, "Byte array body must not be null.");
            checkSetBody();
            this.bodyBytes = body;
            this.headerLength = body.length;
            return this;
        }

        public Builder body(Multipart body) {
            Utils.isNotNull(body, "Multipart body must not be null.");
            if (this.headerType != null) {
                throw new IllegalStateException("Content type must not be explicitly set for multipart body.");
            }
            checkSetBody();
            this.headerType = null;
            this.bodyMultipart = body;
            return this;
        }

        public Part build() {
            Map<String, String> headers = new LinkedHashMap<>();
            if (this.headerDisposition != null) {
                headers.put("Content-Disposition", this.headerDisposition);
            }
            if (this.headerType != null) {
                headers.put("Content-Type", this.headerType);
            }
            if (this.headerLength != 0) {
                headers.put("Content-Length", Integer.toString(this.headerLength));
            }
            if (this.headerLanguage != null) {
                headers.put("Content-Language", this.headerLanguage);
            }
            if (this.headerEncoding != null) {
                headers.put("Content-Transfer-Encoding", this.headerEncoding);
            }
            if (this.bodyBytes != null) {
                return new BytesPart(headers, this.bodyBytes);
            }
            if (this.bodyStream != null) {
                return new StreamPart(headers, this.bodyStream);
            }
            if (this.bodyFile != null) {
                return new FilePart(headers, this.bodyFile);
            }
            if (this.bodyMultipart != null) {
                headers.putAll(this.bodyMultipart.getHeaders());
                return new PartPart(headers, this.bodyMultipart);
            }
            throw new IllegalStateException("Part required body to be set.");
        }

        /* loaded from: classes.dex */
        private static abstract class PartImpl implements Part {
            private final Map<String, String> headers;

            protected PartImpl(Map<String, String> headers) {
                this.headers = headers;
            }

            @Override // com.squareup.mimecraft.Part
            public Map<String, String> getHeaders() {
                return this.headers;
            }
        }

        /* loaded from: classes.dex */
        private static final class PartPart extends PartImpl {
            private final Part body;

            protected PartPart(Map<String, String> headers, Part body) {
                super(headers);
                this.body = body;
            }

            @Override // com.squareup.mimecraft.Part
            public void writeBodyTo(OutputStream stream) throws IOException {
                this.body.writeBodyTo(stream);
            }
        }

        /* loaded from: classes.dex */
        static final class BytesPart extends PartImpl {
            private final byte[] contents;

            BytesPart(Map<String, String> headers, byte[] contents) {
                super(headers);
                this.contents = contents;
            }

            @Override // com.squareup.mimecraft.Part
            public void writeBodyTo(OutputStream out) throws IOException {
                out.write(this.contents);
            }
        }

        /* loaded from: classes.dex */
        private static final class StreamPart extends PartImpl {
            private final byte[] buffer;

            /* renamed from: in */
            private final InputStream f1434in;

            private StreamPart(Map<String, String> headers, InputStream in) {
                super(headers);
                this.buffer = new byte[4096];
                this.f1434in = in;
            }

            @Override // com.squareup.mimecraft.Part
            public void writeBodyTo(OutputStream out) throws IOException {
                Utils.copyStream(this.f1434in, out, this.buffer);
            }
        }

        /* loaded from: classes.dex */
        private static final class FilePart extends PartImpl {
            private final byte[] buffer;
            private final File file;

            private FilePart(Map<String, String> headers, File file) {
                super(headers);
                this.buffer = new byte[4096];
                this.file = file;
            }

            @Override // com.squareup.mimecraft.Part
            public void writeBodyTo(OutputStream out) throws IOException {
                InputStream in = null;
                try {
                    InputStream in2 = new FileInputStream(this.file);
                    try {
                        Utils.copyStream(in2, out, this.buffer);
                        if (in2 != null) {
                            try {
                                in2.close();
                            } catch (IOException e) {
                            }
                        }
                    } catch (Throwable th) {
                        th = th;
                        in = in2;
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e2) {
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            }
        }
    }
}
