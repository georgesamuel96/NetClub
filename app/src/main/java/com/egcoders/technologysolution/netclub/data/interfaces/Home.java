package com.egcoders.technologysolution.netclub.data.interfaces;

import com.egcoders.technologysolution.netclub.model.post.PostData;

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
