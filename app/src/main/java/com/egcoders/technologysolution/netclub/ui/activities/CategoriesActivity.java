package com.egcoders.technologysolution.netclub.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.data.interfaces.SelectCategory;
import com.egcoders.technologysolution.netclub.data.presenter.SelectCategoryPresenter;
import com.egcoders.technologysolution.netclub.model.category.ChooseCategory;
import com.egcoders.technologysolution.netclub.data.adapter.ChooseCategoryAdapter;
import com.egcoders.technologysolution.netclub.R;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity implements SelectCategory.View {

    private android.support.v7.widget.Toolbar toolbar;
    private ListView listView;
    private ChooseCategoryAdapter adapter;
    private SelectCategory.Presenter presenter;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Choose Categories");

        listView = (ListView) findViewById(R.id.list_view);

        presenter = new SelectCategoryPresenter(CategoriesActivity.this, this);
        utils = new Utils(CategoriesActivity.this);

        presenter.getCategories();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.submit){

            Boolean itemChecked = (adapter.itemsChecked());
            if(itemChecked) {
                List<ChooseCategory> list = adapter.getCheckedList();
                presenter.setSelectedCategories(list);
            }
            else {
                utils.showMessage("Categories", "You must choose at least on category");
            }

        }

        return true;
    }

    @Override
    public void showCategories(List<ChooseCategory> categories) {

        adapter = new ChooseCategoryAdapter(getApplicationContext(), categories);
        listView.setAdapter(adapter);
    }
}
