package p045rx.observers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import p045rx.Notification;
import p045rx.Observer;
import p045rx.Subscriber;
import p045rx.annotations.Experimental;
import p045rx.exceptions.CompositeException;

/* loaded from: classes.dex */
public class TestSubscriber<T> extends Subscriber<T> {
    private static final Observer<Object> INERT = new Observer<Object>() { // from class: rx.observers.TestSubscriber.1
        @Override // p045rx.Observer
        public void onCompleted() {
        }

        @Override // p045rx.Observer
        public void onError(Throwable e) {
        }

        @Override // p045rx.Observer
        public void onNext(Object t) {
        }
    };
    private int completions;
    private final Observer<T> delegate;
    private final List<Throwable> errors;
    private volatile Thread lastSeenThread;
    private final CountDownLatch latch;
    private volatile int valueCount;
    private final List<T> values;

    public TestSubscriber(long initialRequest) {
        this(INERT, initialRequest);
    }

    public TestSubscriber(Observer<T> delegate, long initialRequest) {
        this.latch = new CountDownLatch(1);
        if (delegate == null) {
            throw new NullPointerException();
        }
        this.delegate = delegate;
        if (initialRequest >= 0) {
            request(initialRequest);
        }
        this.values = new ArrayList();
        this.errors = new ArrayList();
    }

    public TestSubscriber(Subscriber<T> delegate) {
        this(delegate, -1L);
    }

    public TestSubscriber(Observer<T> delegate) {
        this(delegate, -1L);
    }

    public TestSubscriber() {
        this(-1L);
    }

    public static <T> TestSubscriber<T> create() {
        return new TestSubscriber<>();
    }

    public static <T> TestSubscriber<T> create(long initialRequest) {
        return new TestSubscriber<>(initialRequest);
    }

    public static <T> TestSubscriber<T> create(Observer<T> delegate, long initialRequest) {
        return new TestSubscriber<>(delegate, initialRequest);
    }

    public static <T> TestSubscriber<T> create(Subscriber<T> delegate) {
        return new TestSubscriber<>((Subscriber) delegate);
    }

    public static <T> TestSubscriber<T> create(Observer<T> delegate) {
        return new TestSubscriber<>(delegate);
    }

    @Override // p045rx.Observer
    public void onCompleted() {
        try {
            this.completions++;
            this.lastSeenThread = Thread.currentThread();
            this.delegate.onCompleted();
        } finally {
            this.latch.countDown();
        }
    }

    @Deprecated
    public List<Notification<T>> getOnCompletedEvents() {
        int c = this.completions;
        List<Notification<T>> result = new ArrayList<>(c != 0 ? c : 1);
        for (int i = 0; i < c; i++) {
            result.add(Notification.createOnCompleted());
        }
        return result;
    }

    @Experimental
    public final int getCompletions() {
        return this.completions;
    }

    @Override // p045rx.Observer
    public void onError(Throwable e) {
        try {
            this.lastSeenThread = Thread.currentThread();
            this.errors.add(e);
            this.delegate.onError(e);
        } finally {
            this.latch.countDown();
        }
    }

    public List<Throwable> getOnErrorEvents() {
        return this.errors;
    }

    @Override // p045rx.Observer
    public void onNext(T t) {
        this.lastSeenThread = Thread.currentThread();
        this.values.add(t);
        this.valueCount = this.values.size();
        this.delegate.onNext(t);
    }

    public final int getValueCount() {
        return this.valueCount;
    }

    public void requestMore(long n) {
        request(n);
    }

    public List<T> getOnNextEvents() {
        return this.values;
    }

    public void assertReceivedOnNext(List<T> items) {
        if (this.values.size() != items.size()) {
            assertionError("Number of items does not match. Provided: " + items.size() + "  Actual: " + this.values.size() + ".\nProvided values: " + items + IOUtils.LINE_SEPARATOR_UNIX + "Actual values: " + this.values + IOUtils.LINE_SEPARATOR_UNIX);
        }
        for (int i = 0; i < items.size(); i++) {
            assertItem(items.get(i), i);
        }
    }

    private void assertItem(T expected, int i) {
        T actual = this.values.get(i);
        if (expected == null) {
            if (actual != null) {
                assertionError("Value at index: " + i + " expected to be [null] but was: [" + actual + "]\n");
            }
        } else if (!expected.equals(actual)) {
            assertionError("Value at index: " + i + " expected to be [" + expected + "] (" + expected.getClass().getSimpleName() + ") but was: [" + actual + "] (" + (actual != null ? actual.getClass().getSimpleName() : "null") + ")\n");
        }
    }

    @Experimental
    public final boolean awaitValueCount(int expected, long timeout, TimeUnit unit) throws InterruptedException {
        while (timeout != 0 && this.valueCount < expected) {
            unit.sleep(1L);
            timeout--;
        }
        return this.valueCount >= expected;
    }

