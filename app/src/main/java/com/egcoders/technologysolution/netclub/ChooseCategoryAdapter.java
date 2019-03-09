package com.egcoders.technologysolution.netclub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ChooseCategoryAdapter extends BaseAdapter {

    private ArrayList<ChooseCategory> categoryList = new ArrayList<>();
    private Context context;
    private static LayoutInflater inflater = null;
    private int countCategoriesChecked = 0;

    public ChooseCategoryAdapter(Context context, ArrayList<ChooseCategory> list)
    {
        this.categoryList = list;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return categoryList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder viewHolder = new ViewHolder();

        if(convertView == null){
            view = inflater.inflate(R.layout.item_choose_category, null);
            /*CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            ChooseCategory category = new ChooseCategory();
            category = categoryList.get(position);
            checkBox.setText(category.getCategoryName());
            checkBox.setChecked(category.getcategoryChecked());

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    categoryList.get(position).setcategoryChecked(isChecked);
                    if(isChecked)
                        countCategoriesChecked++;
                    else
                        countCategoriesChecked--;
                }
            });*/

            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            viewHolder.textCategory = (TextView) view.findViewById(R.id.text_item);
            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) view.getTag();
        }
        //
        viewHolder.textCategory.setText(categoryList.get(position).getCategoryName());
        viewHolder.checkBox.setChecked(categoryList.get(position).getcategoryChecked());
        viewHolder.checkBox.setTag(position);

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean statue = !categoryList.get(position).getcategoryChecked();
                categoryList.get(position).setcategoryChecked(statue);
                if(statue) {
                    countCategoriesChecked++;
                }
                else {
                    countCategoriesChecked--;
                }
            }
        });

        viewHolder.textCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean statue = !categoryList.get(position).getcategoryChecked();
                if(statue) {
                    countCategoriesChecked++;
                }
                else {
                    countCategoriesChecked--;
                }
            }
        });

        viewHolder.checkBox.setChecked(isChecked(position));

        return view;
    }

    public boolean isChecked(int position) {
        return categoryList.get(position).getcategoryChecked();
    }

    public ArrayList<ChooseCategory> getCheckedList(){
        return categoryList;
    }

    public Boolean itemsChecked(){
        if(countCategoriesChecked > 0)
            return true;
        return false;
    }

    public static class ViewHolder{
        CheckBox checkBox;
        TextView textCategory;
    }

    public void clearAdapter()
    {
        categoryList.clear();
        notifyDataSetChanged();
    }
}
