package com.exp.sign;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.exp.sign.R.id.loading;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.bt_go)
    Button btGo;
    @BindView(R.id.cv)
    CardView cv;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.putong)
    RadioButton rbpt;
    @BindView(R.id.guanli)
    RadioButton rbgl;
    @BindView(loading)
    AVLoadingIndicatorView loadingIndicatorView;

    public static MainActivity instance;
    public static BmobUser sBmobUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Bmob.initialize(this, "5bdd33410c98658cbaee2491400e594e");
        ButterKnife.bind(this);
        instance = this;
        if (!TextUtils.isEmpty((String) SPUtil.get(this, "username", ""))) {
            BmobQuery<BmobUser> q = new BmobQuery<>();
            q.addWhereEqualTo("username", (String) SPUtil.get(this, "username", ""));
            q.findObjects(new FindListener<BmobUser>() {
                @Override
                public void done(List<BmobUser> list, BmobException e) {
                    sBmobUser = list.get(0);
                }
            });
            startActivity(new Intent(this, XActivity.class));
            finish();
        }
    }

    private void gotoHome() {
        instance = null;
        startActivity(new Intent(this, XActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick({R.id.bt_go, R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(this, fab, fab.getTransitionName());
                    startActivity(new Intent(this, RegisterActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(this, RegisterActivity.class));
                }
                break;
            case R.id.bt_go:
                final String un = etUsername.getText().toString();
                final String pd = etPassword.getText().toString();
                if (TextUtils.isEmpty(un) || TextUtils.isEmpty(pd)) {
                    Toast.makeText(MainActivity.this, "please enter a user name or password", Toast.LENGTH_SHORT).show();
                    return;
                }
                loadingIndicatorView.show();
                BmobQuery<BmobUser> userBmobQuery = new BmobQuery<>();
                userBmobQuery.addWhereEqualTo("username", un)
                        .addWhereEqualTo("password", pd)
                        .findObjects(new FindListener<BmobUser>() {
                            @Override
                            public void done(List<BmobUser> list, BmobException e) {
                                if (e == null) {
                                    loadingIndicatorView.hide();
                                    if (list == null || list.size() == 0) {
                                        Toast.makeText(MainActivity.this, "incorrect user name or password", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        sBmobUser = list.get(0);
                                        SPUtil.put(MainActivity.this, "name", list.get(0).getName());
                                        SPUtil.put(MainActivity.this, "username", un);
                                        SPUtil.put(MainActivity.this, "password", pd);
                                        Toast.makeText(MainActivity.this, "login successful", Toast.LENGTH_SHORT).show();
                                        gotoHome();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "network error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
        }
    }
}