    public void assertTerminalEvent() {
        if (this.errors.size() > 1) {
            assertionError("Too many onError events: " + this.errors.size());
        }
        if (this.completions > 1) {
            assertionError("Too many onCompleted events: " + this.completions);
        }
        if (this.completions == 1 && this.errors.size() == 1) {
            assertionError("Received both an onError and onCompleted. Should be one or the other.");
        }
        if (this.completions == 0 && this.errors.isEmpty()) {
            assertionError("No terminal events received.");
        }
    }

    public void assertUnsubscribed() {
        if (!isUnsubscribed()) {
            assertionError("Not unsubscribed.");
        }
    }

    public void assertNoErrors() {
        List<Throwable> onErrorEvents = getOnErrorEvents();
        if (!onErrorEvents.isEmpty()) {
            assertionError("Unexpected onError events");
        }
    }

    public void awaitTerminalEvent() {
        try {
            this.latch.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException("Interrupted", e);
        }
    }

    public void awaitTerminalEvent(long timeout, TimeUnit unit) {
        try {
            this.latch.await(timeout, unit);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Interrupted", e);
        }
    }

    public void awaitTerminalEventAndUnsubscribeOnTimeout(long timeout, TimeUnit unit) {
        try {
            boolean result = this.latch.await(timeout, unit);
            if (!result) {
                unsubscribe();
            }
        } catch (InterruptedException e) {
            unsubscribe();
        }
    }

    public Thread getLastSeenThread() {
        return this.lastSeenThread;
    }

    public void assertCompleted() {
        int s = this.completions;
        if (s == 0) {
            assertionError("Not completed!");
        } else if (s > 1) {
            assertionError("Completed multiple times: " + s);
        }
    }

    public void assertNotCompleted() {
        int s = this.completions;
        if (s == 1) {
            assertionError("Completed!");
        } else if (s > 1) {
            assertionError("Completed multiple times: " + s);
        }
    }

    public void assertError(Class<? extends Throwable> clazz) {
        List<Throwable> err = this.errors;
        if (err.isEmpty()) {
            assertionError("No errors");
            return;
        }
        if (err.size() > 1) {
            AssertionError ae = new AssertionError("Multiple errors: " + err.size());
            ae.initCause(new CompositeException(err));
            throw ae;
        }
        if (!clazz.isInstance(err.get(0))) {
            AssertionError ae2 = new AssertionError("Exceptions differ; expected: " + clazz + ", actual: " + err.get(0));
            ae2.initCause(err.get(0));
            throw ae2;
        }
    }

    public void assertError(Throwable throwable) {
        List<Throwable> err = this.errors;
        if (err.isEmpty()) {
            assertionError("No errors");
        } else if (err.size() > 1) {
            assertionError("Multiple errors");
        } else if (!throwable.equals(err.get(0))) {
            assertionError("Exceptions differ; expected: " + throwable + ", actual: " + err.get(0));
        }
    }

    public void assertNoTerminalEvent() {
        List<Throwable> err = this.errors;
        int s = this.completions;
        if (!err.isEmpty() || s > 0) {
            if (err.isEmpty()) {
                assertionError("Found " + err.size() + " errors and " + s + " completion events instead of none");
            } else if (err.size() == 1) {
                assertionError("Found " + err.size() + " errors and " + s + " completion events instead of none");
            } else {
                assertionError("Found " + err.size() + " errors and " + s + " completion events instead of none");
            }
        }
    }

    public void assertNoValues() {
        int s = this.values.size();
        if (s != 0) {
            assertionError("No onNext events expected yet some received: " + s);
        }
    }

    public void assertValueCount(int count) {
        int s = this.values.size();
        if (s != count) {
            assertionError("Number of onNext events differ; expected: " + count + ", actual: " + s);
        }
    }

    public void assertValues(T... values) {
        assertReceivedOnNext(Arrays.asList(values));
    }

    public void assertValue(T value) {
        assertReceivedOnNext(Collections.singletonList(value));
    }

    final void assertionError(String message) {
        StringBuilder b = new StringBuilder(message.length() + 32);
        b.append(message).append(" (");
        int c = this.completions;
        b.append(c).append(" completion");
        if (c != 1) {
            b.append('s');
        }
        b.append(')');
        if (!this.errors.isEmpty()) {
            int size = this.errors.size();
            b.append(" (+").append(size).append(" error");
            if (size != 1) {
                b.append('s');
            }
            b.append(')');
        }
        AssertionError ae = new AssertionError(b.toString());
        if (!this.errors.isEmpty()) {
            if (this.errors.size() == 1) {
                ae.initCause(this.errors.get(0));
                throw ae;
            }
            ae.initCause(new CompositeException(this.errors));
            throw ae;
        }
        throw ae;
    }

    @Experimental
    public final void assertValuesAndClear(T expectedFirstValue, T... expectedRestValues) {
        int n = expectedRestValues.length + 1;
        assertValueCount(n);
        assertItem(expectedFirstValue, 0);
        for (int i = 0; i < expectedRestValues.length; i++) {
            assertItem(expectedRestValues[i], i + 1);
        }
        this.values.clear();
    }
}
