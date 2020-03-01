package com.exp.sign;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class BigActivity extends Activity {

    private ImageView x;
    private ImageView x1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bg);
        x = (ImageView) findViewById(R.id.x);
        x1 = (ImageView) findViewById(R.id.x1);
        Glide.with(this).load(getIntent().getStringExtra("url")).into(x);
        if(getIntent().getBooleanExtra("x", true)) {
            Glide.with(this).load(getIntent().getStringExtra("url")).transform(new BlurTransformation(this)).into(x1);
        }
        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            moveTaskToBack(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
