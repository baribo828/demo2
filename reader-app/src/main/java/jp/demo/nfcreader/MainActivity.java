package jp.demo.nfcreader;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity implements NfcAdapter.ReaderCallback {
    private NfcAdapter nfcAdapter;
    private LinearLayout resultBox;
    private LinearLayout detailsBox;
    private TextView logText;
    private String history = "履歴はまだありません。";

    private static final int BLUE = Color.rgb(18, 71, 163);
    private static final int BLUE_DARK = Color.rgb(12, 53, 126);
    private static final int GREEN = Color.rgb(28, 169, 88);
    private static final int RED = Color.rgb(210, 60, 55);
    private static final int BG = Color.rgb(244, 247, 251);
    private static final int TEXT_DARK = Color.rgb(28, 36, 50);
    private static final int TEXT_MID = Color.rgb(82, 94, 112);
    private static final int LINE = Color.rgb(228, 232, 238);

    private static final byte[] SELECT_AID = HexUtils.hexToBytes("00A4040007F001020304050600");
    private static final byte[] GET_DATA = HexUtils.hexToBytes("00CA000000");

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BG);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(20), dp(18), dp(24));
        scroll.addView(root, matchWrap());

        LinearLayout appHeader = new LinearLayout(this);
        appHeader.setOrientation(LinearLayout.VERTICAL);
        appHeader.setPadding(dp(18), dp(18), dp(18), dp(18));
        appHeader.setBackground(grad(BLUE, BLUE_DARK, 0));
        root.addView(appHeader, matchWrap());

        TextView appTitle = text("社員証明リーダー", 22, Color.WHITE, true);
        appTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        appHeader.addView(appTitle, matchWrap());

        addSpace(root, 16);

        LinearLayout waitingHero = new LinearLayout(this);
        waitingHero.setOrientation(LinearLayout.VERTICAL);
        waitingHero.setPadding(dp(18), dp(22), dp(18), dp(22));
        waitingHero.setBackground(grad(BLUE, BLUE_DARK, 18));
        root.addView(waitingHero, matchWrap());

        TextView heroTitle = text("タッチして社員確認", 26, Color.WHITE, true);
        heroTitle.setGravity(Gravity.CENTER);
        waitingHero.addView(heroTitle, matchWrap());

        TextView heroSub = text("読み取り待機中：カード役Androidを背面にかざしてください", 14, Color.rgb(218, 233, 255), false);
        heroSub.setGravity(Gravity.CENTER);
        heroSub.setPadding(0, dp(6), 0, 0);
        waitingHero.addView(heroSub, matchWrap());

        addSpace(root, 16);

        resultBox = whiteBox();
        resultBox.setPadding(dp(18), dp(18), dp(18), dp(18));
        root.addView(resultBox, matchWrap());
        renderWaitingResult();

        addSpace(root, 16);

        root.addView(sectionTitle("検証内容"));
        detailsBox = whiteBox();
        detailsBox.setPadding(dp(16), dp(10), dp(16), dp(10));
        root.addView(detailsBox, matchWrap());
        renderWaitingDetails();

        addSpace(root, 16);

        root.addView(sectionTitle("読み取り履歴"));
        LinearLayout logBox = whiteBox();
        logBox.setPadding(dp(16), dp(16), dp(16), dp(16));
        root.addView(logBox, matchWrap());
        logText = text(history, 14, TEXT_DARK, false);
        logBox.addView(logText, matchWrap());

        setContentView(scroll);
    }

    @Override protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            Bundle options = new Bundle();
            nfcAdapter.enableReaderMode(this, this,
                    NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                    options);
        }
    }

    @Override protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) nfcAdapter.disableReaderMode(this);
    }

    @Override public void onTagDiscovered(Tag tag) {
        IsoDep isoDep = IsoDep.get(tag);
        if (isoDep == null) {
            showError("IsoDep非対応のタグです。", "このデモではAndroid HCEの疑似社員証を読み取ります。");
            return;
        }

        try {
            isoDep.connect();
            isoDep.setTimeout(5000);
            byte[] selectResponse = isoDep.transceive(SELECT_AID);
            byte[] dataResponse = isoDep.transceive(GET_DATA);
            String json = stripStatusAndDecode(dataResponse);
            JSONObject obj = new JSONObject(json);
            showSuccess(obj, HexUtils.bytesToHex(selectResponse));
        } catch (Exception e) {
            showError("読み取り失敗", e.getMessage() == null ? "原因不明のエラーです。" : e.getMessage());
        } finally {
            try { isoDep.close(); } catch (Exception ignored) { }
        }
    }

    private void renderWaitingResult() {
        resultBox.removeAllViews();
        TextView label = text("認証結果", 16, TEXT_MID, true);
        label.setGravity(Gravity.CENTER);
        resultBox.addView(label, matchWrap());

        TextView big = text("WAIT", 44, Color.rgb(120, 130, 145), true);
        big.setGravity(Gravity.CENTER);
        big.setPadding(0, dp(6), 0, 0);
        resultBox.addView(big, matchWrap());

        TextView msg = text("カードをかざすと社員属性を検証します", 15, TEXT_MID, false);
        msg.setGravity(Gravity.CENTER);
        msg.setPadding(0, dp(4), 0, 0);
        resultBox.addView(msg, matchWrap());
    }

    private void renderWaitingDetails() {
        detailsBox.removeAllViews();
        detailsBox.addView(infoRow("状態", "読み取り待機中", false));
        detailsBox.addView(separator());
        detailsBox.addView(infoRow("検証予定", "発行者・属性・有効期限・署名状態", false));
        detailsBox.addView(separator());
        detailsBox.addView(infoRow("失効確認", "デモでは省略", false));
    }

    private void showSuccess(JSONObject obj, String selectHex) {
        runOnUiThread(() -> {
            try {
                JSONObject attrs = obj.optJSONObject("attributes");
                boolean employee = attrs != null && attrs.optBoolean("employee", false);
                String type = obj.optString("credential_type", "-");
                String subjectId = obj.optString("subject_id", "-");
                String issuer = obj.optString("issuer", "-");
                String certId = obj.optString("certificate_id", "-");
                String expires = obj.optString("expires_at", "-");
                String sig = obj.optString("signature_status", "-");
                boolean signatureOk = "demo_valid".equals(sig);
                boolean ok = "employee_attribute_proof".equals(type) && employee && signatureOk;

                resultBox.removeAllViews();
                TextView label = text("認証結果", 16, TEXT_MID, true);
                label.setGravity(Gravity.CENTER);
                resultBox.addView(label, matchWrap());

                TextView big = text(ok ? "OK" : "NG", 52, ok ? GREEN : RED, true);
                big.setGravity(Gravity.CENTER);
                big.setPadding(0, dp(6), 0, 0);
                resultBox.addView(big, matchWrap());

                TextView line1 = text(ok ? "社員です" : "社員確認NG", 24, TEXT_DARK, true);
                line1.setGravity(Gravity.CENTER);
                line1.setPadding(0, dp(4), 0, 0);
                resultBox.addView(line1, matchWrap());

                TextView line2 = text(ok ? "アクセス権限：利用可能" : "属性または署名状態を確認できません", 15, ok ? GREEN : RED, true);
                line2.setGravity(Gravity.CENTER);
                line2.setPadding(0, dp(6), 0, 0);
                resultBox.addView(line2, matchWrap());

                detailsBox.removeAllViews();
                detailsBox.addView(infoRow("提示属性", employee ? "社員であること" : "社員ではない", false));
                detailsBox.addView(separator());
                detailsBox.addView(infoRow("発行者", issuer, false));
                detailsBox.addView(separator());
                detailsBox.addView(infoRow("証明ID", certId, false));
                detailsBox.addView(separator());
                detailsBox.addView(infoRow("匿名化ID", subjectId, false));
                detailsBox.addView(separator());
                detailsBox.addView(infoRow("有効期限", expires, false));
                detailsBox.addView(separator());
                detailsBox.addView(infoRow("署名検証", signatureOk ? "OK（デモ）" : "NG", signatureOk));
                detailsBox.addView(separator());
                detailsBox.addView(infoRow("失効確認", "デモでは省略", false));
                detailsBox.addView(separator());
                detailsBox.addView(infoRow("証明種別", type, false));
                detailsBox.addView(separator());
                detailsBox.addView(infoRow("SELECT応答", selectHex, false));

                String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPAN).format(new Date());
                String line = time + "  " + (ok ? "OK" : "NG") + "  " + certId;
                if (history.equals("履歴はまだありません。")) history = line; else history = line + "\n" + history;
                logText.setText(history);
            } catch (Exception e) {
                showError("表示処理でエラー", e.getMessage());
            }
        });
    }

    private void showError(String title, String detail) {
        runOnUiThread(() -> {
            resultBox.removeAllViews();
            TextView label = text("認証結果", 16, TEXT_MID, true);
            label.setGravity(Gravity.CENTER);
            resultBox.addView(label, matchWrap());

            TextView big = text("NG", 52, RED, true);
            big.setGravity(Gravity.CENTER);
            big.setPadding(0, dp(6), 0, 0);
            resultBox.addView(big, matchWrap());

            TextView line1 = text(title, 22, TEXT_DARK, true);
            line1.setGravity(Gravity.CENTER);
            line1.setPadding(0, dp(4), 0, 0);
            resultBox.addView(line1, matchWrap());

            TextView line2 = text("読み取りまたは検証に失敗しました", 15, RED, true);
            line2.setGravity(Gravity.CENTER);
            line2.setPadding(0, dp(6), 0, 0);
            resultBox.addView(line2, matchWrap());

            detailsBox.removeAllViews();
            detailsBox.addView(infoRow("エラー詳細", detail == null ? "詳細なし" : detail, false));
            detailsBox.addView(separator());
            detailsBox.addView(infoRow("失効確認", "デモでは省略", false));
        });
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

    private LinearLayout infoRow(String left, String right, boolean rightGreen) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(10), 0, dp(10));

        TextView l = text(left, 14, TEXT_MID, false);
        row.addView(l, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.42f));

        TextView r = text(right, 14, rightGreen ? GREEN : TEXT_DARK, true);
        r.setGravity(Gravity.RIGHT);
        row.addView(r, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.58f));
        return row;
    }

    private TextView separator() {
        TextView sep = new TextView(this);
        sep.setBackgroundColor(LINE);
        sep.setHeight(dp(1));
        return sep;
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
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void addSpace(LinearLayout root, int h) {
        TextView v = new TextView(this);
        root.addView(v, new LinearLayout.LayoutParams(1, dp(h)));
    }

    private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }

    private static String stripStatusAndDecode(byte[] response) {
        int len = response.length;
        if (len >= 2 && response[len - 2] == (byte)0x90 && response[len - 1] == 0x00) {
            return new String(response, 0, len - 2, StandardCharsets.UTF_8);
        }
        return new String(response, StandardCharsets.UTF_8);
    }
}
