package com.exp.sign;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class PersonActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private TextView tvName;
    private TextView tvSex;
    private TextView tv;
    private TextView tvPhone;
    private TextView tvEmail;
    private TextView bio;
    private Button exit;
    private Button gf;
    private Button cpwd;
    private AVLoadingIndicatorView loadingIndicatorView;
    private CircleImageView mCircleImageView;
    private BmobUser mBmobUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_personal);
        mCircleImageView = (CircleImageView) findViewById(R.id.profile_image);
        loadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.loading);
        tvWelcome = (TextView) findViewById(R.id.tv_welcome);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvSex = (TextView) findViewById(R.id.tv_sex);
        tv = (TextView) findViewById(R.id.tv_);
        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvEmail = (TextView) findViewById(R.id.tv_email);
        bio = (TextView) findViewById(R.id.bio);
        exit = (Button) findViewById(R.id.exit);
        cpwd = (Button) findViewById(R.id.cpwd);
        gf = (Button) findViewById(R.id.gf);
        loadingIndicatorView.show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().getStringExtra("username")
                .equalsIgnoreCase((String) SPUtil.get(this, "username", ""))) {
            exit.setVisibility(View.VISIBLE);
            cpwd.setVisibility(View.VISIBLE);
        } else {
            gf.setText("Chat");
        }

        BmobQuery<BmobUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username", getIntent().getStringExtra("username"))
                .findObjects(new FindListener<BmobUser>() {
                    @Override
                    public void done(List<BmobUser> list, BmobException e) {
                        loadingIndicatorView.hide();
                        if (list != null && list.size() > 0) {
                            mBmobUser = list.get(0);
                            tvWelcome.setText("");
                            tvName.setText(mBmobUser.getName());
                            tvSex.setText(mBmobUser.getSex());
                            tv.setText(mBmobUser.getHeight());
                            tvPhone.setText(mBmobUser.getWeight());
                            tvEmail.setText(mBmobUser.getEmail());
                            bio.setText(mBmobUser.getBio());
                            if (!TextUtils.isEmpty(mBmobUser.getUrl())) {
                                Glide.with(PersonActivity.this).load(mBmobUser.getUrl()).into(mCircleImageView);
                                mCircleImageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(PersonActivity.this, BigActivity.class)
                                                .putExtra("url", mBmobUser.getUrl()));
                                    }
                                });
                            } else if (mBmobUser.getSex().equals("male")) {
                                Glide.with(PersonActivity.this).load(R.mipmap.av_m).into(mCircleImageView);
                            }
                        } else if (e != null) {
                            Toast.makeText(PersonActivity.this, "network error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void changePersonInfo(View view) {
        if (gf.getText().toString().equals("Chat")) {

        } else {
            if (getIntent().getStringExtra("username")
                    .equalsIgnoreCase((String) SPUtil.get(this, "username", ""))) {
                startActivity(new Intent(this, FillInfoActivity.class)
                        .putExtra("username", getIntent().getStringExtra("username")));
            } else {
                startActivity(new Intent(this, FillInfoActivity.class)
                        .putExtra("type", getIntent().getStringExtra("type"))
                        .putExtra("username", getIntent().getStringExtra("username"))
                        .putExtra("ccpwd", true));
            }
        }
    }

    public void exit(View view) {
        SPUtil.put(this, "username", "");
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changePwd(View view) {
        startActivity(new Intent(this, CPWDActivity.class));
    }
}
