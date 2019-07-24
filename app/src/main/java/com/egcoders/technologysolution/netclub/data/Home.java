package com.egcoders.technologysolution.netclub.data;

import com.egcoders.technologysolution.netclub.model.Post;
import com.egcoders.technologysolution.netclub.model.PostData;

import java.util.ArrayList;
import java.util.List;

public interface Home {

    interface View{
        void showPosts(PostData data);
        void showMorePosts(PostData data);
    }

    interface Presenter{
        void loadPosts();
        void loadMorePosts();
    }
}
