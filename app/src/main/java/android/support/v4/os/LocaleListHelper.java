package android.support.v4.os;

import android.os.Build;
import android.support.annotation.GuardedBy;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.Size;
import android.support.v4.media.MediaDescriptionCompat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

@RequiresApi(14)
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
final class LocaleListHelper {
    private static final int NUM_PSEUDO_LOCALES = 2;
    private static final String STRING_AR_XB = "ar-XB";
    private static final String STRING_EN_XA = "en-XA";
    private final Locale[] mList;
    @NonNull
    private final String mStringRepresentation;
    private static final Locale[] sEmptyList = new Locale[0];
    private static final LocaleListHelper sEmptyLocaleList = new LocaleListHelper(new Locale[0]);
    private static final Locale LOCALE_EN_XA = new Locale("en", "XA");
    private static final Locale LOCALE_AR_XB = new Locale("ar", "XB");
    private static final Locale EN_LATN = LocaleHelper.forLanguageTag("en-Latn");
    private static final Object sLock = new Object();
    @GuardedBy("sLock")
    private static LocaleListHelper sLastExplicitlySetLocaleList = null;
    @GuardedBy("sLock")
    private static LocaleListHelper sDefaultLocaleList = null;
    @GuardedBy("sLock")
    private static LocaleListHelper sDefaultAdjustedLocaleList = null;
    @GuardedBy("sLock")
    private static Locale sLastDefaultLocale = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public Locale get(int index) {
        if (index < 0 || index >= this.mList.length) {
            return null;
        }
        return this.mList[index];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public boolean isEmpty() {
        return this.mList.length == 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @IntRange(from = MediaDescriptionCompat.BT_FOLDER_TYPE_MIXED)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int size() {
        return this.mList.length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @IntRange(from = -1)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int indexOf(Locale locale) {
        for (int i = 0; i < this.mList.length; i++) {
            if (this.mList[i].equals(locale)) {
                return i;
            }
        }
        return -1;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof LocaleListHelper)) {
            return false;
        }
        Locale[] otherList = ((LocaleListHelper) other).mList;
        if (this.mList.length != otherList.length) {
            return false;
        }
        for (int i = 0; i < this.mList.length; i++) {
            if (!this.mList[i].equals(otherList[i])) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int result = 1;
        for (int i = 0; i < this.mList.length; i++) {
            result = (result * 31) + this.mList[i].hashCode();
        }
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < this.mList.length; i++) {
            sb.append(this.mList[i]);
            if (i < this.mList.length - 1) {
                sb.append(',');
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @NonNull
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public String toLanguageTags() {
        return this.mStringRepresentation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public LocaleListHelper(@NonNull Locale... list) {
        if (list.length == 0) {
            this.mList = sEmptyList;
            this.mStringRepresentation = "";
            return;
        }
        Locale[] localeList = new Locale[list.length];
        HashSet<Locale> seenLocales = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.length; i++) {
            Locale l = list[i];
            if (l == null) {
                throw new NullPointerException("list[" + i + "] is null");
            }
            if (seenLocales.contains(l)) {
                throw new IllegalArgumentException("list[" + i + "] is a repetition");
            }
            Locale localeClone = (Locale) l.clone();
            localeList[i] = localeClone;
            sb.append(LocaleHelper.toLanguageTag(localeClone));
            if (i < list.length - 1) {
                sb.append(',');
            }
            seenLocales.add(localeClone);
        }
        this.mList = localeList;
        this.mStringRepresentation = sb.toString();
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    LocaleListHelper(@NonNull Locale topLocale, LocaleListHelper otherLocales) {
        if (topLocale == null) {
            throw new NullPointerException("topLocale is null");
        }
        int inputLength = otherLocales == null ? 0 : otherLocales.mList.length;
        int topLocaleIndex = -1;
        int i = 0;
        while (true) {
            if (i >= inputLength) {
                break;
            }
            if (!topLocale.equals(otherLocales.mList[i])) {
                i++;
            } else {
                topLocaleIndex = i;
                break;
            }
        }
        int outputLength = inputLength + (topLocaleIndex == -1 ? 1 : 0);
        Locale[] localeList = new Locale[outputLength];
        localeList[0] = (Locale) topLocale.clone();
        if (topLocaleIndex == -1) {
            for (int i2 = 0; i2 < inputLength; i2++) {
                localeList[i2 + 1] = (Locale) otherLocales.mList[i2].clone();
            }
        } else {
            for (int i3 = 0; i3 < topLocaleIndex; i3++) {
                localeList[i3 + 1] = (Locale) otherLocales.mList[i3].clone();
            }
            for (int i4 = topLocaleIndex + 1; i4 < inputLength; i4++) {
                localeList[i4] = (Locale) otherLocales.mList[i4].clone();
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i5 = 0; i5 < outputLength; i5++) {
            sb.append(LocaleHelper.toLanguageTag(localeList[i5]));
            if (i5 < outputLength - 1) {
                sb.append(',');
            }
        }
        this.mList = localeList;
        this.mStringRepresentation = sb.toString();
    }

    @NonNull
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    static LocaleListHelper getEmptyLocaleList() {
        return sEmptyLocaleList;
    }

    @NonNull
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    static LocaleListHelper forLanguageTags(@Nullable String list) {
        if (list == null || list.isEmpty()) {
            return getEmptyLocaleList();
        }
        String[] tags = list.split(",");
        Locale[] localeArray = new Locale[tags.length];
        for (int i = 0; i < localeArray.length; i++) {
            localeArray[i] = LocaleHelper.forLanguageTag(tags[i]);
        }
        return new LocaleListHelper(localeArray);
    }

    private static String getLikelyScript(Locale locale) {
        if (Build.VERSION.SDK_INT >= 21) {
            String script = locale.getScript();
            if (script.isEmpty()) {
                return "";
            }
            return script;
        }
        return "";
    }

    private static boolean isPseudoLocale(String locale) {
        return STRING_EN_XA.equals(locale) || STRING_AR_XB.equals(locale);
    }

    private static boolean isPseudoLocale(Locale locale) {
        return LOCALE_EN_XA.equals(locale) || LOCALE_AR_XB.equals(locale);
    }

    @IntRange(from = MediaDescriptionCompat.BT_FOLDER_TYPE_MIXED, m1to = 1)
    private static int matchScore(Locale supported, Locale desired) {
        if (supported.equals(desired)) {
            return 1;
        }
        if (!supported.getLanguage().equals(desired.getLanguage()) || isPseudoLocale(supported) || isPseudoLocale(desired)) {
            return 0;
        }
        String supportedScr = getLikelyScript(supported);
        if (supportedScr.isEmpty()) {
            String supportedRegion = supported.getCountry();
            return (supportedRegion.isEmpty() || supportedRegion.equals(desired.getCountry())) ? 1 : 0;
        }
        String desiredScr = getLikelyScript(desired);
        return supportedScr.equals(desiredScr) ? 1 : 0;
    }

    private int findFirstMatchIndex(Locale supportedLocale) {
        for (int idx = 0; idx < this.mList.length; idx++) {
            int score = matchScore(supportedLocale, this.mList[idx]);
            if (score > 0) {
                return idx;
            }
        }
        return Integer.MAX_VALUE;
    }

    private int computeFirstMatchIndex(Collection<String> supportedLocales, boolean assumeEnglishIsSupported) {
        if (this.mList.length == 1) {
            return 0;
        }
        if (this.mList.length == 0) {
            return -1;
        }
        int bestIndex = Integer.MAX_VALUE;
        if (assumeEnglishIsSupported) {
            int idx = findFirstMatchIndex(EN_LATN);
            if (idx == 0) {
                return 0;
            }
            if (idx < Integer.MAX_VALUE) {
                bestIndex = idx;
            }
        }
        for (String languageTag : supportedLocales) {
            Locale supportedLocale = LocaleHelper.forLanguageTag(languageTag);
            int idx2 = findFirstMatchIndex(supportedLocale);
            if (idx2 == 0) {
                return 0;
            }
            if (idx2 < bestIndex) {
                bestIndex = idx2;
            }
        }
        if (bestIndex == Integer.MAX_VALUE) {
            return 0;
        }
        return bestIndex;
    }

    private Locale computeFirstMatch(Collection<String> supportedLocales, boolean assumeEnglishIsSupported) {
        int bestIndex = computeFirstMatchIndex(supportedLocales, assumeEnglishIsSupported);
        if (bestIndex == -1) {
            return null;
        }
        return this.mList[bestIndex];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public Locale getFirstMatch(String[] supportedLocales) {
        return computeFirstMatch(Arrays.asList(supportedLocales), false);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    int getFirstMatchIndex(String[] supportedLocales) {
        return computeFirstMatchIndex(Arrays.asList(supportedLocales), false);
    }

    @Nullable
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    Locale getFirstMatchWithEnglishSupported(String[] supportedLocales) {
        return computeFirstMatch(Arrays.asList(supportedLocales), true);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    int getFirstMatchIndexWithEnglishSupported(Collection<String> supportedLocales) {
        return computeFirstMatchIndex(supportedLocales, true);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    int getFirstMatchIndexWithEnglishSupported(String[] supportedLocales) {
        return getFirstMatchIndexWithEnglishSupported(Arrays.asList(supportedLocales));
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    static boolean isPseudoLocalesOnly(@Nullable String[] supportedLocales) {
        if (supportedLocales == null) {
            return true;
        }
        if (supportedLocales.length > 3) {
            return false;
        }
        for (String locale : supportedLocales) {
            if (!locale.isEmpty() && !isPseudoLocale(locale)) {
                return false;
            }
        }
        return true;
    }

    @Size(min = 1)
    @NonNull
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    static LocaleListHelper getDefault() {
        LocaleListHelper localeListHelper;
        Locale defaultLocale = Locale.getDefault();
        synchronized (sLock) {
            if (!defaultLocale.equals(sLastDefaultLocale)) {
                sLastDefaultLocale = defaultLocale;
                if (sDefaultLocaleList != null && defaultLocale.equals(sDefaultLocaleList.get(0))) {
                    localeListHelper = sDefaultLocaleList;
                } else {
                    sDefaultLocaleList = new LocaleListHelper(defaultLocale, sLastExplicitlySetLocaleList);
                    sDefaultAdjustedLocaleList = sDefaultLocaleList;
                }
            }
            localeListHelper = sDefaultLocaleList;
        }
        return localeListHelper;
    }

    @Size(min = 1)
    @NonNull
    static LocaleListHelper getAdjustedDefault() {
        LocaleListHelper localeListHelper;
        getDefault();
        synchronized (sLock) {
            localeListHelper = sDefaultAdjustedLocaleList;
        }
        return localeListHelper;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    static void setDefault(@Size(min = 1) @NonNull LocaleListHelper locales) {
        setDefault(locales, 0);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    static void setDefault(@Size(min = 1) @NonNull LocaleListHelper locales, int localeIndex) {
        if (locales == null) {
            throw new NullPointerException("locales is null");
        }
        if (locales.isEmpty()) {
            throw new IllegalArgumentException("locales is empty");
        }
        synchronized (sLock) {
            sLastDefaultLocale = locales.get(localeIndex);
            Locale.setDefault(sLastDefaultLocale);
            sLastExplicitlySetLocaleList = locales;
            sDefaultLocaleList = locales;
            if (localeIndex == 0) {
                sDefaultAdjustedLocaleList = sDefaultLocaleList;
            } else {
                sDefaultAdjustedLocaleList = new LocaleListHelper(sLastDefaultLocale, sDefaultLocaleList);
            }
        }
    }
}
