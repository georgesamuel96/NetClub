package com.egcoders.technologysolution.netclub.data;

import com.egcoders.technologysolution.netclub.model.Post;

import java.util.List;

public interface CategoryPosts {

    interface View{
        void viewPosts(List<Post> list);
        void viewMorePosts(List<Post> list);
        void viewCategories(List<String> list);
    }

    interface Presenter{
        void loadPosts(String category);
        void loadMorePosts(String category);
        void loadCategories();
    }
}
