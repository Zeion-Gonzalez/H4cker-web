package org.jcodec.movtool;

import org.jcodec.movtool.QTEdit;

/* loaded from: classes.dex */
public class QTRefEdit {
    protected final QTEdit.EditFactory[] factories;

    public QTRefEdit(QTEdit.EditFactory... factories) {
        this.factories = factories;
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0040, code lost:
    
        if (r3 != r10.factories.length) goto L38;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void execute(java.lang.String[] r11) throws java.lang.Exception {
        /*
            Method dump skipped, instructions count: 297
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jcodec.movtool.QTRefEdit.execute(java.lang.String[]):void");
    }

    protected void help() {
        System.out.println("Quicktime movie editor");
        System.out.println("Syntax: qtedit <command1> <options> ... <commandN> <options> <movie> <output>");
        System.out.println("Where options:");
        QTEdit.EditFactory[] arr$ = this.factories;
        for (QTEdit.EditFactory commandFactory : arr$) {
            System.out.println("\t" + commandFactory.getHelp());
        }
        System.exit(-1);
    }
}
