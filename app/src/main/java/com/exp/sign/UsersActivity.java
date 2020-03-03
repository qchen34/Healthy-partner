package com.exp.sign;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class UsersActivity extends AppCompatActivity {

    private ListView listView;
    private AVLoadingIndicatorView loadingIndicatorView;
    private List<BmobUser> list;
    private XAdapter mBaseAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        listView =findViewById(R.id.list);
        loadingIndicatorView = findViewById(R.id.loading);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(UsersActivity.this, PersonActivity.class)
                        .putExtra("username", list.get(i).getUsername()));
            }
        });
        getData(null);
    }

    private void getData(final String key) {
        BmobQuery<BmobUser> query = new BmobQuery<>();
        query.findObjects(new FindListener<BmobUser>() {
            @Override
            public void done(List<BmobUser> list, BmobException e) {
                loadingIndicatorView.hide();
                if (e != null) {
                    Toast.makeText(UsersActivity.this, "network error", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (list == null || list.isEmpty()) {
                    return;
                }
                BmobUser self = null;
                for (BmobUser bmobUser : list) {
                    if (bmobUser.getUsername().equals(SPUtil.get(UsersActivity.this, "username", ""))) {
                        self = bmobUser;
                        break;
                    }
                }
                if (self == null) return;
                Iterator<BmobUser> iterator = list.iterator();
                while (iterator.hasNext()) {
                    final BmobUser next = iterator.next();
                    if (!TextUtils.isEmpty(key)) {
                        if (!next.getName().contains(key) && !next.getBio().contains(key)
                            && !next.getMatch().contains(key) && !next.getBio().contains(key)) {
                            iterator.remove();
                        }
                    } else if (self.getPartners().contains(next.getUsername())) {
                        iterator.remove();
                    } else if ((SPUtil.get(UsersActivity.this, "username", "")).equals(next.getUsername())) {
                        iterator.remove();
                    }
                }
                final BmobUser finalSelf = self;
                Collections.sort(list, new Comparator<BmobUser>() {
                    @Override
                    public int compare(BmobUser bmobUser, BmobUser user2) {
                        Match selfMatch = finalSelf.getMatchObejct();
                        Match match1 = bmobUser.getMatchObejct();
                        Match match2 = user2.getMatchObejct();
                        if (Objects.equals(match1.getAvailableTime(), selfMatch.getAvailableTime()))
                            return -1;
                        else if (Objects.equals(match2.getAvailableTime(), selfMatch.getAvailableTime()))
                            return 1;
                        else if (Objects.equals(match1.getPurpose(), selfMatch.getPurpose()))
                            return -1;
                        else if (Objects.equals(match2.getPurpose(), selfMatch.getPurpose()))
                            return 1;
                        else if (Objects.equals(match1.getInterests(), selfMatch.getInterests()))
                            return -1;
                        else if (Objects.equals(match2.getInterests(), selfMatch.getInterests()))
                            return 1;
                        else if (Objects.equals(match1.getAgeGroup(), selfMatch.getAgeGroup()))
                            return -1;
                        else if (Objects.equals(match2.getAgeGroup(), selfMatch.getAgeGroup()))
                            return 1;
                        return 0;
                    }
                });
                loadingIndicatorView.hide();
                UsersActivity.this.list = list;
                mBaseAdapter = new XAdapter(UsersActivity.this.list, UsersActivity.this);
                listView.setAdapter(mBaseAdapter);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.xx, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.xs) {
            final View v = LayoutInflater.from(this).inflate(R.layout.sss, null);
            final EditText et = v.findViewById(R.id.et);
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Search")
                    .setView(v);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String k = et.getText().toString();
                    if (TextUtils.isEmpty(k)) {
                        return;
                    }
                    getData(k);
                }
            }).setNegativeButton("CANCEL", null);
            builder.show();
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
