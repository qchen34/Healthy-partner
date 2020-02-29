package com.exp.sign;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
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

import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class PartnersActivity extends AppCompatActivity {

    private ListView listView;
    private AVLoadingIndicatorView loadingIndicatorView;
    private List<BmobUser> list;
    private UAdapter mBaseAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.xx2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sd) {
            final String[] items = { "Search for new partner","Partner request received"};
            AlertDialog.Builder listDialog =
                    new AlertDialog.Builder(PartnersActivity.this);
            listDialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == 0) {
                        startActivity(new Intent(PartnersActivity.this, UsersActivity.class));
                    } else {
                        startActivity(new Intent(PartnersActivity.this, ReceivedActivity.class));
                    }
                }
            });
            listDialog.show();
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        listView = (ListView) findViewById(R.id.list);
        loadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.loading);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(PartnersActivity.this, PersonActivity.class)
                        .putExtra("username", list.get(i).getUsername()));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getData(null);
    }

    private void getData(final String key) {
        BmobQuery<BmobUser> query = new BmobQuery<>();
        query.findObjects(new FindListener<BmobUser>() {
            @Override
            public void done(List<BmobUser> list, BmobException e) {
                loadingIndicatorView.hide();
                if(e != null) {
                    Toast.makeText(PartnersActivity.this, "network error", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(list == null || list.isEmpty()) {
                    return;
                }
                BmobUser self = null;
                for (BmobUser bmobUser : list) {
                    if (bmobUser.getUsername().equals((String) SPUtil.get(PartnersActivity.this, "username", ""))) {
                        self = bmobUser;
                        break;
                    }
                }
                Iterator<BmobUser> iterator = list.iterator();
                while (iterator.hasNext()) {
                    final BmobUser next = iterator.next();
                    if (!self.getPartnersList().contains(next.getUsername()) || self.getUsername().equals(next.getUsername())) {
                        iterator.remove();
                    } else {
                        if (!TextUtils.isEmpty(key)) {
                            if (!next.getName().contains(key) && !next.getBio().contains(key)) {
                                iterator.remove();
                            }
                        }
                    }
                }
                PartnersActivity.this.list = list;
                mBaseAdapter = new UAdapter(PartnersActivity.this.list, PartnersActivity.this);
                listView.setAdapter(mBaseAdapter);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData(null);
    }
}
