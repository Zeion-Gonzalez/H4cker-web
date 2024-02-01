package com.squareup.mimecraft;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/* loaded from: classes.dex */
public final class Multipart implements Part {
    private final String boundary;
    private final Map<String, String> headers;
    private final List<Part> parts;

    /* loaded from: classes.dex */
    public enum Type {
        MIXED("mixed"),
        ALTERNATIVE("alternative"),
        DIGEST("digest"),
        PARALLEL("parallel"),
        FORM("form-data");

        final String contentType;

        Type(String contentType) {
            this.contentType = contentType;
        }
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private final String boundary;
        private final List<Part> parts;
        private Type type;

        public Builder() {
            this(UUID.randomUUID().toString());
        }

        public Builder(String boundary) {
            this.parts = new ArrayList();
            this.type = Type.MIXED;
            this.boundary = boundary;
        }

        public Builder type(Type type) {
            Utils.isNotNull(type, "Type must not be null.");
            this.type = type;
            return this;
        }

        public Builder addPart(Part part) {
            Utils.isNotNull(part, "Part must not be null.");
            this.parts.add(part);
            return this;
        }

        public Multipart build() {
            if (this.parts.isEmpty()) {
                throw new IllegalStateException("Multipart body must have at least one part.");
            }
            return new Multipart(this.type, this.parts, this.boundary);
        }
    }

    private Multipart(Type type, List<Part> parts, String boundary) {
        Utils.isNotNull(type, "Multipart type must not be null.");
        this.parts = parts;
        this.headers = Collections.singletonMap("Content-Type", "multipart/" + type.contentType + "; boundary=" + boundary);
        this.boundary = boundary;
    }

    @Override // com.squareup.mimecraft.Part
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Override // com.squareup.mimecraft.Part
    public void writeBodyTo(OutputStream stream) throws IOException {
        byte[] boundary = this.boundary.getBytes("UTF-8");
        boolean first = true;
        for (Part part : this.parts) {
            writeBoundary(stream, boundary, first, false);
            writePart(stream, part);
            first = false;
        }
        writeBoundary(stream, boundary, false, true);
    }

    private static void writeBoundary(OutputStream out, byte[] boundary, boolean first, boolean last) throws IOException {
        if (!first) {
            out.write(13);
            out.write(10);
        }
        out.write(45);
        out.write(45);
        out.write(boundary);
        if (last) {
            out.write(45);
            out.write(45);
        } else {
            out.write(13);
            out.write(10);
        }
    }

    private static void writePart(OutputStream out, Part part) throws IOException {
        Map<String, String> headers = part.getHeaders();
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                out.write(header.getKey().getBytes("UTF-8"));
                out.write(58);
                out.write(32);
                out.write(header.getValue().getBytes("UTF-8"));
                out.write(13);
                out.write(10);
            }
        }
        out.write(13);
        out.write(10);
        part.writeBodyTo(out);
    }
}
