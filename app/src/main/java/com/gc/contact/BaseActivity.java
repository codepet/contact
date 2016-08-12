package com.gc.contact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected abstract void initView(Bundle savedInstanceState);

    protected abstract void fetchData();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(savedInstanceState);
        fetchData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
    }

}
