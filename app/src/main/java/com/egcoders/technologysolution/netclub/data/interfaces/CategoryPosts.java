package com.egcoders.technologysolution.netclub.data.interfaces;

import com.egcoders.technologysolution.netclub.model.post.Post;

import java.util.List;

public interface CategoryPosts {

    interface View{
        void viewPosts(List<Post> list);
        void viewMorePosts(List<Post> list);
        void viewCategories(List<String> list);
    }

    interface Presenter{
        void loadPosts(int category_id);
        void loadMorePosts(int categoryId);
        void loadCategories();
    }
}
