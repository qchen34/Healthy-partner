package com.exp.sign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.lxj.xpopup.core.DrawerPopupView;

public class CustomDrawerPopupView extends DrawerPopupView implements View.OnClickListener {
    private TextView name;
    private TextView bio;
    private TextView account;
    private TextView calendar;
    private TextView partner;
    private TextView exercises;
    private TextView chats;
    private TextView settings;
    private TextView rate;
    private TextView out;
    private BmobUser mBmobUser;

    public CustomDrawerPopupView(Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.custom_drawer_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        name = (TextView) findViewById(R.id.name);
        bio = (TextView) findViewById(R.id.bio);
        account = (TextView) findViewById(R.id.account);
        calendar = (TextView) findViewById(R.id.calendar);
        partner = (TextView) findViewById(R.id.partner);
        exercises = (TextView) findViewById(R.id.exercises);
        chats = (TextView) findViewById(R.id.chats);
        settings = (TextView) findViewById(R.id.settings);
        rate = (TextView) findViewById(R.id.rate);
        out = (TextView) findViewById(R.id.Logout);
        account.setOnClickListener(this);
        calendar.setOnClickListener(this);
        partner.setOnClickListener(this);
        exercises.setOnClickListener(this);
        chats.setOnClickListener(this);
        settings.setOnClickListener(this);
        rate.setOnClickListener(this);
        out.setOnClickListener(this);
        if (mBmobUser != null) {
            if (name != null) {
                name.setText(mBmobUser.getName());
            }
            if (bio != null) {
                bio.setText(mBmobUser.getBio());
            }
        }
    }

    public void set(BmobUser user) {
        mBmobUser = user;
        if (name != null) {
            name.setText(user.getName());
        }
        if (bio != null) {
            bio.setText(user.getBio());
        }
    }

    @Override
    protected void onShow() {
        super.onShow();
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
    }

    @Override
    public void onClick(View v) {
        if (v == account) {
            getContext().startActivity(new Intent(getContext(), PersonActivity.class)
                    .putExtra("username", (String) SPUtil.get(getContext(), "username", "")));
        } else if (v == calendar) {

        } else if (v == partner) {
            getContext().startActivity(new Intent(getContext(), PartnersActivity.class));
        } else if (v == settings) {

        } else if (v == exercises) {
            getContext().startActivity(new Intent(getContext(), ExActivity.class));
        } else if (v == chats) {

        } else if (v == rate) {

        } else if (v == out) {
            SPUtil.put(getContext(), "username", "");
            getContext().startActivity(new Intent(getContext(), MainActivity.class));
            ((Activity) getContext()).finish();
        }
        dismiss();
    }
}