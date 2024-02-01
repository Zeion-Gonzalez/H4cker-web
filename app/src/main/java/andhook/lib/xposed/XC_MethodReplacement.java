package andhook.lib.xposed;

import andhook.lib.xposed.XC_MethodHook;

/* loaded from: classes2.dex */
public abstract class XC_MethodReplacement extends XC_MethodHook {
    public static final XC_MethodReplacement DO_NOTHING = new XC_MethodReplacement(20000) { // from class: andhook.lib.xposed.XC_MethodReplacement.1
        @Override // andhook.lib.xposed.XC_MethodReplacement
        protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
            return null;
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // andhook.lib.xposed.XC_MethodHook
    public final void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
    }

    protected abstract Object replaceHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable;

    public XC_MethodReplacement() {
    }

    public XC_MethodReplacement(int i) {
        super(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // andhook.lib.xposed.XC_MethodHook
    public final void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
        try {
            methodHookParam.setResult(replaceHookedMethod(methodHookParam));
        } catch (Throwable th) {
            methodHookParam.setThrowable(th);
        }
    }

    public static XC_MethodReplacement returnConstant(Object obj) {
        return returnConstant(50, obj);
    }

    public static XC_MethodReplacement returnConstant(int i, final Object obj) {
        return new XC_MethodReplacement(i) { // from class: andhook.lib.xposed.XC_MethodReplacement.2
            @Override // andhook.lib.xposed.XC_MethodReplacement
            protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                return obj;
            }
        };
    }
}
