package com.egcoders.technologysolutions.netclub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

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
        if(convertView == null){
            view = inflater.inflate(R.layout.item_choose_category, null);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
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
            });


        }
        return view;
    }

    public ArrayList<ChooseCategory> getCheckedList(){
        return categoryList;
    }

    public Boolean itemsChecked(){
        if(countCategoriesChecked > 0)
            return true;
        return false;
    }
}
