package com.egcoders.technologysolution.netclub.ui.adapter;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.egcoders.technologysolution.netclub.R;

import java.util.ArrayList;

public class PaymentMethodAdapter extends BaseAdapter {

    private ArrayList<Pair<Integer, String>> list = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    public PaymentMethodAdapter(Context context, ArrayList<Pair<Integer, String>> list){
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            view = inflater.inflate(R.layout.item_payment_method, null);
            TextView name = (TextView) view.findViewById(R.id.name);
            ImageView image = (ImageView) view.findViewById(R.id.postImage);

            name.setText(list.get(position).second);
            image.setImageDrawable(context.getResources().getDrawable(list.get(position).first));
        }

        return view;
    }
}
