package com.egcoders.technologysolution.netclub.data.presenter;

import android.app.Activity;
import android.content.Intent;

import com.egcoders.technologysolution.netclub.ui.activities.MainActivity;
import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.Utils.UserSharedPreference;
import com.egcoders.technologysolution.netclub.data.interfaces.SelectCategory;
import com.egcoders.technologysolution.netclub.model.category.Category;
import com.egcoders.technologysolution.netclub.model.category.CategorySelected;
import com.egcoders.technologysolution.netclub.model.category.CategorySelectedData;
import com.egcoders.technologysolution.netclub.model.category.ChooseCategory;
import com.egcoders.technologysolution.netclub.model.category.DeleteCategoriesResponse;
import com.egcoders.technologysolution.netclub.model.category.SelectCategoryResponse;
import com.egcoders.technologysolution.netclub.model.profile.UserResponse;
import com.egcoders.technologysolution.netclub.remote.ApiManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectCategoryPresenter implements SelectCategory.Presenter {

    private SelectCategory.View view;
    private Activity activity;
    private UserSharedPreference preference;
    private Utils utils;
    private Thread[] threads = new Thread[2];


    public SelectCategoryPresenter(Activity activity, SelectCategory.View view){
        this.activity = activity;
        this.view = view;
        preference = new UserSharedPreference(activity.getApplicationContext());
        utils = new Utils(activity);
    }

    @Override
    public void getCategories() {

        utils.showProgressDialog("Categories", "Show Categories");

        final List<ChooseCategory> list = new ArrayList<>();
        final List<CategorySelectedData> selectedCategoryList = new ArrayList<>();

        final String token = preference.getUser().getData().getToken();
        final int user_id = preference.getUser().getData().getId();

        ApiManager.getInstance().showCategories(token, new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {

                final Category categoryResponse = response.body();

                if(categoryResponse.getSuccess()){
                    for(CategorySelectedData category : categoryResponse.getData()){

                        ChooseCategory chooseCategory = new ChooseCategory();
                        chooseCategory.setcategoryChecked(false);
                        chooseCategory.setCategoryId(category.getId());
                        chooseCategory.setCategoryName(category.getName());

                        list.add(chooseCategory);
                    }

                    ApiManager.getInstance().categorySelected(token, user_id, new Callback<CategorySelected>() {
                        @Override
                        public void onResponse(Call<CategorySelected> call, Response<CategorySelected> response) {

                            CategorySelected categorySelected = response.body();

                            if(categorySelected.getStatus_code() == 201) {


                                for (int i = 0; i < categorySelected.getData().size(); i++) {

                                    CategorySelectedData category = categorySelected.getData().get(i).get(0);
                                    selectedCategoryList.add(category);
                                }

                                for(CategorySelectedData category : selectedCategoryList){

                                    int index = searchIndex(category.getId() - 1, list);
                                    list.get(index).setcategoryChecked(true);
                                }

                                utils.hideProgressDialog();

                                view.showCategories(list);

                            }
                            else if(categorySelected.getStatus_code() == 500){
                                utils.hideProgressDialog();

                                view.showCategories(list);
                            }

                        }

                        @Override
                        public void onFailure(Call<CategorySelected> call, Throwable t) {

                            utils.showMessage("Get selected categories", t.getMessage());
                        }
                    });

                }
                else {

                    utils.hideProgressDialog();
                }

            }
            @Override
            public void onFailure(Call<Category> call, Throwable t) {

                utils.hideProgressDialog();
                utils.showMessage("Get categories", t.getMessage());
            }
        });
    }

    @Override
    public void setSelectedCategories(final List<ChooseCategory> list) {

        utils.showProgressDialog("Selecting Categories", "Loading");

        final String token = preference.getUser().getData().getToken();
        final int user_id = preference.getUser().getData().getId();
        ApiManager.getInstance().deleteCategorySelected(token, user_id, new Callback<DeleteCategoriesResponse>() {
            @Override
            public void onResponse(Call<DeleteCategoriesResponse> call, Response<DeleteCategoriesResponse> response) {

                DeleteCategoriesResponse done = response.body();

                if(done.getStatus_code() == 201 || done.getStatus_code() == 500){

                    for(ChooseCategory category : list) {
                        if(category.getcategoryChecked()){
                            ApiManager.getInstance().selectCategory(token, category.getCategoryId(), user_id, new Callback<SelectCategoryResponse>() {
                                @Override
                                public void onResponse(Call<SelectCategoryResponse> call, Response<SelectCategoryResponse> response) {

                                    SelectCategoryResponse selectCategory = response.body();
                                    if(selectCategory.getSuccess()){

                                    }
                                    else {

                                    }

                                }

                                @Override
                                public void onFailure(Call<SelectCategoryResponse> call, Throwable t) {

                                }
                            });
                        }
                    }

                    UserResponse userResponse = preference.getUser();
                    userResponse.getData().setSelectedCategory(true);
                    preference.setUser(userResponse);

                    utils.hideProgressDialog();
                    sendToMain();
                }
            }

            @Override
            public void onFailure(Call<DeleteCategoriesResponse> call, Throwable t) {
                utils.hideProgressDialog();
                utils.showMessage("Select Category", t.getMessage());
            }
        });
    }

    private int searchIndex(int id, List<ChooseCategory> list) {
        int index = -1, lo = 0, hi = list.size() - 1, mid;

        while(lo <= hi){
            mid = (lo + hi) / 2;
            if(mid == id){
                index = mid;
                break;
            }
            if(id < mid){
                hi = mid - 1;
            }
            else {
                lo = mid + 1;
            }
        }

        return index;
    }

    private void sendToMain(){
        Intent i = new Intent(activity, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(i);
    }
}
