package com.exp.sign;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class XAdapter extends BaseAdapter {

    private List<BmobUser> objects = new ArrayList<>();

    private Context context;
    private LayoutInflater layoutInflater;

    public XAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public XAdapter(List<BmobUser> objects, Context context) {
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
            convertView = layoutInflater.inflate(R.layout.uxx, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews(getItem(position), (ViewHolder) convertView.getTag());
        if (convertView.findViewById(R.id.cc) != null) {

            ((ViewGroup) convertView).getChildAt(1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Request request = new Request();
                    request.setFrom((String) SPUtil.get(context, "username", ""));
                    request.setTo(objects.get(position).getUsername());
                    request.setStatus(0);
                    request.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            Toast.makeText(context, "request success", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }
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
