package com.egcoders.technologysolution.netclub.data.interfaces;


import android.util.Pair;

import com.egcoders.technologysolution.netclub.model.post.Post;

import java.util.List;

public interface AddPost {

    interface View{

        void showPost(Post post);
        void showCategories(List<Pair<String, Integer>> list);
    }

    interface Presenter {

        void setPost(Post post);
        void getPost(int postId);
        void getCategories();
        void updatePost(Post post);
    }
}
