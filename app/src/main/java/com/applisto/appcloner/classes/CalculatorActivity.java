package com.applisto.appcloner.classes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.applisto.appcloner.classes.util.Log;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes2.dex */
public class CalculatorActivity extends Activity implements View.OnClickListener {
    private static final String TAG = CalculatorActivity.class.getSimpleName();
    public static boolean sUnlocked;
    private TextView mDisplay;
    private boolean mDot;
    private String mFakeCalculatorCode;
    private String mOriginalActivityName;
    private Set<Button> mButtons = new HashSet();
    private String mSymbol = "";
    private String mNumber1 = "0";
    private String mNumber2 = "0";

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        Window window;
        super.onCreate(bundle);
        try {
            Bundle bundle2 = getPackageManager().getActivityInfo(getComponentName(), 129).metaData;
            if (bundle2 != null) {
                this.mOriginalActivityName = bundle2.getString("com.applisto.appcloner.originalActivityName");
                if (this.mOriginalActivityName != null && this.mOriginalActivityName.startsWith(".")) {
                    this.mOriginalActivityName = getPackageName() + this.mOriginalActivityName;
                }
                this.mFakeCalculatorCode = CloneSettings.getInstance(this).getString("fakeCalculatorCode", null);
            }
        } catch (Exception e) {
            Log.m21w(TAG, e);
            exit();
        }
        try {
            setTheme(16973838);
            if (Build.VERSION.SDK_INT > 21 && (window = getWindow()) != null) {
                window.addFlags(Integer.MIN_VALUE);
                window.setNavigationBarColor(-12303292);
            }
        } catch (Exception e2) {
            Log.m21w(TAG, e2);
        }
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setBackgroundColor(-12303292);
        linearLayout.setOrientation(1);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        setContentView(linearLayout);
        this.mDisplay = new TextView(this);
        linearLayout.addView(this.mDisplay, new LinearLayout.LayoutParams(-1, -2));
        this.mDisplay.setGravity(GravityCompat.END);
        int dp2px = Utils.dp2px(this, 16.0f);
        this.mDisplay.setPadding(dp2px, dp2px, dp2px, 0);
        this.mDisplay.setTextColor(-1);
        this.mDisplay.setTextSize(40.0f);
        this.mDisplay.setText("0");
        this.mDisplay.setSingleLine(false);
        this.mDisplay.setMaxLines(Utils.isLandscape(this) ? 1 : 2);
        this.mDisplay.setLines(Utils.isLandscape(this) ? 1 : 2);
        this.mDisplay.setEllipsize(TextUtils.TruncateAt.END);
        TableLayout tableLayout = new TableLayout(this);
        linearLayout.addView(tableLayout, new LinearLayout.LayoutParams(-1, -1));
        TableRow addTableRow = addTableRow(tableLayout);
        addButton("7", addTableRow, false);
        addButton("8", addTableRow, false);
        addButton("9", addTableRow, false);
        addButton("÷", addTableRow, true);
        TableRow addTableRow2 = addTableRow(tableLayout);
        addButton("4", addTableRow2, false);
        addButton("5", addTableRow2, false);
        addButton("6", addTableRow2, false);
        addButton("×", addTableRow2, true);
        TableRow addTableRow3 = addTableRow(tableLayout);
        addButton("1", addTableRow3, false);
        addButton("2", addTableRow3, false);
        addButton("3", addTableRow3, false);
        addButton("-", addTableRow3, true);
        TableRow addTableRow4 = addTableRow(tableLayout);
        Button addButton = addButton("0", addTableRow4, false);
        addButton(".", addTableRow4, false);
        addButton("=", addTableRow4, false);
        addButton("+", addTableRow4, true);
        addButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.applisto.appcloner.classes.CalculatorActivity.1
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View view) {
                CalculatorActivity.this.mSymbol = "";
                CalculatorActivity.this.mNumber1 = "0";
                CalculatorActivity.this.mNumber2 = "0";
                CalculatorActivity.this.mDisplay.setText("0");
                return true;
            }
        });
        linearLayout.post(new Runnable() { // from class: com.applisto.appcloner.classes.CalculatorActivity.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    DisplayMetrics displayMetrics = CalculatorActivity.this.getResources().getDisplayMetrics();
                    for (Button button : CalculatorActivity.this.mButtons) {
                        float height = (button.getHeight() / displayMetrics.scaledDensity) * 0.33f;
                        button.setTextSize(height);
                        CalculatorActivity.this.mDisplay.setTextSize(height);
                    }
                } catch (Exception e3) {
                    Log.m21w(CalculatorActivity.TAG, e3);
                }
            }
        });
        setTitle("Calculator");
    }

    private TableRow addTableRow(TableLayout tableLayout) {
        TableRow tableRow = new TableRow(this);
        tableLayout.addView(tableRow, new TableLayout.LayoutParams(-1, 0, 1.0f));
        int dp2px = Utils.dp2px(this, 8.0f);
        tableLayout.setPadding(dp2px, dp2px, dp2px, dp2px);
        return tableRow;
    }

    private Button addButton(String str, TableRow tableRow, boolean z) {
        Button button = new Button(this);
        button.setText(str);
        button.setTextColor(-1);
        button.setTextSize(30.0f);
        button.setOnClickListener(this);
        tableRow.addView(button, new TableRow.LayoutParams(0, -1, 1.0f));
        if (z) {
            button.getBackground().setColorFilter(-26624, PorterDuff.Mode.MULTIPLY);
        } else {
            button.getBackground().setColorFilter(-7829368, PorterDuff.Mode.MULTIPLY);
        }
        this.mButtons.add(button);
        return button;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        String charSequence = ((Button) view).getText().toString();
        String charSequence2 = this.mDisplay.getText().toString();
        try {
            int parseInt = Integer.parseInt(charSequence);
            if ("0".equals(this.mNumber1) && "0".equals(this.mNumber2) && "".equals(this.mSymbol)) {
                charSequence2 = "";
            }
            if ("".equals(this.mSymbol)) {
                StringBuilder sb = new StringBuilder();
                sb.append(this.mNumber1);
                sb.append(this.mDot ? "." : "");
                sb.append(charSequence);
                this.mNumber1 = sb.toString();
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(this.mNumber2);
                sb2.append(this.mDot ? "." : "");
                sb2.append(charSequence);
                this.mNumber2 = sb2.toString();
            }
            this.mDisplay.setText(charSequence2 + parseInt);
            this.mDot = false;
        } catch (Exception unused) {
            if ("0".equals(this.mNumber1) && "0".equals(this.mNumber2) && "".equals(this.mSymbol)) {
                if ("0".equals(charSequence2)) {
                    charSequence2 = "";
                } else if (!"=".equals(charSequence)) {
                    this.mNumber1 = charSequence2;
                }
            }
            if ("=".equals(charSequence)) {
                if (this.mSymbol.equals("+")) {
                    setDisplay(Double.parseDouble(this.mNumber1) + Double.parseDouble(this.mNumber2));
                } else if (this.mSymbol.equals("-")) {
                    setDisplay(Double.parseDouble(this.mNumber1) - Double.parseDouble(this.mNumber2));
                } else if (this.mSymbol.equals("×")) {
                    setDisplay(Double.parseDouble(this.mNumber1) * Double.parseDouble(this.mNumber2));
                } else if (this.mSymbol.equals("÷")) {
                    setDisplay(Double.parseDouble(this.mNumber1) / Double.parseDouble(this.mNumber2));
                }
            } else if (".".equals(charSequence)) {
                String str = "".equals(this.mSymbol) ? this.mNumber1 : this.mNumber2;
                if (!this.mDot && !str.contains(".")) {
                    this.mDot = true;
                    this.mDisplay.setText(charSequence2 + ".");
                }
            } else {
                if (!"".equals(this.mSymbol)) {
                    if (this.mSymbol.equals("+")) {
                        this.mNumber1 = Double.toString(Double.parseDouble(this.mNumber1) + Double.parseDouble(this.mNumber2));
                    } else if (this.mSymbol.equals("-")) {
                        this.mNumber1 = Double.toString(Double.parseDouble(this.mNumber1) - Double.parseDouble(this.mNumber2));
                    } else if (this.mSymbol.equals("×")) {
                        this.mNumber1 = Double.toString(Double.parseDouble(this.mNumber1) * Double.parseDouble(this.mNumber2));
                    } else if (this.mSymbol.equals("÷")) {
                        this.mNumber1 = Double.toString(Double.parseDouble(this.mNumber1) / Double.parseDouble(this.mNumber2));
                    }
                    this.mNumber2 = "0";
                }
                this.mSymbol = charSequence;
                this.mDisplay.setText(charSequence2 + " " + this.mSymbol + " ");
            }
        }
        String str2 = this.mFakeCalculatorCode;
        if (str2 == null || !str2.equals(this.mDisplay.getText().toString())) {
            return;
        }
        startApp();
    }

    private void setDisplay(double d) {
        String d2 = Double.toString(d);
        if (d2.endsWith(".0")) {
            d2 = d2.substring(0, d2.length() - 2);
        }
        this.mDisplay.setText(d2);
        this.mNumber1 = "0";
        this.mNumber2 = "0";
        this.mSymbol = "";
    }

    private void startApp() {
        sUnlocked = true;
        try {
            Intent intent = new Intent(this, Class.forName(this.mOriginalActivityName));
            intent.addFlags(268435456);
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                intent.putExtras(extras);
            }
            startActivity(intent);
        } catch (Exception e) {
            Log.m21w(TAG, e);
        }
        finish();
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [com.applisto.appcloner.classes.CalculatorActivity$3] */
    private void exit() {
        if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        } else {
            finish();
        }
        new Thread() { // from class: com.applisto.appcloner.classes.CalculatorActivity.3
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    Thread.sleep(250L);
                } catch (InterruptedException unused) {
                }
                System.exit(0);
            }
        }.start();
    }
}
