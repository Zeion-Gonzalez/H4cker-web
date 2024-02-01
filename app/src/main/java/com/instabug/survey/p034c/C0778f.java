package com.instabug.survey.p034c;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.instabug.library.core.InstabugCore;
import com.instabug.library.internal.storage.cache.UserAttributesCacheManager;
import com.instabug.library.logging.InstabugUserEventLogger;
import com.instabug.library.model.State;
import com.instabug.library.settings.SettingsManager;
import com.instabug.library.util.InstabugDateFormatter;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.StringUtility;
import com.instabug.survey.cache.SurveysCacheManager;
import com.instabug.survey.p032a.C0767a;
import com.instabug.survey.p032a.C0769c;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* compiled from: SurveysValidator.java */
/* renamed from: com.instabug.survey.c.f */
/* loaded from: classes.dex */
public class C0778f {

    /* renamed from: a */
    private String f1357a;

    /* renamed from: b */
    private String f1358b;

    public C0778f(String str, String str2) {
        this.f1358b = str;
        this.f1357a = str2;
    }

    @Nullable
    /* renamed from: a */
    public C0769c m2007a() throws ParseException {
        for (C0769c c0769c : SurveysCacheManager.getNotAnsweredSurveys()) {
            if (m2013a(c0769c)) {
                return c0769c;
            }
        }
        return null;
    }

    /* renamed from: b */
    public List<C0769c> m2017b() throws ParseException {
        List<C0769c> notAnsweredSurveys = SurveysCacheManager.getNotAnsweredSurveys();
        ArrayList arrayList = new ArrayList();
        for (C0769c c0769c : notAnsweredSurveys) {
            if (m2013a(c0769c)) {
                arrayList.add(c0769c);
            }
        }
        return arrayList;
    }

    /* renamed from: c */
    public boolean m2019c() throws ParseException {
        return m2017b().size() > 0;
    }

