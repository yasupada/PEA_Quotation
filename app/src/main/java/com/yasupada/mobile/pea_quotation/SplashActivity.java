package com.yasupada.mobile.pea_quotation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class SplashActivity extends Activity {

    SharedPreferences sharedPreferences;
    private static final String MY_PREFS = "scm_yasupada";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            //ActionBar actionBar = getActionBar();
            //actionBar.hide();
        }

        setContentView(R.layout.activity_splash);
        sharedPreferences = getApplicationContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) { }

                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }

        }).start();

    } // end onCreate

} // end class