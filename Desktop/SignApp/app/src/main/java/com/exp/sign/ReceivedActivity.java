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

import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ReceivedActivity extends AppCompatActivity {

    private ListView listView;
    private AVLoadingIndicatorView loadingIndicatorView;
    private List<BmobUser> list;
    private RAdapter mBaseAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.list);
        loadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.loading);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(ReceivedActivity.this, PersonActivity.class)
                        .putExtra("username", list.get(i).getUsername()));
            }
        });
        getData(null);
    }

    private void getData(final String key) {
        BmobQuery<BmobUser> query = new BmobQuery<>();
        query.findObjects(new FindListener<BmobUser>() {
            @Override
            public void done(final List<BmobUser> list, BmobException e) {
                loadingIndicatorView.hide();
                if (e != null) {
                    Toast.makeText(ReceivedActivity.this, "network error", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (list == null || list.isEmpty()) {
                    return;
                }
                BmobUser self = null;
                for (BmobUser bmobUser : list) {
                    if (bmobUser.getUsername().equals(SPUtil.get(ReceivedActivity.this, "username", ""))) {
                        self = bmobUser;
                        break;
                    }
                }
                BmobQuery<Request> cc = new BmobQuery<>();
                if (e == null) {
                    cc.findObjects(new FindListener<Request>() {
                                       @Override
                                       public void done(List<Request> list2, BmobException e) {
                                           if (e != null) {
                                               Toast.makeText(ReceivedActivity.this, "network error", Toast.LENGTH_SHORT).show();
                                               return;
                                           }
                                           if (list2 == null || list2.isEmpty()) {
                                               return;
                                           }
                                           if (e != null) {
                                               Toast.makeText(ReceivedActivity.this, "network error", Toast.LENGTH_SHORT).show();
                                           } else {
                                               if (list2 != null && !list2.isEmpty()) {
                                                   Iterator<Request> iterator = list2.iterator();
                                                   while (iterator.hasNext()) {
                                                       Request u = iterator.next();
                                                       if (u.getStatus() != 0 || !u.getTo().equals(SPUtil.get(ReceivedActivity.this, "username", ""))) {
                                                           iterator.remove();
                                                       }
                                                   }
                                                   Iterator<BmobUser> su = list.iterator();
                                                   while (su.hasNext()) {
                                                       BmobUser u = su.next();
                                                       Request r = new Request();
                                                       r.setFrom(u.getUsername());
                                                       if (!list2.contains(r)) {
                                                           su.remove();
                                                       } else if (SPUtil.get(ReceivedActivity.this, "username", "").equals(u.getUsername())) {
                                                           iterator.remove();
                                                       }
                                                   }
                                               }
                                               ReceivedActivity.this.list = list;
                                               mBaseAdapter = new RAdapter(ReceivedActivity.this.list, ReceivedActivity.this);
                                               listView.setAdapter(mBaseAdapter);
                                           }
                                       }
                                   }
                    );
                } else {
                    Toast.makeText(ReceivedActivity.this, "network error", Toast.LENGTH_SHORT).show();
                }
                loadingIndicatorView.hide();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
