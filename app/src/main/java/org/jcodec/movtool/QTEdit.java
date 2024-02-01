package org.jcodec.movtool;

import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.movtool.Flattern;

/* loaded from: classes.dex */
public class QTEdit {
    protected final EditFactory[] factories;
    private final List<Flattern.ProgressListener> listeners = new ArrayList();

    /* loaded from: classes.dex */
    public interface EditFactory {
        String getHelp();

        String getName();

        MP4Edit parseArgs(List<String> list);
    }

    /* loaded from: classes.dex */
    public static abstract class BaseCommand implements MP4Edit {
        @Override // org.jcodec.movtool.MP4Edit
        public abstract void apply(MovieBox movieBox);

        public void apply(MovieBox movie, FileChannel[][] refs) {
            apply(movie);
        }
    }

    public QTEdit(EditFactory... factories) {
        this.factories = factories;
    }

    public void addProgressListener(Flattern.ProgressListener listener) {
        this.listeners.add(listener);
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0040, code lost:
    
        if (r3 != r8.factories.length) goto L32;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void execute(java.lang.String[] r9) throws java.lang.Exception {
        /*
            r8 = this;
            r7 = 0
            java.util.LinkedList r0 = new java.util.LinkedList
            java.util.List r5 = java.util.Arrays.asList(r9)
            r0.<init>(r5)
            java.util.LinkedList r1 = new java.util.LinkedList
            r1.<init>()
        Lf:
            int r5 = r0.size()
            if (r5 <= 0) goto L42
            r3 = 0
        L16:
            org.jcodec.movtool.QTEdit$EditFactory[] r5 = r8.factories
            int r5 = r5.length
            if (r3 >= r5) goto L3d
            java.lang.Object r5 = r0.get(r7)
            java.lang.String r5 = (java.lang.String) r5
            org.jcodec.movtool.QTEdit$EditFactory[] r6 = r8.factories
            r6 = r6[r3]
            java.lang.String r6 = r6.getName()
            boolean r5 = r5.equals(r6)
            if (r5 == 0) goto Lc4
            r0.remove(r7)
            org.jcodec.movtool.QTEdit$EditFactory[] r5 = r8.factories     // Catch: java.lang.Exception -> La6
            r5 = r5[r3]     // Catch: java.lang.Exception -> La6
            org.jcodec.movtool.MP4Edit r5 = r5.parseArgs(r0)     // Catch: java.lang.Exception -> La6
            r1.add(r5)     // Catch: java.lang.Exception -> La6
        L3d:
            org.jcodec.movtool.QTEdit$EditFactory[] r5 = r8.factories
            int r5 = r5.length
            if (r3 != r5) goto Lf
        L42:
            int r5 = r0.size()
            if (r5 != 0) goto L52
            java.io.PrintStream r5 = java.lang.System.err
            java.lang.String r6 = "ERROR: A movie file should be specified"
            r5.println(r6)
            r8.help()
        L52:
            int r5 = r1.size()
            if (r5 != 0) goto L62
            java.io.PrintStream r5 = java.lang.System.err
            java.lang.String r6 = "ERROR: At least one command should be specified"
            r5.println(r6)
            r8.help()
        L62:
            java.io.File r4 = new java.io.File
            java.lang.Object r5 = r0.remove(r7)
            java.lang.String r5 = (java.lang.String) r5
            r4.<init>(r5)
            boolean r5 = r4.exists()
            if (r5 != 0) goto L98
            java.io.PrintStream r5 = java.lang.System.err
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "ERROR: Input file '"
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r7 = r4.getAbsolutePath()
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r7 = "' doesn't exist"
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r6 = r6.toString()
            r5.println(r6)
            r8.help()
        L98:
            org.jcodec.movtool.ReplaceMP4Editor r5 = new org.jcodec.movtool.ReplaceMP4Editor
            r5.<init>()
            org.jcodec.movtool.CompoundMP4Edit r6 = new org.jcodec.movtool.CompoundMP4Edit
            r6.<init>(r1)
            r5.replace(r4, r6)
        La5:
            return
        La6:
            r2 = move-exception
            java.io.PrintStream r5 = java.lang.System.err
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "ERROR: "
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r7 = r2.getMessage()
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r6 = r6.toString()
            r5.println(r6)
            goto La5
        Lc4:
            int r3 = r3 + 1
            goto L16
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jcodec.movtool.QTEdit.execute(java.lang.String[]):void");
    }

    protected void help() {
        System.out.println("Quicktime movie editor");
        System.out.println("Syntax: qtedit <command1> <options> ... <commandN> <options> <movie>");
        System.out.println("Where options:");
        EditFactory[] arr$ = this.factories;
        for (EditFactory commandFactory : arr$) {
            System.out.println("\t" + commandFactory.getHelp());
        }
        System.exit(-1);
    }
}
