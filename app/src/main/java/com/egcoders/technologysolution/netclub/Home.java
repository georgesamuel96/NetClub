package com.egcoders.technologysolution.netclub;

import java.util.ArrayList;

public interface Home {

    interface View{
        void showPosts(ArrayList<Post> list);
        void showMorePosts(ArrayList<Post> list);
    }

    interface Presenter{
        void loadPosts();
        void loadMorePosts();
    }
}
