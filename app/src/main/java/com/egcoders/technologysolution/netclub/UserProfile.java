package com.egcoders.technologysolution.netclub;

import java.util.ArrayList;
import java.util.Map;

public interface UserProfile {

    interface View{
        void showUserData(Map<String, Object> userMap);
        void showUserPosts(ArrayList<Post> postsList);
        void showMorePosts(ArrayList<Post> postsList);
        void showUserSavePosts(ArrayList<Post> postsList);
        void showMoreSavePosts(ArrayList<Post> postsList);
    }

    interface Presenter{
        void showUserData();
        void setUserData(Map<String, Object> userMap);
        void getUserPosts();
        void getMorePosts();
        void getUserSavePosts();
        void getMoreSavePosts();

    }
}
