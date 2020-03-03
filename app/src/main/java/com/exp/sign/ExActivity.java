package com.exp.sign;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ExActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cvAdd;
    private TextView ava;
    private TextView goal;
    private EditText ints;
    private TextView age;
    private Button btGo;
    private FloatingActionButton fab;
    private AVLoadingIndicatorView loading;
    private BmobUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_infoss);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cvAdd = (CardView) findViewById(R.id.cv_add);
        ava = (TextView) findViewById(R.id.ava);
        goal = (TextView) findViewById(R.id.goal);
        ints = (EditText) findViewById(R.id.ints);
        age = (TextView) findViewById(R.id.age);
        btGo = (Button) findViewById(R.id.bt_go);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        loading = (AVLoadingIndicatorView) findViewById(R.id.loading);

        ava.setOnClickListener(this);
        goal.setOnClickListener(this);
        age.setOnClickListener(this);

        BmobQuery<BmobUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username", (String) SPUtil.get(this, "username", ""))
                .findObjects(new FindListener<BmobUser>() {
                    @Override
                    public void done(List<BmobUser> list, BmobException e) {
                        loading.hide();
                        if (e != null) {
                            Toast.makeText(ExActivity.this, "network error", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (list == null || list.isEmpty()) {
                            return;
                        }
                        user = list.get(0);
                        Match m = user.getMatchObejct();
                        if (m == null) {
                            return;
                        }
                        ava.setText(m.getAvailableTime());
                        goal.setText(m.getPurpose());
                        age.setText(m.getAgeGroup());
                        ints.setText(m.getInterests());
                        ints.setSelection(m.getInterests().length());
                    }
                });
    }

    public void next(View view) {

        final String avas = ava.getText().toString();
        final String goals = goal.getText().toString();
        final String ages = age.getText().toString();
        final String intss = ints.getText().toString();

        if (TextUtils.isEmpty(avas) || TextUtils.isEmpty(goals)
                || TextUtils.isEmpty(ages)
                || TextUtils.isEmpty(intss)) {
            Toast.makeText(this, "please enter full information", Toast.LENGTH_SHORT).show();
            return;
        }
        final Match match = new Match();
        match.setAvailableTime(avas);
        match.setAgeGroup(ages);
        match.setInterests(intss);
        match.setPurpose(goals);
        BmobQuery<BmobUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username", (String) SPUtil.get(this, "username", ""))
                .findObjects(new FindListener<BmobUser>() {
                    @Override
                    public void done(List<BmobUser> list, BmobException e) {
                        loading.hide();
                        if (e != null) {
                            Toast.makeText(ExActivity.this, "network error", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (list == null || list.isEmpty()) {
                            return;
                        }
                        user = list.get(0);
                        user.setMatch(JSONObject.toJSONString(match));
                        user.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(ExActivity.this, "save success", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(ExActivity.this, "network error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == ava) {
            final String[] items = {"clear", "choose"};
            AlertDialog.Builder listDialog =
                    new AlertDialog.Builder(this);
            listDialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == 0) {
                        ava.setText("Availability time");
                    } else {
                        final StringBuilder stringBuilder = new StringBuilder();
                        final List<String> ss = new ArrayList<>();
                        final String[] items = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
                        final boolean initChoiceSets[] = {false, false, false, false, false, false, false};
                        AlertDialog.Builder multiChoiceDialog =
                                new AlertDialog.Builder(ExActivity.this);
                        multiChoiceDialog.setMultiChoiceItems(items, initChoiceSets,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which,
                                                        boolean isChecked) {
                                        if (isChecked) {
                                            ss.add(items[which]);
                                        } else {
                                            ss.add(items[which]);
                                        }
                                    }
                                });
                        multiChoiceDialog.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        for (int i = 0; i < ss.size(); i++) {
                                            final View v = LayoutInflater.from(ExActivity.this).inflate(R.layout.sss, null);
                                            final EditText et = v.findViewById(R.id.et);
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ExActivity.this)
                                                    .setTitle(ss.get(i) + " time range")
                                                    .setView(v);
                                            final int finalI = i;
                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String k = et.getText().toString();
                                                    if (TextUtils.isEmpty(k)) {
                                                        return;
                                                    }
                                                    ss.set(finalI, ss.get(finalI) + "   " + k);
                                                }
                                            }).setNegativeButton("CANCEL", null);
                                            AlertDialog d = builder.show();
                                            if(i == 0) {
                                                d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                    @Override
                                                    public void onDismiss(DialogInterface dialog) {
                                                        StringBuilder s = new StringBuilder();
                                                        for (int i = 0; i < ss.size(); i++) {
                                                            s.append(ss.get(i));
                                                            if (i != ss.size() - 1) {
                                                                s.append(", ");
                                                            }
                                                            ava.setText(s.toString());
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                        multiChoiceDialog.show();
                        multiChoiceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {

                            }
                        });
                    }
                }
            });
            listDialog.show();
        } else if (v == goal) {
            final String[] items = {"gain muscle", "lose weight", "keep fit"};
            AlertDialog.Builder listDialog =
                    new AlertDialog.Builder(this);
            listDialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goal.setText(items[which]);
                }
            });
            listDialog.show();
        } else if (v == age) {
            final String[] items = {"under 18", "18 - 22", "23 -30", "30 and up"};
            AlertDialog.Builder listDialog =
                    new AlertDialog.Builder(this);
            listDialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    age.setText(items[which]);
                }
            });
            listDialog.show();
        }
    }
}
