package com.exp.sign;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupPosition;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class XActivity extends Activity {

    private View x;
    private CircleImageView profileImage;
    private TextView name;
    private TextView bio;
    private BmobUser mBmobUser;
    private CustomDrawerPopupView mCustomDrawerPopupView;
    private TextView xx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x);
        x = (View) findViewById(R.id.x);
        xx = (TextView) findViewById(R.id.cc);
        profileImage = (CircleImageView) findViewById(R.id.profile_image);
        name = (TextView) findViewById(R.id.name);
        bio = (TextView) findViewById(R.id.bio);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(0xff8491a4);
        }
        BmobQuery<BmobUser> query = new BmobQuery<>();
        mCustomDrawerPopupView = new CustomDrawerPopupView(this);
        query.addWhereEqualTo("username", (String) SPUtil.get(this, "username", ""))
                .findObjects(new FindListener<BmobUser>() {
                    @Override
                    public void done(List<BmobUser> list, BmobException e) {
                        if (list != null && list.size() > 0 && e == null) {
                            mBmobUser = list.get(0);
                            name.setText(mBmobUser.getName());
                            bio.setText(mBmobUser.getBio());
                            xx.setText(mBmobUser.getName().charAt(0) + "");
                            mCustomDrawerPopupView.set(mBmobUser);
                            /*if (!TextUtils.isEmpty(mBmobUser.getUrl())) {
                                Glide.with(XActivity.this).load(mBmobUser.getUrl()).into(profileImage);
                                profileImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(XActivity.this, BigActivity.class)
                                                .putExtra("url", mBmobUser.getUrl()));
                                    }
                                });
                            } else if (mBmobUser.getSex().equals("male")) {
                                Glide.with(XActivity.this).load(R.mipmap.av_m).into(profileImage);
                            }*/
                        } else if (e != null) {
                            Toast.makeText(XActivity.this, "network error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void drawer(View v) {
        new XPopup.Builder(this)
                .popupPosition(PopupPosition.Right)//右边
                .hasStatusBarShadow(true) //启用状态栏阴影
                .asCustom(mCustomDrawerPopupView)
                .show();
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
