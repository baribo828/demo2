package jp.demo.nfccard;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int BLUE = Color.rgb(18, 71, 163);
    private static final int BLUE_DARK = Color.rgb(12, 53, 126);
    private static final int TEXT_DARK = Color.rgb(28, 36, 50);
    private static final int TEXT_MID = Color.rgb(82, 94, 112);
    private static final int BG = Color.rgb(244, 247, 251);
    private static final int GREEN = Color.rgb(28, 169, 88);
    private static final int LINE = Color.rgb(228, 232, 238);

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BG);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(20), dp(18), dp(24));
        scroll.addView(root, matchWrap());

        // Top title bar style area
        LinearLayout appHeader = new LinearLayout(this);
        appHeader.setOrientation(LinearLayout.VERTICAL);
        appHeader.setPadding(dp(18), dp(18), dp(18), dp(18));
        appHeader.setBackground(grad(BLUE, BLUE_DARK, 0));
        root.addView(appHeader, matchWrap());

        TextView appTitle = text("社員属性証明", 22, Color.WHITE, true);
        appTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        appHeader.addView(appTitle, matchWrap());

        addSpace(root, 16);

        // Employee card container
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(16), dp(16), dp(16), dp(18));
        panel.setBackground(cardBackground());
        root.addView(panel, matchWrap());

        // Company bar
        LinearLayout company = new LinearLayout(this);
        company.setOrientation(LinearLayout.HORIZONTAL);
        company.setGravity(Gravity.CENTER_VERTICAL);
        panel.addView(company, matchWrap());

        TextView logo = new TextView(this);
        logo.setText("◎");
        logo.setTextSize(26);
        logo.setTextColor(BLUE);
        company.addView(logo);

        TextView companyName = text("日本通信株式会社", 20, BLUE_DARK, true);
        companyName.setPadding(dp(8), 0, 0, 0);
        company.addView(companyName);

        addSpace(panel, 18);

        LinearLayout profileRow = new LinearLayout(this);
        profileRow.setOrientation(LinearLayout.HORIZONTAL);
        panel.addView(profileRow, matchWrap());

        // avatar
        TextView avatar = new TextView(this);
        avatar.setText("社員\n写真");
        avatar.setGravity(Gravity.CENTER);
        avatar.setTextSize(18);
        avatar.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        avatar.setTextColor(Color.rgb(110, 120, 140));
        GradientDrawable avatarBg = new GradientDrawable();
        avatarBg.setColor(Color.rgb(237, 239, 243));
        avatarBg.setShape(GradientDrawable.OVAL);
        avatar.setBackground(avatarBg);
        profileRow.addView(avatar, new LinearLayout.LayoutParams(dp(124), dp(124)));

        TextView divider = new TextView(this);
        divider.setBackgroundColor(Color.rgb(236, 238, 242));
        LinearLayout.LayoutParams dividerLp = new LinearLayout.LayoutParams(dp(1), dp(124));
        dividerLp.setMargins(dp(14), 0, dp(14), 0);
        profileRow.addView(divider, dividerLp);

        LinearLayout profileInfo = new LinearLayout(this);
        profileInfo.setOrientation(LinearLayout.VERTICAL);
        profileRow.addView(profileInfo, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        profileInfo.addView(labelValue("氏 名", "高橋 太郎", 16, 30));
        addSpace(profileInfo, 10);
        profileInfo.addView(labelValue("所属部署", "システム企画部", 16, 20));

        addSpace(panel, 16);
        panel.addView(separator());
        addSpace(panel, 16);

        panel.addView(infoRow("証明属性", "社員であること", false));
        addSpace(panel, 12);
        panel.addView(statusRow());
        addSpace(panel, 12);
        panel.addView(infoRow("証明ID", "cert-demo-employee-001", false));
        addSpace(panel, 12);
        panel.addView(infoRow("有効期限", "2026/12/31", false));

        addSpace(root, 14);
        TextView note = text("※ タッチ時には氏名・社員番号ではなく、社員属性のみを証明します", 13, TEXT_MID, false);
        note.setGravity(Gravity.CENTER_HORIZONTAL);
        root.addView(note, matchWrap());

        addSpace(root, 14);
        TextView action = text(")))  NFCでタッチして証明", 20, Color.WHITE, true);
        action.setGravity(Gravity.CENTER);
        action.setPadding(dp(18), dp(18), dp(18), dp(18));
        action.setBackground(roundRect(BLUE, 18));
        root.addView(action, matchWrap());

        addSpace(root, 12);
        TextView subAction = text("証明内容を確認", 16, BLUE_DARK, true);
        subAction.setGravity(Gravity.CENTER);
        subAction.setPadding(dp(12), dp(14), dp(12), dp(8));
        root.addView(subAction, matchWrap());

        addSpace(root, 18);
        root.addView(sectionTitle("このデモでNFC送信する情報"));
        LinearLayout meta = whiteBox();
        meta.setPadding(dp(14), dp(10), dp(14), dp(10));
        root.addView(meta, matchWrap());
        meta.addView(simpleRow("証明属性", "employee = true"));
        meta.addView(simpleRow("氏名", "送信しない"));
        meta.addView(simpleRow("社員番号", "送信しない"));
        meta.addView(simpleRow("署名検証", "デモ用OK"));
        meta.addView(simpleRow("失効確認", "デモでは省略"));

        addSpace(root, 18);
        root.addView(sectionTitle("送信データ（デモ用）"));
        TextView payload = text("{\n" +
                "  \"credential_type\": \"employee_attribute_proof\",\n" +
                "  \"subject_id\": \"anon-demo-001\",\n" +
                "  \"attributes\": { \"employee\": true },\n" +
                "  \"issuer\": \"Japan Communications Inc.\",\n" +
                "  \"certificate_id\": \"cert-demo-employee-001\",\n" +
                "  \"expires_at\": \"2026-12-31T23:59:59+09:00\",\n" +
                "  \"signature_status\": \"demo_valid\"\n" +
                "}", 13, TEXT_DARK, false);
        payload.setTypeface(Typeface.MONOSPACE);
        payload.setPadding(dp(16), dp(16), dp(16), dp(16));
        payload.setBackground(roundRect(Color.WHITE, 16));
        root.addView(payload, matchWrap());

        setContentView(scroll);
    }

    private LinearLayout labelValue(String label, String value, int labelSp, int valueSp) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        TextView l = text(label, labelSp, BLUE_DARK, true);
        TextView v = text(value, valueSp, TEXT_DARK, true);
        v.setPadding(0, dp(4), 0, 0);
        box.addView(l);
        box.addView(v);
        return box;
    }

    private LinearLayout infoRow(String label, String value, boolean green) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView l = text(label, 16, BLUE_DARK, true);
        row.addView(l, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.42f));

        TextView v = text(value, 17, green ? GREEN : TEXT_DARK, true);
        row.addView(v, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.58f));
        return row;
    }

    private LinearLayout statusRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView l = text("ステータス", 16, BLUE_DARK, true);
        row.addView(l, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.42f));

        LinearLayout right = new LinearLayout(this);
        right.setOrientation(LinearLayout.HORIZONTAL);
        right.setGravity(Gravity.CENTER_VERTICAL);
        TextView active = text("有効", 16, Color.WHITE, true);
        active.setGravity(Gravity.CENTER);
        active.setPadding(dp(12), dp(4), dp(12), dp(4));
        active.setBackground(roundRect(GREEN, 8));
        right.addView(active);
        row.addView(right, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.58f));
        return row;
    }

    private LinearLayout simpleRow(String left, String right) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dp(8), 0, dp(8));
        TextView l = text(left, 14, TEXT_MID, false);
        TextView r = text(right, 14, TEXT_DARK, true);
        row.addView(l, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        r.setGravity(Gravity.RIGHT);
        row.addView(r, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        return row;
    }

    private TextView sectionTitle(String s) {
        TextView v = text(s, 16, TEXT_DARK, true);
        v.setPadding(0, 0, 0, dp(8));
        return v;
    }

    private LinearLayout whiteBox() {
        LinearLayout v = new LinearLayout(this);
        v.setOrientation(LinearLayout.VERTICAL);
        v.setBackground(roundRect(Color.WHITE, 16));
        return v;
    }

    private TextView separator() {
        TextView sep = new TextView(this);
        sep.setBackgroundColor(LINE);
        sep.setHeight(dp(1));
        return sep;
    }

    private GradientDrawable cardBackground() {
        GradientDrawable g = new GradientDrawable();
        g.setColor(Color.WHITE);
        g.setCornerRadius(dp(18));
        g.setStroke(dp(1), Color.rgb(232, 236, 243));
        return g;
    }

    private GradientDrawable roundRect(int color, int radius) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(radius));
        return g;
    }

    private GradientDrawable grad(int top, int bottom, int radius) {
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{top, bottom});
        g.setCornerRadius(dp(radius));
        return g;
    }

    private TextView text(String s, int sp, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(s);
        v.setTextSize(sp);
        v.setTextColor(color);
        if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        v.setLineSpacing(0, 1.1f);
        return v;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.Layout_CONTENT);
    }

    private void addSpace(LinearLayout root, int h) {
        TextView v = new TextView(this);
        root.addView(v, new LinearLayout.LayoutParams(1, dp(h)));
    }

    private int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density + 0.5f); }
}
