package com.exp.sign;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import static com.exp.sign.MainActivity.instance;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.cv_add)
    CardView cvAdd;

    @BindView(R.id.et_username)
    EditText editText;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_repeatpassword)
    EditText etReaptPassword;

    @BindView(R.id.male)
    RadioButton male;

    @BindView(R.id.putong)
    RadioButton rbpt;
    @BindView(R.id.guanli)
    RadioButton rbgl;

    @BindView(R.id.et_name)
    EditText etname;
    @BindView(R.id.et_phone)
    EditText etphone;
    @BindView(R.id.et_email)
    EditText etemail;
    @BindView(R.id.et_weight)
    EditText etwight;
    @BindView(R.id.bio)
    EditText bio;
    @BindView(R.id.loading)
    AVLoadingIndicatorView loadingIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        loadingIndicatorView.hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

    public void next(View view) {
        final String username = editText.getText().toString();
        final String password = etPassword.getText().toString();
        String repeatPassword = etReaptPassword.getText().toString();
        final String name = etname.getText().toString();
        String phone = etphone.getText().toString();
        String email = etemail.getText().toString();
        String weight = etwight.getText().toString();
        String bios = bio.getText().toString();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(repeatPassword)
                || TextUtils.isEmpty(bios) || TextUtils.isEmpty(weight) || TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "please enter full information", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!repeatPassword.equals(password)) {
            Toast.makeText(this, "the passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        String sex = "male";
        if (!male.isChecked())
            sex = "female";
        loadingIndicatorView.show();
        final BmobUser user = new BmobUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setSex(sex);
        user.setName(name);
        user.setEmail(email);
        user.setHeight(phone);
        user.setWeight(weight);
        user.setBio(bios);
        BmobQuery<BmobUser> userBmobQuery = new BmobQuery<>();
        userBmobQuery
                .addWhereEqualTo("username", username)
                .findObjects(new FindListener<BmobUser>() {
                    @Override
                    public void done(List<BmobUser> list, BmobException e) {
                        if (e == null) {
                            if (list != null && list.size() > 0) {
                                loadingIndicatorView.hide();
                                Toast.makeText(RegisterActivity.this, "the user already exists, please change the user name", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            user.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    loadingIndicatorView.hide();
                                    if (e == null) {
                                        MainActivity.sBmobUser = user;
                                        SPUtil.put(RegisterActivity.this, "name", name);
                                        SPUtil.put(RegisterActivity.this, "username", username);
                                        SPUtil.put(RegisterActivity.this, "password", password);
                                        Toast.makeText(RegisterActivity.this, "registered successfully", Toast.LENGTH_SHORT).show();
                                        gotoHome();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "network error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            loadingIndicatorView.hide();
                            Toast.makeText(RegisterActivity.this, "network error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void gotoHome() {
        if (instance != null) {
            instance.finish();
            instance = null;
        }
        startActivity(new Intent(this, XActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }
}