    @VisibleForTesting
    /* renamed from: a */
    boolean m2013a(C0769c c0769c) throws ParseException {
        boolean m2016a = m2016a(c0769c.m1952c(), c0769c.m1959f(), c0769c.m1963j());
        boolean m2015a = m2015a(c0769c.m1956d(), c0769c.m1959f());
        boolean m2001b = m2001b(c0769c.m1958e(), c0769c.m1959f());
        boolean m1965l = c0769c.m1965l();
        boolean m2014a = m2014a(c0769c.m1952c());
        if (m1965l && !m2014a) {
            return false;
        }
        if ((c0769c.m1958e() == null || c0769c.m1958e().size() <= 0) && c0769c.m1956d().size() <= 0 && c0769c.m1952c().size() <= 0) {
            return true;
        }
        String m1959f = c0769c.m1959f();
        char c = 65535;
        switch (m1959f.hashCode()) {
            case 3555:
                if (m1959f.equals("or")) {
                    c = 1;
                    break;
                }
                break;
            case 96727:
                if (m1959f.equals("and")) {
                    c = 0;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return m2016a && m2015a && m2001b;
            case 1:
                return m2016a || m2015a || m2001b;
            default:
                return m2016a && m2015a && m2001b;
        }
    }

    @VisibleForTesting
    /* renamed from: a */
    boolean m2016a(ArrayList<C0767a> arrayList, String str, long j) throws ParseException {
        boolean equals = str.equals("and");
        for (int i = 0; i < arrayList.size(); i++) {
            boolean m2009a = m2009a(arrayList.get(i), j);
            if (i == 0) {
                equals = m2009a;
            } else {
                char c = 65535;
                switch (str.hashCode()) {
                    case 3555:
                        if (str.equals("or")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 96727:
                        if (str.equals("and")) {
                            c = 0;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        equals &= m2009a;
                        continue;
                    case 1:
                        equals |= m2009a;
                        continue;
                    default:
                        equals &= m2009a;
                        continue;
                }
            }
        }
        return equals;
    }

    @VisibleForTesting
    /* renamed from: a */
    boolean m2009a(C0767a c0767a, long j) throws ParseException {
        String m1922a = c0767a.m1922a();
        char c = 65535;
        switch (m1922a.hashCode()) {
            case -1464712027:
                if (m1922a.equals("days_since_signup")) {
                    c = 4;
                    break;
                }
                break;
            case -901870406:
                if (m1922a.equals(State.KEY_APP_VERSION)) {
                    c = 0;
                    break;
                }
                break;
            case 3076014:
                if (m1922a.equals("date")) {
                    c = 1;
                    break;
                }
                break;
            case 96619420:
                if (m1922a.equals("email")) {
                    c = 2;
                    break;
                }
                break;
            case 1421955229:
                if (m1922a.equals("days_since_dismiss")) {
                    c = 5;
                    break;
                }
                break;
            case 1905908461:
                if (m1922a.equals("sessions_count")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return m2008a(c0767a);
            case 1:
                return m2018b(c0767a);
            case 2:
                return m2020c(c0767a);
            case 3:
                return m1997a(c0767a, SettingsManager.getInstance().getSessionsCount());
            case 4:
                return m2003e(c0767a);
            case 5:
                return m2010a(c0767a, Long.valueOf(j));
            default:
                return false;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @VisibleForTesting
    /* renamed from: a */
    boolean m2008a(C0767a c0767a) {
        char c;
        String m1998b = m1998b(c0767a.m1924b());
        String m1998b2 = m1998b(this.f1357a);
        if (m1998b != null) {
            try {
                int compareVersion = StringUtility.compareVersion(m1998b2, m1998b);
                String m1926c = c0767a.m1926c();
                switch (m1926c.hashCode()) {
                    case -1374681402:
                        if (m1926c.equals("greater_than")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case 96757556:
                        if (m1926c.equals("equal")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case 365984903:
                        if (m1926c.equals("less_than")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1614662344:
                        if (m1926c.equals("not_equal")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                        return compareVersion == 0;
                    case 1:
                        return compareVersion != 0;
                    case 2:
                        return compareVersion == 1;
                    case 3:
                        return compareVersion == -1;
                    default:
                        return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return m2011a(c0767a, this.f1358b);
    }

    /* renamed from: b */
    private String m1998b(String str) {
        Matcher matcher = Pattern.compile("\\d+(\\.\\d+)*").matcher(str);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    @VisibleForTesting
    /* renamed from: b */
    boolean m2018b(C0767a c0767a) throws ParseException {
        return m2012a(c0767a, new Date());
    }

    @VisibleForTesting
    /* renamed from: c */
    boolean m2020c(C0767a c0767a) throws ParseException {
        return m2011a(c0767a, InstabugCore.getUserEmail());
    }

    @VisibleForTesting
    /* renamed from: a */
    boolean m2010a(C0767a c0767a, Long l) throws ParseException {
        return m2000b(c0767a, l.longValue());
    }

    /* renamed from: a */
    private boolean m1997a(C0767a c0767a, int i) {
        return m2002c(c0767a, i);
    }

    /* renamed from: e */
    private boolean m2003e(C0767a c0767a) {
        return m2005g(c0767a);
    }

    @VisibleForTesting
    /* renamed from: a */
    boolean m2015a(ArrayList<C0767a> arrayList, String str) {
        boolean z;
        boolean equals = str.equals("and");
        for (int i = 0; i < arrayList.size(); i++) {
            boolean m2021d = m2021d(arrayList.get(i));
            if (i == 0) {
                equals = m2021d;
            } else {
                char c = 65535;
                switch (str.hashCode()) {
                    case 3555:
                        if (str.equals("or")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 96727:
                        if (str.equals("and")) {
                            c = 0;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        z = equals & m2021d;
                        break;
                    case 1:
                        z = equals | m2021d;
                        break;
                    default:
                        z = equals & m2021d;
                        break;
                }
                equals = z & m2021d;
            }
        }
        return equals;
    }

    @VisibleForTesting
    /* renamed from: d */
    boolean m2021d(C0767a c0767a) {
        HashMap<String, String> all = UserAttributesCacheManager.getAll();
        return all != null && all.containsKey(c0767a.m1922a()) && m2011a(c0767a, all.get(c0767a.m1922a()));
    }

    /* renamed from: b */
    private boolean m2001b(ArrayList<C0767a> arrayList, String str) throws ParseException {
        boolean z;
        boolean equals = str.equals("and");
        if (arrayList != null) {
            boolean z2 = equals;
            for (int i = 0; i < arrayList.size(); i++) {
                boolean m2004f = m2004f(arrayList.get(i));
                if (i == 0) {
                    z2 = m2004f;
                } else {
                    char c = 65535;
                    switch (str.hashCode()) {
                        case 3555:
                            if (str.equals("or")) {
                                c = 1;
                                break;
                            }
                            break;
                        case 96727:
                            if (str.equals("and")) {
                                c = 0;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            z = z2 & m2004f;
                            break;
                        case 1:
                            z = z2 | m2004f;
                            break;
                        default:
                            z = z2 & m2004f;
                            break;
                    }
                    z2 = z & m2004f;
                }
            }
            return z2;
        }
        return equals;
    }

    /* renamed from: f */
    private boolean m2004f(C0767a c0767a) throws ParseException {
        return m1999b(c0767a, m2006a(c0767a.m1922a()));
    }

    /* renamed from: a */
    int m2006a(String str) {
        try {
            Method declaredMethod = InstabugUserEventLogger.class.getDeclaredMethod("getLoggingEventCount", String.class);
            declaredMethod.setAccessible(true);
            return ((Integer) declaredMethod.invoke(InstabugUserEventLogger.getInstance(), str)).intValue();
        } catch (IllegalAccessException e) {
            InstabugSDKLogger.m1800e(C0778f.class, "METHOD NOT FOUND !");
            e.printStackTrace();
            return 0;
        } catch (NoSuchMethodException e2) {
            InstabugSDKLogger.m1800e(C0778f.class, "METHOD NOT FOUND !");
            e2.printStackTrace();
            return 0;
        } catch (InvocationTargetException e3) {
            InstabugSDKLogger.m1800e(C0778f.class, "METHOD NOT FOUND !");
            e3.printStackTrace();
            return 0;
        }
    }

    @VisibleForTesting
    /* renamed from: a */
    boolean m2011a(C0767a c0767a, String str) {
        if (c0767a.m1924b() == null || str == null) {
            return false;
        }
        String m1924b = c0767a.m1924b();
        String m1926c = c0767a.m1926c();
        char c = 65535;
        switch (m1926c.hashCode()) {
            case -630852760:
                if (m1926c.equals("not_contain")) {
                    c = 3;
                    break;
                }
                break;
            case 96757556:
                if (m1926c.equals("equal")) {
                    c = 0;
                    break;
                }
                break;
            case 951526612:
                if (m1926c.equals("contain")) {
                    c = 2;
                    break;
                }
                break;
            case 1614662344:
                if (m1926c.equals("not_equal")) {
                    c = 1;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return str.equals(m1924b);
            case 1:
                return !str.equals(m1924b);
            case 2:
                return str.contains(m1924b);
            case 3:
                return !str.contains(m1924b);
            default:
                return false;
        }
    }

    /* renamed from: b */
    private boolean m1999b(C0767a c0767a, int i) {
        if (c0767a.m1924b() == null) {
            return false;
        }
        int parseInt = Integer.parseInt(c0767a.m1924b());
        String m1926c = c0767a.m1926c();
        char c = 65535;
        switch (m1926c.hashCode()) {
            case -1374681402:
                if (m1926c.equals("greater_than")) {
                    c = 2;
                    break;
                }
                break;
            case 96757556:
                if (m1926c.equals("equal")) {
                    c = 0;
                    break;
                }
                break;
            case 365984903:
                if (m1926c.equals("less_than")) {
                    c = 3;
                    break;
                }
                break;
            case 1614662344:
                if (m1926c.equals("not_equal")) {
                    c = 1;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return i == parseInt;
            case 1:
                return i != parseInt;
            case 2:
                return i > parseInt;
            case 3:
                return i < parseInt;
            default:
                return false;
        }
    }

    @VisibleForTesting
    /* renamed from: a */
    boolean m2012a(C0767a c0767a, Date date) throws ParseException {
        if (c0767a.m1924b() == null || date == null) {
            return false;
        }
        Date standardizedDate = InstabugDateFormatter.getStandardizedDate(InstabugDateFormatter.getDate(c0767a.m1924b()));
        Date standardizedDate2 = InstabugDateFormatter.getStandardizedDate(date);
        String m1926c = c0767a.m1926c();
        char c = 65535;
        switch (m1926c.hashCode()) {
            case -1374681402:
                if (m1926c.equals("greater_than")) {
                    c = 2;
                    break;
                }
                break;
            case 96757556:
                if (m1926c.equals("equal")) {
                    c = 0;
                    break;
                }
                break;
            case 365984903:
                if (m1926c.equals("less_than")) {
                    c = 3;
                    break;
                }
                break;
            case 1614662344:
                if (m1926c.equals("not_equal")) {
                    c = 1;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return standardizedDate2.getDate() == standardizedDate.getDate();
            case 1:
                return standardizedDate2.getDate() != standardizedDate.getDate();
            case 2:
                return standardizedDate2.after(standardizedDate);
            case 3:
                return standardizedDate2.before(standardizedDate);
            default:
                return false;
        }
    }

    /* renamed from: b */
    private boolean m2000b(C0767a c0767a, long j) {
        if (c0767a.m1924b() == null) {
            return false;
        }
        if (j == 0) {
            return true;
        }
        int parseInt = Integer.parseInt(c0767a.m1924b());
        int m1988a = C0773a.m1988a(j);
        String m1926c = c0767a.m1926c();
        char c = 65535;
        switch (m1926c.hashCode()) {
            case -1374681402:
                if (m1926c.equals("greater_than")) {
                    c = 2;
                    break;
                }
                break;
            case 96757556:
                if (m1926c.equals("equal")) {
                    c = 0;
                    break;
                }
                break;
            case 365984903:
                if (m1926c.equals("less_than")) {
                    c = 3;
                    break;
                }
                break;
            case 1614662344:
                if (m1926c.equals("not_equal")) {
                    c = 1;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return m1988a == parseInt;
            case 1:
                return m1988a != parseInt;
            case 2:
                return m1988a > parseInt;
            case 3:
                return m1988a < parseInt;
            default:
                return false;
        }
    }

    /* renamed from: c */
    private boolean m2002c(C0767a c0767a, int i) {
        if (c0767a.m1924b() == null) {
            return false;
        }
        int parseInt = Integer.parseInt(c0767a.m1924b());
        String m1926c = c0767a.m1926c();
        char c = 65535;
        switch (m1926c.hashCode()) {
            case -1374681402:
                if (m1926c.equals("greater_than")) {
                    c = 2;
                    break;
                }
                break;
            case 96757556:
                if (m1926c.equals("equal")) {
                    c = 0;
                    break;
                }
                break;
            case 365984903:
                if (m1926c.equals("less_than")) {
                    c = 3;
                    break;
                }
                break;
            case 1614662344:
                if (m1926c.equals("not_equal")) {
                    c = 1;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return i == parseInt;
            case 1:
                return i != parseInt;
            case 2:
                return i > parseInt;
            case 3:
                return i < parseInt;
            default:
                return false;
        }
    }

    /* renamed from: g */
    private boolean m2005g(C0767a c0767a) {
        if (c0767a.m1924b() == null) {
            return false;
        }
        int parseInt = Integer.parseInt(c0767a.m1924b());
        int m1988a = C0773a.m1988a(InstabugCore.getFirstRunAt());
        String m1926c = c0767a.m1926c();
        char c = 65535;
        switch (m1926c.hashCode()) {
            case -1374681402:
                if (m1926c.equals("greater_than")) {
                    c = 2;
                    break;
                }
                break;
            case 96757556:
                if (m1926c.equals("equal")) {
                    c = 0;
                    break;
                }
                break;
            case 365984903:
                if (m1926c.equals("less_than")) {
                    c = 3;
                    break;
                }
                break;
            case 1614662344:
                if (m1926c.equals("not_equal")) {
                    c = 1;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return m1988a == parseInt;
            case 1:
                return m1988a != parseInt;
            case 2:
                return m1988a > parseInt;
            case 3:
                return m1988a < parseInt;
            default:
                return false;
        }
    }

    /* renamed from: a */
    boolean m2014a(ArrayList<C0767a> arrayList) throws ParseException {
        Iterator<C0767a> it = arrayList.iterator();
        while (it.hasNext()) {
            if (it.next().m1922a().equals("days_since_dismiss")) {
                return true;
            }
        }
        return false;
    }
}
