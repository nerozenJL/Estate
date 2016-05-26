package com.uestc.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class VerificationActivity1 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_verification1);
    }
}
