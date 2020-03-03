package com.exp.sign;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class UAdapter extends BaseAdapter {

    private List<BmobUser> objects = new ArrayList<>();

    private Context context;
    private LayoutInflater layoutInflater;

    public UAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public UAdapter(List<BmobUser> objects, Context context) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.u, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews(getItem(position), (ViewHolder) convertView.getTag());
        convertView.findViewById(R.id.cc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // to chat
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
