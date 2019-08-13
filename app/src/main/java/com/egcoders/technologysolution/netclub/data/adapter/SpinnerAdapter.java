package com.egcoders.technologysolution.netclub.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.egcoders.technologysolution.netclub.R;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter extends BaseAdapter {

    private List<String> list = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    public SpinnerAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null){
            view = inflater.inflate(R.layout.item_spinner, null);
        }
        TextView categoryName = (TextView) view.findViewById(R.id.category);
        categoryName.setText(list.get(position));

        return view;
    }
}
