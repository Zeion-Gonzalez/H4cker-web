package com.squareup.mimecraft;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

/* loaded from: classes.dex */
public final class FormEncoding implements Part {
    private static final Map<String, String> HEADERS = Collections.singletonMap("Content-Type", "application/x-www-form-urlencoded");
    private final byte[] data;

    /* loaded from: classes.dex */
    public static class Builder {
        private final StringBuilder content = new StringBuilder();

        public Builder add(String name, String value) {
            if (this.content.length() > 0) {
                this.content.append('&');
            }
            try {
                this.content.append(URLEncoder.encode(name, "UTF-8")).append('=').append(URLEncoder.encode(value, "UTF-8"));
                return this;
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError(e);
            }
        }

        public FormEncoding build() {
            if (this.content.length() == 0) {
                throw new IllegalStateException("Form encoded body must have at least one part.");
            }
            return new FormEncoding(this.content.toString());
        }
    }

    private FormEncoding(String data) {
        try {
            this.data = data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unable to convert input to UTF-8: " + data, e);
        }
    }

    @Override // com.squareup.mimecraft.Part
    public Map<String, String> getHeaders() {
        return HEADERS;
    }

    @Override // com.squareup.mimecraft.Part
    public void writeBodyTo(OutputStream stream) throws IOException {
        stream.write(this.data);
    }
}
