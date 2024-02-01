package org.jcodec.codecs.mjpeg.tools;

/* loaded from: classes.dex */
public class Asserts {
    public static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new IllegalStateException("assert failed: " + expected + " != " + actual);
        }
    }

    public static void assertInRange(String message, int low, int up, int val) {
        if (val < low || val > up) {
            throw new IllegalStateException(message);
        }
    }

    public static void assertEpsilonEquals(int[] expected, int[] actual, int eps) {
        if (expected.length != actual.length) {
            throw new IllegalStateException("arrays of different size");
        }
        for (int i = 0; i < expected.length; i++) {
            int e = expected[i];
            int a = actual[i];
            if (Math.abs(e - a) > eps) {
                throw new IllegalStateException("array element out of expected diff range");
            }
        }
    }

    public static void assertEpsilonEquals(byte[] expected, byte[] actual, int eps) {
        if (expected.length != actual.length) {
            throw new IllegalStateException("arrays of different size");
        }
        for (int i = 0; i < expected.length; i++) {
            int e = expected[i] & 255;
            int a = actual[i] & 255;
            if (Math.abs(e - a) > eps) {
                throw new IllegalStateException("array element out of expected diff range: " + Math.abs(e - a));
            }
        }
    }
}
