package com.hootywhooo86.bitmail;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.browser.customtabs.CustomTabsClient;
import com.google.androidbrowserhelper.trusted.LauncherActivity;

/**
 * Visible start screen. It hands off to Chrome (full-screen bitmail), and if that can't happen on
 * this phone it stays on screen with a message and buttons instead of silently doing nothing.
 */
public class StartActivity extends Activity {

    static final String URL = "https://hootywhooo86.github.io/bitmail/app/";
    private long launchedAt = 0;
    private boolean wentAway = false;
    private TextView msg;
    private Button retry;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.parseColor("#0B0B16"));
        getWindow().setNavigationBarColor(Color.parseColor("#0B0B16"));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.parseColor("#0B0B16"));
        int pad = dp(28);
        root.setPadding(pad, pad, pad, pad);

        TextView title = new TextView(this);
        title.setText("✉ bitmail");
        title.setTextColor(Color.parseColor("#FF5D8F"));
        title.setTextSize(30);
        title.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        msg = new TextView(this);
        msg.setText("Opening…");
        msg.setTextColor(Color.parseColor("#C9C6E8"));
        msg.setTextSize(15);
        msg.setGravity(Gravity.CENTER);
        msg.setLineSpacing(0, 1.3f);
        msg.setPadding(0, dp(18), 0, dp(22));
        root.addView(msg);

        retry = button("OPEN BITMAIL", "#FF5D8F", "#0B0B16");
        retry.setOnClickListener(v -> launch());
        retry.setVisibility(Button.GONE);
        root.addView(retry);

        Button browser = button("OPEN IN BROWSER", "#22223A", "#F4F1FF");
        browser.setOnClickListener(v -> openInBrowser());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(260), dp(52));
        lp.topMargin = dp(12);
        root.addView(browser, lp);

        setContentView(root);
        launch();
    }

    private Button button(String text, String bg, String fg) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextColor(Color.parseColor(fg));
        b.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.parseColor(bg));
        b.setBackground(d);
        b.setLayoutParams(new LinearLayout.LayoutParams(dp(260), dp(52)));
        return b;
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    private void launch() {
        // A browser that supports Custom Tabs (Chrome, Samsung Internet, Edge, Firefox...) is needed for Google sign-in.
        if (CustomTabsClient.getPackageName(this, null) == null) {
            showHelp("bitmail opens in Chrome so Google sign-in works, but no compatible browser is turned on.\n\nInstall or enable Chrome, then tap OPEN BITMAIL.");
            return;
        }
        try {
            Intent i = new Intent(this, LauncherActivity.class);
            i.setData(Uri.parse(URL));
            startActivity(i);
            launchedAt = SystemClock.elapsedRealtime();
            wentAway = false;
            msg.setText("Opening…");
        } catch (Exception e) {
            showHelp("Couldn't open bitmail on this phone (" + e.getClass().getSimpleName() + ").\n\nTap OPEN IN BROWSER to use it in your browser instead.");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (launchedAt != 0) wentAway = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (launchedAt == 0) return;
        if (!wentAway) {
            // Still on this screen a few seconds after launching: Chrome never took over.
            msg.postDelayed(() -> { if (launchedAt != 0 && !wentAway) { showHelp("bitmail didn't open in Chrome.\n\nTap OPEN BITMAIL to try again, or OPEN IN BROWSER."); launchedAt = 0; } }, 6000);
            return;
        }
        // Back from bitmail after using it: close quietly. Back within a moment: it didn't open.
        if (SystemClock.elapsedRealtime() - launchedAt > 4000) {
            finish();
        } else {
            showHelp("bitmail didn't open in Chrome.\n\nTap OPEN BITMAIL to try again, or OPEN IN BROWSER.");
        }
        launchedAt = 0;
    }

    private void showHelp(String text) {
        msg.setText(text);
        retry.setVisibility(Button.VISIBLE);
    }

    private void openInBrowser() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));
        } catch (ActivityNotFoundException e) {
            msg.setText("No web browser is installed. Install Chrome from the Play Store, then try again.");
        }
    }
}
