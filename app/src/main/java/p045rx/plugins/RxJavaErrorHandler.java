package p045rx.plugins;

import p045rx.annotations.Beta;
import p045rx.exceptions.Exceptions;

/* loaded from: classes.dex */
public abstract class RxJavaErrorHandler {
    protected static final String ERROR_IN_RENDERING_SUFFIX = ".errorRendering";

    @Deprecated
    public void handleError(Throwable e) {
    }

    @Beta
    public final String handleOnNextValueRendering(Object item) {
        try {
            return render(item);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return item.getClass().getName() + ERROR_IN_RENDERING_SUFFIX;
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            return item.getClass().getName() + ERROR_IN_RENDERING_SUFFIX;
        }
    }

    @Beta
    protected String render(Object item) throws InterruptedException {
        return null;
    }
}
