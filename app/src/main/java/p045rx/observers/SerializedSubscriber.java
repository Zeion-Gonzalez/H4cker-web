package p045rx.observers;

import p045rx.Observer;
import p045rx.Subscriber;

/* loaded from: classes.dex */
public class SerializedSubscriber<T> extends Subscriber<T> {

    /* renamed from: s */
    private final Observer<T> f1639s;

    public SerializedSubscriber(Subscriber<? super T> s) {
        this(s, true);
    }

    public SerializedSubscriber(Subscriber<? super T> s, boolean shareSubscriptions) {
        super(s, shareSubscriptions);
        this.f1639s = new SerializedObserver(s);
    }

    @Override // p045rx.Observer
    public void onCompleted() {
        this.f1639s.onCompleted();
    }

    @Override // p045rx.Observer
    public void onError(Throwable e) {
        this.f1639s.onError(e);
    }

    @Override // p045rx.Observer
    public void onNext(T t) {
        this.f1639s.onNext(t);
    }
}
