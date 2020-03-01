package com.exp.sign;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class FillInfoActivity extends AppCompatActivity {

    private CardView cvAdd;
    private EditText etName;
    private RadioButton male;
    private RadioButton female;
    private EditText etNo;
    private EditText etPhone;
    private EditText etEmail;
    private EditText bio;
    private Button btGo;
    private FloatingActionButton fab;
    private LinearLayout llCpwd;
    private EditText etPwd;
    private AVLoadingIndicatorView loadingIndicatorView;

    private String username;
    private BmobUser user;
    private String uurl;
    private CircleImageView mCircleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_info);
        ButterKnife.bind(this);

        mCircleImageView = (CircleImageView) findViewById(R.id.profile_image);
        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FillInfoActivity.this, ImageGridActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        loadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.loading);
        loadingIndicatorView.show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cvAdd = (CardView) findViewById(R.id.cv_add);
        etName = (EditText) findViewById(R.id.et_name);
        bio = (EditText) findViewById(R.id.bio);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        etNo = (EditText) findViewById(R.id.et_no);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etEmail = (EditText) findViewById(R.id.et_email);
        btGo = (Button) findViewById(R.id.bt_go);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        llCpwd = (LinearLayout) findViewById(R.id.ll_cpwd);
        etPwd = (EditText) findViewById(R.id.et_pwd);

        username = getIntent().getStringExtra("username");

        if (getIntent().getBooleanExtra("ccpwd", false)) {
            llCpwd.setVisibility(View.VISIBLE);
        }

        btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next(view);
            }
        });

        BmobQuery<BmobUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username", username)
                .findObjects(new FindListener<BmobUser>() {
                    @Override
                    public void done(List<BmobUser> list, BmobException e) {
                        loadingIndicatorView.hide();
                        if (list != null && list.size() > 0) {
                            user = list.get(0);
                            etPwd.setText(user.getPassword());
                            etEmail.setText(user.getEmail());
                            etName.setText(user.getName());
                            etNo.setText(user.getHeight());
                            etPhone.setText(user.getWeight());
                            bio.setText(user.getBio());
                            if (user.getSex().equalsIgnoreCase("female")) {
                                female.setChecked(true);
                            } else {
                                Glide.with(FillInfoActivity.this).load(R.mipmap.av_m).into(mCircleImageView);
                            }
                            if (!TextUtils.isEmpty(user.getUrl())) {
                                Glide.with(FillInfoActivity.this).load(user.getUrl()).into(mCircleImageView);
                            }
                        } else if (e != null) {
                            Toast.makeText(FillInfoActivity.this, "network error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void next(View view) {

        final String name = etName.getText().toString();
        final String email = etEmail.getText().toString();
        final String sex = male.isChecked() ? "male" : "female";
        final String phone = etPhone.getText().toString();
        final String no = etNo.getText().toString();
        final String pwd = etPwd.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(pwd)
                || TextUtils.isEmpty(sex)
                || TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(no)) {
            Toast.makeText(this, "please enter full information", Toast.LENGTH_SHORT).show();
            return;
        }
        loadingIndicatorView.show();
        user.setPassword(pwd);
        user.setName(name);
        user.setEmail(email);
        user.setHeight(no);
        user.setWeight(phone);
        user.setSex(sex);
        user.setUrl(uurl);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e != null) {
                    Toast.makeText(FillInfoActivity.this, "network error", Toast.LENGTH_SHORT).show();
                } else {
                    loadingIndicatorView.hide();
                    Toast.makeText(FillInfoActivity.this, "modify the success", Toast.LENGTH_SHORT).show();
                    gotoHome();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 1) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                final BmobFile file = new BmobFile(new File(images.get(0).path));
                uurl = file.getUrl();
                file.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e != null) {
                            Toast.makeText(FillInfoActivity.this, "network error", Toast.LENGTH_SHORT).show();
                        } else {
                            uurl = file.getUrl();
                            Glide.with(FillInfoActivity.this).load(uurl).into(mCircleImageView);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "there is no data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoHome() {
        startActivity(new Intent(this, XActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }
}
