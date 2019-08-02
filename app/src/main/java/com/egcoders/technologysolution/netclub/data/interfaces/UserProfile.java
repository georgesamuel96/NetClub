package com.egcoders.technologysolution.netclub.data.interfaces;

import com.egcoders.technologysolution.netclub.model.post.PostData;
import com.egcoders.technologysolution.netclub.model.profile.UserData;

public interface UserProfile {

    interface View{
        void showUserData(UserData user);
        void showUserPosts(PostData post);
        void showMorePosts(PostData post);
        void showUserSavePosts(PostData post);
        void showMoreSavePosts(PostData post);
    }

    interface Presenter{
        void showUserData();
        void setUserDataWithPhoto(UserData user, String imagePath);
        void setUserDataNoPhoto(UserData user);
        void getUserPosts();
        void getMorePosts();
        void getUserSavePosts();
        void getMoreSavePosts();

    }
}
