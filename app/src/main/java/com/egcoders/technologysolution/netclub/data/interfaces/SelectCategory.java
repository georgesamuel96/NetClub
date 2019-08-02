package com.egcoders.technologysolution.netclub.data.interfaces;

import com.egcoders.technologysolution.netclub.model.category.ChooseCategory;

import java.util.List;

public interface SelectCategory {

    interface View {

        void showCategories(List<ChooseCategory> categories);
    }

    interface Presenter{

        void getCategories();
        void setSelectedCategories(List<ChooseCategory> list);
    }
}
