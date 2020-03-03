package com.exp.sign;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class CPWDActivity extends AppCompatActivity {

    private CardView cvAdd;
    private EditText etName;
    private EditText etNo;
    private EditText etPhone;
    private Button btGo;
    private FloatingActionButton fab;
    private AVLoadingIndicatorView loadingIndicatorView;

    private BmobUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_pwd);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.loading);
        cvAdd = (CardView) findViewById(R.id.cv_add);
        etName = (EditText) findViewById(R.id.et_name);
        etNo = (EditText) findViewById(R.id.et_no);
        etPhone = (EditText) findViewById(R.id.et_phone);
        btGo = (Button) findViewById(R.id.bt_go);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        loadingIndicatorView.hide();


        btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next(view);
            }
        });

        BmobQuery<BmobUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username",SPUtil.get(this,"username",""))
                .findObjects(new FindListener<BmobUser>() {
                    @Override
                    public void done(List<BmobUser> list, BmobException e) {
                        if(e != null) {
                            Toast.makeText(CPWDActivity.this, "network error", Toast.LENGTH_SHORT).show();
                        } else {
                            if(list!=null && list.size() > 0){
                                user = list.get(0);
                            }
                        }
                    }
                });

    }

    public void next(View view) {

        final String name = etName.getText().toString();
        final String no = etNo.getText().toString();
        final String phone = etPhone.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(no)
                || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "please enter full information", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!no.equalsIgnoreCase(phone)){
            Toast.makeText(this, "the two passwords are not the same", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!name.equalsIgnoreCase(user.getPassword())){
            Toast.makeText(this, "the original password was entered incorrectly", Toast.LENGTH_SHORT).show();
            return;
        }
        loadingIndicatorView.show();
        user.setPassword(no);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                loadingIndicatorView.hide();
                if(e == null) {
                    Toast.makeText(CPWDActivity.this, "modify the success", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CPWDActivity.this, "network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
