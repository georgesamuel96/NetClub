package com.egcoders.technologysolution.netclub.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.Utils.SharedPreferenceConfig;
import com.egcoders.technologysolution.netclub.model.category.ChooseCategory;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.ArrayList;
import java.util.List;

public class ChooseCategoryAdapter extends BaseAdapter {

    private List<ChooseCategory> categoryList = new ArrayList<>();
    private Context context;
    private static LayoutInflater inflater = null;
    private int countCategoriesChecked = 0;
    private SharedPreferenceConfig preferenceConfig;

    public ChooseCategoryAdapter(Context context, List<ChooseCategory> list)
    {
        this.categoryList = list;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        preferenceConfig = new SharedPreferenceConfig(context);
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

            viewHolder.checkBox = (CustomCheckBox) view.findViewById(R.id.checkbox);
            viewHolder.textCategory = (TextView) view.findViewById(R.id.text_item);
            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.textCategory.setText(categoryList.get(position).getCategoryName());
        viewHolder.checkBox.setChecked(isChecked(position));


        viewHolder.checkBox.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                Boolean statue = isChecked;
                categoryList.get(position).setcategoryChecked(statue);
            }
        });

        viewHolder.checkBox.setChecked(isChecked(position));

        return view;
    }

    public boolean isChecked(int position) {
        return categoryList.get(position).getcategoryChecked();
    }

    public List<ChooseCategory> getCheckedList(){
        return categoryList;
    }

    public Boolean itemsChecked(){
        countCategoriesChecked = 0;
        for(ChooseCategory category : categoryList)
            if(category.getcategoryChecked())
                countCategoriesChecked++;
        if(countCategoriesChecked > 0)
            return true;
        return false;
    }

    public static class ViewHolder{
        TextView textCategory;
        CustomCheckBox checkBox;
    }

    public void clearAdapter()
    {
        categoryList.clear();
        notifyDataSetChanged();
    }
}
