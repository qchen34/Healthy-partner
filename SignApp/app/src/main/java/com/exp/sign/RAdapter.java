package com.exp.sign;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class RAdapter extends BaseAdapter {

    private List<BmobUser> objects = new ArrayList<>();

    private Context context;
    private LayoutInflater layoutInflater;

    public RAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public RAdapter(List<BmobUser> objects, Context context) {
        this.objects = objects;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setObjects(List<BmobUser> objects) {
        this.objects = objects;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public BmobUser getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.u_r, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews(getItem(position), (ViewHolder) convertView.getTag());
        convertView.findViewById(R.id.cc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BmobUser u = objects.get(position);
                BmobQuery<BmobUser> uq = new BmobQuery<>();
                uq.addWhereEqualTo("username", (String) SPUtil.get(context, "username", ""));
                uq.findObjects(new FindListener<BmobUser>() {
                    @Override
                    public void done(List<BmobUser> list, BmobException e) {
                        if (e != null) {
                            Toast.makeText(context, "network error", Toast.LENGTH_SHORT).show();
                        } else {
                            final BmobUser self = list.get(0);
                            List<String> xs = self.getPartnersList();
                            xs.add(u.getUsername());
                            self.setPartners(JSONArray.toJSONString(xs));
                            self.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    MainActivity.sBmobUser = self;
                                }
                            });
                            List<String> xs2 = u.getPartnersList();
                            xs2.add(self.getUsername());
                            u.setPartners(JSONArray.toJSONString(xs2));
                            u.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e != null) {
                                        Toast.makeText(context, "network error", Toast.LENGTH_SHORT).show();
                                    } else {
                                        objects.remove(u);
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                            BmobQuery<Request> ss = new BmobQuery<>();
                            ss.addWhereEqualTo("from", u.getUsername());
                            ss.addWhereEqualTo("to", self.getUsername());
                            ss.findObjects(new FindListener<Request>() {
                                @Override
                                public void done(List<Request> list, BmobException e) {
                                    if (list != null && !list.isEmpty()) {
                                        list.get(0).setStatus(1);
                                        list.get(0).update(new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {

                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        return convertView;
    }

    private void initializeViews(BmobUser object, ViewHolder holder) {
        holder.bio.setText(object.getBio());
        holder.name.setText(object.getName());
    }

    protected class ViewHolder {
        private TextView name;
        private TextView bio;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.name);
            bio = (TextView) view.findViewById(R.id.bio);
        }
    }
}
